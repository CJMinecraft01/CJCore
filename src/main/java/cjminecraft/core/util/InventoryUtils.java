package cjminecraft.core.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Utility class for inventories
 * 
 * @author CJMinecraft
 *
 */
public class InventoryUtils {

	/**
	 * States whether the chosen {@link ItemStack} is in the
	 * {@link EntityPlayer}s hotbar
	 * 
	 * @param stack
	 *            The {@link ItemStack} to check
	 * @param player
	 *            The {@link EntityPlayer} which should have the
	 *            {@link ItemStack} in their hotbar
	 * @param ignoreNBT
	 *            Whether NBT data should be ignored. Reference -
	 *            {@link #isStackEqual(ItemStack, ItemStack, boolean, boolean)}
	 * @param ignoreMetaData
	 *            Whether meta data should be ignored. Reference -
	 *            {@link #isStackEqual(ItemStack, ItemStack, boolean, boolean)}
	 * @return Whether the {@link EntityPlayer} has the stack in their hotbar
	 */
	public static boolean hasInHotbar(ItemStack stack, EntityPlayer player, boolean ignoreNBT, boolean ignoreMetaData) {
		if (isStackEqual(stack, player.getHeldItemOffhand(), ignoreNBT, ignoreMetaData))
			return true;

		for (int slot = 0; slot < player.inventory.getHotbarSize(); slot++) {
			if (isStackEqual(stack, player.inventory.getStackInSlot(slot), ignoreNBT, ignoreMetaData))
				return true;
		}
		return false;
	}

	/**
	 * States whether {@link ItemStack} a is the same as {@link ItemStack} b
	 * 
	 * @param a
	 *            The first {@link ItemStack}
	 * @param b
	 *            The second {@link ItemStack}
	 * @param ignoreNBT
	 *            Whether NBT data should be ignored. If not, both
	 *            {@link ItemStack}s will need to have the same
	 *            {@link NBTTagCompound}
	 * @param ignoreMetaData
	 *            Whether meta data should be ignored. If not, both
	 *            {@link ItemStack}s will need to have the same damage
	 * @return Whether the two {@link ItemStack}s are the same
	 */
	public static boolean isStackEqual(ItemStack a, ItemStack b, boolean ignoreNBT, boolean ignoreMetaData) {
		if (ignoreNBT && ignoreMetaData)
			return a.getItem() == b.getItem();
		if (ignoreNBT && !ignoreMetaData)
			return a.isItemEqual(b);
		if (!ignoreNBT && ignoreMetaData)
			return a.isItemEqualIgnoreDurability(b) && a.getTagCompound().equals(b.getTagCompound());
		if (!ignoreNBT && !ignoreMetaData)
			return a.isItemEqual(b) && a.getTagCompound().equals(b.getTagCompound());
		return false;
	}

	/**
	 * Calculate the redstone current from a item stack handler
	 * 
	 * @param handler
	 *            The handler
	 * @return The redstone power
	 */
	public static int calculateRedstone(ItemStackHandler handler) {
		int filledSlots = 0;
		float stacksFull = 0.0F;
		for (int slot = 0; slot < handler.getSlots(); slot++) {
			ItemStack stack = handler.getStackInSlot(slot);
			if (!stack.isEmpty()) {
				stacksFull += (float) stack.getCount()
						/ (float) Math.min(handler.getSlotLimit(slot), stack.getMaxStackSize());
				filledSlots++;
			}
		}
		stacksFull = stacksFull / (float) handler.getSlots();
		return MathHelper.floor(stacksFull * 14.0F) + (filledSlots > 0 ? 1 : 0);
	}

	/**
	 * Adds the chosen item stack to the inventory
	 * 
	 * @param handler
	 *            The holder of the items
	 * @param stack
	 *            The stack to add
	 * @param simulate
	 *            Is the task a simulation?
	 * @return The remainder left if the slot was full
	 */
	public static ItemStack addStackToInventory(IItemHandler handler, ItemStack stack, boolean simulate) {
		ItemStack remainder = stack;
		for (int slot = 0; slot < handler.getSlots(); slot++) {
			remainder = handler.insertItem(slot, stack, simulate);
			if (remainder == ItemStack.EMPTY)
				break;
		}
		return remainder;
	}

