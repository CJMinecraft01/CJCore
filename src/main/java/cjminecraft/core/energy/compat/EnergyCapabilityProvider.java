package cjminecraft.core.energy.compat;

import cjminecraft.core.CJCore;
import cjminecraft.core.energy.EnergyUnits;
import cjminecraft.core.energy.EnergyUtils;
import cjminecraft.core.energy.EnergyUnits.EnergyUnit;
import cjminecraft.core.energy.compat.forge.CustomForgeEnergyStorage;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;

/**
 * Used in {@link Item#initCapabilities(ItemStack, NBTTagCompound)}. This
 * capability provider allows support for build craft and tesla
 * 
 * @author CJMinecraft
 *
 */
public class EnergyCapabilityProvider implements ICapabilityProvider {

	private CustomForgeEnergyStorage storage;
	private Object teslaWrapper;
	private Object buildCraftWrapper;

	/**
	 * Create a new {@link CustomForgeEnergyStorage} for an {@link ItemStack}
	 * 
	 * @param stack
	 *            The stack which will have the {@link CustomForgeEnergyStorage}
	 * @param nbt
	 *            The {@link NBTTagCompound} with the data about energy (can be
	 *            got from any {@link TileEntity} which has a
	 *            {@link CustomForgeEnergyStorage}
	 * @param unit
	 *            The {@link EnergyUnit} the energy is in
	 */
	public EnergyCapabilityProvider(ItemStack stack, NBTTagCompound nbt, EnergyUnit unit) {
		this(stack, nbt.getInteger("Energy"), nbt.getInteger("Capacity"), nbt.getInteger("MaxReceive"),
				nbt.getInteger("MaxExtract"), unit);
	}

