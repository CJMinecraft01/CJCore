package cjminecraft.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cjminecraft.core.command.CommandEditTileEntity;
import cjminecraft.core.config.CJCoreConfig;
import cjminecraft.core.crafting.CraftingHandler;
import cjminecraft.core.energy.EnergyUnits;
import cjminecraft.core.energy.EnergyUtils;
import cjminecraft.core.energy.support.BuildCraftSupport;
import cjminecraft.core.energy.support.TeslaSupport;
import cjminecraft.core.init.CJCoreItems;
import cjminecraft.core.items.ItemMultimeter;
import cjminecraft.core.proxy.CommonProxy;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;;

/**
 * The main class for the API
 * @author CJMinecraft
 *
 */
@Mod(name = CJCore.NAME, version = CJCore.VERSION, modid = CJCore.MODID, guiFactory = CJCore.GUI_FACTORY, acceptedMinecraftVersions = CJCore.ACCEPTED_MC_VERSIONS)
public class CJCore {
	
	public static final String NAME = "CJCore";
	public static final String MODID = "cjcore";
	public static final String VERSION = "0.0.1.1";
	public static final String ACCEPTED_MC_VERSIONS = "[1.11,1.11.2]";
	public static final String ACCEPTED_MC_VERSION = "1.11.2";
	public static final String GUI_FACTORY = "cjminecraft.core.config.CJCoreGuiFactory";
	public static final String SERVER_PROXY_CLASS = "cjminecraft.core.proxy.ServerProxy";
	public static final String CLIENT_PROXY_CLASS = "cjminecraft.core.proxy.ClientProxy";
	public static final Logger logger = LogManager.getFormatterLogger(NAME);
	
	/**
	 * The instance for guis
	 */
	@Mod.Instance(MODID)
	public static CJCore instance;
	
	/**
	 * The proxy
	 */
	@SidedProxy(serverSide = SERVER_PROXY_CLASS, clientSide = CLIENT_PROXY_CLASS)
	public static CommonProxy proxy;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		CJCoreItems.init();
		CJCoreItems.register();
		proxy.preInit();
		EnergyUtils.preInit();
		CJCoreConfig.preInit();
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init();
		CraftingHandler.registerCraftingRecipes();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit();
	}
	
	@EventHandler
	public void serverStart(FMLServerStartingEvent event) {
		ICommandManager command = event.getServer().getCommandManager();
		ServerCommandManager manager = (ServerCommandManager) command;
		manager.registerCommand(new CommandEditTileEntity());
	}
	
	@EventHandler
	public void imcEvent(IMCEvent event) {
		for(IMCMessage message : event.getMessages()) {
			if(message.isResourceLocationMessage() && message.key == "multimeterBlacklist") {
				ItemMultimeter.MultimeterOverlay.blacklistBlocks.add(message.getResourceLocationValue());
				logger.info("Blacklisting block: " + message.getResourceLocationValue().getResourceDomain() + ":" + message.getResourceLocationValue().getResourcePath());
			}
		}
	}

}
