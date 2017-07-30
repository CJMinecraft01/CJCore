package cjminecraft.core.proxy;

import cjminecraft.core.config.CJCoreConfig;
import cjminecraft.core.init.CJCoreItems;
import cjminecraft.core.items.ItemMultimeter;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;

/**
 * For all things client
 * @author CJMinecraft
 *
 */
public class ClientProxy extends CommonProxy {
	
	@Override
	public void preInit() {
		super.preInit();
		CJCoreConfig.clientPreInit();
	}

	@Override
	public void onItemRegister(RegistryEvent.Register<Item> register) {
		super.onItemRegister(register);
		CJCoreItems.registerRenders();
	}

	@Override
	public void init() {
		super.init();
	}
	
	@Override
	public void postInit() {
		super.postInit();
		MinecraftForge.EVENT_BUS.register(new ItemMultimeter.MultimeterOverlay());
	}
}