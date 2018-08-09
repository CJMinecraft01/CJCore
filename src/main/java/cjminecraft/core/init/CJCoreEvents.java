package cjminecraft.core.init;

import cjminecraft.core.CJCore;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = CJCore.MODID)
public class CJCoreEvents {
	
	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent event) {
		if (event.getModID().equals(CJCore.MODID))
			ConfigManager.sync(CJCore.MODID, Type.INSTANCE);
	}

}
