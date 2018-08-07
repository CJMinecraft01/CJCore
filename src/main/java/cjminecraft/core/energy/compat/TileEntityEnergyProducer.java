package cjminecraft.core.energy.compat;

import cjminecraft.core.energy.EnergyUtils;
import cofh.redstoneflux.api.IEnergyProvider;
import cjminecraft.core.energy.EnergyUnit;
import cjminecraft.core.energy.EnergyUtils;
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.energy.tile.IEnergyTile;
import ic2.api.energy.tile.IMultiEnergySource;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.Optional;

/**
 * A {@link TileEntity} which acts as an energy storage. The {@link TileEntity}
 * uses forge energy and to access the storage use <code>this.storage</code>.
 * This is a an energy storage which will generate energy and transfer this to
 * its surroundings
 * 
 * @author CJMinecraft
 *
 */
@Optional.InterfaceList(value = { @Optional.Interface(iface = "ic2.api.energy.tile.IEnergySource", modid = "ic2"),
		@Optional.Interface(iface = "cofh.redstoneflux.api.IEnergyProvider", modid = "redstoneflux") })
public class TileEntityEnergyProducer extends TileEntityEnergy implements IEnergyProvider, IEnergySource {

	private Object teslaWrapper;
	private Object buildCraftWrapper;

	/**
	 * Create an energy storage
	 * 
	 * @param capacity
	 *            The capacity of the energy storage
	 */
	public TileEntityEnergyProducer(long capacity) {
		super(capacity, capacity, capacity, 0);
	}

	/**
	 * Create an energy storage
	 * 
	 * @param capacity
	 *            The capacity of the energy storage
	 * @param maxTransfer
	 *            The max receive and max extract of the energy storage
	 */
	public TileEntityEnergyProducer(long capacity, long maxTransfer) {
		super(capacity, maxTransfer, maxTransfer, 0);
	}

	/**
	 * Create an energy storage
	 * 
	 * @param capacity
	 *            The capacity of the energy storage
	 * @param maxReceive
	 *            The maximum amount of energy which can be received
	 * @param maxExtract
	 *            The maximum amount of energy which can be extracted
	 */
	public TileEntityEnergyProducer(long capacity, long maxReceive, long maxExtract) {
		super(capacity, maxReceive, maxExtract, 0);
	}

	/**
	 * Create an energy storage
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
	public TileEntityEnergyProducer(long capacity, long maxReceive, long maxExtract, long energy) {
		super(capacity, maxReceive, maxExtract, energy);
	}

	@Override
	public int getEnergyStored(EnumFacing from) {
		return (int) this.storage.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		return (int) this.storage.getMaxEnergyStored();
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return true;
	}

	@Override
	public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
		return (int) this.storage.extractEnergy(maxExtract, simulate);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (EnergyUtils.TESLA_LOADED
				&& (capability == EnergyUtils.TESLA_PRODUCER || capability == EnergyUtils.TESLA_HOLDER))
			return true;
		if (EnergyUtils.BUILDCRAFT_LOADED && (capability == EnergyUtils.BUILDCRAFT_PASSIVE_PROVIDER
				|| capability == EnergyUtils.BUILDCRAFT_READABLE))
			return true;
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (EnergyUtils.TESLA_LOADED
				&& (capability == EnergyUtils.TESLA_PRODUCER || capability == EnergyUtils.TESLA_HOLDER)) {
			if (this.teslaWrapper == null)
				this.teslaWrapper = new TeslaWrapper(this.storage);
			return (T) this.teslaWrapper;
		}
		if (EnergyUtils.BUILDCRAFT_LOADED && (capability == EnergyUtils.BUILDCRAFT_PASSIVE_PROVIDER
				|| capability == EnergyUtils.BUILDCRAFT_READABLE)) {
			if (this.buildCraftWrapper == null)
				this.buildCraftWrapper = new BuildCraftWrapper(this.storage);
			return (T) this.buildCraftWrapper;
		}
		return super.getCapability(capability, facing);
	}

	/**
	 * Determine if this emitter can emit energy to an adjacent receiver.
	 *
	 * The TileEntity in the receiver parameter is what was originally added to
	 * the energy net, which may be normal in-world TileEntity, a delegate or an
	 * IMetaDelegate.
	 *
	 * @param receiver
	 *            receiver, may also be null or an IMetaDelegate
	 * @param side
	 *            side the energy is to be sent to
	 * @return Whether energy should be emitted
	 */
	@Override
	@Optional.Method(modid = "ic2")
	public boolean emitsEnergyTo(IEnergyAcceptor receiver, EnumFacing side) {
		return true;
	}

	/**
	 * Maximum energy output provided by the source this tick, typically the
	 * stored energy.
	 *
	 * <p>
	 * The value will be limited to the source tier's power multiplied with the
	 * packet count (see {@link IMultiEnergySource}, default 1).
	 *
	 * <i>Modifying the energy net from this method is disallowed.</i>
	 *
	 * @return Energy offered this tick
	 */
	@Override
	@Optional.Method(modid = "ic2")
	public double getOfferedEnergy() {
		return EnergyUtils.convertEnergy(EnergyUnit.FORGE_ENERGY, EnergyUnit.ENERGY_UNIT,
				this.storage.getMaxExtract());
	}

	/**
	 * Draw energy from this source's buffer.
	 *
	 * <p>
	 * If the source doesn't have a buffer, this may be a no-op.
	 *
	 * @param amount
	 *            amount of EU to draw, may be negative
	 */
	@Override
	@Optional.Method(modid = "ic2")
	public void drawEnergy(double amount) {
		this.storage.extractEnergy(
				(int) EnergyUtils.convertEnergy(EnergyUnit.ENERGY_UNIT, EnergyUnit.FORGE_ENERGY, amount), false);
	}

	/**
	 * Determine the tier of this energy source. 1 = LV, 2 = MV, 3 = HV, 4 = EV
	 * etc.
	 *
	 * <i>Modifying the energy net from this method is disallowed.</i>
	 *
	 * @return tier of this energy source
	 */
	@Override
	@Optional.Method(modid = "ic2")
	public int getSourceTier() {
		return ((int) (Math.log(getOfferedEnergy()) / Math.log(2)) - 3) / 2;
	}

}
