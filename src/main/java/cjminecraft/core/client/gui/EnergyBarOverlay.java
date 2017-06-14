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
		super(buttonId, x, y, energy, capacity);
		this.overlayWidth = DEFAULT_OVERLAY_WIDTH;
		this.overlayHeight = DEFAULT_OVERLAY_HEIGHT;
		this.xPosition += 4;
		this.yPosition += 4;
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
	 * @param energy
	 *            The amount of energy in the {@link EnergyBar} (can be 0)
	 * @param capacity
	 *            The maximum amount of energy in the {@link EnergyBar} (can be
	 *            0)
	 * @param texture
	 *            The texture for the {@link EnergyBar}
	 * @param width
	 *            The width of the {@link EnergyBar} in the texture
	 * @param height
	 *            The height of the {@link EnergyBar} in the texture
	 * @param textureX
	 *            The x position of the {@link EnergyBar} in the texture
	 * @param textureY
	 *            The y position of the {@link EnergyBar} in the texture
	 */
	public EnergyBarOverlay(int buttonId, int x, int y, long energy, long capacity, ResourceLocation texture, int width,
			int height, int textureX, int textureY) {
		super(buttonId, x, y, energy, capacity, texture, width, height, textureX, textureY);
		this.width -= 8;
		this.height -= 8;
		this.overlayWidth = width;
		this.overlayHeight = height;
	}

	/**
	 * Draws the overlay
	 */
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		super.drawButton(mc, mouseX, mouseY); // Draws the actual bar
		// Allows alpha not to be drawn as black but transparent
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO);
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		mc.getTextureManager().bindTexture(DEFAULT_TEXTURE);
		this.drawTexturedModalRect(xPosition - 4, yPosition - 4, width, 0, overlayWidth, overlayHeight);
		GlStateManager.disableBlend();
		// Shows the energy inside of the {@link TileEntity}
		if (!CJCoreConfig.MULTIMETER_SIMPLIFY_ENERGY) {
			mc.fontRendererObj.drawStringWithShadow(NumberFormat.getNumberInstance().format(energy) + " "
					+ CJCoreConfig.DEFAULT_ENERGY_UNIT.getSuffix()
					+ (CJCoreConfig.MULTIMETER_SHOW_CAPACITY ? " / " + NumberFormat.getNumberInstance().format(capacity)
							+ " " + CJCoreConfig.DEFAULT_ENERGY_UNIT.getSuffix() : ""),
					xPosition + width + 6, yPosition + height - 7, 0xFFFFFF);
		} else {
			mc.fontRendererObj.drawStringWithShadow(
					EnergyUtils.getEnergyAsString(energy, CJCoreConfig.DEFAULT_ENERGY_UNIT)
							+ (CJCoreConfig.MULTIMETER_SHOW_CAPACITY
									? " / " + EnergyUtils.getEnergyAsString(capacity, CJCoreConfig.DEFAULT_ENERGY_UNIT) : ""),
					xPosition + width + 6, yPosition + height - 7, 0xFFFFFF);
		}
	}

}
