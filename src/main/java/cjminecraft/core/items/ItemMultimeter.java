package cjminecraft.core.items;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import cjminecraft.core.CJCore;
import cjminecraft.core.client.gui.GuiOverlay;
import cjminecraft.core.client.gui.element.ElementEnergyBar;
import cjminecraft.core.client.gui.element.ElementItemSlot;
import cjminecraft.core.client.gui.overlay.OverlayBase;
import cjminecraft.core.client.gui.overlay.OverlayInventory;
import cjminecraft.core.config.CJCoreConfig;
import cjminecraft.core.energy.EnergyUtils;
import cjminecraft.core.init.CJCoreItems;
import cjminecraft.core.inventory.InventoryUtils;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * The item which shows how much energy is in any {@link TileEntity}
 * 
 * @author CJMinecraft
 *
 */
public class ItemMultimeter extends Item {

	/**
	 * Initialize the item with its unlocalized name and registry name
	 * 
	 * @param unlocalizedName
	 *            The unlocalized name of the item
	 */
	public ItemMultimeter(String unlocalizedName) {
		this.setUnlocalizedName(unlocalizedName);
		this.setRegistryName(new ResourceLocation(CJCore.MODID, unlocalizedName));
		this.setMaxStackSize(1);
		this.setHasSubtypes(true);
		this.setCreativeTab(CreativeTabs.REDSTONE);
	}

	/**
	 * Add the different variants
	 */
	@Override
	public void getSubItems(Item item, CreativeTabs tab, NonNullList<ItemStack> subItems) {
		subItems.add(new ItemStack(item, 1, 0));
		subItems.add(new ItemStack(item, 1, 1));
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return this.getUnlocalizedName() + (stack.getItemDamage() == 0 ? ".energy" : ".item");
	}

