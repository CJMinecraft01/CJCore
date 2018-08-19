package cjminecraft.core.config;

import java.util.HashMap;

import cjminecraft.core.CJCore;
import cjminecraft.core.client.gui.element.ElementEnergyBar;
import cjminecraft.core.client.gui.element.ElementFluidBar;
import cjminecraft.core.energy.EnergyUnit;
import net.minecraftforge.common.config.Config;

@Config(modid = CJCore.MODID)
public class CJCoreConfig {
	
	@Config.LangKey("gui.config.category.energy")
	public static Energy ENERGY = new Energy();
	@Config.LangKey("gui.config.category.fluid")
	public static Fluids FLUIDS = new Fluids();
	@Config.LangKey("gui.config.category.multimeter")
	public static Multimeter MULTIMETER = new Multimeter();
	
	public static class Energy {
		
		/**
		 * The default {@link EnergyUnit}
		 */
		@Config.Comment("The energy unit which will be utilised by any Energy Bar which uses CJCore")
		@Config.LangKey("gui.config.energy.default_energy_unit.name")
		public EnergyUnit DEFAULT_ENERGY_UNIT = EnergyUnit.FORGE_ENERGY;

		/**
		 * Whether or not the energy bar should show capacity
		 */
		@Config.Comment("Whether the energy bar should display the capacity")
		@Config.LangKey("gui.config.energy.energy_bar_show_capacity.name")
		public boolean ENERGY_BAR_SHOW_CAPACITY = false;

		/**
		 * Whether the energy bar should simplify the energy displayed
		 */
		@Config.Comment("Whether the energy bar should simplify the format which the energy is displayed in")
		@Config.LangKey("gui.config.energy.energy_bar_simplify_energy.name")
		public boolean ENERGY_BAR_SIMPLIFY_ENERGY = false;

	}
	
	public static class Fluids {
		
		/**
		 * Whether or not the fluid bar should show capacity
		 */
		@Config.Comment("Whether the energy bar should show the capacity")
		@Config.LangKey("gui.config.fluid.fluid_bar_show_capacity.name")
		public boolean FLUID_BAR_SHOW_CAPACITY = false;

		/**
		 * Whether the fluid bar should simplify the fluid displayed
		 */
		@Config.Comment("Whether the energy bar should simplify the format of the fluid values")
		@Config.LangKey("gui.config.fluid.fluid_bar_simplify_fluids.name")
		public boolean FLUID_BAR_SIMPLIFY_FLUIDS = false;

	}

	public static class Multimeter {
		
		/**
		 * The position of the multimeter on the X axis from the left hand side
		 */
		@Config.Comment("The offset from the left hand side when using the multimeter")
		@Config.LangKey("gui.config.multimeter.offset_x.name")
		@Config.RangeInt(min = 0)
		public int MULTIMETER_OFFSET_X = 6;

		/**
		 * The position of the multimeter on the Y axis from the bottom
		 */
		@Config.Comment("The offset from the bottom of the screen when using the multimeter")
		@Config.LangKey("gui.config.multimeter.offset_y.name")
		@Config.RangeInt(min = 0)
		public int MULTIMETER_OFFSET_Y = 7;

		/**
		 * The width of the energy multimeter
		 */
		@Config.Comment("The width of the multimeter energy gui")
		@Config.LangKey("gui.config.multimeter.energy_width.name")
		@Config.RangeInt(min = 1, max = ElementEnergyBar.DEFAULT_WIDTH)
		public int MULTIMETER_ENERGY_WIDTH = ElementEnergyBar.DEFAULT_WIDTH;

		/**
		 * The height of the energy multimeter
		 */
		@Config.Comment("The height of the multimeter energy gui")
		@Config.LangKey("gui.config.multimeter.energy_height.name")
		@Config.RangeInt(min = 1, max = ElementEnergyBar.DEFAULT_HEIGHT)
		public int MULTIMETER_ENERGY_HEIGHT = ElementEnergyBar.DEFAULT_HEIGHT;
		
		/**
		 * The width of the fluid multimeter
		 */
		@Config.Comment("The width of the multimeter fluid gui")
		@Config.LangKey("gui.config.multimeter.fluid_width.name")
		@Config.RangeInt(min = 1, max = ElementFluidBar.DEFAULT_WIDTH)
		public int MULTIMETER_FLUID_WIDTH = ElementFluidBar.DEFAULT_WIDTH;
		
		/**
		 * The height of the fluid multimeter
		 */
		@Config.Comment("The height of the multimeter fluid gui")
		@Config.LangKey("gui.config.multimeter.fluid_height.name")
		@Config.RangeInt(min = 1, max = ElementFluidBar.DEFAULT_HEIGHT)
		public int MULTIMETER_FLUID_HEIGHT = ElementFluidBar.DEFAULT_HEIGHT;
		
		/**
		 * The maximum number of columns for the item multimeter
		 */
		@Config.Comment("The maximum amount of columns for the item multimeter")
		@Config.LangKey("gui.config.multimeter.item_max_columns.name")
		@Config.RangeInt(min = 1)
		public int MULTIMETER_ITEM_MAX_COLUMNS = 9;

	}

}
