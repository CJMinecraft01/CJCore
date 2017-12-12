package cjminecraft.core.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * A utility class for rendering
 * 
 * @author CJMinecraft
 *
 */
@SideOnly(Side.CLIENT)
public class RenderUtils {

	public static final ResourceLocation BLOCK_SHEET = new ResourceLocation("textures/atlas/blocks.png");
	public static final ResourceLocation FONT_DEFAULT = new ResourceLocation("textures/font/ascii.png");
	public static final ResourceLocation FONT_ALTERNATE = new ResourceLocation("textures/fonts/ascii_sga.png");
	public static final ResourceLocation ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");

	public static TextureManager getTextureManager() {
		return Minecraft.getMinecraft().renderEngine;
	}

	public static TextureMap getTextureMap() {
		return Minecraft.getMinecraft().getTextureMapBlocks();
	}

	public static Tessellator getTessellator() {
		return Tessellator.getInstance();
	}

	public static RenderItem getRenderItem() {
		return Minecraft.getMinecraft().getRenderItem();
	}

	/**
	 * Set the current colour using a hex integer
	 * 
	 * @param colour
	 *            The colour in format 0xRRGGBB
	 */
	public static void setGLColourFromInt(int colour) {
		float red = (colour >> 16 & 255) / 255.0F;
		float green = (colour >> 8 & 255) / 255.0F;
		float blue = (colour & 255) / 255.0F;
		GlStateManager.color(red, green, blue);
	}

	/**
	 * Reset the colour to white
	 */
	public static void resetColour() {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	}

	/**
	 * Gets the texture of the {@link Fluid}
	 * 
	 * @param fluid
	 *            The {@link Fluid} to get the texture of
	 * @return The texture of the {@link Fluid}
	 */
	public static TextureAtlasSprite getFluidTexture(Fluid fluid) {
		if (fluid == null)
			fluid = FluidRegistry.LAVA;
		return getTexture(fluid.getStill());
	}

	/**
	 * Gets the texture of the {@link Fluid} inside of the given
	 * {@link FluidStack}
	 * 
	 * @param fluid
	 *            The {@link FluidStack} which has the {@link Fluid} to get the
	 *            texture from
	 * @return The texture of the {@link Fluid} inside the {@link FluidStack}
	 */
	public static TextureAtlasSprite getFluidTexture(FluidStack fluid) {
		if (fluid == null || fluid.getFluid().getStill() == null)
			fluid = new FluidStack(FluidRegistry.LAVA, 1);
		return getTexture(fluid.getFluid().getStill());
	}

	/**
	 * Bind the given texture
	 * 
	 * @param texture
	 *            The texture to bind
	 */
	public static void bindTexture(ResourceLocation texture) {
		getTextureManager().bindTexture(texture);
	}

	/**
	 * Binds the block sheet
	 */
	public static void setBlockTextureSheet() {
		bindTexture(BLOCK_SHEET);
	}

	/**
	 * Binds the default font texture sheet
	 */
	public static void setDefaultFontTextureSheet() {
		bindTexture(FONT_DEFAULT);
	}
	
	/**
	 * Binds the alternate font texture sheet
	 */
	public static void setAlternateFontTextureSheet() {
		bindTexture(FONT_ALTERNATE);
	}

	/**
	 * Get the {@link TextureAtlasSprite} from the given texture location
	 * 
	 * @param location
	 *            The location of the texture
	 * @return The {@link TextureAtlasSprite} from the given texture location
	 */
	public static TextureAtlasSprite getTexture(String location) {
		return getTextureMap().getAtlasSprite(location);
	}
	
	/**
	 * Get the {@link TextureAtlasSprite} from the given texture location
	 * 
	 * @param location
	 *            The location of the texture
	 * @return The {@link TextureAtlasSprite} from the given texture location
	 */
	public static TextureAtlasSprite getTexture(ResourceLocation location) {
		return getTexture(location.toString());
	}
	
	/**
	 * Enable standard lighting for items in a gui
	 */
	public static void enableGUIStandardItemLighting() {
		RenderHelper.enableGUIStandardItemLighting();
	}
	
	/**
	 * Enable standard lighting for items
	 */
	public static void enableStardardItemLighting() {
		RenderHelper.enableStandardItemLighting();
	}

}
