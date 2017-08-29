package cjminecraft.core.client.gui.element;

import cjminecraft.core.client.gui.GuiBase;
import cjminecraft.core.client.gui.GuiCore;

/**
 * A basic element which draws a simple rectangle
 * 
 * Adaptation from CoFH Lib with permission from KingLemming
 * 
 * @author CJMinecraft
 *
 */
public class ElementBox extends ElementBase {

	protected int colour;

	/**
	 * A basic element which draws a simple rectangle
	 * 
	 * @param gui
	 *            The parent {@link GuiBase}
	 * @param posX
	 *            The x position relative to the parent {@link GuiCore}
	 * @param posY
	 *            The y position relative to the parent {@link GuiCore}
	 * @param colour
	 *            The colour of the rectangle in the format 0xAARRGGBB
	 */
	public ElementBox(GuiCore gui, int posX, int posY, Number colour) {
		this(gui, posX, posY, 16, 16, colour);
	}

	/**
	 * A basic element which draws a simple rectangle
	 * 
	 * @param gui
	 *            The parent {@link GuiBase}
	 * @param posX
	 *            The x position relative to the parent {@link GuiCore}
	 * @param posY
	 *            The y position relative to the parent {@link GuiCore}
	 * @param width
	 *            The width of the rectangle
	 * @param height
	 *            The height of the rectangle
	 * @param colour
	 *            The colour of the rectangle in the format 0xAARRGGBB
	 */
	public ElementBox(GuiCore gui, int posX, int posY, int width, int height, Number colour) {
		super(gui, posX, posY, width, height);
		setColour(colour);
	}

	/**
	 * Set the colour of the rectangle
	 * 
	 * @param colour
	 *            The colour of the rectangle
	 * @return The updated {@link ElementBox}
	 */
	public ElementBox setColour(Number colour) {
		this.colour = colour.intValue();
		return this;
	}

	/**
	 * Draw the background
	 */
	@Override
	public void drawBackground(int mouseX, int mouseY, float gameTicks) {
		drawModalRect(this.posX, this.posY, this.posX + this.width, this.posY + this.height, this.colour);
	}

	/**
	 * Draw the foreground
	 */
	@Override
	public void drawForeground(int mouseX, int mouseY) {
	}

}
