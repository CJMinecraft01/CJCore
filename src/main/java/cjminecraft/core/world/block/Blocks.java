package cjminecraft.core.world.block;

import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Blocks {
	public static ArrayList<BlockPos> getCoordDataInRange(int posX, int posY, int posZ, int range) {
		ArrayList<BlockPos> data = new ArrayList<BlockPos>();

		for (int x = posX - range; x < posX + range * 2; x++) {
			for (int y = posY - range; y < posY + range * 2; y++) {
				for (int z = posZ - range; z < posZ + range * 2; z++) {
					data.add(new BlockPos(x, y, z));
				}
			}
		}

		return data;
	}

	public static ArrayList<BlockPos> getPositionsInRange(int posX, int posY, int posZ, int range) {
		ArrayList<BlockPos> data = new ArrayList<BlockPos>();

		for (int x = posX - range; x < posX + range * 2; x++) {
			for (int y = posY - range; y < posY + range * 2; y++) {
				for (int z = posZ - range; z < posZ + range * 2; z++) {
					data.add(new BlockPos(x, y, z));
				}
			}
		}

		return data;
	}

	public static ArrayList<BlockPos> getCoordDataInRangeIncluding(int posX, int posY, int posZ, int range, World world, Block... types) {
		ArrayList<BlockPos> data = new ArrayList<BlockPos>();

		for (int x = posX - range; x < posX + range * 2; x++) {
			for (int y = posY - range; y < posY + range * 2; y++) {
				for (int z = posZ - range; z < posZ + range * 2; z++) {
					BlockPos coordData = new BlockPos(x, y, z);
					Block block = world.getBlockState(coordData).getBlock();

					if (Arrays.asList(types).contains(block)) {
						data.add(coordData);
					}
				}
			}
		}

		return data;
	}

	public static ArrayList<BlockPos> getBlocksInRangeIncluding(int posX, int posY, int posZ, int range, World world, Block... types) {
		ArrayList<BlockPos> data = new ArrayList<BlockPos>();

		for (int x = posX - range; x < posX + range * 2; x++) {
			for (int y = posY - range; y < posY + range * 2; y++) {
				for (int z = posZ - range; z < posZ + range * 2; z++) {
					BlockPos position = new BlockPos(x, y, z);

					if (Arrays.asList(types).contains(world.getBlockState(position).getBlock())) {
						data.add(position);
					}
				}
			}
		}

		return data;
	}

	public static ArrayList<BlockPos> getCoordDataInRangeExcluding(int posX, int posY, int posZ, int range, World world, Block... types) {
		ArrayList<BlockPos> data = new ArrayList<BlockPos>();

		for (int x = posX - range; x < posX + range * 2; x++) {
			for (int y = posY - range; y < posY + range * 2; y++) {
				for (int z = posZ - range; z < posZ + range * 2; z++) {
					BlockPos coordData = new BlockPos(x, y, z);
					Block block = world.getBlockState(coordData).getBlock();

					if (!Arrays.asList(types).contains(block)) {
						data.add(coordData);
					}
				}
			}
		}

		return data;
	}

	public static String getDomain(Block block) {
		String domain = "minecraft:";

		if (block.getUnlocalizedName().contains(":")) {
			domain = (block.getUnlocalizedName().split(":")[0] + ":").replace("tile.", "");
		}

		return domain;
	}

	public static void setCreativeTab(Block block, CreativeTabs tab) {
		if (tab != null) {
			block.setCreativeTab(tab);
		}
	}
}