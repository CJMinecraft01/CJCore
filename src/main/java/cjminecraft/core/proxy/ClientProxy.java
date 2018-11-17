package cjminecraft.core.proxy;

import cjminecraft.core.util.registries.AutomaticRegistrar;
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
		AutomaticRegistrar.registerModels();
	}
	
	@Override
	public void init() {
		super.init();
	}
	
	@Override
	public void postInit() {
		super.postInit();
	}

}
