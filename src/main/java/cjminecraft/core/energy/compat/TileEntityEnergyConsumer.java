package cjminecraft.core.energy.compat;

import cjminecraft.core.energy.EnergyUnit;
import cjminecraft.core.energy.EnergyUtils;
import cofh.redstoneflux.api.IEnergyReceiver;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergyTile;
import ic2.api.info.Info;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Optional;

/**
 * A {@link TileEntity} which acts as an energy storage. The {@link TileEntity}
 * uses forge energy and to access the storage use <code>this.storage</code>.
 * This is a an energy storage which will consume energy from its surroundings
 * 
 * @author CJMinecraft
 *
 */
@Optional.InterfaceList(value = { @Optional.Interface(iface = "ic2.api.energy.tile.IEnergySink", modid = "ic2"),
		@Optional.Interface(iface = "cofh.redstoneflux.api.IEnergyReceiver", modid = "redstoneflux") })
public class TileEntityEnergyConsumer extends TileEntityEnergy implements IEnergyReceiver, IEnergySink {

	private Object teslaWrapper;
	private Object buildCraftWrapper;

	/**
	 * Create an energy storage
	 * 
	 * @param capacity
	 *            The capacity of the energy storage
	 */
	public TileEntityEnergyConsumer(long capacity) {
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
	public TileEntityEnergyConsumer(long capacity, long maxTransfer) {
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
	public TileEntityEnergyConsumer(long capacity, long maxReceive, long maxExtract) {
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
	public TileEntityEnergyConsumer(long capacity, long maxReceive, long maxExtract, long energy) {
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
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		return (int) this.storage.receiveEnergy(maxReceive, simulate);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (EnergyUtils.TESLA_LOADED
				&& (capability == EnergyUtils.TESLA_CONSUMER || capability == EnergyUtils.TESLA_HOLDER))
			return true;
		if (EnergyUtils.BUILDCRAFT_LOADED
				&& (capability == EnergyUtils.BUILDCRAFT_RECEIVER || capability == EnergyUtils.BUILDCRAFT_READABLE))
			return true;
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (EnergyUtils.TESLA_LOADED
				&& (capability == EnergyUtils.TESLA_CONSUMER || capability == EnergyUtils.TESLA_HOLDER)) {
			if (this.teslaWrapper == null)
				this.teslaWrapper = new TeslaWrapper(this.storage);
			return (T) this.teslaWrapper;
		}
		if (EnergyUtils.BUILDCRAFT_LOADED
				&& (capability == EnergyUtils.BUILDCRAFT_RECEIVER || capability == EnergyUtils.BUILDCRAFT_READABLE)) {
			if (this.buildCraftWrapper == null)
				this.buildCraftWrapper = new BuildCraftWrapper(this.storage);
			return (T) this.buildCraftWrapper;
		}
		return super.getCapability(capability, facing);
	}

	/**
	 * Determine if this acceptor can accept current from an adjacent emitter in
	 * a direction.
	 *
	 * The TileEntity in the emitter parameter is what was originally added to
	 * the energy net, which may be normal in-world TileEntity, a delegate or an
	 * IMetaDelegate.
	 *
	 * @param emitter
	 *            energy emitter, may also be null or an IMetaDelegate
	 * @param side
	 *            side the energy is being received from
	 */
	@Override
	@Optional.Method(modid = "ic2")
	public boolean acceptsEnergyFrom(IEnergyEmitter emitter, EnumFacing side) {
		return true;
	}

	/**
	 * Determine how much energy the sink accepts.
	 *
	 * Make sure that injectEnergy() does accepts energy if demandsEnergy()
	 * returns anything greater than 0.
	 * 
	 * <i>Modifying the energy net from this method is disallowed.</i>
	 *
	 * @return max accepted input in eu
	 */
	@Override
	@Optional.Method(modid = "ic2")
	public double getDemandedEnergy() {
		return EnergyUtils.convertEnergy(EnergyUnit.FORGE_ENERGY, EnergyUnit.ENERGY_UNIT,
				this.storage.getMaxReceive());
	}

	/**
	 * Determine the tier of this energy sink. 1 = LV, 2 = MV, 3 = HV, 4 = EV
	 * etc.
	 * 
	 * <i>Modifying the energy net from this method is disallowed. Return
	 * Integer.MAX_VALUE to allow any voltage.</i>
	 *
	 * @return tier of this energy sink
	 */
	@Override
	@Optional.Method(modid = "ic2")
	public int getSinkTier() {
		return ((int) (Math.log(getDemandedEnergy()) / Math.log(2)) - 3) / 2;
	}

	/**
	 * Transfer energy to the sink.
	 * 
	 * It's highly recommended to accept all energy by letting the internal
	 * buffer overflow to increase the performance and accuracy of the
	 * distribution simulation.
	 *
	 * @param directionFrom
	 *            direction from which the energy comes from
	 * @param amount
	 *            energy to be transferred
	 * @param voltage
	 *            The voltage of the energy
	 * @return Energy not consumed (leftover)
	 */
	@Override
	@Optional.Method(modid = "ic2")
	public double injectEnergy(EnumFacing directionFrom, double amount, double voltage) {
		return Math.abs(EnergyUtils.convertEnergy(EnergyUnit.FORGE_ENERGY, EnergyUnit.ENERGY_UNIT,
				this.storage.receiveEnergy(
						(int) EnergyUtils.convertEnergy(EnergyUnit.ENERGY_UNIT, EnergyUnit.FORGE_ENERGY, amount),
						false))
				- amount);
	}

}
