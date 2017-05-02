package cofh.api.energy;

import net.minecraft.util.EnumFacing;
import net.minecraft.tileentity.TileEntity;

/**
 * Implement this interface on TileEntities which should connect to energy
 * transportation blocks. This is intended for blocks which generate energy but
 * do not accept it; otherwise just use IEnergyHandler.
 *
 * Note that {@link IEnergyHandler} is an extension of this.
 *
 * @author King Lemming
 */
public interface IEnergyConnection {

	/**
	 * Returns TRUE if the TileEntity can connect on a given side.
	 * 
	 * @param from
	 *            The side
	 * @return if the {@link TileEntity} can connect on a give side
	 */
	boolean canConnectEnergy(EnumFacing from);

}
