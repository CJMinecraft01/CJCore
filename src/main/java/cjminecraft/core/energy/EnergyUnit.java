package cjminecraft.core.energy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.text.WordUtils;
import org.lwjgl.util.Color;
import org.lwjgl.util.ReadableColor;

import cjminecraft.core.CJCore;
import cjminecraft.core.client.gui.element.ElementEnergyBar;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Handler of all the different types of {@link EnergyUnit}
 * 
 * @author CJMinecraft
 *
 */
public enum EnergyUnit implements IStringSerializable {

	REDSTONE_FLUX("redstone_flux", 10.0D, Color.RED), 
	TESLA("tesla", 10, Color.CYAN), 
	FORGE_ENERGY("forge_energy", 10, Color.ORANGE), 
	JOULES("joules", 4, Color.GREEN), 
	MINECRAFT_JOULES("minecraft_joules", 1, Color.YELLOW), 
	ENERGY_UNIT("energy_unit", 2.5D, Color.BLUE); // The IC2 Unit
	
	public static List<EnergyUnit> VALUES = Arrays.asList(values());

	/**
	 * The name and representation of the energy unit
	 */
	private String unlocalizedName;
	/**
	 * The multiplier to convert to the unit. The multiplier is how to convert
	 * to {@link EnergyUnit#MINECRAFT_JOULES} 10RF = 1MJ 10T = 1MJ 10FE = 1MJ
	 * 4J = 1MJ 5/2EU = 1MJ
	 */
	private double multiplier;
	/**
	 * The colour as its individual values. colour[0] = red colour[1] = green
	 * colour[2] = blue
	 */
	private float[] colour;

	/**
	 * Create a energy unit
	 * 
	 * @param unlocalizedName
	 *            The unlocalized name of the energy unit. You will need to add
	 *            <code>energy.unit.</code><strong>unlocalizedName</strong><code>.name</code>
	 *            and
	 *            <code>energy.unit.</code><strong>unlocalizedName</strong><code>.suffix</code>
	 *            to your language file
	 * @param multiplier
	 *            The multiplier to convert to the unit from
	 *            {@link EnergyUnit#MINECRAFT_JOULES} 10RF = 1MJ 10FE = 1MJ 4J
	 *            = 1MJ 6EU = 1MJ
	 * @param colour
	 *            The colour of the {@link ElementEnergyBar}
	 */
	private EnergyUnit(String unlocalizedName, double multiplier, ReadableColor colour) {
		this.unlocalizedName = unlocalizedName;
		this.multiplier = multiplier;
		this.colour = new float[] { colour.getRed() / 255.0F, colour.getGreen() / 255.0F,
				colour.getGreen() / 255.0F };
	}
	
	/**
	 * Create a energy unit
	 * 
	 * @param unlocalizedName
	 *            The unlocalized name of the energy unit. You will need to add
	 *            <code>energy.unit.</code><strong>unlocalizedName</strong><code>.name</code>
	 *            and
	 *            <code>energy.unit.</code><strong>unlocalizedName</strong><code>.suffix</code>
	 *            to your language file
	 * @param multiplier
	 *            The multiplier to convert to the unit from
	 *            {@link EnergyUnit#MINECRAFT_JOULES} 10RF = 1MJ 10FE = 1MJ 4J
	 *            = 1MJ 6EU = 1MJ
	 * @param colour
	 *            The colour of the {@link ElementEnergyBar}
	 */
	private EnergyUnit(String unlocalizedName, double multiplier, int colour) {
		this.unlocalizedName = unlocalizedName;
		this.multiplier = multiplier;
		this.colour = new float[] { (colour >> 15 & 255) / 255.0F, (colour >> 8 & 255) / 255.0F,
				(colour & 255) / 255.0F };
	}

	/**
	 * Create a energy unit
	 * 
	 * @param unlocalizedName
	 *            The unlocalized name of the energy unit. You will need to add
	 *            <code>energy.unit.</code><strong>unlocalizedName</strong><code>.name</code>
	 *            and
	 *            <code>energy.unit.</code><strong>unlocalizedName</strong><code>.suffix</code>
	 *            to your language file
	 * @param multiplier
	 *            The multiplier to convert to the unit from
	 *            {@link EnergyUnit#MINECRAFT_JOULES} 10RF = 1MJ 10FE = 1MJ 4J
	 *            = 1MJ 6EU = 1MJ
	 * @param colour
	 *            The colour of the {@link ElementEnergyBar}
	 */
	private EnergyUnit(String unlocalizedName, double multiplier, float[] colour) {
		this.unlocalizedName = unlocalizedName;
		this.multiplier = multiplier;
		this.colour = colour;
	}

	public String getUnlocalizedName() {
		return unlocalizedName;
	}

	@Override
	public String getName() {
		return getUnlocalizedName();
	}

	public String getLocalizedName() {
		if (FMLCommonHandler.instance().getSide() == Side.CLIENT)
			return I18n.format("energy.unit." + unlocalizedName + ".name");
		return WordUtils.capitalize(unlocalizedName.replace('_', ' '));
	}

	public String getSuffix() {
		if (FMLCommonHandler.instance().getSide() == Side.CLIENT)
			return I18n.format("energy.unit." + unlocalizedName + ".suffix");
		String initials = "";
		for (String word : unlocalizedName.split("_"))
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
		this.colour = new float[] { (colour >> 15 & 255) / 255.0F, (colour >> 8 & 255) / 255.0F,
				(colour & 255) / 255.0F };
	}

	/**
	 * Returns the next energy unit in the registered energy units
	 * 
	 * @return The next unit
	 */
	public EnergyUnit cycleUnit() {
		if (VALUES.indexOf(this) + 1 > VALUES.size() - 1)
			return VALUES.get(0);
		return VALUES.get(VALUES.indexOf(this) + 1);
	}

	@Override
	public String toString() {
		return super.toString();
	}

	/**
	 * Gets the {@link EnergyUnit} by its unlocalized name
	 * 
	 * @param unlocalizedName
	 *            The unit's unlocalized name
	 * @return 
	 * @return The {@link EnergyUnit}
	 */
	public static EnergyUnit byUnlocalizedName(String unlocalizedName) {
		for (EnergyUnit unit : VALUES)
			if (unit.unlocalizedName.equalsIgnoreCase(unlocalizedName))
				return unit;
		return EnergyUnit.MINECRAFT_JOULES;
	}

}
