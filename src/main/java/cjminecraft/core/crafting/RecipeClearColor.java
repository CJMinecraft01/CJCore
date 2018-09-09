package cjminecraft.core.crafting;

import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonObject;

import cjminecraft.core.CJCore;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

/**
 * Allows the colour tag to be cleared from coloured {@link ItemStack}s
 * 
 * @author CJMinecraft
 *
 */
public class RecipeClearColor implements IRecipe {

	/**
	 * The {@link ItemStack} to target
	 */
	private ItemStack targetItemStack;

	public RecipeClearColor() {
		this(null);
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
			ItemStack stack = inv.getStackInSlot(slot);
			if (stack.getItem() == this.targetItemStack.getItem())
				stackFound = true;
			else if (stack != null)
				randomItemDetected = true;
		}
		return stackFound && !randomItemDetected;
	}

	/**
	 * Gets the result from seeing the crafting formation
	 */
	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		ItemStack toClear = null;
		for (int slot = 0; slot < inv.getSizeInventory(); slot++)
			if (inv.getStackInSlot(slot).getItem() == this.targetItemStack.getItem())
				toClear = inv.getStackInSlot(slot).copy();
		if (toClear == null)
			return null;
		if (!toClear.hasTagCompound())
			toClear.setTagCompound(new NBTTagCompound());
		toClear.getTagCompound().setInteger("color", 0xFFFFFF);
		return toClear;
	}

	/**
	 * By default there is no output
	 */
	@Override
	public ItemStack getRecipeOutput() {
		return null;
	}

	/**
	 * How many items should be left
	 */
	@Override
	public ItemStack[] getRemainingItems(InventoryCrafting inv) {
		return new ItemStack[inv.getSizeInventory()];
	}

	/**
	 * The size of the recipe area
	 */
	@Override
	public int getRecipeSize() {
		return 4;
	}

}
