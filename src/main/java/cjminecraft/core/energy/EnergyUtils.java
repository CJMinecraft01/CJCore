package cjminecraft.core.energy;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import cjminecraft.core.CJCore;
import cjminecraft.core.energy.EnergyUnits.EnergyUnit;
import cjminecraft.core.energy.support.CoFHSupport;
import cjminecraft.core.energy.support.ForgeEnergySupport;
import cjminecraft.core.energy.support.IEnergySupport;
import cjminecraft.core.energy.support.TeslaSupport;
import cjminecraft.core.network.PacketHandler;
import cjminecraft.core.network.energy.PacketGetCapacity;
import cjminecraft.core.network.energy.PacketGetEnergy;
import cjminecraft.core.network.energy.PacketGetEnergyData;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import scala.annotation.meta.field;

/**
 * Utility class for energy
 * 
 * @author CJMinecraft
 *
 */
public class EnergyUtils {

	private static HashMap<String, HashMap<String, EnergyData>> cachedEnergyData = new HashMap<String, HashMap<String, EnergyData>>();

	/**
	 * Lists of registered support
	 */
	private static List<IEnergySupport> energyHolderSupport;
	private static List<IEnergySupport> energyConsumerSupport;
	private static List<IEnergySupport> energyProducerSupport;

	/**
	 * Should not be called outside of {@link CJCore}
	 */
	public static void preInit() {
		energyHolderSupport = Arrays.<IEnergySupport>asList(new CoFHSupport.CoFHHolderSupport(),
				new ForgeEnergySupport(), new TeslaSupport.TeslaHolderSupport());
		energyConsumerSupport = Arrays.<IEnergySupport>asList(new CoFHSupport.CoFHReceiverSupport(),
				new ForgeEnergySupport(), new TeslaSupport.TeslaConsumerSupport());
		energyProducerSupport = Arrays.<IEnergySupport>asList(new CoFHSupport.CoFHProviderSupport(),
				new ForgeEnergySupport(), new TeslaSupport.TeslaProducerSupport());
	}

	/**
	 * Add support for an energy holder.
	 * 
	 * @param support
	 *            The support you are registering
	 */
	public static void addEnergyHolderSupport(IEnergySupport support) {
		if (energyHolderSupport.contains(support))
			CJCore.logger.info(I18n.format("energy.utils.repeat_support", support.getClass().getSimpleName()));
		else {
			energyHolderSupport.add(support);
			CJCore.logger.info(I18n.format("energy.utils.support_success", support.getClass().getSimpleName()));
		}
	}

	/**
	 * Add support for an energy consumer
	 * 
	 * @param support
	 *            The support you are registering
	 */
	public static void addEnergyConsumerSupport(IEnergySupport support) {
		if (energyConsumerSupport.contains(support))
			CJCore.logger.info(I18n.format("energy.utils.repeat_support", support.getClass().getSimpleName()));
		else {
			energyConsumerSupport.add(support);
			CJCore.logger.info(I18n.format("energy.utils.support_success", support.getClass().getSimpleName()));
		}
	}

	/**
	 * Add support for an energy producer
	 * 
	 * @param support
	 *            The support you are registering
	 */
	public static void addEnergyProducerSupport(IEnergySupport support) {
		if (energyProducerSupport.contains(support))
			CJCore.logger.info(I18n.format("energy.utils.repeat_support", support.getClass().getSimpleName()));
		else {
			energyProducerSupport.add(support);
			CJCore.logger.info(I18n.format("energy.utils.support_success", support.getClass().getSimpleName()));
		}
	}

	/**
	 * Get the correct energy holder support from the {@link TileEntity}
	 * 
	 * @param <I>
	 *            The interface which represents this support
	 * @param te
	 *            The {@link TileEntity} which holds energy
	 * @param from
	 *            The side of the {@link TileEntity} for use with
	 *            {@link Capability}
	 * @return The {@link IEnergySupport} for the {@link TileEntity} if it has
	 *         support. Can be <code>null</code>
	 */
	@Nullable
	public static <I> IEnergySupport<I> getEnergyHolderSupport(TileEntity te, EnumFacing from) {
		if(te == null)
			return null;
		for (IEnergySupport<I> support : energyHolderSupport)
			if (support.hasSupport(te, from))
				return support;
		return null;
	}

