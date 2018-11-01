package cjminecraft.core.client.gui;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import cjminecraft.core.util.RenderUtils;
import cjminecraft.core.util.SoundBase;
import cjminecraft.core.util.SoundUtils;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

/**
 * The core of all gui's, contains useful methods for rendering
 * 
 * Adaptation from CoFH Lib with permission from KingLemming
 * 
 * @author CJMinecraft
 *
 */
public abstract class GuiCore extends GuiContainer {

	public static final int LEFT_CLICK = 0;
	public static final int RIGHT_CLICK = 1;
	public static final int MIDDLE_CLICK = 2;

	/**
	 * Initialise the {@link GuiContainer}
	 * 
	 * @param container
	 *            The container of the slots
	 */
	public GuiCore(Container container) {
		super(container);
	}

	/**
	 * Bind the provided texture for rendering
	 * 
	 * @param texture
	 *            The texture to bind
	 */
	public void bindTexture(ResourceLocation texture) {
		this.mc.renderEngine.bindTexture(texture);
	}

	/**
	 * Get the correct {@link Slot} the player is hovering over
	 * 
	 * @param xCoord
	 *            The x position of the mouse
	 * @param yCoord
	 *            The y position of the mouse
	 * @return The {@link Slot} the player is hovering over. Returns null if the
	 *         player is not hovering over a slot
	 */
	public Slot getSlotAtPosition(int xCoord, int yCoord) {
		for (int i = 0; i < this.inventorySlots.inventorySlots.size(); i++) {
			Slot slot = this.inventorySlots.inventorySlots.get(i);
			if (this.isMouseOverSlot(slot, xCoord, yCoord))
				return slot;
		}
		return null;
	}

	/**
	 * Says whether the player is hovering over the selected {@link Slot}
	 * 
	 * @param slot
	 *            The slot to test
	 * @param xCoord
	 *            The x position of the mouse
	 * @param yCoord
	 *            The y position of the mouse
	 * @return Whether the player is hovering over the selected {@link Slot}
	 */
	public boolean isMouseOverSlot(Slot slot, int xCoord, int yCoord) {
		return this.isPointInRegion(slot.xPos, slot.yPos, 16, 16, xCoord, yCoord);
	}

	/**
	 * Draws the {@link ItemStack} provided onto the screen
	 * 
	 * @param stack
	 *            The {@link ItemStack} to draw
	 * @param x
	 *            The x position on the screen
	 * @param y
	 *            The y position on the screen
	 * @param drawOverlay
	 *            Whether to draw the overlay text with the item
	 * @param overlayTxt
	 *            The overlay text to draw with the item if enabled
	 */
	public void drawItemStack(ItemStack stack, int x, int y, boolean drawOverlay, @Nullable String overlayTxt) {
		if (stack.isEmpty())
			return;
		RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.enableDepth();
		GlStateManager.enableLighting();
		GlStateManager.pushMatrix();
		GlStateManager.translate(0.0F, 0.0F, 32.0F);
		this.zLevel = 200.0F;
		this.itemRender.zLevel = 200.0F;

		FontRenderer font = null;
		if (stack != null)
			font = stack.getItem().getFontRenderer(stack);
		if (font == null)
			font = this.fontRenderer;

		this.itemRender.renderItemAndEffectIntoGUI(stack, x, y);
		RenderHelper.disableStandardItemLighting();
		
		if (drawOverlay)
			this.itemRender.renderItemOverlayIntoGUI(font, stack, x, y, overlayTxt);
		this.zLevel = 0.0F;
		this.itemRender.zLevel = 0.0F;
		GlStateManager.popMatrix();
		GlStateManager.disableLighting();
	}

	/**
	 * Draw the given {@link Fluid} as a tiled rectangle
	 * 
	 * @param x
	 *            The x position of the rectangle
	 * @param y
	 *            The y position of the rectangle
	 * @param fluid
	 *            The {@link Fluid} to draw
	 * @param width
	 *            The width of the rectangle
	 * @param height
	 *            The height of the rectangle
	 */
	public void drawFluid(int x, int y, FluidStack fluid, int width, int height) {
		if (fluid == null)
			return;
		RenderUtils.setBlockTextureSheet();
		int colour = fluid.getFluid().getColor(fluid);
		RenderUtils.setGLColourFromInt(colour);
		drawTiledTexture(x, y, RenderUtils.getFluidTexture(fluid), width, height);
	}
	
