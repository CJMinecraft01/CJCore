package cjminecraft.core.items;

import cjminecraft.core.blocks.BlockMeta;
import cjminecraft.core.util.EnumUtils;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;

/**
 * An item block which uses meta data. Used by {@link BlockMeta}
 * 
 * @author CJMinecraft
 *
 * @param <E>
 *            The enum which the item uses
 */
public class ItemBlockMeta<E extends Enum<E> & IStringSerializable> extends ItemBlock {

	private E[] values;

	/**
	 * Create a new meta item block using the different values of the given enum
	 * class
	 * 
	 * @param enumClass
	 *            The enum class containing the different meta values
	 * @param block
	 *            The block which the {@link ItemBlock} will use
	 */
	public ItemBlockMeta(final Class<E> enumClass, Block block) {
		super(block);
		this.values = EnumUtils.getEnumValues(enumClass);
		setHasSubtypes(true);
		this.setMaxDamage(0);
	}

	@Override
	public int getMetadata(int damage) {
		return damage;
	}

	@Override
	public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> items) {
		for (E value : this.values)
			items.add(new ItemStack(this, 1, value.ordinal()));
	}

	@Override
	public String getUnlocalizedNameInefficiently(ItemStack stack) {
		return this.getUnlocalizedName() + "."
				+ this.values[Math.min(stack.getItemDamage(), this.values.length - 1)].getName();
	}

}