	/**
	 * Get the correct energy consumer support from the {@link TileEntity}
	 * 
	 * @param <I>
	 *            The interface which represents this support
	 * @param te
	 *            The {@link TileEntity} which consumes energy
	 * @param from
	 *            The side of the {@link TileEntity} for use with
	 *            {@link Capability}
	 * @return The {@link IEnergySupport} for the {@link TileEntity} if it has
	 *         support. Can be <code>null</code>
	 */
	@Nullable
	public static <I> IEnergySupport<I> getEnergyConsumerSupport(TileEntity te, EnumFacing from) {
		if(te == null)
			return null;
		for (IEnergySupport<I> support : energyConsumerSupport)
			if (support.hasSupport(te, from))
				return support;
		return null;
	}

	/**
	 * Get the correct energy producer support from the {@link TileEntity}
	 * 
	 * @param <I>
	 *            The interface which represents this support
	 * 
	 * @param te
	 *            The {@link TileEntity} which produces energy
	 * @param from
	 *            The side of the {@link TileEntity} for use with
	 *            {@link Capability}
	 * @return The {@link IEnergySupport} for the {@link TileEntity} if it has
	 *         support. Can be <code>null</code>
	 */
	@Nullable
	public static <I> IEnergySupport<I> getEnergyProducerSupport(TileEntity te, EnumFacing from) {
		if(te == null)
			return null;
		for (IEnergySupport<I> support : energyProducerSupport)
			if (support.hasSupport(te, from))
				return support;
		return null;
	}

	/**
	 * Get the correct energy holder support from the {@link ItemStack}
	 * 
	 * @param <I>
	 *            The interface which represents this support
	 * @param stack
	 *            The {@link ItemStack} which holds energy
	 * @param from
	 *            The side of the {@link ItemStack} for use with
	 *            {@link Capability}
	 * @return The {@link IEnergySupport} for the {@link ItemStack} if it has
	 *         support. Can be <code>null</code>
	 */
	@Nullable
	public static <I> IEnergySupport<I> getEnergyHolderSupport(ItemStack stack, EnumFacing from) {
		if(stack == null || stack.getItem() == null)
			return null;
		for (IEnergySupport<I> support : energyHolderSupport)
			if (support.hasSupport(stack, from))
				return support;
		return null;
	}

	/**
	 * Get the correct energy consumer support from the {@link ItemStack}
	 * 
	 * @param <I>
	 *            The interface which represents this support
	 * @param stack
	 *            The {@link ItemStack} which consumes energy
	 * @param from
	 *            The side of the {@link ItemStack} for use with
	 *            {@link Capability}
	 * @return The {@link IEnergySupport} for the {@link ItemStack} if it has
	 *         support. Can be <code>null</code>
	 */
	@Nullable
	public static <I> IEnergySupport<I> getEnergyConsumerSupport(ItemStack stack, EnumFacing from) {
		if(stack == null || stack.getItem() == null)
			return null;
		for (IEnergySupport<I> support : energyConsumerSupport)
			if (support.hasSupport(stack, from))
				return support;
		return null;
	}

	/**
	 * Get the correct energy producer support from the {@link ItemStack}
	 * 
	 * @param <I>
	 *            The interface which represents this support
	 * @param stack
	 *            The {@link ItemStack} which produces energy
	 * @param from
	 *            The side of the {@link ItemStack} for use with
	 *            {@link Capability}
	 * @return The {@link IEnergySupport} for the {@link ItemStack} if it has
	 *         support. Can be <code>null</code>
	 */
	@Nullable
	public static <I> IEnergySupport<I> getEnergyProducerSupport(ItemStack stack, EnumFacing from) {
		if(stack == null || stack.getItem() == null)
			return null;
		for (IEnergySupport<I> support : energyProducerSupport)
			if (support.hasSupport(stack, from))
				return support;
		return null;
	}

	/**
	 * Gets any compatible energy support for the given {@link TileEntity}
	 * 
	 * @param <I>
	 *            The interface which represents this support
	 * @param te
	 *            The {@link TileEntity} which handles energy
	 * @param from
	 *            The side of the {@link TileEntity} for use with
	 *            {@link Capability}
	 * @return The {@link IEnergySupport} for the {@link TileEntity} if it has
	 *         support. Can be <code>null</code>
	 */
	@Nullable
	public static <I> IEnergySupport<I> getEnergySupport(TileEntity te, EnumFacing from) {
		IEnergySupport<I> support = getEnergyHolderSupport(te, from);
		if (support == null)
			support = getEnergyConsumerSupport(te, from);
		if (support == null)
			support = getEnergyProducerSupport(te, from);
		return support;
	}

