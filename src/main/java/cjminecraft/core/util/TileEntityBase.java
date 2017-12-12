package cjminecraft.core.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * A normal {@link TileEntity} which automatically should save
 * 
 * @author CJMinecraft
 *
 */
public class TileEntityBase extends TileEntity {

	/**
	 * Unless the actual block has changed, don't remove the tile entity when a
	 * block update occurs
	 */
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	/**
	 * Should write all the nbt data the client needs but just incase there is
	 * nothing specific use {@link #writeToNBT(NBTTagCompound)}
	 * 
	 * @param nbt The nbt to write client data to
	 */
	public void writeClientDataToNBT(NBTTagCompound nbt) {
	}

	/**
	 * Should read all the nbt data the client needs but just incase there is
	 * nothing specific use {@link #readFromNBT(NBTTagCompound)}
	 * 
	 * @param nbt The nbt to read data from
	 */
	public void readClientDataFromNBT(NBTTagCompound nbt) {
	}

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
	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound nbt = super.getUpdateTag();
		writeClientDataToNBT(nbt);
		return nbt;
	}
	
	/**
	 * Handles when you get an update
	 */
	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		readClientDataFromNBT(tag);
	}
	
	/**
	 * Gets the tile entities nbt with all of the data stored in it
	 */
	@Override
	public NBTTagCompound getTileData() {
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeToNBT(nbt);
		return nbt;
	}
	
	/**
	 * Says whether the player can interact with the block
	 * 
	 * @param player
	 *            The player to test
	 * @return If the player can interact with the block
	 */
	public boolean isUseableByPlayer(EntityPlayer player) {
		return this.world.getTileEntity(this.getPos()) == this
				&& player.getDistanceSq(this.pos.add(0.5, 0.5, 0.5)) <= 64;
	}

}
