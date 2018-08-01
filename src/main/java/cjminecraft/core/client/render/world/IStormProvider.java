package cjminecraft.core.client.render.world;

import cjminecraft.core.client.render.Texture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IStormProvider {
	public boolean isStormApplicableTo(WorldProvider provider);

	public boolean isStormActive(World world);

	public float getStormStrength();

	public float getStormDensity();

	public int getStormSize();

	public boolean isStormVisibleInBiome(Biome biome);

	public float getStormDownfallSpeed();

	public float getStormWindSpeed();

	public boolean doesLightingApply();

	public float getStormDirection();

	@SideOnly(Side.CLIENT)
	public Texture getStormTexture(World world, Biome biome);

	public default void spawnParticleOnGround(World world, double pX, double pY, double pZ) {
		world.spawnParticle(EnumParticleTypes.DRIP_LAVA, pX, pY, pZ, 0.0D, 0.0D, 0.0D, new int[0]);
	}

	public default void playStormSoundAbove(World world, double x, double y, double z) {
		world.playSound(x, y, z, SoundEvents.WEATHER_RAIN_ABOVE, SoundCategory.WEATHER, 0.1F, 0.5F, false);
	}

	public default void playStormSound(World world, double x, double y, double z) {
		world.playSound(x, y, z, SoundEvents.WEATHER_RAIN, SoundCategory.WEATHER, 0.2F, 1.0F, false);
	}

	public void updateStorm(World world);

	@SideOnly(Side.CLIENT)
	public void renderStorm(float partialTicks, WorldClient world, Minecraft minecraft);
}