	/**
	 * Gets any compatible energy support for the given {@link ItemStack}
	 * 
	 * @param <I>
	 *            The interface which represents this support
	 * @param stack
	 *            The {@link ItemStack} which handles energy
	 * @param from
	 *            The side of the {@link ItemStack} for use with
	 *            {@link Capability}
	 * @return The {@link IEnergySupport} for the {@link ItemStack} if it has
	 *         support. Can be <code>null</code>
	 */
	@Nullable
	public static <I> IEnergySupport<I> getEnergySupport(ItemStack stack, EnumFacing from) {
		IEnergySupport<I> support = getEnergyHolderSupport(stack, from);
		if (support == null)
			support = getEnergyConsumerSupport(stack, from);
		if (support == null)
			support = getEnergyProducerSupport(stack, from);
		return support;
	}

	/**
	 * States whether the {@link TileEntity} has a compatible energy support
	 * 
	 * @param te
	 *            The {@link TileEntity} to test
	 * @param from
	 *            The side of the {@link TileEntity} for use with
	 *            {@link Capability}
	 * @return Whether the {@link TileEntity} has a compatible energy support
	 */
	public static boolean hasSupport(TileEntity te, EnumFacing from) {
		return getEnergySupport(te, from) != null;
	}

	/**
	 * States whether the {@link ItemStack} has a compatible energy support
	 * 
	 * @param stack
	 *            The {@link ItemStack} to test
	 * @param from
	 *            The side of the {@link ItemStack} for use with
	 *            {@link Capability}
	 * @return Whether the {@link ItemStack} has a compatible energy support
	 */
	public static boolean hasSupport(ItemStack stack, EnumFacing from) {
		return getEnergySupport(stack, from) != null;
	}

	/**
	 * Convert one energy unit to another energy unit
	 * 
	 * @param from
	 *            The unit the energy is in
	 * @param to
	 *            The unit the energy wants to be in
	 * @param energy
	 *            The actual energy
	 * @return The converted energy
	 */
	public static long convertEnergy(EnergyUnit from, EnergyUnit to, long energy) {
		return energy / from.getMultiplier() * to.getMultiplier();
	}

	/**
	 * Get the energy stored from the given {@link TileEntity}
	 * 
	 * @param te
	 *            The {@link TileEntity} which holds the energy
	 * @param from
	 *            The side of the {@link TileEntity} for use with
	 *            {@link Capability}
	 * @return The amount of energy stored in the {@link TileEntity} in the
	 *         {@link EnergyUnit} {@link EnergyUnits#MINECRAFT_JOULES}
	 */
	public static long getEnergyStored(TileEntity te, EnumFacing from) {
		IEnergySupport support = getEnergySupport(te, from);
		if (support != null)
			return convertEnergy(support.defaultEnergyUnit(), EnergyUnits.MINECRAFT_JOULES,
					support.getEnergyStored(support.getContainer(te, from), from));
		return 0;
	}

	/**
	 * Get the energy stored from the given {@link ItemStack}
	 * 
	 * @param stack
	 *            The {@link ItemStack} which holds the energy
	 * @param from
	 *            The side of the {@link ItemStack} for use with
	 *            {@link Capability}
	 * @return The amount of energy stored in the {@link ItemStack} in the
	 *         {@link EnergyUnit} {@link EnergyUnits#MINECRAFT_JOULES}
	 */
	public static long getEnergyStored(ItemStack stack, EnumFacing from) {
		IEnergySupport support = getEnergySupport(stack, from);
		if (support != null)
			return convertEnergy(support.defaultEnergyUnit(), EnergyUnits.MINECRAFT_JOULES,
					support.getEnergyStored(support.getContainer(stack, from), from));
		return 0;
	}

	/**
	 * Get the capacity from the given {@link TileEntity}
	 * 
	 * @param te
	 *            The {@link TileEntity} which holds the energy
	 * @param from
	 *            The side of the {@link TileEntity} for use with
	 *            {@link Capability}
	 * @return The maximum amount of energy in the {@link TileEntity} in the
	 *         {@link EnergyUnit} {@link EnergyUnits#MINECRAFT_JOULES}
	 */
	public static long getCapacity(TileEntity te, EnumFacing from) {
		IEnergySupport support = getEnergySupport(te, from);
		if (support != null)
			return convertEnergy(support.defaultEnergyUnit(), EnergyUnits.MINECRAFT_JOULES,
					support.getCapacity(support.getContainer(te, from), from));
		return 0;
	}

