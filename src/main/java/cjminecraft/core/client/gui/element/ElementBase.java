package cjminecraft.core.client.gui.element;

import java.io.IOException;
import java.util.List;

import cjminecraft.core.client.gui.GuiCore;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;

/**
 * The base class for all elements. Handles itself and links with a
 * {@link GuiCore}
 * 
 * Adaptation from CoFH Lib with permission from KingLemming
 * 
 * @author CJMinecraft
 */
public abstract class ElementBase {

	public static final int LEFT_CLICK = GuiCore.LEFT_CLICK;
	public static final int RIGHT_CLICK = GuiCore.RIGHT_CLICK;
	public static final int MIDDLE_CLICK = GuiCore.MIDDLE_CLICK;

	protected GuiCore gui;
	protected ResourceLocation texture;
	protected FontRenderer fontRenderer;

	protected int posX;
	protected int posY;

	protected int width;
	protected int height;

	protected int textureWidth = 256;
	protected int textureHeight = 256;

	protected String name;

	private boolean visible = true;
	private boolean enabled = true;

	/**
	 * Initialise a new {@link ElementBase}
	 * 
	 * @param gui
	 *            The parent gui
	 * @param posX
	 *            The x position relative to the gui's x position
	 * @param posY
	 *            The y position relative to the gui's y position
	 */
	public ElementBase(GuiCore gui, int posX, int posY) {
		this.gui = gui;
		this.posX = posX;
		this.posY = posY;
		this.fontRenderer = gui.getFontRenderer();
	}

	/**
	 * Initialise a new {@link ElementBase}
	 * 
	 * @param gui
	 *            The parent gui
	 * @param posX
	 *            The x position relative to the gui's x position
	 * @param posY
	 *            The y position relative to the gui's y position
	 * @param width
	 *            The width of the element
	 * @param height
	 *            The height of the element
	 */
	public ElementBase(GuiCore gui, int posX, int posY, int width, int height) {
		this.gui = gui;
		this.posX = posX;
		this.posY = posY;
		this.width = width;
		this.height = height;
		this.fontRenderer = gui.getFontRenderer();
	}

	/**
	 * Set the name of the element
	 * 
	 * @param name
	 *            The name to set
	 * @return The updated element
	 */
	public ElementBase setName(String name) {
		this.name = name;
		return this;
	}

	/**
	 * Set the position of the element
	 * 
	 * @param posX
	 *            The x position relative to the gui's x position
	 * @param posY
	 *            The y position relative to the gui's y position
	 * @return The updated element
	 */
	public ElementBase setPosition(int posX, int posY) {
		this.posX = posX;
		this.posY = posY;
		return this;
	}

	/**
	 * Set the size of the element
	 * 
	 * @param width
	 *            The width of the element
	 * @param height
	 *            The height of the element
	 * @return The updated element
	 */
	public ElementBase setSize(int width, int height) {
		this.width = width;
		this.height = height;
		return this;
	}

	/**
	 * Set the element's texture
	 * 
	 * @param texture
	 *            The texture to set
	 * @param textureWidth
	 *            The width of the texture
	 * @param textureHeight
	 *            The height of the texture
	 * @return The updated element
	 */
	public ElementBase setTexture(ResourceLocation texture, int textureWidth, int textureHeight) {
		this.texture = texture;
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
		return this;
	}

	/**
	 * Set whether the element is visible
	 * 
	 * @param visible
	 *            Whether the element is visible
	 * @return The updated element
	 */
	public ElementBase setVisible(boolean visible) {
		this.visible = visible;
		return this;
	}

