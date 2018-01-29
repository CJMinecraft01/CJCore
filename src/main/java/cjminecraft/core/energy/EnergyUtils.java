package cjminecraft.core.energy;

import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import cjminecraft.core.CJCore;
import cjminecraft.core.config.CJCoreConfig;
import cjminecraft.core.energy.EnergyUnits.EnergyUnit;
import cjminecraft.core.energy.support.CoFHSupport;
import cjminecraft.core.energy.support.ForgeEnergySupport;
import cjminecraft.core.energy.support.IEnergySupport;
import cjminecraft.core.energy.support.IndustrialCraftSupport;
import cjminecraft.core.energy.support.TeslaSupport;
import cjminecraft.core.network.PacketHandler;
import cjminecraft.core.network.energy.PacketGetCapacity;
import cjminecraft.core.network.energy.PacketGetEnergy;
import cjminecraft.core.network.energy.PacketGetEnergyData;
import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.api.ITeslaProducer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.common.Loader;
import scala.annotation.meta.field;

/**
 * Utility class for energy
 * 
 * @author CJMinecraft
 *
 */
public class EnergyUtils {

	private static HashMap<String, HashMap<String, EnergyData>> cachedEnergyData = new HashMap<String, HashMap<String, EnergyData>>();

	public static final boolean TESLA_LOADED = Loader.isModLoaded("tesla");
	public static final boolean INDUSTRAIL_CRAFT_LOADED = Loader.isModLoaded("ic2");
	public static final boolean REDSTONE_FLUX_API_LOADED = Loader.isModLoaded("redstoneflux");

	@CapabilityInject(ITeslaConsumer.class)
	public static Capability<ITeslaConsumer> TESLA_CONSUMER;

	@CapabilityInject(ITeslaProducer.class)
	public static Capability<ITeslaProducer> TESLA_PRODUCER;

	@CapabilityInject(ITeslaHolder.class)
	public static Capability<ITeslaHolder> TESLA_HOLDER;

	/**
	 * Lists of registered support
	 */
	private static List<IEnergySupport> energyHolderSupport = new ArrayList<IEnergySupport>();
	private static List<IEnergySupport> energyConsumerSupport = new ArrayList<IEnergySupport>();
	private static List<IEnergySupport> energyProducerSupport = new ArrayList<IEnergySupport>();

	/**
	 * Should not be called outside of {@link CJCore}
	 */
	public static void preInit() {
		CJCore.logger.info("Adding Forge Energy Support!");
		addEnergyHolderSupport(new ForgeEnergySupport.ForgeEnergyHolder());
		addEnergyConsumerSupport(new ForgeEnergySupport.ForgeEnergyConsumer());
		addEnergyProducerSupport(new ForgeEnergySupport.ForgeEnergyProducer());

		if (REDSTONE_FLUX_API_LOADED) {
			CJCore.logger.info("Adding CoFH Support!");
			addEnergyHolderSupport(new CoFHSupport.CoFHHolderSupport());
			addEnergyConsumerSupport(new CoFHSupport.CoFHReceiverSupport());
			addEnergyProducerSupport(new CoFHSupport.CoFHProviderSupport());
		}
		if (TESLA_LOADED) {
			CJCore.logger.info("Adding Tesla Support!");
			addEnergyHolderSupport(new TeslaSupport.TeslaHolderSupport());
			addEnergyConsumerSupport(new TeslaSupport.TeslaConsumerSupport());
			addEnergyProducerSupport(new TeslaSupport.TeslaProducerSupport());
		}
		if (Loader.isModLoaded("ic2")) {
			CJCore.logger.info("Adding Industrial Craft 2 Support!");
			addEnergyHolderSupport(new IndustrialCraftSupport.IndustrialCraftHolderSupport());
			addEnergyConsumerSupport(new IndustrialCraftSupport.IndustrialCraftSinkSupport());
			addEnergyProducerSupport(new IndustrialCraftSupport.IndustrialCraftSourceSupport());
		}
	}

	/**
	 * Add support for an energy holder.
	 * 
	 * @param support
	 *            The support you are registering
	 */
	public static void addEnergyHolderSupport(@Nonnull IEnergySupport support) {
		if (energyHolderSupport.contains(support))
			CJCore.logger.info(String.format("A energy support of type %s has already been registered - SKIPPING",
					support.getClass().getSimpleName()));
		else {
			energyHolderSupport.add(support);
			CJCore.logger.info(
					String.format("Successfully registered energy support %s", support.getClass().getSimpleName()));
		}
	}

	/**
	 * Add support for an energy consumer
	 * 
	 * @param support
	 *            The support you are registering
	 */
	public static void addEnergyConsumerSupport(@Nonnull IEnergySupport support) {
		if (energyConsumerSupport.contains(support))
			CJCore.logger.info(String.format("A energy support of type %s has already been registered - SKIPPING",
					support.getClass().getSimpleName()));
		else {
			energyConsumerSupport.add(support);
			CJCore.logger.info(
					String.format("Successfully registered energy support %s", support.getClass().getSimpleName()));
		}
	}

