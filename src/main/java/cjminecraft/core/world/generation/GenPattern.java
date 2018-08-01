package cjminecraft.core.world.generation;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GenPattern {

	/**
	 * Makes a cube out of {@code blockState} anchored at the bottom-middle. NOTE: this ignores the anchor. Also, make sure the side length is an odd natural number, else you'll break something
	 * 
	 * @param world
	 *            the world
	 * @param blockState
	 *            the blockState to make a cube of (use {@link Block#getDefaultState()} if unsure)
	 * @param pos
	 *            the {@link BlockPos} of the anchor
	 * @param sideLength
	 *            the length of the sides of the cube
	 */
	public static void blockCube(World world, IBlockState blockState, BlockPos pos, int sideLength) {
		for (int x = -sideLength / 2; x < sideLength / 2 + 1; x++) {
			for (int y = 0; y < sideLength + 1; y++) {
				for (int z = -sideLength / 2; z < sideLength / 2 + 1; z++) {
					if (x == 0 && y == 0 && z == 0) {
						continue;
					} else {
						world.setBlockState(pos.add(x, y, z), blockState);
					}
				}
			}
		}
	}

	
	//TODO: Add block filtering capabilities.
	public static void genSquare(World world, BlockPos center, int radius, IBlockState state) {
		BlockPos pos = center.add(-radius, 0, -radius);
		EnumFacing facing = EnumFacing.EAST;
		for (int i = 0; i < 4; i++) {
			for (int k = radius * 2 - 1; k >= 0; k--) {
				if (world.getBlockState(pos).getBlock() == Blocks.AIR)
					world.setBlockState(pos, state);
				pos = pos.offset(facing);
			}
			facing = facing.rotateY();
		}
	}

	/**
	 * Creates a dome.
	 * 
	 * @param world
	 *            the world
	 * @param pos
	 *            the bottom-middle of the shield
	 * @param domeHeight
	 *            difference in height between {@code pos} and the peak of the dome
	 */
	//TODO: Add block filtering capabilities.
	public static void genTruncatedPyramid(World world, BlockPos pos, IBlockState state, int domeHeight, int domeTopLength, int slope) {
		int radius = domeTopLength / 2;
		for (int i = domeHeight - 1; i >= 0; i -= slope) {
			for (int k = 0; k >= -slope + 1 && i + k >= 0; k--)
				genSquare(world, new BlockPos(pos.getX(), pos.getY() + i + k, pos.getZ()), (domeHeight - i - 1) / slope + radius, state);
		}
		for (int x = -radius; x <= radius; x++)
			for (int z = -radius; z <= radius; z++)
				if (world.getBlockState(pos.add(x, domeHeight - 1, z)).getBlock() == Blocks.SNOW_LAYER || world.getBlockState(pos.add(x, domeHeight - 1, z)).getBlock() == Blocks.AIR)
					world.setBlockState(pos.add(x, domeHeight - 1, z), state);
	}

	//TODO: Add sphere, pyramid, cube, cylinder, tetrahedron, triangular prism
}
