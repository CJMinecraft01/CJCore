package cjminecraft.core.energy.support;

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
public class ForgeEnergySupport implements IEnergySupport<IEnergyStorage> {

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
		return container.extractEnergy((int) energy, simulate);
	}

	@Override
	public boolean canReceive(IEnergyStorage container, EnumFacing from) {
		return container.canReceive();
	}

	@Override
	public boolean canExtract(IEnergyStorage container, EnumFacing from) {
		return container.canExtract();
	}

	@Override
	public IEnergyStorage getContainer(TileEntity te, EnumFacing from) {
		return te.getCapability(CapabilityEnergy.ENERGY, from);
	}

	@Override
	public boolean hasSupport(TileEntity te, EnumFacing from) {
		return te.hasCapability(CapabilityEnergy.ENERGY, from);
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
		return stack.hasCapability(CapabilityEnergy.ENERGY, from);
	}

}
