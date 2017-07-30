package cjminecraft.core.init;

import cjminecraft.core.CJCore;
import cjminecraft.core.items.ItemMultimeter;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent.Register;

/**
 * Handles all of {@link CJCore}s items
 * 
 * @author CJMinecraft
 *
 */
public class CJCoreItems {

	public static Item multimeter;

	/**
	 * Initialize the items
	 */
	private static void init() {
		multimeter = new ItemMultimeter("multimeter");
	}

	/**
	 * Register the items
	 */
	public static void register(Register<Item> register) {
		init();
		registerItem(multimeter, register);
	}

	/**
	 * Register the render for the items
	 */
	public static void registerRenders() {
		registerRender(multimeter);
	}

	/**
	 * Register an item
	 * 
	 * @param item
	 *            The item to register
	 */
	private static void registerItem(Item item, Register<Item> register) {
		register.getRegistry().register(item);
	}

	/**
	 * Register the render for the item
	 * 
	 * @param item
	 *            The item to register a render for
	 */
	private static void registerRender(Item item) {
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(
				new ResourceLocation(CJCore.MODID, item.getUnlocalizedName().substring(5)), "inventory"));
	}
}