	/**
	 * Get the capacity from the given {@link ItemStack}
	 * 
	 * @param stack
	 *            The {@link ItemStack} which holds the energy
	 * @param from
	 *            The side of the {@link ItemStack} for use with
	 *            {@link Capability}
	 * @return The maximum amount of energy in the {@link ItemStack} in the
	 *         {@link EnergyUnit} {@link EnergyUnits#MINECRAFT_JOULES}
	 */
	public static long getCapacity(ItemStack stack, EnumFacing from) {
		IEnergySupport support = getEnergySupport(stack, from);
		if (support != null)
			return convertEnergy(support.defaultEnergyUnit(), EnergyUnits.MINECRAFT_JOULES,
					support.getCapacity(support.getContainer(stack, from), from));
		return 0;
	}

	/**
	 * Give energy to the given {@link TileEntity}
	 * 
	 * @param te
	 *            The {@link TileEntity} which will receive energy
	 * @param energy
	 *            The energy to be given in the
	 *            {@link EnergyUnits#MINECRAFT_JOULES} unit
	 * @param simulate
	 *            Whether or not it is a simulation. If so, no energy is
	 *            actually given
	 * @param from
	 *            The side of the {@link TileEntity} for use with
	 *            {@link Capability}
	 * @return The amount of energy which was given (or would have been given if
	 *         it is simulated) in the {@link EnergyUnit}
	 *         {@link EnergyUnits#MINECRAFT_JOULES}
	 */
	public static long giveEnergy(TileEntity te, long energy, boolean simulate, EnumFacing from) {
		IEnergySupport support = getEnergyConsumerSupport(te, from);
		if (support != null)
			if (support.canReceive(support.getContainer(te, from), from))
				return convertEnergy(support.defaultEnergyUnit(), EnergyUnits.MINECRAFT_JOULES,
						support.giveEnergy(support.getContainer(te, from),
								convertEnergy(EnergyUnits.MINECRAFT_JOULES, support.defaultEnergyUnit(), energy),
								simulate, from));
		return 0;
	}

	/**
	 * Give energy to the given {@link ItemStack}
	 * 
	 * @param stack
	 *            The {@link ItemStack} which will receive energy
	 * @param energy
	 *            The energy to be given in the
	 *            {@link EnergyUnits#MINECRAFT_JOULES} unit
	 * @param simulate
	 *            Whether or not it is a simulation. If so, no energy is
	 *            actually given
	 * @param from
	 *            The side of the {@link ItemStack} for use with
	 *            {@link Capability}
	 * @return The amount of energy which was given (or would have been given if
	 *         it is simulated) in the {@link EnergyUnit}
	 *         {@link EnergyUnits#MINECRAFT_JOULES}
	 */
	public static long giveEnergy(ItemStack stack, long energy, boolean simulate, EnumFacing from) {
		IEnergySupport support = getEnergyConsumerSupport(stack, from);
		if (support != null)
			if (support.canReceive(support.getContainer(stack, from), from))
				return convertEnergy(support.defaultEnergyUnit(), EnergyUnits.MINECRAFT_JOULES,
						support.giveEnergy(support.getContainer(stack, from),
								convertEnergy(EnergyUnits.MINECRAFT_JOULES, support.defaultEnergyUnit(), energy),
								simulate, from));
		return 0;
	}

	/**
	 * Take energy from the given {@link TileEntity}
	 * 
	 * @param te
	 *            The {@link TileEntity} which will have extracted from
	 * @param energy
	 *            The energy to be taken in the
	 *            {@link EnergyUnits#MINECRAFT_JOULES} unit
	 * @param simulate
	 *            Whether or not it is a simulation. If so, no energy is
	 *            actually taken
	 * @param from
	 *            The side of the {@link TileEntity} for use with
	 *            {@link Capability}
	 * @return The amount of energy which was taken (or would have been taken if
	 *         it is simulated) in the {@link EnergyUnit}
	 *         {@link EnergyUnits#MINECRAFT_JOULES}
	 */
	public static long takeEnergy(TileEntity te, long energy, boolean simulate, EnumFacing from) {
		IEnergySupport support = getEnergyProducerSupport(te, from);
		if (support != null)
			if (support.canExtract(support.getContainer(te, from), from))
				return convertEnergy(support.defaultEnergyUnit(), EnergyUnits.MINECRAFT_JOULES,
						support.takeEnergy(support.getContainer(te, from),
								convertEnergy(EnergyUnits.MINECRAFT_JOULES, support.defaultEnergyUnit(), energy),
								simulate, from));
		return 0;
	}

