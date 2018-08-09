package cjminecraft.core.proxy;

import cjminecraft.core.items.ItemMultimeter;
import net.minecraftforge.common.MinecraftForge;

/**
 * For all things client
 * @author CJMinecraft
 *
 */
public class ClientProxy extends CommonProxy {
	
	@Override
	public void preInit() {
		super.preInit();
	}
	
	@Override
	public void init() {
		super.init();
		MinecraftForge.EVENT_BUS.register(new ItemMultimeter.MultimeterOverlay());
	}
	
	@Override
	public void postInit() {
		super.postInit();
	}

}
