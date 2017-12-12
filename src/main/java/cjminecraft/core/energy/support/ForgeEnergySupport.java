package cjminecraft.core.energy.support;

import cjminecraft.core.CJCore;
import cjminecraft.core.energy.EnergyUnits;
import cjminecraft.core.energy.EnergyUnits.EnergyUnit;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

/**
 * Class which handles the Forge Energy capability system
 * 
 * @author CJMinecraft
 *
 */
public class ForgeEnergySupport {

	/**
	 * For any block which simply holds Forge Energy
	 * 
	 * @author CJMinecraft
	 *
	 */
	public static class ForgeEnergyHolder implements IEnergySupport<IEnergyStorage> {

		@Override
		public long getEnergyStored(IEnergyStorage container, EnumFacing from) {
			return container.getEnergyStored();
		}

		@Override
		public long getCapacity(IEnergyStorage container, EnumFacing from) {
			return container.getMaxEnergyStored();
		}

		@Override
		public long giveEnergy(IEnergyStorage container, long energy, boolean simulate, EnumFacing from) {
			return 0;
		}

		@Override
		public long takeEnergy(IEnergyStorage container, long energy, boolean simulate, EnumFacing from) {
			return 0;
		}

		@Override
		public boolean canReceive(IEnergyStorage container, EnumFacing from) {
			return false;
		}

		@Override
		public boolean canExtract(IEnergyStorage container, EnumFacing from) {
			return false;
		}

		@Override
		public IEnergyStorage getContainer(TileEntity te, EnumFacing from) {
			return te.getCapability(CapabilityEnergy.ENERGY, from);
		}

		@Override
		public boolean hasSupport(TileEntity te, EnumFacing from) {
			return te.hasCapability(CapabilityEnergy.ENERGY, from)
					? !getContainer(te, from).canExtract() && !getContainer(te, from).canReceive() : false;
		}

		@Override
		public EnergyUnit defaultEnergyUnit() {
			return EnergyUnits.FORGE_ENERGY;
		}

		@Override
		public IEnergyStorage getContainer(ItemStack stack, EnumFacing from) {
			return stack.getCapability(CapabilityEnergy.ENERGY, from);
		}

		@Override
		public boolean hasSupport(ItemStack stack, EnumFacing from) {
			return stack.hasCapability(CapabilityEnergy.ENERGY, from)
					? !getContainer(stack, from).canExtract() && !getContainer(stack, from).canReceive() : false;
		}

	}

	/**
	 * For any block which generates or provides Forge Energy
	 * 
	 * @author CJMinecraft
	 *
	 */
	public static class ForgeEnergyProducer implements IEnergySupport<IEnergyStorage> {

		@Override
		public long getEnergyStored(IEnergyStorage container, EnumFacing from) {
			return container.getEnergyStored();
		}

		@Override
		public long getCapacity(IEnergyStorage container, EnumFacing from) {
			return container.getMaxEnergyStored();
		}

		@Override
		public long giveEnergy(IEnergyStorage container, long energy, boolean simulate, EnumFacing from) {
			return 0;
		}

		@Override
		public long takeEnergy(IEnergyStorage container, long energy, boolean simulate, EnumFacing from) {
			return container.extractEnergy((int) energy, simulate);
		}

		@Override
		public boolean canReceive(IEnergyStorage container, EnumFacing from) {
			return false;
		}

		@Override
		public boolean canExtract(IEnergyStorage container, EnumFacing from) {
			return true;
		}

		@Override
		public IEnergyStorage getContainer(TileEntity te, EnumFacing from) {
			return te.getCapability(CapabilityEnergy.ENERGY, from);
		}

		@Override
		public boolean hasSupport(TileEntity te, EnumFacing from) {
			return te.hasCapability(CapabilityEnergy.ENERGY, from) ? getContainer(te, from).canExtract() : false;
		}

		@Override
		public EnergyUnit defaultEnergyUnit() {
			return EnergyUnits.FORGE_ENERGY;
		}

		@Override
		public IEnergyStorage getContainer(ItemStack stack, EnumFacing from) {
			return stack.getCapability(CapabilityEnergy.ENERGY, from);
		}

		@Override
		public boolean hasSupport(ItemStack stack, EnumFacing from) {
			return stack.hasCapability(CapabilityEnergy.ENERGY, from) ? getContainer(stack, from).canExtract() : false;
		}

	}

	/**
	 * For any block which consumes Forge Energy
	 * 
	 * @author CJMinecraft
	 *
	 */
	public static class ForgeEnergyConsumer implements IEnergySupport<IEnergyStorage> {

		@Override
		public long getEnergyStored(IEnergyStorage container, EnumFacing from) {
			return container.getEnergyStored();
		}

		@Override
		public long getCapacity(IEnergyStorage container, EnumFacing from) {
			return container.getMaxEnergyStored();
		}

		@Override
		public long giveEnergy(IEnergyStorage container, long energy, boolean simulate, EnumFacing from) {
			return container.receiveEnergy((int) energy, simulate);
		}

		@Override
		public long takeEnergy(IEnergyStorage container, long energy, boolean simulate, EnumFacing from) {
			return 0;
		}

		@Override
		public boolean canReceive(IEnergyStorage container, EnumFacing from) {
			return true;
		}

		@Override
		public boolean canExtract(IEnergyStorage container, EnumFacing from) {
			return false;
		}

		@Override
		public IEnergyStorage getContainer(TileEntity te, EnumFacing from) {
			return te.getCapability(CapabilityEnergy.ENERGY, from);
		}

		@Override
		public boolean hasSupport(TileEntity te, EnumFacing from) {
			return te.hasCapability(CapabilityEnergy.ENERGY, from) ? getContainer(te, from).canReceive() : false;
		}

		@Override
		public EnergyUnit defaultEnergyUnit() {
			return EnergyUnits.FORGE_ENERGY;
		}

		@Override
		public IEnergyStorage getContainer(ItemStack stack, EnumFacing from) {
			return stack.getCapability(CapabilityEnergy.ENERGY, from);
		}

		@Override
		public boolean hasSupport(ItemStack stack, EnumFacing from) {
			return stack.hasCapability(CapabilityEnergy.ENERGY, from) ? getContainer(stack, from).canReceive() : false;
		}

	}

}
