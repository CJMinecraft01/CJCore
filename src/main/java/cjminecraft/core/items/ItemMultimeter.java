package cjminecraft.core.items;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.Color;

import cjminecraft.core.CJCore;
import cjminecraft.core.client.gui.EnergyBarOverlay;
import cjminecraft.core.config.CJCoreConfig;
import cjminecraft.core.energy.EnergyData;
import cjminecraft.core.energy.EnergyUnits;
import cjminecraft.core.energy.EnergyUtils;
import cjminecraft.core.init.CJCoreItems;
import cjminecraft.core.util.InventoryUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import scala.actors.threadpool.Arrays;

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
		this.setCreativeTab(CreativeTabs.REDSTONE);
	}

	/**
	 * Allows the player to remove the target block
	 */
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		if (!player.isSneaking()) {
			player.getHeldItem(hand).setTagCompound(new NBTTagCompound());
		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}

	/**
	 * Add the target block
	 */
	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX,
			float hitY, float hitZ, EnumHand hand) {
		if (player.isSneaking() && EnergyUtils.hasSupport(world.getTileEntity(pos), side)) {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setIntArray("BlockPos", new int[] { pos.getX(), pos.getY(), pos.getZ() });
			player.getHeldItem(hand).setTagCompound(nbt);
			IBlockState state = world.getBlockState(pos);
			String blockName = new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state))
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
			IBlockState state = Minecraft.getMinecraft().world.getBlockState(pos);
			ItemStack blockStack = new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state));
			String blockName = blockStack.getDisplayName();
			tooltip.add(TextFormatting.GREEN
					+ I18n.format("item.multimeter.tooltip.blockpos", blockName, pos.getX(), pos.getY(), pos.getZ()));
		}
		tooltip.add(I18n.format("item.multimeter.tooltip"));
	}

	/**
	 * Handles the overlay which displays the energy inside of any
	 * {@link TileEntity}
	 * 
	 * @author CJMinecraft
	 *
	 */
	public static class MultimeterOverlay {

		/**
		 * The blacklist of blocks which should not display the
		 * {@link EnergyBarOverlay}
		 */
		public static List<ResourceLocation> blacklistBlocks = new ArrayList<ResourceLocation>();

		public MultimeterOverlay() {
			blacklistBlocks.add(new ResourceLocation("actuallyadditions", "block_shock_suppressor"));
			blacklistBlocks.add(new ResourceLocation("actuallyadditions", "block_display_stand"));
			blacklistBlocks.add(new ResourceLocation("actuallyadditions", "block_firework_box"));
			blacklistBlocks.add(new ResourceLocation("actuallyadditions", "block_atomic_reconstructor"));
			blacklistBlocks.add(new ResourceLocation("actuallyadditions", "block_miner"));
			blacklistBlocks.add(new ResourceLocation("actuallyadditions", "block_lava_factory_controller"));
			blacklistBlocks.add(new ResourceLocation("actuallyadditions", "block_furnace_solar"));
			blacklistBlocks.add(new ResourceLocation("actuallyadditions", "block_heat_collector"));
			blacklistBlocks.add(new ResourceLocation("actuallyadditions", "block_leaf_generator"));
			blacklistBlocks.add(new ResourceLocation("actuallyadditions", "block_player_interface"));
		}

		/**
		 * The overlay to be drawn
		 */
		public static EnergyBarOverlay overlay = new EnergyBarOverlay(0, CJCoreConfig.MULTIMETER_OFFSET_X,
				CJCoreConfig.MULTIMETER_OFFSET_Y, CJCoreConfig.MULTIMETER_WIDTH, CJCoreConfig.MULTIMETER_HEIGHT, 0, 0);

		/**
		 * Sync with the server Used every 10 ticks
		 */
		private static int sync = 0;

		/**
		 * The current energy data
		 */
		public static EnergyData data = new EnergyData();

		/**
		 * Is true when the item in the hand of the {@link EntityPlayer} does
		 * not hold energy
		 */
		private static boolean targetBlock = true;

		/**
		 * Actually draws the overlay
		 * 
		 * @param event
		 *            The event
		 */
		@SubscribeEvent(receiveCanceled = true)
		public void onEvent(RenderGameOverlayEvent.Pre event) {
			if (Minecraft.getMinecraft().currentScreen != null)
				return;
			EntityPlayerSP player = Minecraft.getMinecraft().player;
			if (player == null)
				return;

			if (sync == 0) {
				if (!InventoryUtils.hasInHotbar(new ItemStack(CJCoreItems.multimeter), player, true, true))
					return;
				ItemStack found = InventoryUtils.findInHotbar(new ItemStack(CJCoreItems.multimeter), player, true,
						false);
				if (found.hasTagCompound() && found.getTagCompound().hasKey("BlockPos")) {
					NBTTagCompound nbt = found.getTagCompound();
					BlockPos pos = new BlockPos(nbt.getIntArray("BlockPos")[0], nbt.getIntArray("BlockPos")[1],
							nbt.getIntArray("BlockPos")[2]);
					if (blacklistBlocks.contains(Minecraft.getMinecraft().world
							.getBlockState(Minecraft.getMinecraft().objectMouseOver.getBlockPos()).getBlock()
							.getRegistryName()))
						return;
					TileEntity te = Minecraft.getMinecraft().world.getTileEntity(pos);
					if (te == null)
						return;
					if (!EnergyUtils.hasSupport(te, player.getAdjustedHorizontalFacing())) {
						if (data == null)
							data = new EnergyData();
						data.setEnergy(0).setEnergy(0);
						return;
					}
					EnergyUtils.syncEnergyData(CJCoreConfig.DEFAULT_ENERGY_UNIT, pos,
							player.getAdjustedHorizontalFacing(), CJCore.MODID);
					data = EnergyUtils.getCachedEnergyData(CJCore.MODID);
				} else {
					if (EnergyUtils.hasSupport(player.getHeldItemMainhand(), player.getAdjustedHorizontalFacing())) {
						if (data == null)
							data = new EnergyData();
						data.setEnergy(EnergyUtils.getEnergyStored(player.getHeldItemMainhand(),
								player.getAdjustedHorizontalFacing(), CJCoreConfig.DEFAULT_ENERGY_UNIT));
						data.setCapacity(EnergyUtils.getCapacity(player.getHeldItemMainhand(),
								player.getAdjustedHorizontalFacing(), CJCoreConfig.DEFAULT_ENERGY_UNIT));
						targetBlock = false;
					} else if (EnergyUtils.hasSupport(player.getHeldItemOffhand(),
							player.getAdjustedHorizontalFacing())) {
						if (data == null)
							data = new EnergyData();
						data.setEnergy(EnergyUtils.getEnergyStored(player.getHeldItemOffhand(),
								player.getAdjustedHorizontalFacing(), CJCoreConfig.DEFAULT_ENERGY_UNIT));
						data.setCapacity(EnergyUtils.getCapacity(player.getHeldItemOffhand(),
								player.getAdjustedHorizontalFacing(), CJCoreConfig.DEFAULT_ENERGY_UNIT));
						targetBlock = false;
					} else {
						targetBlock = true;
					}
					if (targetBlock) {
						RayTraceResult target = Minecraft.getMinecraft().objectMouseOver;
						if (target.typeOfHit != RayTraceResult.Type.BLOCK)
							return;
						if (blacklistBlocks.contains(Minecraft.getMinecraft().world.getBlockState(target.getBlockPos())
								.getBlock().getRegistryName()))
							return;
						TileEntity te = Minecraft.getMinecraft().world.getTileEntity(target.getBlockPos());
						if (te == null)
							return;
						if (!EnergyUtils.hasSupport(te, target.sideHit)) {
							if (data == null)
								data = new EnergyData();
							data.setEnergy(0).setEnergy(0);
							return;
						}
						EnergyUtils.syncEnergyData(CJCoreConfig.DEFAULT_ENERGY_UNIT, target.getBlockPos(),
								target.sideHit, CJCore.MODID);
						data = EnergyUtils.getCachedEnergyData(CJCore.MODID);
					}
				}
			}

			sync++;
			sync %= 10;

			if (event.getType() == ElementType.ALL) {
				overlay.width = CJCoreConfig.MULTIMETER_WIDTH;
				overlay.height = CJCoreConfig.MULTIMETER_HEIGHT;
				overlay.updateEnergyBar(data);
				overlay.xPosition = CJCoreConfig.MULTIMETER_OFFSET_X;
				overlay.yPosition = event.getResolution().getScaledHeight() - overlay.height
						- CJCoreConfig.MULTIMETER_OFFSET_Y;
				overlay.drawButton(Minecraft.getMinecraft(), 0, 0);
			}
		}

	}

}