	/**
	 * Take energy from the given {@link ItemStack}
	 * 
	 * @param stack
	 *            The {@link ItemStack} which will have extracted from
	 * @param energy
	 *            The energy to be taken in the
	 *            {@link EnergyUnits#MINECRAFT_JOULES} unit
	 * @param simulate
	 *            Whether or not it is a simulation. If so, no energy is
	 *            actually taken
	 * @param from
	 *            The side of the {@link ItemStack} for use with
	 *            {@link Capability}
	 * @return The amount of energy which was taken (or would have been taken if
	 *         it is simulated) in the {@link EnergyUnit}
	 *         {@link EnergyUnits#MINECRAFT_JOULES}
	 */
	public static long takeEnergy(ItemStack stack, long energy, boolean simulate, EnumFacing from) {
		IEnergySupport support = getEnergyProducerSupport(stack, from);
		if (support != null)
			if (support.canExtract(support.getContainer(stack, from), from))
				return convertEnergy(support.defaultEnergyUnit(), EnergyUnits.MINECRAFT_JOULES,
						support.takeEnergy(support.getContainer(stack, from),
								convertEnergy(EnergyUnits.MINECRAFT_JOULES, support.defaultEnergyUnit(), energy),
								simulate, from));
		return 0;
	}

	/**
	 * Checks whether the {@link TileEntity} can receive energy
	 * 
	 * @param te
	 *            The {@link TileEntity} to test
	 * @param from
	 *            The side of the {@link TileEntity} for use with
	 *            {@link Capability}
	 * @return Whether the {@link TileEntity} can receive energy
	 */
	public static boolean canReceive(TileEntity te, EnumFacing from) {
		IEnergySupport support = getEnergyConsumerSupport(te, from);
		if (support != null)
			return support.canReceive(support.getContainer(te, from), from);
		return false;
	}

	/**
	 * Checks whether the {@link ItemStack} can receive energy
	 * 
	 * @param stack
	 *            The {@link ItemStack} to test
	 * @param from
	 *            The side of the {@link ItemStack} for use with
	 *            {@link Capability}
	 * @return Whether the {@link ItemStack} can receive energy
	 */
	public static boolean canReceive(ItemStack stack, EnumFacing from) {
		IEnergySupport support = getEnergyConsumerSupport(stack, from);
		if (support != null)
			return support.canReceive(support.getContainer(stack, from), from);
		return false;
	}

	/**
	 * Checks whether the {@link TileEntity} can have energy extracted
	 * 
	 * @param te
	 *            The {@link TileEntity} to test
	 * @param from
	 *            The side of the {@link TileEntity} for use with
	 *            {@link Capability}
	 * @return Whether the {@link TileEntity} can have energy extracted
	 */
	public static boolean canExtract(TileEntity te, EnumFacing from) {
		IEnergySupport support = getEnergyProducerSupport(te, from);
		if (support != null)
			return support.canExtract(support.getContainer(te, from), from);
		return false;
	}

	/**
	 * Checks whether the {@link ItemStack} can have energy extracted
	 * 
	 * @param stack
	 *            The {@link ItemStack} to test
	 * @param from
	 *            The side of the {@link ItemStack} for use with
	 *            {@link Capability}
	 * @return Whether the {@link ItemStack} can have energy extracted
	 */
	public static boolean canExtract(ItemStack stack, EnumFacing from) {
		IEnergySupport support = getEnergyProducerSupport(stack, from);
		if (support != null)
			return support.canExtract(support.getContainer(stack, from), from);
		return false;
	}

	/**
	 * Takes energy from all connecting energy handlers surrounding the target
	 * block
	 * 
	 * @param world
	 *            The world to get the {@link TileEntity} from
	 * @param pos
	 *            The center position
	 * @param energy
	 *            The energy to take altogether. Will be distributed evenly
	 *            between the {@link TileEntity}s. Needs to be in the
	 *            {@link EnergyUnit} {@link EnergyUnits#MINECRAFT_JOULES}
	 * @param simulate
	 *            Whether it is a simulation or not. If so, the energy won't
	 *            actually be taken
	 * @return The amount of energy taken in the {@link EnergyUnit}
	 *         {@link EnergyUnits#MINECRAFT_JOULES}
	 */
	public static long takeEnergyAllFaces(World world, BlockPos pos, long energy, boolean simulate) {
		HashMap<EnumFacing, TileEntity> tiles = new HashMap<EnumFacing, TileEntity>();
		for (EnumFacing side : EnumFacing.VALUES) {
			TileEntity te = world.getTileEntity(pos.offset(side));
			if (te == null)
				continue;
			if (hasSupport(te, side))
				tiles.put(side, te);
		}
		long energyPerSide = energy / tiles.size();
		Iterator<Entry<EnumFacing, TileEntity>> tilesIterator = tiles.entrySet().iterator();
		long energyTaken = 0;
		long extraEnergy = 0;
		while (tilesIterator.hasNext()) {
			Entry<EnumFacing, TileEntity> entry = tilesIterator.next();
			EnumFacing side = entry.getKey();
			TileEntity te = entry.getValue();
			long et = takeEnergy(te, energyPerSide + extraEnergy, simulate, side);
			energyTaken += et;
			if (et < energyPerSide)
				extraEnergy = energyPerSide - et;
			else
				extraEnergy = 0;
		}
		return energyTaken;
	}

