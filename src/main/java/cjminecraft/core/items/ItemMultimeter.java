package cjminecraft.core.items;

import java.util.ArrayList;
import java.util.List;

import cjminecraft.core.CJCore;
import cjminecraft.core.client.gui.EnergyBarOverlay;
import cjminecraft.core.config.CJCoreConfig;
import cjminecraft.core.energy.EnergyData;
import cjminecraft.core.energy.EnergyUnits;
import cjminecraft.core.energy.EnergyUtils;
import cjminecraft.core.init.CJCoreItems;
import cjminecraft.core.util.InventoryUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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
	 * Handles the overlay which displays the energy inside of any
	 * {@link TileEntity}
	 * 
	 * @author CJMinecraft
	 *
	 */
	public static class MultimeterOverlay {

		private static List<ResourceLocation> blacklistBlocks = new ArrayList<ResourceLocation>();
		
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
		}
		
		/**
		 * The overlay to be drawn
		 */
		public static EnergyBarOverlay overlay = new EnergyBarOverlay(0, CJCoreConfig.MULTIMETER_OFFSET_X, CJCoreConfig.MULTIMETER_OFFSET_Y, 0, 0);

		/**
		 * Sync with the server Used every 10 ticks
		 */
		private static int sync = 0;

		public static EnergyData data = new EnergyData();

		/**
		 * Is true when the item in the hand of the {@link EntityPlayer} does
		 * not hold energy
		 */
		private static boolean targetBlock = true;

		/**
		 * Actually draws the overlay
		 * 
		 * @param event The event
		 */
		@SubscribeEvent(receiveCanceled = true)
		public void onEvent(RenderGameOverlayEvent.Pre event) {
			if (Minecraft.getMinecraft().currentScreen != null)
				return;
			EntityPlayerSP player = Minecraft.getMinecraft().player;
			if (player == null)
				return;

			if (!InventoryUtils.hasInHotbar(new ItemStack(CJCoreItems.multimeter), player, true, true))
				return;

			if (EnergyUtils.hasSupport(player.getHeldItemMainhand(), player.getAdjustedHorizontalFacing())) {
				if (sync == 0) {
					if(data == null)
						data = new EnergyData();
						
					data.setEnergy(EnergyUtils.convertEnergy(EnergyUnits.MINECRAFT_JOULES, CJCoreConfig.DEFAULT_ENERGY_UNIT,
							EnergyUtils.getEnergyStored(player.getHeldItemMainhand(),
									player.getAdjustedHorizontalFacing())));
					data.setCapacity(EnergyUtils.convertEnergy(EnergyUnits.MINECRAFT_JOULES, CJCoreConfig.DEFAULT_ENERGY_UNIT,
							EnergyUtils.getCapacity(player.getHeldItemMainhand(),
									player.getAdjustedHorizontalFacing())));
				}
				targetBlock = false;
			} else if (EnergyUtils.hasSupport(player.getHeldItemOffhand(), player.getAdjustedHorizontalFacing())) {
				if (sync == 0) {
					if(data == null)
						data = new EnergyData();
					
					data.setEnergy(EnergyUtils.convertEnergy(EnergyUnits.MINECRAFT_JOULES, CJCoreConfig.DEFAULT_ENERGY_UNIT,
							EnergyUtils.getEnergyStored(player.getHeldItemOffhand(),
									player.getAdjustedHorizontalFacing())));
					data.setCapacity(EnergyUtils.convertEnergy(EnergyUnits.MINECRAFT_JOULES, CJCoreConfig.DEFAULT_ENERGY_UNIT,
							EnergyUtils.getCapacity(player.getHeldItemOffhand(), player.getAdjustedHorizontalFacing())));
				}
				targetBlock = false;
			} else {
				targetBlock = true;
			}
			if (targetBlock) {
				RayTraceResult target = Minecraft.getMinecraft().objectMouseOver;
				if (target.typeOfHit != RayTraceResult.Type.BLOCK)
					return;
				if (blacklistBlocks.contains(Minecraft.getMinecraft().world.getBlockState(target.getBlockPos()).getBlock().getRegistryName()))
					return;
				TileEntity te = Minecraft.getMinecraft().world.getTileEntity(target.getBlockPos());
				if (te == null)
					return;
				if (!EnergyUtils.hasSupport(te, target.sideHit)) {
					if(data == null)
						data = new EnergyData();
					data.setEnergy(0).setEnergy(0);
					return;
				}
				if (sync == 0)
					EnergyUtils.syncEnergyData(target.getBlockPos(), target.sideHit, CJCore.MODID);
				data = EnergyUtils.getCachedEnergyData(CJCore.MODID);
				if(data != null)
					data.convertData(CJCoreConfig.DEFAULT_ENERGY_UNIT);
			}
			sync++;
			sync %= 10;

			if (event.getType() == ElementType.ALL) {
				overlay.updateEnergyBar(data);
				overlay.xPosition = CJCoreConfig.MULTIMETER_OFFSET_X;
				overlay.yPosition = event.getResolution().getScaledHeight() - overlay.height - CJCoreConfig.MULTIMETER_OFFSET_Y;
				overlay.drawButton(Minecraft.getMinecraft(), 0, 0);
			}
		}

	}

}