	/**
	 * Draw the given {@link Fluid} as a tiled rectangle with a border
	 * 
	 * @param x
	 *            The x position of the rectangle
	 * @param y
	 *            The y position of the rectangle
	 * @param fluid
	 *            The {@link Fluid} to draw
	 * @param width
	 *            The width of the rectangle
	 * @param height
	 *            The height of the rectangle
	 */
	public void drawFluidWithBorder(int x, int y, FluidStack fluid, int width, int height) {
		if (fluid == null)
			return;
		RenderUtils.setBlockTextureSheet();
		int colour = fluid.getFluid().getColor(fluid);
		RenderUtils.setGLColourFromInt(colour);
		drawTiledTextureWithBorder(x, y, RenderUtils.getFluidTexture(fluid), width, height);
	}

	/**
	 * Draw a texture tiled
	 * 
	 * @param x
	 *            The x position of the rectangle
	 * @param y
	 *            The y position of the rectangle
	 * @param icon
	 *            The icon to draw
	 * @param width
	 *            The width of the rectangle
	 * @param height
	 *            The height of the rectangle
	 */
	public void drawTiledTexture(int x, int y, TextureAtlasSprite icon, int width, int height) {
		for (int offsetX = 0; offsetX < width; offsetX += 16) {
			for (int offsetY = 0; offsetY < height; offsetY += 16) {
				drawScaledTexturedModelRectFromIcon(x + offsetX, y + offsetY, icon, Math.min(width - offsetX, 16),
						Math.min(height - offsetY, 16));
			}
		}
		RenderUtils.resetColour();
	}
	
	/**
	 * Draw a texture tiled with a border
	 * 
	 * @param x
	 *            The x position of the rectangle
	 * @param y
	 *            The y position of the rectangle
	 * @param icon
	 *            The icon to draw
	 * @param width
	 *            The width of the rectangle
	 * @param height
	 *            The height of the rectangle
	 */
	public void drawTiledTextureWithBorder(int x, int y, TextureAtlasSprite icon, int width, int height) {
		this.drawTiledTexture(x + 1, y + 1, icon, width - 1, height - 1);
		this.drawBorder(x, y, width, height);
	}

	/**
	 * Draw the icon
	 * 
	 * @param icon
	 *            The icon to draw
	 * @param x
	 *            The x position of the icon
	 * @param y
	 *            The y position of the icon
	 */
	public void drawIcon(TextureAtlasSprite icon, int x, int y) {
		RenderUtils.setBlockTextureSheet();
		GlStateManager.color(1, 1, 1, 1);
		drawColourIcon(icon, x, y);
	}

	/**
	 * Draw the icon without clearing the colour
	 * 
	 * @param icon
	 *            The icon to draw
	 * @param x
	 *            The x position of the icon
	 * @param y
	 *            The y position of the icon
	 */
	public void drawColourIcon(TextureAtlasSprite icon, int x, int y) {
		drawTexturedModalRect(x, y, icon, 16, 16);
	}

	/**
	 * Draw a rectangle of one colour from the given locations. Alpha is blended
	 * correctly
	 * 
	 * @param x1
	 *            The left position
	 * @param y1
	 *            The top position
	 * @param x2
	 *            The right position
	 * @param y2
	 *            The bottom position
	 * @param colour
	 *            The colour in format 0xAARRGGBB. A = Alpha, R = Red, G =
	 *            Green, B = Blue
	 */
	public void drawSizedModalRect(int x1, int y1, int x2, int y2, int colour) {
		Gui.drawRect(x1, y1, x2, y2, colour);
		RenderUtils.resetColour();
	}

	/**
	 * Draw a rectangle of one colour from the given locations. No blending used
	 * 
	 * @param x1
	 *            The left position
	 * @param y1
	 *            The top position
	 * @param x2
	 *            The right position
	 * @param y2
	 *            The bottom position
	 * @param colour
	 *            The colour in format 0xAARRGGBB. A = Alpha, R = Red, G =
	 *            Green, B = Blue
	 */
	public void drawSizedRect(int x1, int y1, int x2, int y2, int colour) {
		int temp;

		if (x1 < x2) {
			temp = x1;
			x1 = x2;
			x2 = temp;
		}
		if (y1 < y2) {
			temp = y1;
			y1 = y2;
			y2 = temp;
		}

		float alpha = (colour >> 24 & 255) / 255.0F;
		float red = (colour >> 16 & 255) / 255.0F;
		float green = (colour >> 8 & 255) / 255.0F;
		float blue = (colour & 255) / 255.0F;
		GlStateManager.disableTexture2D();
		GlStateManager.color(red, green, blue, alpha);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
		buffer.pos(x1, y2, this.zLevel).endVertex();
		buffer.pos(x2, y2, this.zLevel).endVertex();
		buffer.pos(x2, y1, this.zLevel).endVertex();
		buffer.pos(x1, y1, this.zLevel).endVertex();
		tessellator.draw();
		GlStateManager.enableTexture2D();
	}

