package cjminecraft.core.client.gui.element;

import cjminecraft.core.client.gui.GuiCore;
import cjminecraft.core.client.gui.ProgressBar;
import cjminecraft.core.util.RenderUtils;
import net.minecraft.tileentity.TileEntity;

/**
 * A simple yet effective progress bar which displays an operations progress in
 * a sleek layout
 * 
 * @author CJMinecraft
 *
 */
public class ElementProgressBar extends ElementTexture {

	private ProgressBarDirection direction;

	private int min, max = 0;

	/**
	 * A simple yet effective progress bar which displays an operations progress
	 * in a sleek layout
	 * 
	 * @param gui
	 *            The parent gui
	 * @param posX
	 *            The x position relative to the gui's x position
	 * @param posY
	 *            The y position relative to the gui's y position
	 */
	public ElementProgressBar(GuiCore gui, int posX, int posY) {
		super(gui, posX, posY);
	}

	/**
	 * A simple yet effective progress bar which displays an operations progress
	 * in a sleek layout
	 * 
	 * @param gui
	 *            The parent gui
	 * @param posX
	 *            The x position relative to the gui's x position
	 * @param posY
	 *            The y position relative to the gui's y position
	 * @param width
	 *            The width of the energy bar
	 * @param height
	 *            The height of the energy bar
	 */
	public ElementProgressBar(GuiCore gui, int posX, int posY, int width, int height) {
		super(gui, posX, posY, width, height);
	}

	/**
	 * Set the direction of the progress bar
	 * 
	 * @param direction
	 *            The {@link ProgressBarDirection}
	 * @return The updated element
	 */
	public ElementProgressBar setDirection(ProgressBarDirection direction) {
		this.direction = direction;
		return this;
	}

	/**
	 * Set the minimum value (cooldown)
	 * 
	 * @param min
	 *            The minimum value
	 * @return The updated element
	 */
	public ElementProgressBar setMin(int min) {
		this.min = min;
		return this;
	}

	/**
	 * Set the maximum value (maxCooldown)
	 * 
	 * @param max
	 *            The maximum value
	 * @return The updated element
	 */
	public ElementProgressBar setMax(int max) {
		this.max = max;
		return this;
	}

	/**
	 * Get the width based on the min and max values
	 * 
	 * @return The width
	 */
	private int getAdjustedWidth() {
		return (int) (this.min != 0 && this.max != 0 ? (float)this.min / (float)this.max * (float)this.width : 0);
	}

	/**
	 * Get the height based on the min and max values
	 * 
	 * @return The height
	 */
	private int getAdjustedHeight() {
		return (int) (this.min != 0 && this.max != 0 ? (float)this.min / (float)this.max * (float)this.height : 0);
	}

	@Override
	public void drawBackground(int mouseX, int mouseY, float gameTicks) {
		RenderUtils.resetColour();
		this.gui.bindTexture(this.texture);
		switch (direction) {
		case DIAGONAL_UP_LEFT:
			this.drawTexturedModalRect(this.posX, this.posY, this.textureU, this.textureV, this.width, this.height);
			this.drawTexturedModalRect(this.posX, this.posY, this.posX, this.posY, this.width - getAdjustedWidth(),
					this.height - getAdjustedHeight());
			break;
		case DIAGONAL_UP_RIGHT:
			this.drawTexturedModalRect(this.posX, this.posY, this.textureU, this.textureV, this.width, this.height);
			this.drawTexturedModalRect(this.posX + getAdjustedWidth(), this.posY, this.posX + getAdjustedWidth(),
					this.posY, this.width - getAdjustedWidth(), this.height - getAdjustedHeight());
			break;
		case DIAGONAL_DOWN_LEFT:
			this.drawTexturedModalRect(this.posX, this.posY, this.textureU, this.textureV, this.width, this.height);
			this.drawTexturedModalRect(this.posX, this.posY + getAdjustedHeight(), this.posX,
					this.posY + getAdjustedHeight(), this.width - getAdjustedWidth(),
					this.height - getAdjustedHeight());
			break;
		case DIAGONAL_DOWN_RIGHT:
			this.drawTexturedModalRect(this.posX, this.posY, this.textureU, this.textureV, getAdjustedWidth(),
					getAdjustedHeight());
			break;
		case DOWN_TO_UP:
			this.drawTexturedModalRect(this.posX, this.posY, this.textureU, this.textureV, this.width, this.height);
			this.drawTexturedModalRect(this.posX, this.posY, this.posX, this.posY, this.width,
					this.height - getAdjustedHeight());
			break;
		case LEFT_TO_RIGHT:
			this.drawTexturedModalRect(this.posX, this.posY, this.textureU, this.textureV, getAdjustedWidth(),
					this.height);
			break;
		case RIGHT_TO_LEFT:
			this.drawTexturedModalRect(this.posX, this.posY, this.textureU, this.textureV, this.width, this.height);
			this.drawTexturedModalRect(this.posX, this.posY, this.posX, this.posY, this.width - getAdjustedWidth(),
					this.height);
			break;
		case UP_TO_DOWN:
			this.drawTexturedModalRect(this.posX, this.posY, this.textureU, this.textureV, this.width,
					getAdjustedHeight());
			break;
		default:
			this.drawTexturedModalRect(this.posX, this.posY, this.textureU, this.textureV, this.width, this.height);
			break;
		}
	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {
	}

	/**
	 * Which way the {@link ElementProgressBar} will move
	 * 
	 * @author CJMinecraft
	 *
	 */
	public static enum ProgressBarDirection {
		LEFT_TO_RIGHT, RIGHT_TO_LEFT, UP_TO_DOWN, DOWN_TO_UP, DIAGONAL_UP_RIGHT, DIAGONAL_UP_LEFT, DIAGONAL_DOWN_RIGHT, DIAGONAL_DOWN_LEFT;
	}

}
