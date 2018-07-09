package cjminecraft.core.energy.compat;

import java.util.List;

import cjminecraft.core.energy.EnergyUnits;
import cjminecraft.core.energy.EnergyUtils;
import cjminecraft.core.energy.compat.forge.CustomForgeEnergyStorage;
import cofh.api.energy.IEnergyContainerItem;
import cofh.api.energy.ItemEnergyContainer;
import ic2.api.item.IElectricItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.common.Optional;

@Optional.InterfaceList(value = { @Optional.Interface(iface = "ic2.api.item.IElectricItem", modid = "ic2"), })
public class ItemEnergy extends Item implements IElectricItem, IEnergyContainerItem {

	protected long capacity;
	protected long maxReceive;
	protected long maxExtract;

	public ItemEnergy(long capacity) {
		this(capacity, capacity, capacity);
	}

	public ItemEnergy(long capacity, long maxTransfer) {
		this(capacity, maxTransfer, maxTransfer);
	}

	public ItemEnergy(long capacity, long maxReceive, long maxExtract) {
		this.capacity = capacity;
		this.maxReceive = maxReceive;
		this.maxExtract = maxExtract;
	}

	public ItemEnergy setCapacity(long capacity) {
		this.capacity = capacity;
		return this;
	}

	public ItemEnergy setMaxTransfer(long maxTransfer) {
		setMaxReceive(maxTransfer);
		setMaxExtract(maxTransfer);
		return this;
	}

	public ItemEnergy setMaxReceive(long maxReceive) {
		this.maxReceive = maxReceive;
		return this;
	}

	public ItemEnergy setMaxExtract(long maxExtract) {
		this.maxExtract = maxExtract;
		return this;
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		EnergyUtils.addEnergyInformation(stack, tooltip);
	}
	
	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return EnergyUtils.hasSupport(stack, null);
	}
	
	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		return EnergyUtils.getEnergyDurabilityForDisplay(stack);
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
		if (nbt != null && nbt.hasKey("Energy") && nbt.hasKey("Capacity") && nbt.hasKey("MaxReceive")
				&& nbt.hasKey("MaxExtract"))
			return new EnergyCapabilityProvider(stack, nbt, EnergyUnits.FORGE_ENERGY);
		return new EnergyCapabilityProvider(stack, getEnergyStored(stack), this.capacity, this.maxReceive,
				this.maxExtract, EnergyUnits.FORGE_ENERGY);
	}

	/**
	 * Determine if the item can be used in a machine or as an armor part to
	 * supply energy.
	 * 
	 * @param stack
	 *            The {@link ItemStack} which is asking whether it can provide
	 *            energy
	 *
	 * @return Whether the item can supply energy
	 */
	@Override
	@Optional.Method(modid = "ic2")
	public boolean canProvideEnergy(ItemStack stack) {
		return EnergyUtils.canExtract(stack, null);
	}

	/**
	 * Get the item's maximum charge energy in EU.
	 * 
	 * @param stack
	 *            The {@link ItemStack} to get the max charge from
	 * @return Maximum charge energy
	 */
	@Override
	@Optional.Method(modid = "ic2")
	public double getMaxCharge(ItemStack stack) {
		return EnergyUtils.getCapacity(stack, null, EnergyUnits.ENERGY_UNIT);
	}

	/**
	 * Get the item's tier, lower tiers can't send energy to higher ones.
	 *
	 * Batteries are Tier 1, Advanced Batteries are Tier 2, Energy Crystals are
	 * Tier 3, Lapotron Crystals are Tier 4.
	 *
	 * @param stack
	 *            The {@link ItemStack} to get the tier from
	 *
	 * @return Item's tier
	 */
	@Override
	@Optional.Method(modid = "ic2")
	public int getTier(ItemStack stack) {
		if (stack.hasCapability(CapabilityEnergy.ENERGY, null))
			return ((int) (Math.log(
					((EnergyStorage) stack.getCapability(CapabilityEnergy.ENERGY, null)).getMaxTransfer())
					/ Math.log(2)) - 3) / 2;
		return 0;
	}

	/**
	 * Get the item's transfer limit in EU per transfer operation.
	 *
	 * @param stack
	 *            The maximum amount of energy which can be transfered using
	 *            this {@link ItemStack}
	 * @return Transfer limit
	 */
	@Override
	@Optional.Method(modid = "ic2")
	public double getTransferLimit(ItemStack stack) {
		if (stack.hasCapability(CapabilityEnergy.ENERGY, null))
			return ((EnergyStorage) stack.getCapability(CapabilityEnergy.ENERGY, null)).getMaxTransfer();
		return 0;
	}

	@Override
	public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {
		if (!container.hasTagCompound())
			container.setTagCompound(new NBTTagCompound());
		long energy = container.getTagCompound().getLong("Energy");
		long energyReceived = Math.min(this.capacity - energy, Math.min(this.maxReceive, maxReceive));

		if (!simulate) {
			energy += energyReceived;
			container.getTagCompound().setLong("Energy", energy);
		}
		return (int) energyReceived;
	}

	@Override
	public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {
		if (container.getTagCompound() == null || !container.getTagCompound().hasKey("Energy"))
			return 0;
		long energy = container.getTagCompound().getLong("Energy");
		long energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));

		if (!simulate) {
			energy -= energyExtracted;
			container.getTagCompound().setLong("Energy", energy);
		}
		return (int) energyExtracted;
	}

	@Override
	public int getEnergyStored(ItemStack container) {
		if (container.getTagCompound() == null || !container.getTagCompound().hasKey("Energy"))
			return 0;
		return (int) container.getTagCompound().getLong("Energy");
	}

	@Override
	public int getMaxEnergyStored(ItemStack container) {
		return (int) this.capacity;
	}

}
