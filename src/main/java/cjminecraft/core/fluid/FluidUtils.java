package cjminecraft.core.fluid;

import java.text.NumberFormat;
import java.util.HashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import cjminecraft.core.CJCore;
import cjminecraft.core.config.CJCoreConfig;
import cjminecraft.core.network.PacketHandler;
import cjminecraft.core.network.fluid.*;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.*;

/**
 * Utility class for fluids
 * 
 * @author CJMinecraft
 *
 */
public class FluidUtils {

	private static HashMap<String, HashMap<String, FluidTankInfo>> cachedFluidData = new HashMap<String, HashMap<String, FluidTankInfo>>();

	/**
	 * States whether the {@link TileEntity} has fluid support
	 * 
	 * @param te
	 *            The {@link TileEntity} to test
	 * @param from
	 *            The side of the {@link TileEntity} for use with
	 *            {@link Capability}
	 * @return Whether the {@link TileEntity} has fluid support
	 */
	public static boolean hasSupport(@Nullable TileEntity te, @Nullable EnumFacing from) {
		return te == null ? false : te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, from);
	}

	/**
	 * States whether the {@link ItemStack} has fluid support
	 * 
	 * @param stack
	 *            The {@link ItemStack} to test
	 * @param from
	 *            The side of the {@link ItemStack} for use with
	 *            {@link Capability}
	 * @return Whether the {@link ItemStack} has fluid support
	 */
	public static boolean hasSupport(@Nullable ItemStack stack, @Nullable EnumFacing from) {
		return stack == null || stack.isEmpty() ? false
				: stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, from);
	}

	/**
	 * Shortens and simplifies the amount of any fluid and its name into a
	 * string
	 * 
	 * @param amount
	 *            The amount of the fluid (got from {@link FluidStack#amount}
	 * @param name
	 *            The name of the fluid (got from
	 *            {@link FluidStack#getLocalizedName()}
	 * @return The simplified version of the fluid
	 */
	public static String getFluidAsString(int amount, String name) {
		if (amount < 1000)
			return amount + " mb " + name;
		int exp = (int) (Math.log(amount) / Math.log(1000));
		char prefix = "KMGTPE".charAt(exp - 1);
		return String.format("%.1f %s" + " b " + name, amount / Math.pow(1000, exp), prefix);
	}

	/**
	 * Gets the information inside of the {@link FluidTankInfo} and puts it into
	 * a nice string using the config file for formatting
	 * 
	 * @param fluidTankInfo
	 *            The holder of the information on the fluids
	 * @return The information inside of the {@link FluidTankInfo} inside a nice
	 *         string
	 */
	public static String getFluidTankInfoToString(@Nullable FluidTankInfo fluidTankInfo) {
		if (fluidTankInfo == null || fluidTankInfo.fluid == null)
			return I18n.format("fluid.empty");
		if (CJCoreConfig.FLUID_BAR_SIMPLIFY_FLUIDS)
			return getFluidAsString(fluidTankInfo.fluid.amount, (CJCoreConfig.ENERGY_BAR_SHOW_CAPACITY ? ""
					: fluidTankInfo.fluid.getLocalizedName())
					+ (CJCoreConfig.ENERGY_BAR_SHOW_CAPACITY
							? " / " + getFluidAsString(fluidTankInfo.capacity, fluidTankInfo.fluid.getLocalizedName())
							: ""));
		else
			return NumberFormat.getInstance().format(fluidTankInfo.fluid.amount) + " mb "
					+ (CJCoreConfig.FLUID_BAR_SHOW_CAPACITY
							? " / " + NumberFormat.getInstance().format(fluidTankInfo.capacity) + " mb "
									+ fluidTankInfo.fluid.getLocalizedName()
							: fluidTankInfo.fluid.getLocalizedName());
	}

	/**
	 * Gets the number of tanks inside of a {@link TileEntity}
	 * 
	 * @param te
	 *            The {@link TileEntity} which holds the tanks
	 * @param from
	 *            The side of the {@link TileEntity} for use with
	 *            {@link Capability}
	 * @return the number of tanks inside of the {@link TileEntity}
	 */
	public static int getNumberOfTanks(@Nullable TileEntity te, @Nullable EnumFacing from) {
		if (te == null)
			return 0;
		return te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, from).getTankProperties().length;
	}

	/**
	 * Gets the number of tanks inside of an {@link ItemStack}
	 * 
	 * @param stack
	 *            The {@link ItemStack} which holds the tanks
	 * @param from
	 *            The side of the {@link ItemStack} for use with
	 *            {@link Capability}
	 * @return the number of tanks inside of the {@link ItemStack}
	 */
	public static int getNumberOfTanks(@Nullable ItemStack stack, @Nullable EnumFacing from) {
		if (stack == null || stack.isEmpty())
			return 0;
		return stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, from)
				.getTankProperties().length;
	}

	/**
	 * Gets the amount of fluid inside fluid tank of the given index inside of
	 * the given {@link TileEntity}
	 * 
	 * @param te
	 *            The {@link TileEntity} which holds the tank
	 * @param from
	 *            The side of the {@link TileEntity} for use with
	 *            {@link Capability}
	 * @param tankIndex
	 *            The index of the tank (if they hold more than one tank)
	 * @return the amount of fluid inside of the fluid tank specified
	 */
	public static int getFluidAmount(@Nullable TileEntity te, @Nullable EnumFacing from, int tankIndex) {
		if (te == null)
			return 0;
		IFluidHandler handler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, from);
		if (tankIndex >= handler.getTankProperties().length)
			return 0;
		return handler.getTankProperties()[tankIndex].getContents() != null
				? handler.getTankProperties()[tankIndex].getContents().amount : 0;
	}

	/**
	 * Gets the amount of fluid inside fluid tank of the given index inside of
	 * the given {@link ItemStack}
	 * 
	 * @param stack
	 *            The {@link ItemStack} which holds the tank
	 * @param from
	 *            The side of the {@link ItemStack} for use with
	 *            {@link Capability}
	 * @param tankIndex
	 *            The index of the tank (if they hold more than one tank)
	 * @return the amount of fluid inside of the fluid tank specified
	 */
	public static int getFluidAmount(@Nullable ItemStack stack, @Nullable EnumFacing from, int tankIndex) {
		if (stack == null || stack.isEmpty())
			return 0;
		IFluidHandler handler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, from);
		if (tankIndex >= handler.getTankProperties().length)
			return 0;
		return handler.getTankProperties()[tankIndex].getContents() != null
				? handler.getTankProperties()[tankIndex].getContents().amount : 0;
	}

	/**
	 * Gets the {@link FluidStack} containing the fluid and amount from the
	 * given {@link TileEntity}
	 * 
	 * @param te
	 *            The {@link TileEntity} which holds the tank
	 * @param from
	 *            The side of the {@link TileEntity} for use with
	 *            {@link Capability}
	 * @param tankIndex
	 *            The index of the tank (if they hold more than one tank)
	 * @return the {@link FluidStack} containing the fluid and amount
	 */
	@Nullable
	public static FluidStack getFluidStack(@Nullable TileEntity te, @Nullable EnumFacing from, int tankIndex) {
		if (te == null)
			return null;
		IFluidHandler handler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, from);
		if (tankIndex >= handler.getTankProperties().length)
			return null;
		return handler.getTankProperties()[tankIndex].getContents();
	}

	/**
	 * Gets the {@link FluidStack} containing the fluid and amount from the
	 * given {@link ItemStack}
	 * 
	 * @param stack
	 *            The {@link ItemStack} which holds the tank
	 * @param from
	 *            The side of the {@link ItemStack} for use with
	 *            {@link Capability}
	 * @param tankIndex
	 *            The index of the tank (if they hold more than one tank)
	 * @return the {@link FluidStack} containing the fluid and amount
	 */
	@Nullable
	public static FluidStack getFluidStack(@Nullable ItemStack stack, @Nullable EnumFacing from, int tankIndex) {
		if (stack == null || stack.isEmpty())
			return null;
		IFluidHandler handler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, from);
		if (tankIndex >= handler.getTankProperties().length)
			return null;
		return handler.getTankProperties()[tankIndex].getContents();
	}

	/**
	 * Gets the capacity of the fluid tank specified in the given
	 * {@link TileEntity}
	 * 
	 * @param te
	 *            The {@link TileEntity} which holds the tank
	 * @param from
	 *            The side of the {@link TileEntity} for use with
	 *            {@link Capability}
	 * @param tankIndex
	 *            The index of the tank (if they hold more than one tank)
	 * @return the capacity of the fluid tank specified
	 */
	public static int getCapacity(@Nullable TileEntity te, @Nullable EnumFacing from, int tankIndex) {
		if (te == null)
			return 0;
		IFluidHandler handler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, from);
		if (tankIndex >= handler.getTankProperties().length)
			return 0;
		return handler.getTankProperties()[tankIndex].getCapacity();
	}

	/**
	 * Gets the capacity of the fluid tank specified in the given
	 * {@link ItemStack}
	 * 
	 * @param stack
	 *            The {@link ItemStack} which holds the tank
	 * @param from
	 *            The side of the {@link ItemStack} for use with
	 *            {@link Capability}
	 * @param tankIndex
	 *            The index of the tank (if they hold more than one tank)
	 * @return the capacity of the fluid tank specified
	 */
	public static int getCapacity(@Nullable ItemStack stack, @Nullable EnumFacing from, int tankIndex) {
		if (stack == null || stack.isEmpty())
			return 0;
		IFluidHandler handler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, from);
		if (tankIndex >= handler.getTankProperties().length)
			return 0;
		return handler.getTankProperties()[tankIndex].getCapacity();
	}

	/**
	 * Gets the fluid inside of the tank specified in the given
	 * {@link TileEntity}. If there is no fluid inside the tank or the specified
	 * tank is invalid, {@link FluidRegistry#WATER} will be returned
	 * 
	 * @param te
	 *            The {@link TileEntity} which holds the tank
	 * @param from
	 *            The side of the {@link TileEntity} for use with
	 *            {@link Capability}
	 * @param tankIndex
	 *            The index of the tank (if they hold more than one tank)
	 * @return the fluid inside of the tank
	 */
	public static Fluid getFluidInTank(@Nullable TileEntity te, @Nullable EnumFacing from, int tankIndex) {
		if (te == null)
			return FluidRegistry.WATER;
		IFluidHandler handler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, from);
		if (tankIndex >= handler.getTankProperties().length)
			return FluidRegistry.WATER;
		return handler.getTankProperties()[tankIndex].getContents().getFluid();
	}

	/**
	 * Gets the fluid inside of the tank specified in the given
	 * {@link ItemStack}. If there is no fluid inside the tank or the specified
	 * tank is invalid, {@link FluidRegistry#WATER} will be returned
	 * 
	 * @param stack
	 *            The {@link ItemStack} which holds the tank
	 * @param from
	 *            The side of the {@link ItemStack} for use with
	 *            {@link Capability}
	 * @param tankIndex
	 *            The index of the tank (if they hold more than one tank)
	 * @return the fluid inside of the tank
	 */
	public static Fluid getFluidInTank(@Nullable ItemStack stack, @Nullable EnumFacing from, int tankIndex) {
		if (stack == null || stack.isEmpty())
			return FluidRegistry.WATER;
		IFluidHandler handler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, from);
		if (tankIndex >= handler.getTankProperties().length)
			return FluidRegistry.WATER;
		return handler.getTankProperties()[tankIndex].getContents().getFluid();
	}

	/**
	 * States whether you can fill up the tank specified in the given
	 * {@link TileEntity}
	 * 
	 * @param te
	 *            The {@link TileEntity} which holds the tank
	 * @param from
	 *            The side of the {@link TileEntity} for use with
	 *            {@link Capability}
	 * @param tankIndex
	 *            The index of the tank (if they hold more than one tank)
	 * @return whether you can fill up the tank
	 */
	public static boolean canFill(@Nullable TileEntity te, @Nullable EnumFacing from, int tankIndex) {
		if (te == null)
			return false;
		IFluidHandler handler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, from);
		if (tankIndex >= handler.getTankProperties().length)
			return false;
		return handler.getTankProperties()[tankIndex].canFill();
	}

	/**
	 * States whether you can fill up the tank specified in the given
	 * {@link ItemStack}
	 * 
	 * @param stack
	 *            The {@link ItemStack} which holds the tank
	 * @param from
	 *            The side of the {@link ItemStack} for use with
	 *            {@link Capability}
	 * @param tankIndex
	 *            The index of the tank (if they hold more than one tank)
	 * @return whether you can fill up the tank
	 */
	public static boolean canFill(@Nullable ItemStack stack, @Nullable EnumFacing from, int tankIndex) {
		if (stack == null || stack.isEmpty())
			return false;
		IFluidHandler handler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, from);
		if (tankIndex >= handler.getTankProperties().length)
			return false;
		return handler.getTankProperties()[tankIndex].canFill();
	}

	/**
	 * States whether you can drain the tank specified in the given
	 * {@link TileEntity}
	 * 
	 * @param te
	 *            The {@link TileEntity} which holds the tank
	 * @param from
	 *            The side of the {@link TileEntity} for use with
	 *            {@link Capability}
	 * @param tankIndex
	 *            The index of the tank (if they hold more than one tank)
	 * @return whether you can drain the tank
	 */
	public static boolean canDrain(@Nullable TileEntity te, @Nullable EnumFacing from, int tankIndex) {
		if (te == null)
			return false;
		IFluidHandler handler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, from);
		if (tankIndex >= handler.getTankProperties().length)
			return false;
		return handler.getTankProperties()[tankIndex].canDrain();
	}

	/**
	 * States whether you can drain the tank specified in the given
	 * {@link ItemStack}
	 * 
	 * @param stack
	 *            The {@link ItemStack} which holds the tank
	 * @param from
	 *            The side of the {@link ItemStack} for use with
	 *            {@link Capability}
	 * @param tankIndex
	 *            The index of the tank (if they hold more than one tank)
	 * @return whether you can drain the tank
	 */
	public static boolean canDrain(@Nullable ItemStack stack, @Nullable EnumFacing from, int tankIndex) {
		if (stack == null || stack.isEmpty())
			return false;
		IFluidHandler handler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, from);
		if (tankIndex >= handler.getTankProperties().length)
			return false;
		return handler.getTankProperties()[tankIndex].canDrain();
	}

	/**
	 * States whether you can fill up the specified tank in the given
	 * {@link TileEntity} with the given {@link FluidStack}
	 * 
	 * @param te
	 *            The {@link TileEntity} which holds the tank
	 * @param from
	 *            The side of the {@link TileEntity} for use with
	 *            {@link Capability}
	 * @param tankIndex
	 *            The index of the tank (if they hold more than one tank)
	 * @param fluidStack
	 *            The {@link FluidStack} to test
	 * @return whether you can fill up the tank with the given
	 *         {@link FluidStack}
	 */
	public static boolean canFillFluidType(@Nullable TileEntity te, @Nullable EnumFacing from, int tankIndex,
			FluidStack fluidStack) {
		if (te == null)
			return false;
		IFluidHandler handler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, from);
		if (tankIndex >= handler.getTankProperties().length)
			return false;
		return handler.getTankProperties()[tankIndex].canFillFluidType(fluidStack);
	}

	/**
	 * States whether you can fill up the specified tank in the given
	 * {@link ItemStack} with the given {@link FluidStack}
	 * 
	 * @param stack
	 *            The {@link ItemStack} which holds the tank
	 * @param from
	 *            The side of the {@link ItemStack} for use with
	 *            {@link Capability}
	 * @param tankIndex
	 *            The index of the tank (if they hold more than one tank)
	 * @param fluidStack
	 *            The {@link FluidStack} to test
	 * @return whether you can fill up the tank with the given
	 *         {@link FluidStack}
	 */
	public static boolean canFillFluidType(@Nullable ItemStack stack, @Nullable EnumFacing from, int tankIndex,
			FluidStack fluidStack) {
		if (stack == null || stack.isEmpty())
			return false;
		IFluidHandler handler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, from);
		if (tankIndex >= handler.getTankProperties().length)
			return false;
		return handler.getTankProperties()[tankIndex].canFillFluidType(fluidStack);
	}

	/**
	 * States whether you can drain a specific {@link FluidStack} from the
	 * specified tank in the given {@link TileEntity}
	 * 
	 * @param te
	 *            The {@link TileEntity} which holds the tank
	 * @param from
	 *            The side of the {@link TileEntity} for use with
	 *            {@link Capability}
	 * @param tankIndex
	 *            The index of the tank (if they have more than one tank)
	 * @param fluidStack
	 *            The {@link FluidStack} to test
	 * @return whether you can drain the given {@link FluidStack} from the tank
	 */
	public static boolean canDrainFluidType(@Nullable TileEntity te, @Nullable EnumFacing from, int tankIndex,
			FluidStack fluidStack) {
		if (te == null)
			return false;
		IFluidHandler handler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, from);
		if (tankIndex >= handler.getTankProperties().length)
			return false;
		return handler.getTankProperties()[tankIndex].canDrainFluidType(fluidStack);
	}

	/**
	 * States whether you can drain a specific {@link FluidStack} from the
	 * specified tank in the given {@link ItemStack}
	 * 
	 * @param stack
	 *            The {@link ItemStack} which holds the tank
	 * @param from
	 *            The side of the {@link ItemStack} for use with
	 *            {@link Capability}
	 * @param tankIndex
	 *            The index of the tank (if they have more than one tank)
	 * @param fluidStack
	 *            The {@link FluidStack} to test
	 * @return whether you can drain the given {@link FluidStack} from the tank
	 */
	public static boolean canDrainFluidType(@Nullable ItemStack stack, @Nullable EnumFacing from, int tankIndex,
			FluidStack fluidStack) {
		if (stack == null || stack.isEmpty())
			return false;
		IFluidHandler handler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, from);
		if (tankIndex >= handler.getTankProperties().length)
			return false;
		return handler.getTankProperties()[tankIndex].canDrainFluidType(fluidStack);
	}

	/**
	 * Fill the tanks in the given {@link TileEntity} with the given
	 * {@link FluidStack}
	 * 
	 * @param te
	 *            The {@link TileEntity} which holds the tanks
	 * @param from
	 *            The side of the {@link TileEntity} for use with
	 *            {@link Capability}
	 * @param resource
	 *            The {@link FluidStack} to fill the tank with
	 * @param simulate
	 *            Whether it is a simulation (if so, no fluid will actually be
	 *            given)
	 * @return the left over (or would have been left over) fluid
	 */
	@Nullable
	public static int fill(@Nullable TileEntity te, @Nullable EnumFacing from, @Nullable FluidStack resource,
			boolean simulate) {
		if (te == null || resource == null)
			return 0;
		return te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, from).fill(resource, !simulate);
	}

	/**
	 * Fill the tanks in the given {@link ItemStack} with the given
	 * {@link FluidStack}
	 * 
	 * @param stack
	 *            The {@link ItemStack} which holds the tanks
	 * @param from
	 *            The side of the {@link ItemStack} for use with
	 *            {@link Capability}
	 * @param resource
	 *            The {@link FluidStack} to fill the tank with
	 * @param simulate
	 *            Whether it is a simulation (if so, no fluid will actually be
	 *            given)
	 * @return the left over (or would have been left over) fluid amount
	 */
	public static int fill(@Nullable ItemStack stack, @Nullable EnumFacing from, @Nullable FluidStack resource,
			boolean simulate) {
		if (stack == null || stack.isEmpty() || resource == null)
			return 0;
		return stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, from).fill(resource,
				!simulate);
	}

	/**
	 * Drain the tanks in the given {@link TileEntity} of the given
	 * {@link FluidStack}
	 * 
	 * @param te
	 *            The {@link TileEntity} which holds the tanks
	 * @param from
	 *            The side of the {@link TileEntity} for use with
	 *            {@link Capability}
	 * @param resource
	 *            The {@link FluidStack} to drain
	 * @param simulate
	 *            Whether this is a simulation (if so, no fluid will actually be
	 *            drained)
	 * @return The {@link FluidStack} which was (or would have been) drained
	 */
	@Nullable
	public static FluidStack drain(@Nullable TileEntity te, @Nullable EnumFacing from, @Nullable FluidStack resource,
			boolean simulate) {
		if (te == null || resource == null)
			return null;
		return te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, from).drain(resource, !simulate);
	}

	/**
	 * Drain the tanks in the given {@link ItemStack} of the given
	 * {@link FluidStack}
	 * 
	 * @param stack
	 *            The {@link ItemStack} which holds the tanks
	 * @param from
	 *            The side of the {@link ItemStack} for use with
	 *            {@link Capability}
	 * @param resource
	 *            The {@link FluidStack} to drain
	 * @param simulate
	 *            Whether this is a simulation (if so, no fluid will actually be
	 *            drained)
	 * @return The {@link FluidStack} which was (or would have been) drained
	 */
	@Nullable
	public static FluidStack drain(@Nullable ItemStack stack, @Nullable EnumFacing from, @Nullable FluidStack resource,
			boolean simulate) {
		if (stack == null || stack.isEmpty() || resource == null)
			return null;
		return stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, from).drain(resource,
				!simulate);
	}

	/**
	 * Drain a certain amount of fluid from the tanks inside of the given
	 * {@link TileEntity}
	 * 
	 * @param te
	 *            The {@link TileEntity} which holds the tanks
	 * @param from
	 *            The side of the {@link TileEntity} for use with
	 *            {@link Capability}
	 * @param maxDrain
	 *            The amount to drain
	 * @param simulate
	 *            Whether this is a simulation (if so, no fluid will actually be
	 *            drained)
	 * @return the {@link FluidStack} which was (or would have been) drained
	 */
	@Nullable
	public static FluidStack drain(@Nullable TileEntity te, @Nullable EnumFacing from, @Nonnull int maxDrain,
			boolean simulate) {
		if (te == null)
			return null;
		return te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, from).drain(maxDrain, !simulate);
	}

	/**
	 * Drain a certain amount of fluid from the tanks inside of the given
	 * {@link ItemStack}
	 * 
	 * @param stack
	 *            The {@link ItemStack} which holds the tanks
	 * @param from
	 *            The side of the {@link ItemStack} for use with
	 *            {@link Capability}
	 * @param maxDrain
	 *            The amount to drain
	 * @param simulate
	 *            Whether this is a simulation (if so, no fluid will actually be
	 *            drained)
	 * @return the {@link FluidStack} which was (or would have been) drained
	 */
	@Nullable
	public static FluidStack drain(@Nullable ItemStack stack, @Nullable EnumFacing from, @Nonnull int maxDrain,
			boolean simulate) {
		if (stack == null || stack.isEmpty())
			return null;
		return stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, from).drain(maxDrain,
				!simulate);
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
	public static void addCachedFluidData(String modid, String className, FluidTankInfo data) {
		if (!cachedFluidData.containsKey(modid))
			cachedFluidData.put(modid, new HashMap<String, FluidTankInfo>());
		if (!cachedFluidData.get(modid).containsKey(className))
			cachedFluidData.get(modid).put(className, data);
	}

	/**
	 * Retrieves the latest data from the calling class
	 * 
	 * @param modid
	 *            The modid to get mod specific data
	 * @return The latest data from the calling class
	 */
	public static FluidTankInfo getCachedFluidData(String modid) {
		return getCachedFluidData(modid, new Exception().getStackTrace()[1].getClassName());
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
	public static FluidTankInfo getCachedFluidData(String modid, String className) {
		if (!cachedFluidData.containsKey(modid))
			return null;
		if (!cachedFluidData.get(modid).containsKey(className))
			return null;
		FluidTankInfo data = cachedFluidData.get(modid).get(className);
		cachedFluidData.get(modid).remove(className);
		return data;
	}

	/**
	 * Sync fluid data with the server. To get the data, use
	 * {@link #getCachedFluidData(String)} or
	 * {@link #getCachedFluidData(String, String)}. This will store the data in
	 * the calling class in the cache
	 * 
	 * @param tankIndex
	 *            The index of the tank to get the information from (for blocks
	 *            which have more than one tank)
	 * @param pos
	 *            The position of the {@link TileEntity}
	 * @param from
	 *            The side of the {@link TileEntity} for use with
	 *            {@link Capability}
	 * @param modid
	 *            The modid for mod specific data
	 */
	public static void syncFluidData(int tankIndex, BlockPos pos, @Nullable EnumFacing from, String modid) {
		PacketHandler.INSTANCE.sendToServer(new PacketGetFluidData(tankIndex, pos, from, false, modid,
				new Exception().getStackTrace()[1].getClassName()));
	}

	/**
	 * Sync fluid data with the server. To get the data, use
	 * {@link #getCachedFluidData(String)} or
	 * {@link #getCachedFluidData(String, String)}. This will store the data in
	 * the calling class in the cache
	 * 
	 * @param tankIndex
	 *            The index of the tank to get the information from (for blocks
	 *            which have more than one tank)
	 * @param pos
	 *            The position of the {@link TileEntity}
	 * @param from
	 *            The side of the {@link TileEntity} for use with
	 *            {@link Capability}
	 * @param modid
	 *            The modid for mod specific data
	 * @param className
	 *            The name of the class which will be used by the cache
	 */
	public static void syncFluidData(int tankIndex, BlockPos pos, @Nullable EnumFacing from, String modid,
			String className) {
		PacketHandler.INSTANCE.sendToServer(new PacketGetFluidData(tankIndex, pos, from, false, modid, className));
	}

	/**
	 * Clears all the cached fluid data
	 */
	public static void clearCache() {
		cachedFluidData.clear();
	}

}