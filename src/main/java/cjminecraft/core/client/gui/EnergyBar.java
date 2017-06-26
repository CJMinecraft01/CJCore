package cjminecraft.core.client.gui;

import cjminecraft.core.CJCore;
import cjminecraft.core.config.CJCoreConfig;
import cjminecraft.core.energy.EnergyData;
import cjminecraft.core.energy.EnergyUnits.EnergyUnit;
import cjminecraft.core.energy.EnergyUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;

/**
 * Draw a simple {@link EnergyBar} which represents the energy inside of a
 * {@link TileEntity}
 * 
 * @author CJMinecraft
 *
 */
public class EnergyBar extends GuiButton {
	
	public static final ResourceLocation DEFAULT_TEXTURE = new ResourceLocation(CJCore.MODID,
			"textures/gui/energy_bar.png");
	public static final int DEFAULT_WIDTH = 18;
	public static final int DEFAULT_HEIGHT = 84;

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
		this.width = DEFAULT_WIDTH;
		this.height = DEFAULT_HEIGHT;
		this.textureX = 0;
		this.textureY = 0;
		this.energy = energy;
		this.capacity = capacity;
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
	public EnergyBar(int buttonId, int x, int y, int width, int height, long energy, long capacity) {
		super(buttonId, x, y, "");
		this.width = width >= DEFAULT_WIDTH ? DEFAULT_WIDTH : width;
		this.height = height >= DEFAULT_HEIGHT ? DEFAULT_HEIGHT : height;
		this.textureX = 0;
		this.textureY = 0;
		this.energy = energy;
		this.capacity = capacity;
	}

	/**
	 * Draw the button on the screen
	 */
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width
				&& mouseY < this.yPosition + this.height;

		// Outer rim of bar
		this.drawVerticalLine(xPosition, yPosition - 1, yPosition + height, 0xFF373737);
		this.drawHorizontalLine(xPosition + 1, xPosition + width - 2, yPosition, 0xFF373737);
		this.drawHorizontalLine(xPosition + width - 1, xPosition + width - 1, yPosition, 0xFF8B8B8B);
		this.drawHorizontalLine(xPosition, xPosition, yPosition + height, 0xFF8B8B8B);
		this.drawVerticalLine(xPosition + width - 1, yPosition, yPosition + height, 0xFFFFFFFF);
		this.drawHorizontalLine(xPosition + 1, xPosition + width - 1, yPosition + height, 0xFFE2E2E2);

		// Actual background energy bar
		mc.getTextureManager().bindTexture(DEFAULT_TEXTURE);
		int[] colour = CJCoreConfig.DEFAULT_ENERGY_UNIT.getColour();
		GlStateManager.color(colour[0], colour[1], colour[2]);
		this.drawTexturedModalRect(xPosition + 1, yPosition + 1, textureX + 1 + (Math.abs(DEFAULT_WIDTH - width) / 2),
				textureY + 1, width - 2, height - 1);

		// The overlay to show the amount of energy in the {@link TileEntity}
		mc.getTextureManager().bindTexture(DEFAULT_TEXTURE);
		GlStateManager.color(1.0F, 1.0F, 1.0F);
		this.drawTexturedModalRect(xPosition + 1, yPosition + 1, textureX + 1 + (Math.abs(DEFAULT_WIDTH - width) / 2),
				textureY + 1, width - 2, height - getEnergyBarHeight() - 1);
		
		updateEnergyBar(EnergyUtils.getCachedEnergyData(CJCore.MODID));
	}

	/**
	 * Use this in the action performed to be able to cycle the
	 * {@link EnergyUnit} when you click the {@link EnergyBar}
	 * 
	 * @param mc
	 *            Instance of {@link Minecraft} used for the press sound
	 * @param te
	 *            The tile entity which is holding the block's energy
	 */
	public void actionPerformed(Minecraft mc, TileEntity te) {
		ItemStack selectedItem = mc.player.inventory.getItemStack();
		if (selectedItem != ItemStack.EMPTY && !selectedItem.isEmpty()) {
			if (EnergyUtils.hasSupport(selectedItem, null)) {
				EnergyUtils.giveEnergy(selectedItem, EnergyUtils.takeEnergy(te,
						Math.abs(EnergyUtils.getCapacity(selectedItem, null, CJCoreConfig.DEFAULT_ENERGY_UNIT)
								- EnergyUtils.getEnergyStored(selectedItem, null, CJCoreConfig.DEFAULT_ENERGY_UNIT)),
						CJCoreConfig.DEFAULT_ENERGY_UNIT, false, null), CJCoreConfig.DEFAULT_ENERGY_UNIT, false, null);
			}
		} else {
			this.playPressSound(mc.getSoundHandler());
			EnergyUnit toBe = CJCoreConfig.DEFAULT_ENERGY_UNIT.cycleUnit();
			this.energy = EnergyUtils.convertEnergy(CJCoreConfig.DEFAULT_ENERGY_UNIT, toBe, energy);
			this.capacity = EnergyUtils.convertEnergy(CJCoreConfig.DEFAULT_ENERGY_UNIT, toBe, capacity);
			CJCoreConfig.DEFAULT_ENERGY_UNIT = toBe;
			CJCoreConfig.syncFromFields();
		}
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
	
	/**
	 * Update the energy inside of this {@link EnergyBar} from the {@link TileEntity} at the given {@link BlockPos}
	 * @param pos The position of the {@link TileEntity}
	 * @param side The side of the {@link TileEntity} to get the energy from. For use with {@link Capability}s
	 */
	public void syncData(BlockPos pos, EnumFacing side) {
		EnergyUtils.syncEnergyData(CJCoreConfig.DEFAULT_ENERGY_UNIT, pos, side, CJCore.MODID);
	}

}
