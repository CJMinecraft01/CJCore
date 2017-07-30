package cjminecraft.core.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;

/**
 * Allows the colour tag to be cleared from coloured {@link ItemStack}s
 * @author CJMinecraft
 */
public class RecipeClearColour extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

	/**
	 * The {@link ItemStack} to target
	 */
	private ItemStack targetItemStack;

    /**
	 * Default constructor for registering the recipe
	 */
	RecipeClearColour() {
        this(ItemStack.EMPTY);
    }

    /**
	 * Create a recipe which can clear the colour of an {@link ItemStack}
	 * @param target The {@link ItemStack} to target
	 */
	public RecipeClearColour(ItemStack target) {
		targetItemStack = target;
	}

	/**
	 * States whether the current crafting formation is valid
	 */
	@Override
    @SuppressWarnings("ConstantConditions")
	public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World world) {
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
    @Nonnull
    @Override
    @SuppressWarnings("ConstantConditions")
	public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {
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

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 9;
    }

	/**
	 * By default there is no output
	 */
	@Nonnull
    @Override
	public ItemStack getRecipeOutput() {
		return ItemStack.EMPTY;
	}

	/**
	 * How many items should be left
	 */
	@Nonnull
    @Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
		return NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
	}
}