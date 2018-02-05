package cjminecraft.core.client.gui.element;

import java.util.List;

import javax.annotation.Nullable;

import cjminecraft.core.CJCore;
import cjminecraft.core.client.gui.GuiCore;
import cjminecraft.core.client.gui.ISpecialOverlayElement;
import cjminecraft.core.fluid.FluidUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidTankInfo;

/**
 * A neat little fluid bar to display all the fluids you will ever need. Can be
 * synced with a {@link TileEntity}
 * 
 * @author CJMinecraft
 */
public class ElementFluidBar extends ElementBase implements ISpecialOverlayElement {

	public static final int DEFAULT_WIDTH = 18;
	public static final int DEFAULT_HEIGHT = 83;

	protected int tankIndex;
	protected FluidTankInfo fluidTank;

	private BlockPos pos;
	private EnumFacing side;
	private boolean shouldSync = false;
	private int sync = 0;

	/**
	 * A neat little fluid bar to display all the fluids you will ever need. Can
	 * be synced with a {@link TileEntity}
	 * 
	 * @param gui
	 *            The parent gui
	 * @param posX
	 *            The x position relative to the gui's x position
	 * @param posY
	 *            The y position relative to the gui's y position
	 * @param tankIndex
	 *            The index of the tank (for blocks which have multiple tanks)
	 */
	public ElementFluidBar(GuiCore gui, int posX, int posY, int tankIndex) {
		this(gui, posX, posY, DEFAULT_WIDTH, DEFAULT_HEIGHT, tankIndex);
	}

	/**
	 * A neat little fluid bar to display all the fluids you will ever need. Can
	 * be synced with a {@link TileEntity}
	 * 
	 * @param gui
	 *            The parent gui
	 * @param posX
	 *            The x position relative to the gui's x position
	 * @param posY
	 *            The y position relative to the gui's y position
	 * @param width
	 *            The width of the fluid bar
	 * @param height
	 *            The height of the fluid bar
	 * @param tankIndex
	 *            The index of the tank (for blocks which have multiple tanks)
	 */
	public ElementFluidBar(GuiCore gui, int posX, int posY, int width, int height, int tankIndex) {
		super(gui, posX, posY, Math.min(width, DEFAULT_WIDTH), Math.min(height, DEFAULT_HEIGHT));
		this.tankIndex = tankIndex;
	}

	/**
	 * Sets the fluid information of this element
	 * 
	 * @param fluidTankInfo
	 *            The fluid information
	 * @return The updated element
	 */
	public ElementFluidBar setFluidTankInfo(FluidTankInfo fluidTankInfo) {
		if (fluidTankInfo == null)
			return this;
		this.fluidTank = fluidTankInfo;
		return this;
	}

	/**
	 * States that the fluid bar should sync with the server
	 * 
	 * @param pos
	 *            The position of the {@link TileEntity}
	 * @param side
	 *            The side of the {@link TileEntity} where the fluid is found.
	 *            For use with {@link Capability}
	 * @return The updated element
	 */
	public ElementFluidBar shouldSync(BlockPos pos, EnumFacing side) {
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
	public ElementFluidBar shouldntSync() {
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
	 * Get the height of the fluid bar (adjusted to the contents)
	 * 
	 * @return the height of the fluid bar
	 */
	public int getFluidBarHeight() {
		if (this.fluidTank == null || this.fluidTank.fluid == null)
			return 0;
		return (int) ((this.fluidTank.capacity != 0 && this.fluidTank.fluid.amount != 0)
				? (this.fluidTank.fluid.amount * (this.height - 1)) / this.fluidTank.capacity : 0);
	}

	@Override
	public void drawBackground(int mouseX, int mouseY, float gameTicks) {
		if (this.fluidTank != null && this.fluidTank.fluid != null) {
			this.gui.drawFluidWithBorder(this.posX, this.posY, this.fluidTank.fluid, this.width, this.height);
			this.gui.drawSizedRect(this.posX + 1, this.posY + 1, this.posX + this.width - 1,
					this.posY + this.height - getFluidBarHeight(), 0xFF8B8B8B);
		} else {
			this.gui.drawSizedRectWithBorder(this.posX, this.posY, this.posX + this.width,
					this.posY + this.height - getFluidBarHeight() - 1, 0xFF8B8B8B);
		}

		// Draw Scale
		for (int row = 1; row < (this.height - 2) / 3; row++) {
			if (row % 5 == 0)
				this.gui.drawHorizontalLine(this.posX + 1, this.posX + this.width - 2,
						this.posY + this.height - 1 - (row * 3), 0x7F0000FF);
			else
				this.gui.drawHorizontalLine(this.posX + 1, this.posX + 7, this.posY + this.height - 1 - (row * 3),
						0x7F0000FF);
		}
	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {
	}

	@Override
	public void update() {
		this.height = 78;
		if (this.shouldSync) {
			setFluidTankInfo(FluidUtils.getCachedFluidData(CJCore.MODID, this.getClass().getName() + this.tankIndex));
			this.sync++;
			this.sync %= 10;
			if (this.sync == 0)
				FluidUtils.syncFluidData(this.tankIndex, this.pos, this.side, CJCore.MODID, this.getClass().getName() + this.tankIndex);
		}
	}

	@Override
	public void addTooltip(List<String> tooltip) {
		if (this.fluidTank != null)
			tooltip.add(FluidUtils.getFluidTankInfoToString(this.fluidTank));
	}

	@Override
	public void addOverlayText(List<String> text) {
		if (this.fluidTank != null)
			text.add(FluidUtils.getFluidTankInfoToString(this.fluidTank));
	}

	/**
	 * @return the pos in which the fluid bar is syncing with if provided
	 */
	@Nullable
	public BlockPos getPos() {
		return pos;
	}

	/**
	 * @return the side of which the fluid bar is syncing with if provided
	 */
	@Nullable
	public EnumFacing getSide() {
		return side;
	}

	/**
	 * @return whether the fluid bar should be syncing with the server
	 */
	public boolean shouldSync() {
		return this.shouldSync;
	}

	public FluidTankInfo getFluidTank() {
		return fluidTank;
	}

	public int getTankIndex() {
		return tankIndex;
	}

}
