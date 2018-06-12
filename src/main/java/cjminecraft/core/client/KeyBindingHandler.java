package cjminecraft.core.client;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class KeyBindingHandler {

	public static void registerKeybinding(String unlocalizedName, int keyboardCode, String CATEGORY) {
		KeyBinding key = new KeyBinding(unlocalizedName, keyboardCode, CATEGORY);
		ClientRegistry.registerKeyBinding(key);
	}
	
	public static void registerKeybinding(KeyBinding key) {
		ClientRegistry.registerKeyBinding(key);
	}
}
