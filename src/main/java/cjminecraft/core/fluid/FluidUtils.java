package cjminecraft.core.fluid;

import java.text.NumberFormat;
import java.util.HashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import cjminecraft.core.network.PacketHandler;
import cjminecraft.core.network.fluid.*;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.*;

public class FluidUtils {
	
	private static HashMap<String, HashMap<String, FluidTankInfo>> cachedFluidData = new HashMap<String, HashMap<String, FluidTankInfo>>();

	public static boolean hasSupport(@Nullable TileEntity te, @Nullable EnumFacing side) {
		return te == null ? false : te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side);
	}

	public static boolean hasSupport(@Nullable ItemStack stack, @Nullable EnumFacing side) {
		return stack == null || stack.isEmpty() ? false
				: stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, side);
	}
	
	public static String getFluidTankInfoToString(FluidTankInfo fluidTankInfo) {
		return fluidTankInfo.fluid.getLocalizedName() + " " + NumberFormat.getInstance().format(fluidTankInfo.fluid.amount) + "mb";
	}

	public static int getNumberOfTanks(@Nullable TileEntity te, @Nullable EnumFacing side) {
		if(te == null)
			return 0;
		return te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side).getTankProperties().length;
	}
	
	public static int getNumberOfTanks(@Nullable ItemStack stack, @Nullable EnumFacing side) {
		if(stack == null || stack.isEmpty())
			return 0;
		return stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, side).getTankProperties().length;
	}

	public static int getFluidAmount(@Nullable TileEntity te, @Nullable EnumFacing side, int tankIndex) {
		if (te == null)
			return 0;
		IFluidHandler handler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side);
		if (tankIndex >= handler.getTankProperties().length)
			return 0;
		return handler.getTankProperties()[tankIndex].getContents() != null
				? handler.getTankProperties()[tankIndex].getContents().amount : 0;
	}
	
	public static int getFluidAmount(@Nullable ItemStack stack, @Nullable EnumFacing side, int tankIndex) {
		if (stack == null || stack.isEmpty())
			return 0;
		IFluidHandler handler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, side);
		if (tankIndex >= handler.getTankProperties().length)
			return 0;
		return handler.getTankProperties()[tankIndex].getContents() != null
				? handler.getTankProperties()[tankIndex].getContents().amount : 0;
	}
	
	@Nullable
	public static FluidStack getFluidStack(@Nullable TileEntity te, @Nullable EnumFacing side, int tankIndex) {
		if (te == null)
			return null;
		IFluidHandler handler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side);
		if (tankIndex >= handler.getTankProperties().length)
			return null;
		return handler.getTankProperties()[tankIndex].getContents();
	}
	
	@Nullable
	public static FluidStack getFluidStack(@Nullable ItemStack stack, @Nullable EnumFacing side, int tankIndex) {
		if (stack == null || stack.isEmpty())
			return null;
		IFluidHandler handler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, side);
		if (tankIndex >= handler.getTankProperties().length)
			return null;
		return handler.getTankProperties()[tankIndex].getContents();
	}

	public static int getCapacity(@Nullable TileEntity te, @Nullable EnumFacing side, int tankIndex) {
		if (te == null)
			return 0;
		IFluidHandler handler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side);
		if (tankIndex >= handler.getTankProperties().length)
			return 0;
		return handler.getTankProperties()[tankIndex].getCapacity();
	}
	
	public static int getCapacity(@Nullable ItemStack stack, @Nullable EnumFacing side, int tankIndex) {
		if (stack == null || stack.isEmpty())
			return 0;
		IFluidHandler handler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, side);
		if (tankIndex >= handler.getTankProperties().length)
			return 0;
		return handler.getTankProperties()[tankIndex].getCapacity();
	}

	public static Fluid getFluidInTank(@Nullable TileEntity te, @Nullable EnumFacing side, int tankIndex) {
		if (te == null)
			return FluidRegistry.WATER;
		IFluidHandler handler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side);
		if (tankIndex >= handler.getTankProperties().length)
			return FluidRegistry.WATER;
		return handler.getTankProperties()[tankIndex].getContents().getFluid();
	}
	
	public static Fluid getFluidInTank(@Nullable ItemStack stack, @Nullable EnumFacing side, int tankIndex) {
		if (stack == null || stack.isEmpty())
			return FluidRegistry.WATER;
		IFluidHandler handler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, side);
		if (tankIndex >= handler.getTankProperties().length)
			return FluidRegistry.WATER;
		return handler.getTankProperties()[tankIndex].getContents().getFluid();
	}

	public static boolean canFill(@Nullable TileEntity te, @Nullable EnumFacing side, int tankIndex) {
		if (te == null)
			return false;
		IFluidHandler handler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side);
		if (tankIndex >= handler.getTankProperties().length)
			return false;
		return handler.getTankProperties()[tankIndex].canFill();
	}
	
	public static boolean canFill(@Nullable ItemStack stack, @Nullable EnumFacing side, int tankIndex) {
		if (stack == null || stack.isEmpty())
			return false;
		IFluidHandler handler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, side);
		if (tankIndex >= handler.getTankProperties().length)
			return false;
		return handler.getTankProperties()[tankIndex].canFill();
	}

	public static boolean canDrain(@Nullable TileEntity te, @Nullable EnumFacing side, int tankIndex) {
		if (te == null)
			return false;
		IFluidHandler handler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side);
		if (tankIndex >= handler.getTankProperties().length)
			return false;
		return handler.getTankProperties()[tankIndex].canDrain();
	}
	
	public static boolean canDrain(@Nullable ItemStack stack, @Nullable EnumFacing side, int tankIndex) {
		if (stack == null || stack.isEmpty())
			return false;
		IFluidHandler handler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, side);
		if (tankIndex >= handler.getTankProperties().length)
			return false;
		return handler.getTankProperties()[tankIndex].canDrain();
	}

	public static boolean canFillFluidType(@Nullable TileEntity te, @Nullable EnumFacing side, int tankIndex,
			FluidStack fluidStack) {
		if (te == null)
			return false;
		IFluidHandler handler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side);
		if (tankIndex >= handler.getTankProperties().length)
			return false;
		return handler.getTankProperties()[tankIndex].canFillFluidType(fluidStack);
	}
	
	public static boolean canFillFluidType(@Nullable ItemStack stack, @Nullable EnumFacing side, int tankIndex,
			FluidStack fluidStack) {
		if (stack == null || stack.isEmpty())
			return false;
		IFluidHandler handler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, side);
		if (tankIndex >= handler.getTankProperties().length)
			return false;
		return handler.getTankProperties()[tankIndex].canFillFluidType(fluidStack);
	}

	public static boolean canDrainFluidType(@Nullable TileEntity te, @Nullable EnumFacing side, int tankIndex,
			FluidStack fluidStack) {
		if (te == null)
			return false;
		IFluidHandler handler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side);
		if (tankIndex >= handler.getTankProperties().length)
			return false;
		return handler.getTankProperties()[tankIndex].canDrainFluidType(fluidStack);
	}
	
	public static boolean canDrainFluidType(@Nullable ItemStack stack, @Nullable EnumFacing side, int tankIndex,
			FluidStack fluidStack) {
		if (stack == null || stack.isEmpty())
			return false;
		IFluidHandler handler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, side);
		if (tankIndex >= handler.getTankProperties().length)
			return false;
		return handler.getTankProperties()[tankIndex].canDrainFluidType(fluidStack);
	}

	public static int fill(@Nullable TileEntity te, @Nullable EnumFacing side, @Nonnull FluidStack resource,
			boolean simulate) {
		if (te == null)
			return 0;
		return te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side).fill(resource, simulate);
	}
	
	public static int fill(@Nullable ItemStack stack, @Nullable EnumFacing side, @Nonnull FluidStack resource,
			boolean simulate) {
		if (stack == null || stack.isEmpty())
			return 0;
		return stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side).fill(resource, simulate);
	}
	
	@Nullable
	public static FluidStack drain(@Nullable TileEntity te, @Nullable EnumFacing side, @Nonnull FluidStack resource, boolean simulate) {
		if (te == null)
			return null;
		return te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side).drain(resource, simulate);
	}
	
	@Nullable
	public static FluidStack drain(@Nullable ItemStack stack, @Nullable EnumFacing side, @Nonnull FluidStack resource, boolean simulate) {
		if (stack == null || stack.isEmpty())
			return null;
		return stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side).drain(resource, simulate);
	}
	
	@Nullable
	public static FluidStack drain(@Nullable TileEntity te, @Nullable EnumFacing side, @Nonnull int maxDrain, boolean simulate) {
		if (te == null)
			return null;
		return te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side).drain(maxDrain, simulate);
	}
	
	@Nullable
	public static FluidStack drain(@Nullable ItemStack stack, @Nullable EnumFacing side, @Nonnull int maxDrain, boolean simulate) {
		if (stack == null || stack.isEmpty())
			return null;
		return stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side).drain(maxDrain, simulate);
	}
	
	public static void addCachedFluidData(String modid, String className, FluidTankInfo data) {
		if(!cachedFluidData.containsKey(modid))
			cachedFluidData.put(modid, new HashMap<String, FluidTankInfo>());
		if(!cachedFluidData.get(modid).containsKey(className))
			cachedFluidData.get(modid).put(className, data);
	}
	
	public static FluidTankInfo getCachedFluidData(String modid) {
		return getCachedFluidData(modid, new Exception().getStackTrace()[1].getClassName());
	}
	
	public static FluidTankInfo getCachedFluidData(String modid, String className) {
		if(!cachedFluidData.containsKey(modid))
			return null;
		if(!cachedFluidData.get(modid).containsKey(className))
			return null;
		FluidTankInfo data = cachedFluidData.get(modid).get(className);
		cachedFluidData.get(modid).remove(className);
		return data;
	}
	
	public static void syncFluidData(int tankIndex, BlockPos pos, @Nullable EnumFacing side, String modid) {
		PacketHandler.INSTANCE.sendToServer(new PacketGetFluidData(tankIndex, pos, side, false, modid, new Exception().getStackTrace()[1].getClassName()));
	}
	
	public static void syncFluidData(int tankIndex, BlockPos pos, @Nullable EnumFacing side, String modid, String className) {
		PacketHandler.INSTANCE.sendToServer(new PacketGetFluidData(tankIndex, pos, side, false, modid, className));
	}
	
	public static void syncFluidStack(int tankIndex, BlockPos pos, @Nullable EnumFacing side, String modid) {
		PacketHandler.INSTANCE.sendToServer(new PacketGetFluidStack(tankIndex, pos, side, false, modid, new Exception().getStackTrace()[1].getClassName()));
	}
	
	public static void syncFluidStack(int tankIndex, BlockPos pos, @Nullable EnumFacing side, String modid, String className) {
		PacketHandler.INSTANCE.sendToServer(new PacketGetFluidStack(tankIndex, pos, side, false, modid, className));
	}

}