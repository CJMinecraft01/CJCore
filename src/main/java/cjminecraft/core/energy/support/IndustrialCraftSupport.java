package cjminecraft.core.energy.support;

import java.lang.reflect.Field;

import cjminecraft.core.CJCore;
import cjminecraft.core.energy.EnergyUnits;
import cjminecraft.core.energy.EnergyUnits.EnergyUnit;
import ic2.api.energy.EnergyNet;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Handles all of the support for Industrial Craft 2 Energy
 * 
 * @author CJMinecraft
 *
 */
public class IndustrialCraftSupport {

	/**
	 * The handler for any Industrial Craft Energy Storage
	 * 
	 * @author CJMinecraft
	 *
	 */
	public static class IndustrialCraftHolderSupport implements IEnergySupport<Object> {

		@Override
		public long getEnergyStored(Object container, EnumFacing from) {
			if(container instanceof ItemStack) {
				ItemStack stack = (ItemStack) container;
				return (long) ElectricItem.manager.getCharge(stack);
			}
			try {
				return (long) (double) container.getClass().getMethod("getEnergy").invoke(container);
			} catch (Exception e) {
				return 0;
			}
		}

		@Override
		public long getCapacity(Object container, EnumFacing from) {
			if(container instanceof ItemStack) {
				ItemStack stack = (ItemStack) container;
				return (long) ElectricItem.manager.getMaxCharge(stack);
			}
			try {
				return (long) (double) container.getClass().getMethod("getCapacity").invoke(container);
			} catch (Exception e) {
				return 0;
			}
		}

		@Override
		public long giveEnergy(Object container, long energy, boolean simulate, EnumFacing from) {
			return 0;
		}

		@Override
		public long takeEnergy(Object container, long energy, boolean simulate, EnumFacing from) {
			return 0;
		}

		@Override
		public boolean canReceive(Object container, EnumFacing from) {
			return false;
		}

		@Override
		public boolean canExtract(Object container, EnumFacing from) {
			return false;
		}

		@Override
		public Object getContainer(TileEntity te, EnumFacing from) {
			try {
				Field energyField = te.getClass().getSuperclass().getDeclaredField("energy");
				energyField.setAccessible(true);
				return energyField.get(te);
			} catch (Exception e) {
				return null;
			}
		}

		@Override
		public Object getContainer(ItemStack stack, EnumFacing from) {
			return stack;
		}

		@Override
		public boolean hasSupport(TileEntity te, EnumFacing from) {
			if (FMLCommonHandler.instance().getSide() == Side.SERVER)
				return EnergyNet.instance.getTile(te.getWorld(), te.getPos()) != null;
			try {
				return te.getClass().getSuperclass().getDeclaredField("energy") != null;
			} catch (Exception e) {
				return false;
			}
		}

		@Override
		public boolean hasSupport(ItemStack stack, EnumFacing from) {
			return stack.getItem() instanceof IElectricItem;
		}

		@Override
		public EnergyUnit defaultEnergyUnit() {
			return EnergyUnits.ENERGY_UNIT;
		}

	}

	/**
	 * The handler for any Industrial Craft Energy Sink - aka anything that will
	 * consume energy
	 * 
	 * @author CJMinecraft
	 *
	 */
	public static class IndustrialCraftSinkSupport implements IEnergySupport<Object> {

		@Override
		public long getEnergyStored(Object container, EnumFacing from) {
			return 0;
		}

		@Override
		public long getCapacity(Object container, EnumFacing from) {
			return 0;
		}

		@Override
		public long giveEnergy(Object container, long energy, boolean simulate, EnumFacing from) {
			if(container instanceof ItemStack) {
				ItemStack stack = (ItemStack) container;
				IElectricItem item = (IElectricItem) stack.getItem();
				return (long) ElectricItem.manager.charge(stack, energy, item.getTier(stack), true, simulate);
			}
			try {
				double storedBefore = (double) container.getClass().getMethod("getEnergy").invoke(container);
				container.getClass().getMethod("addEnergy", double.class).invoke(container,
						(double) energy);
				double storedAfter = (double) container.getClass().getMethod("getEnergy").invoke(container);
				return (long)Math.abs(storedBefore - storedAfter);
			} catch (Exception e) {
				return 0;
			}
		}

