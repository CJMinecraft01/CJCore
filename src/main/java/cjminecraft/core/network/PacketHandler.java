package cjminecraft.core.network;

import cjminecraft.core.CJCore;
import cjminecraft.core.network.energy.*;
import cjminecraft.core.network.inventory.*;
import cjminecraft.core.proxy.CommonProxy;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Handler of all the packets
 * 
 * @author CJMinecraft
 *
 */
public class PacketHandler {

	/**
	 * The instance where all {@link CJCore} packets should be sent from
	 */
	public static SimpleNetworkWrapper INSTANCE;
	private static int ID = 0;

	/**
	 * Get the next id
	 * 
	 * @return the next id
	 */
	private static int nextID() {
		return ID++;
	}

	/**
	 * For use in the {@link CommonProxy} only
	 * 
	 * @param channelName
	 *            The name of the channel
	 */
	public static void registerMessages(String channelName) {
		INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(channelName);

		// Server Messages
		INSTANCE.registerMessage(PacketGetEnergy.Handler.class, PacketGetEnergy.class, nextID(), Side.SERVER);
		INSTANCE.registerMessage(PacketGetCapacity.Handler.class, PacketGetCapacity.class, nextID(), Side.SERVER);
		INSTANCE.registerMessage(PacketGetEnergyData.Handler.class, PacketGetEnergyData.class, nextID(), Side.SERVER);
		INSTANCE.registerMessage(PacketGetInventory.Handler.class, PacketGetInventory.class, nextID(), Side.SERVER);

		// Client Messages
		INSTANCE.registerMessage(PacketReturnEnergy.Handler.class, PacketReturnEnergy.class, nextID(), Side.CLIENT);
		INSTANCE.registerMessage(PacketReturnCapacity.Handler.class, PacketReturnCapacity.class, nextID(), Side.CLIENT);
		INSTANCE.registerMessage(PacketReturnEnergyData.Handler.class, PacketReturnEnergyData.class, nextID(),
				Side.CLIENT);
		INSTANCE.registerMessage(PacketReturnInventory.Handler.class, PacketReturnInventory.class, nextID(),
				Side.CLIENT);
	}

}
