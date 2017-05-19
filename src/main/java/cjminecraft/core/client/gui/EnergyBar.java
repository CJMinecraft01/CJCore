package cjminecraft.core.client.gui;

import cjminecraft.core.CJCore;
import cjminecraft.core.config.CJCoreConfig;
import cjminecraft.core.energy.EnergyUtils;
import cjminecraft.core.energy.EnergyData;
import cjminecraft.core.energy.EnergyUnits.EnergyUnit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

/**
 * Draw a simple {@link EnergyBar} which represents the energy inside of a
 * {@link TileEntity}
 * 
 * @author CJMinecraft
 *
 */
public class EnergyBar extends GuiButton {

	/**
	 * The default texture of the {@link EnergyBar}
	 */
	public ResourceLocation texture;

	private int textureX;
	private int textureY;

	/**
	 * Store the energy and capacity in the {@link EnergyBar} - used for
	 * rendering
	 */
	public long energy;
	public long capacity;

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
	 */
	public EnergyBar(int buttonId, int x, int y, long energy, long capacity) {
		super(buttonId, x, y, "");
		this.width = 18;
		this.height = 85;
		this.textureX = 0;
		this.textureY = 0;
		this.energy = energy;
		this.capacity = capacity;
		this.texture = new ResourceLocation(CJCore.MODID, "textures/gui/energy_bar.png");
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
	public EnergyBar(int buttonId, int x, int y, long energy, long capacity, ResourceLocation texture, int width,
			int height, int textureX, int textureY) {
		super(buttonId, x, y, "");
		this.energy = energy;
		this.capacity = capacity;
		this.texture = texture;
		this.width = width;
		this.height = height;
		this.textureX = textureX;
		this.textureY = textureY;
	}

	/**
	 * Draw the button on the screen
	 */
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width
				&& mouseY < this.yPosition + this.height;
		mc.getTextureManager().bindTexture(texture);
		GlStateManager.color(1.0F, 1.0F, 1.0F);
		this.drawTexturedModalRect(xPosition, yPosition, textureX, textureY, width, height);

		mc.getTextureManager().bindTexture(texture);
		int[] colour = CJCoreConfig.DEFAULT_ENERGY_UNIT.getColour();
		GlStateManager.color(colour[0], colour[1], colour[2]);
		this.drawTexturedModalRect(xPosition + 1, yPosition + 1, textureX + 1, textureY + 1, width - 2, height - 2);

		mc.getTextureManager().bindTexture(texture);
		GlStateManager.color(1.0F, 1.0F, 1.0F);
		this.drawTexturedModalRect(xPosition + 1, yPosition + 1, textureX + 1, textureY + 1, width - 1, height - getEnergyBarHeight() - 1);
	}

	/**
	 * Use this in the action performed to be able to cycle the
	 * {@link EnergyUnit} when you click the {@link EnergyBar}
	 * 
	 * @param mc
	 *            Instance of {@link Minecraft} used for the press sound
	 */
	public void actionPerformed(Minecraft mc) {
		this.playPressSound(mc.getSoundHandler());
		EnergyUnit toBe = CJCoreConfig.DEFAULT_ENERGY_UNIT.cycleUnit();
		this.energy = EnergyUtils.convertEnergy(CJCoreConfig.DEFAULT_ENERGY_UNIT, toBe, energy);
		this.capacity = EnergyUtils.convertEnergy(CJCoreConfig.DEFAULT_ENERGY_UNIT, toBe, capacity);
		CJCoreConfig.DEFAULT_ENERGY_UNIT = toBe;
		CJCoreConfig.syncFromFields();
	}

	/**
	 * Update the values inside of the {@link EnergyBar} Should update every
	 * time the game is rendered
	 * 
	 * @param energy
	 *            The energy in the {@link EnergyBar}
	 * @param capacity
	 *            The maximum amount of energy in the {@link EnergyBar}
	 */
	public void updateEnergyBar(long energy, long capacity) {
		this.energy = energy;
		this.capacity = capacity;
	}

	/**
	 * Update the values inside of the {@link EnergyBar} Should update every
	 * time the game is rendered
	 * 
	 * @param data
	 *            Holds the data regarding the energy inside
	 */
	public void updateEnergyBar(EnergyData data) {
		if (data == null)
			return;
		this.energy = data.getEnergy();
		this.capacity = data.getCapacity();
	}

	/**
	 * Calculates the height of the {@link EnergyBar} from the {@link #capacity}
	 * {@link #energy}
	 * 
	 * @return the height of the energy bar
	 */
	private int getEnergyBarHeight() {
		return (int) ((this.capacity != 0 && this.energy != 0) ? (this.energy * this.height) / this.capacity : 0);
	}

}