	/**
	 * Allows the player to remove the target block
	 */
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		if (!player.isSneaking())
			player.getHeldItem(hand).setTagCompound(new NBTTagCompound());
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}

	/**
	 * Add the target block
	 */
	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX,
			float hitY, float hitZ, EnumHand hand) {
		if (player.isSneaking() && ((EnergyUtils.hasSupport(world.getTileEntity(pos), side)
				&& player.getHeldItem(hand).getItemDamage() == 0)
				|| (InventoryUtils.hasSupport(world.getTileEntity(pos), side)
						&& player.getHeldItem(hand).getItemDamage() == 1))) {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setIntArray("BlockPos", new int[] { pos.getX(), pos.getY(), pos.getZ() });
			nbt.setString("Side", player.getAdjustedHorizontalFacing().getName2());
			player.getHeldItem(hand).setTagCompound(nbt);
			String blockName = world.getBlockState(pos)
					.getBlock().getPickBlock(world.getBlockState(pos),
							new RayTraceResult(Type.BLOCK, new Vec3d(pos), side, pos), world, pos, player)
					.getDisplayName();
			player.sendMessage(new TextComponentString(TextFormatting.GREEN
					+ I18n.format("item.multimeter.tooltip.blockpos", blockName, pos.getX(), pos.getY(), pos.getZ())));
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.PASS;
	}

	/**
	 * Add the tooltip
	 */
	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey("BlockPos")) {
			BlockPos pos = new BlockPos(stack.getTagCompound().getIntArray("BlockPos")[0],
					stack.getTagCompound().getIntArray("BlockPos")[1],
					stack.getTagCompound().getIntArray("BlockPos")[2]);
			String blockName = player.getEntityWorld().getBlockState(pos).getBlock()
					.getPickBlock(player.getEntityWorld().getBlockState(pos),
							new RayTraceResult(Type.BLOCK, new Vec3d(pos), null, pos), player.getEntityWorld(), pos,
							player)
					.getDisplayName();
			tooltip.add(TextFormatting.GREEN
					+ I18n.format("item.multimeter.tooltip.blockpos", blockName, pos.getX(), pos.getY(), pos.getZ()));
		}
		tooltip.add(I18n.format("item.multimeter.tooltip." + (stack.getItemDamage() == 0 ? "energy" : "item")));
	}

	/**
	 * Handles the overlay which displays the energy inside of any
	 * {@link TileEntity}
	 * 
	 * @author CJMinecraft
	 *
	 */
	public static class MultimeterOverlay extends GuiOverlay {

		/**
		 * The blacklist of blocks which should not display the
		 * {@link EnergyBarOverlay}
		 */
		public static List<ResourceLocation> blacklistBlocksEnergy = new ArrayList<ResourceLocation>();

		public MultimeterOverlay() {
			super();
			blacklistBlocksEnergy.add(new ResourceLocation("actuallyadditions", "block_shock_suppressor"));
			blacklistBlocksEnergy.add(new ResourceLocation("actuallyadditions", "block_display_stand"));
			blacklistBlocksEnergy.add(new ResourceLocation("actuallyadditions", "block_firework_box"));
			blacklistBlocksEnergy.add(new ResourceLocation("actuallyadditions", "block_atomic_reconstructor"));
			blacklistBlocksEnergy.add(new ResourceLocation("actuallyadditions", "block_miner"));
			blacklistBlocksEnergy.add(new ResourceLocation("actuallyadditions", "block_lava_factory_controller"));
			blacklistBlocksEnergy.add(new ResourceLocation("actuallyadditions", "block_furnace_solar"));
			blacklistBlocksEnergy.add(new ResourceLocation("actuallyadditions", "block_heat_collector"));
			blacklistBlocksEnergy.add(new ResourceLocation("actuallyadditions", "block_leaf_generator"));
			blacklistBlocksEnergy.add(new ResourceLocation("actuallyadditions", "block_player_interface"));

			OverlayBase itemOverlay = new OverlayInventory(this, CJCoreConfig.MULTIMETER_OFFSET_X,
					this.height - CJCoreConfig.MULTIMETER_OFFSET_Y - CJCoreConfig.MULTIMETER_ENERGY_HEIGHT - 20)
							.setShowOverlayText(false);
			itemOverlay.addElement(new ElementItemSlot(this, 0, 0));
			addOverlay(itemOverlay);
			OverlayBase energyOverlay = new OverlayBase(this, CJCoreConfig.MULTIMETER_OFFSET_X,
					this.height - CJCoreConfig.MULTIMETER_OFFSET_Y - CJCoreConfig.MULTIMETER_ENERGY_HEIGHT - 20);
			energyOverlay.addElement(new ElementEnergyBar(this, 0, 0, CJCoreConfig.MULTIMETER_ENERGY_WIDTH,
					CJCoreConfig.MULTIMETER_ENERGY_HEIGHT));
			energyOverlay.addElement(new ElementItemSlot(this, 0, CJCoreConfig.MULTIMETER_ENERGY_HEIGHT + 1));
			energyOverlay.setVisible(false);
			addOverlay(energyOverlay);
		}

		@Override
		protected void updateElementInformation() {
			this.overlays.get(0).setPosition(CJCoreConfig.MULTIMETER_OFFSET_X,
					this.height - CJCoreConfig.MULTIMETER_OFFSET_Y - this.overlays.get(0).getHeight());
			if (this.overlays.get(0).isVisible())
				this.overlays.get(1).setPosition(this.overlays.get(0).getPosX() + this.overlays.get(0).getWidth() + 6,
						this.height - CJCoreConfig.MULTIMETER_OFFSET_Y - this.overlays.get(1).getHeight());
			else
				this.overlays.get(1).setPosition(CJCoreConfig.MULTIMETER_OFFSET_X,
						this.height - CJCoreConfig.MULTIMETER_OFFSET_Y - CJCoreConfig.MULTIMETER_ENERGY_HEIGHT - 26);
			addInventoryOverlay();
			addEnergyOverlay();
		}

		/**
		 * Handle the energy overlay
		 */
		private void addEnergyOverlay() {
			ItemStack energyMultimeter = InventoryUtils.findInHotbar(new ItemStack(CJCoreItems.multimeter, 1, 0),
					this.player, true, false);
			if (energyMultimeter.isEmpty()) {
				this.overlays.get(1).setEnabled(false);
				return;
			}
			if (blacklistBlocksEnergy.contains(
					this.mc.world.getBlockState(this.mc.objectMouseOver.getBlockPos()).getBlock().getRegistryName())) {
				this.overlays.get(1).setEnabled(false);
				return;
			}
			this.overlays.get(1).setEnabled(false);
			ElementEnergyBar energyBar = (ElementEnergyBar) this.overlays.get(1).getElements().get(0);
			ElementItemSlot itemSlot = (ElementItemSlot) this.overlays.get(1).getElements().get(1);

			energyBar.setSize(CJCoreConfig.MULTIMETER_ENERGY_WIDTH, CJCoreConfig.MULTIMETER_ENERGY_HEIGHT);
			itemSlot.setPosition(itemSlot.getPosX(), CJCoreConfig.MULTIMETER_ENERGY_HEIGHT + 1);

			if (energyMultimeter.hasTagCompound() && energyMultimeter.getTagCompound().hasKey("BlockPos")) {
				this.overlays.get(1).setEnabled(true);
				this.overlays.get(1).setVisible(true);
				NBTTagCompound nbt = energyMultimeter.getTagCompound();
				BlockPos pos = new BlockPos(nbt.getIntArray("BlockPos")[0], nbt.getIntArray("BlockPos")[1],
						nbt.getIntArray("BlockPos")[2]);
				EnumFacing side = nbt.hasKey("Side") ? EnumFacing.byName(nbt.getString("Side")) : null;
				if (energyBar.getPos() != pos || energyBar.getSide() != side)
					energyBar.shouldSync(pos, side);
				ItemStack block = getStackFromBlock(pos, side);
				if (!InventoryUtils.isStackEqual(block, itemSlot.getStack(), true, false))
					itemSlot.setStack(block);
			} else {
				RayTraceResult result = this.mc.objectMouseOver;
				if (EnergyUtils.hasSupport(this.mc.world.getTileEntity(result.getBlockPos()), result.sideHit)) {
					this.overlays.get(1).setEnabled(true);
					this.overlays.get(1).setVisible(true);
					if (energyBar.getPos() != result.getBlockPos())
						energyBar.shouldSync(result.getBlockPos(), result.sideHit);
					ItemStack block = getStackFromBlock(result.getBlockPos(), result.sideHit);
					if (!InventoryUtils.isStackEqual(block, itemSlot.getStack(), true, false))
						itemSlot.setStack(block);
				} else {
					this.overlays.get(1).setEnabled(false);
					this.overlays.get(1).setVisible(false);
				}
			}
			if (EnergyUtils.hasSupport(this.player.getHeldItemMainhand(), null)) {
				this.overlays.get(1).setEnabled(true);
				this.overlays.get(1).setVisible(true);
				energyBar.shouldntSync();
				energyBar.setEnergy(
						EnergyUtils.getEnergyStored(this.player.getHeldItemMainhand(), null,
								CJCoreConfig.DEFAULT_ENERGY_UNIT),
						EnergyUtils.getCapacity(this.player.getHeldItemMainhand(), null,
								CJCoreConfig.DEFAULT_ENERGY_UNIT),
						CJCoreConfig.DEFAULT_ENERGY_UNIT);
				itemSlot.setStack(this.player.getHeldItemMainhand());
			} else if (EnergyUtils.hasSupport(this.player.getHeldItemOffhand(), null)) {
				this.overlays.get(1).setEnabled(true);
				this.overlays.get(1).setVisible(true);
				energyBar.shouldntSync();
				energyBar.setEnergy(
						EnergyUtils.getEnergyStored(this.player.getHeldItemOffhand(), null,
								CJCoreConfig.DEFAULT_ENERGY_UNIT),
						EnergyUtils.getCapacity(this.player.getHeldItemOffhand(), null,
								CJCoreConfig.DEFAULT_ENERGY_UNIT),
						CJCoreConfig.DEFAULT_ENERGY_UNIT);
				itemSlot.setStack(this.player.getHeldItemOffhand());
			}
		}

		/**
		 * Handle the inventory overlay
		 */
		private void addInventoryOverlay() {
			ItemStack itemMultimeter = InventoryUtils.findInHotbar(new ItemStack(CJCoreItems.multimeter, 1, 1), player,
					true, false);
			if (itemMultimeter.isEmpty()) {
				this.overlays.get(0).setEnabled(false);
				return;
			}
			ElementItemSlot itemSlot = (ElementItemSlot) this.overlays.get(0).getElements().get(0);
			OverlayInventory inv = (OverlayInventory) this.overlays.get(0);
			inv.setEnabled(true);

			itemSlot.setPosition(0, inv.getRows() * 18 + (inv.getExcessColumns() > 0 ? 18 : 0));

			if (itemMultimeter.hasTagCompound() && itemMultimeter.getTagCompound().hasKey("BlockPos")) {
				NBTTagCompound nbt = itemMultimeter.getTagCompound();
				BlockPos pos = new BlockPos(nbt.getIntArray("BlockPos")[0], nbt.getIntArray("BlockPos")[1],
						nbt.getIntArray("BlockPos")[2]);
				EnumFacing side = nbt.hasKey("Side") ? EnumFacing.byName(nbt.getString("Side")) : null;
				if (InventoryUtils.hasSupport(this.mc.world.getTileEntity(pos), side)) {
					if (inv.getPos() != pos || inv.getSide() != side) {
						inv.shouldSync(pos, side, true);
						inv.setVisible(true);
					}
					ItemStack block = getStackFromBlock(pos, side);
					if (!InventoryUtils.isStackEqual(block, itemSlot.getStack(), true, false))
						itemSlot.setStack(block);
				}
			} else {
				RayTraceResult result = this.mc.objectMouseOver;
				if (InventoryUtils.hasSupport(this.mc.world.getTileEntity(result.getBlockPos()), result.sideHit)) {
					if (inv.getPos() != result.getBlockPos() || inv.getSide() != result.sideHit)
						inv.shouldSync(result.getBlockPos(), result.sideHit, true);
					ItemStack block = getStackFromBlock(result.getBlockPos(), result.sideHit);
					if (!InventoryUtils.isStackEqual(block, itemSlot.getStack(), true, false))
						itemSlot.setStack(block);
				} else {
					inv.shouldntSync();
					inv.setVisible(false);
				}
			}
			if (InventoryUtils.hasSupport(this.player.getHeldItemMainhand(), null)) {
				inv.shouldntSync();
				inv.setInventory(InventoryUtils.getInventoryStacked(this.player.getHeldItemMainhand(), null));
				itemSlot.setStack(this.player.getHeldItemMainhand());
			} else if (InventoryUtils.hasSupport(this.player.getHeldItemOffhand(), null)) {
				inv.setInventory(InventoryUtils.getInventoryStacked(this.player.getHeldItemOffhand(), null));
				itemSlot.setStack(this.player.getHeldItemOffhand());
			}
		}

		/**
		 * Get the correct {@link ItemStack} from the given block at the
		 * position provided
		 * 
		 * @param pos
		 *            The position of the block
		 * @param side
		 *            The side of the block (for use with
		 *            {@link Block#getPickBlock(net.minecraft.block.state.IBlockState, RayTraceResult, World, BlockPos, EntityPlayer)}
		 * @return The {@link ItemStack} of the block at the position provided
		 */
		private ItemStack getStackFromBlock(BlockPos pos, EnumFacing side) {
			Block block = this.mc.world.getBlockState(pos).getBlock();
			return block.getPickBlock(this.mc.world.getBlockState(pos),
					new RayTraceResult(Type.BLOCK, new Vec3d(pos), side, pos), this.mc.world, pos, player);
		}

	}

}
