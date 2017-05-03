package cjminecraft.core.proxy;

import cjminecraft.core.init.CJCoreEvents;
import cjminecraft.core.network.PacketHandler;
import net.minecraftforge.common.MinecraftForge;

/**
 * For all things common
 * @author CJMinecraft
 *
 */
public class CommonProxy implements IProxy {

	@Override
	public void preInit() {
		PacketHandler.registerMessages("cjcore");
		MinecraftForge.EVENT_BUS.register(new CJCoreEvents());
	}

	@Override
	public void init() {
	}

	@Override
	public void postInit() {
	}

}
