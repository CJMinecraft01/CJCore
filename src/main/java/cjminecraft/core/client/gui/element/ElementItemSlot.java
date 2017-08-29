package cjminecraft.core.client.gui.element;

import java.util.List;

import cjminecraft.core.CJCore;
import cjminecraft.core.client.gui.GuiCore;
import cjminecraft.core.client.gui.ISpecialOverlayElement;
import net.minecraft.item.ItemStack;

/**
 * A simple item slot display for any {@link ItemStack}
 * 
 * @author CJMinecraft
 *
 */
public class ElementItemSlot extends ElementBase implements ISpecialOverlayElement {

	protected ItemStack stack = ItemStack.EMPTY;

	/**
	 * A simple item slot display for any {@link ItemStack}
	 * 
	 * @param gui
	 *            The parent gui
	 * @param posX
	 *            The x position relative to the gui's x position
	 * @param posY
	 *            The y position relative to the gui's y position
	 */
	public ElementItemSlot(GuiCore gui, int posX, int posY) {
		super(gui, posX, posY, 18, 18);
	}

	/**
	 * Set the {@link ItemStack} to draw
	 * 
	 * @param stack
	 *            The {@link ItemStack} to draw
	 * @return The updated element
	 */
	public ElementItemSlot setStack(ItemStack stack) {
		this.stack = stack;
		return this;
	}

	@Override
	public void drawBackground(int mouseX, int mouseY, float gameTicks) {
		this.gui.drawSizedRectWithBorder(this.posX, this.posY, this.posX + this.width, this.posY + this.height, 0xFF8B8B8B);
		this.gui.drawItemStack(this.stack, this.posX + 1 , this.posY + 1, true, null);
	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {

	}
	
	@Override
	public void drawSpecialLayer() {
	}
	
	@Override
	public void addOverlayText(List<String> text) {
		text.add(this.stack.getDisplayName());
	}

	/**
	 * @return The {@link ItemStack} in the slot
	 */
	public ItemStack getStack() {
		return this.stack;
	}
	
}
