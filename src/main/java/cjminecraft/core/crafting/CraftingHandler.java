package cjminecraft.core.crafting;

import cjminecraft.core.CJCore;
import cjminecraft.core.init.CJCoreItems;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;

/**
 * Handles all of the {@link CJCore} recipes
 * @author CJMinecraft
 *
 */
public class CraftingHandler {
	
	/**
	 * Registers the crafting recipes. Used only by {@link CJCore}
	 */
	public static void registerCraftingRecipes() {
		RecipeSorter.register("clearColour", RecipeClearColour.class, Category.SHAPELESS, "after:minecraft:shapeless");
		RecipeSorter.register("itemColour", RecipeItemColour.class, Category.SHAPELESS, "after:minecraft:shapeless");
		
		GameRegistry.addRecipe(new ItemStack(CJCoreItems.multimeter), new Object[] { " G ", "GRG", " G ", 'G', Items.GOLD_INGOT, 'R', Blocks.REDSTONE_BLOCK });
	}

}
