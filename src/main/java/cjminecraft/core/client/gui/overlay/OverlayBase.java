package cjminecraft.core.client.gui.overlay;

import java.util.ArrayList;
import java.util.List;

import cjminecraft.core.CJCore;
import cjminecraft.core.client.gui.GuiCore;
import cjminecraft.core.client.gui.GuiOverlay;
import cjminecraft.core.client.gui.ISpecialOverlayElement;
import cjminecraft.core.client.gui.element.ElementBase;
import cjminecraft.core.util.RenderUtils;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

/**
 * The base class for all overlays. Handles itself and links with a parent
 * {@link GuiOverlay}. Can hold elements
 * 
 * @author CJMinecraft
 *
 */
public class OverlayBase {

	public static final ResourceLocation WIDGETS_TEXTURE = new ResourceLocation(CJCore.MODID,
			"textures/gui/widgets.png");

	public static final int LEFT_CLICK = GuiCore.LEFT_CLICK;
	public static final int RIGHT_CLICK = GuiCore.RIGHT_CLICK;
	public static final int MIDDLE_CLICK = GuiCore.MIDDLE_CLICK;

	protected GuiOverlay gui;
	protected FontRenderer fontRenderer;

	protected int width;
	protected int height;
	protected int posX;
	protected int posY;

	protected List<ElementBase> elements = new ArrayList<ElementBase>();

	private boolean visible = true;
	private boolean enabled = true;
	private boolean showOverlayText = true;

	/**
	 * Initialise a new {@link OverlayBase}
	 * 
	 * @param gui
	 *            The parent gui
	 * @param posX
	 *            The x position of this overlay
	 * @param posY
	 *            The y position of this overlay
	 */
	public OverlayBase(GuiOverlay gui, int posX, int posY) {
		this.gui = gui;
		this.posX = posX + 4;
		this.posY = posY + 4;
		this.width = 8;
		this.height = 8;
		this.fontRenderer = gui.getFontRenderer();
	}

	/**
	 * Add the given {@link ElementBase} to the list of elements to be handled
	 * 
	 * @param element
	 *            The {@link ElementBase} to add
	 * @return The {@link ElementBase} which was added
	 */
	public ElementBase addElement(ElementBase element) {
		// Increase position to allow border to fit in
		element.setPosition(element.getPosX(), element.getPosY());
		this.elements.add(element);
		calculateSize();
		return element;
	}

	/**
	 * Update all of the element information
	 */
	public void updateElementInformation() {
		calculateSize();
	}

	/**
	 * Update the overlay
	 */
	public void update() {
		for (int i = this.elements.size(); i-- > 0;) {
			ElementBase element = this.elements.get(i);
			element.update();
		}
		calculateSize();
	}

	/**
	 * Draw the background of the overlay
	 */
	public void drawBackground() {
		// Top
		this.gui.drawHorizontalLine(this.posX, this.posX + this.width - 9, this.posY - 4, 0xFF000000);
		this.gui.drawHorizontalLine(this.posX, this.posX + this.width - 9, this.posY - 3, 0xFFFFFFFF);
		this.gui.drawHorizontalLine(this.posX, this.posX + this.width - 9, this.posY - 2, 0xFFFFFFFF);
		this.gui.drawHorizontalLine(this.posX, this.posX + this.width - 9, this.posY - 1, 0xFFC6C6C6);
		// Right
		this.gui.drawVerticalLine(this.posX + this.width - 8, this.posY - 1, this.posY + this.height - 8, 0xFFC6C6C6);
		this.gui.drawVerticalLine(this.posX + this.width - 7, this.posY - 1, this.posY + this.height - 8, 0xFF555555);
		this.gui.drawVerticalLine(this.posX + this.width - 6, this.posY - 1, this.posY + this.height - 8, 0xFF555555);
		this.gui.drawVerticalLine(this.posX + this.width - 5, this.posY - 1, this.posY + this.height - 8, 0xFF000000);
		// Bottom
		this.gui.drawHorizontalLine(this.posX, this.posX + this.width - 9, this.posY + this.height - 8, 0xFFC6C6C6);
		this.gui.drawHorizontalLine(this.posX, this.posX + this.width - 9, this.posY + this.height - 7, 0xFF555555);
		this.gui.drawHorizontalLine(this.posX, this.posX + this.width - 9, this.posY + this.height - 6, 0xFF555555);
		this.gui.drawHorizontalLine(this.posX, this.posX + this.width - 9, this.posY + this.height - 5, 0xFF000000);
		// Left
		this.gui.drawVerticalLine(this.posX - 1, this.posY - 1, this.posY + this.height - 8, 0xFFC6C6C6);
		this.gui.drawVerticalLine(this.posX - 2, this.posY - 1, this.posY + this.height - 8, 0xFFFFFFFF);
		this.gui.drawVerticalLine(this.posX - 3, this.posY - 1, this.posY + this.height - 8, 0xFFFFFFFF);
		this.gui.drawVerticalLine(this.posX - 4, this.posY - 1, this.posY + this.height - 8, 0xFF000000);

		// Allows alpha not to be drawn as black but transparent

		RenderUtils.resetColour();
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO);
		// Top left
		this.gui.bindTexture(WIDGETS_TEXTURE);
		drawTexturedModalRect(this.posX - 4, this.posY - 4, 0, 85, 4, 4);
		// Top right
		// this.gui.bindTexture(WIDGETS_TEXTURE);
		drawTexturedModalRect(this.posX + this.width - 8, this.posY - 4, 4, 85, 4, 4);
		// Bottom Left
		this.gui.bindTexture(WIDGETS_TEXTURE);
		drawTexturedModalRect(this.posX - 4, this.posY + this.height - 8, 0, 89, 4, 4);
		// Bottom Right
		this.gui.bindTexture(WIDGETS_TEXTURE);
		drawTexturedModalRect(this.posX + this.width - 8, this.posY + this.height - 8, 4, 89, 4, 4);
		GlStateManager.disableBlend();

