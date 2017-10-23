package cjminecraft.core.client.gui.element;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;

import javax.annotation.Nullable;

import cjminecraft.core.CJCore;
import cjminecraft.core.client.gui.GuiCore;
import cjminecraft.core.client.gui.ISpecialOverlayElement;
import cjminecraft.core.config.CJCoreConfig;
import cjminecraft.core.energy.EnergyData;
import cjminecraft.core.energy.EnergyUnits.EnergyUnit;
import cjminecraft.core.energy.compat.forge.CustomForgeEnergyStorage;
import cjminecraft.core.energy.EnergyUtils;
import cjminecraft.core.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.capabilities.Capability;

/**
 * A simple yet effective energy bar which helps show energy. Can be synced with
 * a {@link TileEntity}
 * 
 * @author CJMinecraft
 *
 */
public class ElementEnergyBar extends ElementTexture implements ISpecialOverlayElement {

	public static final int DEFAULT_WIDTH = 18;
	public static final int DEFAULT_HEIGHT = 84;

	protected long energy = 0;
	protected long capacity = 0;

	private BlockPos pos;
	private EnumFacing side;
	private boolean shouldSync = false;
	private int sync = 0;

	/**
	 * A simple yet effective energy bar which helps show energy. Can be synced
	 * with a {@link TileEntity}
	 * 
	 * @param gui
	 *            The parent gui
	 * @param posX
	 *            The x position relative to the gui's x position
	 * @param posY
	 *            The y position relative to the gui's y position
	 */
	public ElementEnergyBar(GuiCore gui, int posX, int posY) {
		this(gui, posX, posY, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}

	/**
	 * A simple yet effective energy bar which helps show energy. Can be synced
	 * with a {@link TileEntity}
	 * 
	 * @param gui
	 *            The parent gui
	 * @param posX
	 *            The x position relative to the gui's x position
	 * @param posY
	 *            The y position relative to the gui's y position
	 * @param width
	 *            The width of the energy bar
	 * @param height
	 *            The height of the energy bar
	 */
	public ElementEnergyBar(GuiCore gui, int posX, int posY, int width, int height) {
		super(gui, posX, posY, Math.min(width, DEFAULT_WIDTH), Math.min(height, DEFAULT_HEIGHT));
		setTexture(WIDGETS_TEXTURE, 256, 256);
		setTextureUV(0, 0);
	}

	/**
	 * Set the energy inside of this element
	 * 
	 * @param energy
	 *            The energy to set
	 * @param capacity
	 *            The capacity
	 * @param unit
	 *            The {@link EnergyUnit} the energy and capacity are in
	 * @return The updated element
	 */
	public ElementEnergyBar setEnergy(long energy, long capacity, EnergyUnit unit) {
		this.energy = EnergyUtils.convertEnergy(unit, CJCoreConfig.DEFAULT_ENERGY_UNIT, energy);
		this.capacity = EnergyUtils.convertEnergy(unit, CJCoreConfig.DEFAULT_ENERGY_UNIT, capacity);
		return this;
	}

	/**
	 * Set the energy inside of this element
	 * 
	 * @param data
	 *            The {@link EnergyData} holding all of the energy data.
	 *            Typically used by
	 *            {@link EnergyUtils#getCachedEnergyData(String)} and
	 *            {@link EnergyUtils#getCachedEnergyData(String, String)}
	 * @return The updated element
	 */
	public ElementEnergyBar setEnergy(EnergyData data) {
		if (data == null)
			return this;
		return setEnergy(data.getEnergy(), data.getCapacity(), CJCoreConfig.DEFAULT_ENERGY_UNIT);
	}

	/**
	 * States that the energy bar should sync with the server
	 * 
	 * @param pos
	 *            The position of the {@link TileEntity}
	 * @param side
	 *            The side of the {@link TileEntity} the energy is found. For
	 *            use with {@link Capability}
	 * @return The updated element
	 */
	public ElementEnergyBar shouldSync(BlockPos pos, EnumFacing side) {
		this.shouldSync = true;
		this.pos = pos;
		this.side = side;
		return this;
	}

	/**
	 * States that the energy bar shouldn't sync with the server
	 * 
	 * @return The updated element
	 */
	public ElementEnergyBar shouldntSync() {
		this.shouldSync = false;
		this.pos = BlockPos.ORIGIN;
		this.side = null;
		return this;
	}

	@Override
	public ElementBase setSize(int width, int height) {
		return super.setSize(Math.min(width, DEFAULT_WIDTH), Math.min(height, DEFAULT_HEIGHT));
	}

	/**
	 * Get the height of the energy bar based on the energy and capacity
	 * 
	 * @return The height of the energy bar based on the energy and capacity
	 */
	public int getEnergyBarHeight() {
		return (int) ((this.capacity != 0 && this.energy != 0) ? (this.energy * this.height) / this.capacity : 0);
	}

	@Override
	public void drawBackground(int mouseX, int mouseY, float gameTicks) {
		// Actual energy bar
		this.gui.bindTexture(this.texture);
		float[] colour = CJCoreConfig.DEFAULT_ENERGY_UNIT.getColour();
		GlStateManager.color(colour[0], colour[1], colour[2]);
		drawSizedTexturedModalRectWithBorder(this.posX, this.posY,
				this.textureU + (Math.abs(DEFAULT_WIDTH - this.width) / 2), this.textureV, this.width, this.height,
				this.textureWidth, this.textureHeight);

		// Overlay which shows the actual energy
		this.gui.bindTexture(this.texture);
		RenderUtils.resetColour();
		drawSizedTexturedModalRect(this.posX + 1, this.posY + 1,
				this.textureU + (Math.abs(DEFAULT_WIDTH - this.width) / 2), this.textureV, this.width - 2,
				this.height - getEnergyBarHeight() - 2, this.textureWidth, this.textureHeight);
	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {

	}
	
	@Override
	public void drawSpecialLayer() {
		
	}
	
	@Override
	public void addOverlayText(List<String> text) {
		if (CJCoreConfig.ENERGY_BAR_SIMPLIFY_ENERGY) {
			text.add(EnergyUtils.getEnergyAsString(this.energy, CJCoreConfig.DEFAULT_ENERGY_UNIT)
					+ (CJCoreConfig.ENERGY_BAR_SHOW_CAPACITY
							? " / " + EnergyUtils.getEnergyAsString(this.capacity, CJCoreConfig.DEFAULT_ENERGY_UNIT)
							: ""));
		} else {
			text.add(NumberFormat.getInstance().format(this.energy) + " "
					+ CJCoreConfig.DEFAULT_ENERGY_UNIT.getSuffix()
					+ (CJCoreConfig.ENERGY_BAR_SHOW_CAPACITY ? " / " + NumberFormat.getInstance().format(this.capacity)
							+ " " + CJCoreConfig.DEFAULT_ENERGY_UNIT.getSuffix() : ""));
		}
	}

	@Override
	public void update() {
		if (this.shouldSync) {
			setEnergy(EnergyUtils.getCachedEnergyData(CJCore.MODID));
			this.sync++;
			this.sync %= 10;
			if (this.sync == 0)
				EnergyUtils.syncEnergyData(CJCoreConfig.DEFAULT_ENERGY_UNIT, this.pos, this.side, CJCore.MODID);
		}
	}

	@Override
	public boolean onMousePressed(int mouseX, int mouseY, int mouseButton) throws IOException {
		if (intersectsWith(mouseX, mouseY)) {
			GuiCore.playClickSound(1.0F, 1.0F);
			EnergyUnit toBe = CJCoreConfig.DEFAULT_ENERGY_UNIT.cycleUnit();
			this.energy = EnergyUtils.convertEnergy(CJCoreConfig.DEFAULT_ENERGY_UNIT, toBe, energy);
			this.capacity = EnergyUtils.convertEnergy(CJCoreConfig.DEFAULT_ENERGY_UNIT, toBe, capacity);
			CJCoreConfig.DEFAULT_ENERGY_UNIT = toBe;
			CJCoreConfig.syncFromFields();
			return true;
		}
		return false;
	}

	@Override
	public void addTooltip(List<String> tooltip) {
		if (CJCoreConfig.ENERGY_BAR_SIMPLIFY_ENERGY) {
			tooltip.add(EnergyUtils.getEnergyAsString(this.energy, CJCoreConfig.DEFAULT_ENERGY_UNIT)
					+ (CJCoreConfig.ENERGY_BAR_SHOW_CAPACITY
							? " / " + EnergyUtils.getEnergyAsString(this.capacity, CJCoreConfig.DEFAULT_ENERGY_UNIT)
							: ""));
		} else {
			tooltip.add(NumberFormat.getInstance().format(this.energy) + " "
					+ CJCoreConfig.DEFAULT_ENERGY_UNIT.getSuffix()
					+ (CJCoreConfig.ENERGY_BAR_SHOW_CAPACITY ? " / " + NumberFormat.getInstance().format(this.capacity)
							+ " " + CJCoreConfig.DEFAULT_ENERGY_UNIT.getSuffix() : ""));
		}
		int percentageFull = (int) ((double) this.energy / (double) this.capacity * 100.0D);
		tooltip.add((percentageFull > 50 ? TextFormatting.GREEN
				: percentageFull <= 50 && percentageFull >= 15 ? TextFormatting.YELLOW : TextFormatting.RED)
				+ I18n.format("energy.percentage", String.valueOf(percentageFull) + "%" + TextFormatting.RESET));
	}

	/**
	 * @return the pos in which the energy bar is syncing with if provided
	 */
	@Nullable
	public BlockPos getPos() {
		return this.pos;
	}
	
	/**
	 * @return the side of which the energy bar is syncing with if provided
	 */
	@Nullable
	public EnumFacing getSide() {
		return this.side;
	}

}
