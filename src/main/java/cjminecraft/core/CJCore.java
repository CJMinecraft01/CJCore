package cjminecraft.core;

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
import net.minecraft.item.Item;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.Mod.CustomProperty;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * The main class for the API
 * 
 * @author CJMinecraft
 *
 */
@Mod(
        name = CJCore.NAME,
        version = CJCore.VERSION,
        modid = CJCore.MODID,
        guiFactory = CJCore.GUI_FACTORY,
        acceptedMinecraftVersions = CJCore.ACCEPTED_MC_VERSIONS,
        customProperties = {@CustomProperty(k = "useVersionChecker", v = "true")},
        useMetadata = true
)
public class CJCore {

    public static final List<String> DEPENDANTS = new ArrayList<>();

    public static final String
            NAME = "CJCore",
            MODID = "cjcore",
            VERSION = "0.0.1.12",
            ACCEPTED_MC_VERSIONS = "[1.12,]",
            ACCEPTED_MC_VERSION = ForgeVersion.mcVersion,
            GUI_FACTORY = "cjminecraft.core.config.CJCoreGuiFactory",
            SERVER_PROXY_CLASS = "cjminecraft.core.proxy.ServerProxy",
            CLIENT_PROXY_CLASS = "cjminecraft.core.proxy.ClientProxy";
    public static final Logger logger = LogManager.getFormatterLogger(NAME);

    /**
     * Update the APIs list of mods which use it
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
							logger.error(String.format("Mod %s does not say whether it uses an version checker! Please fix this!", mod.getModId()));
							FMLCommonHandler.instance().exitJava(0, false);
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
							logger.error(String.format("Mod %s does not say whether it uses an version checker! Please fix this!", mod.getModId()));
							FMLCommonHandler.instance().exitJava(0, false);
						}
					}
				}
			}
		}
		DEPENDANTS.forEach(mod -> logger.info("Found dependant: " + mod));
		CJCoreConfig.UPDATE_CHECKER_MODS.forEach((key, value) ->
                logger.info("Mod " + key + " says it has a version checker!"))
        ;
	}

	/**
	 * The instance for GUIs
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
		MinecraftForge.EVENT_BUS.register(this);
		updateDependants();
		EnergyUnits.preInit();
		EnergyUtils.preInit();
		CJCoreConfig.preInit();
		proxy.preInit();
	}

	@SubscribeEvent
    public void onItemRegister(RegistryEvent.Register<Item> register) {
        CJCoreItems.register(register);
        proxy.onItemRegister(register);
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
			if (message.isResourceLocationMessage() && message.key.equals("multimeterBlacklist")) {
				ItemMultimeter.MultimeterOverlay.blacklistBlocks.add(message.getResourceLocationValue());
				logger.info(String.format("Blacklisting block: %s:%s",
                        message.getResourceLocationValue().getResourceDomain(),
                        message.getResourceLocationValue().getResourcePath())
                );
			}
		}
	}
}