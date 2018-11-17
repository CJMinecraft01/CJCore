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

}
