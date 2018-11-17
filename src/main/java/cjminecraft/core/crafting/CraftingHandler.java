package cjminecraft.core.crafting;

import cjminecraft.core.CJCore;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;
import net.minecraftforge.oredict.ShapedOreRecipe;

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
		RecipeSorter.register("clearColour", RecipeClearColor.class, Category.SHAPELESS, "after:minecraft:shapeless");
		RecipeSorter.register("itemColour", RecipeItemColor.class, Category.SHAPELESS, "after:minecraft:shapeless");
	}

}
