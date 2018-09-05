package cjminecraft.core.crafting;

import com.google.gson.JsonObject;

import cjminecraft.core.CJCore;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * Allows the colour tag to be cleared from coloured {@link ItemStack}s
 * 
 * @author CJMinecraft
 *
 */
public class RecipeClearColor extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

	/**
	 * The {@link ItemStack} to target
	 */
	private ItemStack targetItemStack;

	public RecipeClearColor() {
		this(ItemStack.EMPTY);
	}
	
	/**
	 * Create a recipe which can clear the colour of an {@link ItemStack}
	 * 
	 * @param targetItemStack
	 *            The {@link ItemStack} to target
	 */
	public RecipeClearColor(ItemStack targetItemStack) {
		this.targetItemStack = targetItemStack;
	}

	/**
	 * States whether the current crafting formation is valid
	 */
	@Override
	public boolean matches(InventoryCrafting inv, World world) {
		boolean randomItemDetected = false;
		boolean stackFound = false;
		for (int slot = 0; slot < inv.getSizeInventory(); slot++) {
			if (inv.getStackInSlot(slot).getItem() == targetItemStack.getItem())
				if (inv.getStackInSlot(slot).hasTagCompound()
						&& (inv.getStackInSlot(slot).getTagCompound().hasKey("colour")
								|| inv.getStackInSlot(slot).getTagCompound().hasKey("color")))
					stackFound = true;
			if (inv.getStackInSlot(slot).getItem() != Item.getItemFromBlock(Blocks.AIR)
					&& inv.getStackInSlot(slot).getItem() != targetItemStack.getItem())
				randomItemDetected = true;
		}
		return stackFound && !randomItemDetected;
	}

	/**
	 * Gets the result from seeing the crafting formation
	 */
	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		ItemStack toClear = ItemStack.EMPTY;
		for (int slot = 0; slot < inv.getSizeInventory(); slot++)
			if (inv.getStackInSlot(slot).getItem() == targetItemStack.getItem())
				if (inv.getStackInSlot(slot).hasTagCompound()
						&& (inv.getStackInSlot(slot).getTagCompound().hasKey("colour")
								|| inv.getStackInSlot(slot).getTagCompound().hasKey("color")))
					toClear = inv.getStackInSlot(slot).copy();
		if (toClear.getTagCompound().hasKey("colour"))
			toClear.getTagCompound().setInteger("colour", 0xFFFFFF);
		if (toClear.getTagCompound().hasKey("color"))
			toClear.getTagCompound().setInteger("color", 0xFFFFFF);
		return toClear;
	}

	/**
	 * By default there is no output
	 */
	@Override
	public ItemStack getRecipeOutput() {
		return ItemStack.EMPTY;
	}

	/**
	 * How many items should be left
	 */
	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
		return NonNullList.<ItemStack>withSize(inv.getSizeInventory(), ItemStack.EMPTY);
	}

	@Override
	public boolean canFit(int width, int height) {
		return width * height >= 1;
	}
	
	public static RecipeClearColor factory(JsonContext context, JsonObject json) {
		ItemStack item = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "item"), context);
		return new RecipeClearColor(item);
	}

}
