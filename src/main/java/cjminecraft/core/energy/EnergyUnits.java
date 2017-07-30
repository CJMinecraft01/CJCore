package cjminecraft.core.energy;

import cjminecraft.core.CJCore;
import cjminecraft.core.client.gui.EnergyBar;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.commons.lang3.text.WordUtils;
import org.lwjgl.util.Color;
import org.lwjgl.util.ReadableColor;

import java.util.ArrayList;
import java.util.List;

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
		 * convert to {@link EnergyUnits#MINECRAFT_JOULES}
		 * 		10RF = 1MJ
		 * 		10T = 1MJ
		 * 		10FE = 1MJ
		 * 		4J = 1MJ
		 * 		6EU = 1MJ
		 */
		private int multiplier;
		/**
		 * The colour as its individual values. colour[0] = red colour[1] =
		 * green colour[2] = blue
		 */
		private int[] colour;

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
		 *            {@link EnergyUnits#MINECRAFT_JOULES}
         *            10RF = 1MJ
         *            10FE = 1MJ
		 *            4J = 1MJ
         *            6EU = 1MJ
		 * @param colour
		 *            The colour of the {@link EnergyBar}
		 */
		private EnergyUnit(String unlocalizedName, int multiplier, int colour) {
			this.unlocalizedName = unlocalizedName;
			this.multiplier = multiplier;
			this.colour = new int[] { colour >> 15 & 255, colour >> 8 & 255, colour & 255 };
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
		 *            {@link EnergyUnits#MINECRAFT_JOULES}
         *            10RF = 1MJ
         *            10FE = 1MJ
		 *            4J = 1MJ
         *            6EU = 1MJ
		 * @param colour
		 *            The colour of the {@link EnergyBar}
		 */
		private EnergyUnit(String unlocalizedName, int multiplier, int[] colour) {
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
			StringBuilder initials = new StringBuilder();
			for(String word : unlocalizedName.split("_"))
				initials.append(word.charAt(0));
			return initials.toString().toUpperCase();
		}

		public int getMultiplier() {
			return multiplier;
		}

		public int[] getColour() {
			return colour;
		}

		public void setColour(ReadableColor colour) {
			setColour(colour.getRed(), colour.getGreen(), colour.getBlue());
		}
		
		public void setColour(int r, int g, int b) {
			this.colour = new int[] { r, g, b };
		}

		public void setColour(int colour) {
			this.colour = new int[] { colour >> 15 & 255, colour >> 8 & 255, colour & 255 };
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
	private static List<EnergyUnit> energyUnits = new ArrayList<>();

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
	 *            {@link EnergyUnits#MINECRAFT_JOULES}
     *            10RF = 1MJ
     *            10FE = 1MJ
     *            4J = 1MJ
     *            6EU = 1MJ
	 * @param colour
	 *            The colour of the {@link EnergyBar}
	 * @return The registered energy unit
	 */
	public static EnergyUnit createEnergyUnit(String unlocalizedName, int multiplier, int colour) {
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
	 *            {@link EnergyUnits#MINECRAFT_JOULES}
     *            10RF = 1MJ
     *            10FE = 1MJ
     *            4J = 1MJ
     *            6EU = 1MJ
	 * @param colour
	 *            The colour of the {@link EnergyBar}
	 * @return The registered energy unit
	 */
	public static EnergyUnit createEnergyUnit(String unlocalizedName, int multiplier, ReadableColor colour) {
		EnergyUnit unit = new EnergyUnit(unlocalizedName, multiplier,
				new int[] { colour.getRed(), colour.getGreen(), colour.getBlue() });
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
		return EnergyUnits.REDSTONE_FLUX;
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
		ENERGY_UNIT = createEnergyUnit("energy_unit", 6, Color.BLUE);
	}

}
