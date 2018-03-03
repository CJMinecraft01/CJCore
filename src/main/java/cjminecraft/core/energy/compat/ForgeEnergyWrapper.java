package cjminecraft.core.energy.compat;

import cjminecraft.core.energy.compat.forge.CustomForgeEnergyStorage;
import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.api.ITeslaProducer;
import net.minecraftforge.energy.IEnergyStorage;

/**
 * An energy storage which is compatible with forge energy
 * 
 * @author CJMinecraft
 *
 */
class ForgeEnergyWrapper implements IEnergyStorage {

	private EnergyStorage storage;

	/**
	 * Initialise a new forge energy wrapper which makes the energy storage
	 * compatible with forge energy
	 * 
	 * @param storage
	 *            The actual energy storage
	 */
	public ForgeEnergyWrapper(EnergyStorage storage) {
		this.storage = storage;
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		return (int) this.storage.receiveEnergy(maxReceive, simulate);
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		return (int) this.storage.extractEnergy(maxExtract, simulate);
	}

	@Override
	public int getEnergyStored() {
		return (int) this.storage.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored() {
		return (int) this.storage.getMaxEnergyStored();
	}

	@Override
	public boolean canExtract() {
		return this.storage.canExtract();
	}

	@Override
	public boolean canReceive() {
		return this.storage.canReceive();
	}
	
	public int getMaxTransfer() {
		return (int) this.storage.getMaxTransfer();
	}

}
