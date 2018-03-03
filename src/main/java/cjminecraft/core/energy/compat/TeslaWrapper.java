package cjminecraft.core.energy.compat;

import cjminecraft.core.energy.compat.forge.CustomForgeEnergyStorage;
import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.api.ITeslaProducer;

/**
 * An energy storage which is compatible with tesla
 * 
 * @author CJMinecraft
 *
 */
class TeslaWrapper implements ITeslaHolder, ITeslaConsumer, ITeslaProducer {

	private EnergyStorage storage;

	/**
	 * Initialise a new tesla wrapper which makes the energy storage compatible
	 * with tesla
	 * 
	 * @param storage
	 *            The actual energy storage
	 */
	public TeslaWrapper(EnergyStorage storage) {
		this.storage = storage;
	}

	@Override
	public long takePower(long power, boolean simulated) {
		return this.storage.extractEnergy(power, simulated);
	}

	@Override
	public long givePower(long power, boolean simulated) {
		return this.storage.receiveEnergy(power, simulated);
	}

	@Override
	public long getStoredPower() {
		return this.storage.getEnergyStored();
	}

	@Override
	public long getCapacity() {
		return this.storage.getMaxEnergyStored();
	}

}
