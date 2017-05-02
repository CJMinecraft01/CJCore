package cjminecraft.core.proxy;

import cjminecraft.core.network.PacketHandler;

/**
 * For all things common
 * @author CJMinecraft
 *
 */
public class CommonProxy implements IProxy {

	@Override
	public void preInit() {
		PacketHandler.registerMessages("cjcore");
	}

	@Override
	public void init() {
	}

	@Override
	public void postInit() {
	}

}