	/**
	 * Adds the chosen item stack to the inventory
	 * 
	 * @param handler
	 *            The holder of the items
	 * @param maxSlot
	 *            The max slot to add to
	 * @param stack
	 *            The stack to add
	 * @param simulate
	 *            Is the task a simulation?
	 * @return The remainder left if the slot was full
	 */
	public static ItemStack addStackToInventory(IItemHandler handler, int maxSlot, ItemStack stack, boolean simulate) {
		ItemStack remainder = stack;
		for (int slot = 0; slot < maxSlot; slot++) {
			remainder = handler.insertItem(slot, stack, simulate);
			if (remainder == ItemStack.EMPTY)
				break;
		}
		return remainder;
	}

	/**
	 * Checks if the inventory is full
	 * 
	 * @param handler
	 *            The inventory
	 * @return true if it is full
	 */
	public static boolean isInventoryFull(IItemHandler handler) {
		int filledSlots = 0;
		for (int slot = 0; slot < handler.getSlots(); slot++) {
			if (handler.getStackInSlot(slot).getCount() == handler.getSlotLimit(slot))
				filledSlots++;
		}
		return filledSlots == handler.getSlots();
	}

	/**
	 * Checks if the inventory is full
	 * 
	 * @param handler
	 *            The inventory
	 * @param maxSlot
	 *            The number of slots to check
	 * @return true if it is full
	 */
	public static boolean isInventoryFull(IItemHandler handler, int maxSlot) {
		int filledSlots = 0;
		for (int slot = 0; slot < maxSlot; slot++) {
			if (handler.getStackInSlot(slot).getCount() == handler.getSlotLimit(slot))
				filledSlots++;
		}
		return filledSlots == maxSlot;
	}

	/**
	 * Gets the correct colour from any item stack using the ore dictionary The
	 * item must be registered as a dye
	 * 
	 * @param stack
	 *            The {@link ItemStack} to test
	 * @return The {@link EnumDyeColor} of the {@link ItemStack} to test. If the
	 *         stack is not registered as a dye, the {@link EnumDyeColor#WHITE}
	 *         will be used
	 */
	public static EnumDyeColor getColourFromDye(ItemStack stack) {
		for (int id : OreDictionary.getOreIDs(stack)) {
			if (id == OreDictionary.getOreID("dyeBlack"))
				return EnumDyeColor.BLACK;
			if (id == OreDictionary.getOreID("dyeRed"))
				return EnumDyeColor.RED;
			if (id == OreDictionary.getOreID("dyeGreen"))
				return EnumDyeColor.GREEN;
			if (id == OreDictionary.getOreID("dyeBrown"))
				return EnumDyeColor.BROWN;
			if (id == OreDictionary.getOreID("dyeBlue"))
				return EnumDyeColor.BLUE;
			if (id == OreDictionary.getOreID("dyePurple"))
				return EnumDyeColor.PURPLE;
			if (id == OreDictionary.getOreID("dyeCyan"))
				return EnumDyeColor.CYAN;
			if (id == OreDictionary.getOreID("dyeLightGray"))
				return EnumDyeColor.SILVER;
			if (id == OreDictionary.getOreID("dyeGray"))
				return EnumDyeColor.GRAY;
			if (id == OreDictionary.getOreID("dyePink"))
				return EnumDyeColor.PINK;
			if (id == OreDictionary.getOreID("dyeLime"))
				return EnumDyeColor.LIME;
			if (id == OreDictionary.getOreID("dyeYellow"))
				return EnumDyeColor.YELLOW;
			if (id == OreDictionary.getOreID("dyeLightBlue"))
				return EnumDyeColor.LIGHT_BLUE;
			if (id == OreDictionary.getOreID("dyeMagenta"))
				return EnumDyeColor.MAGENTA;
			if (id == OreDictionary.getOreID("dyeOrange"))
				return EnumDyeColor.ORANGE;
			if (id == OreDictionary.getOreID("dyeWhite"))
				return EnumDyeColor.WHITE;
		}
		return EnumDyeColor.WHITE;
	}

}
