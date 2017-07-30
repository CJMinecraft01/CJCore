package cjminecraft.core.init;

import cjminecraft.core.CJCore;
import cjminecraft.core.util.VersionChecker;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class CJCoreEvents {
	
	@SubscribeEvent
	public void onPlayerLogIn(PlayerLoggedInEvent event) {
		VersionChecker.checkForUpdate(VersionChecker.cjcoreURL, CJCore.MODID, CJCore.VERSION, event.player);
	}
}