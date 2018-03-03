package cjminecraft.core.energy.compat;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

/**
 * An energy storage which is not limited by the maximum 32 bit integer.
 * 
 * @author CJMinecraft
 *
 */
public class EnergyStorage {

	private long energy;
	private long capacity;
	private long maxReceive;
	private long maxExtract;

	/**
	 * Initialise an energy storage.
	 * 
	 * @param capacity
	 *            The capacity of the energy storage
	 */
	public EnergyStorage(long capacity) {
		this(capacity, capacity, capacity, 0);
	}

	/**
	 * Initialise an energy storage.
	 * 
	 * @param capacity
	 *            The capacity of the energy storage
	 * @param maxTransfer
	 *            The maximum amount of energy which can be received and
	 *            extracted
	 */
	public EnergyStorage(long capacity, long maxTransfer) {
		this(capacity, maxTransfer, maxTransfer, 0);
	}

	/**
	 * Initialise an energy storage
	 * 
	 * @param capacity
	 *            The capacity of the energy storage
	 * @param maxReceive
	 *            The maximum amount of energy which can be received
	 * @param maxExtract
	 *            The maximum amount of energy which can be extracted
	 */
	public EnergyStorage(long capacity, long maxReceive, long maxExtract) {
		this(capacity, maxReceive, maxExtract, 0);
	}

	/**
	 * Initialise an energy storage
	 * 
	 * @param capacity
	 *            The capacity of the energy storage
	 * @param maxReceive
	 *            The maximum amount of energy which can be received
	 * @param maxExtract
	 *            The maximum amount of energy which can be extracted
	 * @param energy
	 *            The energy inside of the energy storage
	 */
	public EnergyStorage(long capacity, long maxReceive, long maxExtract, long energy) {
		this.capacity = capacity;
		this.maxReceive = maxReceive;
		this.maxExtract = maxExtract;
		this.energy = Math.max(0, Math.min(capacity, energy));
	}

	/**
	 * Give energy to the energy storage
	 * 
	 * @param maxReceive
	 *            The amount of energy to give
	 * @param simulate
	 *            Whether it is a simulation - if so, no energy will actually be
	 *            given
	 * @return The amount of energy which was received
	 */
	public long receiveEnergy(long maxReceive, boolean simulate) {
		if (!canReceive())
			return 0;

		long energyReceived = Math.min(this.capacity - this.energy, Math.min(this.maxReceive, maxReceive));
		if (!simulate)
			this.energy += energyReceived;
		return energyReceived;
	}

	/**
	 * Take energy from the energy storage
	 * 
	 * @param maxExtract
	 *            The amount of energy to take
	 * @param simulate
	 *            Whether it is a simulation - if so, no energy will actually be
	 *            taken
	 * @return The amount of energy which was extracted
	 */
	public long extractEnergy(long maxExtract, boolean simulate) {
		if (!canExtract())
			return 0;

		long energyExtracted = Math.min(this.energy, Math.min(this.maxExtract, maxExtract));
		if (!simulate)
			this.energy -= energyExtracted;
		return energyExtracted;
	}

	/**
	 * For use only inside your {@link TileEntity}
	 * 
	 * @param maxExtract
	 *            How much you want to extract
	 * @param simulate
	 *            Whether it is a simulation
	 * @return The amount of energy taken
	 */
	public long extractEnergyInternal(long maxExtract, boolean simulate) {
		long before = this.maxExtract;
		this.maxExtract = Long.MAX_VALUE;

		long toReturn = this.extractEnergy(maxExtract, simulate);

		this.maxExtract = before;
		return toReturn;
	}

	/**
	 * For use only inside your {@link TileEntity}
	 * 
	 * @param maxReceive
	 *            How much you want to give
	 * @param simulate
	 *            Whether it is a simulation
	 * @return The amount of energy given
	 */
	public long receiveEnergyInternal(long maxReceive, boolean simulate) {
		long before = this.maxReceive;
		this.maxReceive = Long.MAX_VALUE;

		long toReturn = this.receiveEnergy(maxReceive, simulate);

		this.maxReceive = before;
		return toReturn;
	}

