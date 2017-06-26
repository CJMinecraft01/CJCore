package cjminecraft.core.energy;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.energy.EnergyStorage;

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

	public void readFromNBT(NBTTagCompound nbt) {
		this.setEnergyStored(nbt.getInteger("Energy"));
	}

	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("Energy", this.getEnergyStored());
	}

	public void setEnergyStored(int energy) {
		this.energy = energy;
	}

}
