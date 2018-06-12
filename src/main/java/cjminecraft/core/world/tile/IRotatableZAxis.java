package cjminecraft.core.world.tile;

import net.minecraft.util.EnumFacing;

public interface IRotatableZAxis {
	public EnumFacing getRotationZAxis();

	public void setRotationZAxis(EnumFacing facing);
}