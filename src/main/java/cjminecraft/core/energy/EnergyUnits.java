package cjminecraft.core.energy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.text.WordUtils;
import org.lwjgl.util.Color;
import org.lwjgl.util.ReadableColor;

import cjminecraft.core.CJCore;
import cjminecraft.core.client.gui.EnergyBar;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Handler of all the different types of {@link EnergyUnit}
 * 
 * @author CJMinecraft
 *
 */
public class EnergyUnits {

	/**
	 * Class which represents an energy unit
	 * 
	 * @author CJMinecraft
	 *
	 */
	public static class EnergyUnit {

		/**
		 * The name and representation of the energy unit
		 */
		private String unlocalizedName;
		/**
		 * The multiplier to convert to the unit. The multiplier is how to
		 * convert to {@link EnergyUnits#MINECRAFT_JOULES} 10RF = 1MJ 10T = 1MJ
		 * 10FE = 1MJ 4J = 1MJ 5/2EU = 1MJ
		 */
		private double multiplier;
		/**
		 * The colour as its individual values. colour[0] = red colour[1] =
		 * green colour[2] = blue
		 */
		private float[] colour;

		/**
		 * Create a energy unit
		 * 
		 * @param unlocalizedName
		 *            The unlocalized name of the energy unit. You will need to
		 *            add
		 *            <code>energy.unit.</code><strong>unlocalizedName</strong><code>.name</code>
		 *            and
		 *            <code>energy.unit.</code><strong>unlocalizedName</strong><code>.suffix</code>
		 *            to your language file
		 * @param multiplier
		 *            The multiplier to convert to the unit from
		 *            {@link EnergyUnits#MINECRAFT_JOULES} 10RF = 1MJ 10FE = 1MJ
		 *            4J = 1MJ 6EU = 1MJ
		 * @param colour
		 *            The colour of the {@link EnergyBar}
		 */
		private EnergyUnit(String unlocalizedName, double multiplier, int colour) {
			this.unlocalizedName = unlocalizedName;
			this.multiplier = multiplier;
			this.colour = new float[] { (colour >> 15 & 255) / 255.0F, (colour >> 8 & 255) / 255.0F, (colour & 255) / 255.0F };
		}

		/**
		 * Create a energy unit
		 * 
		 * @param unlocalizedName
		 *            The unlocalized name of the energy unit. You will need to
		 *            add
		 *            <code>energy.unit.</code><strong>unlocalizedName</strong><code>.name</code>
		 *            and
		 *            <code>energy.unit.</code><strong>unlocalizedName</strong><code>.suffix</code>
		 *            to your language file
		 * @param multiplier
		 *            The multiplier to convert to the unit from
		 *            {@link EnergyUnits#MINECRAFT_JOULES} 10RF = 1MJ 10FE = 1MJ
		 *            4J = 1MJ 6EU = 1MJ
		 * @param colour
		 *            The colour of the {@link EnergyBar}
		 */
		private EnergyUnit(String unlocalizedName, double multiplier, float[] colour) {
			this.unlocalizedName = unlocalizedName;
			this.multiplier = multiplier;
			this.colour = colour;
		}

		public String getUnlocalizedName() {
			return unlocalizedName;
		}

		public String getName() {
			if(FMLCommonHandler.instance().getSide() == Side.CLIENT)
				return I18n.format("energy.unit." + unlocalizedName + ".name");
			return WordUtils.capitalize(unlocalizedName.replace('_', ' '));
		}

		public String getSuffix() {
			if(FMLCommonHandler.instance().getSide() == Side.CLIENT)
				return I18n.format("energy.unit." + unlocalizedName + ".suffix");
			String initials = "";
			for(String word : unlocalizedName.split("_"))
				initials += word.charAt(0);
			return initials.toUpperCase();
		}

		public double getMultiplier() {
			return multiplier;
		}

		public float[] getColour() {
			return colour;
		}

		public void setColour(ReadableColor colour) {
			setColour(colour.getRed(), colour.getGreen(), colour.getBlue());
		}
		
		public void setColour(int r, int g, int b) {
			this.colour = new float[] { r / 255.0F, g / 255.0F, b / 255.0F };
		}
		
		public void setColour(float r, float g, float b) {
			this.colour = new float[] { r, g, b };
		}

		public void setColour(int colour) {
			this.colour = new float[] { (colour >> 15 & 255) / 255.0F, (colour >> 8 & 255) / 255.0F, (colour & 255) / 255.0F };
		}

		/**
		 * Returns the next energy unit in the registered energy units
		 * 
		 * @return The next unit
		 */
		public EnergyUnit cycleUnit() {
			if (energyUnits.indexOf(this) + 1 > energyUnits.size() - 1)
				return energyUnits.get(0);
			return energyUnits.get(energyUnits.indexOf(this) + 1);
		}

	}

