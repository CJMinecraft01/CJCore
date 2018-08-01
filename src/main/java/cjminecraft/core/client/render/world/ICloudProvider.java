package cjminecraft.core.client.render.world;

import cjminecraft.core.access.AccessHandler;
import cjminecraft.core.client.render.Texture;
import cjminecraft.core.util.MathUtils;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ICloudProvider {
	public float getCloudMovementSpeed(World world);

	public default float getMaxCloudSpeedDuringStorm() {
		return 12F;
	}

	public default float getMaxNormalCloudSpeed() {
		return 2F;
	}

	@SideOnly(Side.CLIENT)
	public Texture getCloudTexture();

	public default double getCloudMovementX(World world, float cloudTicksPrev, float cloudTicks) {
		return MathUtils.interpolateRotation(cloudTicksPrev, cloudTicks, AccessHandler.getPartialTicks());
	}

	public default double getCloudMovementZ(World world, float cloudTicksPrev, float cloudTicks) {
		return 0;
	}

	public boolean areCloudsApplicableTo(WorldProvider provider);
}