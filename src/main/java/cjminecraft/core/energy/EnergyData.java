package cjminecraft.core.energy;

import cjminecraft.core.energy.EnergyUnits.EnergyUnit;

/**
 * Holds energy data for use with syncing to servers. See {@link EnergyUtils}
 * 
 * @author CJMinecraft
 *
 */
public class EnergyData {

	private EnergyUnit unit;
	private long energy;
	private long capacity;

	/**
	 * Initialize all values to the default
	 */
	public EnergyData() {
		this.unit = EnergyUnits.MINECRAFT_JOULES;
		this.energy = 0;
		this.capacity = 0;
	}

	/**
	 * Convert the energy and capacity to the new {@link EnergyUnit}
	 * 
	 * @param to
	 *            The {@link EnergyUnit} to convert into
	 * @return This
	 */
	public EnergyData convertData(EnergyUnit to) {
		this.energy = EnergyUtils.convertEnergy(this.unit, to, this.energy);
		this.capacity = EnergyUtils.convertEnergy(this.unit, to, this.capacity);
		return this;
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
