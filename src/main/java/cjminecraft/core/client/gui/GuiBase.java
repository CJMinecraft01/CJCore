package cjminecraft.core.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nullable;

import org.lwjgl.input.Mouse;

import cjminecraft.core.client.gui.element.ElementBase;
import cjminecraft.core.util.RenderUtils;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

/**
 * Base class for guis which have elements and tabs
 * 
 * Adaptation from CoFH Lib with permission from KingLemming
 * 
 * @author CJMinecraft
 *
 */
public abstract class GuiBase extends GuiCore {

	protected boolean drawTitle = true;
	protected boolean centerTitle = true;
	protected boolean drawInventory = true;
	protected int mouseX, mouseY;

	protected String name;
	protected ResourceLocation texture;

	protected List<ElementBase> elements = new ArrayList<ElementBase>();

	protected List<String> tooltip = new LinkedList<String>();
	protected boolean tooltips = true;

	/**
	 * Initialise the {@link GuiContainer}
	 * 
	 * @param container
	 *            The container of the slots
	 */
	public GuiBase(Container container) {
		super(container);
	}

	/**
	 * Initialise the {@link GuiContainer} setting the background of the gui's
	 * texture
	 * 
	 * @param container
	 *            The container of the slots
	 * @param texture
	 *            The gui texture
	 */
	public GuiBase(Container container, ResourceLocation texture) {
		super(container);
		this.texture = texture;
	}

	/**
	 * Initialise all the gui elements
	 */
	@Override
	public void initGui() {
		super.initGui();
		this.elements.clear();
	}

	/**
	 * Draws all the tooltips and sets the mouse position correctly
	 */
	@Override
	public void drawScreen(int x, int y, float partialTicks) {
		updateElementInformation();

		super.drawScreen(x, y, partialTicks);

		if (this.tooltips && this.mc.player.inventory.getItemStack().isEmpty()) {
			addTooltips(this.tooltip);
			drawTooltip(this.tooltip);
		}

		this.mouseX = x - this.guiLeft;
		this.mouseY = y - this.guiTop;

		updateElements();
	}

