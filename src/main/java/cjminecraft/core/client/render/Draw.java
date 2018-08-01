package cjminecraft.core.client.render;

import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import cjminecraft.core.access.AccessHandler;
import cjminecraft.core.client.GameResources;
import cjminecraft.core.world.Worlds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;

public class Draw {

	public static BufferBuilder buffer() {
		return Tessellator.getInstance().getBuffer();
	}

	public static void startQuads() {
		buffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
	}

	public static void startTriangleFan() {
		buffer().begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION);
	}

	public static void startQuadsColored() {
		buffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
	}

	public static void tessellate() {
		Tessellator.getInstance().draw();
	}

	public static BufferBuilder vertex(int x, int y, int z) {
		return vertex((double) x, (double) y, (double) z);
	}

	public static BufferBuilder vertex(double x, double y, double z) {
		return buffer().pos(x, y, z);
	}

	public static BufferBuilder vertex(int x, int y, int z, float u, float v) {
		return vertex((double) x, (double) y, (double) z, u, v);
	}

	public static BufferBuilder vertex(double x, double y, double z, float u, float v) {
		return buffer().pos(x, y, z).tex(u, v);
	}

	public static BufferBuilder vertex(double x, double y, double z, double u, double v) {
		return buffer().pos(x, y, z).tex(u, v);
	}

	public static void triangle(Vertex vertex1, Vertex vertex2, Vertex vertex3) {
		triangle(vertex1, vertex2, vertex3, false);
	}

	public static void triangle(Vertex vertex1, Vertex vertex2, Vertex vertex3, boolean cullFace) {
		Draw.startTriangleFan();
		Draw.vertex(vertex1.x, vertex1.y, vertex1.z).endVertex();
		Draw.vertex(vertex2.x, vertex2.y, vertex2.z).endVertex();
		Draw.vertex(vertex3.x, vertex3.y, vertex3.z).endVertex();

		if (cullFace) {
			Draw.vertex(vertex3.x, vertex3.y, vertex3.z).endVertex();
			Draw.vertex(vertex2.x, vertex2.y, vertex2.z).endVertex();
			Draw.vertex(vertex1.x, vertex1.y, vertex1.z).endVertex();
		}
		Draw.tessellate();
	}

	public static interface ITooltipLineHandler {
		public Dimension getSize();

		public void draw(int x, int y);
	}

	public static void line(int x1, int y1, int x2, int y2, float depth, float width, int color) {
		GL11.glLineWidth(width);
		OpenGL.color4i(color);
		OpenGL.translate(0F, 0F, depth);
		// TODO: Find a replacement for this, doesn't seem to have an existing
		// replacement in GLStateManager.
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex2d(x1, y1);
		GL11.glVertex2d(x2, y2);
		GL11.glEnd();
		GL11.glLineWidth(1.0F);
		OpenGL.translate(0F, 0F, -depth);
	}

	/**
	 * Draws a rectangle at the specified coordinates, with the specified width, height and color.
	 * 
	 * @param x
	 *            - x coordinate
	 * @param y
	 *            - y coordinate
	 * @param w
	 *            - Width of this rectangle
	 * @param h
	 *            - Height of this rectangle
	 * @param color
	 *            - Color of this rectangle
	 */
	public static void drawRect(int x, int y, int w, int h, int color) {
		Draw.drawGradientRect(x, y, w, h, color, color);
	}

	/**
	 * Draws a rectangle at the specified coordinates, with the specified width, height and linear gradient color.
	 * 
	 * @param x
	 *            - x coordinate
	 * @param y
	 *            - y coordinate
	 * @param w
	 *            - Width of this rectangle
	 * @param h
	 *            - Height of this rectangle
	 * @param color1
	 *            - First color of the linear gradient
	 * @param color2
	 *            - Second color of the linear gradient
	 */
	public static void drawGradientRect(int x, int y, int w, int h, int color1, int color2) {
		Draw.drawGradientRect(x, y, x + w, y + h, 0, color1, color2);
	}

	/**
	 * Draws a rectangle at the specified coordinates, with the specified width, height and linear gradient color.
	 * 
	 * @param x
	 *            - x coordinate
	 * @param y
	 *            - y coordinate
	 * @param w
	 *            - Width of this rectangle
	 * @param h
	 *            - Height of this rectangle
	 * @param zLevel
	 *            - z level of which to draw the rectangle on.
	 * @param color1
	 *            - First color of the linear gradient
	 * @param color2
	 *            - Second color of the linear gradient
	 */
	public static void drawGradientRect(int x, int y, int w, int h, int zLevel, int color1, int color2) {
		OpenGL.disableTexture2d();
		OpenGL.shadeSmooth();
		startQuadsColored();
		vertex(w, y, zLevel).color((color1 >> 16 & 255) / 255.0F, (color1 >> 8 & 255) / 255.0F, (color1 & 255) / 255.0F, (color1 >> 24 & 255) / 255.0F).endVertex();
		vertex(x, y, zLevel).color((color1 >> 16 & 255) / 255.0F, (color1 >> 8 & 255) / 255.0F, (color1 & 255) / 255.0F, (color1 >> 24 & 255) / 255.0F).endVertex();
		vertex(x, h, zLevel).color((color2 >> 16 & 255) / 255.0F, (color2 >> 8 & 255) / 255.0F, (color2 & 255) / 255.0F, (color2 >> 24 & 255) / 255.0F).endVertex();
		vertex(w, h, zLevel).color((color2 >> 16 & 255) / 255.0F, (color2 >> 8 & 255) / 255.0F, (color2 & 255) / 255.0F, (color2 >> 24 & 255) / 255.0F).endVertex();
		tessellate();
		OpenGL.shadeFlat();
		OpenGL.enableTexture2d();
	}

	/**
	 * Draws a quad at the specified coordinates, with the specified width and height
	 * 
	 * @param x
	 *            - x coordinate
	 * @param y
	 *            - y coordinate
	 * @param w
	 *            - Width of this quad
	 * @param h
	 *            - Height of this quad
	 */
	public static void drawQuad(int x, int y, int w, int h) {
		Draw.drawQuad(x, y, w, h, -90);
	}

	/**
	 * Draws a quad at the specified coordinates, with the specified width and height on the specified z level.
	 * 
	 * @param x
	 *            - x coordinate
	 * @param y
	 *            - y coordinate
	 * @param w
	 *            - Width of this quad
	 * @param h
	 *            - Height of this quad
	 * @param z
	 *            - z level to render this quad on
	 */
	public static void drawQuad(int x, int y, int w, int h, int z) {
		Draw.drawQuad(x, y, w, h, z, 0, 1, 0, 1);
	}

	/**
	 * Draws a quad at the specified coordinates, with the specified width and height and specified texture uv coords.
	 * 
	 * @param x
	 *            - x coordinate
	 * @param y
	 *            - y coordinate
	 * @param w
	 *            - Width of this quad
	 * @param h
	 *            - Height of this quad
	 * @param u
	 *            - x coordinate of the texture to draw on the quad.
	 * @param v
	 *            - y coordinate of the texture to draw on the quad.
	 */
	public static void drawQuad(int x, int y, int w, int h, int u, int v) {
		Draw.drawQuad(x, y, w, h, -90, u, v);
	}

	/**
	 * Draws a quad at the specified coordinates, with the specified width and height and specified texture uv coords.
	 * 
	 * @param x
	 *            - x coordinate
	 * @param y
	 *            - y coordinate
	 * @param w
	 *            - Width of this quad
	 * @param h
	 *            - Height of this quad
	 * @param z
	 *            - z level to render this quad on
	 * @param u
	 *            - x coordinate of the texture to draw on the quad.
	 * @param v
	 *            - y coordinate of the texture to draw on the quad.
	 */
	public static void drawQuad(int x, int y, int w, int h, int z, int u, int v) {
		float f = 0.00390625F;
		float f1 = 0.00390625F;
		startQuads();
		vertex(x + 0, y + h, z, (u + 0) * f, (v + h) * f1).endVertex();
		vertex(x + w, y + h, z, (u + w) * f, (v + h) * f1).endVertex();
		vertex(x + w, y + 0, z, (u + w) * f, (v + 0) * f1).endVertex();
		vertex(x + 0, y + 0, z, (u + 0) * f, (v + 0) * f1).endVertex();
		tessellate();
	}

	/**
	 * Draws a quad at the specified coordinates, with the specified width and height and specified texture uv coords.
	 * 
	 * @param x
	 *            - x coordinate
	 * @param y
	 *            - y coordinate
	 * @param w
	 *            - Width of this quad
	 * @param h
	 *            - Height of this quad
	 * @param minU
	 *            - x coordinate of the texture to draw on the quad.
	 * @param maxU
	 *            - width of the texture being draw on this quad.
	 * @param minV
	 *            - y coordinate of the texture to draw on the quad.
	 * @param maxV
	 *            - height of the texture being draw on this quad.
	 */
	public static void drawQuad(int x, int y, int w, int h, float minU, float maxU, float minV, float maxV) {
		Draw.drawQuad(x, y, w, h, -90, minU, maxU, minV, maxV);
	}

	/**
	 * Draws a quad at the specified coordinates, with the specified width and height and specified texture uv coords.
	 * 
	 * @param x
	 *            - x coordinate
	 * @param y
	 *            - y coordinate
	 * @param w
	 *            - Width of this quad
	 * @param h
	 *            - Height of this quad
	 * @param z
	 *            - z level to render this quad on
	 * @param minU
	 *            - x coordinate of the texture to draw on the quad.
	 * @param maxU
	 *            - width of the texture being draw on this quad.
	 * @param minV
	 *            - y coordinate of the texture to draw on the quad.
	 * @param maxV
	 *            - height of the texture being draw on this quad.
	 */
	public static void drawQuad(int x, int y, int w, int h, int z, float minU, float maxU, float minV, float maxV) {
		startQuads();
		vertex(x + 0, y + h, z, minU, maxV).endVertex();
		vertex(x + w, y + h, z, maxU, maxV).endVertex();
		vertex(x + w, y + 0, z, maxU, minV).endVertex();
		vertex(x + 0, y + 0, z, minU, minV).endVertex();
		tessellate();
	}

	/**
	 * Draw the specified String at the specified coordinates using the specified color.
	 * 
	 * @param text
	 *            - String to draw
	 * @param x
	 *            - x coordinate to draw at
	 * @param y
	 *            - y coordinate to draw at
	 * @param color
	 *            - Color to draw using
	 * @param shadow
	 *            - Set to true to draw a shadow beneath the rendered string.
	 */
	public static void drawString(String text, int x, int y, int color, boolean shadow) {
		String original = text;
		text = I18n.translateToLocal(text);

		if (text.toLowerCase().contains("error:".toLowerCase())) {
			text = original;
		}

		if (shadow) {
			AccessHandler.getFontRenderer().drawStringWithShadow(text, x, y, color);
		}

		if (!shadow) {
			AccessHandler.getFontRenderer().drawString(text, x, y, color);
		}

		OpenGL.color3i(0xFFFFFF);
		GlStateManager.resetColor();
	}

	/**
	 * Draw the specified String at the specified coordinates using the specified color.
	 * 
	 * @param text
	 *            - String to draw
	 * @param x
	 *            - x coordinate to draw at
	 * @param y
	 *            - y coordinate to draw at
	 * @param color
	 *            - Color to draw using
	 */
	public static void drawString(String text, int x, int y, int color) {
		drawString(text, x, y, color, true);
	}

	/**
	 * Draw the specified String centered at the specified coordinates using the specified color.
	 *
	 * @param text
	 *            - String to draw
	 * @param x
	 *            - x coordinate to draw at
	 * @param y
	 *            - y coordinate to draw at
	 * @param w
	 *            - width of the string
	 * @param h
	 *            - height of the string
	 * @param color
	 *            - Color to draw using
	 * @param shadow
	 *            - Set to true to draw a shadow beneath the rendered string.
	 */
	public static void drawStringAlignCenter(String text, int x, int y, int w, int h, int color, boolean shadow) {
		drawString(text, x + (w - Draw.getStringRenderWidth(I18n.translateToLocal(text))) / 2, y + (h - 8) / 2, color, shadow);
	}

	/**
	 * Draw the specified String centered at the specified coordinates using the specified color.
	 * 
	 * @param text
	 *            - String to draw
	 * @param x
	 *            - x coordinate to draw at
	 * @param y
	 *            - y coordinate to draw at
	 * @param color
	 *            - Color to draw using
	 */
	public static void drawStringAlignCenter(String text, int x, int y, int w, int h, int color) {
		drawStringAlignCenter(text, x, y, w, h, color, true);
	}

	/**
	 * Draw the specified String centered at the specified coordinates using the specified color.
	 * 
	 * @param text
	 *            - String to draw
	 * @param x
	 *            - x coordinate to draw at
	 * @param y
	 *            - y coordinate to draw at
	 * @param color
	 *            - Color to draw using
	 * @param shadow
	 *            - Set to true to draw a shadow beneath the rendered string.
	 */
	public static void drawStringAlignCenter(String text, int x, int y, int color, boolean shadow) {
		drawString(text, x - Draw.getStringRenderWidth(I18n.translateToLocal(text)) / 2, y, color, shadow);
	}

	/**
	 * Draw the specified String centered at the specified coordinates using the specified color.
	 * 
	 * @param text
	 *            - String to draw
	 * @param x
	 *            - x coordinate to draw at
	 * @param y
	 *            - y coordinate to draw at
	 * @param color
	 *            - Color to draw using
	 */
	public static void drawStringAlignCenter(String text, int x, int y, int color) {
		drawStringAlignCenter(text, x, y, color, true);
	}

	/**
	 * Draw the specified String aligned to the right at the specified coordinates using the specified color.
	 * 
	 * @param text
	 *            - String to draw
	 * @param x
	 *            - x coordinate to draw at
	 * @param y
	 *            - y coordinate to draw at
	 * @param color
	 *            - Color to draw using
	 * @param shadow
	 *            - Set to true to draw a shadow beneath the rendered string.
	 */
	public static void drawStringAlignRight(String text, int x, int y, int color, boolean shadow) {
		drawString(text, x - Draw.getStringRenderWidth(I18n.translateToLocal(text)), y, color, shadow);
	}

	/**
	 * Draw the specified String aligned to the right at the specified coordinates using the specified color.
	 * 
	 * @param text
	 *            - String to draw
	 * @param x
	 *            - x coordinate to draw at
	 * @param y
	 *            - y coordinate to draw at
	 * @param color
	 *            - Color to draw using
	 */
	public static void drawStringAlignRight(String text, int x, int y, int color) {
		drawStringAlignRight(text, x, y, color, true);
	}

	/**
	 * @param s
	 *            - String to get the render width for.
	 * @return The render width of the specified String.
	 */
	public static int getStringRenderWidth(String s) {
		return AccessHandler.getFontRenderer().getStringWidth(TextFormatting.getTextWithoutFormattingCodes(s));
	}

	public static final String TOOLTIP_LINESPACE = "\u00A7h";
	public static final String TOOLTIP_HANDLER = "\u00A7x";
	public static List<Draw.ITooltipLineHandler> tipLineHandlers = new ArrayList<Draw.ITooltipLineHandler>();

	public static int getTipLineId(ITooltipLineHandler handler) {
		tipLineHandlers.add(handler);
		return tipLineHandlers.size() - 1;
	}

	public static ITooltipLineHandler getTipLine(String line) {
		return !line.startsWith(TOOLTIP_HANDLER) ? null : tipLineHandlers.get(Integer.parseInt(line.substring(2)));
	}

	/**
	 * Draws a tooltip box at the specified cordinates, with the specified width and height.
	 * 
	 * @param x
	 *            - x coordinate
	 * @param y
	 *            - y coordinate
	 * @param w
	 *            - Width of the box
	 * @param h
	 *            - Height of the box
	 */
	public static void drawTooltipBox(int x, int y, int w, int h) {
		int bg = 0xf0100010;
		drawGradientRect(x + 1, y, w - 1, 1, bg, bg);
		drawGradientRect(x + 1, y + h, w - 1, 1, bg, bg);
		drawGradientRect(x + 1, y + 1, w - 1, h - 1, bg, bg);
		drawGradientRect(x, y + 1, 1, h - 1, bg, bg);
		drawGradientRect(x + w, y + 1, 1, h - 1, bg, bg);
		int grad1 = 0x505000ff;
		int grad2 = 0x5028007F;
		drawGradientRect(x + 1, y + 2, 1, h - 3, grad1, grad2);
		drawGradientRect(x + w - 1, y + 2, 1, h - 3, grad1, grad2);
		drawGradientRect(x + 1, y + 1, w - 1, 1, grad1, grad1);
		drawGradientRect(x + 1, y + h - 1, w - 1, 1, grad2, grad2);
	}

	/**
	 * Draws a progress bar.
	 * 
	 * @param label
	 *            - Label to draw on top of the progress bar.
	 * @param maxProgress
	 *            - Maximum progress
	 * @param curProgress
	 *            - Current progress
	 * @param posX
	 *            - x coordinate to draw the bar at
	 * @param posY
	 *            - y coordinate to draw the bar at
	 * @param barWidth
	 *            - The width of the progress bar
	 * @param barHeight
	 *            - The height of the progress bar
	 * @param stringPosY
	 *            - The offset height of the label text (0 is default)
	 * @param color
	 *            - The color of the progress bar
	 * @param barStyle
	 *            - Set to false for a solid style progress bar. Set to true for a box-style progress bar.
	 */
	public static void drawProgressBar(String label, int maxProgress, int curProgress, int posX, int posY, int barWidth, int barHeight, int stringPosY, int color, boolean barStyle) {
		OpenGL.pushMatrix();
		{
			Gui.drawRect(posX + 0, posY + 0, posX + barWidth, posY + 5 + barHeight, 0x77000000);

			if (!barStyle && curProgress > maxProgress / barWidth) {
				Gui.drawRect(posX + 1, posY + 1, posX + ((((curProgress * maxProgress) / maxProgress) * barWidth) / maxProgress) - 1, posY + 4 + barHeight, color);
				Gui.drawRect(posX + 1, posY + 2 + (barHeight / 2), posX + ((((curProgress * maxProgress) / maxProgress) * barWidth) / maxProgress) - 1, posY + 4 + barHeight, 0x55000000);
			} else if (curProgress > maxProgress / barWidth) {
				int spaceBetweenBars = 1;
				int amountOfBars = 70;
				int widthOfBar = (barWidth / amountOfBars - spaceBetweenBars);

				for (int x = 1; x <= amountOfBars - ((curProgress * amountOfBars) / maxProgress); x++) {
					int barStartX = (posX + widthOfBar) * (x) - widthOfBar;

					Gui.drawRect(barStartX + spaceBetweenBars * x, posY + 1, barStartX + widthOfBar + spaceBetweenBars * x, posY + 4 + barHeight, color);
					Gui.drawRect(barStartX + spaceBetweenBars * x, posY + 2 + (barHeight / 2), barStartX + widthOfBar + spaceBetweenBars * x, posY + 4 + barHeight, 0x55000000);
				}
			}

			AccessHandler.getFontRenderer().drawStringWithShadow(label, posX + (barWidth / 2) - AccessHandler.getFontRenderer().getStringWidth(label) + (AccessHandler.getFontRenderer().getStringWidth(label) / 2), (posY - 1) + stringPosY, 0xFFFFFFFF);
		}
		OpenGL.popMatrix();
	}

	/**
	 * Draws a centered rectangle with an outline at the specified coordinates and the specified width, height, and color.
	 * 
	 * @param x
	 *            - x coordinate
	 * @param y
	 *            - y coordinate
	 * @param w
	 *            - Width of this rectangle
	 * @param h
	 *            - Height of this rectangle
	 * @param borderWidth
	 *            - Width of the rectangle's border
	 * @param fillColor
	 *            - Color of the inner portion of this rectangle
	 * @param borderColor
	 *            - Color of the border of this rectangle
	 */
	public static void drawCenteredRectWithOutline(int x, int y, int w, int h, int borderWidth, int fillColor, int borderColor) {
		drawRect(x - w / 2 + borderWidth, y - h / 2, w, h, fillColor);
		drawRect(x - w / 2 + borderWidth, y - h / 2, w, borderWidth, borderColor);
		drawRect(x - w / 2, y + h / 2, w, borderWidth, borderColor);
		drawRect(x - w / 2, y - h / 2, borderWidth, h, borderColor);
		drawRect(x + w / 2, y - h / 2 + borderWidth, borderWidth, h, borderColor);
	}

	/**
	 * Draws a rectangle with an outline at the specified coordinates and the specified width, height, and color.
	 * 
	 * @param x
	 *            - x coordinate
	 * @param y
	 *            - y coordinate
	 * @param w
	 *            - Width of this rectangle
	 * @param h
	 *            - Height of this rectangle
	 * @param borderWidth
	 *            - Width of the rectangle's border
	 * @param fillColor
	 *            - Color of the inner portion of this rectangle
	 * @param borderColor
	 *            - Color of the border of this rectangle
	 */
	public static void drawRectWithOutline(int x, int y, int w, int h, int borderWidth, int fillColor, int borderColor) {
		int x1 = x;
		int y1 = y;
		int x2 = x + w;
		int y2 = y + h;

		Gui.drawRect(x1, y1, x2, y2, fillColor);
		Gui.drawRect(x1, y1, x2, y2 - h + borderWidth, borderColor);
		Gui.drawRect(x1, y1 + h - borderWidth, x2, y2, borderColor);
		Gui.drawRect(x1, y1 + borderWidth, x2 - w + borderWidth, y2 - borderWidth, borderColor);
		Gui.drawRect(x1 + w - borderWidth, y1 + borderWidth, x2, y2 - borderWidth, borderColor);
	}

	/**
	 * Draws an overlay across the entire screen using the specified ResourceLocation
	 * 
	 * @param resource
	 *            - The ResourceLocation to draw
	 */
	public static void drawOverlay(ResourceLocation resource) {
		Draw.drawOverlay(resource, 1.0F, 1.0F, 1.0F, 1.0F);
	}

	/**
	 * Draws an overlay across the entire screen using the specified ResourceLocation and an alpha value.
	 * 
	 * @param resource
	 *            - The ResourceLocation to draw
	 * @param a
	 *            - Alpha value to render the overlay at. For transparency.
	 */
	public static void drawOverlay(ResourceLocation resource, float a) {
		Draw.drawOverlay(resource, 1.0F, 1.0F, 1.0F, a);
	}

	/**
	 * Draws an overlay across the entire screen using the specified ResourceLocation and 3 RGB color values.
	 * 
	 * @param resource
	 *            - The ResourceLocation to draw
	 * @param r
	 *            - Red value to render the overlay at.
	 * @param g
	 *            - Green value to render the overlay at.
	 * @param b
	 *            - Blue value to render the overlay at.
	 */
	public static void drawOverlay(ResourceLocation resource, float r, float g, float b) {
		Draw.drawOverlay(resource, r, g, b, 1.0F);
	}

	/**
	 * Draws an overlay across the entire screen using the specified ResourceLocation and 4 RGBA color values.
	 * 
	 * @param resource
	 *            - The ResourceLocation to draw
	 * @param r
	 *            - Red value to render the overlay at.
	 * @param g
	 *            - Green value to render the overlay at.
	 * @param b
	 *            - Blue value to render the overlay at.
	 * @param a
	 *            - Alpha value to render the overlay at. For transparency.
	 */
	public static void drawOverlay(ResourceLocation resource, float r, float g, float b, float a) {
		OpenGL.enableBlend();
		OpenGL.disableDepthTest();
		OpenGL.depthMask(false);
		OpenGL.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		OpenGL.color(r, g, b, a);
		OpenGL.disableAlphaTest();
		Draw.bindTexture(resource);
		drawQuad(0, 0, Screen.scaledDisplayResolution().getScaledWidth(), Screen.scaledDisplayResolution().getScaledHeight());
		OpenGL.depthMask(true);
		OpenGL.enableDepthTest();
		OpenGL.enableAlphaTest();
		OpenGL.color(1.0F, 1.0F, 1.0F, 1.0F);
		OpenGL.disableBlend();
	}

	/**
	 * Draw the specified ModelBase instance at 0,0,0 with the specified ResourceLocation.
	 * 
	 * @param model
	 *            - ModelBase instance to draw.
	 * @param resource
	 *            - ResourceLocation to draw on the ModelBase instance.
	 */
	public static void drawModel(ModelBase model, ResourceLocation resource) {
		Draw.drawModel(null, model, resource, 0, 0, 0);
	}

	/**
	 * Draw the specified ModelBase instance at the specified coordinates with the specified ResourceLocation.
	 * 
	 * @param model
	 *            - ModelBase instance to draw.
	 * @param resource
	 *            - ResourceLocation to draw on the ModelBase instance.
	 * @param posX
	 *            - x coordinate to draw this model at.
	 * @param posY
	 *            - y coordinate to draw this model at.
	 * @param posZ
	 *            - z coordinate to draw this model at.
	 */
	public static void drawModel(ModelBase model, ResourceLocation resource, double posX, double posY, double posZ) {
		Draw.drawModel(null, model, resource, posX, posY, posZ);
	}

	/**
	 * Draw the specified ModelBase instance at the specified coordinates with the specified ResourceLocation.
	 * 
	 * @param entity
	 *            - The entity class to provide the ModelBase instance with.
	 * @param model
	 *            - ModelBase instance to draw.
	 * @param resource
	 *            - ResourceLocation to draw on the ModelBase instance.
	 */
	public static void drawModel(Entity entity, ModelBase model, ResourceLocation resource) {
		Draw.drawModel(entity, model, resource, 0, 0, 0);
	}

	/**
	 * Draw the specified ModelBase instance at the specified coordinates with the specified ResourceLocation.
	 * 
	 * @param entity
	 *            - The entity class to provide the ModelBase instance with.
	 * @param model
	 *            - ModelBase instance to draw.
	 * @param resource
	 *            - ResourceLocation to draw on the ModelBase instance.
	 * @param posX
	 *            - x coordinate to draw this model at.
	 * @param posY
	 *            - y coordinate to draw this model at.
	 * @param posZ
	 *            - z coordinate to draw this model at.
	 */
	public static void drawModel(Entity entity, ModelBase model, ResourceLocation resource, double posX, double posY, double posZ) {
		OpenGL.disableCullFace();
		Draw.bindTexture(resource);
		OpenGL.translate(posX, posY, posZ);
		model.render(entity, 0, 0, 0, 0, 0, 0.625F);
	}

	/**
	 * Draw the specified ModelBase instance at the specified coordinates with the specified ResourceLocation.
	 * 
	 * @param model
	 *            - ModelBase instance to draw.
	 * @param resource
	 *            - ResourceLocation to draw on the ModelBase instance.
	 * @param x
	 *            - x coordinate to draw this model at.
	 * @param y
	 *            - y coordinate to draw this model at.
	 * @param scale
	 *            - The scale this model should be rendered at.
	 */
	public static void drawShowcaseModel(ModelBase model, ResourceLocation resource, int x, int y, float scale) {
		OpenGL.color(1.0F, 1.0F, 1.0F, 1.0F);
		OpenGL.pushMatrix();
		OpenGL.translate(x, y - (scale * 0.43f), 10);
		OpenGL.scale(0.06f * scale, 0.06f * scale, 1);
		OpenGL.rotate(-20, 1, 0, 0);
		OpenGL.rotate(205, 0, 1, 0);
		OpenGL.disableCullFace();
		OpenGL.enableDepthTest();
		Draw.bindTexture(resource);
		model.render(null, 0F, 0F, 0F, 0F, 0F, 0.0625F);
		OpenGL.enableCullFace();
		OpenGL.disableDepthTest();
		OpenGL.popMatrix();
	}

	/**
	 * Draw the specified entity at the specified coordinates using the specified scale, yaw, and pitch.
	 * 
	 * @param x
	 *            - x coordinate
	 * @param y
	 *            - y coordinate
	 * @param scale
	 *            - The scale this model should be rendered at.
	 * @param yaw
	 *            - The rotation yaw.
	 * @param pitch
	 *            - The rotation pitch.
	 * @param entity
	 *            - The Entity instance that is being rendered.
	 */
	public static void drawEntity(int x, int y, int scale, float yaw, float pitch, Entity entity) {
		if (!AccessHandler.getMinecraft().world.isRemote) {
			GlStateManager.enableColorMaterial();
			OpenGL.pushMatrix();
			{
				OpenGL.translate(x, y, 100.0F);
				OpenGL.scale(-scale, scale, scale);
				OpenGL.rotate(180.0F, 0.0F, 0.0F, 1.0F);
				OpenGL.rotate(yaw, 0.0F, 1.0F, 0.0F);
				OpenGL.rotate(pitch, 1.0F, 0.0F, 0.0F);
				AccessHandler.getMinecraft().getRenderManager().renderEntityStatic(entity, AccessHandler.getPartialTicks(), true);
				OpenGL.enableLightMapping();
			}
			OpenGL.popMatrix();
		}
	}

	/**
	 * Draw the client player's face. Will default to a Steve face if one is not present.
	 * 
	 * @param player
	 *            - The client player
	 * @param x
	 *            - x coordinate
	 * @param y
	 *            - y coordinate
	 * @param width
	 *            - Width to render the face at.
	 * @param height
	 *            - Height to render the face at.
	 */
	public static void drawPlayerFace(EntityPlayer player, int x, int y, int width, int height) {
		if (player instanceof AbstractClientPlayer) {
			AbstractClientPlayer clientPlayer = (AbstractClientPlayer) player;
			Draw.bindTexture(clientPlayer.getLocationSkin());
			drawQuad(x, y, width, height, 90, 0.125F, 0.25F, 0.125F, 0.25F);
			drawQuad(x, y, width, height, 90, 0.625F, 0.75F, 0.125F, 0.25F);
		}
	}

	/**
	 * Draw the specified ResourceLocation at the specified coordinates and dimensions.
	 * 
	 * @param resource
	 *            - ResourceLocation to render
	 * @param posX
	 *            - x coordinate
	 * @param posY
	 *            - y coordinate
	 * @param width
	 *            - Width to render this resource at.
	 * @param height
	 *            - Height to render this resource at.
	 */
	public static void drawResource(ResourceLocation resource, int posX, int posY, int width, int height) {
		Draw.drawResource(resource, posX, posY, width, height, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f);
	}

	/**
	 * Draw the specified ResourceLocation at the specified coordinates and dimensions.
	 * 
	 * @param resource
	 *            - ResourceLocation to render
	 * @param posX
	 *            - x coordinate
	 * @param posY
	 *            - y coordinate
	 * @param width
	 *            - Width to render this resource at.
	 * @param height
	 *            - Height to render this resource at.
	 * @param r
	 *            - Red value
	 * @param g
	 *            - Green value
	 * @param b
	 *            - Blue value
	 * @param a
	 *            - Alpha value (Transparency)
	 */
	public static void drawResource(ResourceLocation resource, int posX, int posY, int width, int height, float r, float g, float b, float a) {
		Draw.drawResource(resource, posX, posY, width, height, r, g, b, a, 1.0f, 1.0f);
	}

	/**
	 * Draw the specified ResourceLocation at the specified coordinates and dimensions.
	 * 
	 * @param resource
	 *            - ResourceLocation to render
	 * @param posX
	 *            - x coordinate
	 * @param posY
	 *            - y coordinate
	 * @param width
	 *            - Width to render this resource at.
	 * @param height
	 *            - Height to render this resource at.
	 * @param r
	 *            - Red value
	 * @param g
	 *            - Green value
	 * @param b
	 *            - Blue value
	 * @param a
	 *            - Alpha value (Transparency)
	 * @param u
	 *            - x coordinate of the texture offset
	 * @param v
	 *            - y coordinate of the texture offset
	 */
	public static void drawResource(ResourceLocation resource, int posX, int posY, int width, int height, float r, float g, float b, float a, float u, float v) {
		OpenGL.disableLighting();
		OpenGL.disableFog();
		Draw.bindTexture(resource);
		OpenGL.color(r, g, b, a);
		drawQuad(posX, posY, width, height, 0, 0, u, 0, v);
	}

	/**
	 * Draw the specified ResourceLocation centered at the specified coordinates and dimensions.
	 * 
	 * @param resource
	 *            - ResourceLocation to render
	 * @param posX
	 *            - x coordinate
	 * @param posY
	 *            - y coordinate
	 * @param width
	 *            - Width to render this resource at.
	 * @param height
	 *            - Height to render this resource at.
	 */
	public static void drawResourceCentered(ResourceLocation resource, int posX, int posY, int width, int height) {
		Draw.drawResourceCentered(resource, posX, posY, width, height, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f);
	}

	/**
	 * Draw the specified ResourceLocation centered at the specified coordinates and dimensions.
	 * 
	 * @param resource
	 *            - ResourceLocation to render
	 * @param posX
	 *            - x coordinate
	 * @param posY
	 *            - y coordinate
	 * @param width
	 *            - Width to render this resource at.
	 * @param height
	 *            - Height to render this resource at.
	 * @param r
	 *            - Red value
	 * @param g
	 *            - Green value
	 * @param b
	 *            - Blue value
	 * @param a
	 *            - Alpha value (Transparency)
	 */
	public static void drawResourceCentered(ResourceLocation resource, int posX, int posY, int width, int height, float r, float g, float b, float a) {
		Draw.drawResourceCentered(resource, posX, posY, width, height, r, g, b, a, 1.0f, 1.0f);
	}

	/**
	 * Draw the specified ResourceLocation centered at the specified coordinates and dimensions.
	 * 
	 * @param resource
	 *            - ResourceLocation to render
	 * @param posX
	 *            - x coordinate
	 * @param posY
	 *            - y coordinate
	 * @param width
	 *            - Width to render this resource at.
	 * @param height
	 *            - Height to render this resource at.
	 * @param r
	 *            - Red value
	 * @param g
	 *            - Green value
	 * @param b
	 *            - Blue value
	 * @param a
	 *            - Alpha value (Transparency)
	 * @param u
	 *            - x coordinate of the texture offset
	 * @param v
	 *            - y coordinate of the texture offset
	 */
	public static void drawResourceCentered(ResourceLocation resource, int posX, int posY, int width, int height, float r, float g, float b, float a, float u, float v) {
		OpenGL.disableLighting();
		OpenGL.disableFog();
		Draw.bindTexture(resource);
		OpenGL.color(r, g, b, a);
		drawQuad(posX - (width / 2), posY, width, height, 0, 0, u, 0, v);
	}

	/**
	 * Draw the specified particle at the specified coordinates and dimensions.
	 * 
	 * @param particleId
	 *            - ID of the particle to draw
	 * @param x
	 *            - x coordinate
	 * @param y
	 *            - y coordinate
	 * @param width
	 *            - Width to render the particle at
	 * @param height
	 *            - Height to render the particle at
	 */
	public static void drawParticle(int index, int x, int y, int width, int height) {
		float tS = 0.0624375F;
		float u = (float) (index % 16) / 16.0F;
		float mU = u + tS;
		float v = (float) (index / 16) / 16.0F;
		float mV = v + tS;

		Draw.bindTexture(GameResources.PARTICLES);
		drawQuad(x, y, width, height, 0, u, mU, v, mV);
	}

	public static void renderItem(ItemStack stack, int x, int y) {
		OpenGL.pushMatrix();
		OpenGL.translate(0F, 0F, -100F);

		GlStateManager.pushMatrix();
		AccessHandler.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		AccessHandler.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableAlpha();
		GlStateManager.alphaFunc(516, 0.1F);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		GlStateManager.translate((float) x, (float) y, 100F);
		GlStateManager.translate(8.0F, 8.0F, 0.0F);
		GlStateManager.scale(1.0F, -1.0F, 1.0F);
		GlStateManager.scale(16.0F, 16.0F, 16.0F);

		IBakedModel ibakedmodel = AccessHandler.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(stack);
		ibakedmodel = ibakedmodel.getOverrides().handleItemState(ibakedmodel, stack, AccessHandler.getMinecraft().world, AccessHandler.getMinecraft().player);
		ibakedmodel = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(ibakedmodel, ItemCameraTransforms.TransformType.GUI, false);

		AccessHandler.getMinecraft().getRenderItem().renderItem(stack, ibakedmodel);
		GlStateManager.disableAlpha();
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableLighting();
		GlStateManager.popMatrix();
		AccessHandler.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		AccessHandler.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();

		OpenGL.popMatrix();
	}

	/**
	 * Draw the specified itemstack in a GUI with a flat icon. No 3D rendering is done.
	 * 
	 * @param stack
	 *            - The itemstack to draw
	 * @param x
	 *            - x coordinate
	 * @param y
	 *            - y corodinate
	 * @param width
	 *            - Width to render the icon at
	 * @param height
	 *            - Height to render the icon at
	 * @param u
	 *            - x coordinate of the texture offset
	 * @param v
	 *            - y coordinate of the texture offset
	 */
	public static void drawItem(ItemStack stack, int x, int y, int width, int height) {
		IBakedModel ibakedmodel = AccessHandler.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(stack);
		ibakedmodel = ibakedmodel.getOverrides().handleItemState(ibakedmodel, stack, AccessHandler.getMinecraft().world, AccessHandler.getMinecraft().player);

		GlStateManager.pushMatrix();
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableAlpha();
		GlStateManager.alphaFunc(516, 0.1F);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableLighting();
		Draw.bindTexture(getResourceLocationFullPath(ibakedmodel.getParticleTexture()));
		Draw.drawQuad(x, y, width, height);
		GlStateManager.disableAlpha();
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableLighting();
		GlStateManager.popMatrix();
	}

	/**
	 * Binds a texture to OpenGL using Minecraft's render engine.
	 * 
	 * @param resource
	 *            - The ResourceLocation of the resource to bind.
	 */
	public static void bindTexture(ResourceLocation resource) {
		AccessHandler.getMinecraft().renderEngine.bindTexture(resource);
	}

	/**
	 * Get the full path of the specified ResourceLocation. Format: domain:path/to/resource.png
	 * 
	 * @param resource
	 *            - The ResourceLocation to retrieve a path of.
	 * @return The full path of the resource, including the domain.
	 */
	public static String getResourcePath(ResourceLocation resource) {
		return String.format("%s:%s", resource.getResourceDomain(), resource.getResourcePath());
	}

	public static ResourceLocation getMissingTexture() {
		return getResourceLocationPartialPath(AccessHandler.getMinecraft().getTextureMapBlocks().getMissingSprite());
	}

	public static ResourceLocation getResourceLocationFullPath(TextureAtlasSprite sprite) {
		if (sprite != null) {
			Minecraft mc = AccessHandler.getMinecraft();
			ResourceLocation r = new ResourceLocation(sprite.getIconName());
			return new ResourceLocation(r.getResourceDomain(), String.format("%s/%s%s", new Object[] { mc.getTextureMapBlocks().getBasePath(), r.getResourcePath(), ".png" }));
		}

		return getMissingTexture();
	}

	public static ResourceLocation getResourceLocationPartialPath(TextureAtlasSprite sprite) {
		if (sprite != null) {
			ResourceLocation r = new ResourceLocation(sprite.getIconName());
			return new ResourceLocation(r.getResourceDomain(), String.format("%s", new Object[] { r.getResourcePath() }));
		}

		return getMissingTexture();
	}

	public static void lightingHelper(Entity entity, float offset) {

		int brightness = Worlds.getLightAtCoord(entity.world, entity.getPosition());
		OpenGL.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightness % 65536, brightness / 65536);
		OpenGL.color(1.0F, 1.0F, 1.0F);
	}

	public static ArrayList<String> wrapString(String string, int width) {
		ArrayList<String> strings = new ArrayList<String>();
		int stringWidth = getStringRenderWidth(string);

		if (stringWidth > width) {
			String currentLine = "";

			for (String word : string.split(" ")) {
				int wordWidth = getStringRenderWidth(word);
				int currentLineWidth = getStringRenderWidth(currentLine);

				if ((currentLineWidth + wordWidth) <= width) {
					currentLine = currentLine.isEmpty() ? word : currentLine + " " + word;
				} else {
					strings.add(currentLine);
					currentLine = word;
				}
			}

			if (!currentLine.isEmpty()) {
				strings.add(currentLine);
			}
		} else {
			strings.add(string);
		}

		return strings;
	}

}