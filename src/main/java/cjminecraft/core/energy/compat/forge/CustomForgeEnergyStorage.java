package cjminecraft.core.energy.compat.forge;

import cjminecraft.core.CJCore;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

/**
 * An improved version of an {@link EnergyStorage}
 * 
 * @author CJMinecraft
 *
 */
public class CustomForgeEnergyStorage extends EnergyStorage {

	public CustomForgeEnergyStorage(int capacity) {
		super(capacity, capacity, capacity, 0);
	}

	public CustomForgeEnergyStorage(int capacity, int maxTransfer) {
		super(capacity, maxTransfer, maxTransfer, 0);
	}

	public CustomForgeEnergyStorage(int capacity, int maxReceive, int maxExtract) {
		super(capacity, maxReceive, maxExtract, 0);
	}

	public CustomForgeEnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) {
		super(capacity, maxReceive, maxExtract, energy);
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
	public int extractEnergyInternal(int maxExtract, boolean simulate) {
		int before = this.maxExtract;
		this.maxExtract = Integer.MAX_VALUE;

		int toReturn = this.extractEnergy(maxExtract, simulate);

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
	public int receiveEnergyInternal(int maxReceive, boolean simulate) {
		int before = this.maxReceive;
		this.maxReceive = Integer.MAX_VALUE;

		int toReturn = this.receiveEnergy(maxReceive, simulate);

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
		nbt.setInteger("Energy", this.energy);
		nbt.setInteger("Capacity", this.capacity);
		nbt.setInteger("MaxReceive", this.maxReceive);
		nbt.setInteger("MaxExtract", this.maxExtract);
	}

	/**
	 * Set the current energy
	 * 
	 * @param energy
	 *            The energy to set
	 * @return The {@link CustomForgeEnergyStorage} with the new energy
	 */
	public CustomForgeEnergyStorage setEnergyStored(int energy) {
		this.energy = energy;
		return this;
	}

	/**
	 * Set the current capacity
	 * 
	 * @param capacity
	 *            The capacity to set
	 * @return The {@link CustomForgeEnergyStorage} with the new capacity
	 */
	public CustomForgeEnergyStorage setCapacity(int capacity) {
		this.capacity = capacity;
		return this;
	}

	/**
	 * Set the current max transfer
	 * 
	 * @param transfer
	 *            The max transfer to set
	 * @return The {@link CustomForgeEnergyStorage} with the new max transfer
	 */
	public CustomForgeEnergyStorage setMaxTransfer(int transfer) {
		this.maxReceive = transfer;
		this.maxExtract = transfer;
		return this;
	}

	/**
	 * Set the current max receive
	 * 
	 * @param maxReceive
	 *            The max receive to set
	 * @return The {@link CustomForgeEnergyStorage} with the new max receive
	 */
	public CustomForgeEnergyStorage setMaxReceive(int maxReceive) {
		this.maxReceive = maxReceive;
		return this;
	}

	/**
	 * Set the current max extract
	 * 
	 * @param maxExtract
	 *            The max extract to set
	 * @return The {@link CustomForgeEnergyStorage} with the new max extract
	 */
	public CustomForgeEnergyStorage setMaxExtract(int maxExtract) {
		this.maxExtract = maxExtract;
		return this;
	}

	/**
	 * Get the maximum energy this can extract and receive
	 * 
	 * @return The max transfer
	 */
	public int getMaxTransfer() {
		return this.maxReceive == this.maxExtract ? this.maxReceive : Math.max(this.maxReceive, this.maxExtract);
	}

	/**
	 * Get the maximum energy this can receive
	 * 
	 * @return The maximum energy this can receive
	 */
	public int getMaxReceive() {
		return this.maxReceive;
	}

	/**
	 * Get the maximum energy that can be extracted
	 * 
	 * @return The maximum energy that can be extracted
	 */
	public int getMaxExtract() {
		return this.maxExtract;
	}

}