	/**
	 * Add support for an energy producer
	 * 
	 * @param support
	 *            The support you are registering
	 */
	public static void addEnergyProducerSupport(@Nonnull IEnergySupport support) {
		if (energyProducerSupport.contains(support))
			CJCore.logger.info(String.format("A energy support of type %s has already been registered - SKIPPING",
					support.getClass().getSimpleName()));
		else {
			energyProducerSupport.add(support);
			CJCore.logger.info(
					String.format("Successfully registered energy support %s", support.getClass().getSimpleName()));
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
	public static <I> IEnergySupport<I> getEnergyHolderSupport(@Nullable TileEntity te, @Nullable EnumFacing from) {
		if (te == null)
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
	public static <I> IEnergySupport<I> getEnergyConsumerSupport(@Nullable TileEntity te, @Nullable EnumFacing from) {
		if (te == null)
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
	public static <I> IEnergySupport<I> getEnergyProducerSupport(@Nullable TileEntity te, @Nullable EnumFacing from) {
		if (te == null)
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
	public static <I> IEnergySupport<I> getEnergyHolderSupport(@Nullable ItemStack stack, @Nullable EnumFacing from) {
		if (stack == null || stack.isEmpty())
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
	public static <I> IEnergySupport<I> getEnergyConsumerSupport(@Nullable ItemStack stack, @Nullable EnumFacing from) {
		if (stack == null || stack.isEmpty())
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
	public static <I> IEnergySupport<I> getEnergyProducerSupport(@Nullable ItemStack stack, @Nullable EnumFacing from) {
		if (stack == null || stack.isEmpty())
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
	public static <I> IEnergySupport<I> getEnergySupport(@Nullable TileEntity te, @Nullable EnumFacing from) {
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
	public static <I> IEnergySupport<I> getEnergySupport(@Nullable ItemStack stack, @Nullable EnumFacing from) {
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
	public static boolean hasSupport(@Nullable TileEntity te, @Nullable EnumFacing from) {
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
	public static boolean hasSupport(@Nullable ItemStack stack, @Nullable EnumFacing from) {
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
		return (long) ((double) energy / (double) from.getMultiplier() * (double) to.getMultiplier());
	}

	/**
	 * Convert one energy unit to another energy unit leaving the conversion in
	 * a double and not a long
	 * 
	 * @param from
	 *            The unit the energy is in
	 * @param to
	 *            The unit the energy wants to be in
	 * @param energy
	 *            The actual energy
	 * @return The converted energy
	 */
	public static double convertEnergy(EnergyUnit from, EnergyUnit to, double energy) {
		return (energy / (double) from.getMultiplier() * (double) to.getMultiplier());
	}

	/**
	 * Displays any energy in a short and simplified {@link String}
	 * 
	 * @param energy
	 *            The energy
	 * @param unit
	 *            The {@link EnergyUnit} for the energy
	 * @return The simple {@link String} representation of the energy
	 */
	public static String getEnergyAsString(long energy, @Nonnull EnergyUnit unit) {
		if (energy < 1000)
			return energy + " " + unit.getSuffix();
		int exp = (int) (Math.log(energy) / Math.log(1000));
		char prefix = "KMGTPE".charAt(exp - 1);
		return String.format("%.1f %s" + unit.getSuffix(), energy / Math.pow(1000, exp), prefix);
	}

	/**
	 * Displays any energy and capacity in the same way a energy bar would
	 * 
	 * @param energy
	 *            The energy in the {@link CJCoreConfig#DEFAULT_ENERGY_UNIT}
	 * @param capacity
	 *            The capacity in the {@link CJCoreConfig#DEFAULT_ENERGY_UNIT}
	 * @return The formatted energy string
	 */
	public static String getFormattedEnergy(long energy, long capacity) {
		if (CJCoreConfig.ENERGY_BAR_SIMPLIFY_ENERGY)
			return EnergyUtils.getEnergyAsString(energy, CJCoreConfig.DEFAULT_ENERGY_UNIT)
					+ (CJCoreConfig.ENERGY_BAR_SHOW_CAPACITY
							? " / " + EnergyUtils.getEnergyAsString(capacity, CJCoreConfig.DEFAULT_ENERGY_UNIT) : "");
		else
			return NumberFormat.getInstance().format(energy) + " " + CJCoreConfig.DEFAULT_ENERGY_UNIT.getSuffix()
					+ (CJCoreConfig.ENERGY_BAR_SHOW_CAPACITY ? " / " + NumberFormat.getInstance().format(capacity) + " "
							+ CJCoreConfig.DEFAULT_ENERGY_UNIT.getSuffix() : "");
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
	@Deprecated
	public static long getEnergyStored(@Nullable TileEntity te, @Nullable EnumFacing from) {
		return getEnergyStored(te, from, EnergyUnits.MINECRAFT_JOULES);
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
	@Deprecated
	public static long getEnergyStored(@Nullable ItemStack stack, @Nullable EnumFacing from) {
		return getEnergyStored(stack, from, EnergyUnits.MINECRAFT_JOULES);
	}

	/**
	 * Get the energy stored from the given {@link TileEntity}
	 * 
	 * @param te
	 *            The {@link TileEntity} which holds the energy
	 * @param from
	 *            The side of the {@link TileEntity} for use with
	 *            {@link Capability}
	 * @param unit
	 *            The unit the energy stored will be returned in
	 * @return The amount of energy stored in the {@link TileEntity} in the
	 *         {@link EnergyUnit} provided
	 */
	public static long getEnergyStored(@Nullable TileEntity te, @Nullable EnumFacing from, @Nonnull EnergyUnit unit) {
		IEnergySupport support = getEnergySupport(te, from);
		if (support != null)
			return convertEnergy(support.defaultEnergyUnit(), unit,
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
	 * @param unit
	 *            The unit the energy stored will be returned in
	 * @return The amount of energy stored in the {@link ItemStack} in the
	 *         {@link EnergyUnit} provided
	 */
	public static long getEnergyStored(@Nullable ItemStack stack, @Nullable EnumFacing from, @Nonnull EnergyUnit unit) {
		IEnergySupport support = getEnergySupport(stack, from);
		if (support != null)
			return convertEnergy(support.defaultEnergyUnit(), unit,
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
	@Deprecated
	public static long getCapacity(@Nullable TileEntity te, @Nullable EnumFacing from) {
		return getCapacity(te, from, EnergyUnits.MINECRAFT_JOULES);
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
	@Deprecated
	public static long getCapacity(@Nullable ItemStack stack, @Nullable EnumFacing from) {
		return getCapacity(stack, from, EnergyUnits.MINECRAFT_JOULES);
	}

	/**
	 * Get the capacity from the given {@link TileEntity}
	 * 
	 * @param te
	 *            The {@link TileEntity} which holds the energy
	 * @param from
	 *            The side of the {@link TileEntity} for use with
	 *            {@link Capability}
	 * @param unit
	 *            The unit the capacity will be returned in
	 * @return The maximum amount of energy in the {@link TileEntity} in the
	 *         {@link EnergyUnit} provided
	 */
	public static long getCapacity(@Nullable TileEntity te, @Nullable EnumFacing from, @Nonnull EnergyUnit unit) {
		IEnergySupport support = getEnergySupport(te, from);
		if (support != null)
			return convertEnergy(support.defaultEnergyUnit(), unit,
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
	 * @param unit
	 *            The unit the capacity will be returned in
	 * @return The maximum amount of energy in the {@link ItemStack} in the
	 *         {@link EnergyUnit} provided
	 */
	public static long getCapacity(@Nullable ItemStack stack, @Nullable EnumFacing from, @Nonnull EnergyUnit unit) {
		IEnergySupport support = getEnergySupport(stack, from);
		if (support != null)
			return convertEnergy(support.defaultEnergyUnit(), unit,
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
	@Deprecated
	public static long giveEnergy(TileEntity te, long energy, boolean simulate, EnumFacing from) {
		return giveEnergy(te, energy, EnergyUnits.MINECRAFT_JOULES, simulate, from);
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
	@Deprecated
	public static long giveEnergy(ItemStack stack, long energy, boolean simulate, EnumFacing from) {
		return giveEnergy(stack, energy, EnergyUnits.MINECRAFT_JOULES, simulate, from);
	}

	/**
	 * Give energy to the given {@link TileEntity}
	 * 
	 * @param te
	 *            The {@link TileEntity} which will receive energy
	 * @param energy
	 *            The energy to be given in the provided {@link EnergyUnit}
	 * @param unit
	 *            The {@link EnergyUnit} of the energy to give and the energy
	 *            returned
	 * @param simulate
	 *            Whether or not it is a simulation. If so, no energy is
	 *            actually given
	 * @param from
	 *            The side of the {@link TileEntity} for use with
	 *            {@link Capability}
	 * @return The amount of energy which was given (or would have been given if
	 *         it is simulated) in the {@link EnergyUnit} provided
	 */
	public static long giveEnergy(TileEntity te, long energy, @Nonnull EnergyUnit unit, boolean simulate,
			EnumFacing from) {
		IEnergySupport support = getEnergyConsumerSupport(te, from);
		if (support != null && support.canReceive(support.getContainer(te, from), from))
			return convertEnergy(support.defaultEnergyUnit(), unit, support.giveEnergy(support.getContainer(te, from),
					convertEnergy(unit, support.defaultEnergyUnit(), energy), simulate, from));
		return 0;
	}

	/**
	 * Give energy to the given {@link ItemStack}
	 * 
	 * @param stack
	 *            The {@link ItemStack} which will receive energy
	 * @param energy
	 *            The energy to be given in the provided {@link EnergyUnit}
	 * @param unit
	 *            The {@link EnergyUnit} of the energy to give and the energy
	 *            returned
	 * @param simulate
	 *            Whether or not it is a simulation. If so, no energy is
	 *            actually given
	 * @param from
	 *            The side of the {@link ItemStack} for use with
	 *            {@link Capability}
	 * @return The amount of energy which was given (or would have been given if
	 *         it is simulated) in the {@link EnergyUnit} provided
	 */
	public static long giveEnergy(ItemStack stack, long energy, @Nonnull EnergyUnit unit, boolean simulate,
			EnumFacing from) {
		IEnergySupport support = getEnergyConsumerSupport(stack, from);
		if (support != null && support.canReceive(support.getContainer(stack, from), from))
			return convertEnergy(support.defaultEnergyUnit(), unit,
					support.giveEnergy(support.getContainer(stack, from),
							convertEnergy(unit, support.defaultEnergyUnit(), energy), simulate, from));
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
	@Deprecated
	public static long takeEnergy(TileEntity te, long energy, boolean simulate, EnumFacing from) {
		return takeEnergy(te, energy, EnergyUnits.MINECRAFT_JOULES, simulate, from);
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
	@Deprecated
	public static long takeEnergy(ItemStack stack, long energy, boolean simulate, EnumFacing from) {
		return takeEnergy(stack, energy, EnergyUnits.MINECRAFT_JOULES, simulate, from);
	}

	/**
	 * Take energy from the given {@link TileEntity}
	 * 
	 * @param te
	 *            The {@link TileEntity} which will have extracted from
	 * @param energy
	 *            The energy to be taken in the {@link EnergyUnit} provided
	 * @param unit
	 *            The {@link EnergyUnit} the energy will be returned in
	 * @param simulate
	 *            Whether or not it is a simulation. If so, no energy is
	 *            actually taken
	 * @param from
	 *            The side of the {@link TileEntity} for use with
	 *            {@link Capability}
	 * @return The amount of energy which was taken (or would have been taken if
	 *         it is simulated) in the {@link EnergyUnit} provided
	 */
	public static long takeEnergy(TileEntity te, long energy, @Nonnull EnergyUnit unit, boolean simulate,
			EnumFacing from) {
		IEnergySupport support = getEnergyProducerSupport(te, from);
		if (support != null && support.canExtract(support.getContainer(te, from), from))
			return convertEnergy(support.defaultEnergyUnit(), unit, support.takeEnergy(support.getContainer(te, from),
					convertEnergy(unit, support.defaultEnergyUnit(), energy), simulate, from));
		return 0;
	}

	/**
	 * Take energy from the given {@link ItemStack}
	 * 
	 * @param stack
	 *            The {@link ItemStack} which will have extracted from
	 * @param energy
	 *            The energy to be taken in the {@link EnergyUnit} provided
	 * @param unit
	 *            The {@link EnergyUnit} the energy will be returned in
	 * @param simulate
	 *            Whether or not it is a simulation. If so, no energy is
	 *            actually taken
	 * @param from
	 *            The side of the {@link ItemStack} for use with
	 *            {@link Capability}
	 * @return The amount of energy which was taken (or would have been taken if
	 *         it is simulated) in the {@link EnergyUnit} provided
	 */
	public static long takeEnergy(ItemStack stack, long energy, @Nonnull EnergyUnit unit, boolean simulate,
			EnumFacing from) {
		IEnergySupport support = getEnergyProducerSupport(stack, from);
		if (support != null && support.canExtract(support.getContainer(stack, from), from))
			return convertEnergy(support.defaultEnergyUnit(), unit,
					support.takeEnergy(support.getContainer(stack, from),
							convertEnergy(unit, support.defaultEnergyUnit(), energy), simulate, from));
		return 0;
	}

	/**
	 * Set the energy inside of the given {@link TileEntity}
	 * 
	 * @param te
	 *            The {@link TileEntity} which holds energy
	 * @param energy
	 *            The energy to set in the {@link EnergyUnits#MINECRAFT_JOULES}
	 *            unit
	 * @param from
	 *            The side of the {@link TileEntity} for use with
	 *            {@link Capability} {@link Capability}
	 * @return The energy which was set in the {@link EnergyUnit}
	 *         {@link EnergyUnits#MINECRAFT_JOULES}
	 */
	@Deprecated
	public static long setEnergy(TileEntity te, long energy, EnumFacing from) {
		return setEnergy(te, energy, EnergyUnits.MINECRAFT_JOULES, from);
	}

	/**
	 * Set the energy inside of the given {@link ItemStack}
	 * 
	 * @param stack
	 *            The {@link ItemStack} which holds energy
	 * @param energy
	 *            The energy to set in the {@link EnergyUnits#MINECRAFT_JOULES}
	 *            unit
	 * @param from
	 *            The side of the {@link ItemStack} for use with
	 *            {@link Capability}
	 * @return The energy which was set in the {@link EnergyUnit}
	 *         {@link EnergyUnits#MINECRAFT_JOULES}
	 */
	@Deprecated
	public static long setEnergy(ItemStack stack, long energy, EnumFacing from) {
		return setEnergy(stack, energy, EnergyUnits.MINECRAFT_JOULES, from);
	}

	/**
	 * Set the energy inside of the given {@link TileEntity}
	 * 
	 * @param te
	 *            The {@link TileEntity} which holds energy
	 * @param energy
	 *            The energy to set in the {@link EnergyUnit} provided
	 * @param unit
	 *            The unit of the energy will be returned in
	 * @param from
	 *            The side of the {@link TileEntity} for use with
	 *            {@link Capability}
	 * @return The energy which was set
	 */
	public static long setEnergy(TileEntity te, long energy, @Nonnull EnergyUnit unit, EnumFacing from) {
		if (hasSupport(te, from)) {
			long energyStored = getEnergyStored(te, from, unit);
			if (energyStored < energy)
				return giveEnergy(te, energy - energyStored, unit, false, from);
			else if (energyStored > energy)
				return takeEnergy(te, energyStored - energy, unit, false, from);
		}
		return 0;
	}

	/**
	 * Set the energy inside of the given {@link ItemStack}
	 * 
	 * @param stack
	 *            The {@link ItemStack} which holds energy
	 * @param energy
	 *            The energy to set in the {@link EnergyUnit} provided
	 * @param unit
	 *            The unit of the energy will be returned in
	 * @param from
	 *            The side of the {@link ItemStack} for use with
	 *            {@link Capability}
	 * @return The energy which was set
	 */
	public static long setEnergy(ItemStack stack, long energy, @Nonnull EnergyUnit unit, EnumFacing from) {
		if (hasSupport(stack, from)) {
			long energyStored = getEnergyStored(stack, from, unit);
			if (energyStored < energy)
				return giveEnergy(stack, energy - energyStored, unit, false, from);
			else if (energyStored > energy)
				return takeEnergy(stack, energyStored - energy, unit, false, from);
		}
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
	public static boolean canReceive(@Nullable TileEntity te, @Nullable EnumFacing from) {
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
	public static boolean canReceive(@Nullable ItemStack stack, @Nullable EnumFacing from) {
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
	public static boolean canExtract(@Nullable TileEntity te, @Nullable EnumFacing from) {
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
	public static boolean canExtract(@Nullable ItemStack stack, @Nullable EnumFacing from) {
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
	@Deprecated
	public static long takeEnergyAllFaces(@Nonnull World world, BlockPos pos, long energy, boolean simulate) {
		return takeEnergyAllFaces(world, pos, energy, EnergyUnits.MINECRAFT_JOULES, simulate);
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
	@Deprecated
	public static long giveEnergyAllFaces(@Nonnull World world, BlockPos pos, long energy, boolean simulate) {
		return giveEnergyAllFaces(world, pos, energy, EnergyUnits.MINECRAFT_JOULES, simulate);
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
	 *            {@link EnergyUnit} provided
	 * @param unit
	 *            The unit the energy taken will be returned in
	 * @param simulate
	 *            Whether it is a simulation or not. If so, the energy won't
	 *            actually be taken
	 * @return The amount of energy taken in the {@link EnergyUnit} provided
	 */
	public static long takeEnergyAllFaces(@Nonnull World world, BlockPos pos, long energy, @Nonnull EnergyUnit unit,
			boolean simulate) {
		HashMap<EnumFacing, TileEntity> tiles = new HashMap<EnumFacing, TileEntity>();
		for (EnumFacing side : EnumFacing.VALUES) {
			TileEntity te = world.getTileEntity(pos.offset(side));
			if (te == null)
				continue;
			if (hasSupport(te, side))
				tiles.put(side, te);
		}
		if (tiles.size() <= 0)
			return 0;
		long energyPerSide = energy / tiles.size();
		Iterator<Entry<EnumFacing, TileEntity>> tilesIterator = tiles.entrySet().iterator();
		long energyTaken = 0;
		long extraEnergy = 0;
		while (tilesIterator.hasNext()) {
			Entry<EnumFacing, TileEntity> entry = tilesIterator.next();
			EnumFacing side = entry.getKey();
			TileEntity te = entry.getValue();
			long et = takeEnergy(te, energyPerSide + extraEnergy, unit, simulate, side);
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
	 *            {@link EnergyUnit} provided
	 * @param unit
	 *            The {@link EnergyUnit} the energy given will be returned in
	 * @param simulate
	 *            Whether it is a simulation or not. If so, the energy won't
	 *            actually be given
	 * @return The amount of energy given in the {@link EnergyUnit} provided
	 */
	public static long giveEnergyAllFaces(@Nonnull World world, BlockPos pos, long energy, @Nonnull EnergyUnit unit,
			boolean simulate) {
		HashMap<EnumFacing, TileEntity> tiles = new HashMap<EnumFacing, TileEntity>();
		for (EnumFacing side : EnumFacing.VALUES) {
			TileEntity te = world.getTileEntity(pos.offset(side));
			if (te == null)
				continue;
			if (hasSupport(te, side))
				tiles.put(side, te);
		}
		if (tiles.size() <= 0)
			return 0;
		long energyPerSide = energy / tiles.size();
		Iterator<Entry<EnumFacing, TileEntity>> tilesIterator = tiles.entrySet().iterator();
		long energyGiven = 0;
		long extraEnergy = 0;
		while (tilesIterator.hasNext()) {
			Entry<EnumFacing, TileEntity> entry = tilesIterator.next();
			EnumFacing side = entry.getKey();
			TileEntity te = entry.getValue();
			long eg = giveEnergy(te, energyPerSide + extraEnergy, unit, simulate, side);
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
	@Nullable
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
	@Deprecated
	public static void syncEnergy(BlockPos pos, @Nullable EnumFacing side, String modid) {
		PacketHandler.INSTANCE.sendToServer(new PacketGetEnergy(EnergyUnits.MINECRAFT_JOULES, pos, side, false, modid,
				new Exception().getStackTrace()[1].getClassName()));
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
	@Deprecated
	public static void syncEnergy(BlockPos pos, @Nullable EnumFacing side, String modid, String className) {
		PacketHandler.INSTANCE
				.sendToServer(new PacketGetEnergy(EnergyUnits.MINECRAFT_JOULES, pos, side, false, modid, className));
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
	@Deprecated
	public static void syncCapacity(BlockPos pos, @Nullable EnumFacing side, String modid) {
		PacketHandler.INSTANCE.sendToServer(new PacketGetCapacity(EnergyUnits.MINECRAFT_JOULES, pos, side, false, modid,
				new Exception().getStackTrace()[1].getClassName()));
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
	@Deprecated
	public static void syncCapacity(BlockPos pos, @Nullable EnumFacing side, String modid, String className) {
		PacketHandler.INSTANCE
				.sendToServer(new PacketGetCapacity(EnergyUnits.MINECRAFT_JOULES, pos, side, false, modid, className));
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
	@Deprecated
	public static void syncEnergyData(BlockPos pos, @Nullable EnumFacing side, String modid) {
		PacketHandler.INSTANCE.sendToServer(new PacketGetEnergyData(EnergyUnits.MINECRAFT_JOULES, pos, side, false,
				modid, new Exception().getStackTrace()[1].getClassName()));
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
	@Deprecated
	public static void syncEnergyData(BlockPos pos, @Nullable EnumFacing side, String modid, String className) {
		PacketHandler.INSTANCE.sendToServer(
				new PacketGetEnergyData(EnergyUnits.MINECRAFT_JOULES, pos, side, false, modid, className));
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
	@Deprecated
	public static void syncEnergyField(BlockPos pos, @Nullable EnumFacing side, String energyFieldName) {
		PacketHandler.INSTANCE.sendToServer(new PacketGetEnergy(EnergyUnits.MINECRAFT_JOULES, pos, side, true,
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
	@Deprecated
	public static void syncEnergyField(BlockPos pos, @Nullable EnumFacing side, String className,
			String energyFieldName) {
		PacketHandler.INSTANCE.sendToServer(
				new PacketGetEnergy(EnergyUnits.MINECRAFT_JOULES, pos, side, true, className, energyFieldName));
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
	@Deprecated
	public static void syncCapacityField(BlockPos pos, @Nullable EnumFacing side, String capacityFieldName) {
		PacketHandler.INSTANCE.sendToServer(new PacketGetCapacity(EnergyUnits.MINECRAFT_JOULES, pos, side, true,
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
	@Deprecated
	public static void syncCapacityField(BlockPos pos, @Nullable EnumFacing side, String className,
			String capacityFieldName) {
		PacketHandler.INSTANCE.sendToServer(
				new PacketGetCapacity(EnergyUnits.MINECRAFT_JOULES, pos, side, true, className, capacityFieldName));
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
	@Deprecated
	public static void syncEnergyDataFields(BlockPos pos, @Nullable EnumFacing side, String energyFieldName,
			String capacityFieldName) {
		PacketHandler.INSTANCE.sendToServer(new PacketGetEnergyData(EnergyUnits.MINECRAFT_JOULES, pos, side, true,
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
	@Deprecated
	public static void syncEnergyDataFields(BlockPos pos, @Nullable EnumFacing side, String className,
			String energyFieldName, String capacityFieldName) {
		PacketHandler.INSTANCE.sendToServer(new PacketGetEnergyData(EnergyUnits.MINECRAFT_JOULES, pos, side, true,
				className, energyFieldName, capacityFieldName));
	}

	/**
	 * Sync energy with the server. To get the data use
	 * {@link #getCachedEnergyData(String)} or
	 * {@link #getCachedEnergyData(String, String)} and then use
	 * {@link EnergyData#getEnergy()}. This will store the data in the calling
	 * class in the cache
	 * 
	 * @param unit
	 *            The {@link EnergyUnit} in which the energy will be returned in
	 * @param pos
	 *            The position of the {@link TileEntity}
	 * @param side
	 *            The side of the {@link TileEntity} for use with
	 *            {@link Capability}
	 * @param modid
	 *            The modid for mod specific data
	 */
	public static void syncEnergy(@Nonnull EnergyUnit unit, BlockPos pos, @Nullable EnumFacing side, String modid) {
		PacketHandler.INSTANCE.sendToServer(
				new PacketGetEnergy(unit, pos, side, false, modid, new Exception().getStackTrace()[1].getClassName()));
	}

	/**
	 * Sync energy with the server. To get the data use
	 * {@link #getCachedEnergyData(String)} or
	 * {@link #getCachedEnergyData(String, String)} and then use
	 * {@link EnergyData#getEnergy()}. This will store the data in the class
	 * path provided in the cache
	 * 
	 * @param unit
	 *            The {@link EnergyUnit} in which the energy will be returned in
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
	public static void syncEnergy(@Nonnull EnergyUnit unit, BlockPos pos, @Nullable EnumFacing side, String modid,
			String className) {
		PacketHandler.INSTANCE.sendToServer(new PacketGetEnergy(unit, pos, side, false, modid, className));
	}

	/**
	 * Sync capacity with the server. To get the data use
	 * {@link #getCachedEnergyData(String)} or
	 * {@link #getCachedEnergyData(String, String)} and then use
	 * {@link EnergyData#getCapacity()}. This will store the data in the calling
	 * class in the cache
	 * 
	 * @param unit
	 *            The {@link EnergyUnit} in which the capacity will be returned
	 *            in
	 * @param pos
	 *            The position of the {@link TileEntity}
	 * @param side
	 *            The side of the {@link TileEntity} for use with
	 *            {@link Capability}
	 * @param modid
	 *            The modid for mod specific data
	 */
	public static void syncCapacity(@Nonnull EnergyUnit unit, BlockPos pos, @Nullable EnumFacing side, String modid) {
		PacketHandler.INSTANCE.sendToServer(new PacketGetCapacity(unit, pos, side, false, modid,
				new Exception().getStackTrace()[1].getClassName()));
	}

	/**
	 * Sync capacity with the server. To get the data use
	 * {@link #getCachedEnergyData(String)} or
	 * {@link #getCachedEnergyData(String, String)} and then use
	 * {@link EnergyData#getEnergy()}. This will store the data in the class
	 * path provided in the cache
	 * 
	 * @param unit
	 *            The {@link EnergyUnit} in which the capacity will be returned
	 *            in
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
	public static void syncCapacity(@Nonnull EnergyUnit unit, BlockPos pos, @Nullable EnumFacing side, String modid,
			String className) {
		PacketHandler.INSTANCE.sendToServer(new PacketGetCapacity(unit, pos, side, false, modid, className));
	}

	/**
	 * Sync {@link EnergyData} with the server. To get the data use
	 * {@link #getCachedEnergyData(String)} or
	 * {@link #getCachedEnergyData(String, String)}. This will store the data in
	 * the calling class in the cache
	 * 
	 * @param unit
	 *            The {@link EnergyUnit} in which the energy data will be
	 *            returned in
	 * @param pos
	 *            The position of the {@link TileEntity}
	 * @param side
	 *            The side of the {@link TileEntity} for use with
	 *            {@link Capability}
	 * @param modid
	 *            The modid for mod specific data
	 */
	public static void syncEnergyData(@Nonnull EnergyUnit unit, BlockPos pos, @Nullable EnumFacing side, String modid) {
		PacketHandler.INSTANCE.sendToServer(new PacketGetEnergyData(unit, pos, side, false, modid,
				new Exception().getStackTrace()[1].getClassName()));
	}

	/**
	 * Sync {@link EnergyData} with the server. To get the data use
	 * {@link #getCachedEnergyData(String)} or
	 * {@link #getCachedEnergyData(String, String)} and then use
	 * {@link EnergyData#getEnergy()}. This will store the data in the class
	 * path provided in the cache
	 * 
	 * @param unit
	 *            The {@link EnergyUnit} in which the energy data will be
	 *            returned in
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
	public static void syncEnergyData(@Nonnull EnergyUnit unit, BlockPos pos, @Nullable EnumFacing side, String modid,
			String className) {
		PacketHandler.INSTANCE.sendToServer(new PacketGetEnergyData(unit, pos, side, false, modid, className));
	}

	/**
	 * Sync energy using a {@link Field} in the calling class. This will set the
	 * fields value to the energy of the {@link TileEntity} at the given
	 * {@link BlockPos}
	 * 
	 * @param unit
	 *            The {@link EnergyUnit} in which the energy will be returned in
	 * @param pos
	 *            The position of the {@link TileEntity}
	 * @param side
	 *            The side of the {@link TileEntity} for use with
	 *            {@link Capability}
	 * @param energyFieldName
	 *            The name of the field which will hold the energy. Must be a
	 *            <code>long</code>
	 */
	public static void syncEnergyField(@Nonnull EnergyUnit unit, BlockPos pos, @Nullable EnumFacing side,
			String energyFieldName) {
		PacketHandler.INSTANCE.sendToServer(new PacketGetEnergy(unit, pos, side, true,
				new Exception().getStackTrace()[1].getClassName(), energyFieldName));
	}

	/**
	 * Sync energy using a {@link Field} in the class provided. This will set
	 * the fields value to the energy of the {@link TileEntity} at the given
	 * {@link BlockPos}
	 * 
	 * @param unit
	 *            The {@link EnergyUnit} in which the energy will be returned in
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
	public static void syncEnergyField(@Nonnull EnergyUnit unit, BlockPos pos, @Nullable EnumFacing side,
			String className, String energyFieldName) {
		PacketHandler.INSTANCE.sendToServer(new PacketGetEnergy(unit, pos, side, true, className, energyFieldName));
	}

	/**
	 * Sync capacity using a {@link Field} in the calling class. This will set
	 * the fields value to the capacity of the {@link TileEntity} at the given
	 * {@link BlockPos}
	 * 
	 * @param unit
	 *            The {@link EnergyUnit} in which the capacity will be returned
	 *            in
	 * @param pos
	 *            The position of the {@link TileEntity}
	 * @param side
	 *            The side of the {@link TileEntity} for use with
	 *            {@link Capability}
	 * @param capacityFieldName
	 *            The name of the field which will hold the capacity. Must be a
	 *            <code>long</code>
	 */
	public static void syncCapacityField(@Nonnull EnergyUnit unit, BlockPos pos, @Nullable EnumFacing side,
			String capacityFieldName) {
		PacketHandler.INSTANCE.sendToServer(new PacketGetCapacity(unit, pos, side, true,
				new Exception().getStackTrace()[1].getClassName(), capacityFieldName));
	}

	/**
	 * Sync capacity using a {@link Field} in the class provided. This will set
	 * the fields value to the capacity of the {@link TileEntity} at the given
	 * {@link BlockPos}
	 * 
	 * @param unit
	 *            The {@link EnergyUnit} in which the capacity will be returned
	 *            in
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
	public static void syncCapacityField(@Nonnull EnergyUnit unit, BlockPos pos, @Nullable EnumFacing side,
			String className, String capacityFieldName) {
		PacketHandler.INSTANCE.sendToServer(new PacketGetCapacity(unit, pos, side, true, className, capacityFieldName));
	}

	/**
	 * Sync energy and capacity using {@link Field}s in the calling class. This
	 * will set the fields values to the energy and capacity (each respectively)
	 * of the {@link TileEntity} at the given {@link BlockPos}
	 * 
	 * @param unit
	 *            The {@link EnergyUnit} in which the energy data will be
	 *            returned in
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
	public static void syncEnergyDataFields(@Nonnull EnergyUnit unit, BlockPos pos, @Nullable EnumFacing side,
			String energyFieldName, String capacityFieldName) {
		PacketHandler.INSTANCE.sendToServer(new PacketGetEnergyData(unit, pos, side, true,
				new Exception().getStackTrace()[1].getClassName(), energyFieldName, capacityFieldName));
	}

	/**
	 * Sync energy and capacity using {@link Field}s in the class provided. This
	 * will set the fields values to the energy and capacity (each respectively)
	 * of the {@link TileEntity} at the given {@link BlockPos}
	 * 
	 * @param unit
	 *            The {@link EnergyUnit} in which the energy data will be
	 *            returned in
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
	public static void syncEnergyDataFields(@Nonnull EnergyUnit unit, BlockPos pos, @Nullable EnumFacing side,
			String className, String energyFieldName, String capacityFieldName) {
		PacketHandler.INSTANCE.sendToServer(
				new PacketGetEnergyData(unit, pos, side, true, className, energyFieldName, capacityFieldName));
	}

	/**
	 * Gets the durability for use with a durability bar for the given
	 * {@link ItemStack}
	 * 
	 * @param stack
	 *            The stack which holds energy
	 * @return The durability
	 */
	public static double getEnergyDurabilityForDisplay(ItemStack stack) {
		if (hasSupport(stack, null)) {
			double capacity = getCapacity(stack, null, CJCoreConfig.DEFAULT_ENERGY_UNIT);
			double energyDiff = capacity - getEnergyStored(stack, null, CJCoreConfig.DEFAULT_ENERGY_UNIT);
			return energyDiff / capacity;
		}
		return 0;
	}

	/**
	 * Get the RGB colour of the durability bar that the given {@link ItemStack}
	 * will use
	 * 
	 * @param stack
	 *            The {@link ItemStack} which holds energy
	 * @return The RGB colour of the durability bar
	 */
	public static int getEnergyRGBDurabilityForDisplay(ItemStack stack) {
		float[] colour = CJCoreConfig.DEFAULT_ENERGY_UNIT.getColour();
		return MathHelper.rgb(colour[0], colour[1], colour[2]);
	}

	/**
	 * Add the energy information to the items tooltip
	 * 
	 * @param stack
	 *            The {@link ItemStack} holding the energy
	 * @param tooltip
	 *            The current tooltip. The list to add the information to
	 */
	public static void addEnergyInformation(ItemStack stack, List<String> tooltip) {
		if (hasSupport(stack, null)) {
			if (CJCoreConfig.ENERGY_BAR_SIMPLIFY_ENERGY) {
				tooltip.add(getEnergyAsString(getEnergyStored(stack, null, CJCoreConfig.DEFAULT_ENERGY_UNIT),
						CJCoreConfig.DEFAULT_ENERGY_UNIT)
						+ (CJCoreConfig.ENERGY_BAR_SHOW_CAPACITY
								? " / " + getEnergyAsString(getCapacity(stack, null, CJCoreConfig.DEFAULT_ENERGY_UNIT),
										CJCoreConfig.DEFAULT_ENERGY_UNIT)
								: ""));
			} else {
				tooltip.add(
						NumberFormat.getInstance()
								.format(getEnergyStored(stack, null,
										CJCoreConfig.DEFAULT_ENERGY_UNIT))
								+ " " + CJCoreConfig.DEFAULT_ENERGY_UNIT.getSuffix()
								+ (CJCoreConfig.ENERGY_BAR_SHOW_CAPACITY ? " / "
										+ NumberFormat.getInstance()
												.format(getCapacity(stack, null, CJCoreConfig.DEFAULT_ENERGY_UNIT))
										+ " " + CJCoreConfig.DEFAULT_ENERGY_UNIT.getSuffix() : ""));
			}
		}
	}

}
