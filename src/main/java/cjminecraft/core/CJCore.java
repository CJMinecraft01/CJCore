package cjminecraft.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cjminecraft.core.command.CommandEditTileEntity;
import cjminecraft.core.config.CJCoreConfig;
import cjminecraft.core.crafting.CraftingHandler;
import cjminecraft.core.energy.EnergyUnits;
import cjminecraft.core.energy.EnergyUtils;
import cjminecraft.core.init.CJCoreItems;
import cjminecraft.core.items.ItemMultimeter;
import cjminecraft.core.proxy.CommonProxy;
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
@Mod(name = CJCore.NAME, version = CJCore.VERSION, modid = CJCore.MODID, guiFactory = CJCore.GUI_FACTORY, acceptedMinecraftVersions = CJCore.ACCEPTED_MC_VERSIONS, customProperties = {
		@CustomProperty(k = "useVersionChecker", v = "true") }, useMetadata = true)
public class CJCore {

	public static final List<String> DEPENDANTS = new ArrayList<String>();

	public static final String NAME = "CJCore";
	public static final String MODID = "cjcore";
	public static final String VERSION = "0.0.2.0";
	public static final String ACCEPTED_MC_VERSIONS = "[1.11,1.11.2]";
	public static final String ACCEPTED_MC_VERSION = ForgeVersion.mcVersion;
	public static final String GUI_FACTORY = "cjminecraft.core.config.CJCoreGuiFactory";
	public static final String SERVER_PROXY_CLASS = "cjminecraft.core.proxy.ServerProxy";
	public static final String CLIENT_PROXY_CLASS = "cjminecraft.core.proxy.ClientProxy";
	public static final Logger logger = LogManager.getFormatterLogger(NAME);

	/**
	 * Update the API's list of mods which use it
	 */
	private static void updateDependants() {
		CJCoreConfig.UPDATE_CHECKER_MODS.put(MODID, true);
		for (ModContainer mod : Loader.instance().getActiveModList()) {
			for (ArtifactVersion version : mod.getDependencies()) {
				if (version.getLabel().equals(MODID)) {
					if (!DEPENDANTS.contains(mod.getModId())) {
						DEPENDANTS.add(mod.getModId());
						if (mod.getCustomModProperties().containsKey("useVersionChecker")) {
							if (Boolean.valueOf(mod.getCustomModProperties().get("useVersionChecker"))) {
								if (!CJCoreConfig.UPDATE_CHECKER_MODS.containsKey(mod.getModId())) {
									CJCoreConfig.UPDATE_CHECKER_MODS.put(mod.getModId(), true);
								}
							}
						} else {
							logger.error("Mod " + mod.getModId()
									+ " does not say whether it uses an version checker! Please fix this!");
						}
					}
				}
			}
			for (ArtifactVersion version : mod.getRequirements()) {
				if (version.getLabel().equals(MODID)) {
					if (!DEPENDANTS.contains(mod.getModId())) {
						DEPENDANTS.add(mod.getModId());
						if (mod.getCustomModProperties().containsKey("useVersionChecker")) {
							if (Boolean.valueOf(mod.getCustomModProperties().get("useVersionChecker"))) {
								if (!CJCoreConfig.UPDATE_CHECKER_MODS.containsKey(mod.getModId())) {
									CJCoreConfig.UPDATE_CHECKER_MODS.put(mod.getModId(), true);
								}
							}
						} else {
							logger.error("Mod " + mod.getModId()
									+ " does not say whether it uses an version checker! Please fix this!");
						}
					}
				}
			}
		}
		DEPENDANTS.forEach(mod -> {
			CJCore.logger.info("Found dependant: " + mod);
		});
		CJCoreConfig.UPDATE_CHECKER_MODS.forEach((key, value) -> {
			CJCore.logger.info("Mod " + key + " says it has a version checker!");
		});
	}

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
		updateDependants();
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

	@EventHandler
	public void serverStart(FMLServerStartingEvent event) {
		ICommandManager command = event.getServer().getCommandManager();
		ServerCommandManager manager = (ServerCommandManager) command;
		manager.registerCommand(new CommandEditTileEntity());
	}

	@EventHandler
	public void imcEvent(IMCEvent event) {
		for (IMCMessage message : event.getMessages()) {
			if (message.isResourceLocationMessage() && message.key == "multimeterBlacklist") {
				ItemMultimeter.MultimeterOverlay.blacklistBlocksEnergy.add(message.getResourceLocationValue());
				logger.info(String.format("Blacklisting block: %s:%s",
						message.getResourceLocationValue().getResourceDomain(),
						message.getResourceLocationValue().getResourcePath()));
			}
		}
	}

}
