package cjminecraft.core.world;

import java.util.Random;
import java.util.UUID;

import cjminecraft.core.world.entity.Entities;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenerator;

@SuppressWarnings("all")
public class Worlds {
	/**
	 * Create an explosion in the specified world, at the specified coordinates,
	 * with the specified effects.
	 * 
	 * @param entity
	 *            - The entity that triggered the explosion.
	 * @param world
	 *            - The world that the explosion should be created in.
	 * @param data
	 *            - The CoordData containing the coordinates to create an explosion
	 *            at.
	 * @param strength
	 *            - The strength of the explosion
	 * @param isFlaming
	 *            - Set to true if the explosion causes surrounding blocks to catch
	 *            on fire.
	 * @param isSmoking
	 *            - Set to true if the explosion emits smoke particles.
	 * @param doesBlockDamage
	 *            - Set to true if the explosion does physical Block damage.
	 * @return Return the instance of the explosion that was just created.
	 */
	public static Explosion createExplosion(Entity entity, World world, BlockPos data, float strength, boolean isFlaming, boolean isSmoking, boolean doesBlockDamage) {
		Explosion explosion = new Explosion(world, entity, data.getX(), data.getY(), data.getZ(), strength, isFlaming, isSmoking);

		if (doesBlockDamage) {
			explosion.doExplosionA();
		}

		explosion.doExplosionB(true);

		return explosion;
	}

	/**
	 * Gets the next safe position above the specified position
	 * 
	 * @param entity
	 *            - The position we're checking for safe positions above.
	 * @return The safe position.
	 */
	public static BlockPos getNextSafePositionAbove(BlockPos pos, World world) {
		for (int y = (int) pos.getY(); y < world.getHeight(); y++) {
			BlockPos position = new BlockPos(pos.getX(), y + 1, pos.getZ());

			if (Entities.isPositionSafe(position, world)) {
				return position;
			}
		}

		return pos;
	}

