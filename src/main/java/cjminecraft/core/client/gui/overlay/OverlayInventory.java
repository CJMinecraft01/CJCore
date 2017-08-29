package cjminecraft.core.client.gui.overlay;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import cjminecraft.core.CJCore;
import cjminecraft.core.client.gui.GuiOverlay;
import cjminecraft.core.client.gui.element.ElementBase;
import cjminecraft.core.client.gui.element.ElementItemSlot;
import cjminecraft.core.config.CJCoreConfig;
import cjminecraft.core.inventory.InventoryUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;

public class OverlayInventory extends OverlayBase {

	private List<ElementItemSlot> itemSlots = new ArrayList<ElementItemSlot>();

	private ImmutableList<ItemStack> inventory;

	private int sync = 0;

	private boolean shouldSync;
	private BlockPos pos;
	private EnumFacing side;
	private boolean stacked;
	
	private boolean useSlots = false;
	private int fromSlot;
	private int toSlot;
	
	private int excessColumns;
	private int rows;
	private int columns;

	public OverlayInventory(GuiOverlay gui, int posX, int posY) {
		super(gui, posX, posY);
		setVisible(false);
	}

	/**
	 * Set the inventory to be drawn. Typically get the inventory from
	 * {@link InventoryUtils}. If not, use
	 * {@link ImmutableList#copyOf(java.util.Collection)}
	 * 
	 * @param inventory
	 *            The inventory to be drawn
	 * @return The updated overlay
	 */
	public OverlayInventory setInventory(@Nullable ImmutableList<ItemStack> inventory) {
		if(inventory == null)
			return this;
		if (inventory.size() != 0) {
			setVisible(true);
			this.inventory = inventory;
			this.itemSlots.clear();
			this.rows = inventory.size() / CJCoreConfig.MULTIMETER_ITEM_MAX_COLUMNS;
			if (inventory.size() <= CJCoreConfig.MULTIMETER_ITEM_MAX_COLUMNS) {
				this.rows = 1;
				this.columns = inventory.size();
			} else {
				this.columns = CJCoreConfig.MULTIMETER_ITEM_MAX_COLUMNS;
			}
			this.excessColumns = inventory.size() - (this.rows * this.columns);
			for (int row = 0; row < this.rows; row++)
				for (int column = 0; column < this.columns; column++)
					this.itemSlots.add((new ElementItemSlot(this.gui, column * 18, row * 18)
							.setStack(inventory.get(Math.min(row * CJCoreConfig.MULTIMETER_ITEM_MAX_COLUMNS + column, inventory.size() - 1)))));
			for(int column = 0; column < this.excessColumns; column++)
				this.itemSlots.add((new ElementItemSlot(this.gui, column * 18, this.rows * 18)
						.setStack(inventory.get(Math.min(this.rows * CJCoreConfig.MULTIMETER_ITEM_MAX_COLUMNS + column, inventory.size() - 1)))));
		} else {
			setVisible(false);
		}
		return this;
	}

	/**
	 * States that the overlay should sync with the server
	 * 
	 * @param pos
	 *            The position of the {@link TileEntity}
	 * @param side
	 *            The side of the {@link TileEntity} the inventory is found. For
	 *            use with {@link ISidedInventory} and {@link Capability}
	 * @param stacked
	 *            Whether the inventory should be "stacked". See
	 *            {@link InventoryUtils#getInventoryStacked(TileEntity, EnumFacing)}
	 * @return The updated overlay
	 */
	public OverlayInventory shouldSync(BlockPos pos, EnumFacing side, boolean stacked) {
		this.shouldSync = true;
		this.pos = pos;
		this.side = side;
		this.stacked = stacked;
		return this;
	}

	/**
	 * States that the overlay should sync with the server
	 * 
	 * @param pos
	 *            The position of the {@link TileEntity}
	 * @param side
	 *            The side of the {@link TileEntity} the inventory is found. For
	 *            use with {@link ISidedInventory} and {@link Capability}
	 * @param stacked
	 *            Whether the inventory should be "stacked". See
	 *            {@link InventoryUtils#getInventoryStacked(TileEntity, EnumFacing)}
	 * @param fromSlot
	 *            The first slot to start getting items from
	 * @param toSlot
	 *            The last slot to get the items from
	 * @return The updated overlay
	 */
	public OverlayInventory shouldSync(BlockPos pos, EnumFacing side, boolean stacked, int fromSlot, int toSlot) {
		this.shouldSync = true;
		this.pos = pos;
		this.side = side;
		this.stacked = stacked;
		this.useSlots = true;
		this.fromSlot = fromSlot;
		this.toSlot = toSlot;
		setEnabled(true);
		return this;
	}

	/**
	 * States that the overlay shouldn't sync with the server
	 * 
	 * @return The updated overlay
	 */
	public OverlayInventory shouldntSync() {
		this.shouldSync = false;
		return this;
	}

	@Override
	public void update() {
		if (this.shouldSync) {
			this.sync++;
			this.sync %= 10;
			setInventory(InventoryUtils.getCachedInventoryData(CJCore.MODID));
			if (this.sync == 0) {
				if (this.stacked)
					if (this.useSlots)
						InventoryUtils.syncInventoryStacked(this.pos, this.side, this.fromSlot, this.toSlot,
								CJCore.MODID);
					else
						InventoryUtils.syncInventoryStacked(this.pos, this.side, CJCore.MODID);
				else if (this.useSlots)
					InventoryUtils.syncInventory(this.pos, this.side, this.fromSlot, this.toSlot, CJCore.MODID);
				else
					InventoryUtils.syncInventory(this.pos, this.side, CJCore.MODID);
			}
		}
		super.update();
	}
	
	@Override
	public void drawBackground() {
		super.drawBackground();
		GlStateManager.pushMatrix();
		GlStateManager.translate(this.posX, this.posY, 0);

		for (int i = this.itemSlots.size(); i-- > 0;) {
			ElementBase element = this.itemSlots.get(i);
			if (element.isVisible() && element.isEnabled())
				element.drawBackground(0, 0, 0);
		}
		
		if(this.fontRenderer.getStringWidth(((ElementItemSlot) this.elements.get(0)).getStack().getDisplayName()) + 20 <= this.columns * 18)
			this.fontRenderer.drawStringWithShadow(((ElementItemSlot) this.elements.get(0)).getStack().getDisplayName(), 20, this.rows * 18 + (this.excessColumns > 0 ? 18 : 0) + 4.5F, 0xFFFFFF);

		GlStateManager.popMatrix();
	}
	
	@Override
	public OverlayBase calculateSize() {
		super.calculateSize();
		for(ElementItemSlot element : this.itemSlots) {
			this.width = Math.max(this.width, element.getPosX() + element.getWidth() + 8);
			this.height = Math.max(this.height, element.getPosY() + element.getHeight() + 8);
		}
		return this;
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

	/**
	 * @return the number of rows of items
	 */
	public int getRows() {
		return this.rows;
	}
	
	/**
	 * @return the number of columns of items
	 */
	public int getColumns() {
		return this.columns;
	}
	
	/**
	 * @return the number of excess columns of items
	 */
	public int getExcessColumns() {
		return this.excessColumns;
	}
	
}
