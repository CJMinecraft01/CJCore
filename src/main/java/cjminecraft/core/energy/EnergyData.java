package cjminecraft.core.energy;

import cjminecraft.core.energy.EnergyUnits.EnergyUnit;

/**
 * Holds energy data for use with syncing to servers. See {@link EnergyUtils}
 * 
 * @author CJMinecraft
 *
 */
public class EnergyData {

	private long energy;
	private long capacity;

	/**
	 * Initialize all values to the default
	 */
	public EnergyData() {
		this.energy = 0;
		this.capacity = 0;
	}

	/**
	 * Set the energy inside of the {@link EnergyData}
	 * 
	 * @param energy
	 *            The energy inside
	 * @return This
	 */
	public EnergyData setEnergy(long energy) {
		this.energy = energy;
		return this;
	}

	/**
	 * Set the capacity inside of the {@link EnergyData}
	 * 
	 * @param capacity
	 *            The capacity inside
	 * @return This
	 */
	public EnergyData setCapacity(long capacity) {
		this.capacity = capacity;
		return this;
	}

	/**
	 * Returns the energy inside of the {@link EnergyData}
	 * 
	 * @return The energy inside
	 */
	public long getEnergy() {
		return energy;
	}

	/**
	 * Returns the capacity inside of the {@link EnergyData}
	 * 
	 * @return The capacity inside
	 */
	public long getCapacity() {
		return capacity;
	}

}
