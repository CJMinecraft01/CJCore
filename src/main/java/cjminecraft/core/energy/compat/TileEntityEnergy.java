package cjminecraft.core.energy.compat;

import cjminecraft.core.energy.EnergyUtils;
import cjminecraft.core.util.TileEntityBase;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyTile;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Optional;

/**
 * A simple {@link TileEntity} which can hold energy. This uses forge energy. To
 * access the energy storage, use <code>this.storage</code>
 * 
 * @author CJMinecraft
 *
 */
@Optional.InterfaceList(value = { @Optional.Interface(iface = "ic2.api.energy.tile.IEnergyTile", modid = "ic2") })
public class TileEntityEnergy extends TileEntityBase implements IEnergyTile {

	protected EnergyStorage storage;
	private Object forgeWrapper;

	/**
	 * Create an energy storage
	 * 
	 * @param capacity
	 *            The capacity of the energy storage
	 */
	public TileEntityEnergy(long capacity) {
		this(capacity, capacity, capacity, 0);
	}

	/**
	 * Create an energy storage
	 * 
	 * @param capacity
	 *            The capacity of the energy storage
	 * @param maxTransfer
	 *            The max receive and max extract of the energy storage
	 */
	public TileEntityEnergy(long capacity, long maxTransfer) {
		this(capacity, maxTransfer, maxTransfer, 0);
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
	public TileEntityEnergy(long capacity, long maxReceive, long maxExtract) {
		this(capacity, maxReceive, maxExtract, 0);
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
	public TileEntityEnergy(long capacity, long maxReceive, long maxExtract, long energy) {
		this.storage = new EnergyStorage(capacity, maxReceive, maxExtract, energy);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		this.storage.readFromNBT(nbt);
		super.readFromNBT(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		this.storage.writeToNBT(nbt);
		return super.writeToNBT(nbt);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (capability == CapabilityEnergy.ENERGY)
			return true;
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityEnergy.ENERGY) {
			if(this.forgeWrapper == null)
				this.forgeWrapper = new ForgeEnergyWrapper(this.storage);
			return (T) this.forgeWrapper;
		}
		return super.getCapability(capability, facing);
	}
	
	private boolean addedToEnet;
	
	@Override
	@Optional.Method(modid = "ic2")
	public void onLoad() {
		if(!this.addedToEnet && !FMLCommonHandler.instance().getEffectiveSide().isClient() && EnergyUtils.INDUSTRAIL_CRAFT_LOADED) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
			this.addedToEnet = true;
		}
	}
	
	@Override
	@Optional.Method(modid = "ic2")
	public void invalidate() {
		super.invalidate();
		onChunkUnload();
	}
	
	@Override
	@Optional.Method(modid = "ic2")
	public void onChunkUnload() {
		super.onChunkUnload();
		if(this.addedToEnet && EnergyUtils.INDUSTRAIL_CRAFT_LOADED) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
			this.addedToEnet = false;
		}
	}

}
