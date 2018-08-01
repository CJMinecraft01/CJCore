package cjminecraft.core.init;

import cjminecraft.core.CJCore;
import cjminecraft.core.items.ItemMultimeter;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
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
		ModelBakery.registerItemVariants(multimeter, new ResourceLocation(CJCore.MODID, "multimeter_energy"),
				new ResourceLocation(CJCore.MODID, "multimeter_item"),
				new ResourceLocation(CJCore.MODID, "multimeter_fluid"));
		registerRender(multimeter, 0, "multimeter_energy");
		registerRender(multimeter, 1, "multimeter_item");
		registerRender(multimeter, 2, "multimeter_fluid");
	}

	/**
	 * Register an item
	 * 
	 * @param item
	 *            The item to register
	 */
	public static void registerItem(Item item) {
		ForgeRegistries.ITEMS.register(item);
	}

	/**
	 * Register the render for the item
	 * 
	 * @param item
	 *            The item to render
	 */
	public static void registerRender(Item item) {
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(
				new ResourceLocation(CJCore.MODID, item.getTranslationKey()), "inventory"));
	}

	/**
	 * Register the render for the item
	 * 
	 * @param item
	 *            The item to render
	 * @param meta
	 *            The item's meta data
	 */
	public static void registerRender(Item item, int meta) {
		ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(
				new ResourceLocation(CJCore.MODID, item.getTranslationKey()), "inventory"));
	}

	/**
	 * Register the render for the item
	 * 
	 * @param item
	 *            The item to render
	 * @param meta
	 *            The item's meta data
	 * @param fileName
	 *            The name of the model file
	 */
	public static void registerRender(Item item, int meta, String fileName) {
		ModelLoader.setCustomModelResourceLocation(item, meta,
				new ModelResourceLocation(new ResourceLocation(CJCore.MODID, fileName), "inventory"));
	}

}
