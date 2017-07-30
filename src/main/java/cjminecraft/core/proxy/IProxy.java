package cjminecraft.core.proxy;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;

/**
 * Any proxy should look like this
 * @author CJMinecraft
 *
 */
public interface IProxy {
	
	void preInit();

	void onItemRegister(RegistryEvent.Register<Item> register);
	
	void init();
	
	void postInit();
}