		@Override
		public long takeEnergy(Object container, long energy, boolean simulate, EnumFacing from) {
			return 0;
		}

		@Override
		public boolean canReceive(Object container, EnumFacing from) {
			return true;
		}

		@Override
		public boolean canExtract(Object container, EnumFacing from) {
			return false;
		}

		@Override
		public Object getContainer(TileEntity te, EnumFacing from) {
			try {
				Field energyField = te.getClass().getSuperclass().getDeclaredField("energy");
				energyField.setAccessible(true);
				return energyField.get(te);
			} catch (Exception e) {
				return null;
			}
		}

		@Override
		public Object getContainer(ItemStack stack, EnumFacing from) {
			return stack;
		}

		@Override
		public boolean hasSupport(TileEntity te, EnumFacing from) {
			if (FMLCommonHandler.instance().getSide() == Side.SERVER)
				return EnergyNet.instance.getTile(te.getWorld(), te.getPos()) != null;
			try {
				return te.getClass().getSuperclass().getDeclaredField("energy") != null;
			} catch (Exception e) {
				return false;
			}
		}

		@Override
		public boolean hasSupport(ItemStack stack, EnumFacing from) {
			return stack.getItem() instanceof IElectricItem;
		}

		@Override
		public EnergyUnit defaultEnergyUnit() {
			return EnergyUnits.ENERGY_UNIT;
		}

	}

	/**
	 * The handler for any Industrial Craft Energy Source - aka anything which
	 * produces energy
	 * 
	 * @author CJMinecraft
	 *
	 */
	public static class IndustrialCraftSourceSupport implements IEnergySupport<Object> {

		@Override
		public long getEnergyStored(Object container, EnumFacing from) {
			return 0;
		}

		@Override
		public long getCapacity(Object container, EnumFacing from) {
			return 0;
		}

		@Override
		public long giveEnergy(Object container, long energy, boolean simulate, EnumFacing from) {
			return 0;
		}

		@Override
		public long takeEnergy(Object container, long energy, boolean simulate, EnumFacing from) {
			if(container instanceof ItemStack) {
				ItemStack stack = (ItemStack) container;
				IElectricItem item = (IElectricItem) stack.getItem();
				return (long) ElectricItem.manager.discharge(stack, energy, item.getTier(stack), true, false, simulate);
			}
			try {
				double storedBefore = (double) container.getClass().getMethod("getEnergy").invoke(container);
				container.getClass().getMethod("addEnergy", double.class).invoke(container,
						(double) -energy);
				double storedAfter = (double) container.getClass().getMethod("getEnergy").invoke(container);
				return (long)Math.abs(storedBefore - storedAfter);
			} catch (Exception e) {
				CJCore.logger.catching(e);
				return 0;
			}
		}

		@Override
		public boolean canReceive(Object container, EnumFacing from) {
			return false;
		}

		@Override
		public boolean canExtract(Object container, EnumFacing from) {
			return true;
		}

		@Override
		public Object getContainer(TileEntity te, EnumFacing from) {
			try {
				Field energyField = te.getClass().getSuperclass().getDeclaredField("energy");
				energyField.setAccessible(true);
				return energyField.get(te);
			} catch (Exception e) {
				return null;
			}
		}

		@Override
		public Object getContainer(ItemStack stack, EnumFacing from) {
			return stack;
		}

		@Override
		public boolean hasSupport(TileEntity te, EnumFacing from) {
			if (FMLCommonHandler.instance().getSide() == Side.SERVER)
				return EnergyNet.instance.getTile(te.getWorld(), te.getPos()) != null;
			try {
				return te.getClass().getSuperclass().getDeclaredField("energy") != null;
			} catch (Exception e) {
				return false;
			}
		}

		@Override
		public boolean hasSupport(ItemStack stack, EnumFacing from) {
			return stack.getItem() instanceof IElectricItem;
		}

		@Override
		public EnergyUnit defaultEnergyUnit() {
			return EnergyUnits.ENERGY_UNIT;
		}

	}

}
