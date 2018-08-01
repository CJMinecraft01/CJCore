package cjminecraft.core.client.render;

import static org.lwjgl.opengl.GL11.GL_CLAMP;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_ZERO;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cjminecraft.core.access.AccessHandler;
import cjminecraft.core.world.tile.IRotatableXAxis;
import cjminecraft.core.world.tile.IRotatableYAxis;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class OpenGL {
	public static ArrayList<Framebuffer>	frameBuffers	= new ArrayList<Framebuffer>();
	public static boolean					lightmapTexUnitTextureEnable;
	public static int						lightmapTexUnit	= OpenGlHelper.lightmapTexUnit;
	public static int						defaultTexUnit	= OpenGlHelper.defaultTexUnit;

	public static void pushMatrix() {
		GL11.glPushMatrix();
	}

	public static void popMatrix() {
		GL11.glPopMatrix();
	}

	public static void translate(double offsetX, double offsetY, double offsetZ) {
		GL11.glTranslated(offsetX, offsetY, offsetZ);
	}

	public static void translate(float offsetX, float offsetY, float offsetZ) {
		GL11.glTranslatef(offsetX, offsetY, offsetZ);
	}

	public static void scale(double scaleX, double scaleY, double scaleZ) {
		GL11.glScaled(scaleX, scaleY, scaleZ);
	}

	public static void scale(float scaleX, float scaleY, float scaleZ) {
		GL11.glScalef(scaleX, scaleY, scaleZ);
	}

	public static void begin(int mode) {
		GL11.glBegin(mode);
	}

	public static void end() {
		GL11.glEnd();
	}

	public static void newList(int list, int mode) {
		GL11.glNewList(list, mode);
	}

	public static void callList(int list) {
		GL11.glCallList(list);
	}

	public static void endList() {
		GL11.glEndList();
	}

	public static void enableTexture2d() {
		GlStateManager.enableTexture2D();
	}

	public static void disableTexture2d() {
		GlStateManager.disableTexture2D();
	}

	public static void normal(float x, float y, float z) {
		GL11.glNormal3f(x, y, z);
	}

	public static void texCoord(float u, float v) {
		GL11.glTexCoord2f(u, v);
	}

	public static void vertex(float x, float y, float z) {
		GL11.glVertex3f(x, y, z);
	}

	public static void rotate(float angle, float x, float y, float z) {
		GL11.glRotatef(angle, x, y, z);
	}

	public static void enableBlend() {
		GlStateManager.disableBlend();
		GlStateManager.enableBlend();
	}

	public static void disableBlend() {
		GlStateManager.disableBlend();
	}

	public static void color(float r, float g, float b) {
		GL11.glColor3f(r, g, b);
	}

	public static void color(float r, float g, float b, float a) {
		GL11.glColor4f(r, g, b, a);
	}

	/**
	 * Same functionality as GlStateManager.color
	 * 
	 * @param color
	 *            - Hexadecimal color value
	 */
	public static void color4i(int color) {
		float a = (color >> 24 & 255) / 255.0F;
		float r = (color >> 16 & 255) / 255.0F;
		float g = (color >> 8 & 255) / 255.0F;
		float b = (color & 255) / 255.0F;
		color(r, g, b, a);
	}

	/**
	 * Same functionality as glColor3f
	 * 
	 * @param color
	 *            - Hexadecimal color value
	 */
	public static void color3i(int color) {
		float r = (color >> 16 & 255) / 255.0F;
		float g = (color >> 8 & 255) / 255.0F;
		float b = (color & 255) / 255.0F;
		color(r, g, b);
	}

	public static String getString(int name) {
		return GL11.glGetString(name);
	}

	public static void enableDepthTest() {
		GlStateManager.enableDepth();
	}

	public static void disableDepthTest() {
		GlStateManager.disableDepth();
	}

	public static void enable(int cap) {
		GL11.glEnable(cap);
	}

	public static void disable(int cap) {
		GL11.glDisable(cap);
	}

	public static void blendFunc(int sfactor, int dfactor) {
		GL11.glBlendFunc(sfactor, dfactor);
	}

	public static void depthMask(boolean flag) {
		GL11.glDepthMask(flag);
	}

	public static void setLightmapTextureCoords(int lightmapTexUnit, float x, float y) {
		OpenGlHelper.setLightmapTextureCoords(lightmapTexUnit, x, y);
	}

	public static void setActiveTexture(int id) {
		OpenGlHelper.setActiveTexture(id);
	}

	public static void enableLighting() {
		GlStateManager.enableLighting();
	}

	public static void disableLighting() {
		GlStateManager.disableLighting();
	}

	public static boolean getBoolean(int pname) {
		return GL11.glGetBoolean(pname);
	}

	public static void texParameter(int target, int pname, int param) {
		GL11.glTexParameteri(target, pname, param);
	}

	public static void texParameter(int target, int pname, float param) {
		GL11.glTexParameterf(target, pname, param);
	}

	public static void texParameter(int target, int pname, FloatBuffer buffer) {
		GL11.glTexParameter(target, pname, buffer);
	}

	public static void texParameter(int target, int pname, IntBuffer buffer) {
		GL11.glTexParameter(target, pname, buffer);
	}

	/**
	 * @param resource
	 *            - The ResourceLocation of which to get the GL texture ID from.
	 * @return Returns the GL texture ID of the specified ResourceLocation
	 */
	public static int getTextureId(ResourceLocation resource) {
		Object object = AccessHandler.getMinecraft().getTextureManager().getTexture(resource);
		object = object == null ? new SimpleTexture(resource) : object;
		return ((ITextureObject) object).getGlTextureId();
	}

	public static void shadeSmooth() {
		GL11.glShadeModel(GL11.GL_SMOOTH);
	}

	public static void shadeFlat() {
		GL11.glShadeModel(GL11.GL_FLAT);
	}

	public static void enableRescaleNormal() {
		enable(GL12.GL_RESCALE_NORMAL);
	}

	public static void disableRescaleNormal() {
		disable(GL12.GL_RESCALE_NORMAL);
	}

	public static void enableStandardItemLighting() {
		RenderHelper.enableStandardItemLighting();
	}

	public static void disableStandardItemLighting() {
		RenderHelper.disableStandardItemLighting();
	}

	public static void enableAlphaTest() {
		GlStateManager.enableAlpha();
	}

	public static void disableAlphaTest() {
		GlStateManager.disableAlpha();
	}

	public static void readBuffer(int mode) {
		GL11.glReadBuffer(mode);
	}

	public static void readPixels(int x, int y, int width, int height, int format, int type, ByteBuffer pixels) {
		GL11.glReadPixels(x, y, width, height, format, type, pixels);
	}

	public static void enableCullFace() {
		GlStateManager.enableCull();
	}

	public static void disableCullFace() {
		GlStateManager.disableCull();
	}

	/**
	 * Disable lightmapping, enable GL_BLEND, and reset the colors to default
	 * values.
	 */
	public static void disableLightMapping() {
		char light = 61680;
		OpenGL.enableBlend();
		OpenGL.blendFunc(GL_ONE, GL_ONE);
		OpenGL.depthMask(true);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) light % 65536 / 1.0F, (float) light / 65536 / 1.0F);
		OpenGL.color(1.0F, 1.0F, 1.0F, 1.0F);
	}

	/**
	 * Enable lightmapping, disable GL_BLEND, and reset the colors to default
	 * values.
	 */
	public static void enableLightMapping() {
		char light = 61680;
		OpenGL.disableBlend();
		OpenGL.depthMask(true);
		OpenGL.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) light % 65536 / 1.0F, (float) light / 65536 / 1.0F);
		OpenGL.color(1.0F, 1.0F, 1.0F, 1.0F);
	}

	/**
	 * Disable lighting
	 */
	public static void disableLight() {
		OpenGL.setActiveTexture(OpenGL.lightmapTexUnit);
		if (lightmapTexUnitTextureEnable = OpenGL.getBoolean(GL11.GL_TEXTURE_2D)) {
			OpenGL.disableTexture2d();
		}
		OpenGL.setActiveTexture(OpenGlHelper.defaultTexUnit);
		OpenGL.disableLighting();
	}

	/**
	 * Enable lighting
	 */
	public static void enableLight() {
		OpenGL.setActiveTexture(OpenGL.lightmapTexUnit);
		if (lightmapTexUnitTextureEnable) {
			OpenGL.enableTexture2d();
		}
		OpenGL.setActiveTexture(OpenGL.defaultTexUnit);
		OpenGL.enableLighting();
	}

	public static void blendClear() {
		OpenGlHelper.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
	}

	/**
	 * Combonation of GL functions used to smooth out the rough edges of a 2D
	 * texture.
	 */
	public static void antiAlias2d() {
		OpenGL.texParameter(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		OpenGL.texParameter(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		OpenGL.texParameter(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
		OpenGL.texParameter(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
	}

	public static void enableFog() {
		GlStateManager.enableFog();
	}

	public static void disableFog() {
		GlStateManager.disableFog();
	}

	public static void bindTexture(int target, int texture) {
		GL11.glBindTexture(target, texture);
	}

	public static void copyTexSubImage(int target, int level, int xoffset, int yoffset, int x, int y) {
		GL11.glCopyTexSubImage1D(target, level, xoffset, yoffset, x, y);
	}

	public static void copyTexSubImage(int target, int level, int xoffset, int yoffset, int x, int y, int width, int height) {
		GL11.glCopyTexSubImage2D(target, level, xoffset, yoffset, x, y, width, height);
	}

	public static void copyDownsizedRender(TextureManager manager, ResourceLocation target, int x, int y, int w, int h, int index) {
		ITextureObject textureObject = manager.getTexture(target);

		if (textureObject != null) {
			OpenGL.bindTexture(GL11.GL_TEXTURE_2D, textureObject.getGlTextureId());
			OpenGL.copyTexSubImage(GL11.GL_TEXTURE_2D, 0, index, index, x, y, w, h);
		}
	}

	public static Framebuffer createFrameBuffer(int width, int height, boolean useDepth) {
		Framebuffer render = new Framebuffer(width, height, useDepth);
		frameBuffers.add(render);
		return render;
	}

	public static void destroyFrameBuffer(Framebuffer buffer) {
		OpenGL.enableDepthTest();
		if (buffer.framebufferObject >= 0) {
			buffer.deleteFramebuffer();
		}
		frameBuffers.remove(buffer);
	}

	@SideOnly(Side.CLIENT)
	public static void rotate(TileEntity tile) {
		if (tile instanceof IRotatableYAxis) {
			IRotatableYAxis rotatable = (IRotatableYAxis) tile;

			if (rotatable != null && rotatable.getRotationYAxis() != null) {
				if (rotatable.getRotationYAxis() != null) {
					if (rotatable.getRotationYAxis() == EnumFacing.NORTH) {
						rotate(180F, 0F, 1F, 0F);
					}
					if (rotatable.getRotationYAxis() == EnumFacing.WEST) {
						rotate(-90F, 0F, 1F, 0F);
					} else if (rotatable.getRotationYAxis() == EnumFacing.EAST) {
						rotate(90F, 0F, 1F, 0F);
					}
				}
			}
		}

		if (tile instanceof IRotatableXAxis) {
			IRotatableXAxis rotatable = (IRotatableXAxis) tile;

			if (rotatable != null && rotatable.getRotationXAxis() != null) {
				if (rotatable.getRotationXAxis() != null) {
					if (rotatable.getRotationXAxis() == EnumFacing.DOWN) {
						rotate(-180F, 1F, 0F, 0F);
					}
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public static void rotateOpposite(TileEntity tile) {
		if (tile instanceof IRotatableYAxis) {
			IRotatableYAxis rotatable = (IRotatableYAxis) tile;

			if (rotatable != null && rotatable.getRotationYAxis() != null) {
				if (rotatable.getRotationYAxis() != null) {
					if (rotatable.getRotationYAxis() == EnumFacing.SOUTH) {
						rotate(180F, 0F, 1F, 0F);
					} else if (rotatable.getRotationYAxis() == EnumFacing.NORTH) {
						rotate(0F, 0F, 0F, 0F);
					} else if (rotatable.getRotationYAxis() == EnumFacing.EAST) {
						rotate(-90F, 0F, 1F, 0F);
					} else if (rotatable.getRotationYAxis() == EnumFacing.WEST) {
						rotate(90F, 0F, 1F, 0F);
					}
				}
			}
		}
	}
}