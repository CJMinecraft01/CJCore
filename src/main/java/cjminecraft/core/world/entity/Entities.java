package cjminecraft.core.world.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import cjminecraft.core.access.AccessHandler;
import cjminecraft.core.world.Worlds;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Entities {
	private static Entity pointedEntity;

	/**
	 * Get the first Entity instance of the specified class type found, within specified range, at the specified world coordinates.
	 * 
	 * @param world
	 *            - World instance to scan for entities in
	 * @param entityClass
	 *            - Entity class type to scan for.
	 * @param data
	 *            - The CoordData containing the coordinates to start scanning at.
	 * @param range
	 *            - Range of blocks to scan within.
	 * @return First Entity instance found using the specified parameters.
	 */
	public static Entity getEntityInCoordsRange(World world, Class<? extends Entity> entityClass, BlockPos data, int range) {
		return getEntityInCoordsRange(world, entityClass, data, range, 16);
	}

	/**
	 * Checks if the specified position is safe for an entity to spawn at.
	 * 
	 * @param pos
	 *            - The position we are checking.
	 * @param world
	 *            - The world instance we are checking in.
	 * @return true if the position is safe.
	 */

	public static boolean isPositionSafe(BlockPos pos, World world) {
		if (pos != null && world != null) {
			BlockPos newPos = new BlockPos(pos.getX(), pos.getY(), pos.getZ());
			BlockPos newPosBelow = new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ());

			return world.getBlockState(newPosBelow) != net.minecraft.init.Blocks.AIR && world.getBlockState(newPos) == net.minecraft.init.Blocks.AIR;
		}

		return false;
	}

	/**
	 * Gets a safe position for the entity to spawn at from the given position.
	 * 
	 * @param pos
	 *            - The position that we should check around.
	 * @param world
	 *            - The world we're checking for a safe position in.
	 * @return The safe position.
	 */
	public static BlockPos getSafePositionAboveBelow(BlockPos pos, World world) {
		BlockPos newSafePosition = Worlds.getNextSafePositionAbove(pos, world);

		if (newSafePosition == null) {
			newSafePosition = Worlds.getNextSafePositionBelow(pos, world);
		}

		return newSafePosition;
	}

	/**
	 * Get the first Entity instance of the specified class type found, within specified range, at the specified world coordinates, within specified height.
	 * 
	 * @param world
	 *            - World instance to scan for entities in
	 * @param entityClass
	 *            - Entity class type to scan for.
	 * @param data
	 *            - The CoordData containing the coordinates to start scanning at.
	 * @param range
	 *            - Range of blocks to scan within.
	 * @param height
	 *            - Height to scan for entities within
	 * @return First Entity instance found using the specified parameters.
	 */
	public static Entity getEntityInCoordsRange(World world, Class<? extends Entity> entityClass, BlockPos data, int range, int height) {
		List<? extends Entity> entities = getEntitiesInCoordsRange(world, entityClass, data, range, height);

		return entities.size() >= 1 ? (Entity) entities.get(world.rand.nextInt(entities.size())) : null;
	}

	/**
	 * Get a random Entity instance of the specified class type found, within specified range, at the specified world coordinates, within specified height.
	 * 
	 * @param world
	 *            - World instance to scan for entities in
	 * @param entityClass
	 *            - Entity class type to scan for.
	 * @param data
	 *            - The CoordData containing the coordinates to start scanning at.
	 * @param range
	 *            - Range of blocks to scan within.
	 * @return First Entity instance found using the specified parameters.
	 */
	public static Entity getRandomEntityInCoordsRange(World world, Class<? extends Entity> entityClass, BlockPos data, int range) {
		return getRandomEntityInCoordsRange(world, entityClass, data, range, 16);
	}

	/**
	 * Get a random Entity instance of the specified class type found, within specified range, at the specified world coordinates, within specified height.
	 * 
	 * @param world
	 *            - World instance to scan for entities in
	 * @param entityClass
	 *            - Entity class type to scan for.
	 * @param data
	 *            - The CoordData containing the coordinates to start scanning at.
	 * @param range
	 *            - Range of blocks to scan within.
	 * @param height
	 *            - Height to scan for entities within
	 * @return First Entity instance found using the specified parameters.
	 */
	public static Entity getRandomEntityInCoordsRange(World world, Class<? extends Entity> entityClass, BlockPos data, int range, int height) {
		List<? extends Entity> entities = getEntitiesInCoordsRange(world, entityClass, data, range, height);

		return entities.size() > 1 ? (Entity) entities.get((new Random()).nextInt(entities.size())) : null;
	}

	/**
	 * Gets all Entity instances of the specified class type found, within specified range, at the specified world coordinates, within specified height.
	 * 
	 * @param world
	 *            - World instance to scan for entities in
	 * @param entityClass
	 *            - Entity class type to scan for.
	 * @param data
	 *            - The CoordData containing the coordinates to start scanning at.
	 * @param range
	 *            - Range of blocks to scan within.
	 * @return All the Entity instances found using the specified parameters.
	 */
	public static <T extends Entity> List<T> getEntitiesInCoordsRange(World world, Class<T> entityClass, BlockPos data, int range) {
		return getEntitiesInCoordsRange(world, entityClass, data, range, 16);
	}

	/**
	 * Gets all Entity instances of the specified class type found, within specified range, at the specified world coordinates, within specified height.
	 * 
	 * @param world
	 *            - World instance to scan for entities in
	 * @param entityClass
	 *            - Entity class type to scan for.
	 * @param data
	 *            - The CoordData containing the coordinates to start scanning at.
	 * @param range
	 *            - Range of blocks to scan within.
	 * @param height
	 *            - Height to scan for entities within
	 * @return All the Entity instances found using the specified parameters.
	 */
	public static <T extends Entity> List<T> getEntitiesInCoordsRange(World world, Class<? extends T> entityClass, BlockPos data, int range, int height) {
		return world.getEntitiesWithinAABB(entityClass, new AxisAlignedBB(data.getX(), data.getY(), data.getZ(), data.getX() + 1, data.getY() + 1, data.getZ() + 1).expand(range * 2, height, range * 2));
	}

	public static RayTraceResult.Type getMovingObjectType(int ordinal) {
		for (RayTraceResult.Type type : RayTraceResult.Type.values()) {
			if (type.ordinal() == ordinal) {
				return type;
			}
		}

		return null;
	}

	/**
	 * @param e1
	 *            - The entity that entityLooking is looking for.
	 * @param e2
	 *            - The entity that is looking for the first entity.
	 * @return Returns true if the first Entity can be seen by the second Entity.
	 */
	public static boolean canEntityBeSeenBy(Entity e1, Entity e2) {
		return rayTraceBlocks(e1, e2) == null;
	}

	public static boolean canEntityBeSeenBy(Entity e, BlockPos pos) {
		return rayTraceBlocks(e, pos) == null;
	}

	public static boolean canCoordBeSeenBy(World world, BlockPos p1, BlockPos p2) {
		return rayTraceBlocks(world, p1, p2) == null;
	}

	/**
	 * Finds what block or object the mouse is over at the specified partial tick time. Args: partialTickTime
	 */
	@SideOnly(Side.CLIENT)
	public static RayTraceResult rayTraceSpecial(double reach, float partialTicks) {
		if (AccessHandler.getMinecraft().getRenderViewEntity() != null) {
			if (AccessHandler.getMinecraft().world != null) {
				pointedEntity = null;
				double distance = reach;
				Vec3d renderPosition = AccessHandler.getMinecraft().getRenderViewEntity().getPositionEyes(partialTicks);
				Vec3d lookVec = AccessHandler.getMinecraft().getRenderViewEntity().getLook(partialTicks);
				Vec3d lookPos = renderPosition.addVector(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach);
				RayTraceResult blockTrace = rayTraceBlocks(AccessHandler.getMinecraft().world, AccessHandler.getMinecraft().getRenderViewEntity().getPositionEyes(partialTicks), lookPos, false, true, true);

				if (blockTrace != null) {
					distance = blockTrace.hitVec.distanceTo(renderPosition);
				}

				pointedEntity = null;
				Vec3d hitVec = null;
				List list = AccessHandler.getMinecraft().world.getEntitiesWithinAABBExcludingEntity(AccessHandler.getMinecraft().getRenderViewEntity(), AccessHandler.getMinecraft().getRenderViewEntity().getEntityBoundingBox().expand(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach).expand((double) 1F, (double) 1F, (double) 1F));
				double entityDist = distance;

				for (int idx = 0; idx < list.size(); ++idx) {
					Entity entity = (Entity) list.get(idx);

					if (entity.canBeCollidedWith()) {
						AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().expand((double) entity.getCollisionBorderSize(), (double) entity.getCollisionBorderSize(), (double) entity.getCollisionBorderSize());
						RayTraceResult movingobjectposition = axisalignedbb.calculateIntercept(renderPosition, lookPos);

						if (axisalignedbb.contains(renderPosition)) {
							if (0.0D < entityDist || entityDist == 0.0D) {
								pointedEntity = entity;
								hitVec = movingobjectposition == null ? renderPosition : movingobjectposition.hitVec;
								entityDist = 0.0D;
							}
						} else if (movingobjectposition != null) {
							double distToHit = renderPosition.distanceTo(movingobjectposition.hitVec);

							if (distToHit < entityDist || entityDist == 0.0D) {
								if (entity == AccessHandler.getMinecraft().getRenderViewEntity().getRidingEntity() && !entity.canRiderInteract()) {
									if (entityDist == 0.0D) {
										pointedEntity = entity;
										hitVec = movingobjectposition.hitVec;
									}
								} else {
									pointedEntity = entity;
									hitVec = movingobjectposition.hitVec;
									entityDist = distToHit;
								}
							}
						}
					}
				}

				if (pointedEntity != null && (entityDist < distance)) {
					return new RayTraceResult(pointedEntity, hitVec);
				} else if (blockTrace != null) {
					return blockTrace;
				}
			}
		}

		return null;
	}

	/**
	 * @param e1
	 *            - The entity that entityLooking is looking for.
	 * @param e2
	 *            - The entity that is looking for the first entity.
	 * @return Returns the MovingObjectPosition hit by the rayTrace.
	 */
	public static RayTraceResult rayTraceBlocks(Entity e1, Entity e2) {
		return e1 != null && e2 != null ? rayTraceBlocks(e1.world, e1.posX, e1.posY + (e1.height / 2), e1.posZ, e2.posX, e2.posY + e2.getEyeHeight(), e2.posZ) : null;
	}

	public static RayTraceResult rayTraceBlocks(Entity e, BlockPos p) {
		return e != null && p != null ? rayTraceBlocks(e.world, e.posX, e.posY + (e.height / 2), e.posZ, p.getX(), p.getY(), p.getZ()) : null;
	}

	public static RayTraceResult rayTraceBlocks(World world, BlockPos p1, BlockPos p2) {
		return p1 != null && p2 != null ? rayTraceBlocks(world, p1.getX(), p1.getY(), p1.getZ(), p2.getX(), p2.getY(), p2.getZ()) : null;
	}

	public static RayTraceResult rayTraceBlocks(World world, double x1, double y1, double z1, double x2, double y2, double z2) {
		return world != null ? rayTraceBlocks(world, new Vec3d(x1, y1, z1), new Vec3d(x2, y2, z2), false, false, false) : null;
	}

	public static RayTraceResult rayTraceAll(Entity entity, int reach) {
		return rayTraceAll(entity.world, new Vec3d(entity.posX, entity.posY, entity.posZ), entity.rotationYaw, entity.rotationPitch, reach, new ArrayList<Entity>(Arrays.asList(new Entity[] { entity })));
	}

	public static RayTraceResult rayTraceAll(World world, Vec3d pos, float rotationYaw, float rotationPitch, int reach, ArrayList<Entity> exclude) {
		Vec3d lookVec = getLookVector(rotationYaw, rotationPitch);

		Entity entityHit = null;
		Vec3d hitVec = null;
		Vec3d posHit = null;

		if (lookVec != null) {
			posHit = pos.addVector(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach);
			List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos.x, pos.y, pos.z, pos.x + 1F, pos.y + 1F, pos.z + 1F).expand(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach).expand(1.0F, 1.0F, 1.0F));

			for (Entity e : entities) {
				if (e != null && e.canBeCollidedWith() && !exclude.contains(e)) {
					float size = e.getCollisionBorderSize();
					AxisAlignedBB box = e.getEntityBoundingBox().expand(size, size, size);
					RayTraceResult movobjpos = box.calculateIntercept(pos, posHit);

					entityHit = e;

					if (movobjpos == null) {
						hitVec = pos;
					} else {
						hitVec = movobjpos.hitVec;
					}
				}
			}
		}

		if (entityHit != null && hitVec != null) {
			return new RayTraceResult(entityHit, hitVec);
		}

		if (posHit != null) {
			RayTraceResult blockHitVec = rayTraceBlocks(world, pos, posHit, true, true, true);

			if (blockHitVec != null) {
				return blockHitVec;
			}
		}

		return null;
	}

	public static RayTraceResult rayTraceBlocks(World world, BlockPos pos, BlockPos pos2, boolean hitLiquid, boolean ignoreBlocksWithoutBoundingBox, boolean returnLastUncollidableBlock) {
		return rayTraceBlocks(world, new Vec3d(pos.getX(), pos.getY(), pos.getZ()), new Vec3d(pos2.getX(), pos2.getY(), pos2.getZ()), hitLiquid, ignoreBlocksWithoutBoundingBox, returnLastUncollidableBlock);
	}

	public static RayTraceResult rayTraceBlocks(World world, Vec3d pos, Vec3d pos2, boolean hitLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock) {
		if (!Double.isNaN(pos.x) && !Double.isNaN(pos.y) && !Double.isNaN(pos.z)) {
			if (!Double.isNaN(pos2.x) && !Double.isNaN(pos2.y) && !Double.isNaN(pos2.z)) {
				BlockPos blockpos = new BlockPos(MathHelper.floor(pos.x), MathHelper.floor(pos.y), MathHelper.floor(pos.z));
				IBlockState blockstate = world.getBlockState(blockpos);
				Block block = blockstate.getBlock();
				int posMeta = block.getMetaFromState(blockstate);
				int posX = blockpos.getX();
				int posY = blockpos.getY();
				int posZ = blockpos.getZ();

				if ((!ignoreBlockWithoutBoundingBox || blockstate.getCollisionBoundingBox(world, blockpos) != null) && block.isCollidable()) {
					RayTraceResult obj = blockstate.collisionRayTrace(world, blockpos, pos, pos2);

					if (obj != null) {
						return obj;
					}
				}

				RayTraceResult movObjPos = null;
				int dist = 200;

				double tX = pos.x;
				double tY = pos.y;
				double tZ = pos.z;

				while (dist-- >= 0) {
					if (Double.isNaN(pos.x) || Double.isNaN(pos.y) || Double.isNaN(pos.z)) {
						return null;
					}

					if (posX == pos2.x && posY == pos2.y && posZ == pos2.z) {
						return returnLastUncollidableBlock ? movObjPos : null;
					}

					boolean endX = true;
					boolean endY = true;
					boolean endZ = true;
					double distX = 999.0D;
					double distY = 999.0D;
					double distZ = 999.0D;

					if (pos2.x > posX) {
						distX = (double) posX + 1.0D;
					} else if (pos2.x < posX) {
						distX = (double) posX + 0.0D;
					} else {
						endX = false;
					}

					if (pos2.y > posY) {
						distY = (double) posY + 1.0D;
					} else if (pos2.y < posY) {
						distY = (double) posY + 0.0D;
					} else {
						endY = false;
					}

					if (pos2.z > posZ) {
						distZ = (double) posZ + 1.0D;
					} else if (pos2.z < posZ) {
						distZ = (double) posZ + 0.0D;
					} else {
						endZ = false;
					}

					double dX = 999.0D;
					double dY = 999.0D;
					double dZ = 999.0D;
					double displacementX = pos2.x - pos.x;
					double displacementY = pos2.y - pos.y;
					double displacementZ = pos2.z - pos.z;

					if (endX) {
						dX = (distX - pos.x) / displacementX;
					}

					if (endY) {
						dY = (distY - pos.y) / displacementY;
					}

					if (endZ) {
						dZ = (distZ - pos.z) / displacementZ;
					}

					byte side;

					if (dX < dY && dX < dZ) {
						if (pos2.x > posX) {
							side = 4;
						} else {
							side = 5;
						}

						tX = distX;
						tY += displacementY * dX;
						tZ += displacementZ * dX;
					} else if (dY < dZ) {
						if (pos2.y > posY) {
							side = 0;
						} else {
							side = 1;
						}

						tX += displacementX * dY;
						tY = distY;
						tZ += displacementZ * dY;
					} else {
						if (pos2.z > posZ) {
							side = 2;
						} else {
							side = 3;
						}

						tX += displacementX * dZ;
						tY += displacementY * dZ;
						tZ = distZ;
					}

					pos = new Vec3d(tX, tY, tZ);

					posX = (int) ((double) MathHelper.floor(pos.x));

					if (side == 5) {
						--posX;
					}

					posY = (int) ((double) MathHelper.floor(pos.y));

					if (side == 1) {
						--posY;
					}

					posZ = (int) ((double) MathHelper.floor(pos.z));

					if (side == 3) {
						--posZ;
					}

					BlockPos newPos = new BlockPos(posX, posY, posZ);
					IBlockState newBlockstate = world.getBlockState(newPos);
					Block newPosBlock = newBlockstate.getBlock();

					if (!ignoreBlockWithoutBoundingBox || newBlockstate.getCollisionBoundingBox(world, newPos) != null) {
						if (newPosBlock.isCollidable()) {
							RayTraceResult obj = newBlockstate.collisionRayTrace(world, newPos, pos, pos2);

							if (obj != null) {
								return obj;
							}
						} else {
							movObjPos = new RayTraceResult(RayTraceResult.Type.MISS, pos, EnumFacing.getFront(side), newPos);
						}
					}
				}

				return returnLastUncollidableBlock ? movObjPos : null;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * @param partialTicks
	 *            - The partial tick time or amount of partial ticks between each CPU tick.
	 * @return an interpolated look vector used for ray tracing.
	 */
	public static Vec3d getLookVector(float rotationYaw, float rotationPitch) {
		return getLookVector(rotationYaw, rotationPitch, 0F, 0F, 1.0F);
	}

	/**
	 * @param partialTicks
	 *            - The partial tick time or amount of partial ticks between each CPU tick.
	 * @return an interpolated look vector used for ray tracing.
	 */
	public static Vec3d getLookVector(float rotationYaw, float rotationPitch, float prevRotationYaw, float prevRotationPitch, float partialTicks) {
		float f1;
		float f2;
		float f3;
		float f4;

		if (partialTicks == 1.0F) {
			f1 = MathHelper.cos(-rotationYaw * 0.017453292F - (float) Math.PI);
			f2 = MathHelper.sin(-rotationYaw * 0.017453292F - (float) Math.PI);
			f3 = -MathHelper.cos(-rotationPitch * 0.017453292F);
			f4 = MathHelper.sin(-rotationPitch * 0.017453292F);
			return new Vec3d((double) (f2 * f3), (double) f4, (double) (f1 * f3));
		} else {
			f1 = prevRotationPitch + (rotationPitch - prevRotationPitch) * partialTicks;
			f2 = prevRotationYaw + (rotationYaw - prevRotationYaw) * partialTicks;
			f3 = MathHelper.cos(-f2 * 0.017453292F - (float) Math.PI);
			f4 = MathHelper.sin(-f2 * 0.017453292F - (float) Math.PI);
			float f5 = -MathHelper.cos(-f1 * 0.017453292F);
			float f6 = MathHelper.sin(-f1 * 0.017453292F);
			return new Vec3d((double) (f4 * f5), (double) f6, (double) (f3 * f5));
		}
	}

	/**
	 * Constructs a new Entity instance from the specified class name in the specified world.
	 * 
	 * @param world
	 *            - World to construct the Entity instance in.
	 * @param name
	 *            - String name of the entity class of which will be constructed.
	 * @return Entity instance constructed using this method.
	 */
	public static Entity constructEntityViaClasspath(World world, String name) {
		try {
			return constructEntity(world, (Class<? extends Entity>) Class.forName(name));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Constructs a new Entity instance from the specified class in the specified world.
	 * 
	 * @param world
	 *            - World to construct the Entity instance in.
	 * @param c
	 *            - The entity class of which will be constructed.
	 * @return Entity instance constructed using this method.
	 */
	public static Entity constructEntity(World world, Class<? extends Entity> c) {
		if (world == null) {
			System.out.println("World object null while attempting to construct entity.");
			return null;
		}

		if (c == null) {
			System.out.println("Entity class null while attempting to construct entity.");
			return null;
		}

		try {
			return (c.getConstructor(World.class)).newInstance(new Object[] { world });
		} catch (Exception e) {
			System.out.println("Failed to construct entity: " + (c != null ? c.getName() : c));
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Causes the facer entity to face the faced entity.
	 * 
	 * @param facer
	 *            - The Entity that is going to be facing the faced entity.
	 * @param faced
	 *            - The Entity that is going to be faced.
	 * @param maxYaw
	 *            - The max rotation yaw that the facer can rotate.
	 * @param maxPitch
	 *            - The max rotation pitch that the facer can rotate.
	 */
	public static void faceEntity(Entity facer, Entity faced, float maxYaw, float maxPitch) {
		double xDistance = faced.posX - facer.posX;
		double zDistance = faced.posZ - facer.posZ;
		double yDistance;

		if (faced instanceof EntityLivingBase) {
			EntityLivingBase entitylivingbase = (EntityLivingBase) faced;
			yDistance = entitylivingbase.posY + entitylivingbase.getEyeHeight() - (facer.posY + facer.getEyeHeight());
		} else {
			yDistance = (faced.getEntityBoundingBox().minY + faced.getEntityBoundingBox().maxY) / 2.0D - (facer.posY + facer.getEyeHeight());
		}

		double d3 = MathHelper.sqrt(xDistance * xDistance + zDistance * zDistance);
		float f2 = (float) (Math.atan2(zDistance, xDistance) * 180.0D / Math.PI) - 90.0F;
		float f3 = (float) (-(Math.atan2(yDistance, d3) * 180.0D / Math.PI));
		facer.rotationPitch = updateRotation(facer.rotationPitch, f3, maxPitch);
		facer.rotationYaw = updateRotation(facer.rotationYaw, f2, maxYaw);
	}

	/**
	 * @param currentRotation
	 *            - The current rotation value.
	 * @param targetRotation
	 *            - The target rotation value.
	 * @param maxChange
	 *            - The maximum rotation change allowed.
	 * @return Amount of rotation that is results based on the current, target, and max rotation.
	 */
	public static float updateRotation(float currentRotation, float targetRotation, float maxChange) {
		float newRotation = MathHelper.wrapDegrees(targetRotation - currentRotation);
		return currentRotation + (newRotation > maxChange ? maxChange : newRotation < -maxChange ? -maxChange : maxChange);
	}

	/**
	 * Apply a block collision for the provided Entity instance.
	 * 
	 * @param entity
	 *            - The entity to apply a collision for
	 */
	public static void applyCollision(Entity entity) {
		int minX = MathHelper.floor(entity.getEntityBoundingBox().minX + 0.001D);
		int minY = MathHelper.floor(entity.getEntityBoundingBox().minY + 0.001D);
		int minZ = MathHelper.floor(entity.getEntityBoundingBox().minZ + 0.001D);
		int maxX = MathHelper.floor(entity.getEntityBoundingBox().maxX - 0.001D);
		int maxY = MathHelper.floor(entity.getEntityBoundingBox().maxY - 0.001D);
		int maxZ = MathHelper.floor(entity.getEntityBoundingBox().maxZ - 0.001D);

		if (entity.world.isBlockLoaded(entity.getPosition())) {
			for (int x = minX; x <= maxX; ++x) {
				for (int y = minY; y <= maxY; ++y) {
					for (int z = minZ; z <= maxZ; ++z) {
						BlockPos pos = new BlockPos(x, y, z);
						IBlockState blockstate = entity.world.getBlockState(pos);
						Block block = blockstate.getBlock();

						try {
							block.onEntityCollidedWithBlock(entity.world, pos, blockstate, entity);
						} catch (Throwable throwable) {
							System.out.println("Exception while handling entity collision with block.");
						}
					}
				}
			}
		}
	}

	public static boolean isInLava(Entity entity) {
		return isInMaterial(entity, Material.LAVA);
	}

	public static boolean isInWater(Entity entity) {
		return isInMaterial(entity, Material.WATER);
	}

	public static boolean isInMaterial(Entity entity, Material material) {
		if (entity != null && entity.world != null && entity.getEntityBoundingBox() != null) {
			return entity.world.isMaterialInBB(entity.getEntityBoundingBox().expand(-0.10000000149011612D, -0.4000000059604645D, -0.10000000149011612D), material);
		}

		return false;
	}

	public static BlockPos getSafeLocationAround(Entity toCheck, BlockPos around) {
		ArrayList<BlockPos> potentialLocations = cjminecraft.core.world.block.Blocks.getCoordDataInRangeIncluding((int) around.getX(), (int) around.getY(), (int) around.getZ(), 2, toCheck.world, Blocks.AIR);

		for (BlockPos potentialLocation : potentialLocations) {

			Block blockAt = toCheck.world.getBlockState(toCheck.getPosition()).getBlock();
			Block blockBelow = toCheck.world.getBlockState(toCheck.getPosition().add(0, -1, 0)).getBlock();

			if (blockAt != null && blockBelow != null) {
				if (blockAt == Blocks.AIR) {
					if (isGround(blockBelow)) {
						double entityWidth = toCheck.getEntityBoundingBox().maxX - toCheck.getEntityBoundingBox().minX;
						double entityHeight = toCheck.getEntityBoundingBox().maxY - toCheck.getEntityBoundingBox().minY;
						double entityDepth = toCheck.getEntityBoundingBox().maxZ - toCheck.getEntityBoundingBox().minZ;

						entityWidth = entityWidth < 1 ? 1 : entityWidth;
						entityDepth = entityDepth < 1 ? 1 : entityDepth;
						entityHeight = entityHeight < 1 ? 1 : entityHeight;

						Block blockAbove = toCheck.world.getBlockState(toCheck.getPosition().add(0, entityHeight, 0)).getBlock();

						if (isSafe(blockAbove)) {

							Block blockToLeft = toCheck.world.getBlockState(toCheck.getPosition().add(-entityWidth, 0, 0)).getBlock();
							Block blockToRight = toCheck.world.getBlockState(toCheck.getPosition().add(entityWidth, 0, 0)).getBlock();
							Block blockInFront = toCheck.world.getBlockState(toCheck.getPosition().add(0, 0, entityDepth)).getBlock();
							Block blockInBack = toCheck.world.getBlockState(toCheck.getPosition().add(0, 0, -entityDepth)).getBlock();

							if (isSafe(blockToLeft) && isSafe(blockToRight) && isSafe(blockInFront) && isSafe(blockInBack)) {
								return potentialLocation;
							}
						}
					}
				}
			}
		}
		return null;
	}

	public static boolean isGround(IBlockState blockstate) {
		return blockstate.getMaterial().isSolid() && blockstate.getBlock() != Blocks.AIR;
	}

	public static boolean isGround(Block block) {
		return block.getDefaultState().getMaterial().isSolid() && block != Blocks.AIR;
	}

	public static boolean isSafe(Block block) {
		return block == Blocks.AIR;
	}

	public static Entity getEntityRiddenBy(Entity check) {
		return !check.getPassengers().isEmpty() && check.getPassengers().get(0) != null ? check.getPassengers().get(0) : null;
	}

	public static boolean isRiding(Entity check, Entity checkFor) {
		return isRiding(check, checkFor.getClass());
	}

	public static boolean isRiding(Entity check, Class<? extends Entity> type) {
		if (check.getPassengers() == null || check.getPassengers() != null && check.getPassengers().isEmpty()) {
			return false;
		}

		for (Entity entity : check.getPassengers()) {
			if (type.isInstance(entity)) {
				return true;
			}
		}

		return false;
	}

	public static boolean canPlaceEntityOnSide(World world, Block block, BlockPos pos, boolean skipBoundsCheck, int side, Entity entity, ItemStack stack) {
		return canPlaceEntityOnSide(world, block, pos, skipBoundsCheck, EnumFacing.getFront(side), entity, stack);
	}

	public static boolean canPlaceEntityOnSide(World world, Block block, BlockPos pos, boolean skipBoundsCheck, EnumFacing side, Entity entity, ItemStack stack) {
		IBlockState blockstate = world.getBlockState(pos);
		AxisAlignedBB box = skipBoundsCheck ? null : blockstate.getCollisionBoundingBox(world, pos);
		return box != null && !world.checkNoEntityCollision(box, entity) ? false : (blockstate.getMaterial() == Material.CIRCUITS && block == Blocks.ANVIL ? true : blockstate.getBlock().isReplaceable(world, pos) && block.isReplaceable(world, pos));
	}

	public static EnumFacing getEntityFacingRotY(Entity entity) {
		int dir = MathHelper.floor((entity.rotationYaw / 90) + 0.5) & 3;

		switch (dir) {
		case 0:
			return EnumFacing.SOUTH;
		case 1:
			return EnumFacing.WEST;
		case 2:
			return EnumFacing.NORTH;
		case 3:
			return EnumFacing.EAST;
		default:
			return EnumFacing.NORTH;
		}
	}

	public static EnumFacing getEntityFacingRotX(Entity entity) {
		int dir = MathHelper.floor((entity.rotationPitch / 90) + 0.5) & 3;
		System.out.println(dir);

		switch (dir) {
		case 3:
			return EnumFacing.UP;
		case 1:
			return EnumFacing.DOWN;
		default:
			return EnumFacing.NORTH;
		}
	}
}