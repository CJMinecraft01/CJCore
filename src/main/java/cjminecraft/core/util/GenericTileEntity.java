package cjminecraft.core.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * A normal {@link TileEntity} which automatically should save
 * 
 * @author CJMinecraft
 *
 */
public abstract class GenericTileEntity extends TileEntity {

	/**
	 * Unless the actual block has changed, don't remove the tile entity
	 * when a block update occurs
	 */
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, @Nonnull IBlockState oldState, @Nonnull IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	/**
	 * Should write all the nbt data the client needs but just in case there is
	 * nothing specific use {@link #writeToNBT(NBTTagCompound)}
	 * 
	 * @param nbt The nbt to write client data to
	 */
	public abstract void writeClientDataToNBT(NBTTagCompound nbt);

	/**
	 * Should read all the nbt data the client needs but just in case there is
	 * nothing specific use {@link #readFromNBT(NBTTagCompound)}
	 * 
	 * @param nbt The nbt to read data from
	 */
	public abstract void readClientDataFromNBT(NBTTagCompound nbt);

	/**
	 * Makes sure the client has all the data it needs
	 */
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound nbtTag = new NBTTagCompound();
		this.writeClientDataToNBT(nbtTag);
		return new SPacketUpdateTileEntity(pos, 1, nbtTag);
	}

	/**
	 * Make sure to read the client data when it receives the update packet
	 */
	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readClientDataFromNBT(pkt.getNbtCompound());
	}

	/**
	 * Returns the tag with all of the client data saved
	 */
	@Nonnull
	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound nbt = super.getUpdateTag();
		writeClientDataToNBT(nbt);
		return nbt;
	}
}