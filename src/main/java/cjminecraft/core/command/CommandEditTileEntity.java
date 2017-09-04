package cjminecraft.core.command;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import cjminecraft.core.config.CJCoreConfig;
import cjminecraft.core.energy.EnergyUnits;
import cjminecraft.core.energy.EnergyUnits.EnergyUnit;
import cjminecraft.core.energy.EnergyUtils;
import cjminecraft.core.inventory.InventoryUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.capabilities.Capability;

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
				: args.length > 3 && args.length < 5
						? getListOfStringsMatchingLastWord(args, new String[] { "energy", "inventory" })
						: args[3].equalsIgnoreCase("energy") ? getTabCompletionsEnergy(server, sender, args, targetPos)
								: args[3].equalsIgnoreCase("inventory")
										? getTabCompletionsInventory(server, sender, args, targetPos)
										: Collections.<String>emptyList();
	}

	/**
	 * Get a list of options for when the user presses the TAB key for the
	 * energy option
	 * 
	 * @param server
	 *            The server instance
	 * @param sender
	 *            The ICommandSender to get tab completions for
	 * @param args
	 *            Any arguments that were present when TAB was pressed
	 * @param targetPos
	 *            The block that the player's mouse is over, <tt>null</tt> if
	 *            the mouse is not over a block
	 * @return The tab completions for energy
	 */
	public List<String> getTabCompletionsEnergy(MinecraftServer server, ICommandSender sender, String[] args,
			BlockPos targetPos) {
		return args.length > 4 && args.length < 6
				? getListOfStringsMatchingLastWord(args, new String[] { "set", "get", "give", "take" })
				: args[4].equalsIgnoreCase("get") && args.length > 5 && args.length < 7
						? getListOfStringsMatchingLastWord(args, faces)
						: args.length > 7 && args.length < 9 && !args[6].isEmpty()
								? getListOfStringsMatchingLastWord(args, faces)
								: args.length > 6 && args.length < 8
										? getListOfStringsMatchingLastWord(args, energyUnits)
										: Collections.<String>emptyList();
	}

	/**
	 * Get a list of options for when the user presses the TAB key for the
	 * inventory option
	 * 
	 * @param server
	 *            The server instance
	 * @param sender
	 *            The ICommandSender to get tab completions for
	 * @param args
	 *            Any arguments that were present when TAB was pressed
	 * @param targetPos
	 *            The block that the player's mouse is over, <tt>null</tt> if
	 *            the mouse is not over a block
	 * @return The tab completions for inventory
	 */
	public List<String> getTabCompletionsInventory(MinecraftServer server, ICommandSender sender, String[] args,
			BlockPos targetPos) {
		return args.length > 4 && args.length < 6
				? getListOfStringsMatchingLastWord(args, new String[] { "get", "insert", "extract" })
				: args[4].equalsIgnoreCase("get") && args.length > 5 && args.length < 7
						? getListOfStringsMatchingLastWord(args, faces)
						: args.length > 9 && args.length < 11 && !args[6].isEmpty()
								? getListOfStringsMatchingLastWord(args, faces)
								: args.length > 5 && args.length < 7
										? getListOfStringsMatchingLastWord(args, Item.REGISTRY.getKeys())
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
		if (args.length > 5 && args[4].equalsIgnoreCase("get"))
			side = EnumFacing.byName(args[5]);
		if (args.length > 7 && !args[4].equalsIgnoreCase("get"))
			side = EnumFacing.byName(args[7]);
		if (editType == 0)
			throw new CommandException("command.tileentity.usage");
		// Energy commands
		if (editType == 1)
			handleEnergyCommands(te, side, pos, args, sender);
		// Inventory Commands
		else if (editType == 2)
			handleInventoryCommands(te, side, args, sender);
	}

	/**
	 * Handle all of the energy commands Callback for when the command is
	 * executed
	 * 
	 * @param te
	 *            The {@link TileEntity} at the position provided
	 * @param side
	 *            The side of the {@link TileEntity} for use with
	 *            {@link Capability}
	 * @param pos
	 *            The position of the block
	 * @param sender
	 *            The sender who executed the command
	 * @param args
	 *            The arguments that were passed
	 * @throws CommandException
	 *             Allow commands to go wrong
	 */
	public void handleEnergyCommands(TileEntity te, EnumFacing side, BlockPos pos, String[] args, ICommandSender sender)
			throws CommandException {
		if (!EnergyUtils.hasSupport(te, side))
			throw new CommandException("command.tileentity.nosupport", pos.getX(), pos.getY(), pos.getZ());
		if (args[4].equalsIgnoreCase("set")) {
			if (args.length < 7)
				throw new CommandException("command.tileentity.usage");
			long energy = Long.valueOf(args[5]);
			EnergyUnit unit = EnergyUnits.MINECRAFT_JOULES;
			if (!args[6].isEmpty())
				unit = EnergyUnits.byUnlocalizedName(args[6]);
			if (EnergyUtils.setEnergy(te, energy, unit, side) == 0)
				throw new CommandException("command.tileentity.noset");
		}
		if (args[4].equalsIgnoreCase("get")) {
			long energy = EnergyUtils.getEnergyStored(te, side, CJCoreConfig.DEFAULT_ENERGY_UNIT);
			long capacity = EnergyUtils.getCapacity(te, side, CJCoreConfig.DEFAULT_ENERGY_UNIT);
			sender.sendMessage(new TextComponentString(
					NumberFormat.getNumberInstance().format(energy) + " " + CJCoreConfig.DEFAULT_ENERGY_UNIT.getSuffix()
							+ " / " + NumberFormat.getNumberInstance().format(capacity) + " "
							+ CJCoreConfig.DEFAULT_ENERGY_UNIT.getSuffix()));
		}
		if (args[4].equalsIgnoreCase("give")) {
			if (args.length < 7)
				throw new CommandException("command.tileentity.usage");
			long energy = Long.valueOf(args[5]);
			EnergyUnit unit = EnergyUnits.MINECRAFT_JOULES;
			if (!args[6].isEmpty())
				unit = EnergyUnits.byUnlocalizedName(args[6]);
			if (EnergyUtils.giveEnergy(te, energy, unit, false, side) == 0)
				throw new CommandException("command.tileentity.nogive");
		}
		if (args[4].equalsIgnoreCase("take")) {
			if (args.length < 7)
				throw new CommandException("command.tileentity.usage");
			long energy = Long.valueOf(args[5]);
			EnergyUnit unit = EnergyUnits.MINECRAFT_JOULES;
			if (!args[6].isEmpty())
				unit = EnergyUnits.byUnlocalizedName(args[6]);
			if (EnergyUtils.takeEnergy(te, energy, unit, false, side) == 0)
				throw new CommandException("command.tileentity.notake");
		}
	}

	/**
	 * Handle all of the inventory commands Callback for when the command is
	 * executed
	 * 
	 * @param te
	 *            The {@link TileEntity} at the position provided
	 * @param side
	 *            The side of the {@link TileEntity} for use with
	 *            {@link Capability}
	 * @param sender
	 *            The sender who executed the command
	 * @param args
	 *            The arguments that were passed
	 * @throws CommandException
	 *             Allow commands to go wrong
	 */
	public void handleInventoryCommands(TileEntity te, EnumFacing side, String[] args, ICommandSender sender)
			throws CommandException {
		if (args[4].equalsIgnoreCase("get")) {
			ImmutableList<ItemStack> inv = InventoryUtils.getInventoryStacked(te, side);
			for (ItemStack stack : inv)
				if (stack.getCount() > 0)
					sender.sendMessage(new TextComponentString(InventoryUtils.stackToString(stack)));
			return;
		}
		if(args.length <= 5)
			 throw new CommandException(I18n.format("command.tileentity.usage"));
		if (args[4].equalsIgnoreCase("insert")) {
			Item item = getItemByText(sender, args[5]);
			int amount = args.length >= 7 ? parseInt(args[6]) : 1;
			int meta = args.length >= 8 ? parseInt(args[7]) : 0;
			ItemStack stack = new ItemStack(item, amount, meta);

			if (args.length >= 9) {
				try {
					stack.setTagCompound(JsonToNBT.getTagFromJson(args[8]));
				} catch (NBTException e) {
					throw new CommandException("commands.give.tagError", new Object[] { e.getMessage() });
				}
			}
			InventoryUtils.insertStackInInventory(te, stack, false, side);
		}
		if (args[4].equalsIgnoreCase("extract")) {
			Item item = getItemByText(sender, args[5]);
			int amount = args.length >= 7 ? parseInt(args[6]) : 1;
			int meta = args.length >= 8 ? parseInt(args[7]) : 0;
			ItemStack stack = new ItemStack(item, amount, meta);

			if (args.length >= 9) {
				try {
					stack.setTagCompound(JsonToNBT.getTagFromJson(args[8]));
				} catch (NBTException e) {
					throw new CommandException("commands.give.tagError", new Object[] { e.getMessage() });
				}
			}
			InventoryUtils.extractStackFromInventory(te, stack, false, side);
		}
	}

}