	/**
	 * Read and set all values from the data inside the given
	 * {@link NBTTagCompound}
	 * 
	 * @param nbt
	 *            The {@link NBTTagCompound} with all the data
	 */
	public void readFromNBT(NBTTagCompound nbt) {
		this.energy = nbt.getInteger("Energy");
		this.capacity = nbt.getInteger("Capacity");
		this.maxReceive = nbt.getInteger("MaxReceive");
		this.maxExtract = nbt.getInteger("MaxExtract");
	}

	/**
	 * Write all of the data to the {@link NBTTagCompound} provided
	 * 
	 * @param nbt
	 *            The {@link NBTTagCompound} to write to
	 */
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setLong("Energy", this.energy);
		nbt.setLong("Capacity", this.capacity);
		nbt.setLong("MaxReceive", this.maxReceive);
		nbt.setLong("MaxExtract", this.maxExtract);
	}

	/**
	 * Set the current energy
	 * 
	 * @param energy
	 *            The energy to set
	 * @return The {@link EnergyStorage} with the new energy
	 */
	public EnergyStorage setEnergyStored(long energy) {
		this.energy = energy;
		return this;
	}

	/**
	 * Set the current capacity
	 * 
	 * @param capacity
	 *            The capacity to set
	 * @return The {@link EnergyStorage} with the new capacity
	 */
	public EnergyStorage setCapacity(long capacity) {
		this.capacity = capacity;
		return this;
	}

	/**
	 * Set the current max transfer
	 * 
	 * @param transfer
	 *            The max transfer to set
	 * @return The {@link EnergyStorage} with the new max transfer
	 */
	public EnergyStorage setMaxTransfer(long transfer) {
		this.maxReceive = transfer;
		this.maxExtract = transfer;
		return this;
	}

	/**
	 * Set the current max receive
	 * 
	 * @param maxReceive
	 *            The max receive to set
	 * @return The {@link EnergyStorage} with the new max receive
	 */
	public EnergyStorage setMaxReceive(long maxReceive) {
		this.maxReceive = maxReceive;
		return this;
	}

	/**
	 * Set the current max extract
	 * 
	 * @param maxExtract
	 *            The max extract to set
	 * @return The {@link EnergyStorage} with the new max extract
	 */
	public EnergyStorage setMaxExtract(long maxExtract) {
		this.maxExtract = maxExtract;
		return this;
	}

	/**
	 * Get the maximum energy this can extract and receive
	 * 
	 * @return The max transfer
	 */
	public long getMaxTransfer() {
		return this.maxReceive == this.maxExtract ? this.maxReceive : Math.max(this.maxReceive, this.maxExtract);
	}

	/**
	 * Get the maximum energy this can receive
	 * 
	 * @return The maximum energy this can receive
	 */
	public long getMaxReceive() {
		return this.maxReceive;
	}

	/**
	 * Get the maximum energy that can be extracted
	 * 
	 * @return The maximum energy that can be extracted
	 */
	public long getMaxExtract() {
		return this.maxExtract;
	}

	/**
	 * Get the energy inside of the energy storage
	 * 
	 * @return The energy inside of the energy storage
	 */
	public long getEnergyStored() {
		return this.energy;
	}

	/**
	 * Get the maximum amount of energy in the energy storage
	 * 
	 * @return The maximum amount of energy in the energy storage
	 */
	public long getMaxEnergyStored() {
		return this.capacity;
	}

	/**
	 * @return whether you can take energy from the energy storage
	 */
	public boolean canExtract() {
		return this.maxExtract > 0;
	}

	/**
	 * @return whether you can give energy to the energy storage
	 */
	public boolean canReceive() {
		return this.maxReceive > 0;
	}

}
