package cjminecraft.core.proxy;

import cjminecraft.core.config.CJCoreConfig2;
import cjminecraft.core.energy.EnergyUnits;
import cjminecraft.core.init.CJCoreItems;
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
		CJCoreConfig2.clientPreInit();
		CJCoreItems.registerRenders();
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
