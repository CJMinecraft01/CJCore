package cjminecraft.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cjminecraft.core.config.CJCoreConfig;
import cjminecraft.core.crafting.CraftingHandler;
import cjminecraft.core.energy.EnergyUnits;
import cjminecraft.core.energy.EnergyUtils;
import cjminecraft.core.init.CJCoreItems;
import cjminecraft.core.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
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
	public static final String VERSION = "${version}";
	public static final String ACCEPTED_MC_VERSIONS = "[1.11.2]";
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
		EnergyUnits.preInit();
		EnergyUtils.preInit();
		CJCoreConfig.preInit();
		proxy.preInit();
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		CraftingHandler.registerCraftingRecipes();
		proxy.init();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit();
	}

}
