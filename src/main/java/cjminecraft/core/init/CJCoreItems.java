package cjminecraft.core.init;

import cjminecraft.core.CJCore;
import cjminecraft.core.items.ItemMultimeter;
import cjminecraft.core.util.registries.Register;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Handles all of {@link CJCore}s items
 * 
 * @author CJMinecraft
 *
 */
@Register(modid = CJCore.MODID)
public class CJCoreItems {

	@Register.RegisterItem(registryName = "multimeter", setUnlocalizedName = true, unlocalizedName = "multimeter")
	@Register.RegisterRender(hasVariants = true, variants = { "multimeter_energy", "multimeter_item", "multimeter_fluid" })
	public static ItemMultimeter multimeter;

}
