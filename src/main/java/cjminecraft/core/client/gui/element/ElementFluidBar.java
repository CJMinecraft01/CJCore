package cjminecraft.core.client.gui.element;

import java.util.List;

import cjminecraft.core.CJCore;
import cjminecraft.core.client.gui.GuiCore;
import cjminecraft.core.client.gui.ISpecialOverlayElement;
import cjminecraft.core.fluid.FluidUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidTankInfo;

public class ElementFluidBar extends ElementBase implements ISpecialOverlayElement {
	
	public static final int DEFAULT_WIDTH = 18;
	public static final int DEFAULT_HEIGHT = 80;
	
	protected int tankIndex;
	protected FluidTankInfo fluidTank;
	
	private BlockPos pos;
	private EnumFacing side;
	private boolean shouldSync = false;
	private int sync = 0;
	
	public ElementFluidBar(GuiCore gui, int posX, int posY, int tankIndex) {
		this(gui, posX, posY, DEFAULT_WIDTH, DEFAULT_HEIGHT, tankIndex);
	}

	public ElementFluidBar(GuiCore gui, int posX, int posY, int width, int height, int tankIndex) {
		super(gui, posX, posY, Math.min(width, DEFAULT_WIDTH), Math.min(height, DEFAULT_HEIGHT));
		this.tankIndex = tankIndex;
	}
	
	public ElementFluidBar setFluidTankInfo(FluidTankInfo fluidTankInfo) {
		if(fluidTankInfo == null)
			return this;
		this.fluidTank = fluidTankInfo;
		return this;
	}
	
	public ElementFluidBar shouldSync(BlockPos pos, EnumFacing side) {
		this.shouldSync = true;
		this.pos = pos;
		this.side = side;
		return this;
	}

	public ElementFluidBar shouldntSync() {
		this.shouldSync = false;
		this.pos = BlockPos.ORIGIN;
		this.side = null;
		return this;
	}
	
	public int getFluidBarHeight() {
		if(this.fluidTank == null || this.fluidTank.fluid == null)
			return 0;
		return (int) ((this.fluidTank.capacity != 0 && this.fluidTank.fluid.amount != 0) ? (this.fluidTank.fluid.amount * (this.height - 1)) / this.fluidTank.capacity : 0);
	}

	@Override
	public void drawBackground(int mouseX, int mouseY, float gameTicks) {
		if(this.fluidTank != null && this.fluidTank.fluid != null) {
			this.gui.drawFluidWithBorder(this.posX, this.posY, this.fluidTank.fluid, this.width, this.height);
			this.gui.drawSizedRect(this.posX + 1, this.posY + 1, this.posX + this.width - 1, this.posY + this.height - getFluidBarHeight(), 0xFF8B8B8B);
		} else {
			this.gui.drawSizedRectWithBorder(this.posX, this.posY, this.posX + this.width, this.posY + this.height - getFluidBarHeight() - 1, 0xFF8B8B8B);
		}
		
		// Draw Scale
		for(int row = 1; row < (this.height - 2) / 3; row++) {
			if (row % 5 == 0)
				this.gui.drawHorizontalLine(this.posX + 1, this.posX + this.width - 2, this.posY + this.height - 1 - (row * 3), 0x7F0000FF);
			else
				this.gui.drawHorizontalLine(this.posX + 1, this.posX + 7, this.posY + this.height - 1 - (row * 3), 0x7F0000FF);
		}
	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {
	}
	
	@Override
	public void update() {
		this.height = 78;
		if(this.shouldSync) {
			setFluidTankInfo(FluidUtils.getCachedFluidData(CJCore.MODID));
			this.sync++;
			this.sync %= 10;
			if(this.sync == 0)
				FluidUtils.syncFluidData(this.tankIndex, this.pos, this.side, CJCore.MODID);
		}
	}
	
	@Override
	public void addTooltip(List<String> tooltip) {
		if(this.fluidTank != null)
			tooltip.add(FluidUtils.getFluidTankInfoToString(this.fluidTank));
	}
	
	@Override
	public void addOverlayText(List<String> text) {
		if(this.fluidTank != null)
			text.add(FluidUtils.getFluidTankInfoToString(this.fluidTank));
	}
	
	public BlockPos getPos() {
		return pos;
	}
	
	public EnumFacing getSide() {
		return side;
	}
	
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
