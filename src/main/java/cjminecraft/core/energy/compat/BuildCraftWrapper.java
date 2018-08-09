package cjminecraft.core.energy.compat;

import buildcraft.api.mj.IMjConnector;
import buildcraft.api.mj.IMjPassiveProvider;
import buildcraft.api.mj.IMjReadable;
import buildcraft.api.mj.IMjReceiver;
import buildcraft.api.mj.MjAPI;
import cjminecraft.core.energy.EnergyUnit;
import cjminecraft.core.energy.EnergyUtils;

/**
 * An energy storage which is compatible with build craft
 * 
 * @author CJMinecraft
 *
 */
class BuildCraftWrapper implements IMjReceiver, IMjReadable, IMjPassiveProvider {

	private EnergyStorage storage;
	
	/**
	 * Initialise a new build craft wrapper which makes the energy storage compatible
	 * with build craft
	 * 
	 * @param storage
	 *            The actual energy storage
	 */
	public BuildCraftWrapper(EnergyStorage storage) {
		this.storage = storage;
	}
	
	@Override
	public boolean canConnect(IMjConnector connector) {
		return true;
	}

	@Override
	public long extractPower(long min, long max, boolean simulate) {
		return this.storage.extractEnergy((int) EnergyUtils.convertEnergy(EnergyUnit.MINECRAFT_JOULES, EnergyUnit.FORGE_ENERGY, Math.max(min, max) / MjAPI.ONE_MINECRAFT_JOULE), simulate);
	}

	@Override
	public long getCapacity() {
		return EnergyUtils.convertEnergy(EnergyUnit.FORGE_ENERGY, EnergyUnit.MINECRAFT_JOULES, this.storage.getMaxEnergyStored()) * MjAPI.ONE_MINECRAFT_JOULE;
	}

	@Override
	public long getStored() {
		return EnergyUtils.convertEnergy(EnergyUnit.FORGE_ENERGY, EnergyUnit.MINECRAFT_JOULES, this.storage.getEnergyStored()) * MjAPI.ONE_MINECRAFT_JOULE;
	}

	@Override
	public long getPowerRequested() {
		return EnergyUtils.convertEnergy(EnergyUnit.FORGE_ENERGY, EnergyUnit.MINECRAFT_JOULES, this.storage.getMaxReceive()) * MjAPI.ONE_MINECRAFT_JOULE;
	}

	@Override
	public long receivePower(long microJoules, boolean simulate) {
		return this.storage.receiveEnergy((int) (EnergyUtils.convertEnergy(EnergyUnit.MINECRAFT_JOULES, EnergyUnit.FORGE_ENERGY, microJoules) / MjAPI.ONE_MINECRAFT_JOULE), simulate);
	}

}