	/**
	 * Draw a textured rectangle at the positions given with the UV provided.
	 * The texture will be stretched to meet the texture width and height
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
		Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, width, height, textureWidth, textureHeight);
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
		this.drawSizedTexturedModalRect(x + 1, y + 1, u, v, width - 2, height - 2, textureWidth, textureHeight);
		this.drawBorder(x, y, width, height);
	}

	/**
	 * Draw a textured rectangle at the positions given with the UV provided,
	 * with a border around it
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
	 */
	public void drawTexturedModalRectWithBorder(int x, int y, int u, int v, int width, int height) {
		this.drawTexturedModalRect(x + 1, y + 1, u, v, width - 1, height - 1);
		this.drawBorder(x, y, width, height);
	}

	/**
	 * Draw a rectangle of one colour from the given locations. Alpha is blended
	 * correctly. Will draw a border around it
	 * 
	 * @param x1
	 *            The left position
	 * @param y1
	 *            The top position
	 * @param x2
	 *            The right position
	 * @param y2
	 *            The bottom position
	 * @param colour
	 *            The colour in format 0xAARRGGBB. A = Alpha, R = Red, G =
	 *            Green, B = Blue
	 */
	public void drawSizedModalRectWithBorder(int x1, int y1, int x2, int y2, int colour) {
		this.drawBorder(x1, y1, y2 - y1, x2 - x1);
		this.drawSizedModalRect(x1 + 1, y1 + 1, x2 - 1, y2 - 1, colour);
	}

	/**
	 * Draw a rectangle of one colour from the given locations. No blending
	 * used. Will draw a border around it
	 * 
	 * @param x1
	 *            The left position
	 * @param y1
	 *            The top position
	 * @param x2
	 *            The right position
	 * @param y2
	 *            The bottom position
	 * @param colour
	 *            The colour in format 0xAARRGGBB. A = Alpha, R = Red, G =
	 *            Green, B = Blue
	 */
	public void drawSizedRectWithBorder(int x1, int y1, int x2, int y2, int colour) {
		this.drawBorder(x1, y1, x2 - x1, y2 - y1);
		this.drawSizedRect(x1 + 1, y1 + 1, x2 - 1, y2 - 1, colour);
	}

	/**
	 * Draw a scaled textured rectangle from the given icon
	 * 
	 * @param x
	 *            The x position on the screen
	 * @param y
	 *            The y position on the screen
	 * @param icon
	 *            The icon to draw
	 * @param width
	 *            The width of the rectangle
	 * @param height
	 *            The height of the rectangle
	 */
	public void drawScaledTexturedModelRectFromIcon(int x, int y, TextureAtlasSprite icon, int width, int height) {
		if (icon == null)
			return;
		double minU = icon.getMinU();
		double maxU = icon.getMaxU();
		double minV = icon.getMinV();
		double maxV = icon.getMaxV();

		BufferBuilder buffer = Tessellator.getInstance().getBuffer();
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		buffer.pos(x, y + height, this.zLevel).tex(minU, minV + (maxV - minV) * height / 16F).endVertex();
		buffer.pos(x + width, y + height, this.zLevel)
				.tex(minU + (maxU - minU) * width / 16F, minV + (maxV - minV) * height / 16F).endVertex();
		buffer.pos(x + width, y, this.zLevel).tex(minU + (maxU - minU) * width / 16F, minV).endVertex();
		buffer.pos(x, y, this.zLevel).tex(minU, minV).endVertex();
		Tessellator.getInstance().draw();
	}

