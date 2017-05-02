package cjminecraft.core.energy.support;

import buildcraft.api.mj.IMjPassiveProvider;
import buildcraft.api.mj.IMjReadable;
import buildcraft.api.mj.IMjReceiver;
import buildcraft.api.mj.MjAPI;
import cjminecraft.core.energy.EnergyUnits;
import cjminecraft.core.energy.EnergyUnits.EnergyUnit;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

/**
 * Support for all buildcraft blocks which hold energy <strong>WIP</strong>
 * 
 * @author CJMinecraft
 *
 */
public class BuildCraftSupport {

	/**
	 * For blocks which hold energy
	 * @author CJMinecraft
	 *
	 */
	public static class BuildCraftHolderSupport implements IEnergySupport<IMjReadable> {

		@Override
		public long getEnergyStored(IMjReadable container, EnumFacing from) {
			return container.getStored() / 1000000;
		}

		@Override
		public long getCapacity(IMjReadable container, EnumFacing from) {
			return container.getCapacity() / 1000000; // Convert from
															// micro
			// joules to Minecraft
			// Joules
		}

		@Override
		public long giveEnergy(IMjReadable container, long energy, boolean simulate, EnumFacing from) {
			return 0;
		}

		@Override
		public long takeEnergy(IMjReadable container, long energy, boolean simulate, EnumFacing from) {
			return 0;
		}

		@Override
		public boolean canReceive(IMjReadable container, EnumFacing from) {
			return false;
		}

		@Override
		public boolean canExtract(IMjReadable container, EnumFacing from) {
			return false;
		}

		@Override
		public IMjReadable getContainer(TileEntity te, EnumFacing from) {
			return te.getCapability(MjAPI.CAP_READABLE, from);
		}

		@Override
		public boolean hasSupport(TileEntity te, EnumFacing from) {
			return te.hasCapability(MjAPI.CAP_READABLE, from);
		}

		@Override
		public EnergyUnit defaultEnergyUnit() {
			return EnergyUnits.MINECRAFT_JOULES;
		}

		@Override
		public IMjReadable getContainer(ItemStack stack, EnumFacing from) {
			return stack.getCapability(MjAPI.CAP_READABLE, from);
		}

		@Override
		public boolean hasSupport(ItemStack stack, EnumFacing from) {
			return stack.hasCapability(MjAPI.CAP_READABLE, from);
		}

	}

	/**
	 * For blocks which receive energy
	 * @author CJMinecraft
	 *
	 */
	public static class BuildCraftReceiverSupport implements IEnergySupport<IMjReceiver> {

		@Override
		public long getEnergyStored(IMjReceiver container, EnumFacing from) {
			return 0;
		}

		@Override
		public long getCapacity(IMjReceiver container, EnumFacing from) {
			return 0;
		}

		@Override
		public long giveEnergy(IMjReceiver container, long energy, boolean simulate, EnumFacing from) {
			return container.receivePower(energy * 1000000, simulate) / 1000000;
		}

		@Override
		public long takeEnergy(IMjReceiver container, long energy, boolean simulate, EnumFacing from) {
			return 0;
		}

		@Override
		public boolean canReceive(IMjReceiver container, EnumFacing from) {
			return container.canReceive();
		}

		@Override
		public boolean canExtract(IMjReceiver container, EnumFacing from) {
			return false;
		}

		@Override
		public IMjReceiver getContainer(TileEntity te, EnumFacing from) {
			return te.getCapability(MjAPI.CAP_RECEIVER, from);
		}

		@Override
		public boolean hasSupport(TileEntity te, EnumFacing from) {
			return te.hasCapability(MjAPI.CAP_RECEIVER, from);
		}

		@Override
		public EnergyUnit defaultEnergyUnit() {
			return EnergyUnits.MINECRAFT_JOULES;
		}

		@Override
		public IMjReceiver getContainer(ItemStack stack, EnumFacing from) {
			return stack.getCapability(MjAPI.CAP_RECEIVER, from);
		}

		@Override
		public boolean hasSupport(ItemStack stack, EnumFacing from) {
			return stack.hasCapability(MjAPI.CAP_RECEIVER, from);
		}

	}

	/**
	 * For blocks which provide energy
	 * @author CJMinecraft
	 *
	 */
	public static class BuildCraftProviderSupport implements IEnergySupport<IMjPassiveProvider> {

		@Override
		public long getEnergyStored(IMjPassiveProvider container, EnumFacing from) {
			return 0;
		}

		@Override
		public long getCapacity(IMjPassiveProvider container, EnumFacing from) {
			return 0;
		}

		@Override
		public long giveEnergy(IMjPassiveProvider container, long energy, boolean simulate, EnumFacing from) {
			return 0;
		}

		@Override
		public long takeEnergy(IMjPassiveProvider container, long energy, boolean simulate, EnumFacing from) {
			return container.extractPower(energy * 1000000, energy, simulate) / 1000000;
		}

		@Override
		public boolean canReceive(IMjPassiveProvider container, EnumFacing from) {
			return false;
		}

		@Override
		public boolean canExtract(IMjPassiveProvider container, EnumFacing from) {
			return container.extractPower(10, 10, true) > 0;
		}

		@Override
		public IMjPassiveProvider getContainer(TileEntity te, EnumFacing from) {
			return te.getCapability(MjAPI.CAP_PASSIVE_PROVIDER, from);
		}

		@Override
		public boolean hasSupport(TileEntity te, EnumFacing from) {
			return te.hasCapability(MjAPI.CAP_PASSIVE_PROVIDER, from);
		}

		@Override
		public EnergyUnit defaultEnergyUnit() {
			return EnergyUnits.MINECRAFT_JOULES;
		}

		@Override
		public IMjPassiveProvider getContainer(ItemStack stack, EnumFacing from) {
			return stack.getCapability(MjAPI.CAP_PASSIVE_PROVIDER, from);
		}

		@Override
		public boolean hasSupport(ItemStack stack, EnumFacing from) {
			return stack.hasCapability(MjAPI.CAP_PASSIVE_PROVIDER, from);
		}

	}

}