	/**
	 * Gives energy to all connecting energy handlers surrounding the target
	 * block
	 * 
	 * @param world
	 *            The world to get the {@link TileEntity} from
	 * @param pos
	 *            The center position
	 * @param energy
	 *            The energy to give altogether. Will be distributed evenly
	 *            between the {@link TileEntity}s. Needs to be in the
	 *            {@link EnergyUnit} {@link EnergyUnits#MINECRAFT_JOULES}
	 * @param simulate
	 *            Whether it is a simulation or not. If so, the energy won't
	 *            actually be given
	 * @return The amount of energy given in the {@link EnergyUnit}
	 *         {@link EnergyUnits#MINECRAFT_JOULES}
	 */
	public static long giveEnergyAllFaces(World world, BlockPos pos, long energy, boolean simulate) {
		HashMap<EnumFacing, TileEntity> tiles = new HashMap<EnumFacing, TileEntity>();
		for (EnumFacing side : EnumFacing.VALUES) {
			TileEntity te = world.getTileEntity(pos.offset(side));
			if (te == null)
				continue;
			if (hasSupport(te, side))
				tiles.put(side, te);
		}
		long energyPerSide = energy / tiles.size();
		Iterator<Entry<EnumFacing, TileEntity>> tilesIterator = tiles.entrySet().iterator();
		long energyGiven = 0;
		long extraEnergy = 0;
		while (tilesIterator.hasNext()) {
			Entry<EnumFacing, TileEntity> entry = tilesIterator.next();
			EnumFacing side = entry.getKey();
			TileEntity te = entry.getValue();
			long eg = giveEnergy(te, energyPerSide + extraEnergy, simulate, side);
			energyGiven += eg;
			if (eg < energyPerSide)
				extraEnergy = energyPerSide - eg;
			else
				extraEnergy = 0;
		}
		return energyGiven;
	}

	/**
	 * Saves data when syncing with the server
	 * 
	 * @param modid
	 *            The modid so each mod has its own cache of data
	 * @param className
	 *            The name of the class so each class can only sync one data
	 * @param data
	 *            The data to sync
	 */
	public static void addCachedEnergyData(String modid, String className, EnergyData data) {
		if (!cachedEnergyData.containsKey(modid))
			cachedEnergyData.put(modid, new HashMap<String, EnergyData>());
		if (!cachedEnergyData.get(modid).containsKey(className))
			cachedEnergyData.get(modid).put(className, data);
	}

	/**
	 * Retrieves the latest data from the calling class
	 * 
	 * @param modid
	 *            The modid to get mod specific data
	 * @return The latest data from the calling class
	 */
	public static EnergyData getCachedEnergyData(String modid) {
		return getCachedEnergyData(modid, new Exception().getStackTrace()[1].getClassName());
	}

	/**
	 * Retrieves the latest data from the given class
	 * 
	 * @param modid
	 *            The modid to get mod specific data
	 * @param className
	 *            The name of the class which the data was requested from
	 * @return The latest data from the given class
	 */
	public static EnergyData getCachedEnergyData(String modid, String className) {
		if (!cachedEnergyData.containsKey(modid))
			return null;
		if (!cachedEnergyData.get(modid).containsKey(className))
			return null;
		EnergyData data = cachedEnergyData.get(modid).get(className);
		cachedEnergyData.get(modid).remove(className);
		return data;
	}

	/**
	 * Sync energy with the server. To get the data use
	 * {@link #getCachedEnergyData(String)} or
	 * {@link #getCachedEnergyData(String, String)} and then use
	 * {@link EnergyData#getEnergy()}. This will store the data in the calling
	 * class in the cache
	 * 
	 * @param pos
	 *            The position of the {@link TileEntity}
	 * @param side
	 *            The side of the {@link TileEntity} for use with
	 *            {@link Capability}
	 * @param modid
	 *            The modid for mod specific data
	 */
	public static void syncEnergy(BlockPos pos, EnumFacing side, String modid) {
		PacketHandler.INSTANCE.sendToServer(
				new PacketGetEnergy(pos, side, false, modid, new Exception().getStackTrace()[1].getClassName()));
	}

