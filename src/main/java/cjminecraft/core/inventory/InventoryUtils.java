package cjminecraft.core.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import cjminecraft.core.CJCore;
import cjminecraft.core.network.PacketHandler;
import cjminecraft.core.network.inventory.PacketGetInventory;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Utility class for inventories
 * 
 * @author CJMinecraft
 *
 */
public class InventoryUtils {

	private static final Object T = null;
	private static HashMap<String, HashMap<String, ImmutableList<ItemStack>>> cachedInventoryData = new HashMap<String, HashMap<String, ImmutableList<ItemStack>>>();

	/**
	 * Returns how big the given {@link TileEntity}'s inventory is
	 * 
	 * @param te
	 *            The {@link TileEntity} with the inventory
	 * @param side
	 *            The side of the {@link TileEntity} the inventory is for use
	 *            with {@link Capability} and {@link ISidedInventory}
	 * @return How many slots are inside of the inventory
	 */
	public static int getSizeInventory(@Nullable TileEntity te, @Nullable EnumFacing side) {
		if (te == null)
			return 0;
		if (te instanceof ISidedInventory)
			return ((ISidedInventory) te).getSlotsForFace(side).length;
		if (te instanceof IInventory)
			return ((IInventory) te).getSizeInventory();
		if (te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side))
			return te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side).getSlots();
		return 0;
	}

	/**
	 * Returns how big the given {@link ItemStack}'s inventory is
	 * 
	 * @param stack
	 *            The {@link ItemStack} with the inventory
	 * @param side
	 *            The side of the {@link ItemStack} the inventory is for use
	 *            with {@link Capability}
	 * @return How many slots are inside of the inventory
	 */
	public static int getSizeInventory(@Nullable ItemStack stack, @Nullable EnumFacing side) {
		if (stack == null || stack.isEmpty())
			return 0;
		if (stack.getItem() instanceof IInventory)
			return ((IInventory) stack.getItem()).getSizeInventory();
		if (stack.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side))
			return stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side).getSlots();
		return 0;
	}

	/**
	 * Gets the last slot in the {@link TileEntity} which has an inventory
	 * 
	 * @param te
	 *            The {@link TileEntity} which has an inventory
	 * @param side
	 *            The side of the {@link TileEntity} the inventory is. For use
	 *            with {@link ISidedInventory} and {@link Capability}
	 * @return The last slot index in the inventory
	 */
	public static int getLastSlotIndex(@Nullable TileEntity te, @Nullable EnumFacing side) {
		if (te == null)
			return 0;
		if (te instanceof ISidedInventory) {
			int[] slots = ((ISidedInventory) te).getSlotsForFace(side);
			return slots.length != 0 ? slots[slots.length - 1] : 0;
		}
		if (te instanceof IInventory)
			return ((IInventory) te).getSizeInventory() - 1;
		if (te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side))
			return te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side).getSlots() - 1;
		return 0;
	}

	/**
	 * Gets the last slot in the {@link ItemStack} which has an inventory
	 * 
	 * @param stack
	 *            The {@link ItemStack} which has an inventory
	 * @param side
	 *            The side of the {@link ItemStack} the inventory is. For use
	 *            with {@link Capability}
	 * @return The last slot index in the inventory
	 */
	public static int getLastSlotIndex(@Nullable ItemStack stack, @Nullable EnumFacing side) {
		if (stack == null || stack.isEmpty())
			return 0;
		if (stack.getItem() instanceof IInventory)
			return ((IInventory) stack.getItem()).getSizeInventory() - 1;
		if (stack.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side))
			return stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side).getSlots() - 1;
		return 0;
	}

	/**
	 * Gets the first slot index in the given {@link TileEntity}'s inventory
	 * 
	 * @param te
	 *            The {@link TileEntity} which has an inventory
	 * @param side
	 *            The side of the {@link TileEntity} which has the inventory.
	 *            For use with {@link ISidedInventory} and {@link Capability}
	 * @return The first slot index of the inventory
	 */
	public static int getFirstSlotIndex(@Nullable TileEntity te, @Nullable EnumFacing side) {
		if (te == null)
			return 0;
		if (te instanceof ISidedInventory)
			return ((ISidedInventory) te).getSlotsForFace(side).length != 0
					? ((ISidedInventory) te).getSlotsForFace(side)[0] : 0;
		return 0;
	}

	/**
	 * Gets the first slot index in the given {@link ItemStack}'s inventory
	 * 
	 * @param stack
	 *            The {@link ItemStack} which has an inventory
	 * @param side
	 *            The side of the {@link ItemStack} which has the inventory. For
	 *            use with {@link Capability}
	 * @return The first slot index of the inventory
	 */
	public static int getFirstSlotIndex(@Nullable ItemStack stack, @Nullable EnumFacing side) {
		return 0;
	}

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
	public static boolean hasInHotbar(ItemStack stack, @Nonnull EntityPlayer player, boolean ignoreNBT,
			boolean ignoreMetaData) {
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
	public static boolean isStackEqual(@Nullable ItemStack a, @Nullable ItemStack b, boolean ignoreNBT,
			boolean ignoreMetaData) {
		if (a == null || b == null || a.isEmpty() || b.isEmpty())
			return false;
		if (ignoreNBT && ignoreMetaData)
			return a.getItem() == b.getItem();
		if (ignoreNBT && !ignoreMetaData)
			return a.isItemEqual(b);
		if (!ignoreNBT && ignoreMetaData) {
			if (a.hasTagCompound() && b.hasTagCompound())
				return a.isItemEqualIgnoreDurability(b) && a.getTagCompound().equals(b.getTagCompound());
			return a.isItemEqualIgnoreDurability(b) && a.hasTagCompound() == b.hasTagCompound();
		}
		if (!ignoreNBT && !ignoreMetaData) {
			if (a.hasTagCompound() && b.hasTagCompound())
				return a.isItemEqual(b) && a.getTagCompound().equals(b.getTagCompound());
			return a.isItemEqual(b) && ((!a.hasTagCompound() && !b.hasTagCompound())
					|| (a.getTagCompound() == null && b.getTagCompound() == null));
		}
		return false;
	}

	/**
	 * Will find the target {@link ItemStack} in the player's inventory in the
	 * range chosen. Will return the {@link ItemStack} from the player's
	 * inventory if it is found, otherwise it will just return the target
	 * {@link ItemStack}
	 * 
	 * @param toFind
	 *            The {@link ItemStack} to find in the player's inventory
	 * @param player
	 *            The player to search
	 * @param ignoreNBT
	 *            Whether NBT data should be ignored. If not, both
	 *            {@link ItemStack}s will need to have the same
	 *            {@link NBTTagCompound}. Reference
	 *            {@link #isStackEqual(ItemStack, ItemStack, boolean, boolean)}
	 * @param ignoreMetaData
	 *            Whether meta data should be ignored. If not, both
	 *            {@link ItemStack}s will need to have the same damage.
	 *            Reference
	 *            {@link #isStackEqual(ItemStack, ItemStack, boolean, boolean)}
	 * @param from
	 *            The first slot to search
	 * @param to
	 *            The last slot to search
	 * @return The {@link ItemStack} from the player's inventory if it is found.
	 *         If not it will return {@link ItemStack#EMPTY}
	 */
	public static ItemStack findInInventory(ItemStack toFind, @Nonnull EntityPlayer player, boolean ignoreNBT,
			boolean ignoreMetaData, int from, int to) {
		for (int slot = from; slot < player.inventory.getSizeInventory() && slot <= to; slot++) {
			ItemStack stack = player.inventory.getStackInSlot(slot);
			if (isStackEqual(toFind, stack, ignoreNBT, ignoreMetaData)) {
				return stack;
			}
		}
		return ItemStack.EMPTY;
	}

	/**
	 * Will find the target {@link ItemStack} in the player's hotbar or offhand
	 * 
	 * @param toFind
	 *            The {@link ItemStack} to find in the player's inventory
	 * @param player
	 *            The player to search
	 * @param ignoreNBT
	 *            Whether NBT data should be ignored. Reference
	 *            {@link #findInInventory(ItemStack, EntityPlayer, boolean, boolean, int, int)}
	 * @param ignoreMetaData
	 *            Whether meta data should be ignored. Reference
	 *            {@link #findInInventory(ItemStack, EntityPlayer, boolean, boolean, int, int)}
	 * @return The {@link ItemStack} from the player's inventory if it is found.
	 *         If not, it will return {@link ItemStack#EMPTY}
	 */
	public static ItemStack findInHotbar(ItemStack toFind, @Nonnull EntityPlayer player, boolean ignoreNBT,
			boolean ignoreMetaData) {
		ItemStack stack = findInInventory(toFind, player, ignoreNBT, ignoreMetaData, 0, 8);
		if (!isStackEqual(stack, toFind, ignoreNBT, ignoreMetaData) && !player.getHeldItemOffhand().isEmpty())
			stack = findInInventory(toFind, player, ignoreNBT, ignoreMetaData, 40, 40);
		return stack;
	}

	/**
	 * Calculate the redstone current that a comparator should show based on how
	 * full the given {@link TileEntity}'s inventory is
	 * 
	 * @param te
	 *            The {@link TileEntity} to check
	 * @param fromSlot
	 *            The first slot to start calculating from
	 * @param toSlot
	 *            The last slot to calculate from
	 * @param side
	 *            The side of the {@link TileEntity} the inventory is. For use
	 *            with {@link ISidedInventory} and {@link Capability}
	 * @return The redstone current that a comparator should show
	 */
	public static int calculateRedstone(@Nullable TileEntity te, int fromSlot, int toSlot, @Nullable EnumFacing side) {
		if (te == null)
			return 0;
		if (te instanceof ISidedInventory) {
			ISidedInventory inv = (ISidedInventory) te;
			int[] slots = inv.getSlotsForFace(side);
			if (slots.length == 0)
				return 0;
			if (slots[0] > toSlot || fromSlot > toSlot || slots[slots.length - 1] < fromSlot)
				return 0;
			int itemsFound = 0;
			float proportion = 0.0F;

			for (int slot : slots) {
				if (slot < toSlot && slot > fromSlot) {
					ItemStack stack = inv.getStackInSlot(slot);

					if (!stack.isEmpty()) {
						proportion += (float) stack.getCount()
								/ (float) Math.min(inv.getInventoryStackLimit(), stack.getMaxStackSize());
						itemsFound++;
					}
				}
			}

			proportion = proportion / (float) inv.getSizeInventory();
			return MathHelper.floor(proportion * 14.0F) + (itemsFound > 0 ? 1 : 0);
		}
		if (te instanceof IInventory) {
			IInventory inv = (IInventory) te;
			if (inv.getSizeInventory() - 1 < toSlot || fromSlot > toSlot || inv.getSizeInventory() - 1 < fromSlot)
				return 0;
			int itemsFound = 0;
			float proportion = 0.0F;

			for (int slot = fromSlot; slot < toSlot; slot++) {
				if (slot < toSlot && slot > fromSlot) {
					ItemStack stack = inv.getStackInSlot(slot);

					if (!stack.isEmpty()) {
						proportion += (float) stack.getCount()
								/ (float) Math.min(inv.getInventoryStackLimit(), stack.getMaxStackSize());
						itemsFound++;
					}
				}
			}

			proportion = proportion / (float) inv.getSizeInventory();
			return MathHelper.floor(proportion * 14.0F) + (itemsFound > 0 ? 1 : 0);
		}
		if (te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)) {
			IItemHandler inv = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
			if (inv.getSlots() - 1 < toSlot || fromSlot > toSlot || inv.getSlots() - 1 < fromSlot)
				return 0;
			int itemsFound = 0;
			float proportion = 0.0F;

			for (int slot = fromSlot; slot < toSlot; slot++) {
				if (slot < toSlot && slot > fromSlot) {
					ItemStack stack = inv.getStackInSlot(slot);

					if (!stack.isEmpty()) {
						proportion += (float) stack.getCount() / (float) Math.min(64, stack.getMaxStackSize());
						itemsFound++;
					}
				}
			}

			proportion = proportion / (float) inv.getSlots();
			return MathHelper.floor(proportion * 14.0F) + (itemsFound > 0 ? 1 : 0);
		}
		return 0;
	}

	/**
	 * Calculate the redstone current that a comparator should show based on how
	 * full the given {@link TileEntity}'s inventory is
	 * 
	 * @param te
	 *            The {@link TileEntity} to check
	 * @param side
	 *            The side of the {@link TileEntity} the inventory is. For use
	 *            with {@link ISidedInventory} and {@link Capability}
	 * @return The redstone current that a comparator should show
	 */
	public static int calculateRedstone(@Nullable TileEntity te, @Nullable EnumFacing side) {
		if (te == null)
			return 0;
		int toSlot = 0;
		if (te instanceof ISidedInventory) {
			int[] slots = ((ISidedInventory) te).getSlotsForFace(side);
			if (slots.length == 0)
				return 0;
			toSlot = slots[slots.length - 1];
		} else if (te instanceof IInventory) {
			toSlot = ((IInventory) te).getSizeInventory() - 1;
		} else if (te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)) {
			toSlot = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side).getSlots() - 1;
		}
		return calculateRedstone(te, 0, toSlot, side);
	}

	/**
	 * Inserts the given {@link ItemStack} to the inventory in the
	 * {@link TileEntity} provided
	 * 
	 * @param te
	 *            The {@link TileEntity} which has an inventory
	 * @param stack
	 *            The {@link ItemStack} to insert
	 * @param fromSlot
	 *            The first slot to try and put the item in
	 * @param toSlot
	 *            The last slot to try and put the item in
	 * @param simulate
	 *            Whether it is a simulation, if it is, no items will actually
	 *            be inserted.
	 * @param side
	 *            The side of the inventory to insert the {@link ItemStack}
	 *            into. For use with {@link ISidedInventory} and
	 *            {@link Capability}
	 * @return The items which were not inserted. If all the items were inserted
	 *         (or would have been if a simulation) it will return
	 *         {@link ItemStack#EMPTY}
	 */
	public static ItemStack insertStackInInventory(@Nullable TileEntity te, ItemStack stack, int fromSlot, int toSlot,
			boolean simulate, @Nullable EnumFacing side) {
		if (te == null)
			return stack;
		ItemStack remainder = stack.copy();
		if (te instanceof ISidedInventory) {
			ISidedInventory inv = (ISidedInventory) te;
			int[] slots = inv.getSlotsForFace(side);
			if (slots.length == 0)
				return stack;
			if (slots[0] > toSlot || fromSlot > toSlot || slots[slots.length - 1] < fromSlot)
				return stack;
			for (int slot : slots) {
				if (slot <= toSlot && slot >= fromSlot) {
					if (remainder.isEmpty())
						return ItemStack.EMPTY;
					ItemStack inSlot = inv.getStackInSlot(slot);
					if (inSlot.getCount() >= inv.getInventoryStackLimit()
							|| inSlot.getCount() >= inSlot.getMaxStackSize())
						continue;
					if (isStackEqual(stack, inSlot, false, false) || inSlot.isEmpty()) {
						int grow = Math.min(remainder.getCount(), inv.getInventoryStackLimit() - inSlot.getCount());
						if (!simulate) {
							if (inSlot.isEmpty() || inSlot.getCount() <= 0) {
								inv.setInventorySlotContents(slot,
										new ItemStack(stack.getItem(), grow, stack.getItemDamage()));
								if (stack.hasTagCompound())
									inv.getStackInSlot(slot).setTagCompound(stack.getTagCompound());
							} else
								inv.getStackInSlot(slot).grow(grow);
						}
						remainder.shrink(grow);
					}
				}
			}
			return remainder.isEmpty() ? ItemStack.EMPTY : remainder;
		}
		if (te instanceof IInventory) {
			IInventory inv = (IInventory) te;
			if (inv.getSizeInventory() - 1 < toSlot || fromSlot > toSlot || inv.getSizeInventory() - 1 < fromSlot)
				return stack;
			for (int slot = fromSlot; slot <= toSlot; slot++) {
				if (remainder.isEmpty())
					return ItemStack.EMPTY;
				ItemStack inSlot = inv.getStackInSlot(slot);
				if (inSlot.getCount() >= inv.getInventoryStackLimit()
						|| inSlot.getCount() >= inSlot.getMaxStackSize())
					continue;
				if (isStackEqual(stack, inSlot, false, false) || inSlot.isEmpty()) {
					int grow = Math.min(remainder.getCount(),
							inv.getInventoryStackLimit() - inSlot.getCount());
					if (!simulate) {
						if (inSlot.isEmpty() || inSlot.getCount() <= 0) {
							inv.setInventorySlotContents(slot,
									new ItemStack(stack.getItem(), grow, stack.getItemDamage()));
							if (stack.hasTagCompound())
								inv.getStackInSlot(slot).setTagCompound(stack.getTagCompound());
						} else
							inv.getStackInSlot(slot).grow(grow);
					}
					remainder.shrink(grow);
				}
			}
			return remainder.isEmpty() ? ItemStack.EMPTY : remainder;
		}
		if (te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)) {
			IItemHandler inv = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
			if (inv.getSlots() - 1 < toSlot || fromSlot > toSlot || inv.getSlots() - 1 < fromSlot)
				return stack;
			for (int slot = fromSlot; slot <= toSlot; slot++) {
				if (remainder.isEmpty())
					return ItemStack.EMPTY;
				remainder = inv.insertItem(slot, remainder, simulate);
			}
			return remainder.isEmpty() ? ItemStack.EMPTY : remainder;
		}
		return remainder.isEmpty() ? ItemStack.EMPTY : remainder;
	}

	/**
	 * Inserts the given {@link ItemStack} to the inventory in the
	 * {@link TileEntity} provided. The {@link ItemStack} will be inserted
	 * anywhere which has space in the inventory
	 * 
	 * @param te
	 *            The {@link TileEntity} which has an inventory
	 * @param stack
	 *            The {@link ItemStack} to insert
	 * @param simulate
	 *            Whether it is a simulation, if it is, no items will actually
	 *            be inserted.
	 * @param side
	 *            The side of the inventory to insert the {@link ItemStack}
	 *            into. For use with {@link ISidedInventory} and
	 *            {@link Capability}
	 * @return The items which were not inserted. If all the items were inserted
	 *         (or would have been if a simulation) it will return
	 *         {@link ItemStack#EMPTY}
	 */
	public static ItemStack insertStackInInventory(@Nullable TileEntity te, ItemStack stack, boolean simulate,
			@Nullable EnumFacing side) {
		return insertStackInInventory(te, stack, getFirstSlotIndex(te, side), getLastSlotIndex(te, side), simulate,
				side);
	}

	/**
	 * Inserts the given {@link ItemStack} to the inventory in the
	 * {@link ItemStack} provided
	 * 
	 * @param inventory
	 *            The {@link ItemStack} which has an inventory
	 * @param stack
	 *            The {@link ItemStack} to insert
	 * @param fromSlot
	 *            The first slot to try and put the item in
	 * @param toSlot
	 *            The last slot to try and put the item in
	 * @param simulate
	 *            Whether it is a simulation, if it is, no items will actually
	 *            be inserted.
	 * @param side
	 *            The side of the inventory to insert the {@link ItemStack}
	 *            into. For use with {@link Capability}
	 * @return The items which were not inserted. If all the items were inserted
	 *         (or would have been if a simulation) it will return
	 *         {@link ItemStack#EMPTY}
	 */
	public static ItemStack insertStackInInventory(@Nullable ItemStack inventory, ItemStack stack, int fromSlot,
			int toSlot, boolean simulate, @Nullable EnumFacing side) {
		if (inventory == null || inventory.isEmpty())
			return stack;
		ItemStack remainder = stack.copy();
		if (inventory.getItem() instanceof IInventory) {
			IInventory inv = (IInventory) inventory.getItem();
			if (inv.getSizeInventory() - 1 < toSlot || fromSlot > toSlot || inv.getSizeInventory() - 1 < fromSlot)
				return stack;
			for (int slot = fromSlot; slot <= toSlot; slot++) {
				if (remainder.isEmpty())
					return ItemStack.EMPTY;
				ItemStack inSlot = inv.getStackInSlot(slot);
				if (inSlot.getCount() >= inv.getInventoryStackLimit()
						|| inSlot.getCount() >= inSlot.getMaxStackSize())
					continue;
				if (isStackEqual(stack, inSlot, false, false) || inSlot.isEmpty()) {
					int grow = Math.min(remainder.getCount(),
							inv.getInventoryStackLimit() - inSlot.getCount());
					if (!simulate) {
						if (inSlot.isEmpty() || inSlot.getCount() <= 0) {
							inv.setInventorySlotContents(slot,
									new ItemStack(stack.getItem(), grow, stack.getItemDamage()));
							if (stack.hasTagCompound())
								inv.getStackInSlot(slot).setTagCompound(stack.getTagCompound());
						} else
							inv.getStackInSlot(slot).grow(grow);
					}
					remainder.shrink(grow);
				}
			}
			return remainder.isEmpty() ? ItemStack.EMPTY : remainder;
		}
		if (inventory.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)) {
			IItemHandler inv = inventory.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
			if (inv.getSlots() - 1 < toSlot || fromSlot > toSlot || inv.getSlots() - 1 < fromSlot)
				return stack;
			for (int slot = fromSlot; slot <= toSlot; slot++) {
				if (remainder.isEmpty())
					return ItemStack.EMPTY;
				remainder = inv.insertItem(slot, remainder, simulate);
			}
			return remainder.isEmpty() ? ItemStack.EMPTY : remainder;
		}
		return remainder.isEmpty() ? ItemStack.EMPTY : remainder;
	}

	/**
	 * Inserts the given {@link ItemStack} to the inventory in the
	 * {@link ItemStack} provided. The {@link ItemStack} will be inserted
	 * anywhere which has space in the inventory
	 * 
	 * @param inventory
	 *            The {@link ItemStack} which has an inventory
	 * @param stack
	 *            The {@link ItemStack} to insert
	 * @param simulate
	 *            Whether it is a simulation, if it is, no items will actually
	 *            be inserted.
	 * @param side
	 *            The side of the inventory to insert the {@link ItemStack}
	 *            into. For use with {@link Capability}
	 * @return The items which were not inserted. If all the items were inserted
	 *         (or would have been if a simulation) it will return
	 *         {@link ItemStack#EMPTY}
	 */
	public static ItemStack insertStackInInventory(@Nullable ItemStack inventory, ItemStack stack, boolean simulate,
			@Nullable EnumFacing side) {
		return insertStackInInventory(inventory, stack, getFirstSlotIndex(stack, side), getLastSlotIndex(stack, side),
				simulate, side);
	}

	/**
	 * Extracts the given {@link ItemStack} from the inventory in the
	 * {@link TileEntity} provided.
	 * 
	 * @param te
	 *            The {@link TileEntity} which has an inventory
	 * @param stack
	 *            The {@link ItemStack} to extract
	 * @param fromSlot
	 *            The slot to start looking to take from
	 * @param toSlot
	 *            The slot to stop looking to take from
	 * @param simulate
	 *            Whether it is a simulation, if it is, no items will actually
	 *            be taken
	 * @param side
	 *            The side of the inventory to extract the {@link ItemStack}
	 *            from. For use with {@link ISidedInventory} and
	 *            {@link Capability}
	 * @return The items which were extracted. If no items were extracted (or
	 *         supposedly extracted if a simulation) it will return
	 *         {@link ItemStack#EMPTY}
	 */
	public static ItemStack extractStackFromInventory(@Nullable TileEntity te, ItemStack stack, int fromSlot,
			int toSlot, boolean simulate, @Nullable EnumFacing side) {
		if (te == null)
			return ItemStack.EMPTY;
		ItemStack extracted = stack.copy();
		extracted.setCount(0);
		if (te instanceof ISidedInventory) {
			ISidedInventory inv = (ISidedInventory) te;
			int[] slots = inv.getSlotsForFace(side);
			if (slots.length == 0)
				return ItemStack.EMPTY;
			if (slots[0] > toSlot || fromSlot > toSlot || slots[slots.length - 1] < fromSlot)
				return ItemStack.EMPTY;
			for (int slot : slots) {
				if (slot <= toSlot && slot >= fromSlot) {
					ItemStack inSlot = inv.getStackInSlot(slot);
					if (isStackEqual(stack, inSlot, false, false)) {
						int decreaseBy = Math.min(stack.getCount(), inSlot.getCount());
						if (!simulate)
							inv.decrStackSize(slot, decreaseBy);
						stack.shrink(decreaseBy);
						extracted.grow(decreaseBy);
					}
				}
			}
			return extracted.isEmpty() ? ItemStack.EMPTY : extracted;
		}
		if (te instanceof IInventory) {
			IInventory inv = (IInventory) te;
			if (inv.getSizeInventory() - 1 < toSlot || fromSlot > toSlot || inv.getSizeInventory() - 1 < fromSlot)
				return ItemStack.EMPTY;
			for (int slot = fromSlot; slot <= toSlot; slot++) {
				ItemStack inSlot = inv.getStackInSlot(slot);
				if (isStackEqual(stack, inSlot, false, false)) {
					int decreaseBy = Math.min(stack.getCount(), inSlot.getCount());
					if (!simulate)
						inv.decrStackSize(slot, decreaseBy);
					stack.shrink(decreaseBy);
					extracted.grow(decreaseBy);
				}
			}
			return extracted.isEmpty() ? ItemStack.EMPTY : extracted;
		}
		if (te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)) {
			IItemHandler inv = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
			if (inv.getSlots() - 1 < toSlot || fromSlot > toSlot || inv.getSlots() - 1 < fromSlot)
				return ItemStack.EMPTY;
			for (int slot = fromSlot; slot <= toSlot; slot++) {
				ItemStack inSlot = inv.getStackInSlot(slot);
				if (isStackEqual(stack, inSlot, false, false)) {
					int countBefore = extracted.getCount();
					extracted = inv.extractItem(slot, stack.getCount(), simulate);
					stack.shrink(extracted.getCount() - countBefore);
				}
			}
			return extracted.isEmpty() ? ItemStack.EMPTY : extracted;
		}
		return ItemStack.EMPTY;
	}

	/**
	 * Extracts the given {@link ItemStack} from the inventory in the
	 * {@link TileEntity} provided. It will extract the {@link ItemStack} from
	 * the first slot it finds with the {@link ItemStack} in it
	 * 
	 * @param te
	 *            The {@link TileEntity} which has an inventory
	 * @param stack
	 *            The {@link ItemStack} to extract
	 * @param simulate
	 *            Whether it is a simulation, if it is, no items will actually
	 *            be taken
	 * @param side
	 *            The side of the inventory to extract the {@link ItemStack}
	 *            from. For use with {@link ISidedInventory} and
	 *            {@link Capability}
	 * @return The items which were extracted. If no items were extracted (or
	 *         supposedly extracted if a simulation) it will return
	 *         {@link ItemStack#EMPTY}
	 */
	public static ItemStack extractStackFromInventory(@Nullable TileEntity te, ItemStack stack, boolean simulate,
			@Nullable EnumFacing side) {
		return extractStackFromInventory(te, stack, getFirstSlotIndex(te, side), getLastSlotIndex(te, side), simulate,
				side);
	}

	/**
	 * Extracts the first {@link ItemStack} found from the inventory in the
	 * {@link TileEntity} provided.
	 * 
	 * @param te
	 *            The {@link TileEntity} which has an inventory
	 * @param amount
	 *            The amount of the first item found to extract
	 * @param fromSlot
	 *            The slot to start looking to take from
	 * @param toSlot
	 *            The slot to stop looking to take from
	 * @param simulate
	 *            Whether it is a simulation, if it is, no items will actually
	 *            be taken
	 * @param side
	 *            The side of the inventory to extract the {@link ItemStack}
	 *            from. For use with {@link ISidedInventory} and
	 *            {@link Capability}
	 * @return The items which were extracted. If no items were extracted (or
	 *         supposedly extracted if a simulation) it will return
	 *         {@link ItemStack#EMPTY}
	 */
	public static ItemStack extractStackFromInventory(@Nullable TileEntity te, int amount, int fromSlot, int toSlot,
			boolean simulate, @Nullable EnumFacing side) {
		if (te == null)
			return ItemStack.EMPTY;
		ImmutableList<ItemStack> inventory = getInventory(te, fromSlot, toSlot, side);
		if (inventory.size() <= 0)
			return ItemStack.EMPTY;
		ItemStack stack = inventory.get(0);
		stack.setCount(Math.min(amount, stack.getCount()));
		return extractStackFromInventory(te, stack, simulate, side);
	}

	/**
	 * Extracts the first {@link ItemStack} found from the inventory in the
	 * {@link TileEntity} provided.
	 * 
	 * @param te
	 *            The {@link TileEntity} which has an inventory
	 * @param amount
	 *            The amount of the first item found to extract
	 * @param simulate
	 *            Whether it is a simulation, if it is, no items will actually
	 *            be taken
	 * @param side
	 *            The side of the inventory to extract the {@link ItemStack}
	 *            from. For use with {@link ISidedInventory} and
	 *            {@link Capability}
	 * @return The items which were extracted. If no items were extracted (or
	 *         supposedly extracted if a simulation) it will return
	 *         {@link ItemStack#EMPTY}
	 */
	public static ItemStack extractStackFromInventory(@Nullable TileEntity te, int amount, boolean simulate,
			@Nullable EnumFacing side) {
		return extractStackFromInventory(te, amount, getFirstSlotIndex(te, side), getLastSlotIndex(te, side), simulate,
				side);
	}

	/**
	 * Extracts the given {@link ItemStack} from the inventory in the
	 * {@link ItemStack} provided.
	 * 
	 * @param inventory
	 *            The {@link ItemStack} which has an inventory
	 * @param stack
	 *            The {@link ItemStack} to extract
	 * @param fromSlot
	 *            The slot to start looking to take from
	 * @param toSlot
	 *            The slot to stop looking to take from
	 * @param simulate
	 *            Whether it is a simulation, if it is, no items will actually
	 *            be taken
	 * @param side
	 *            The side of the inventory to extract the {@link ItemStack}
	 *            from. For use with {@link Capability}
	 * @return The items which were extracted. If no items were extracted (or
	 *         supposedly extracted if a simulation) it will return
	 *         {@link ItemStack#EMPTY}
	 */
	public static ItemStack extractStackFromInventory(@Nullable ItemStack inventory, ItemStack stack, int fromSlot,
			int toSlot, boolean simulate, @Nullable EnumFacing side) {
		if (inventory == null || inventory.isEmpty())
			return ItemStack.EMPTY;
		ItemStack extracted = stack.copy();
		extracted.setCount(0);
		if (inventory.getItem() instanceof IInventory) {
			IInventory inv = (IInventory) inventory.getItem();
			if (inv.getSizeInventory() - 1 < toSlot || fromSlot > toSlot || inv.getSizeInventory() - 1 < fromSlot)
				return ItemStack.EMPTY;
			for (int slot = fromSlot; slot <= toSlot; slot++) {
				ItemStack inSlot = inv.getStackInSlot(slot);
				if (isStackEqual(stack, inSlot, false, false)) {
					int decreaseBy = MathHelper.clamp(stack.getCount(), 0, inSlot.getCount());
					if (!simulate)
						inv.decrStackSize(slot, decreaseBy);
					stack.shrink(decreaseBy);
					extracted.grow(decreaseBy);
				}
			}
			return extracted.isEmpty() ? ItemStack.EMPTY : extracted;
		}
		if (inventory.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)) {
			IItemHandler inv = inventory.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
			if (inv.getSlots() - 1 < toSlot || fromSlot > toSlot || inv.getSlots() - 1 < fromSlot)
				return ItemStack.EMPTY;
			for (int slot = fromSlot; slot <= toSlot; slot++) {
				ItemStack inSlot = inv.getStackInSlot(slot);
				if (isStackEqual(stack, inSlot, false, false)) {
					int countBefore = extracted.getCount();
					extracted = inv.extractItem(slot, stack.getCount(), simulate);
					stack.shrink(extracted.getCount() - countBefore);
				}
			}
			return extracted.isEmpty() ? ItemStack.EMPTY : extracted;
		}
		return ItemStack.EMPTY;
	}

	/**
	 * Extracts the given {@link ItemStack} from the inventory in the
	 * {@link ItemStack} provided. It will extract the {@link ItemStack} from
	 * the first slot it finds with the {@link ItemStack} in it
	 * 
	 * @param inventory
	 *            The {@link ItemStack} which has an inventory
	 * @param stack
	 *            The {@link ItemStack} to extract
	 * @param simulate
	 *            Whether it is a simulation, if it is, no items will actually
	 *            be taken
	 * @param side
	 *            The side of the inventory to extract the {@link ItemStack}
	 *            from. For use with {@link Capability}
	 * @return The items which were extracted. If no items were extracted (or
	 *         supposedly extracted if a simulation) it will return
	 *         {@link ItemStack#EMPTY}
	 */
	public static ItemStack extractStackFromInventory(@Nullable ItemStack inventory, ItemStack stack, boolean simulate,
			@Nullable EnumFacing side) {
		return extractStackFromInventory(inventory, stack, getFirstSlotIndex(inventory, side),
				getLastSlotIndex(inventory, side), simulate, side);
	}

	/**
	 * Extracts the first {@link ItemStack} found from the inventory in the
	 * {@link ItemStack} provided.
	 * 
	 * @param inventory
	 *            The {@link ItemStack} which has an inventory
	 * @param amount
	 *            The amount of the first item found to extract
	 * @param fromSlot
	 *            The slot to start looking to take from
	 * @param toSlot
	 *            The slot to stop looking to take from
	 * @param simulate
	 *            Whether it is a simulation, if it is, no items will actually
	 *            be taken
	 * @param side
	 *            The side of the inventory to extract the {@link ItemStack}
	 *            from. For use with {@link Capability}
	 * @return The items which were extracted. If no items were extracted (or
	 *         supposedly extracted if a simulation) it will return
	 *         {@link ItemStack#EMPTY}
	 */
	public static ItemStack extractStackFromInventory(@Nullable ItemStack inventory, int amount, int fromSlot,
			int toSlot, boolean simulate, @Nullable EnumFacing side) {
		if (inventory == null || inventory.isEmpty())
			return ItemStack.EMPTY;
		ImmutableList<ItemStack> inv = getInventory(inventory, fromSlot, toSlot, side);
		if (inv.size() <= 0)
			return ItemStack.EMPTY;
		ItemStack stack = inv.get(0);
		stack.setCount(Math.min(amount, stack.getCount()));
		return extractStackFromInventory(inventory, stack, simulate, side);
	}

	/**
	 * Extracts the first {@link ItemStack} found from the inventory in the
	 * {@link ItemStack} provided.
	 * 
	 * @param inventory
	 *            The {@link ItemStack} which has an inventory
	 * @param amount
	 *            The amount of the first item found to extract
	 * @param simulate
	 *            Whether it is a simulation, if it is, no items will actually
	 *            be taken
	 * @param side
	 *            The side of the inventory to extract the {@link ItemStack}
	 *            from. For use with {@link Capability}
	 * @return The items which were extracted. If no items were extracted (or
	 *         supposedly extracted if a simulation) it will return
	 *         {@link ItemStack#EMPTY}
	 */
	public static ItemStack extractStackFromInventory(@Nullable ItemStack inventory, int amount, boolean simulate,
			@Nullable EnumFacing side) {
		return extractStackFromInventory(inventory, amount, getFirstSlotIndex(inventory, side),
				getLastSlotIndex(inventory, side), simulate, side);
	}

	/**
	 * Returns true if the inventory is full
	 * 
	 * @param te
	 *            The {@link TileEntity} which has an inventory
	 * @param side
	 *            The side of the {@link TileEntity} the inventory is. For use
	 *            with {@link ISidedInventory} and {@link Capability}
	 * @return whether the inventory is full
	 */
	public static boolean isInventoryFull(@Nullable TileEntity te, @Nullable EnumFacing side) {
		return isInventoryFull(te, getFirstSlotIndex(te, side), getLastSlotIndex(te, side), side);
	}

	/**
	 * Returns true if the inventory is full
	 * 
	 * @param te
	 *            The {@link TileEntity} which has an inventory
	 * @param fromSlot
	 *            The slot to start checking at
	 * @param toSlot
	 *            The slot to stop looking at
	 * @param side
	 *            The side of the {@link TileEntity} the inventory is. For use
	 *            with {@link ISidedInventory} and {@link Capability}
	 * @return whether the inventory is full
	 */
	public static boolean isInventoryFull(@Nullable TileEntity te, int fromSlot, int toSlot,
			@Nullable EnumFacing side) {
		if (te == null)
			return true;
		if (te instanceof ISidedInventory) {
			if (side != null) {
				ISidedInventory inv = (ISidedInventory) te;
				int[] slots = inv.getSlotsForFace(side);
				if (slots.length == 0)
					return true;
				for (int slot : slots) {
					if (slot >= fromSlot && slot <= toSlot) {
						ItemStack stack = inv.getStackInSlot(slot);
						if (stack.isEmpty() || stack.getCount() != stack.getMaxStackSize())
							return false;
					}
				}
			}
		}
		if (te instanceof IInventory) {
			IInventory inv = (IInventory) te;
			if (fromSlot <= toSlot && inv.getSizeInventory() > toSlot && inv.getSizeInventory() > fromSlot) {
				for (int slot = fromSlot; slot < toSlot; slot++) {
					ItemStack stack = inv.getStackInSlot(slot);
					if (stack.isEmpty() || stack.getCount() != stack.getMaxStackSize())
						return false;
				}
			}
		}
		if (te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)) {
			IItemHandler handler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
			if (fromSlot <= toSlot && handler.getSlots() > toSlot && handler.getSlots() > fromSlot) {
				for (int slot = fromSlot; slot < toSlot; slot++) {
					ItemStack stack = handler.getStackInSlot(slot);
					if (stack.isEmpty() || stack.getCount() != stack.getMaxStackSize())
						return false;
				}
			}
		}
		return true;
	}

	/**
	 * Returns true if the inventory is full
	 * 
	 * @param inventory
	 *            The {@link ItemStack} which has an inventory
	 * @param side
	 *            The side of the {@link ItemStack} the inventory is. For use
	 *            with {@link Capability}
	 * @return whether the inventory is full
	 */
	public static boolean isInventoryFull(@Nullable ItemStack inventory, @Nullable EnumFacing side) {
		return isInventoryFull(inventory, getFirstSlotIndex(inventory, side), getLastSlotIndex(inventory, side), side);
	}

	/**
	 * Returns true if the inventory is full
	 * 
	 * @param inventory
	 *            The {@link ItemStack} which has an inventory
	 * @param fromSlot
	 *            The slot to start checking at
	 * @param toSlot
	 *            The slot to stop looking at
	 * @param side
	 *            The side of the {@link ItemStack} the inventory is. For use
	 *            with {@link Capability}
	 * @return whether the inventory is full
	 */
	public static boolean isInventoryFull(@Nullable ItemStack inventory, int fromSlot, int toSlot,
			@Nullable EnumFacing side) {
		if (inventory == null || inventory.isEmpty())
			return true;
		if (inventory.getItem() instanceof IInventory) {
			IInventory inv = (IInventory) inventory.getItem();
			if (fromSlot <= toSlot && inv.getSizeInventory() > toSlot && inv.getSizeInventory() > fromSlot) {
				for (int slot = fromSlot; slot < toSlot; slot++) {
					ItemStack stack = inv.getStackInSlot(slot);
					if (stack.isEmpty() || stack.getCount() != stack.getMaxStackSize())
						return false;
				}
			}
		}
		if (inventory.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)) {
			IItemHandler handler = inventory.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
			if (fromSlot <= toSlot && handler.getSlots() > toSlot && handler.getSlots() > fromSlot) {
				for (int slot = fromSlot; slot < toSlot; slot++) {
					ItemStack stack = handler.getStackInSlot(slot);
					if (stack.isEmpty() || stack.getCount() != stack.getMaxStackSize())
						return false;
				}
			}
		}
		return true;
	}

	/**
	 * Gets all of the items in the {@link TileEntity}'s inventory in an
	 * {@link ImmutableList}
	 * 
	 * @param te
	 *            The {@link TileEntity} which has an inventory
	 * @param fromSlot
	 *            The first slot to start getting items from
	 * @param toSlot
	 *            The last slot to get the items from
	 * @param side
	 *            The side of the {@link TileEntity} the inventory is. For use
	 *            with {@link ISidedInventory} and {@link Capability}
	 * @return All of the items in the {@link TileEntity}'s inventory
	 */
	public static ImmutableList<ItemStack> getInventory(@Nullable TileEntity te, int fromSlot, int toSlot,
			@Nullable EnumFacing side) {
		if (te == null)
			return ImmutableList.<ItemStack>of();
		List<ItemStack> inventory = new ArrayList<ItemStack>();
		if (te instanceof ISidedInventory) {
			ISidedInventory inv = (ISidedInventory) te;
			int[] slots = inv.getSlotsForFace(side);
			if (slots.length == 0 || slots[0] > toSlot || fromSlot > toSlot || slots[slots.length - 1] < fromSlot)
				return ImmutableList.<ItemStack>copyOf(inventory);
			for (int slot : slots)
				if (slot <= toSlot && slot >= fromSlot)
					inventory.add(inv.getStackInSlot(slot));
		} else if (te instanceof IInventory) {
			IInventory inv = (IInventory) te;
			if (inv.getSizeInventory() - 1 < toSlot || fromSlot > toSlot || inv.getSizeInventory() - 1 < fromSlot)
				return ImmutableList.<ItemStack>copyOf(inventory);
			for (int slot = fromSlot; slot <= toSlot; slot++)
				inventory.add(inv.getStackInSlot(slot));
		} else if (te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)) {
			IItemHandler inv = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
			if (inv.getSlots() - 1 < toSlot || fromSlot > toSlot || inv.getSlots() - 1 < fromSlot)
				return ImmutableList.<ItemStack>copyOf(inventory);
			for (int slot = fromSlot; slot <= toSlot; slot++)
				inventory.add(inv.getStackInSlot(slot));
		}
		return ImmutableList.<ItemStack>copyOf(inventory);
	}

	/**
	 * Gets all of the items in the {@link ItemStack}'s inventory in an
	 * {@link ImmutableList}
	 * 
	 * @param stack
	 *            The {@link ItemStack} which has an inventory
	 * @param fromSlot
	 *            The first slot to start getting items from
	 * @param toSlot
	 *            The last slot to get the items from
	 * @param side
	 *            The side of the {@link ItemStack} the inventory is. For use
	 *            with {@link ISidedInventory} and {@link Capability}
	 * @return All of the items in the {@link ItemStack}'s inventory
	 */
	public static ImmutableList<ItemStack> getInventory(@Nullable ItemStack stack, int fromSlot, int toSlot,
			@Nullable EnumFacing side) {
		if (stack == null || stack.isEmpty())
			return ImmutableList.<ItemStack>of();
		List<ItemStack> inventory = new ArrayList<ItemStack>();
		if (stack.getItem() instanceof IInventory) {
			IInventory inv = (IInventory) stack.getItem();
			if (inv.getSizeInventory() - 1 < toSlot || fromSlot > toSlot || inv.getSizeInventory() - 1 < fromSlot)
				return ImmutableList.<ItemStack>copyOf(inventory);
			for (int slot = fromSlot; slot <= toSlot; slot++)
				inventory.add(inv.getStackInSlot(slot));
		} else if (stack.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)) {
			IItemHandler inv = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
			if (inv.getSlots() - 1 < toSlot || fromSlot > toSlot || inv.getSlots() - 1 < fromSlot)
				return ImmutableList.<ItemStack>copyOf(inventory);
			for (int slot = fromSlot; slot <= toSlot; slot++)
				inventory.add(inv.getStackInSlot(slot));
		}
		return ImmutableList.<ItemStack>copyOf(inventory);
	}

	/**
	 * Gets all of the items in the {@link TileEntity}'s inventory in an
	 * {@link ImmutableList}
	 * 
	 * @param te
	 *            The {@link TileEntity} which has an inventory
	 * @param side
	 *            The side of the {@link TileEntity} the inventory is. For use
	 *            with {@link ISidedInventory} and {@link Capability}
	 * @return All of the items in the {@link TileEntity}'s inventory
	 */
	public static ImmutableList<ItemStack> getInventory(@Nullable TileEntity te, @Nullable EnumFacing side) {
		return getInventory(te, getFirstSlotIndex(te, side), getLastSlotIndex(te, side), side);
	}

	/**
	 * Gets all of the items in the {@link ItemStack}'s inventory in an
	 * {@link ImmutableList}
	 * 
	 * @param stack
	 *            The {@link ItemStack} which has an inventory
	 * @param side
	 *            The side of the {@link ItemStack} the inventory is. For use
	 *            with {@link ISidedInventory} and {@link Capability}
	 * @return All of the items in the {@link ItemStack}'s inventory
	 */
	public static ImmutableList<ItemStack> getInventory(@Nullable ItemStack stack, @Nullable EnumFacing side) {
		return getInventory(stack, getFirstSlotIndex(stack, side), getLastSlotIndex(stack, side), side);
	}

	/**
	 * Get the inventory from the given {@link TileEntity} where all items with
	 * similar description will be placed together like in a set ignoring the
	 * max stack size
	 * 
	 * @param te
	 *            The {@link TileEntity} which has an inventory
	 * @param fromSlot
	 *            The first slot to start getting items from
	 * @param toSlot
	 *            The last slot to get the items from
	 * @param side
	 *            The side of the {@link TileEntity} the inventory is. For use
	 *            with {@link ISidedInventory} and {@link Capability}
	 * @return The items in the inventory "stacked"
	 */
	public static ImmutableList<ItemStack> getInventoryStacked(@Nullable TileEntity te, int fromSlot, int toSlot,
			@Nullable EnumFacing side) {
		if (te == null)
			return ImmutableList.<ItemStack>of();
		return ImmutableList.<ItemStack>copyOf(new ItemStackSet(getInventory(te, fromSlot, toSlot, side)).getStacks());
	}

	/**
	 * Get the inventory from the given {@link TileEntity} where all items with
	 * similar description will be placed together like in a set ignoring the
	 * max stack size
	 * 
	 * @param te
	 *            The {@link TileEntity} which has an inventory
	 * @param side
	 *            The side of the {@link TileEntity} the inventory is. For use
	 *            with {@link ISidedInventory} and {@link Capability}
	 * @return The items in the inventory "stacked"
	 */
	public static ImmutableList<ItemStack> getInventoryStacked(@Nullable TileEntity te, @Nullable EnumFacing side) {
		return getInventoryStacked(te, getFirstSlotIndex(te, side), getLastSlotIndex(te, side), side);
	}

	/**
	 * Get the inventory from the given {@link ItemStack} where all items with
	 * similar description will be placed together like in a set ignoring the
	 * max stack size
	 * 
	 * @param inventory
	 *            The {@link ItemStack} which has an inventory
	 * @param fromSlot
	 *            The first slot to start getting items from
	 * @param toSlot
	 *            The last slot to get the items from
	 * @param side
	 *            The side of the {@link ItemStack} the inventory is. For use
	 *            with {@link Capability}
	 * @return The items in the inventory "stacked"
	 */
	public static ImmutableList<ItemStack> getInventoryStacked(@Nullable ItemStack inventory, int fromSlot, int toSlot,
			@Nullable EnumFacing side) {
		if (inventory == null || inventory.isEmpty())
			return ImmutableList.<ItemStack>of();
		return ImmutableList
				.<ItemStack>copyOf(new ItemStackSet(getInventory(inventory, fromSlot, toSlot, side)).getStacks());
	}

	/**
	 * Get the inventory from the given {@link ItemStack} where all items with
	 * similar description will be placed together like in a set ignoring the
	 * max stack size
	 * 
	 * @param inventory
	 *            The {@link ItemStack} which has an inventory
	 * @param side
	 *            The side of the {@link ItemStack} the inventory is. For use
	 *            with {@link Capability}
	 * @return The items in the inventory "stacked"
	 */
	public static ImmutableList<ItemStack> getInventoryStacked(@Nullable ItemStack inventory,
			@Nullable EnumFacing side) {
		return getInventoryStacked(inventory, getFirstSlotIndex(inventory, side), getLastSlotIndex(inventory, side),
				side);
	}

	/**
	 * Get the player's hotbar inventory
	 * 
	 * @param player
	 *            The player to get the hotbar from
	 * @param includeOffhand
	 *            Whether to include the offhand
	 * @return The player's hotbar
	 */
	public static ImmutableList<ItemStack> getHotbar(@Nonnull EntityPlayer player, boolean includeOffhand) {
		List<ItemStack> inv = new ArrayList<ItemStack>();
		for (int i = 0; i < 8; i++)
			inv.add(player.inventory.mainInventory.get(i));
		if (includeOffhand)
			inv.add(player.getHeldItemOffhand());
		return ImmutableList.<ItemStack>copyOf(inv);
	}

	/**
	 * Will drop the inventory from the given {@link TileEntity} as items
	 * 
	 * @param world
	 *            The world in which the items will spawn in
	 * @param x
	 *            The x position of where the items will spawn
	 * @param y
	 *            The y position of where the items will spawn
	 * @param z
	 *            The z position of where the items will spawn
	 * @param te
	 *            The {@link TileEntity} which has the inventory
	 * @param fromSlot
	 *            The first slot to get the items from
	 * @param toSlot
	 *            The last slot to get the items from
	 * @param side
	 *            The side of the {@link TileEntity} which has the inventory.
	 *            For use with {@link ISidedInventory} and {@link Capability}
	 */
	public static void dropInventoryAsItems(@Nonnull World world, double x, double y, double z, @Nullable TileEntity te,
			int fromSlot, int toSlot, @Nullable EnumFacing side) {
		ImmutableList<ItemStack> inventory = getInventory(te, fromSlot, toSlot, side);
		inventory.forEach(stack -> {
			world.spawnEntity(new EntityItem(world, x, y, z, stack));
		});
	}

	/**
	 * Will drop the inventory from the given {@link TileEntity} as items
	 * 
	 * @param world
	 *            The world in which the items will spawn in
	 * @param pos
	 *            The position in the {@link World} where the items will spawn
	 * @param te
	 *            The {@link TileEntity} which has the inventory
	 * @param fromSlot
	 *            The first slot to get the items from
	 * @param toSlot
	 *            The last slot to get the items from
	 * @param side
	 *            The side of the {@link TileEntity} which has the inventory.
	 *            For use with {@link ISidedInventory} and {@link Capability}
	 */
	public static void dropInventoryAsItems(@Nonnull World world, @Nonnull BlockPos pos, @Nullable TileEntity te,
			int fromSlot, int toSlot, @Nullable EnumFacing side) {
		dropInventoryAsItems(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, te, fromSlot, toSlot,
				side);
	}

	/**
	 * Will drop the inventory from the given {@link TileEntity} as items
	 * 
	 * @param world
	 *            The world in which the items will spawn in
	 * @param x
	 *            The x position of where the items will spawn
	 * @param y
	 *            The y position of where the items will spawn
	 * @param z
	 *            The z position of where the items will spawn
	 * @param te
	 *            The {@link TileEntity} which has the inventory
	 * @param side
	 *            The side of the {@link TileEntity} which has the inventory.
	 *            For use with {@link ISidedInventory} and {@link Capability}
	 */
	public static void dropInventoryAsItems(@Nonnull World world, double x, double y, double z, @Nullable TileEntity te,
			@Nullable EnumFacing side) {
		dropInventoryAsItems(world, x, y, z, te, getFirstSlotIndex(te, side), getLastSlotIndex(te, side), side);
	}

	/**
	 * Will drop the inventory from the given {@link TileEntity} as items
	 * 
	 * @param world
	 *            The world in which the items will spawn in
	 * @param pos
	 *            The position in the {@link World} where the items will spawn
	 * @param te
	 *            The {@link TileEntity} which has the inventory
	 * @param side
	 *            The side of the {@link TileEntity} which has the inventory.
	 *            For use with {@link ISidedInventory} and {@link Capability}
	 */
	public static void dropInventoryAsItems(@Nonnull World world, @Nonnull BlockPos pos, @Nullable TileEntity te,
			@Nullable EnumFacing side) {
		dropInventoryAsItems(world, pos, te, getFirstSlotIndex(te, side), getLastSlotIndex(te, side), side);
	}

	/**
	 * Will drop the inventory from the given {@link TileEntity} as items. The
	 * items will spawn where the tile entity is
	 * 
	 * @param world
	 *            The world in which the items will spawn in
	 * @param te
	 *            The {@link TileEntity} which has the inventory
	 * @param side
	 *            The side of the {@link TileEntity} which has the inventory.
	 *            For use with {@link ISidedInventory} and {@link Capability}
	 */
	public static void dropInventoryAsItems(@Nonnull World world, @Nullable TileEntity te, @Nullable EnumFacing side) {
		if (te == null)
			return;
		dropInventoryAsItems(world, te.getPos(), te, side);
	}

	/**
	 * Will drop the inventory from the given {@link TileEntity} as items. The
	 * items will spawn where the tile entity is
	 * 
	 * @param world
	 *            The world in which the items will spawn in
	 * @param te
	 *            The {@link TileEntity} which has the inventory
	 * @param fromSlot
	 *            The first slot to get the items from
	 * @param toSlot
	 *            The last slot to get the items from
	 * @param side
	 *            The side of the {@link TileEntity} which has the inventory.
	 *            For use with {@link ISidedInventory} and {@link Capability}
	 */
	public static void dropInventoryAsItems(@Nonnull World world, @Nullable TileEntity te, int fromSlot, int toSlot,
			@Nullable EnumFacing side) {
		if (te == null)
			return;
		dropInventoryAsItems(world, te.getPos(), te, fromSlot, toSlot, side);
	}

	/**
	 * Will drop the inventory from the given {@link ItemStack} as items
	 * 
	 * @param world
	 *            The world in which the items will spawn in
	 * @param x
	 *            The x position of where the items will spawn
	 * @param y
	 *            The y position of where the items will spawn
	 * @param z
	 *            The z position of where the items will spawn
	 * @param inventory
	 *            The {@link ItemStack} which has the inventory
	 * @param fromSlot
	 *            The first slot to get the items from
	 * @param toSlot
	 *            The last slot to get the items from
	 * @param side
	 *            The side of the {@link ItemStack} which has the inventory. For
	 *            use with {@link Capability}
	 */
	public static void dropInventoryAsItems(@Nonnull World world, double x, double y, double z,
			@Nullable ItemStack inventory, int fromSlot, int toSlot, @Nullable EnumFacing side) {
		ImmutableList<ItemStack> inv = getInventory(inventory, fromSlot, toSlot, side);
		inv.forEach(stack -> {
			world.spawnEntity(new EntityItem(world, x, y, z, stack));
		});
	}

	/**
	 * Will drop the inventory from the given {@link ItemStack} as items
	 * 
	 * @param world
	 *            The world in which the items will spawn in
	 * @param pos
	 *            The position in the {@link World} where the items will spawn
	 * @param inventory
	 *            The {@link ItemStack} which has the inventory
	 * @param fromSlot
	 *            The first slot to get the items from
	 * @param toSlot
	 *            The last slot to get the items from
	 * @param side
	 *            The side of the {@link ItemStack} which has the inventory. For
	 *            use with {@link Capability}
	 */
	public static void dropInventoryAsItems(@Nonnull World world, @Nonnull BlockPos pos, @Nullable ItemStack inventory,
			int fromSlot, int toSlot, @Nullable EnumFacing side) {
		dropInventoryAsItems(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, inventory, fromSlot,
				toSlot, side);
	}

	/**
	 * Will drop the inventory from the given {@link ItemStack} as items
	 * 
	 * @param world
	 *            The world in which the items will spawn in
	 * @param x
	 *            The x position of where the items will spawn
	 * @param y
	 *            The y position of where the items will spawn
	 * @param z
	 *            The z position of where the items will spawn
	 * @param inventory
	 *            The {@link ItemStack} which has the inventory
	 * @param side
	 *            The side of the {@link ItemStack} which has the inventory. For
	 *            use with {@link Capability}
	 */
	public static void dropInventoryAsItems(@Nonnull World world, double x, double y, double z,
			@Nullable ItemStack inventory, @Nullable EnumFacing side) {
		dropInventoryAsItems(world, x, y, z, inventory, getFirstSlotIndex(inventory, side),
				getLastSlotIndex(inventory, side), side);
	}

	/**
	 * Will drop the inventory from the given {@link ItemStack} as items
	 * 
	 * @param world
	 *            The world in which the items will spawn in
	 * @param pos
	 *            The position in the {@link World} where the items will spawn
	 * @param inventory
	 *            The {@link ItemStack} which has the inventory
	 * @param side
	 *            The side of the {@link ItemStack} which has the inventory. For
	 *            use with {@link Capability}
	 */
	public static void dropInventoryAsItems(@Nonnull World world, @Nonnull BlockPos pos, @Nullable ItemStack inventory,
			@Nullable EnumFacing side) {
		dropInventoryAsItems(world, pos, inventory, getFirstSlotIndex(inventory, side),
				getLastSlotIndex(inventory, side), side);
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

	/**
	 * Converts the given {@link ItemStack} to a nice looking string
	 * 
	 * @param stack
	 *            The {@link ItemStack} to convert to a string
	 * @return The {@link ItemStack} as a string
	 */
	public static String stackToString(ItemStack stack) {
		String string = (stack.getCount() / 64 != 0 ? stack.getCount() / 64 + "x64" : "x")
				+ ((stack.getCount() - ((stack.getCount() / 64) * 64)) != 0
						? " + " + (stack.getCount() - ((stack.getCount() / 64) * 64)) : "")
				+ " " + stack.getDisplayName();
		if ((stack.getCount() / 64) == 0)
			string = string.replace(" + ", "");
		return string;
	}

	/**
	 * States whether the given {@link TileEntity} has an inventory
	 * 
	 * @param te
	 *            The {@link TileEntity} to check
	 * @param side
	 *            The side of the {@link TileEntity} to check. For use with
	 *            {@link ISidedInventory} and {@link Capability}
	 * @return Whether the given {@link TileEntity} has an inventory
	 */
	public static boolean hasSupport(@Nullable TileEntity te, @Nullable EnumFacing side) {
		if (te == null)
			return false;
		return te instanceof IInventory || te instanceof ISidedInventory
				|| te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
	}

	/**
	 * States whether the given {@link ItemStack} has an inventory
	 * 
	 * @param stack
	 *            The {@link ItemStack} to check
	 * @param side
	 *            The side of the {@link ItemStack} to check. For use with
	 *            {@link ISidedInventory} and {@link Capability}
	 * @return Whether the given {@link ItemStack} has an inventory
	 */
	public static boolean hasSupport(@Nullable ItemStack stack, @Nullable EnumFacing side) {
		if (stack == null || stack.isEmpty())
			return false;
		return stack.getItem() instanceof IInventory
				|| stack.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
	}

	/**
	 * Saves data when syncing from the server
	 * 
	 * @param modid
	 *            The modid so each mod has its own cache of data
	 * @param className
	 *            The name of the class so each class can only sync one data
	 * @param inventory
	 *            The inventory to sync
	 */
	public static void addCachedInventoryData(String modid, String className, ImmutableList<ItemStack> inventory) {
		if (!cachedInventoryData.containsKey(modid))
			cachedInventoryData.put(modid, new HashMap<String, ImmutableList<ItemStack>>());
		if (!cachedInventoryData.get(modid).containsKey(className))
			cachedInventoryData.get(modid).put(className, inventory);
	}

	/**
	 * Retrieves the latest data from the given class
	 * 
	 * @param modid
	 *            The modid to get mod specific data
	 * @param className
	 *            The name of the class the data was requested from
	 * @return The latest data from the given class
	 */
	@Nullable
	public static ImmutableList<ItemStack> getCachedInventoryData(String modid, String className) {
		if (!cachedInventoryData.containsKey(modid))
			return null;
		if (!cachedInventoryData.get(modid).containsKey(className))
			return null;
		ImmutableList<ItemStack> inventory = cachedInventoryData.get(modid).get(className);
		cachedInventoryData.get(modid).remove(className);
		return inventory;
	}

	/**
	 * Retrieves the latest data from the calling class
	 * 
	 * @param modid
	 *            The modid to get mod specific data
	 * @return The latest data from the calling class
	 */
	@Nullable
	public static ImmutableList<ItemStack> getCachedInventoryData(String modid) {
		return InventoryUtils.getCachedInventoryData(modid, new Exception().getStackTrace()[1].getClassName());
	}

	/**
	 * Sync an inventory with the server. To get the inventory use
	 * {@link #getCachedInventoryData(String)} or
	 * {@link #getCachedInventoryData(String, String)}. This will store the data
	 * in the class path provided in the cache
	 * 
	 * @param pos
	 *            The position of the {@link TileEntity} with the inventory
	 * @param side
	 *            The side of the {@link TileEntity} for use with
	 *            {@link ISidedInventory} and {@link Capability}
	 * @param fromSlot
	 *            The first slot to get the inventory from
	 * @param toSlot
	 *            The last slot to get the inventory from
	 * @param modid
	 *            The modid for mod specific data
	 * @param className
	 *            The name of the class for the cache
	 */
	public static void syncInventory(BlockPos pos, @Nullable EnumFacing side, int fromSlot, int toSlot, String modid,
			String className) {
		PacketHandler.INSTANCE
				.sendToServer(new PacketGetInventory(pos, side, false, fromSlot, toSlot, false, modid, className));
	}

	/**
	 * Sync an inventory with the server. To get the inventory use
	 * {@link #getCachedInventoryData(String)} or
	 * {@link #getCachedInventoryData(String, String)}. This will store the data
	 * in the class path provided in the cache. The inventory will be "stacked"
	 * - see {@link #getInventoryStacked(TileEntity, int, int, EnumFacing)}.
	 * 
	 * @param pos
	 *            The position of the {@link TileEntity} with the inventory
	 * @param side
	 *            The side of the {@link TileEntity} for use with
	 *            {@link ISidedInventory} and {@link Capability}
	 * @param fromSlot
	 *            The first slot to get the inventory from
	 * @param toSlot
	 *            The last slot to get the inventory from
	 * @param modid
	 *            The modid for mod specific data
	 * @param className
	 *            The name of the class for the cache
	 */
	public static void syncInventoryStacked(BlockPos pos, @Nullable EnumFacing side, int fromSlot, int toSlot,
			String modid, String className) {
		PacketHandler.INSTANCE
				.sendToServer(new PacketGetInventory(pos, side, true, fromSlot, toSlot, false, modid, className));
	}

	/**
	 * Sync an inventory with the server. To get the inventory use
	 * {@link #getCachedInventoryData(String)} or
	 * {@link #getCachedInventoryData(String, String)}. This will store the data
	 * in the class path provided in the cache
	 * 
	 * @param pos
	 *            The position of the {@link TileEntity} with the inventory
	 * @param side
	 *            The side of the {@link TileEntity} for use with
	 *            {@link ISidedInventory} and {@link Capability}
	 * @param modid
	 *            The modid for mod specific data
	 * @param className
	 *            The name of the class for the cache
	 */
	public static void syncInventory(BlockPos pos, @Nullable EnumFacing side, String modid, String className) {
		PacketHandler.INSTANCE.sendToServer(new PacketGetInventory(pos, side, false, false, modid, className));
	}

	/**
	 * Sync an inventory with the server. To get the inventory use
	 * {@link #getCachedInventoryData(String)} or
	 * {@link #getCachedInventoryData(String, String)}. This will store the data
	 * in the class path provided in the cache. The inventory will be "stacked"
	 * - see {@link #getInventoryStacked(TileEntity, int, int, EnumFacing)}
	 * 
	 * @param pos
	 *            The position of the {@link TileEntity} with the inventory
	 * @param side
	 *            The side of the {@link TileEntity} for use with
	 *            {@link ISidedInventory} and {@link Capability}
	 * @param modid
	 *            The modid for mod specific data
	 * @param className
	 *            The name of the class for the cache
	 */
	public static void syncInventoryStacked(BlockPos pos, @Nullable EnumFacing side, String modid, String className) {
		PacketHandler.INSTANCE.sendToServer(new PacketGetInventory(pos, side, true, false, modid, className));
	}

	/**
	 * Sync an inventory with the server. To get the inventory use
	 * {@link #getCachedInventoryData(String)} or
	 * {@link #getCachedInventoryData(String, String)}. This will store the data
	 * in the class path provided in the cache. The calling class will be used
	 * as the name of the class in the cache
	 * 
	 * @param pos
	 *            The position of the {@link TileEntity} with the inventory
	 * @param side
	 *            The side of the {@link TileEntity} for use with
	 *            {@link ISidedInventory} and {@link Capability}
	 * @param fromSlot
	 *            The first slot to get the inventory from
	 * @param toSlot
	 *            The last slot to get the inventory from
	 * @param modid
	 *            The modid for mod specific data
	 */
	public static void syncInventory(BlockPos pos, @Nullable EnumFacing side, int fromSlot, int toSlot, String modid) {
		PacketHandler.INSTANCE.sendToServer(new PacketGetInventory(pos, side, false, fromSlot, toSlot, false, modid,
				new Exception().getStackTrace()[1].getClassName()));
	}

	/**
	 * Sync an inventory with the server. To get the inventory use
	 * {@link #getCachedInventoryData(String)} or
	 * {@link #getCachedInventoryData(String, String)}. This will store the data
	 * in the class path provided in the cache. The inventory will be "stacked"
	 * - see {@link #getInventoryStacked(TileEntity, int, int, EnumFacing)}. The
	 * calling class will be used as the name of the class in the cache
	 * 
	 * @param pos
	 *            The position of the {@link TileEntity} with the inventory
	 * @param side
	 *            The side of the {@link TileEntity} for use with
	 *            {@link ISidedInventory} and {@link Capability}
	 * @param fromSlot
	 *            The first slot to get the inventory from
	 * @param toSlot
	 *            The last slot to get the inventory from
	 * @param modid
	 *            The modid for mod specific data
	 */
	public static void syncInventoryStacked(BlockPos pos, @Nullable EnumFacing side, int fromSlot, int toSlot,
			String modid) {
		PacketHandler.INSTANCE.sendToServer(new PacketGetInventory(pos, side, true, fromSlot, toSlot, false, modid,
				new Exception().getStackTrace()[1].getClassName()));
	}

	/**
	 * Sync an inventory with the server. To get the inventory use
	 * {@link #getCachedInventoryData(String)} or
	 * {@link #getCachedInventoryData(String, String)}. This will store the data
	 * in the class path provided in the cache. The calling class will be used
	 * as the name of the class in the cache
	 * 
	 * @param pos
	 *            The position of the {@link TileEntity} with the inventory
	 * @param side
	 *            The side of the {@link TileEntity} for use with
	 *            {@link ISidedInventory} and {@link Capability}
	 * @param modid
	 *            The modid for mod specific data
	 */
	public static void syncInventory(BlockPos pos, @Nullable EnumFacing side, String modid) {
		PacketHandler.INSTANCE.sendToServer(new PacketGetInventory(pos, side, false, false, modid,
				new Exception().getStackTrace()[1].getClassName()));
	}

	/**
	 * Sync an inventory with the server. To get the inventory use
	 * {@link #getCachedInventoryData(String)} or
	 * {@link #getCachedInventoryData(String, String)}. This will store the data
	 * in the class path provided in the cache. The
	 * inventory will be "stacked" - see
	 * {@link #getInventoryStacked(TileEntity, int, int, EnumFacing)}. The
	 * calling class will be used as the name of the class in the cache
	 * 
	 * @param pos
	 *            The position of the {@link TileEntity} with the inventory
	 * @param side
	 *            The side of the {@link TileEntity} for use with
	 *            {@link ISidedInventory} and {@link Capability}
	 * @param modid
	 *            The modid for mod specific data
	 */
	public static void syncInventoryStacked(BlockPos pos, @Nullable EnumFacing side, String modid) {
		PacketHandler.INSTANCE.sendToServer(new PacketGetInventory(pos, side, true, false, modid,
				new Exception().getStackTrace()[1].getClassName()));
	}

	/**
	 * Sync an inventory with the server. The synced inventory will replace a
	 * field in the class provided with the name provided. Make sure that this
	 * field is an instance of {@link ImmutableList} of type {@link ItemStack}.
	 * 
	 * @param pos
	 *            The position of the {@link TileEntity} with the inventory
	 * @param side
	 *            The side of the {@link TileEntity} for use with
	 *            {@link ISidedInventory} and {@link Capability}
	 * @param fromSlot
	 *            The first slot to get the inventory from
	 * @param toSlot
	 *            The last slot to get the inventory from
	 * @param className
	 *            The name of the class which holds the field to be replaced
	 * @param inventoryFieldName
	 *            The name of the field which will be replaced and will hold the
	 *            synced inventory
	 */
	public static void syncInventoryField(BlockPos pos, @Nullable EnumFacing side, int fromSlot, int toSlot,
			String className, String inventoryFieldName) {
		PacketHandler.INSTANCE.sendToServer(
				new PacketGetInventory(pos, side, false, fromSlot, toSlot, true, className, inventoryFieldName));
	}

	/**
	 * Sync an inventory with the server. The synced inventory will replace a
	 * field in the class provided with the name provided. Make sure that this
	 * field is an instance of {@link ImmutableList} of type
	 * {@link ItemStack}.The inventory will be "stacked" - see
	 * {@link #getInventoryStacked(TileEntity, int, int, EnumFacing)}
	 * 
	 * @param pos
	 *            The position of the {@link TileEntity} with the inventory
	 * @param side
	 *            The side of the {@link TileEntity} for use with
	 *            {@link ISidedInventory} and {@link Capability}
	 * @param fromSlot
	 *            The first slot to get the inventory from
	 * @param toSlot
	 *            The last slot to get the inventory from
	 * @param className
	 *            The name of the class which holds the field to be replaced
	 * @param inventoryFieldName
	 *            The name of the field which will be replaced and will hold the
	 *            synced inventory
	 */
	public static void syncInventoryFieldStacked(BlockPos pos, @Nullable EnumFacing side, int fromSlot, int toSlot,
			String className, String inventoryFieldName) {
		PacketHandler.INSTANCE.sendToServer(
				new PacketGetInventory(pos, side, true, fromSlot, toSlot, true, className, inventoryFieldName));
	}

	/**
	 * Sync an inventory with the server. The synced inventory will replace a
	 * field in the class provided with the name provided. Make sure that this
	 * field is an instance of {@link ImmutableList} of type {@link ItemStack}.
	 * 
	 * @param pos
	 *            The position of the {@link TileEntity} with the inventory
	 * @param side
	 *            The side of the {@link TileEntity} for use with
	 *            {@link ISidedInventory} and {@link Capability}
	 * @param className
	 *            The name of the class which holds the field to be replaced
	 * @param inventoryFieldName
	 *            The name of the field which will be replaced and will hold the
	 *            synced inventory
	 */
	public static void syncInventoryField(BlockPos pos, @Nullable EnumFacing side, String className,
			String inventoryFieldName) {
		PacketHandler.INSTANCE
				.sendToServer(new PacketGetInventory(pos, side, false, true, className, inventoryFieldName));
	}

	/**
	 * Sync an inventory with the server. The synced inventory will replace a
	 * field in the class provided with the name provided. Make sure that this
	 * field is an instance of {@link ImmutableList} of type
	 * {@link ItemStack}.The inventory will be "stacked" - see
	 * {@link #getInventoryStacked(TileEntity, int, int, EnumFacing)}
	 * 
	 * @param pos
	 *            The position of the {@link TileEntity} with the inventory
	 * @param side
	 *            The side of the {@link TileEntity} for use with
	 *            {@link ISidedInventory} and {@link Capability}
	 * @param className
	 *            The name of the class which holds the field to be replaced
	 * @param inventoryFieldName
	 *            The name of the field which will be replaced and will hold the
	 *            synced inventory
	 */
	public static void syncInventoryFieldStacked(BlockPos pos, @Nullable EnumFacing side, String className,
			String inventoryFieldName) {
		PacketHandler.INSTANCE
				.sendToServer(new PacketGetInventory(pos, side, true, true, className, inventoryFieldName));
	}

	/**
	 * Sync an inventory with the server. The synced inventory will replace a
	 * field in the class provided with the name provided. Make sure that this
	 * field is an instance of {@link ImmutableList} of type {@link ItemStack}.
	 * 
	 * @param pos
	 *            The position of the {@link TileEntity} with the inventory
	 * @param side
	 *            The side of the {@link TileEntity} for use with
	 *            {@link ISidedInventory} and {@link Capability}
	 * @param fromSlot
	 *            The first slot to get the inventory from
	 * @param toSlot
	 *            The last slot to get the inventory from
	 * @param inventoryFieldName
	 *            The name of the field which will be replaced and will hold the
	 *            synced inventory
	 */
	public static void syncInventoryField(BlockPos pos, @Nullable EnumFacing side, int fromSlot, int toSlot,
			String inventoryFieldName) {
		PacketHandler.INSTANCE.sendToServer(new PacketGetInventory(pos, side, false, fromSlot, toSlot, true,
				new Exception().getStackTrace()[1].getClassName(), inventoryFieldName));
	}

	/**
	 * Sync an inventory with the server. The synced inventory will replace a
	 * field in the class provided with the name provided. Make sure that this
	 * field is an instance of {@link ImmutableList} of type
	 * {@link ItemStack}.The inventory will be "stacked" - see
	 * {@link #getInventoryStacked(TileEntity, int, int, EnumFacing)}
	 * 
	 * @param pos
	 *            The position of the {@link TileEntity} with the inventory
	 * @param side
	 *            The side of the {@link TileEntity} for use with
	 *            {@link ISidedInventory} and {@link Capability}
	 * @param fromSlot
	 *            The first slot to get the inventory from
	 * @param toSlot
	 *            The last slot to get the inventory from
	 * @param inventoryFieldName
	 *            The name of the field which will be replaced and will hold the
	 *            synced inventory
	 */
	public static void syncInventoryFieldStacked(BlockPos pos, @Nullable EnumFacing side, int fromSlot, int toSlot,
			String inventoryFieldName) {
		PacketHandler.INSTANCE.sendToServer(new PacketGetInventory(pos, side, true, fromSlot, toSlot, true,
				new Exception().getStackTrace()[1].getClassName(), inventoryFieldName));
	}

	/**
	 * Sync an inventory with the server. The synced inventory will replace a
	 * field in the class provided with the name provided. Make sure that this
	 * field is an instance of {@link ImmutableList} of type {@link ItemStack}.
	 * 
	 * @param pos
	 *            The position of the {@link TileEntity} with the inventory
	 * @param side
	 *            The side of the {@link TileEntity} for use with
	 *            {@link ISidedInventory} and {@link Capability}
	 * @param inventoryFieldName
	 *            The name of the field which will be replaced and will hold the
	 *            synced inventory
	 */
	public static void syncInventoryField(BlockPos pos, @Nullable EnumFacing side, String inventoryFieldName) {
		PacketHandler.INSTANCE.sendToServer(new PacketGetInventory(pos, side, false, true,
				new Exception().getStackTrace()[1].getClassName(), inventoryFieldName));
	}

	/**
	 * Sync an inventory with the server. The synced inventory will replace a
	 * field in the class provided with the name provided. Make sure that this
	 * field is an instance of {@link ImmutableList} of type
	 * {@link ItemStack}.The inventory will be "stacked" - see
	 * {@link #getInventoryStacked(TileEntity, int, int, EnumFacing)}
	 * 
	 * @param pos
	 *            The position of the {@link TileEntity} with the inventory
	 * @param side
	 *            The side of the {@link TileEntity} for use with
	 *            {@link ISidedInventory} and {@link Capability}
	 * @param inventoryFieldName
	 *            The name of the field which will be replaced and will hold the
	 *            synced inventory
	 */
	public static void syncInventoryFieldStacked(BlockPos pos, @Nullable EnumFacing side, String inventoryFieldName) {
		PacketHandler.INSTANCE.sendToServer(new PacketGetInventory(pos, side, true, true,
				new Exception().getStackTrace()[1].getClassName(), inventoryFieldName));
	}

}
