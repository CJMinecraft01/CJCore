package cjminecraft.core.init;

import cjminecraft.core.CJCore;
import cjminecraft.core.items.ItemMultimeter;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;

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
	public static void init() {
		multimeter = new ItemMultimeter("multimeter");
	}

	/**
	 * Register the items
	 */
	public static void register() {
		registerItem(multimeter);
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
	public static void registerItem(Item item) {
		GameRegistry.register(item);
	}

	/**
	 * Register the render for the item
	 * 
	 * @param item
	 *            The item to render
	 */
	public static void registerRender(Item item) {
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(
				new ResourceLocation(CJCore.MODID, item.getUnlocalizedName().substring(5)), "inventory"));
	}

}
