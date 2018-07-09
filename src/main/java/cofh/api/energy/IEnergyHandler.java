package cofh.api.energy;

import net.minecraft.util.EnumFacing;

/**
 * Implement this interface on Tile Entities which should handle energy,
 * generally storing it in one or more internal {@link IEnergyStorage} objects.
 *
 * A reference implementation is provided {@link TileEnergyHandler}.
 *
 * Note that {@link IEnergyReceiver} and {@link IEnergyProvider} are extensions
 * of this.
 *
 * @author King Lemming
 */
public interface IEnergyHandler extends IEnergyConnection {

	/**
	 * Returns the amount of energy currently stored.
	 * 
	 * @param from
	 *            The side
	 * @return The energy
	 */
	int getEnergyStored(EnumFacing from);

	/**
	 * Returns the maximum amount of energy that can be stored.
	 * 
	 * @param from
	 *            The side
	 * @return The energy
	 */
	int getMaxEnergyStored(EnumFacing from);

}
