package cjminecraft.core.command;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

import cjminecraft.core.CJCore;
import cjminecraft.core.config.CJCoreConfig;
import cjminecraft.core.energy.EnergyUnits;
import cjminecraft.core.energy.EnergyUnits.EnergyUnit;
import cjminecraft.core.energy.EnergyUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

/**
 * Allows the player to easily edit the energy inside of a {@link TileEntity}
 * 
 * @author CJMinecraft
 *
 */
public class CommandEditTileEntity extends CommandBase {

	private static List<String> energyUnits = new ArrayList<String>();
	private static List<String> faces = new ArrayList<String>();

	/**
	 * Initialize the different energy units and faces
	 */
	public CommandEditTileEntity() {
		EnergyUnits.getEnergyUnits().forEach((unit) -> {
			energyUnits.add(unit.getUnlocalizedName());
		});
		Lists.newArrayList(EnumFacing.VALUES).forEach((face) -> {
			faces.add(face.getName2());
		});
	}

	/**
	 * The name of the command
	 */
	@Override
	public String getName() {
		return "tileentity";
	}

	/**
	 * The usage of the command
	 */
	@Override
	public String getUsage(ICommandSender sender) {
		return "command.tileentity.usage";
	}

	/**
	 * The shortened version of the command
	 */
	@Override
	public List<String> getAliases() {
		return Arrays.asList("tileentity", "te");
	}

	/**
	 * Allows the tab completions to be done
	 */
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
			BlockPos targetPos) {
		return args.length >= 0 && args.length < 4 ? getTabCompletionCoordinate(args, 0, targetPos)
				: args.length > 3 && args.length < 5 ? getListOfStringsMatchingLastWord(args, new String[] { "energy" })
						: args.length > 4 && args.length < 6
								? getListOfStringsMatchingLastWord(args, new String[] { "set", "get", "give", "take" })
								: args.length > 7 && args.length < 9 && !args[6].isEmpty()
										? getListOfStringsMatchingLastWord(args, faces)
										: args.length > 6 && args.length < 8
												? getListOfStringsMatchingLastWord(args, energyUnits)
												: Collections.<String>emptyList();
	}

	/**
	 * Returns which type of edit it is. TODO Implement inventory and fluid
	 * editing
	 * 
	 * @param arg
	 *            The argument to check
	 * @return Which type of edit it is
	 */
	private static int getEditType(String arg) {
		return arg.equalsIgnoreCase("energy") ? 1
				: arg.equalsIgnoreCase("inventory") ? 2 : arg.equalsIgnoreCase("fluid") ? 3 : 0;
	}

	/**
	 * Actually run the command
	 */
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length < 3)
			throw new CommandException("command.tileentity.usage");
		BlockPos pos = parseBlockPos(sender, args, 0, true);
		if (args.length < 5)
			throw new CommandException("command.tileentity.usage");
		TileEntity te = server.getEntityWorld().getTileEntity(pos);
		EnumFacing side = null;
		int editType = getEditType(args[3]);
		if (args.length > 6 && args[5].equalsIgnoreCase("get"))
			side = EnumFacing.byName(args[7]);
		if (args.length > 7 && !args[5].equalsIgnoreCase("get"))
			side = EnumFacing.byName(args[7]);
		if (editType == 0)
			throw new CommandException("command.tileentity.usage");
		if (editType == 1) {
			if (!EnergyUtils.hasSupport(te, side))
				throw new CommandException("command.tileentity.nosupport", pos.getX(), pos.getY(), pos.getZ());
			if (args[4].equalsIgnoreCase("set")) {
				if (args.length < 7)
					throw new CommandException("command.tileentity.usage");
				long energy = Long.valueOf(args[5]);
				EnergyUnit unit = EnergyUnits.MINECRAFT_JOULES;
				if (!args[6].isEmpty()) {
					for (EnergyUnit u : EnergyUnits.getEnergyUnits())
						if (u.getUnlocalizedName().equalsIgnoreCase(args[6])
								|| u.getSuffix().equalsIgnoreCase(args[6])) {
							unit = u;
							break;
						}
				}
				if (EnergyUtils.setEnergy(te, energy, unit, side) == 0)
					throw new CommandException("command.tileentity.noset");
			}
			if (args[4].equalsIgnoreCase("get")) {
				long energy = EnergyUtils.getEnergyStored(te, side, CJCoreConfig.DEFAULT_ENERGY_UNIT);
				long capacity = EnergyUtils.getCapacity(te, side, CJCoreConfig.DEFAULT_ENERGY_UNIT);
				sender.sendMessage(new TextComponentString(NumberFormat.getNumberInstance().format(energy) + " "
						+ CJCoreConfig.DEFAULT_ENERGY_UNIT.getSuffix() + " / "
						+ NumberFormat.getNumberInstance().format(capacity) + " "
						+ CJCoreConfig.DEFAULT_ENERGY_UNIT.getSuffix()));
			}
			if (args[4].equalsIgnoreCase("give")) {
				if (args.length < 7)
					throw new CommandException("command.tileentity.usage");
				long energy = Long.valueOf(args[5]);
				EnergyUnit unit = EnergyUnits.MINECRAFT_JOULES;
				if (!args[6].isEmpty()) {
					for (EnergyUnit u : EnergyUnits.getEnergyUnits())
						if (u.getUnlocalizedName().equalsIgnoreCase(args[6])
								|| u.getSuffix().equalsIgnoreCase(args[6])) {
							unit = u;
							break;
						}
				}
				if (EnergyUtils.giveEnergy(te, energy, unit, false, side) == 0)
					throw new CommandException("command.tileentity.nogive");
			}
			if (args[4].equalsIgnoreCase("take")) {
				if (args.length < 7)
					throw new CommandException("command.tileentity.usage");
				long energy = Long.valueOf(args[5]);
				EnergyUnit unit = EnergyUnits.MINECRAFT_JOULES;
				if (!args[6].isEmpty()) {
					for (EnergyUnit u : EnergyUnits.getEnergyUnits())
						if (u.getUnlocalizedName().equalsIgnoreCase(args[6])
								|| u.getSuffix().equalsIgnoreCase(args[6])) {
							unit = u;
							break;
						}
				}
				if (EnergyUtils.takeEnergy(te, energy, unit, false, side) == 0)
					throw new CommandException("command.tileentity.notake");
			}
		}
	}

}
