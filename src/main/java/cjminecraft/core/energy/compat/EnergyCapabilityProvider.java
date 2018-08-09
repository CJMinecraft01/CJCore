package cjminecraft.core.energy.compat;

import cjminecraft.core.CJCore;
import cjminecraft.core.energy.EnergyUnit;
import cjminecraft.core.energy.EnergyUtils;
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
 * capability provider allows support for tesla
 * 
 * @author CJMinecraft
 *
 */
public class EnergyCapabilityProvider implements ICapabilityProvider {

	private EnergyStorage storage;
	private Object teslaWrapper;
	private Object forgeWrapper;

	/**
	 * Create a new {@link EnergyStorage} for an {@link ItemStack}
	 * 
	 * @param stack
	 *            The stack which will have the {@link EnergyStorage}
	 * @param unit
	 *            The {@link EnergyUnit} the energy is in
	 */
	public EnergyCapabilityProvider(ItemStack stack, EnergyUnit unit) {
		this(stack, stack.getTagCompound().getInteger("Energy"), stack.getTagCompound().getInteger("Capacity"),
				stack.getTagCompound().getInteger("MaxReceive"), stack.getTagCompound().getInteger("MaxExtract"), unit);
	}

	/**
	 * Create a new {@link EnergyStorage} for an {@link ItemStack}
	 * 
	 * @param stack
	 *            The stack which will have the {@link EnergyStorage}
	 * @param energy
	 *            The energy of the {@link EnergyStorage}
	 * @param capacity
	 *            The capacity of the {@link EnergyStorage}
	 * @param maxReceive
	 *            The maximum amount of energy the {@link EnergyStorage} can
	 *            receive
	 * @param maxExtract
	 *            The maximum amount of energy that can be extracted from the
	 *            {@link EnergyStorage}
	 * @param unit
	 *            The {@link EnergyUnit} the energy is in
	 */
	public EnergyCapabilityProvider(ItemStack stack, long energy, long capacity, long maxReceive, long maxExtract,
			EnergyUnit unit) {
		this.storage = new EnergyStorage(EnergyUtils.convertEnergy(unit, EnergyUnit.FORGE_ENERGY, capacity),
				EnergyUtils.convertEnergy(unit, EnergyUnit.FORGE_ENERGY, maxReceive),
				EnergyUtils.convertEnergy(unit, EnergyUnit.FORGE_ENERGY, maxExtract),
				EnergyUtils.convertEnergy(unit, EnergyUnit.FORGE_ENERGY, energy)) {
			@Override
			public long getEnergyStored() {
				if (stack.hasTagCompound())
					return stack.getTagCompound().getLong("Energy");
				return 0;
			}

			@Override
			public EnergyStorage setEnergyStored(long energy) {
				if (!stack.hasTagCompound())
					stack.setTagCompound(new NBTTagCompound());
				stack.getTagCompound().setLong("Energy", energy);
				return this;
			}

			@Override
			public long getMaxEnergyStored() {
				if (stack.hasTagCompound())
					return stack.getTagCompound().getLong("Capacity");
				return 0;
			}

			@Override
			public EnergyStorage setCapacity(long capacity) {
				if (!stack.hasTagCompound())
					stack.setTagCompound(new NBTTagCompound());
				stack.getTagCompound().setLong("Capacity", capacity);
				return this;
			}

			@Override
			public long getMaxReceive() {
				if (stack.hasTagCompound())
					return stack.getTagCompound().getLong("MaxReceive");
				return 0;
			}

			@Override
			public boolean canExtract() {
				if (stack.hasTagCompound())
					return stack.getTagCompound().getLong("MaxExtract") > 0;
				return super.canExtract();
			}

			@Override
			public boolean canReceive() {
				if (stack.hasTagCompound())
					return stack.getTagCompound().getLong("MaxReceive") > 0;
				return super.canReceive();
			}

			@Override
			public EnergyStorage setMaxReceive(long maxReceive) {
				if (!stack.hasTagCompound())
					stack.setTagCompound(new NBTTagCompound());
				stack.getTagCompound().setLong("MaxReceive", maxReceive);
				return this;
			}

			@Override
			public long getMaxExtract() {
				if (stack.hasTagCompound())
					return stack.getTagCompound().getLong("MaxExtract");
				return 0;
			}

			@Override
			public EnergyStorage setMaxExtract(long maxExtract) {
				if (!stack.hasTagCompound())
					stack.setTagCompound(new NBTTagCompound());
				stack.getTagCompound().setLong("MaxExtract", maxExtract);
				return this;
			}

			@Override
			public long getMaxTransfer() {
				if (stack.hasTagCompound()) {
					long maxReceive = stack.getTagCompound().getLong("MaxReceive");
					long maxExtract = stack.getTagCompound().getLong("MaxExtract");
					return maxReceive == maxExtract ? maxReceive : Math.max(maxReceive, maxExtract);
				}
				return 0;
			}

			@Override
			public EnergyStorage setMaxTransfer(long transfer) {
				if (!stack.hasTagCompound())
					stack.setTagCompound(new NBTTagCompound());
				stack.getTagCompound().setLong("MaxExtract", transfer);
				stack.getTagCompound().setLong("MaxReceive", transfer);
				return this;
			}

			@Override
			public long receiveEnergy(long maxReceive, boolean simulate) {
				if (!canReceive())
					return 0;

				long energyReceived = Math.min(getMaxEnergyStored() - getEnergyStored(),
						Math.min(getMaxReceive(), maxReceive));
				if (!simulate)
					stack.getTagCompound().setLong("Energy", getEnergyStored() + energyReceived);
				return energyReceived;
			}

			@Override
			public long extractEnergy(long maxExtract, boolean simulate) {
				if (!canExtract())
					return 0;
				long energyExtracted = Math.min(getEnergyStored(), Math.min(getMaxExtract(), maxExtract));
				if (!simulate)
					stack.getTagCompound().setLong("Energy", getEnergyStored() - energyExtracted);
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
		if (capability == CapabilityEnergy.ENERGY) {
			if (this.forgeWrapper == null)
				this.forgeWrapper = new ForgeEnergyWrapper(this.storage);
			return (T) this.forgeWrapper;
		}
		if (EnergyUtils.TESLA_LOADED && (capability == EnergyUtils.TESLA_HOLDER
				|| capability == EnergyUtils.TESLA_CONSUMER || capability == EnergyUtils.TESLA_PRODUCER)) {
			if (this.teslaWrapper == null)
				this.teslaWrapper = new TeslaWrapper(this.storage);
			return (T) this.teslaWrapper;
		}
		return null;
	}

}
