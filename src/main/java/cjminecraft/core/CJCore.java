package cjminecraft.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cjminecraft.core.command.CommandEditTileEntity;
import cjminecraft.core.config.CJCoreConfig2;
import cjminecraft.core.crafting.CraftingHandler;
import cjminecraft.core.energy.EnergyUtils;
import cjminecraft.core.fluid.FluidUtils;
import cjminecraft.core.init.CJCoreItems;
import cjminecraft.core.inventory.InventoryUtils;
import cjminecraft.core.items.ItemMultimeter;
import cjminecraft.core.proxy.CommonProxy;
import cjminecraft.core.util.UpdateChecker;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.CustomProperty;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;

/**
 * The main class for the API
 * 
 * @author CJMinecraft
 *
 */
@Mod(name = CJCore.NAME, version = CJCore.VERSION, modid = CJCore.MODID, acceptedMinecraftVersions = CJCore.ACCEPTED_MC_VERSIONS, useMetadata = true)
public class CJCore {

	public static final String NAME = "CJCore";
	public static final String MODID = "cjcore";
	public static final String VERSION = "${version}";
	public static final String ACCEPTED_MC_VERSIONS = "[1.12,1.12.2]";
	public static final String ACCEPTED_MC_VERSION = ForgeVersion.mcVersion;
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
		UpdateChecker.registerUpdateURL(MODID, UpdateChecker.cjcoreURL);
		CJCoreItems.init();
		CJCoreItems.register();
		EnergyUtils.preInit();
		proxy.preInit();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		CraftingHandler.registerCraftingRecipes();
		// CJCoreConfig.init();
		proxy.init();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit();
		UpdateChecker.postInit();
	}

	@EventHandler
	public void serverStart(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandEditTileEntity());

		EnergyUtils.clearCache();
		InventoryUtils.clearCache();
		FluidUtils.clearCache();
	}

	@EventHandler
	public void imcEvent(IMCEvent event) {
		for (IMCMessage message : event.getMessages()) {
			if (message.isResourceLocationMessage() && message.key == "multimeterBlacklist") {
				ItemMultimeter.MultimeterOverlay.blacklistBlocksEnergy.add(message.getResourceLocationValue());
				logger.info(String.format("Blacklisting block: %s:%s",
						message.getResourceLocationValue().getNamespace(),
						message.getResourceLocationValue().getPath()));
			}
		}
	}

}