	/**
	 * Draw a tooltip
	 * 
	 * @param tooltip
	 *            The tooltip to draw
	 * @param x
	 *            The x position of the tooltip
	 * @param y
	 *            The y position of the tooltip
	 * @param font
	 *            The font the tooltip will use
	 */
	protected void drawTooltipHoveringText(List<String> tooltip, int x, int y, FontRenderer font) {
		if (tooltip == null || tooltip.isEmpty())
			return;
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableLighting();
		GlStateManager.disableDepth();
		int k = 0;
		Iterator<String> iterator = tooltip.iterator();

		while (iterator.hasNext()) {
			String s = (String) iterator.next();
			int l = font.getStringWidth(s);

			if (l > k) {
				k = l;
			}
		}
		int i1 = x + 12;
		int j1 = y - 12;
		int k1 = 8;

		if (tooltip.size() > 1) {
			k1 += 2 + (tooltip.size() - 1) * 10;
		}
		if (i1 + k > this.width) {
			i1 -= 28 + k;
		}
		if (j1 + k1 + 6 > this.height) {
			j1 = this.height - k1 - 6;
		}
		this.zLevel = 300.0F;
		itemRender.zLevel = 300.0F;
		int l1 = -267386864;
		this.drawGradientRect(i1 - 3, j1 - 4, i1 + k + 3, j1 - 3, l1, l1);
		this.drawGradientRect(i1 - 3, j1 + k1 + 3, i1 + k + 3, j1 + k1 + 4, l1, l1);
		this.drawGradientRect(i1 - 3, j1 - 3, i1 + k + 3, j1 + k1 + 3, l1, l1);
		this.drawGradientRect(i1 - 4, j1 - 3, i1 - 3, j1 + k1 + 3, l1, l1);
		this.drawGradientRect(i1 + k + 3, j1 - 3, i1 + k + 4, j1 + k1 + 3, l1, l1);
		int i2 = 1347420415;
		int j2 = (i2 & 16711422) >> 1 | i2 & -16777216;
		this.drawGradientRect(i1 - 3, j1 - 3 + 1, i1 - 3 + 1, j1 + k1 + 3 - 1, i2, j2);
		this.drawGradientRect(i1 + k + 2, j1 - 3 + 1, i1 + k + 3, j1 + k1 + 3 - 1, i2, j2);
		this.drawGradientRect(i1 - 3, j1 - 3, i1 + k + 3, j1 - 3 + 1, i2, i2);
		this.drawGradientRect(i1 - 3, j1 + k1 + 2, i1 + k + 3, j1 + k1 + 3, j2, j2);

		for (int k2 = 0; k2 < tooltip.size(); ++k2) {
			String s1 = tooltip.get(k2);
			font.drawStringWithShadow(s1, i1, j1, -1);

			if (k2 == 0) {
				j1 += 2;
			}
			j1 += 10;
		}
		this.zLevel = 0.0F;
		itemRender.zLevel = 0.0F;
		GlStateManager.enableLighting();
		GlStateManager.enableDepth();
		GlStateManager.enableRescaleNormal();
	}

	public void drawHorizontalLine(int startX, int endX, int y, int colour) {
		super.drawHorizontalLine(startX, endX, y, colour);
	}

	public void drawVerticalLine(int x, int startY, int endY, int colour) {
		super.drawVerticalLine(x, startY, endY, colour);
	}
	
	public void drawBorder(int x, int y, int width, int height) {
		this.drawVerticalLine(x, y - 1, y + height - 1, 0xFF373737);
		this.drawHorizontalLine(x + 1, x + width - 2, y, 0xFF373737);
		this.drawHorizontalLine(x + width - 1, x + width - 1, y, 0xFF8B8B8B);
		this.drawHorizontalLine(x, x, y + height - 1, 0xFF8B8B8B);
		this.drawVerticalLine(x + width - 1, y, y + height, 0xFFFFFFFF);
		this.drawHorizontalLine(x + 1, x + width - 1, y + height - 1, 0xFFE2E2E2);
	}

	/**
	 * Play the click sound
	 * 
	 * @param volume
	 *            The volume of the sound
	 * @param pitch
	 *            The pitch of the sound
	 */
	public static void playClickSound(float volume, float pitch) {
		SoundUtils.playSound(new SoundBase(SoundEvents.UI_BUTTON_CLICK, SoundCategory.MASTER, volume, pitch));
	}

	/**
	 * Get the font renderer for the gui
	 * 
	 * @return The font renderer
	 */
	public FontRenderer getFontRenderer() {
		return this.fontRenderer;
	}

	/**
	 * Get the x position of the given string so it is centered on the gui
	 * 
	 * @param string
	 *            The string to center
	 * @return The x position of the string so that it is centered on the gui
	 */
	protected int getCenteredOffset(String string) {
		return this.getCenteredOffset(string, this.xSize);
	}

	/**
	 * Get the x position of the given string so it is centered on the gui
	 * 
	 * @param string
	 *            The string to center
	 * @param xWidth
	 *            The width of the string
	 * @return The x position of the string so that it is centered on the gui
	 */
	protected int getCenteredOffset(String string, int xWidth) {
		return (xWidth - this.fontRenderer.getStringWidth(string)) / 2;
	}

}
