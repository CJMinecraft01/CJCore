package cjminecraft.core.energy.support;

import cjminecraft.core.energy.EnergyUnits;
import cjminecraft.core.energy.EnergyUnits.EnergyUnit;
import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.api.ITeslaProducer;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

/**
 * Class which handles all of the support regarding the Tesla API
 * 
 * @author CJMinecraft
 *
 */
public class TeslaSupport {

	/**
	 * Handles instances of {@link ITeslaHolder}
	 * 
	 * @author CJMinecraft
	 *
	 */
	public static class TeslaHolderSupport implements IEnergySupport<ITeslaHolder> {

		@Override
		public long getEnergyStored(ITeslaHolder container, EnumFacing from) {
			return container.getStoredPower();
		}

		@Override
		public long getCapacity(ITeslaHolder container, EnumFacing from) {
			return container.getCapacity();
		}

		@Override
		public long giveEnergy(ITeslaHolder container, long energy, boolean simulate, EnumFacing from) {
			return 0;
		}

		@Override
		public long takeEnergy(ITeslaHolder container, long energy, boolean simulate, EnumFacing from) {
			return 0;
		}

		@Override
		public boolean canReceive(ITeslaHolder container, EnumFacing from) {
			return false;
		}

		@Override
		public boolean canExtract(ITeslaHolder container, EnumFacing from) {
			return false;
		}

		@Override
		public ITeslaHolder getContainer(TileEntity te, EnumFacing from) {
			return te.getCapability(TeslaCapabilities.CAPABILITY_HOLDER, from);
		}

		@Override
		public boolean hasSupport(TileEntity te, EnumFacing from) {
			return te.hasCapability(TeslaCapabilities.CAPABILITY_HOLDER, from);
		}

		@Override
		public EnergyUnit defaultEnergyUnit() {
			return EnergyUnits.TESLA;
		}

		@Override
		public ITeslaHolder getContainer(ItemStack stack, EnumFacing from) {
			return stack.getCapability(TeslaCapabilities.CAPABILITY_HOLDER, from);
		}

		@Override
		public boolean hasSupport(ItemStack stack, EnumFacing from) {
			return stack.hasCapability(TeslaCapabilities.CAPABILITY_HOLDER, from);
		}

	}

	/**
	 * Handles instances of {@link ITeslaConsumer}
	 * 
	 * @author CJMinecraft
	 *
	 */
	public static class TeslaConsumerSupport implements IEnergySupport<ITeslaConsumer> {

		@Override
		public long getEnergyStored(ITeslaConsumer container, EnumFacing from) {
			return 0;
		}

		@Override
		public long getCapacity(ITeslaConsumer container, EnumFacing from) {
			return 0;
		}

		@Override
		public long giveEnergy(ITeslaConsumer container, long energy, boolean simulate, EnumFacing from) {
			return container.givePower(energy, simulate);
		}

		@Override
		public long takeEnergy(ITeslaConsumer container, long energy, boolean simulate, EnumFacing from) {
			return 0;
		}

		@Override
		public boolean canReceive(ITeslaConsumer container, EnumFacing from) {
			return container.givePower(10, true) > 0;
		}

		@Override
		public boolean canExtract(ITeslaConsumer container, EnumFacing from) {
			return false;
		}

		@Override
		public ITeslaConsumer getContainer(TileEntity te, EnumFacing from) {
			return te.getCapability(TeslaCapabilities.CAPABILITY_CONSUMER, from);
		}

		@Override
		public boolean hasSupport(TileEntity te, EnumFacing from) {
			return te.hasCapability(TeslaCapabilities.CAPABILITY_CONSUMER, from);
		}

		@Override
		public EnergyUnit defaultEnergyUnit() {
			return EnergyUnits.TESLA;
		}

		@Override
		public ITeslaConsumer getContainer(ItemStack stack, EnumFacing from) {
			return stack.getCapability(TeslaCapabilities.CAPABILITY_CONSUMER, from);
		}

		@Override
		public boolean hasSupport(ItemStack stack, EnumFacing from) {
			return stack.hasCapability(TeslaCapabilities.CAPABILITY_CONSUMER, from);
		}

	}

	/**
	 * Handles instances of {@link ITeslaProducer}
	 * 
	 * @author CJMinecraft
	 *
	 */
	public static class TeslaProducerSupport implements IEnergySupport<ITeslaProducer> {

		@Override
		public long getEnergyStored(ITeslaProducer container, EnumFacing from) {
			return 0;
		}

		@Override
		public long getCapacity(ITeslaProducer container, EnumFacing from) {
			return 0;
		}

		@Override
		public long giveEnergy(ITeslaProducer container, long energy, boolean simulate, EnumFacing from) {
			return 0;
		}

		@Override
		public long takeEnergy(ITeslaProducer container, long energy, boolean simulate, EnumFacing from) {
			return container.takePower(energy, simulate);
		}

		@Override
		public boolean canReceive(ITeslaProducer container, EnumFacing from) {
			return false;
		}

		@Override
		public boolean canExtract(ITeslaProducer container, EnumFacing from) {
			return container.takePower(10, true) > 0;
		}

		@Override
		public ITeslaProducer getContainer(TileEntity te, EnumFacing from) {
			return te.getCapability(TeslaCapabilities.CAPABILITY_PRODUCER, from);
		}

		@Override
		public boolean hasSupport(TileEntity te, EnumFacing from) {
			return te.hasCapability(TeslaCapabilities.CAPABILITY_PRODUCER, from);
		}

		@Override
		public EnergyUnit defaultEnergyUnit() {
			return EnergyUnits.TESLA;
		}

		@Override
		public ITeslaProducer getContainer(ItemStack stack, EnumFacing from) {
			return stack.getCapability(TeslaCapabilities.CAPABILITY_PRODUCER, from);
		}

		@Override
		public boolean hasSupport(ItemStack stack, EnumFacing from) {
			return stack.hasCapability(TeslaCapabilities.CAPABILITY_PRODUCER, from);
		}

	}

}
