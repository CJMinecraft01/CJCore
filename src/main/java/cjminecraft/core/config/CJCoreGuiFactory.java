package cjminecraft.core.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;

import cjminecraft.core.CJCore;
import cjminecraft.core.energy.EnergyUnits;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiConfigEntries.CategoryEntry;
import net.minecraftforge.fml.client.config.IConfigElement;

/**
 * The {@link IModGuiFactory} for {@link CJCore}
 * 
 * @author Callum
 *
 */
public class CJCoreGuiFactory implements IModGuiFactory {

	/**
	 * Not used
	 */
	@Override
	public void initialize(Minecraft minecraftInstance) {
	}

	/**
	 * Says what our {@link GuiConfig} class is
	 */
	@Override
	public Class<? extends GuiScreen> mainConfigGuiClass() {
		return CJCoreConfigGui.class;
	}

	/**
	 * No runtime gui categories
	 */
	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		return null;
	}

	/**
	 * Deprecated
	 */
	@Override
	public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {
		return null;
	}

	/**
	 * The actual {@link GuiConfig}
	 * 
	 * @author CJMinecraft
	 *
	 */
	public static class CJCoreConfigGui extends GuiConfig {

		/**
		 * Initializes the config gui
		 * 
		 * @param parentScreen
		 *            The screen before this one
		 */
		public CJCoreConfigGui(GuiScreen parentScreen) {
			super(parentScreen, getConfigElements(), CJCore.MODID, false, false, I18n.format("gui.config.main_title"));
		}

		/**
		 * All of the elements to be draw (the categories)
		 * 
		 * @return A list of the elements to draw
		 */
		private static List<IConfigElement> getConfigElements() {
			List<IConfigElement> list = new ArrayList<IConfigElement>();
			list.add(new DummyCategoryElement(I18n.format("gui.config.category.energy"), "gui.config.category.energy",
					CategoryEntryEnergy.class));
			list.add(new DummyCategoryElement(I18n.format("gui.config.category.update_checker"),
					"gui.config.category.update_checker", CategoryEntryVersionChecker.class));
			return list;
		}

		/**
		 * All of the energy configurations
		 * 
		 * @author CJMinecraft
		 *
		 */
		public static class CategoryEntryEnergy extends CategoryEntry {

			public CategoryEntryEnergy(GuiConfig owningScreen, GuiConfigEntries owningEntryList,
					IConfigElement configElement) {
				super(owningScreen, owningEntryList, configElement);
			}

			@Override
			protected GuiScreen buildChildScreen() {
				Configuration config = CJCoreConfig.getConfig();
				ConfigElement category_energy = new ConfigElement(
						config.getCategory(CJCoreConfig.CATEGORY_NAME_ENERGY));
				List<IConfigElement> propertiesOnThisScreen = category_energy.getChildElements();
				String windowTitle = I18n.format("gui.config.category.energy");
				return new GuiConfig(this.owningScreen, propertiesOnThisScreen, this.owningScreen.modID,
						CJCoreConfig.CATEGORY_NAME_ENERGY,
						this.configElement.requiresWorldRestart() || this.owningScreen.allRequireWorldRestart,
						this.configElement.requiresMcRestart() || this.owningScreen.allRequireMcRestart, windowTitle);
			}

		}

		public static class CategoryEntryVersionChecker extends CategoryEntry {

			public CategoryEntryVersionChecker(GuiConfig owningScreen, GuiConfigEntries owningEntryList,
					IConfigElement configElement) {
				super(owningScreen, owningEntryList, configElement);
			}

			@Override
			protected GuiScreen buildChildScreen() {
				Configuration config = CJCoreConfig.getConfig();
				ConfigElement category_version_checker = new ConfigElement(
						config.getCategory(CJCoreConfig.CATEGORY_NAME_VERSION_CHECKER));
				List<IConfigElement> propertiesOnThisScreen = category_version_checker.getChildElements();
				String windowTitle = I18n.format("gui.config.category.update_checker");
				return new GuiConfig(this.owningScreen, propertiesOnThisScreen, this.owningScreen.modID,
						CJCoreConfig.CATEGORY_NAME_VERSION_CHECKER,
						this.configElement.requiresWorldRestart() || this.owningScreen.allRequireWorldRestart,
						this.configElement.requiresMcRestart() || this.owningScreen.allRequireMcRestart, windowTitle);
			}

		}

	}

	@Override
	public boolean hasConfigGui() {
		return true;
	}

	@Override
	public GuiScreen createConfigGui(GuiScreen parentScreen) {
		return new CJCoreConfigGui(parentScreen);
	}

}
