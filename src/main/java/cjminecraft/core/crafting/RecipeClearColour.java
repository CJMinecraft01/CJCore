package cjminecraft.core.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

/**
 * Allows the colour tag to be cleared from coloured {@link ItemStack}s
 * @author CJMinecraft
 *
 */
public class RecipeClearColour implements IRecipe {

	/**
	 * The {@link ItemStack} to target
	 */
	private ItemStack targetItemStack;
	
	/**
	 * Create a recipe which can clear the colour of an {@link ItemStack}
	 * @param targetItemStack The {@link ItemStack} to target
	 */
	public RecipeClearColour(ItemStack targetItemStack) {
		this.targetItemStack = targetItemStack;
	}
	
	/**
	 * States whether the current crafting formation is valid
	 */
	@Override
	public boolean matches(InventoryCrafting inv, World world) {
		boolean randomItemDetected = false;
		boolean stackFound = false;
		for(int slot = 0; slot < inv.getSizeInventory(); slot++) {
			if(inv.getStackInSlot(slot).getItem() == targetItemStack.getItem())
				if(inv.getStackInSlot(slot).hasTagCompound() && (inv.getStackInSlot(slot).getTagCompound().hasKey("colour") || inv.getStackInSlot(slot).getTagCompound().hasKey("color")))
					stackFound = true;
			if(inv.getStackInSlot(slot).getItem() != Item.getItemFromBlock(Blocks.AIR) && inv.getStackInSlot(slot).getItem() != targetItemStack.getItem())
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
		for(int slot = 0; slot < inv.getSizeInventory(); slot++)
			if(inv.getStackInSlot(slot).getItem() == targetItemStack.getItem())
				if(inv.getStackInSlot(slot).hasTagCompound() && (inv.getStackInSlot(slot).getTagCompound().hasKey("colour") || inv.getStackInSlot(slot).getTagCompound().hasKey("color")))
					toClear = inv.getStackInSlot(slot).copy();
		if(toClear.getTagCompound().hasKey("colour"))
			toClear.getTagCompound().setInteger("colour", 0xFFFFFF);
		if(toClear.getTagCompound().hasKey("color"))
			toClear.getTagCompound().setInteger("color", 0xFFFFFF);
		return toClear;
	}

	/**
	 * How many slots are required. 10 because 9 for the crafting bench, 1 for the output
	 */
	@Override
	public int getRecipeSize() {
		return 10;
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

}
