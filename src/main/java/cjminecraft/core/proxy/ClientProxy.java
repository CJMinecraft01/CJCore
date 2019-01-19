package cjminecraft.core.proxy;

import org.lwjgl.util.Color;

import cjminecraft.core.energy.EnergyUnit;
import cjminecraft.core.util.registries.AutomaticRegistrar;

/**
 * For all things client
 * @author CJMinecraft
 *
 */
public class ClientProxy extends CommonProxy {
	
	@Override
	public void preInit() {
		super.preInit();
		AutomaticRegistrar.registerModels();
		EnergyUnit.REDSTONE_FLUX.setColour(Color.RED.getRed(), Color.RED.getGreen(), Color.RED.getBlue());
		EnergyUnit.TESLA.setColour(Color.CYAN.getRed(), Color.CYAN.getGreen(), Color.CYAN.getBlue());
		EnergyUnit.FORGE_ENERGY.setColour(Color.ORANGE.getRed(), Color.ORANGE.getGreen(), Color.ORANGE.getBlue());
		EnergyUnit.JOULES.setColour(Color.GREEN.getRed(), Color.GREEN.getGreen(), Color.GREEN.getBlue());
		EnergyUnit.MINECRAFT_JOULES.setColour(Color.YELLOW.getRed(), Color.YELLOW.getGreen(), Color.YELLOW.getBlue());
		EnergyUnit.ENERGY_UNIT.setColour(Color.BLUE.getRed(), Color.BLUE.getGreen(), Color.BLUE.getBlue());
	}
	
	@Override
	public void init() {
		super.init();
	}
	
	@Override
	public void postInit() {
		super.postInit();
	}

}