	/**
	 * Set whether the element is enabled
	 * 
	 * @param enabled
	 *            Whether the element is enabled
	 * @return The updated element
	 */
	public ElementBase setEnabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}

	/**
	 * @return is the element visible?
	 */
	public boolean isVisible() {
		return this.visible;
	}

	/**
	 * @return is the element enabled?
	 */
	public boolean isEnabled() {
		return this.enabled;
	}

	/**
	 * Update the element
	 * 
	 * @param mouseX
	 *            The x position of the mouse
	 * @param mouseY
	 *            The y position of the mouse
	 */
	public void update(int mouseX, int mouseY) {
		update();
	}

	/**
	 * Update the element
	 */
	public void update() {
	}

	/**
	 * Draw the background of the element
	 * 
	 * @param mouseX
	 *            The x position of the mouse
	 * @param mouseY
	 *            The y position of the mouse
	 * @param gameTicks
	 *            The number of game ticks it has took so far
	 */
	public abstract void drawBackground(int mouseX, int mouseY, float gameTicks);

	/**
	 * Draw the foreground of the element
	 * 
	 * @param mouseX
	 *            The x position of the mouse
	 * @param mouseY
	 *            The y position of the mouse
	 */
	public abstract void drawForeground(int mouseX, int mouseY);

	/**
	 * Add a tooltip here
	 * 
	 * @param tooltip
	 *            The list of previously added tooltips
	 */
	public void addTooltip(List<String> tooltip) {

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
	 * Handle the mouse being pressed
	 * 
	 * @param mouseX
	 *            The x position of the mouse
	 * @param mouseY
	 *            The y position of the mouse
	 * @param mouseButton
	 *            The mouse button which was pressed
	 * @return Whether it should override all other handlers
	 * @throws IOException
	 *             Something may go wrong. Typically due to buttons
	 */
	public boolean onMousePressed(int mouseX, int mouseY, int mouseButton) throws IOException {
		return false;
	}

	/**
	 * Handle when the mouse stops being pressed
	 * 
	 * @param mouseX
	 *            The x position of the mouse
	 * @param mouseY
	 *            The y position of the mouse
	 */
	public void onMouseReleased(int mouseX, int mouseY) {

	}

	/**
	 * Handle when the mouse wheel has moved
	 * 
	 * @param mouseX
	 *            The x position of the mouse
	 * @param mouseY
	 *            The y position of the mouse
	 * @param movement
	 *            How much the mouse wheel has moved
	 * @return Whether it should override all other handlers
	 */
	public boolean onMouseWheel(int mouseX, int mouseY, int movement) {
		return false;
	}

	/**
	 * Handle when a key is typed
	 * 
	 * @param characterTyped
	 *            The character which was typed
	 * @param keyPressed
	 *            The key which was typed
	 * @return Whether it should override all other handlers
	 */
	public boolean onKeyTyped(char characterTyped, int keyPressed) {
		return false;
	}

	/**
	 * Does the mouse intersect with this element?
	 * 
	 * @param mouseX
	 *            The x position of the mouse
	 * @param mouseY
	 *            The y position of the mouse
	 * @return Whether the mouse intersects with this element
	 */
	public boolean intersectsWith(int mouseX, int mouseY) {
		return mouseX >= this.posX && mouseX <= this.posX + this.width && mouseY >= this.posY
				&& mouseY <= this.posY + this.height;
	}

	/**
	 * @return The font renderer the element uses
	 */
	public FontRenderer getFontRenderer() {
		return this.fontRenderer == null ? this.gui.getFontRenderer() : this.fontRenderer;
	}

	/**
	 * Set the font renderer
	 * 
	 * @param fontRenderer
	 *            The font renderer to set
	 * @return The updated element
	 */
	public ElementBase setFontRenderer(FontRenderer fontRenderer) {
		this.fontRenderer = fontRenderer;
		return this;
	}

	/**
	 * @return The name of the element
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return The parent gui
	 */
	public GuiCore getContainerGui() {
		return gui;
	}

	/**
	 * @return The x position of this element relative to the parent gui's x
	 *         position
	 */
	public int getPosX() {
		return posX;
	}

	/**
	 * @return The y position of this element relative to the parent gui's y
	 *         position
	 */
	public int getPosY() {
		return posY;
	}

	/**
	 * @return The width of the element
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @return The height of the element
	 */
	public int getHeight() {
		return height;
	}

}
