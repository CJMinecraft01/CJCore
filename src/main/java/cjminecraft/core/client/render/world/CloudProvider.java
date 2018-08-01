package cjminecraft.core.client.render.world;

import org.lwjgl.opengl.GL11;

import cjminecraft.core.access.AccessHandler;
import cjminecraft.core.client.render.OpenGL;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

//@EventBusSubscriber
public abstract class CloudProvider extends IRenderHandler implements ICloudProvider {
	protected float cloudSpeed = getMaxNormalCloudSpeed();
	protected long cloudTicks;
	protected long cloudTicksPrev;

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void updateClouds(ClientTickEvent event) {
		World world = AccessHandler.getMinecraft().world;

		if (world != null && !AccessHandler.getMinecraft().isGamePaused()) {
			if (world.provider instanceof IClimateProvider) {
				IClimateProvider weatherProvider = (IClimateProvider) world.provider;

				if (weatherProvider.getCloudProvider() instanceof StormProvider) {
					CloudProvider clouds = (CloudProvider) weatherProvider.getCloudProvider();
					IStormProvider storms = (StormProvider) weatherProvider.getStormProvider();

					if (clouds.areCloudsApplicableTo(world.provider) && storms instanceof IStormProvider) {
						if (storms.isStormActive(world)) {
							if (clouds.cloudSpeed < clouds.getMaxCloudSpeedDuringStorm()) {
								clouds.cloudSpeed += 0.0125F;
							}
						} else {
							if (clouds.cloudSpeed > clouds.getMaxNormalCloudSpeed()) {
								clouds.cloudSpeed -= 0.0125F;
							}
						}

						clouds.cloudTicksPrev = clouds.cloudTicks;
						clouds.cloudTicks += clouds.cloudSpeed;
					}
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void render(float partialTicks, WorldClient world, Minecraft mc) {
		if (world.provider instanceof IClimateProvider) {
			IClimateProvider weatherProvider = (IClimateProvider) world.provider;
			ICloudProvider clouds = (ICloudProvider) weatherProvider.getCloudProvider();

			if (clouds.areCloudsApplicableTo(world.provider)) {
				if (AccessHandler.getMinecraft().gameSettings.shouldRenderClouds() >= 1) {
					OpenGL.pushMatrix();
					{
						if (AccessHandler.getMinecraft().gameSettings.fancyGraphics) {
							OpenGL.enable(GL11.GL_FOG);
						}

						this.renderClouds(partialTicks);
						OpenGL.disable(GL11.GL_FOG);
					}
					OpenGL.popMatrix();
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public void renderClouds(float renderPartialTicks) {
		GlStateManager.disableCull();
		Entity entity = AccessHandler.getMinecraft().getRenderViewEntity();
		float yOffset = (float) (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) renderPartialTicks);
		byte cloudSections = 4;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		double viewX = (entity.prevPosX + (entity.posX - entity.prevPosX) * (double) renderPartialTicks + getCloudMovementX(entity.world, cloudTicksPrev, cloudTicks) * 0.029999999329447746D) / 12.0D;
		double viewZ = (entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double) renderPartialTicks + getCloudMovementZ(entity.world, cloudTicksPrev, cloudTicks) * 0.029999999329447746D) / 12.0D;
		float cloudHeight = AccessHandler.getMinecraft().world.provider.getCloudHeight() - yOffset + 0.33F;
		viewX = viewX - (double) (MathHelper.floor(viewX / 2048.0D) * 2048);
		viewZ = viewZ - (double) (MathHelper.floor(viewZ / 2048.0D) * 2048);
		getCloudTexture().bind();
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

		float r = (float) entity.world.provider.getCloudColor(renderPartialTicks).x;
		float g = (float) entity.world.provider.getCloudColor(renderPartialTicks).y;
		float b = (float) entity.world.provider.getCloudColor(renderPartialTicks).z;

		float f17 = (float) MathHelper.floor(viewX) * 0.00390625F;
		float f18 = (float) MathHelper.floor(viewZ) * 0.00390625F;
		float f19 = (float) (viewX - (double) MathHelper.floor(viewX - 6));
		float f20 = (float) (viewZ - (double) MathHelper.floor(viewZ - 6));
		GlStateManager.scale(12.0F, 1.0F, 12.0F);

		for (int pass = 0; pass < 2; ++pass) {
			if (pass == 0) {
				GL11.glColorMask(false, false, false, false);
			} else {
				GL11.glColorMask(true, true, true, true);
			}

			for (int x = -cloudSections + 1; x <= cloudSections; ++x) {
				for (int z = -cloudSections + 1; z <= cloudSections; ++z) {
					vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
					float cU = (float) (x * 8);
					float cV = (float) (z * 8);
					float cX = cU - f19;
					float cZ = cV - f20;

					if (cloudHeight > -5.0F) {
						vertexbuffer.pos((double) (cX + 0.0F), (double) (cloudHeight + 0.0F), (double) (cZ + 8.0F)).tex((double) ((cU + 0.0F) * 0.00390625F + f17), (double) ((cV + 8.0F) * 0.00390625F + f18)).color(r * 0.7F, g * 0.7F, b * 0.7F, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
						vertexbuffer.pos((double) (cX + 8.0F), (double) (cloudHeight + 0.0F), (double) (cZ + 8.0F)).tex((double) ((cU + 8.0F) * 0.00390625F + f17), (double) ((cV + 8.0F) * 0.00390625F + f18)).color(r * 0.7F, g * 0.7F, b * 0.7F, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
						vertexbuffer.pos((double) (cX + 8.0F), (double) (cloudHeight + 0.0F), (double) (cZ + 0.0F)).tex((double) ((cU + 8.0F) * 0.00390625F + f17), (double) ((cV + 0.0F) * 0.00390625F + f18)).color(r * 0.7F, g * 0.7F, b * 0.7F, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
						vertexbuffer.pos((double) (cX + 0.0F), (double) (cloudHeight + 0.0F), (double) (cZ + 0.0F)).tex((double) ((cU + 0.0F) * 0.00390625F + f17), (double) ((cV + 0.0F) * 0.00390625F + f18)).color(r * 0.7F, g * 0.7F, b * 0.7F, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
					}

					if (cloudHeight <= 5.0F) {
						vertexbuffer.pos((double) (cX + 0.0F), (double) (cloudHeight + 4.0F - 9.765625E-4F), (double) (cZ + 8.0F)).tex((double) ((cU + 0.0F) * 0.00390625F + f17), (double) ((cV + 8.0F) * 0.00390625F + f18)).color(r, g, b, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
						vertexbuffer.pos((double) (cX + 8.0F), (double) (cloudHeight + 4.0F - 9.765625E-4F), (double) (cZ + 8.0F)).tex((double) ((cU + 8.0F) * 0.00390625F + f17), (double) ((cV + 8.0F) * 0.00390625F + f18)).color(r, g, b, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
						vertexbuffer.pos((double) (cX + 8.0F), (double) (cloudHeight + 4.0F - 9.765625E-4F), (double) (cZ + 0.0F)).tex((double) ((cU + 8.0F) * 0.00390625F + f17), (double) ((cV + 0.0F) * 0.00390625F + f18)).color(r, g, b, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
						vertexbuffer.pos((double) (cX + 0.0F), (double) (cloudHeight + 4.0F - 9.765625E-4F), (double) (cZ + 0.0F)).tex((double) ((cU + 0.0F) * 0.00390625F + f17), (double) ((cV + 0.0F) * 0.00390625F + f18)).color(r, g, b, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
					}

					if (x > -1) {
						for (int v = 0; v < 8; ++v) {
							vertexbuffer.pos((double) (cX + (float) v + 0.0F), (double) (cloudHeight + 0.0F), (double) (cZ + 8.0F)).tex((double) ((cU + (float) v + 0.5F) * 0.00390625F + f17), (double) ((cV + 8.0F) * 0.00390625F + f18)).color(r * 0.9F, g * 0.9F, b * 0.9F, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
							vertexbuffer.pos((double) (cX + (float) v + 0.0F), (double) (cloudHeight + 4.0F), (double) (cZ + 8.0F)).tex((double) ((cU + (float) v + 0.5F) * 0.00390625F + f17), (double) ((cV + 8.0F) * 0.00390625F + f18)).color(r * 0.9F, g * 0.9F, b * 0.9F, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
							vertexbuffer.pos((double) (cX + (float) v + 0.0F), (double) (cloudHeight + 4.0F), (double) (cZ + 0.0F)).tex((double) ((cU + (float) v + 0.5F) * 0.00390625F + f17), (double) ((cV + 0.0F) * 0.00390625F + f18)).color(r * 0.9F, g * 0.9F, b * 0.9F, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
							vertexbuffer.pos((double) (cX + (float) v + 0.0F), (double) (cloudHeight + 0.0F), (double) (cZ + 0.0F)).tex((double) ((cU + (float) v + 0.5F) * 0.00390625F + f17), (double) ((cV + 0.0F) * 0.00390625F + f18)).color(r * 0.9F, g * 0.9F, b * 0.9F, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
						}
					}

					if (x <= 1) {
						for (int v = 0; v < 8; ++v) {
							vertexbuffer.pos((double) (cX + (float) v + 1.0F - 9.765625E-4F), (double) (cloudHeight + 0.0F), (double) (cZ + 8.0F)).tex((double) ((cU + (float) v + 0.5F) * 0.00390625F + f17), (double) ((cV + 8.0F) * 0.00390625F + f18)).color(r * 0.9F, g * 0.9F, b * 0.9F, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
							vertexbuffer.pos((double) (cX + (float) v + 1.0F - 9.765625E-4F), (double) (cloudHeight + 4.0F), (double) (cZ + 8.0F)).tex((double) ((cU + (float) v + 0.5F) * 0.00390625F + f17), (double) ((cV + 8.0F) * 0.00390625F + f18)).color(r * 0.9F, g * 0.9F, b * 0.9F, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
							vertexbuffer.pos((double) (cX + (float) v + 1.0F - 9.765625E-4F), (double) (cloudHeight + 4.0F), (double) (cZ + 0.0F)).tex((double) ((cU + (float) v + 0.5F) * 0.00390625F + f17), (double) ((cV + 0.0F) * 0.00390625F + f18)).color(r * 0.9F, g * 0.9F, b * 0.9F, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
							vertexbuffer.pos((double) (cX + (float) v + 1.0F - 9.765625E-4F), (double) (cloudHeight + 0.0F), (double) (cZ + 0.0F)).tex((double) ((cU + (float) v + 0.5F) * 0.00390625F + f17), (double) ((cV + 0.0F) * 0.00390625F + f18)).color(r * 0.9F, g * 0.9F, b * 0.9F, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
						}
					}

					if (z > -1) {
						for (int v = 0; v < 8; ++v) {
							vertexbuffer.pos((double) (cX + 0.0F), (double) (cloudHeight + 4.0F), (double) (cZ + (float) v + 0.0F)).tex((double) ((cU + 0.0F) * 0.00390625F + f17), (double) ((cV + (float) v + 0.5F) * 0.00390625F + f18)).color(r * 0.8F, g * 0.8F, b * 0.8F, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
							vertexbuffer.pos((double) (cX + 8.0F), (double) (cloudHeight + 4.0F), (double) (cZ + (float) v + 0.0F)).tex((double) ((cU + 8.0F) * 0.00390625F + f17), (double) ((cV + (float) v + 0.5F) * 0.00390625F + f18)).color(r * 0.8F, g * 0.8F, b * 0.8F, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
							vertexbuffer.pos((double) (cX + 8.0F), (double) (cloudHeight + 0.0F), (double) (cZ + (float) v + 0.0F)).tex((double) ((cU + 8.0F) * 0.00390625F + f17), (double) ((cV + (float) v + 0.5F) * 0.00390625F + f18)).color(r * 0.8F, g * 0.8F, b * 0.8F, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
							vertexbuffer.pos((double) (cX + 0.0F), (double) (cloudHeight + 0.0F), (double) (cZ + (float) v + 0.0F)).tex((double) ((cU + 0.0F) * 0.00390625F + f17), (double) ((cV + (float) v + 0.5F) * 0.00390625F + f18)).color(r * 0.8F, g * 0.8F, b * 0.8F, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
						}
					}

					if (z <= 1) {
						for (int v = 0; v < 8; ++v) {
							vertexbuffer.pos((double) (cX + 0.0F), (double) (cloudHeight + 4.0F), (double) (cZ + (float) v + 1.0F - 9.765625E-4F)).tex((double) ((cU + 0.0F) * 0.00390625F + f17), (double) ((cV + (float) v + 0.5F) * 0.00390625F + f18)).color(r * 0.8F, g * 0.8F, b * 0.8F, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
							vertexbuffer.pos((double) (cX + 8.0F), (double) (cloudHeight + 4.0F), (double) (cZ + (float) v + 1.0F - 9.765625E-4F)).tex((double) ((cU + 8.0F) * 0.00390625F + f17), (double) ((cV + (float) v + 0.5F) * 0.00390625F + f18)).color(r * 0.8F, g * 0.8F, b * 0.8F, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
							vertexbuffer.pos((double) (cX + 8.0F), (double) (cloudHeight + 0.0F), (double) (cZ + (float) v + 1.0F - 9.765625E-4F)).tex((double) ((cU + 8.0F) * 0.00390625F + f17), (double) ((cV + (float) v + 0.5F) * 0.00390625F + f18)).color(r * 0.8F, g * 0.8F, b * 0.8F, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
							vertexbuffer.pos((double) (cX + 0.0F), (double) (cloudHeight + 0.0F), (double) (cZ + (float) v + 1.0F - 9.765625E-4F)).tex((double) ((cU + 0.0F) * 0.00390625F + f17), (double) ((cV + (float) v + 0.5F) * 0.00390625F + f18)).color(r * 0.8F, g * 0.8F, b * 0.8F, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
						}
					}

					tessellator.draw();
				}
			}
		}
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableBlend();
		GlStateManager.enableCull();
	}

	@Override
	public float getCloudMovementSpeed(World world) {
		return cloudSpeed;
	}
}