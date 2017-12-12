package cjminecraft.core.client.gui.element;

import java.util.List;

import cjminecraft.core.CJCore;
import cjminecraft.core.client.gui.GuiCore;
import cjminecraft.core.util.RenderUtils;
import net.minecraft.util.ResourceLocation;

/**
 * A basic element which draws a texture
 * 
 * Adaptation from CoFH Lib with permission from KingLemming
 * 
 * @author CJMinecraft
 *
 */
public class ElementTexture extends ElementBase {

	public static final ResourceLocation WIDGETS_TEXTURE = new ResourceLocation(CJCore.MODID,
			"textures/gui/widgets.png");
	
	protected int textureU, textureV = 0;

	/**
	 * A basic element which draws a texture
	 * 
	 * @param gui
	 *            The parent {@link GuiCore}
	 * @param posX
	 *            The x position relative to the parent {@link GuiCore}
	 * @param posY
	 *            The y position relative to the parent {@link GuiCore}
	 */
	public ElementTexture(GuiCore gui, int posX, int posY) {
		super(gui, posX, posY);
	}

	/**
	 * A basic element which draws a texture
	 * 
	 * @param gui
	 *            The parent {@link GuiCore}
	 * @param posX
	 *            The x position relative to the parent {@link GuiCore}
	 * @param posY
	 *            The y position relative to the parent {@link GuiCore}
	 * @param width
	 *            The width of the rectangle
	 * @param height
	 *            The height of the rectangle
	 */
	public ElementTexture(GuiCore gui, int posX, int posY, int width, int height) {
		super(gui, posX, posY, width, height);
	}

	/**
	 * Set the texture offsets
	 * 
	 * @param u
	 *            The x offset
	 * @param v
	 *            The y offset
	 * @return The updated {@link ElementTexture}
	 */
	public ElementTexture setTextureUV(int u, int v) {
		this.textureU = u;
		this.textureV = v;
		return this;
	}

	/**
	 * Draw the background
	 */
	@Override
	public void drawBackground(int mouseX, int mouseY, float gameTicks) {
		this.gui.bindTexture(this.texture);
		drawSizedTexturedModalRect(this.posX, this.posY, this.textureU, this.textureV, this.width, this.height, this.textureWidth, this.textureHeight);
	}

	/**
	 * Draw the foreground
	 */
	@Override
	public void drawForeground(int mouseX, int mouseY) {
	}

}
