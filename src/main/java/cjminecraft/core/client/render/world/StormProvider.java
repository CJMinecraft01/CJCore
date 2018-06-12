package cjminecraft.core.client.render.world;

import java.util.Random;

import com.google.common.base.Predicate;

import cjminecraft.core.access.AccessHandler;
import cjminecraft.core.client.render.Draw;
import cjminecraft.core.client.render.OpenGL;
import cjminecraft.core.world.Worlds;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * EventBusSubscriber annotation must apply to each individual storm provider.
 * Provider will not work without it.
 **/
@EventBusSubscriber
public abstract class StormProvider implements Predicate<Entity>, IStormProvider {
	protected Random	random			= new Random();

	protected float[]	stormX			= null;
	protected float[]	stormZ			= null;
	protected float		stormDensity	= 0.0F;
	protected int		rainSoundCounter;
	protected boolean	renderStorm;

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void clientTickEvent(ClientTickEvent event) {
		if (AccessHandler.getMinecraft().world != null && AccessHandler.getMinecraft().world.provider instanceof IClimateProvider && !AccessHandler.getMinecraft().isGamePaused()) {
			IClimateProvider climate = (IClimateProvider) AccessHandler.getMinecraft().world.provider;

			if (climate.getStormProvider() instanceof StormProvider) {
				StormProvider storm = (StormProvider) climate.getStormProvider();

				if (storm.isStormApplicableTo(AccessHandler.getMinecraft().world.provider)) {
					int s = storm.getStormSize();
					storm.updateStorm(AccessHandler.getMinecraft().world);

					if (storm.stormX == null || storm.stormZ == null) {
						storm.stormX = new float[s * s];
						storm.stormZ = new float[s * s];

						for (int zCoord = 0; zCoord < s; ++zCoord) {
							for (int xCoord = 0; xCoord < s; ++xCoord) {
								float x = xCoord - 16;
								float z = zCoord - 16;
								float sq = MathHelper.sqrt(x * x + z * z);
								storm.stormX[zCoord << 5 | xCoord] = -z / sq;
								storm.stormZ[zCoord << 5 | xCoord] = x / sq;
							}
						}
					}

					if (storm.isStormActive(AccessHandler.getMinecraft().world)) {
						storm.renderStorm = true;

						if (storm.stormDensity < 1.0F) {
							storm.stormDensity += 0.0025F;
						}
					} else {
						if (storm.stormDensity >= 0.0F) {
							storm.stormDensity -= 0.0025F;
						} else {
							storm.renderStorm = false;
							storm.stormDensity = 0.0F;
						}
					}

					if (storm.isStormActive(AccessHandler.getMinecraft().world)) {
						float strength = storm.getStormStrength();

						if (!AccessHandler.getMinecraft().gameSettings.fancyGraphics) {
							strength /= 2.0F;
						}

						if (strength != 0.0F) {
							Entity entity = AccessHandler.getMinecraft().getRenderViewEntity();
							World world = entity.world;
							BlockPos blockpos = new BlockPos(entity);
							double x = 0.0D;
							double y = 0.0D;
							double z = 0.0D;
							int particleCount = 0;
							int passes = (int) (100.0F * strength * strength);

							if (AccessHandler.getMinecraft().gameSettings.particleSetting == 1) {
								passes >>= 1;
							} else if (AccessHandler.getMinecraft().gameSettings.particleSetting == 2) {
								passes = 0;
							}

							storm.random = new Random();

							for (int i = 0; i < passes; ++i) {
								BlockPos pos1 = world.getPrecipitationHeight(blockpos.add(storm.random.nextInt(10) - storm.random.nextInt(10), 0, storm.random.nextInt(10) - storm.random.nextInt(10)));
								Biome biome = world.getBiome(pos1);
								BlockPos pos2 = pos1.down();
								IBlockState state = world.getBlockState(pos2);

								if (pos1.getY() <= blockpos.getY() + 10 && pos1.getY() >= blockpos.getY() - 10 && storm.isStormVisibleInBiome(biome)) {
									double xOffset = storm.random.nextDouble();
									double zOffset = storm.random.nextDouble();
									AxisAlignedBB box = state.getBoundingBox(world, pos2);

									if (state.getMaterial() != Material.LAVA && state.getBlock() != Blocks.MAGMA) {
										if (state.getMaterial() != Material.AIR) {
											++particleCount;

											if (storm.random.nextInt(particleCount) == 0) {
												x = (double) pos2.getX() + xOffset;
												y = (double) ((float) pos2.getY() + 0.1F) + box.maxY - 1.0D;
												z = (double) pos2.getZ() + zOffset;
											}

											double pX = (double) pos2.getX() + xOffset;
											double pY = (double) ((float) pos2.getY() + 0.1F) + box.maxY;
											double pZ = (double) pos2.getZ() + zOffset;

											storm.spawnParticleOnGround(world, pX, pY, pZ);
										}
									}
								}
							}

							if (particleCount > 0 && storm.random.nextInt(3) < storm.rainSoundCounter++) {
								storm.rainSoundCounter = 0;

								if (y > (double) (blockpos.getY() + 1) && world.getPrecipitationHeight(blockpos).getY() > MathHelper.floor((float) blockpos.getY())) {
									storm.playStormSoundAbove(world, x, y, z);
								} else {
									storm.playStormSound(world, x, y, z);
								}
							}
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void worldTickEvent(WorldTickEvent event) {
		if (event.world != null && event.world.provider instanceof IClimateProvider) {
			IClimateProvider climate = (IClimateProvider) event.world.provider;
			climate.getStormProvider().updateStorm(event.world);
		}
	}

	public void updateStorm(World world) {
		if (world != null && this.isStormApplicableTo(world.provider) && this.isStormActive(world)) {
			for (Object o : world.loadedEntityList.toArray()) {
				if (o instanceof Entity) {
					Entity entity = (Entity) o;

					if (this.apply(entity) && Worlds.canSeeSky(new BlockPos(entity), world)) {
						entity.motionZ += 0.03F;
						entity.motionY += MathHelper.sin(world.getWorldTime() * 0.4F) * 0.1F;
						entity.fallDistance = 0F;
						entity.attackEntityFrom(DamageSource.LAVA, 0.5F);
					}
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void renderLast(RenderWorldLastEvent event) {
		if (AccessHandler.getMinecraft().world != null) {
			if (AccessHandler.getMinecraft().world.provider instanceof IClimateProvider) {
				IClimateProvider climate = (IClimateProvider) AccessHandler.getMinecraft().world.provider;
				climate.getStormProvider().renderStorm(event.getPartialTicks(), AccessHandler.getMinecraft().world, Minecraft.getMinecraft());
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public void renderStorm(float partialTicks, WorldClient world, Minecraft mc) {
		if (!isStormActive(world) && !renderStorm) {
			return;
		}

		if (stormX == null || stormZ == null) {
			return;
		}

		OpenGL.pushMatrix();
		OpenGL.enableLight();
		Entity entity = AccessHandler.getMinecraft().getRenderViewEntity();
		int posX = MathHelper.floor(entity.posX);
		int posY = MathHelper.floor(entity.posY);
		int posZ = MathHelper.floor(entity.posZ);
		BufferBuilder buffer = Draw.buffer();
		GlStateManager.disableCull();
		GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
		GlStateManager.enableBlend();
		OpenGL.blendClear();
		GlStateManager.enableColorMaterial();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

		if (!doesLightingApply()) {
			OpenGL.disableLight();
		}

		double renderPartialX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) partialTicks;
		double renderPartialY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) partialTicks;
		double renderPartialZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) partialTicks;
		int renderYFloor = MathHelper.floor(renderPartialY);
		int stormDepth = 5;
		int stormHeight = 6 + stormDepth;

		if (AccessHandler.getMinecraft().gameSettings.fancyGraphics) {
			stormDepth = 10;
		}

		int lastPass = -1;
		buffer.setTranslation(-renderPartialX, -renderPartialY, -renderPartialZ);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

		for (int z = posZ - stormDepth; z <= posZ + stormDepth; ++z) {
			for (int x = posX - stormDepth; x <= posX + stormDepth; ++x) {
				int idx = (z - posZ + 16) * 32 + x - posX + 16;
				double rX = (double) this.stormX[idx] * 0.5D;
				double rZ = (double) this.stormZ[idx] * 0.5D;
				pos.setPos(x, 0, z);
				Biome biome = world.getBiome(pos);

				if (isStormVisibleInBiome(biome) && renderStorm) {
					int startHeight = world.getPrecipitationHeight(pos).getY();
					int minY = posY - stormHeight;
					int maxY = posY + stormHeight;

					if (minY < startHeight) {
						minY = startHeight;
					}

					if (maxY < startHeight) {
						maxY = startHeight;
					}

					int vY = startHeight;

					if (startHeight < renderYFloor) {
						vY = renderYFloor;
					}

					if (minY != maxY) {
						this.random.setSeed((long) (x * x * 3121 + x * 45238971 ^ z * z * 418711 + z * 13761));
						pos.setPos(x, minY, z);
						OpenGL.enableCullFace();

						if (lastPass != 0) {
							if (lastPass >= 0) {
								Tessellator.getInstance().draw();
							}

							lastPass = 0;
							getStormTexture(world, biome).bind();
							buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
						}

						float vTravel = -(((AccessHandler.getMinecraft().world.getWorldTime() + (x * x) + x + (z * z) + z) & 31) + partialTicks) / getStormDownfallSpeed();
						float hTravel = (((AccessHandler.getMinecraft().world.getWorldTime() + (x * x) + x + (z * z) + z) & 31) + partialTicks) / getStormWindSpeed();

						double offsetX = (double) ((float) x + 0.5F) - entity.posX;
						double offsetZ = (double) ((float) z + 0.5F) - entity.posZ;
						float strength = MathHelper.sqrt(offsetX * offsetX + offsetZ * offsetZ) / (float) stormDepth;
						float alpha = ((1.0F - strength * strength) * 0.5F + 0.5F) * getStormDensity();
						pos.setPos(x, vY, z);
						int light = world.getCombinedLight(pos, 0);
						int lightmapX = light >> 16 & 65535;
						int lightmapY = light & 65535;
						buffer.pos((double) x - rX + 0.5D, (double) minY, (double) z - rZ + 0.5D + hTravel).tex(0.0D, (double) maxY * 0.25D + vTravel).color(1.0F, 1.0F, 1.0F, alpha).lightmap(lightmapX, lightmapY).endVertex();
						buffer.pos((double) x + rX + 0.5D, (double) minY, (double) z + rZ + 0.5D + hTravel).tex(1.0D, (double) maxY * 0.25D + vTravel).color(1.0F, 1.0F, 1.0F, alpha).lightmap(lightmapX, lightmapY).endVertex();
						buffer.pos((double) x + rX + 0.5D, (double) maxY, (double) z + rZ + 0.5D + hTravel).tex(1.0D, (double) minY * 0.25D + vTravel).color(1.0F, 1.0F, 1.0F, alpha).lightmap(lightmapX, lightmapY).endVertex();
						buffer.pos((double) x - rX + 0.5D, (double) maxY, (double) z - rZ + 0.5D + hTravel).tex(0.0D, (double) minY * 0.25D + vTravel).color(1.0F, 1.0F, 1.0F, alpha).lightmap(lightmapX, lightmapY).endVertex();
					}
				}
			}
		}

		if (lastPass >= 0) {
			Tessellator.getInstance().draw();
		}

		buffer.setTranslation(0.0D, 0.0D, 0.0D);
		GlStateManager.enableCull();
		GlStateManager.disableBlend();
		GlStateManager.alphaFunc(516, 0.1F);
		OpenGL.disableLight();
		OpenGL.popMatrix();
	}

	@Override
	public boolean apply(Entity entity) {
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;

			if (player.capabilities.isCreativeMode) {
				return false;
			}
		}

		return true;
	}

	@Override
	public float getStormStrength() {
		return 1F;
	}

	@Override
	public float getStormDensity() {
		return stormDensity;
	}

	@Override
	public boolean isStormVisibleInBiome(Biome biome) {
		return true;
	}

	@Override
	public boolean doesLightingApply() {
		return true;
	}
}