	/**
	 * Sync energy with the server. To get the data use
	 * {@link #getCachedEnergyData(String)} or
	 * {@link #getCachedEnergyData(String, String)} and then use
	 * {@link EnergyData#getEnergy()}. This will store the data in the class
	 * path provided in the cache
	 * 
	 * @param pos
	 *            The position of the {@link TileEntity}
	 * @param side
	 *            The side of the {@link TileEntity} for use with
	 *            {@link Capability}
	 * @param modid
	 *            The modid for mod specific data
	 * @param className
	 *            The name of the class for the cache
	 */
	public static void syncEnergy(BlockPos pos, EnumFacing side, String modid, String className) {
		PacketHandler.INSTANCE.sendToServer(new PacketGetEnergy(pos, side, false, modid, className));
	}

	/**
	 * Sync capacity with the server. To get the data use
	 * {@link #getCachedEnergyData(String)} or
	 * {@link #getCachedEnergyData(String, String)} and then use
	 * {@link EnergyData#getCapacity()}. This will store the data in the calling
	 * class in the cache
	 * 
	 * @param pos
	 *            The position of the {@link TileEntity}
	 * @param side
	 *            The side of the {@link TileEntity} for use with
	 *            {@link Capability}
	 * @param modid
	 *            The modid for mod specific data
	 */
	public static void syncCapacity(BlockPos pos, EnumFacing side, String modid) {
		PacketHandler.INSTANCE.sendToServer(
				new PacketGetCapacity(pos, side, false, modid, new Exception().getStackTrace()[1].getClassName()));
	}

	/**
	 * Sync capacity with the server. To get the data use
	 * {@link #getCachedEnergyData(String)} or
	 * {@link #getCachedEnergyData(String, String)} and then use
	 * {@link EnergyData#getEnergy()}. This will store the data in the class
	 * path provided in the cache
	 * 
	 * @param pos
	 *            The position of the {@link TileEntity}
	 * @param side
	 *            The side of the {@link TileEntity} for use with
	 *            {@link Capability}
	 * @param modid
	 *            The modid for mod specific data
	 * @param className
	 *            The name of the class for the cache
	 */
	public static void syncCapacity(BlockPos pos, EnumFacing side, String modid, String className) {
		PacketHandler.INSTANCE.sendToServer(new PacketGetCapacity(pos, side, false, modid, className));
	}

	/**
	 * Sync {@link EnergyData} with the server. To get the data use
	 * {@link #getCachedEnergyData(String)} or
	 * {@link #getCachedEnergyData(String, String)}. This will store the data in
	 * the calling class in the cache
	 * 
	 * @param pos
	 *            The position of the {@link TileEntity}
	 * @param side
	 *            The side of the {@link TileEntity} for use with
	 *            {@link Capability}
	 * @param modid
	 *            The modid for mod specific data
	 */
	public static void syncEnergyData(BlockPos pos, EnumFacing side, String modid) {
		PacketHandler.INSTANCE.sendToServer(
				new PacketGetEnergyData(pos, side, false, modid, new Exception().getStackTrace()[1].getClassName()));
	}

	/**
	 * Sync {@link EnergyData} with the server. To get the data use
	 * {@link #getCachedEnergyData(String)} or
	 * {@link #getCachedEnergyData(String, String)} and then use
	 * {@link EnergyData#getEnergy()}. This will store the data in the class
	 * path provided in the cache
	 * 
	 * @param pos
	 *            The position of the {@link TileEntity}
	 * @param side
	 *            The side of the {@link TileEntity} for use with
	 *            {@link Capability}
	 * @param modid
	 *            The modid for mod specific data
	 * @param className
	 *            The name of the class for the cache
	 */
	public static void syncEnergyData(BlockPos pos, EnumFacing side, String modid, String className) {
		PacketHandler.INSTANCE.sendToServer(new PacketGetEnergyData(pos, side, false, modid, className));
	}

	/**
	 * Sync energy using a {@link Field} in the calling class. This will set the
	 * fields value to the energy of the {@link TileEntity} at the given
	 * {@link BlockPos}
	 * 
	 * @param pos
	 *            The position of the {@link TileEntity}
	 * @param side
	 *            The side of the {@link TileEntity} for use with
	 *            {@link Capability}
	 * @param energyFieldName
	 *            The name of the field which will hold the energy. Must be a
	 *            <code>long</code>
	 */
	public static void syncEnergyField(BlockPos pos, EnumFacing side, String energyFieldName) {
		PacketHandler.INSTANCE.sendToServer(new PacketGetEnergy(pos, side, true,
				new Exception().getStackTrace()[1].getClassName(), energyFieldName));
	}

