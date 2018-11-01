package cjminecraft.core.init;

import cjminecraft.core.CJCore;
import cjminecraft.core.energy.EnergyUtils;
import cjminecraft.core.fluid.FluidUtils;
import cjminecraft.core.inventory.InventoryUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(modid = CJCore.MODID)
public class CJCoreEvents {
	
	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent event) {
		if (event.getModID().equals(CJCore.MODID))
			ConfigManager.sync(CJCore.MODID, Type.INSTANCE);
	}

}
