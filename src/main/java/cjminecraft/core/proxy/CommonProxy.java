package cjminecraft.core.proxy;

import cjminecraft.core.network.PacketHandler;

/**
 * For all things common
 * @author CJMinecraft
 *
 */
public class CommonProxy {

	public void preInit() {
		PacketHandler.registerMessages("cjcore");
	}

	public void init() {
	}

	public void postInit() {
	}

}