	public static boolean canSeeSky(BlockPos pos, World world) {
		for (int y = (int) pos.getY(); y < world.getHeight(); y++) {
			BlockPos position = new BlockPos(pos.getX(), y + 1, pos.getZ());

			if (world.getBlockState(position) != net.minecraft.init.Blocks.AIR) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Get the light intensity as an Integer at the specified coordinates in the
	 * specified world.
	 * 
	 * @param world
	 *            - World to check for brightness values in.
	 * @param BlockPos
	 *            - BlockPos containing coordinates of the location to check
	 *            brightness at.
	 * @return Returns light intensity of a block as an Integer.
	 */
	public static int getLightAtCoord(World world, BlockPos pos) {
		int sky = world.getLightFor(EnumSkyBlock.BLOCK, pos);
		int block = world.getLightFor(EnumSkyBlock.SKY, pos) - world.calculateSkylightSubtracted(0F);

		return Math.max(block, sky);
	}

	/**
	 * Gets the next safe position below the specified position
	 * 
	 * @param pos
	 *            - The position we're checking for safe positions below.
	 * @return The safe position.
	 */
	public static BlockPos getNextSafePositionBelow(BlockPos pos, World world) {
		for (int y = (int) pos.getY(); y > 0; y--) {
			BlockPos position = new BlockPos(pos.getX(), y - 1, pos.getZ());

			if (Entities.isPositionSafe(position, world)) {
				return position;
			}
		}

		return pos;
	}

	/**
	 * Generate a group of the specified Block in the World, a given amount of
	 * times, in a Chunk at the given CoordData's X and Z coords using the specified
	 * group size and seed.
	 * 
	 * @param world
	 *            - The World instance to generate in.
	 * @param generator
	 *            - The WorldGenerator instance to generate.
	 * @param seed
	 *            - The seed to generate random group coords at.
	 * @param genPerChunk
	 *            - The amount of times to generate this block group per chunk.
	 * @param chunkCoord
	 *            - The CoordData containing the X and Z coordinates of the Chunk to
	 *            generate in.
	 */
	public static void generateInChunk(World world, WorldGenerator generator, Random seed, int genPerChunk, BlockPos chunkCoord) {
		generateInChunk(world, generator, seed, genPerChunk, 0, 128, chunkCoord);
	}

	/**
	 * Generate a group of the specified Block in the World, a given amount of
	 * times, in a Chunk at the given CoordData's X and Z coords using the specified
	 * group size and seed.
	 * 
	 * @param world
	 *            - The World instance to generate in.
	 * @param generator
	 *            - The WorldGenerator instance to generate.
	 * @param seed
	 *            - The seed to generate random group coords at.
	 * @param genPerChunk
	 *            - The amount of times to generate this block group per chunk.
	 * @param levelStart
	 *            - The level that this block group can start generating on
	 * @param levelEnd
	 *            - The level that this block group can stop generating on
	 * @param chunkCoord
	 *            - The CoordData containing the X and Z coordinates of the Chunk to
	 *            generate in.
	 */
	public static void generateInChunk(World world, WorldGenerator generator, Random seed, int genPerChunk, int levelStart, int levelEnd, BlockPos pos) {
		for (int i = 0; i < genPerChunk; ++i) {
			int x = (int) pos.getX() + seed.nextInt(16);
			int y = levelStart + seed.nextInt(levelEnd);
			int z = (int) pos.getZ() + seed.nextInt(16);
			generator.generate(world, seed, new BlockPos(x, y, z));
		}
	}

	/**
	 * Generate a group of the specified Block in the World, a given amount of
	 * times, in a Chunk at the given CoordData's X and Z coords using the specified
	 * group size and seed.
	 * 
	 * @param world
	 *            - The World instance to generate in.
	 * @param generator
	 *            - The WorldGenerator instance to generate.
	 * @param seed
	 *            - The seed to generate random group coords at.
	 * @param genPerChunk
	 *            - The amount of times to generate this block group per chunk.
	 * @param chunkCoord
	 *            - The CoordData containing the X and Z coordinates of the Chunk to
	 *            generate in.
	 * @param biomes
	 *            - The BiomeGenBase instances to generate in.
	 */
	public static void generateInBiome(World world, WorldGenerator generator, Random seed, int genPerChunk, BlockPos chunkCoord, Biome[] biomes) {
		generateInBiome(world, generator, seed, genPerChunk, 0, 128, chunkCoord, biomes);
	}

	/**
	 * Generate a group of the specified Block in the World, a given amount of
	 * times, in a Chunk at the given CoordData's X and Z coords using the specified
	 * group size and seed.
	 * 
	 * @param world
	 *            - The World instance to generate in.
	 * @param generator
	 *            - The WorldGenerator instance to generate.
	 * @param seed
	 *            - The seed to generate random group coords at.
	 * @param genPerChunk
	 *            - The amount of times to generate this block group per chunk.
	 * @param levelStart
	 *            - The level that this block group can start generating on
	 * @param levelEnd
	 *            - The level that this block group can stop generating on
	 * @param pos
	 *            - The CoordData containing the X and Z coordinates of the Chunk to
	 *            generate in.
	 * @param biomes
	 *            - The BiomeGenBase instances to generate in.
	 */
	public static void generateInBiome(World world, WorldGenerator generator, Random seed, int genPerChunk, int levelStart, int levelEnd, BlockPos pos, Biome[] biomes) {
		for (Biome biome : biomes) {
			if (world.provider.getBiomeForCoords(pos) == biome) {
				generateInChunk(world, generator, seed, genPerChunk, levelStart, levelEnd, pos);
			}
		}
	}

	public static BlockPos randPos(Random seed, BlockPos pos, int width, int height) {
		return new BlockPos(pos.getX() + seed.nextInt(width), pos.getY() + seed.nextInt(height), pos.getZ() + seed.nextInt(width));
	}

	public static BlockPos randChunkPos(Random seed, BlockPos pos) {
		return randPos(seed, pos, 16, 128);
	}

	public static Entity getEntityByUUID(World world, UUID uuid) {
		for (Object o : world.loadedEntityList.toArray()) {
			if (o instanceof Entity) {
				Entity entity = (Entity) o;

				if (entity.getUniqueID().equals(uuid)) {
					return entity;
				}
			}
		}

		return null;
	}

	public static UUID uuidFromNBT(NBTTagCompound nbt, String key) {
		return uuidFromSignature(nbt.getString(key));
	}

	public static UUID uuidFromSignature(String signature) {
		if (signature != null && signature.matches("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[34][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}")) {
			return UUID.fromString(signature);
		}

		return null;
	}
}
