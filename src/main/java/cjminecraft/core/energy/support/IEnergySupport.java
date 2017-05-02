package cjminecraft.core.energy.support;

import cjminecraft.core.energy.EnergyUnits;
import cjminecraft.core.energy.EnergyUnits.EnergyUnit;
import cjminecraft.core.energy.EnergyUtils;
import cofh.api.energy.IEnergyProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

/**
 * Used by {@link EnergyUtils} to allow integration between different forms of
 * energy storage
 * 
 * @author CJMinecraft
 *
 * @param <I>
 *            The interface which represents the container For example
 *            {@link IEnergyProvider} for support with CoFH energy providers See
 *            {@link CoFHSupport}
 */
public interface IEnergySupport<I> {

	/**
	 * Returns how much energy is inside of the energy container
	 * 
	 * @param container
	 *            The container which holds the energy
	 * @param from
	 *            The side in which the container is acting on (For use with
	 *            {@link Capability})
	 * @return The amount of energy inside the container in the energy unit of
	 *         the container The unit can received by calling
	 *         {@link #defaultEnergyUnit()}
	 */
	long getEnergyStored(I container, EnumFacing from);

	/**
	 * Returns how much energy can be stored inside of the energy container
	 * 
	 * @param container
	 *            The container which holds energy
	 * @param from
	 *            The side in which the container is acting on (For use with
	 *            {@link Capability})
	 * @return The maximum amount of energy the container can hold in the energy
	 *         unit of the container. The unit can be received by calling
	 *         {@link #defaultEnergyUnit()}
	 */
	long getCapacity(I container, EnumFacing from);

	/**
	 * Gives energy to the container. Energy must be in the unit of the
	 * container. The unit can be received by calling
	 * {@link #defaultEnergyUnit()}
	 * 
	 * @param container
	 *            The container which will receive the energy
	 * @param energy
	 *            The energy to be given in the unit of the container. The unit
	 *            can be received by calling {@link #defaultEnergyUnit()}
	 * @param simulate
	 *            Whether this is a simulation. If so then the energy will not
	 *            actually be given to the container
	 * @param from
	 *            The side which the container is acting on (For use with
	 *            {@link Capability}
	 * @return The amount of energy that would have been given (if a simulation)
	 *         or actually given (if not a simulation) to the container in the
	 *         unit of the container. The unit can be received by calling
	 *         {@link #defaultEnergyUnit()}
	 */
	long giveEnergy(I container, long energy, boolean simulate, EnumFacing from);

	/**
	 * Takes energy from the container. Energy must be in the unit of the
	 * container. The unit can be received by calling
	 * {@link #defaultEnergyUnit()}
	 * 
	 * @param container
	 *            The container which will have the energy taken out of
	 * @param energy
	 *            The energy to take in the unit of the container. The unit can
	 *            be received by calling {@link #defaultEnergyUnit()}
	 * @param simulate
	 *            Whether this is a simulation. If so the energy will not
	 *            actually be taken from the container
	 * @param from
	 *            The side which the container is acting on (For use with
	 *            {@link Capability})
	 * @return The amount of energy which would have been taken (if a
	 *         simulation) or actually was taken (if not a simulation) from the
	 *         container in the unit of the container. The unit can be received
	 *         by calling {@link #defaultEnergyUnit()}
	 */
	long takeEnergy(I container, long energy, boolean simulate, EnumFacing from);

	/**
	 * States whether the container has the ability to receive energy
	 * 
	 * @param container
	 *            The container which handles the energy
	 * @param from
	 *            The side in which the container is acting on (For use with
	 *            {@link Capability})
	 * @return Whether the container has the ability to receive energy
	 */
	boolean canReceive(I container, EnumFacing from);

	/**
	 * States whether the container has the ability to extract energy
	 * 
	 * @param container
	 *            The container which handles the energy
	 * @param from
	 *            The side in which the container is acting on (For use with
	 *            {@link Capability})
	 * @return Whether the container has the ability to extract energy
	 */
	boolean canExtract(I container, EnumFacing from);

	/**
	 * For use for most methods which require a container argument
	 * 
	 * @param te
	 *            The {@link TileEntity} to get the container from. Make sure it
	 *            {@link #hasSupport(TileEntity, EnumFacing)}
	 * @param from
	 *            The side in which the container is acting on (For use with
	 *            {@link Capability})
	 * @return The container which represents the handler for the energy
	 */
	I getContainer(TileEntity te, EnumFacing from);
	
	/**
	 * For use for most methods which require a container argument
	 * 
	 * @param stack
	 *            The {@link ItemStack} to get the container from. Make sure it
	 *            {@link #hasSupport(TileEntity, EnumFacing)}
	 * @param from
	 *            The side in which the container is acting on (For use with
	 *            {@link Capability})
	 * @return The container which represents the handler for the energy
	 */
	I getContainer(ItemStack stack, EnumFacing from);

	/**
	 * States whether this {@link IEnergySupport} will work on a specific
	 * {@link TileEntity}
	 * 
	 * @param te
	 *            The {@link TileEntity} to check
	 * @param from
	 *            The side in which the container is acting on (For use with
	 *            {@link Capability})
	 * @return Whether the {@link TileEntity} has support with this energy
	 *         container
	 */
	boolean hasSupport(TileEntity te, EnumFacing from);
	
	/**
	 * States whether this {@link IEnergySupport} will work on a specific
	 * {@link ItemStack}
	 * 
	 * @param stack
	 *            The {@link ItemStack} to check
	 * @param from
	 *            The side in which the container is acting on (For use with
	 *            {@link Capability})
	 * @return Whether the {@link ItemStack} has support with this energy
	 *         container
	 */
	boolean hasSupport(ItemStack stack, EnumFacing from);

	/**
	 * The default {@link EnergyUnit} for the container
	 * 
	 * @return The default {@link EnergyUnit} for the container. See
	 *         {@link EnergyUnits} for how to add your own unit or get a pre
	 *         made one
	 */
	EnergyUnit defaultEnergyUnit();

}
