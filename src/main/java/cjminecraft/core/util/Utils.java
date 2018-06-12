package cjminecraft.core.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.language.bm.Lang;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

import cjminecraft.core.CJCore;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.oredict.OreDictionary;

/**
 * @author CJMinecraft
 */

//TODO: CJMinecraft, please overlook this class and try to move these methods to a better location.
//Or, rename the class to better fit the names of these methods. :)
public class Utils {

	public static final FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);
	public static final FloatBuffer modelview = GLAllocation.createDirectFloatBuffer(16);

	/**
	 * Makes the variables which will be initialized when there getter method is called
	 */
	private static Lang lang;

	/**
	 * @return The player's projection matrix
	 */
	public static Matrix4f getProjectionMatrix() {
		GlStateManager.getFloat(GL11.GL_PROJECTION_MATRIX, projection);
		GlStateManager.getFloat(GL11.GL_MODELVIEW_MATRIX, modelview);
		Matrix4f projectionMatrix = (Matrix4f) new Matrix4f().load(projection.asReadOnlyBuffer());
		Matrix4f modelViewMatrix = (Matrix4f) new Matrix4f().load(modelview.asReadOnlyBuffer());
		Matrix4f result = Matrix4f.mul(modelViewMatrix, projectionMatrix, null);
		return result;
	}

	/**
	 * Returns the logger. This makes System.out.println look shabby
	 * 
	 * @return The {@link Logger}
	 */
	public static Logger getLogger() {
		return CJCore.logger;
	}

	/**
	 * Gets the text from the path specified.
	 * 
	 * @param location
	 *            The location of the text
	 * @return an array holding each line of the text
	 */
	public static List<String> loadTextFromFile(ResourceLocation location) {
		List<String> output = new ArrayList<String>();
		try {
			InputStreamReader is = new InputStreamReader(Minecraft.getMinecraft().getResourceManager().getResource(location).getInputStream());
			BufferedReader reader = new BufferedReader(is);
			String line = reader.readLine();
			while (line != null) {
				output.add(line);
				line = reader.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return output;
	}

	/**
	 * Calculate the redstone current from a item stack handler
	 * 
	 * @param handler
	 *            The handler
	 * @return The redstone power
	 */
	public static int calculateRedstone(IItemHandler handler) {
		int i = 0;
		float f = 0.0F;
		for (int j = 0; j < handler.getSlots(); j++) {
			ItemStack stack = handler.getStackInSlot(j);
			if (!stack.isEmpty()) {
				f += (float) stack.getCount() / (float) Math.min(handler.getStackInSlot(j).getMaxStackSize(), stack.getMaxStackSize());
				i++;
			}
		}
		f = f / (float) handler.getSlots();
		return (int) (Math.floor(f * 14.0F) + (i > 0 ? 1 : 0));
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
		return addStackToInventory(handler, handler.getSlots(), stack, simulate);
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
		ItemStack remainer = stack.copy();
		for(int slot = 0; slot < maxSlot; slot++) {
			ItemStack tempStack = handler.insertItem(slot, remainer, simulate);
			if(tempStack.isEmpty())
				return ItemStack.EMPTY;
			remainer = tempStack.copy();
		}
		return remainer;
	}

	/**
	 * Takes the chosen item stack from a specified slot to the inventory
	 * 
	 * @param handler
	 *            The holder of the items
	 * @param maxSlot
	 *            The max slot to take from
	 * @param amount
	 *            The amount to take
	 * @param simulate
	 *            Is the task a simulation?
	 * @return The remainder left if the slot was full
	 */
	public static ItemStack removeStackFromInventory(IItemHandler handler, int maxSlot, int amount, boolean simulate) {
		ItemStack remainder = ItemStack.EMPTY;
		for (int slot = 0; slot < maxSlot; slot++) {
			remainder = handler.extractItem(slot, amount, simulate);
			if (remainder.isEmpty())
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
		return isInventoryFull(handler, handler.getSlots());
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
		for (int slot = 0; slot < maxSlot; slot++) {
			if (handler.getStackInSlot(slot).getCount() < handler.getStackInSlot(slot).getMaxStackSize()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Gets the correct colour from any item stack using the ore dictionary The item must be registered as a dye
	 * 
	 * @param stack
	 *            The {@link ItemStack} to test
	 * @return The {@link EnumDyeColor} of the {@link ItemStack} to test. If the stack is not registered as a dye, the {@link EnumDyeColor#WHITE} will be used
	 */
	public static EnumDyeColor getColorFromDye(ItemStack stack) {
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

	public static boolean checkSurroundingBlocks(World world, IBlockState state) {
		for (int x = -1; x < 2; x++) {
			for (int y = -1; y < 2; y++) {
				for (int z = -1; z < 2; z++) {
					if (world.getBlockState(new BlockPos(x, y, z)) == state)
						return true;
				}
			}
		}
		return false;
	}

	public static boolean checkSurroundingBlocks(World world) {
		for (int x = -1; x < 2; x++) {
			for (int y = -1; y < 2; y++) {
				for (int z = -1; z < 2; z++) {
					if (world.getBlockState(new BlockPos(x, y, z)) != Blocks.AIR)
						return true;
				}
			}
		}
		return false;
	}
}