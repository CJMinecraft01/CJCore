package cjminecraft.core.init;

import cjminecraft.core.CJCore;
import cjminecraft.core.crafting.CraftingHandler;
import cjminecraft.core.energy.EnergyUtils;
import cjminecraft.core.fluid.FluidUtils;
import cjminecraft.core.inventory.InventoryUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CJCoreEvents {

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onItemCraft(ItemCraftedEvent event) {
		if (EnergyUtils.hasSupport(event.crafting, null))
			event.player.unlockRecipes(new ResourceLocation[] { new ResourceLocation(CJCore.MODID, "multimeter_energy") });
		if (InventoryUtils.hasSupport(event.crafting, null))
			event.player.unlockRecipes(new ResourceLocation[] { new ResourceLocation(CJCore.MODID, "multimeter_item") });
		if (FluidUtils.hasSupport(event.crafting, null))
			event.player.unlockRecipes(new ResourceLocation[] { new ResourceLocation(CJCore.MODID, "multimeter_fluid") });
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onBlockPlaced(PlaceEvent event) {
		TileEntity te = event.getWorld().getTileEntity(event.getPos());
		if(te == null)
			return;
		if(EnergyUtils.hasSupport(te, null)) 
			event.getPlayer().unlockRecipes(new ResourceLocation[] { new ResourceLocation(CJCore.MODID, "multimeter_energy") });
		if(InventoryUtils.hasSupport(te, null))
			event.getPlayer().unlockRecipes(new ResourceLocation[] { new ResourceLocation(CJCore.MODID, "multimeter_item") });
		if(FluidUtils.hasSupport(te, null))
			event.getPlayer().unlockRecipes(new ResourceLocation[] { new ResourceLocation(CJCore.MODID, "multimeter_fluid") });
	}

}
