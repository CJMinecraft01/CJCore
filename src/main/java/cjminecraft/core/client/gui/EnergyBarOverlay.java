package cjminecraft.core.client.gui;

import java.text.NumberFormat;

import cjminecraft.core.CJCore;
import cjminecraft.core.config.CJCoreConfig;
import cjminecraft.core.energy.EnergyUnits;
import cjminecraft.core.energy.EnergyUtils;
import cjminecraft.core.items.ItemMultimeter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

/**
 * The overlay for use with {@link ItemMultimeter}
 * 
 * @author CJMinecraft
 *
 */
public class EnergyBarOverlay extends EnergyBar {

	public static final int DEFAULT_OVERLAY_WIDTH = DEFAULT_WIDTH + 8;
	public static final int DEFAULT_OVERLAY_HEIGHT = DEFAULT_HEIGHT + 8;

	private int overlayWidth;
	private int overlayHeight;

	/**
	 * Initialize the (@link EnergyBar)
	 * 
	 * @param buttonId
	 *            The id of the button
	 * @param x
	 *            The x position of the (@link EnergyBar)
	 * @param y
	 *            The y position of the (@link EnergyBar)
	 * @param energy
	 *            The amount of energy in the (@link EnergyBar) (can be 0)
	 * @param capacity
	 *            The maximum amount of energy in the (@link EnergyBar) (can be
	 *            0)
	 */
	public EnergyBarOverlay(int buttonId, int x, int y, long energy, long capacity) {
		super(buttonId, x + 4, y + 4, energy, capacity);
		this.overlayWidth = DEFAULT_OVERLAY_WIDTH;
		this.overlayHeight = DEFAULT_OVERLAY_HEIGHT;
	}

	/**
	 * Initialize the {@link EnergyBar}
	 * 
	 * @param buttonId
	 *            The id of the button
	 * @param x
	 *            The x position of the {@link EnergyBar}
	 * @param y
	 *            The y position of the {@link EnergyBar}
	 * @param width
	 *            The width of the {@link EnergyBar}
	 * @param height
	 *            The height of the {@link EnergyBar}
	 * @param energy
	 *            The amount of energy in the {@link EnergyBar} (can be 0)
	 * @param capacity
	 *            The maximum amount of energy in the {@link EnergyBar} (can be
	 *            0)
	 */
	public EnergyBarOverlay(int buttonId, int x, int y, int width, int height, long energy, long capacity) {
		super(buttonId, x + 4, y + 4, width, height, energy, capacity);
		this.overlayWidth = width + 8;
		this.overlayHeight = height + 8;
	}

	/**
	 * Draws the overlay
	 */
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		super.drawButton(mc, mouseX, mouseY); // Draws the actual bar
		// Outer rim
		// Top
		this.drawHorizontalLine(xPosition, xPosition + width - 1, yPosition - 4, 0xFF000000);
		this.drawHorizontalLine(xPosition, xPosition + width - 1, yPosition - 3, 0xFFFFFFFF);
		this.drawHorizontalLine(xPosition, xPosition + width - 1, yPosition - 2, 0xFFFFFFFF);
		this.drawHorizontalLine(xPosition, xPosition + width - 1, yPosition - 1, 0xFFC6C6C6);
		// Right
		this.drawVerticalLine(xPosition + width, yPosition - 1, yPosition + height + 1, 0xFFC6C6C6);
		this.drawVerticalLine(xPosition + width + 1, yPosition - 1, yPosition + height + 1, 0xFF555555);
		this.drawVerticalLine(xPosition + width + 2, yPosition - 1, yPosition + height + 1, 0xFF555555);
		this.drawVerticalLine(xPosition + width + 3, yPosition - 1, yPosition + height + 1, 0xFF000000);
		// Bottom
		this.drawHorizontalLine(xPosition, xPosition + width - 1, yPosition + height + 1, 0xFFC6C6C6);
		this.drawHorizontalLine(xPosition, xPosition + width - 1, yPosition + height + 2, 0xFF555555);
		this.drawHorizontalLine(xPosition, xPosition + width - 1, yPosition + height + 3, 0xFF555555);
		this.drawHorizontalLine(xPosition, xPosition + width - 1, yPosition + height + 4, 0xFF000000);
		// Left
		this.drawVerticalLine(xPosition - 1, yPosition - 1, yPosition + height + 1, 0xFFC6C6C6);
		this.drawVerticalLine(xPosition - 2, yPosition - 1, yPosition + height + 1, 0xFFFFFFFF);
		this.drawVerticalLine(xPosition - 3, yPosition - 1, yPosition + height + 1, 0xFFFFFFFF);
		this.drawVerticalLine(xPosition - 4, yPosition - 1, yPosition + height + 1, 0xFF000000);

		// Allows alpha not to be drawn as black but transparent
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO);
		// Top left
		mc.getTextureManager().bindTexture(DEFAULT_TEXTURE);
		this.drawTexturedModalRect(xPosition - 4, yPosition - 4, 0, 85, 4, 4);
		// Top right
		mc.getTextureManager().bindTexture(DEFAULT_TEXTURE);
		this.drawTexturedModalRect(xPosition + width, yPosition - 4, 4, 85, 4, 4);
		// Bottom Left
		mc.getTextureManager().bindTexture(DEFAULT_TEXTURE);
		this.drawTexturedModalRect(xPosition - 4, yPosition + height + 1, 0, 89, 4, 4);
		// Bottom Right
		mc.getTextureManager().bindTexture(DEFAULT_TEXTURE);
		this.drawTexturedModalRect(xPosition + width, yPosition + height + 1, 4, 89, 4, 4);
		GlStateManager.disableBlend();

		// Shows the energy inside of the {@link TileEntity}
		if (!CJCoreConfig.MULTIMETER_SIMPLIFY_ENERGY) {
			mc.fontRendererObj.drawStringWithShadow(NumberFormat.getNumberInstance().format(energy) + " "
					+ CJCoreConfig.DEFAULT_ENERGY_UNIT.getSuffix()
					+ (CJCoreConfig.MULTIMETER_SHOW_CAPACITY ? " / " + NumberFormat.getNumberInstance().format(capacity)
							+ " " + CJCoreConfig.DEFAULT_ENERGY_UNIT.getSuffix() : ""),
					xPosition + width + 6, yPosition + height - 7, 0xFFFFFF);
		} else {
			mc.fontRendererObj.drawStringWithShadow(EnergyUtils.getEnergyAsString(energy,
					CJCoreConfig.DEFAULT_ENERGY_UNIT)
					+ (CJCoreConfig.MULTIMETER_SHOW_CAPACITY
							? " / " + EnergyUtils.getEnergyAsString(capacity, CJCoreConfig.DEFAULT_ENERGY_UNIT) : ""),
					xPosition + width + 6, yPosition + height - 7, 0xFFFFFF);
		}
	}

}
