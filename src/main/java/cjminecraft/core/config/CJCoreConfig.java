package cjminecraft.core.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import cjminecraft.core.CJCore;
import cjminecraft.core.client.gui.EnergyBar;
import cjminecraft.core.client.gui.element.ElementEnergyBar;
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
	
	/**
	 * The name of the category for the multimeter
	 */
	public static final String CATEGORY_NAME_MULTIMETER = "multimeter";

	/*
	 * Energy Config
	 */
	/**
	 * The default {@link EnergyUnit}
	 */
	public static EnergyUnit DEFAULT_ENERGY_UNIT;

	/**
	 * Whether or not the energy bar should show capacity
	 */
	public static boolean ENERGY_BAR_SHOW_CAPACITY;

	/**
	 * Whether the energy bar should simplify the energy displayed
	 */
	public static boolean ENERGY_BAR_SIMPLIFY_ENERGY;


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

	/*
	 * Multimeter Config
	 */
	/**
	 * The position of the multimeter on the X axis from the left hand side
	 */
	public static int MULTIMETER_OFFSET_X;

	/**
	 * The position of the multimeter on the Y axis from the bottom
	 */
	public static int MULTIMETER_OFFSET_Y;

	/**
	 * The width of the energy multimeter
	 */
	public static int MULTIMETER_ENERGY_WIDTH;

	/**
	 * The height of the energy multimeter
	 */
	public static int MULTIMETER_ENERGY_HEIGHT;
	
	/**
	 * The maximum number of columns for the item multimeter
	 */
	public static int MULTIMETER_ITEM_MAX_COLUMNS;
	
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

		Property propertyEnergyBarShowCapacity = config.get(CATEGORY_NAME_ENERGY, "EnergyBarShowCapacity", false);
		propertyEnergyBarShowCapacity.setComment("Whether or not the energy bar should show the capacity");
		propertyEnergyBarShowCapacity.setLanguageKey("gui.config.energy.energy_bar_show_capacity.name");

		Property propertyEnergyBarSimplifyEnergy = config.get(CATEGORY_NAME_ENERGY, "EnergyBarSimplifyEnergy", false);
		propertyEnergyBarSimplifyEnergy
				.setComment("Whether or not to simplify the way the energy is displayed when using an energy bar");
		propertyEnergyBarSimplifyEnergy.setLanguageKey("gui.config.energy.energy_bar_simplify_energy.name");

		List<String> propertyOrderEnergy = new ArrayList<String>();
		propertyOrderEnergy.add(propertyDefaultEnergyUnit.getName());
		propertyOrderEnergy.add(propertyEnergyBarShowCapacity.getName());
		propertyOrderEnergy.add(propertyEnergyBarSimplifyEnergy.getName());
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

		/*
		 * Multimeter Config
		 */
		Property propertyMultimeterOffsetX = config.get(CATEGORY_NAME_MULTIMETER, "MultimeterOffsetX", 6);
		propertyMultimeterOffsetX.setMinValue(0);
		propertyMultimeterOffsetX.setComment("The offset from the left hand side when using the Multimeter");
		propertyMultimeterOffsetX.setLanguageKey("gui.config.multimeter.offset_x.name");

		Property propertyMultimeterOffsetY = config.get(CATEGORY_NAME_MULTIMETER, "MultimeterOffsetY", 7);
		propertyMultimeterOffsetY.setMinValue(0);
		propertyMultimeterOffsetY.setComment("The offset from the bottom when using the Multimeter");
		propertyMultimeterOffsetY.setLanguageKey("gui.config.multimeter.offset_y.name");

		Property propertyMultimeterEnergyWidth = config.get(CATEGORY_NAME_MULTIMETER, "MultimeterEnergyWidth", ElementEnergyBar.DEFAULT_WIDTH);
		propertyMultimeterEnergyWidth.setMinValue(1);
		propertyMultimeterEnergyWidth.setMaxValue(ElementEnergyBar.DEFAULT_WIDTH);
		propertyMultimeterEnergyWidth.setComment("The width of the multimeter energy gui");
		propertyMultimeterEnergyWidth.setLanguageKey("gui.config.multimeter.energy_width.name");

		Property propertyMultimeterEnergyHeight = config.get(CATEGORY_NAME_MULTIMETER, "MultimeterEnergyHeight",
				ElementEnergyBar.DEFAULT_HEIGHT);
		propertyMultimeterEnergyHeight.setMinValue(1);
		propertyMultimeterEnergyHeight.setMaxValue(ElementEnergyBar.DEFAULT_HEIGHT);
		propertyMultimeterEnergyHeight.setComment("The height of the multimeter energy gui");
		propertyMultimeterEnergyHeight.setLanguageKey("gui.config.multimeter.energy_height.name");
		
		Property propertyMultimeterItemMaxColumns = config.get(CATEGORY_NAME_MULTIMETER, "MultimeterItemMaxColumnst",
				9);
		propertyMultimeterItemMaxColumns.setMinValue(1);
		propertyMultimeterItemMaxColumns.setComment("The maximum amount of columns for the item multimeter");
		propertyMultimeterItemMaxColumns.setLanguageKey("gui.config.multimeter.item_max_columns.name");
		
		List<String> propertyOrderMultimeter = new ArrayList<String>();
		propertyOrderMultimeter.add(propertyMultimeterOffsetX.getName());
		propertyOrderMultimeter.add(propertyMultimeterOffsetY.getName());
		propertyOrderMultimeter.add(propertyMultimeterEnergyWidth.getName());
		propertyOrderMultimeter.add(propertyMultimeterEnergyHeight.getName());
		propertyOrderMultimeter.add(propertyMultimeterItemMaxColumns.getName());
		
		if (readFieldsFromConfig) {
			/*
			 * Energy Config
			 */
			DEFAULT_ENERGY_UNIT = EnergyUnits.byUnlocalizedName(propertyDefaultEnergyUnit.getString());
			ENERGY_BAR_SHOW_CAPACITY = propertyEnergyBarShowCapacity.getBoolean();
			ENERGY_BAR_SIMPLIFY_ENERGY = propertyEnergyBarSimplifyEnergy.getBoolean();

			/*
			 * Update Checkers Config
			 */
			for (Pair<String, Property> mod : UPDATE_CHECKER_MOD_PROPERTIES) {
				UPDATE_CHECKER_MODS.remove(mod.getLeft());
				UPDATE_CHECKER_MODS.put(mod.getLeft(), mod.getRight().getBoolean());
			}
			
			/*
			 * Multimeter Config
			 */
			MULTIMETER_OFFSET_X = propertyMultimeterOffsetX.getInt();
			MULTIMETER_OFFSET_Y = propertyMultimeterOffsetY.getInt();
			MULTIMETER_ENERGY_WIDTH = propertyMultimeterEnergyWidth.getInt();
			MULTIMETER_ENERGY_HEIGHT = propertyMultimeterEnergyHeight.getInt();
			MULTIMETER_ITEM_MAX_COLUMNS = propertyMultimeterItemMaxColumns.getInt();
		}

		/*
		 * Energy Config
		 */
		propertyDefaultEnergyUnit.set(DEFAULT_ENERGY_UNIT.getUnlocalizedName());
		propertyEnergyBarShowCapacity.set(ENERGY_BAR_SHOW_CAPACITY);
		propertyEnergyBarSimplifyEnergy.set(ENERGY_BAR_SIMPLIFY_ENERGY);

		/*
		 * Update Checkers Config
		 */
		for (Pair<String, Property> mod : UPDATE_CHECKER_MOD_PROPERTIES) {
			mod.getRight().set(UPDATE_CHECKER_MODS.get(mod.getLeft()));
		}
		
		/*
		 * Multimeter Config
		 */
		propertyMultimeterOffsetX.set(MULTIMETER_OFFSET_X);
		propertyMultimeterOffsetY.set(MULTIMETER_OFFSET_Y);
		propertyMultimeterEnergyWidth.set(MULTIMETER_ENERGY_WIDTH);
		propertyMultimeterEnergyHeight.set(MULTIMETER_ENERGY_HEIGHT);
		propertyMultimeterItemMaxColumns.set(MULTIMETER_ITEM_MAX_COLUMNS);
		
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