	/**
	 * List of energy units
	 */
	private static List<EnergyUnit> energyUnits = new ArrayList<EnergyUnit>();

	/**
	 * Register an energy unit
	 * 
	 * @param unlocalizedName
	 *            The unlocalized name of the energy unit. You will need to add
	 *            <code>energy.unit.</code><strong>unlocalizedName</strong><code>.name</code>
	 *            and
	 *            <code>energy.unit.</code><strong>unlocalizedName</strong><code>.suffix</code>
	 *            to your language file
	 * @param multiplier
	 *            The multiplier to convert to the unit from
	 *            {@link EnergyUnits#MINECRAFT_JOULES} 10RF = 1MJ 10FE = 1MJ 4J
	 *            = 1MJ 6EU = 1MJ
	 * @param colour
	 *            The colour of the {@link EnergyBar}
	 * @return The registered energy unit
	 */
	public static EnergyUnit createEnergyUnit(String unlocalizedName, double multiplier, int colour) {
		EnergyUnit unit = new EnergyUnit(unlocalizedName, multiplier, colour);
		for (EnergyUnit u : energyUnits) {
			if (u.unlocalizedName.equalsIgnoreCase(unit.unlocalizedName)) {
				CJCore.logger.warn(String.format("An energy unit of type %s has already been registered - SKIPPING", unlocalizedName));
				return u;
			}
		}
		energyUnits.add(unit);
		CJCore.logger.info(String.format("Successfully registered energy unit %s", unlocalizedName));
		return unit;
	}

	/**
	 * Register an energy unit
	 * 
	 * @param unlocalizedName
	 *            The unlocalized name of the energy unit. You will need to add
	 *            <code>energy.unit.</code><strong>unlocalizedName</strong><code>.name</code>
	 *            and
	 *            <code>energy.unit.</code><strong>unlocalizedName</strong><code>.suffix</code>
	 *            to your language file
	 * @param multiplier
	 *            The multiplier to convert to the unit from
	 *            {@link EnergyUnits#MINECRAFT_JOULES} 10RF = 1MJ 10FE = 1MJ 4J
	 *            = 1MJ 6EU = 1MJ
	 * @param colour
	 *            The colour of the {@link EnergyBar}
	 * @return The registered energy unit
	 */
	public static EnergyUnit createEnergyUnit(String unlocalizedName, double multiplier, ReadableColor colour) {
		EnergyUnit unit = new EnergyUnit(unlocalizedName, multiplier,
				new float[] { colour.getRed() / 255.0F, colour.getGreen() / 255.0F, colour.getBlue() / 255.0F });
		for (EnergyUnit u : energyUnits) {
			if (u.unlocalizedName.equalsIgnoreCase(unit.unlocalizedName)) {
				CJCore.logger.warn(String.format("An energy unit of type %s has already been registered - SKIPPING", unlocalizedName));
				return u;
			}
		}
		energyUnits.add(unit);
		CJCore.logger.info(String.format("Successfully registered energy unit %s", unlocalizedName));
		return unit;
	}

	/**
	 * Returns the list of registered energy units
	 * 
	 * @return the list of registered energy units
	 */
	public static List<EnergyUnit> getEnergyUnits() {
		return energyUnits;
	}

	/**
	 * Gets the {@link EnergyUnit} by its unlocalized name
	 * 
	 * @param unlocalizedName
	 *            The unit's unlocalized name
	 * @return The {@link EnergyUnit}
	 */
	public static EnergyUnit byUnlocalizedName(String unlocalizedName) {
		for (EnergyUnit unit : energyUnits)
			if (unit.unlocalizedName.equalsIgnoreCase(unlocalizedName))
				return unit;
		return EnergyUnits.MINECRAFT_JOULES;
	}

	public static EnergyUnit REDSTONE_FLUX;
	public static EnergyUnit TESLA;
	public static EnergyUnit FORGE_ENERGY;
	public static EnergyUnit JOULES;
	public static EnergyUnit MINECRAFT_JOULES;
	public static EnergyUnit ENERGY_UNIT; //The IC2 Unit

	/**
	 * Should not be called outside of {@link CJCore}
	 */
	public static void preInit() {
		REDSTONE_FLUX = createEnergyUnit("redstone_flux", 10, Color.RED);
		TESLA = createEnergyUnit("tesla", 10, Color.CYAN);
		FORGE_ENERGY = createEnergyUnit("forge_energy", 10, Color.ORANGE);
		JOULES = createEnergyUnit("joules", 4, Color.GREEN);
		MINECRAFT_JOULES = createEnergyUnit("minecraft_joules", 1, Color.YELLOW);
		ENERGY_UNIT = createEnergyUnit("energy_unit", 2.5D, Color.BLUE);
	}

}