	/**
	 * Create a new {@link CustomForgeEnergyStorage} for an {@link ItemStack}
	 * 
	 * @param stack
	 *            The stack which will have the {@link CustomForgeEnergyStorage}
	 * @param energy
	 *            The energy of the {@link CustomForgeEnergyStorage}
	 * @param capacity
	 *            The capacity of the {@link CustomForgeEnergyStorage}
	 * @param maxReceive
	 *            The maximum amount of energy the
	 *            {@link CustomForgeEnergyStorage} can receive
	 * @param maxExtract
	 *            The maximum amount of energy that can be extracted from the
	 *            {@link CustomForgeEnergyStorage}
	 * @param unit
	 *            The {@link EnergyUnit} the energy is in
	 */
	public EnergyCapabilityProvider(ItemStack stack, int energy, int capacity, int maxReceive, int maxExtract,
			EnergyUnit unit) {
		this.storage = new CustomForgeEnergyStorage(
				(int) EnergyUtils.convertEnergy(unit, EnergyUnits.FORGE_ENERGY, capacity),
				(int) EnergyUtils.convertEnergy(unit, EnergyUnits.FORGE_ENERGY, maxReceive),
				(int) EnergyUtils.convertEnergy(unit, EnergyUnits.FORGE_ENERGY, maxExtract),
				(int) EnergyUtils.convertEnergy(unit, EnergyUnits.FORGE_ENERGY, energy)) {
			@Override
			public int getEnergyStored() {
				if (stack.hasTagCompound())
					return stack.getTagCompound().getInteger("Energy");
				return 0;
			}

			@Override
			public CustomForgeEnergyStorage setEnergyStored(int energy) {
				if (!stack.hasTagCompound())
					stack.setTagCompound(new NBTTagCompound());
				stack.getTagCompound().setInteger("Energy", energy);
				return this;
			}

			@Override
			public int getMaxEnergyStored() {
				if (stack.hasTagCompound())
					return stack.getTagCompound().getInteger("Capacity");
				return 0;
			}

			@Override
			public CustomForgeEnergyStorage setCapacity(int capacity) {
				if (!stack.hasTagCompound())
					stack.setTagCompound(new NBTTagCompound());
				stack.getTagCompound().setInteger("Capacity", capacity);
				return this;
			}

			@Override
			public int getMaxReceive() {
				if (stack.hasTagCompound())
					return stack.getTagCompound().getInteger("MaxReceive");
				return 0;
			}

			@Override
			public boolean canExtract() {
				if (stack.hasTagCompound())
					return stack.getTagCompound().getInteger("MaxExtract") > 0;
				return super.canExtract();
			}

			@Override
			public boolean canReceive() {
				if (stack.hasTagCompound())
					return stack.getTagCompound().getInteger("MaxReceive") > 0;
				return super.canReceive();
			}

			@Override
			public CustomForgeEnergyStorage setMaxReceive(int maxReceive) {
				if (!stack.hasTagCompound())
					stack.setTagCompound(new NBTTagCompound());
				stack.getTagCompound().setInteger("MaxReceive", maxReceive);
				return this;
			}

			@Override
			public int getMaxExtract() {
				if (stack.hasTagCompound())
					return stack.getTagCompound().getInteger("MaxExtract");
				return 0;
			}

			@Override
			public CustomForgeEnergyStorage setMaxExtract(int maxExtract) {
				if (!stack.hasTagCompound())
					stack.setTagCompound(new NBTTagCompound());
				stack.getTagCompound().setInteger("MaxExtract", maxExtract);
				return this;
			}

			@Override
			public int getMaxTransfer() {
				if (stack.hasTagCompound()) {
					int maxReceive = stack.getTagCompound().getInteger("MaxReceive");
					int maxExtract = stack.getTagCompound().getInteger("MaxExtract");
					return maxReceive == maxExtract ? maxReceive : Math.max(maxReceive, maxExtract);
				}
				return 0;
			}

			@Override
			public CustomForgeEnergyStorage setMaxTransfer(int transfer) {
				if (!stack.hasTagCompound())
					stack.setTagCompound(new NBTTagCompound());
				stack.getTagCompound().setInteger("MaxExtract", transfer);
				stack.getTagCompound().setInteger("MaxReceive", transfer);
				return this;
			}

			@Override
			public int receiveEnergy(int maxReceive, boolean simulate) {
				if (!canReceive())
					return 0;

				int energyReceived = Math.min(getMaxEnergyStored() - getEnergyStored(),
						Math.min(getMaxReceive(), maxReceive));
				if (!simulate)
					stack.getTagCompound().setInteger("Energy", getEnergyStored() + energyReceived);
				return energyReceived;
			}

			@Override
			public int extractEnergy(int maxExtract, boolean simulate) {
				if (!canExtract())
					return 0;
				int energyExtracted = Math.min(getEnergyStored(), Math.min(getMaxExtract(), maxExtract));
				if (!simulate)
					stack.getTagCompound().setInteger("Energy", getEnergyStored() - energyExtracted);
				return energyExtracted;
			}
		};
	}

	/**
	 * Says whether the {@link ItemStack} has the given capability
	 */
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return this.getCapability(capability, facing) != null;
	}

	/**
	 * Get the {@link CapabilityEnergy#ENERGY} from the {@link ItemStack}
	 */
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityEnergy.ENERGY)
			return (T) this.storage;
		if (EnergyUtils.TESLA_LOADED && (capability == EnergyUtils.TESLA_HOLDER
				|| capability == EnergyUtils.TESLA_CONSUMER || capability == EnergyUtils.TESLA_PRODUCER)) {
			if (this.teslaWrapper == null)
				this.teslaWrapper = new TeslaWrapper(this.storage);
			return (T) this.teslaWrapper;
		}
		if (EnergyUtils.BUILDCRAFT_LOADED
				&& (capability == EnergyUtils.BUILDCRAFT_READABLE || capability == EnergyUtils.BUILDCRAFT_RECEIVER
						|| capability == EnergyUtils.BUILDCRAFT_PASSIVE_PROVIDER)) {
			if (this.buildCraftWrapper == null)
				this.buildCraftWrapper = new BuildCraftWrapper(this.storage);
			return (T) this.buildCraftWrapper;
		}
		return null;
	}

}