	/**
	 * Draw all the elements etc.
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		if (this.drawTitle & this.name != null) {
			if(this.centerTitle)
				this.fontRenderer.drawString(I18n.format(this.name), getCenteredOffset(I18n.format(this.name)), 6,
					0x404040);
			else
				this.fontRenderer.drawString(I18n.format(this.name), 6, 6,
						0x404040);
		}
		if (this.drawInventory)
			this.fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 93, 0x404040);
		drawElements(0, true);
	}

	/**
	 * Draw the background texture
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int x, int y) {
		RenderUtils.resetColour();
		bindTexture(this.texture);
		drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);

		this.mouseX = x - this.guiLeft;
		this.mouseY = y - this.guiTop;

		GlStateManager.pushMatrix();
		GlStateManager.translate(this.guiLeft, this.guiTop, 0.0F);
		drawElements(partialTicks, false);
		GlStateManager.popMatrix();
	}

	/**
	 * Handle the typing of characters
	 */
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		for (int i = this.elements.size(); i-- > 0;) {
			ElementBase element = this.elements.get(i); // Get the elements in reverse
			if (!element.isVisible() || !element.isEnabled())
				continue;
			if (element.onKeyTyped(typedChar, keyCode))
				return;
		}
		super.keyTyped(typedChar, keyCode);
	}

	/**
	 * Handle when anything changes regarding the mouse
	 */
	@Override
	public void handleMouseInput() throws IOException {
		int x = Mouse.getEventX() * this.width / this.mc.displayWidth;
		int y = this.height - Mouse.getEventY() * height / this.mc.displayHeight - 1;

		this.mouseX = x - this.guiLeft;
		this.mouseY = y - this.guiTop;

		int wheelMovement = Mouse.getEventDWheel();

		if (wheelMovement != 0) {
			for (int i = this.elements.size(); i-- > 0;) {
				ElementBase element = this.elements.get(i);
				if (!element.isVisible() || !element.isEnabled())
					continue;
				if (element.onMouseWheel(this.mouseX, this.mouseY, wheelMovement))
					return;
			}
			if (onMouseWheel(this.mouseX, this.mouseY, wheelMovement))
				return;
		}

		super.handleMouseInput();
	}

	/**
	 * Handle mouse wheel movement
	 * 
	 * @param mouseX
	 *            The x position of the mouse
	 * @param mouseY
	 *            The y position of the mouse
	 * @param wheelMovement
	 *            How much the wheel has moved
	 * @return Whether to overrule all other handlers
	 */
	protected boolean onMouseWheel(int mouseX, int mouseY, int wheelMovement) {
		return false;
	}

	/**
	 * Handle the mouse being clicked
	 */
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		mouseX -= this.guiLeft;
		mouseY -= this.guiTop;

		for (int i = this.elements.size(); i-- > 0;) {
			ElementBase element = this.elements.get(i);
			if (!element.isVisible() || !element.isEnabled())
				continue;
			if (element.onMousePressed(mouseX, mouseY, mouseButton))
				return;
		}
		mouseX += this.guiLeft;
		mouseY += this.guiTop;

		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	/**
	 * Handle the mouse being released
	 */
	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		mouseX -= this.guiLeft;
		mouseY -= this.guiTop;

		for (int i = this.elements.size(); i-- > 0;) {
			ElementBase element = this.elements.get(i);
			if (!element.isVisible() || !element.isEnabled())
				continue;
			element.onMouseReleased(this.mouseX, this.mouseY);
		}
		mouseX += this.guiLeft;
		mouseY += this.guiTop;

		super.mouseReleased(mouseX, mouseY, state);
	}

	/**
	 * Handle the mouse dragging
	 */
	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
	}

	/**
	 * Add a tooltip here
	 * 
	 * @param tooltip
	 *            The list of tooltips to add to
	 */
	public void addTooltips(List<String> tooltip) {
		ElementBase element = getElementAtPosition(this.mouseX, this.mouseY);
		if (element != null && element.isVisible())
			element.addTooltip(tooltip);
	}

	/**
	 * Add the given {@link ElementBase} to the list of elements to be handled
	 * 
	 * @param element
	 *            The {@link ElementBase} to add
	 * @return The {@link ElementBase} which was added
	 */
	public ElementBase addElement(ElementBase element) {
		this.elements.add(element);
		return element;
	}

	/**
	 * Gets the element the mouse is currently covering
	 * 
	 * @param mouseX
	 *            The x position of the mouse
	 * @param mouseY
	 *            The y position of the mouse
	 * @return The element at the mouse's position
	 */
	@Nullable
	protected ElementBase getElementAtPosition(int mouseX, int mouseY) {
		for (int i = this.elements.size(); i-- > 0;) {
			ElementBase element = this.elements.get(i);
			if (element.intersectsWith(mouseX, mouseY))
				return element;
		}
		return null;
	}

	/**
	 * Update all of the elements
	 */
	protected void updateElements() {
		for (int i = this.elements.size(); i-- > 0;) {
			ElementBase element = this.elements.get(i);
			if (element.isVisible() && element.isEnabled())
				element.update(this.mouseX, this.mouseY);
		}
	}

	/**
	 * Update all of the element information
	 */
	protected void updateElementInformation() {

	}

	/**
	 * Draw all of the elements
	 * 
	 * @param partialTicks
	 *            The ticks it has took
	 * @param foreground
	 *            Whether it is the foreground or not
	 */
	protected void drawElements(float partialTicks, boolean foreground) {
		if (foreground) {
			for (int i = this.elements.size(); i-- > 0;) {
				ElementBase element = this.elements.get(i);
				if (element.isVisible())
					element.drawForeground(this.mouseX, this.mouseY);
			}
		} else {
			for (int i = this.elements.size(); i-- > 0;) {
				ElementBase element = this.elements.get(i);
				if (element.isVisible())
					element.drawBackground(this.mouseX, this.mouseY, partialTicks);
			}
		}
	}

	/**
	 * Draws the given tooltip at the mouse position
	 * 
	 * @param tooltip
	 *            The tooltip to draw at the mouse position
	 */
	public void drawTooltip(List<String> tooltip) {
		drawTooltipHoveringText(tooltip, this.mouseX + this.guiLeft, this.mouseY + this.guiTop, this.fontRenderer);
		this.tooltip.clear();
	}

	/**
	 * @return The x position of the mouse
	 */
	public int getMouseX() {
		return this.mouseX;
	}

	/**
	 * @return The y position of the mouse
	 */
	public int getMouseY() {
		return this.mouseY;
	}
	
	/**
	 * @return All of the elements in the gui
	 */
	public List<ElementBase> getElements() {
		return elements;
	}

}
