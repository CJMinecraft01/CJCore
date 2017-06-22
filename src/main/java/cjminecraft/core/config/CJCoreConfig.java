package cjminecraft.core.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import cjminecraft.core.CJCore;
import cjminecraft.core.client.gui.EnergyBar;
import cjminecraft.core.energy.EnergyUnits;
import cjminecraft.core.energy.EnergyUnits.EnergyUnit;
import cjminecraft.core.proxy.ClientProxy;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * The config for the {@link CJCore}
 * 
 * @author CJMinecraft
 *
 */
public class CJCoreConfig {

	/**
	 * The name of the category for energy
	 */
	public static final String CATEGORY_NAME_ENERGY = "energy";

	/**
	 * The name of the category for the update checkers
	 */
	public static final String CATEGORY_NAME_VERSION_CHECKER = "version_checker";

	/*
	 * Energy Config
	 */
	/**
	 * The default {@link EnergyUnit}
	 */
	public static EnergyUnit DEFAULT_ENERGY_UNIT;

	/**
	 * The position of the multimeter on the X axis from the left hand side
	 */
	public static int MULTIMETER_OFFSET_X;

	/**
	 * The position of the multimeter on the Y axis from the bottom
	 */
	public static int MULTIMETER_OFFSET_Y;

	/**
	 * The width of the multimeter
	 */
	public static int MULTIMETER_WIDTH;

	/**
	 * The height of the multimeter
	 */
	public static int MULTIMETER_HEIGHT;

	/**
	 * Whether or not the multimeter should show capacity
	 */
	public static boolean MULTIMETER_SHOW_CAPACITY;

	/**
	 * Whether the multimeter should simplify the energy displayed
	 */
	public static boolean MULTIMETER_SIMPLIFY_ENERGY;

	/*
	 * Update Checkers Config
	 */
	/**
	 * Whether or not a specific mod's update checker is disabled (the key is
	 * the mod's modid)
	 */
	public static HashMap<String, Boolean> UPDATE_CHECKER_MODS = new HashMap<String, Boolean>();
	/**
	 * A list of {@link Property}'s for use with the config. Each property has
	 * the modid attached
	 */
	public static List<Pair<String, Property>> UPDATE_CHECKER_MOD_PROPERTIES = new ArrayList<Pair<String, Property>>();

	private static Configuration config = null;

	/**
	 * For use in {@link CJCore} only
	 */
	public static void preInit() {
		File configFile = new File(Loader.instance().getConfigDir(), "CJCore.cfg");
		config = new Configuration(configFile);
		syncFromFile();
	}

	/**
	 * For use in {@link ClientProxy} only
	 */
	public static void clientPreInit() {
		MinecraftForge.EVENT_BUS.register(new ConfigEventHandler());
	}

	/**
	 * Get the config
	 * 
	 * @return the actual {@link Configuration}
	 */
	public static Configuration getConfig() {
		return config;
	}

	/**
	 * Sync from changes in the file
	 */
	public static void syncFromFile() {
		syncConfig(true, true);
	}

	/**
	 * Sync from changes in the {@link GuiConfig}
	 */
	public static void syncFromGUI() {
		syncConfig(false, true);
	}

	/**
	 * Sync from changes in the fields
	 */
	public static void syncFromFields() {
		syncConfig(false, false);
	}