	/**
	 * Sync energy using a {@link Field} in the class provided. This will set
	 * the fields value to the energy of the {@link TileEntity} at the given
	 * {@link BlockPos}
	 * 
	 * @param pos
	 *            The position of the {@link TileEntity}
	 * @param side
	 *            The side of the {@link TileEntity} for use with
	 *            {@link Capability}
	 * @param className
	 *            The name of the class which holds the {@link field}
	 * @param energyFieldName
	 *            The name of the field which will hold the energy. Must be a
	 *            <code>long</code>
	 */
	public static void syncEnergyField(BlockPos pos, EnumFacing side, String className, String energyFieldName) {
		PacketHandler.INSTANCE.sendToServer(new PacketGetEnergy(pos, side, true, className, energyFieldName));
	}

	/**
	 * Sync capacity using a {@link Field} in the calling class. This will set
	 * the fields value to the capacity of the {@link TileEntity} at the given
	 * {@link BlockPos}
	 * 
	 * @param pos
	 *            The position of the {@link TileEntity}
	 * @param side
	 *            The side of the {@link TileEntity} for use with
	 *            {@link Capability}
	 * @param capacityFieldName
	 *            The name of the field which will hold the capacity. Must be a
	 *            <code>long</code>
	 */
	public static void syncCapacityField(BlockPos pos, EnumFacing side, String capacityFieldName) {
		PacketHandler.INSTANCE.sendToServer(new PacketGetCapacity(pos, side, true,
				new Exception().getStackTrace()[1].getClassName(), capacityFieldName));
	}

	/**
	 * Sync capacity using a {@link Field} in the class provided. This will set
	 * the fields value to the capacity of the {@link TileEntity} at the given
	 * {@link BlockPos}
	 * 
	 * @param pos
	 *            The position of the {@link TileEntity}
	 * @param side
	 *            The side of the {@link TileEntity} for use with
	 *            {@link Capability}
	 * @param className
	 *            The name of the class which holds the {@link field}
	 * @param capacityFieldName
	 *            The name of the field which will hold the capacity. Must be a
	 *            <code>long</code>
	 */
	public static void syncCapacityField(BlockPos pos, EnumFacing side, String className, String capacityFieldName) {
		PacketHandler.INSTANCE.sendToServer(new PacketGetCapacity(pos, side, true, className, capacityFieldName));
	}

	/**
	 * Sync energy and capacity using {@link Field}s in the calling class. This
	 * will set the fields values to the energy and capacity (each respectively)
	 * of the {@link TileEntity} at the given {@link BlockPos}
	 * 
	 * @param pos
	 *            The position of the {@link TileEntity}
	 * @param side
	 *            The side of the {@link TileEntity} for use with
	 *            {@link Capability}
	 * @param energyFieldName
	 *            The name of the field which will hold the energy. Must be a
	 *            <code>long</code>
	 * @param capacityFieldName
	 *            The name of the field which will hold the capacity. Must be a
	 *            <code>long</code>
	 */
	public static void syncEnergyDataFields(BlockPos pos, EnumFacing side, String energyFieldName,
			String capacityFieldName) {
		PacketHandler.INSTANCE.sendToServer(new PacketGetEnergyData(pos, side, true,
				new Exception().getStackTrace()[1].getClassName(), energyFieldName, capacityFieldName));
	}

	/**
	 * Sync energy and capacity using {@link Field}s in the class provided. This
	 * will set the fields values to the energy and capacity (each respectively)
	 * of the {@link TileEntity} at the given {@link BlockPos}
	 * 
	 * @param pos
	 *            The position of the {@link TileEntity}
	 * @param side
	 *            The side of the {@link TileEntity} for use with
	 *            {@link Capability}
	 * @param className
	 *            The name of the class which holds the {@link field}
	 * @param energyFieldName
	 *            The name of the field which will hold the energy. Must be a
	 *            <code>long</code>
	 * @param capacityFieldName
	 *            The name of the field which will hold the capacity. Must be a
	 *            <code>long</code>
	 */
	public static void syncEnergyDataFields(BlockPos pos, EnumFacing side, String className, String energyFieldName,
			String capacityFieldName) {
		PacketHandler.INSTANCE
				.sendToServer(new PacketGetEnergyData(pos, side, true, className, energyFieldName, capacityFieldName));
	}

}