		GlStateManager.pushMatrix();
		GlStateManager.translate(this.posX, this.posY, 0);
		drawModalRect(0, 0, this.width - 8, this.height - 8, 0xFFC6C6C6);

		for (int i = this.elements.size(); i-- > 0;) {
			ElementBase element = this.elements.get(i);
			if (element.isVisible() && element.isEnabled())
				element.drawBackground(0, 0, 0);
		}

		GlStateManager.popMatrix();
	}

	/**
	 * Draw the foreground of the overlay
	 */
	public void drawForeground() {
		List<String> overlayText = new ArrayList<String>();
		for (int i = this.elements.size(); i-- > 0;) {
			ElementBase element = this.elements.get(i);
			if (element.isVisible() && element.isEnabled()) {
				element.drawForeground(0, 0);
				if (element instanceof ISpecialOverlayElement) {
					((ISpecialOverlayElement) element).drawSpecialLayer();
					((ISpecialOverlayElement) element).addOverlayText(overlayText);
				}
			}
		}
		if (this.showOverlayText)
			for (String text : overlayText)
				this.fontRenderer.drawStringWithShadow(text, this.posX + this.width,
						this.posY + this.height - 7 - ((overlayText.indexOf(text) + 1) * 10), 0xFFFFFF);
	}

	/**
	 * Handle when the mouse wheel has moved
	 * 
	 * @param movement
	 *            How much the mouse wheel has moved
	 * @return Whether it should override all other handlers
	 */
	public boolean onMouseWheel(int movement) {
		for (int i = this.elements.size(); i-- > 0;) {
			ElementBase element = this.elements.get(i);
			if (element.onMouseWheel(0, 0, movement))
				return true;
		}
		return false;
	}

	/**
	 * Draw a rectangle with the given colour
	 * 
	 * @param x
	 *            The x position of the rectangle
	 * @param y
	 *            The y position of the rectangle
	 * @param width
	 *            The width of the rectangle
	 * @param height
	 *            The height of the rectangle
	 * @param colour
	 *            The colour of the rectangle in format 0xRRGGBB
	 */
	public void drawModalRect(int x, int y, int width, int height, int colour) {
		this.gui.drawSizedModalRect(x, y, width, height, colour);
	}

	/**
	 * Draw a textured rectangle
	 * 
	 * @param x
	 *            The x position of the rectangle
	 * @param y
	 *            The y position of the rectangle
	 * @param u
	 *            The x position of the texture
	 * @param v
	 *            The y position of the texture
	 * @param width
	 *            The width of the rectangle
	 * @param height
	 *            The height of the rectangle
	 */
	public void drawTexturedModalRect(int x, int y, int u, int v, int width, int height) {
		this.gui.drawTexturedModalRect(x, y, u, v, width, height);
	}

	/**
	 * Draw a textured rectangle at the positions given with the UV provided.
	 * 
	 * @param x
	 *            The x position of the rectangle on the screen
	 * @param y
	 *            The y position of the rectangle on the screen
	 * @param u
	 *            The x position in the texture to start rendering from
	 * @param v
	 *            They y position in the texture to start rendering from
	 * @param width
	 *            The width of the rectangle
	 * @param height
	 *            The height of the rectangle
	 * @param textureWidth
	 *            The width of the rectangle on the texture
	 * @param textureHeight
	 *            The height of the rectangle on the texture
	 */
	public void drawSizedTexturedModalRect(int x, int y, int u, int v, int width, int height, float textureWidth,
			float textureHeight) {
		this.gui.drawSizedTexturedModalRect(x, y, u, v, width, height, textureWidth, textureHeight);
	}

	/**
	 * Draw a textured rectangle at the positions given with the UV provided.
	 * The texture will be stretched to meet the texture width and height. There
	 * will be a border around it
	 * 
	 * @param x
	 *            The x position of the rectangle on the screen
	 * @param y
	 *            The y position of the rectangle on the screen
	 * @param u
	 *            The x position in the texture to start rendering from
	 * @param v
	 *            They y position in the texture to start rendering from
	 * @param width
	 *            The width of the rectangle
	 * @param height
	 *            The height of the rectangle
	 * @param textureWidth
	 *            The width of the rectangle on the texture
	 * @param textureHeight
	 *            The height of the rectangle on the texture
	 */
	public void drawSizedTexturedModalRectWithBorder(int x, int y, int u, int v, int width, int height,
			float textureWidth, float textureHeight) {
		this.gui.drawSizedTexturedModalRectWithBorder(x, y, u, v, width, height, textureWidth, textureHeight);
	}

	/**
	 * Draw a textured rectangle with a border
	 * 
	 * @param x
	 *            The x position of the rectangle
	 * @param y
	 *            The y position of the rectangle
	 * @param u
	 *            The x position of the texture
	 * @param v
	 *            The y position of the texture
	 * @param width
	 *            The width of the rectangle
	 * @param height
	 *            The height of the rectangle
	 */
	public void drawTexturedModalRectWithBorder(int x, int y, int u, int v, int width, int height) {
		this.gui.drawTexturedModalRectWithBorder(x, y, u, v, width, height);
	}

	/**
	 * Draw a string centered to the x position given
	 * 
	 * @param fontRenderer
	 *            The font renderer to use to draw
	 * @param text
	 *            The text to draw
	 * @param x
	 *            The x position of where the text should be rendered
	 * @param y
	 *            The y position of where the text should be rendered
	 * @param colour
	 *            The colour of the text
	 */
	public void drawCenteredString(FontRenderer fontRenderer, String text, int x, int y, int colour) {
		fontRenderer.drawStringWithShadow(text, x - fontRenderer.getStringWidth(text) / 2, y, colour);
	}

	/**
	 * Set the position of the overlay
	 * 
	 * @param posX
	 *            The x position relative to the gui's x position
	 * @param posY
	 *            The y position relative to the gui's y position
	 * @return The updated overlay
	 */
	public OverlayBase setPosition(int posX, int posY) {
		this.posX = posX + 4;
		this.posY = posY + 4;
		return this;
	}

	/**
	 * Set whether the overlay is visible
	 * 
	 * @param visible
	 *            Whether the overlay is visible
	 * @return The updated overlay
	 */
	public OverlayBase setVisible(boolean visible) {
		this.visible = visible;
		return this;
	}

	/**
	 * Set whether the overlay is enabled
	 * 
	 * @param enabled
	 *            Whether the overlay is enabled
	 * @return The updated overlay
	 */
	public OverlayBase setEnabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}

	/**
	 * @return is the overlay visible?
	 */
	public boolean isVisible() {
		return this.visible;
	}

	/**
	 * @return is the overlay enabled?
	 */
	public boolean isEnabled() {
		return this.enabled;
	}

	/**
	 * Set whether the overlay text should be drawn
	 * 
	 * @param showOverlayText
	 *            Whether the overlay text should be drawn
	 * @return The updated overlay
	 */
	public OverlayBase setShowOverlayText(boolean showOverlayText) {
		this.showOverlayText = showOverlayText;
		return this;
	}

	/**
	 * @return show the overlay text?
	 */
	public boolean showOverlayText() {
		return this.showOverlayText;
	}

	/**
	 * @return The font renderer the overlay uses
	 */
	public FontRenderer getFontRenderer() {
		return this.fontRenderer == null ? this.gui.getFontRenderer() : this.fontRenderer;
	}

	/**
	 * Set the font renderer
	 * 
	 * @param fontRenderer
	 *            The font renderer to set
	 * @return The updated overlay
	 */
	public OverlayBase setFontRenderer(FontRenderer fontRenderer) {
		this.fontRenderer = fontRenderer;
		return this;
	}

	/**
	 * @return The parent gui
	 */
	public GuiOverlay getContainerGui() {
		return gui;
	}

	/**
	 * @return The x position of this overlay
	 */
	public int getPosX() {
		return posX;
	}

	/**
	 * @return The y position of this overlay
	 */
	public int getPosY() {
		return posY;
	}

	/**
	 * Calculate the width and the height of the overlay
	 * 
	 * @return The updated overlay
	 */
	public OverlayBase calculateSize() {
		this.width = 0;
		this.height = 0;
		for (ElementBase element : this.elements) {
			this.width = Math.max(this.width, element.getPosX() + element.getWidth() + 8);
			this.height = Math.max(this.height, element.getPosY() + element.getHeight() + 8);
		}
		return this;
	}

	/**
	 * @return the width of the overlay
	 */
	public int getWidth() {
		calculateSize();
		return this.width;
	}

	/**
	 * @return the height of the overlay
	 */
	public int getHeight() {
		calculateSize();
		return this.height;
	}

	/**
	 * Clear the list of elements
	 * 
	 * @return the updated element
	 */
	public OverlayBase clearElements() {
		this.elements.clear();
		return this;
	}

	/**
	 * @return all the elements in the overlay
	 */
	public List<ElementBase> getElements() {
		return elements;
	}

}