	/**
	 * Actually sync the config
	 * 
	 * @param loadConfigFromFile
	 *            Load the config from the file?
	 * @param readFieldsFromConfig
	 *            Read the fields from the config?
	 */
	private static void syncConfig(boolean loadConfigFromFile, boolean readFieldsFromConfig) {
		if (loadConfigFromFile)
			config.load();

		/*
		 * Energy Config
		 */
		List<String> energyUnits = new ArrayList<String>();
		EnergyUnits.getEnergyUnits().forEach(unit -> {
			energyUnits.add(unit.getUnlocalizedName());
		});
		Property propertyDefaultEnergyUnit = config.get(CATEGORY_NAME_ENERGY, "DefaultEnergyUnit",
				EnergyUnits.FORGE_ENERGY.getUnlocalizedName());
		propertyDefaultEnergyUnit.setValidValues(energyUnits.toArray(new String[] {}));
		propertyDefaultEnergyUnit.setComment("The energy unit that you will normally see");
		propertyDefaultEnergyUnit.setLanguageKey("gui.config.energy.default_energy_unit.name");

		Property propertyMultimeterOffsetX = config.get(CATEGORY_NAME_ENERGY, "MultimeterOffsetX", 6);
		propertyMultimeterOffsetX.setMinValue(0);
		propertyMultimeterOffsetX.setComment("The offset from the left hand side when using the Multimeter");
		propertyMultimeterOffsetX.setLanguageKey("gui.config.energy.multimeter_offset_x.name");

		Property propertyMultimeterOffsetY = config.get(CATEGORY_NAME_ENERGY, "MultimeterOffsetY", 7);
		propertyMultimeterOffsetY.setMinValue(0);
		propertyMultimeterOffsetY.setComment("The offset from the bottom when using the Multimeter");
		propertyMultimeterOffsetY.setLanguageKey("gui.config.energy.multimeter_offset_y.name");

		Property propertyMultimeterWidth = config.get(CATEGORY_NAME_ENERGY, "MultimeterWidth", EnergyBar.DEFAULT_WIDTH);
		propertyMultimeterWidth.setMinValue(1);
		propertyMultimeterWidth.setMaxValue(EnergyBar.DEFAULT_WIDTH);
		propertyMultimeterWidth.setComment("The width of the multimeter gui");
		propertyMultimeterWidth.setLanguageKey("gui.config.energy.multimeter_width.name");

		Property propertyMultimeterHeight = config.get(CATEGORY_NAME_ENERGY, "MultimeterHeight",
				EnergyBar.DEFAULT_HEIGHT);
		propertyMultimeterHeight.setMinValue(1);
		propertyMultimeterHeight.setMaxValue(EnergyBar.DEFAULT_HEIGHT);
		propertyMultimeterHeight.setComment("The height of the multimeter gui");
		propertyMultimeterHeight.setLanguageKey("gui.config.energy.multimeter_height.name");

		Property propertyMultimeterShowCapacity = config.get(CATEGORY_NAME_ENERGY, "MultimeterShowCapacity", false);
		propertyMultimeterShowCapacity.setComment("Whether or not to show the capacity when using the multimeter");
		propertyMultimeterShowCapacity.setLanguageKey("gui.config.energy.multimeter_show_capacity.name");

		Property propertyMultimeterSimplifyEnergy = config.get(CATEGORY_NAME_ENERGY, "MultimeterSimplifyEnergy", false);
		propertyMultimeterSimplifyEnergy
				.setComment("Whether or not to simplify the way the energy is displayed when using the multimeter");
		propertyMultimeterSimplifyEnergy.setLanguageKey("gui.config.energy.multimeter_simplify_energy.name");

		List<String> propertyOrderEnergy = new ArrayList<String>();
		propertyOrderEnergy.add(propertyDefaultEnergyUnit.getName());
		propertyOrderEnergy.add(propertyMultimeterOffsetX.getName());
		propertyOrderEnergy.add(propertyMultimeterOffsetY.getName());
		propertyOrderEnergy.add(propertyMultimeterWidth.getName());
		propertyOrderEnergy.add(propertyMultimeterHeight.getName());
		propertyOrderEnergy.add(propertyMultimeterShowCapacity.getName());
		propertyOrderEnergy.add(propertyMultimeterSimplifyEnergy.getName());
		config.setCategoryPropertyOrder(CATEGORY_NAME_ENERGY, propertyOrderEnergy);

		/*
		 * Update Checkers Config
		 */
		List<String> propertyOrderUpdateChecker = new ArrayList<String>();
		Iterator<String> mods = UPDATE_CHECKER_MODS.keySet().iterator();
		while (mods.hasNext()) {
			String modid = mods.next();
			Property propertyUpdateCheckerEnabled = config.get(CATEGORY_NAME_VERSION_CHECKER, modid, true);
			propertyUpdateCheckerEnabled.setComment("Whether the update checker for " + modid + " is enabled");
			propertyUpdateCheckerEnabled.setLanguageKey("gui.config.update_checker.enabled.name");
			UPDATE_CHECKER_MOD_PROPERTIES.add(Pair.of(modid, propertyUpdateCheckerEnabled));
			propertyOrderUpdateChecker.add(propertyUpdateCheckerEnabled.getName());
		}
		config.setCategoryPropertyOrder(CATEGORY_NAME_VERSION_CHECKER, propertyOrderUpdateChecker);

		if (readFieldsFromConfig) {
			/*
			 * Energy Config
			 */
			DEFAULT_ENERGY_UNIT = EnergyUnits.byUnlocalizedName(propertyDefaultEnergyUnit.getString());
			MULTIMETER_OFFSET_X = propertyMultimeterOffsetX.getInt();
			MULTIMETER_OFFSET_Y = propertyMultimeterOffsetY.getInt();
			MULTIMETER_WIDTH = propertyMultimeterWidth.getInt();
			MULTIMETER_HEIGHT = propertyMultimeterHeight.getInt();
			MULTIMETER_SHOW_CAPACITY = propertyMultimeterShowCapacity.getBoolean();
			MULTIMETER_SIMPLIFY_ENERGY = propertyMultimeterSimplifyEnergy.getBoolean();

			/*
			 * Update Checkers Config
			 */
			for (Pair<String, Property> mod : UPDATE_CHECKER_MOD_PROPERTIES) {
				UPDATE_CHECKER_MODS.remove(mod.getLeft());
				UPDATE_CHECKER_MODS.put(mod.getLeft(), mod.getRight().getBoolean());
			}
		}

		/*
		 * Energy Config
		 */
		propertyDefaultEnergyUnit.set(DEFAULT_ENERGY_UNIT.getUnlocalizedName());
		propertyMultimeterOffsetX.set(MULTIMETER_OFFSET_X);
		propertyMultimeterOffsetY.set(MULTIMETER_OFFSET_Y);
		propertyMultimeterWidth.set(MULTIMETER_WIDTH);
		propertyMultimeterHeight.set(MULTIMETER_HEIGHT);
		propertyMultimeterShowCapacity.set(MULTIMETER_SHOW_CAPACITY);
		propertyMultimeterSimplifyEnergy.set(MULTIMETER_SIMPLIFY_ENERGY);

		/*
		 * Update Checkers Config
		 */
		for (Pair<String, Property> mod : UPDATE_CHECKER_MOD_PROPERTIES) {
			mod.getRight().set(UPDATE_CHECKER_MODS.get(mod.getLeft()));
		}

		if (config.hasChanged())
			config.save();
	}

	/**
	 * Makes sure to save the config when changes are made in the
	 * {@link GuiConfig}
	 * 
	 * @author CJMinecraft
	 *
	 */
	public static class ConfigEventHandler {

		@SubscribeEvent(priority = EventPriority.NORMAL)
		public void onEvent(ConfigChangedEvent.OnConfigChangedEvent event) {
			if (CJCore.MODID.equalsIgnoreCase(event.getModID())) {
				syncFromGUI();
			}
		}

	}

}
