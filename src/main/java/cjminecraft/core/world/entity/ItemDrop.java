package cjminecraft.core.world.entity;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

/**
 * Created to better manage drop rates of itemstacks from entities. Drops can be
 * managed in a single location versus per entity.
 **/
public class ItemDrop {
	private ItemStack[]	itemstacks;
	private int			rate;
	private DropType	dropType;

	/**
	 * RATE_PERDROP_MULTIPLE - Drop rate applies to all items. Will drop all stacks.
	 * RATE_PERDROP_SINGLE - Drop rate applies to all items. Will drop one stack at
	 * random. RATE_INDIVIDUAL_MULTIPLE - Drop rate applies to each individual item.
	 * Will potentially drop multiple stacks. RATE_INDIVIDUAL_SINGLE - Drop rate
	 * applies to each individual item. Will drop only one stack. This mode
	 * increases chance an item will drop.
	 */
	public static enum DropType {
		RATE_PERDROP_MULTIPLE(0),
		RATE_PERDROP_SINGLE(1),
		RATE_PERSTACK_MULTIPLE(2),
		RATE_PERSTACK_SINGLE(3);

		private int id;

		DropType(int id) {
			this.id = id;
		}

		public int getId() {
			return id;
		}
	}

	/**
	 * Single stack ItemDrop
	 * 
	 * @param rate
	 *            - The rate at which this item will drop. Entering 5 will result in
	 *            the ItemStack dropping 5% of the time.
	 * @param stack
	 *            - The ItemStack instance this drop will consist of.
	 */
	public ItemDrop(int rate, ItemStack stack) {
		this(rate, DropType.RATE_PERDROP_MULTIPLE, new ItemStack[] { stack });
	}

	/**
	 * Multiple stack ItemDrop
	 * 
	 * @param rate
	 *            - The rate at which this item will drop. Entering 5 will result in
	 *            the ItemStack dropping 5% of the time.
	 * @param stacks
	 *            - The ItemStack instance this drop will consist of.
	 */
	public ItemDrop(int rate, ItemStack... stacks) {
		this(rate, DropType.RATE_PERDROP_MULTIPLE, stacks);
	}

	/**
	 * Multiple stack ItemDrop
	 * 
	 * @param rate
	 *            - The rate at which this item will drop. Entering 5 will result in
	 *            the ItemStack dropping 5% of the time.
	 * @param dropType
	 *            - The type of drop this is. See
	 *            {@link cjminecraft.core.world.entity.ItemDrop.DropType} for a
	 *            list of types.
	 * @param stacks
	 *            - The ItemStack instance this drop will consist of.
	 */
	public ItemDrop(int rate, DropType dropType, ItemStack... stacks) {
		this.itemstacks = stacks;
		this.dropType = dropType;
		this.rate = rate;
	}

	/**
	 * Try to drop this ItemDrop using the predefined options.
	 * 
	 * @param entity
	 *            - The entity dropping this ItemDrop.
	 * @return Returns true if an item was dropped.
	 */
	public boolean tryDrop(Entity entity) {
		return this.tryDrop(entity, this.rate, this.dropType);
	}

	/**
	 * Try to drop this ItemDrop, but with a modified drop rate.
	 * 
	 * @param entity
	 *            - The entity dropping this ItemDrop.
	 * @param rate
	 *            - The overridden drop rate. A drop rate of 0 will use the
	 *            predefined drop rate.
	 * @return Returns true if an item was dropped.
	 */
	public boolean tryDrop(Entity entity, int rate) {
		return this.tryDrop(entity, rate, this.dropType);
	}

	/**
	 * Try to drop this ItemDrop, but using a different drop type. See
	 * {@link cjminecraft.core.world.entity.ItemDrop.DropType} for a list of
	 * types.
	 * 
	 * @param entity
	 *            - The entity dropping this ItemDrop.
	 * @param type
	 *            - The modified drop type.
	 * @return Returns true if an item was dropped.
	 */
	public boolean tryDrop(Entity entity, DropType type) {
		return this.tryDrop(entity, this.rate, type);
	}

	/**
	 * Try to drop this ItemDrop, but set a different drop type. See
	 * {@link cjminecraft.core.world.entity.ItemDrop.DropType} for a list of
	 * types.
	 * 
	 * @param entity
	 *            - The entity dropping this ItemDrop.
	 * @param rate
	 *            - The overridden drop rate. A drop rate of 0 will use the
	 *            predefined drop rate.
	 * @param type
	 *            - The modified drop type.
	 * @return Returns true if an item was dropped.
	 */
	public boolean tryDrop(Entity entity, int rate, DropType type) {
		if (!entity.world.isRemote) {
			Random rand = new Random();

			switch (type) {
			case RATE_PERDROP_MULTIPLE: {
				if (rand.nextInt(100 / (rate == 0 ? this.rate : rate)) == 0) {
					for (ItemStack stack : itemstacks) {
						entity.entityDropItem(stack.copy(), 0F);
					}

					return true;
				}
			}

			case RATE_PERDROP_SINGLE: {
				if (rand.nextInt(100 / (rate == 0 ? this.rate : rate)) == 0) {
					entity.entityDropItem(itemstacks[rand.nextInt(itemstacks.length)].copy(), 0F);
					return true;
				}
			}

			case RATE_PERSTACK_MULTIPLE: {
				for (ItemStack stack : itemstacks) {
					if (rand.nextInt(100 / (rate == 0 ? this.rate : rate)) == 0) {
						entity.entityDropItem(stack.copy(), 0F);
					}
				}
				return true;
			}

			case RATE_PERSTACK_SINGLE: {
				for (ItemStack stack : itemstacks) {
					if (rand.nextInt(100 / (rate == 0 ? this.rate : rate)) == 0) {
						entity.entityDropItem(stack.copy(), 0F);
						return true;
					}
				}
			}
			}
		}

		return false;
	}

	/**
	 * @return An Array of ItemStack instances included in this drop.
	 */
	public ItemStack[] getItemstacks() {
		return itemstacks;
	}

	/**
	 * @return The drop rate of this drop.
	 */
	public int getRate() {
		return rate;
	}

	/**
	 * @return The type of drop this is. See
	 *         {@link cjminecraft.core.world.entity.ItemDrop.DropType} for a list
	 *         of types.
	 */
	public DropType getDropType() {
		return dropType;
	}
}