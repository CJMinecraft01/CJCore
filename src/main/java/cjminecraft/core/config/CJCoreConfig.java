package cjminecraft.core.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cjminecraft.core.CJCore;
import cjminecraft.core.energy.EnergyUnits;
import cjminecraft.core.energy.EnergyUnits.EnergyUnit;
import cjminecraft.core.proxy.ClientProxy;
import net.minecraft.client.resources.I18n;
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
	 * The default {@link EnergyUnit}
	 */
	public static EnergyUnit DEFAULT_ENERGY_UNIT;
	
	public static int MULTIMETER_OFFSET_X;
	public static int MULTIMETER_OFFSET_Y;
	
	public static boolean MULTIMETER_SHOW_CAPACITY;

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

		List<String> energyUnits = new ArrayList<String>();
		EnergyUnits.getEnergyUnits().forEach(unit -> {
			if(unit.getUnlocalizedName() != "energy_base")
				energyUnits.add(unit.getName());
		});
		Property propertyDefaultEnergyUnit = config.get(CATEGORY_NAME_ENERGY, "DefaultEnergyUnit",
				EnergyUnits.FORGE_ENERGY.getUnlocalizedName());
		propertyDefaultEnergyUnit.setValidValues(energyUnits.toArray(new String[0]));
		propertyDefaultEnergyUnit.setComment(I18n.format("gui.config.energy.default_energy_unit.comment"));
		propertyDefaultEnergyUnit.setLanguageKey("gui.config.energy.default_energy_unit.name");
		
		Property propertyMultimeterOffsetX = config.get(CATEGORY_NAME_ENERGY, "MultimeterOffsetX", 6);
		propertyMultimeterOffsetX.setMinValue(0);
		propertyMultimeterOffsetX.setComment(I18n.format("gui.config.energy.multimeter_offset_x.comment"));
		propertyMultimeterOffsetX.setLanguageKey("gui.config.energy.multimeter_offset_x.name");
		
		Property propertyMultimeterOffsetY = config.get(CATEGORY_NAME_ENERGY, "MultimeterOffsetY", 7);
		propertyMultimeterOffsetY.setMinValue(0);
		propertyMultimeterOffsetY.setComment(I18n.format("gui.config.energy.multimeter_offset_y.comment"));
		propertyMultimeterOffsetY.setLanguageKey("gui.config.energy.multimeter_offset_y.name");
		
		Property propertyMultimeterShowCapacity = config.get(CATEGORY_NAME_ENERGY, "MultimeterShowCapacity", false);
		propertyMultimeterShowCapacity.setComment(I18n.format("gui.config.energy.multimeter_show_capacity.comment"));
		propertyMultimeterShowCapacity.setLanguageKey("gui.config.energy.multimeter_show_capacity.name");

		List<String> propertyOrderEnergy = new ArrayList<String>();
		propertyOrderEnergy.add(propertyDefaultEnergyUnit.getName());
		propertyOrderEnergy.add(propertyMultimeterOffsetX.getName());
		propertyOrderEnergy.add(propertyMultimeterOffsetY.getName());
		propertyOrderEnergy.add(propertyMultimeterShowCapacity.getName());
		config.setCategoryPropertyOrder(CATEGORY_NAME_ENERGY, propertyOrderEnergy);
		
		if (readFieldsFromConfig) {
			for (EnergyUnit unit : EnergyUnits.getEnergyUnits())
				if (unit.getUnlocalizedName().equalsIgnoreCase(propertyDefaultEnergyUnit.getString()) || unit.getName().equalsIgnoreCase(propertyDefaultEnergyUnit.getString()))
					DEFAULT_ENERGY_UNIT = unit;
			if (DEFAULT_ENERGY_UNIT == null) {
				DEFAULT_ENERGY_UNIT = EnergyUnits.FORGE_ENERGY;
			}
			MULTIMETER_OFFSET_X = propertyMultimeterOffsetX.getInt();
			MULTIMETER_OFFSET_Y = propertyMultimeterOffsetY.getInt();
			MULTIMETER_SHOW_CAPACITY = propertyMultimeterShowCapacity.getBoolean();
		}
		
		propertyDefaultEnergyUnit.set(DEFAULT_ENERGY_UNIT.getUnlocalizedName());
		propertyMultimeterOffsetX.set(MULTIMETER_OFFSET_X);
		propertyMultimeterOffsetY.set(MULTIMETER_OFFSET_Y);
		propertyMultimeterShowCapacity.set(MULTIMETER_SHOW_CAPACITY);

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
