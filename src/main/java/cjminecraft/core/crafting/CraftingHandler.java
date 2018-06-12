package cjminecraft.core.crafting;

import cjminecraft.core.CJCore;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;

/**
 * Handles all of the {@link CJCore} recipes
 * 
 * @author CJMinecraft
 *
 */
public class CraftingHandler {
	
	/**
	 * Registers the crafting recipes. Used only by {@link CJCore}
	 */
	public static void registerCraftingRecipes() {
//		ForgeRegistries.RECIPES.register(new RecipeClearColour().setRegistryName(CJCore.MODID, "clearColor"));
//		ForgeRegistries.RECIPES.register(new RecipeItemColour().setRegistryName(CJCore.MODID, "itemColor"));

		registerCraftingRecipe("multimeter_energy");
		registerCraftingRecipe("multimeter_item");
		registerCraftingRecipe("multimeter_fluid");
	}
	
	private static void registerCraftingRecipe(String recipePath) {
		CraftingHelper.register(new ResourceLocation(CJCore.MODID, "recipes/" + recipePath),
				(IRecipeFactory) (context, json) -> CraftingHelper.getRecipe(json, context));
	}

}
