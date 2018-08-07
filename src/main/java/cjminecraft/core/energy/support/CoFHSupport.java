package cjminecraft.core.energy.support;

import cjminecraft.core.energy.EnergyUnit;
import cofh.redstoneflux.api.IEnergyHandler;
import cofh.redstoneflux.api.IEnergyProvider;
import cofh.redstoneflux.api.IEnergyReceiver;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

/**
 * Class which handles all of the support regarding the CoFH Redstone Flux API
 * 
 * @author CJMinecraft
 *
 */
public class CoFHSupport {

	/**
	 * Handles instances of {@link IEnergyHandler}
	 * 
	 * @author CJMinecraft
	 *
	 */
	public static class CoFHHolderSupport implements IEnergySupport<IEnergyHandler> {

		@Override
		public long getEnergyStored(IEnergyHandler container, EnumFacing from) {
			return container.getEnergyStored(from);
		}

		@Override
		public long getCapacity(IEnergyHandler container, EnumFacing from) {
			return container.getMaxEnergyStored(from);
		}

		@Override
		public long giveEnergy(IEnergyHandler container, long energy, boolean simulate, EnumFacing from) {
			return 0;
		}

		@Override
		public long takeEnergy(IEnergyHandler container, long energy, boolean simulate, EnumFacing from) {
			return 0;
		}

		@Override
		public boolean canReceive(IEnergyHandler container, EnumFacing from) {
			return false;
		}

		@Override
		public boolean canExtract(IEnergyHandler container, EnumFacing from) {
			return false;
		}

		@Override
		public IEnergyHandler getContainer(TileEntity te, EnumFacing from) {
			return (IEnergyHandler) te;
		}

		@Override
		public boolean hasSupport(TileEntity te, EnumFacing from) {
			return te instanceof IEnergyHandler;
		}

		@Override
		public EnergyUnit defaultEnergyUnit() {
			return EnergyUnit.REDSTONE_FLUX;
		}

		@Override
		public IEnergyHandler getContainer(ItemStack stack, EnumFacing from) {
			return (IEnergyHandler) stack.getItem();
		}

		@Override
		public boolean hasSupport(ItemStack stack, EnumFacing from) {
			return stack.getItem() instanceof IEnergyHandler;
		}

	}

	/**
	 * Handles instances of {@link IEnergyProvider}
	 * 
	 * @author CJMinecraft
	 *
	 */
	public static class CoFHProviderSupport implements IEnergySupport<IEnergyProvider> {

		@Override
		public long getEnergyStored(IEnergyProvider container, EnumFacing from) {
			return container.getEnergyStored(from);
		}

		@Override
		public long getCapacity(IEnergyProvider container, EnumFacing from) {
			return container.getMaxEnergyStored(from);
		}

		@Override
		public long giveEnergy(IEnergyProvider container, long energy, boolean simulate, EnumFacing from) {
			return 0;
		}

		@Override
		public long takeEnergy(IEnergyProvider container, long energy, boolean simulate, EnumFacing from) {
			return container.extractEnergy(from, (int) energy, simulate);
		}

		@Override
		public boolean canReceive(IEnergyProvider container, EnumFacing from) {
			return false;
		}

		@Override
		public boolean canExtract(IEnergyProvider container, EnumFacing from) {
			return container.extractEnergy(from, 10, true) > 0;
		}

		@Override
		public IEnergyProvider getContainer(TileEntity te, EnumFacing from) {
			return (IEnergyProvider) te;
		}

		@Override
		public boolean hasSupport(TileEntity te, EnumFacing from) {
			return te instanceof IEnergyProvider;
		}

		@Override
		public EnergyUnit defaultEnergyUnit() {
			return EnergyUnit.REDSTONE_FLUX;
		}

		@Override
		public IEnergyProvider getContainer(ItemStack stack, EnumFacing from) {
			return (IEnergyProvider) stack.getItem();
		}

		@Override
		public boolean hasSupport(ItemStack stack, EnumFacing from) {
			return stack.getItem() instanceof IEnergyProvider;
		}

	}

	/**
	 * Handles instances of {@link IEnergyReceiver}
	 * 
	 * @author CJMinecraft
	 *
	 */
	public static class CoFHReceiverSupport implements IEnergySupport<IEnergyReceiver> {

		@Override
		public long getEnergyStored(IEnergyReceiver container, EnumFacing from) {
			return container.getEnergyStored(from);
		}

		@Override
		public long getCapacity(IEnergyReceiver container, EnumFacing from) {
			return container.getMaxEnergyStored(from);
		}

		@Override
		public long giveEnergy(IEnergyReceiver container, long energy, boolean simulate, EnumFacing from) {
			return container.receiveEnergy(from, (int) energy, simulate);
		}

		@Override
		public long takeEnergy(IEnergyReceiver container, long energy, boolean simulate, EnumFacing from) {
			return 0;
		}

		@Override
		public boolean canReceive(IEnergyReceiver container, EnumFacing from) {
			return container.receiveEnergy(from, 10, true) > 0;
		}

		@Override
		public boolean canExtract(IEnergyReceiver container, EnumFacing from) {
			return false;
		}

		@Override
		public IEnergyReceiver getContainer(TileEntity te, EnumFacing from) {
			return (IEnergyReceiver) te;
		}

		@Override
		public boolean hasSupport(TileEntity te, EnumFacing from) {
			return te instanceof IEnergyReceiver;
		}

		@Override
		public EnergyUnit defaultEnergyUnit() {
			return EnergyUnit.REDSTONE_FLUX;
		}

		@Override
		public IEnergyReceiver getContainer(ItemStack stack, EnumFacing from) {
			return (IEnergyReceiver) stack.getItem();
		}

		@Override
		public boolean hasSupport(ItemStack stack, EnumFacing from) {
			return stack.getItem() instanceof IEnergyReceiver;
		}

	}

}
