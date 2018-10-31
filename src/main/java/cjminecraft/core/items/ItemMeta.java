package cjminecraft.core.items;

import java.util.List;

import cjminecraft.core.util.EnumUtils;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.EnumHelper;

/**
 * An item which uses metadata
 * 
 * @author CJMinecraft
 *
 * @param <E>
 *            The enum which the item uses
 */
public class ItemMeta<E extends Enum<E> & IStringSerializable> extends Item {

	private E[] values;

	public ItemMeta(final Class<E> enumClass) {
		this.values = EnumUtils.getEnumValues(enumClass);
		setHasSubtypes(true);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (isInCreativeTab(tab)) {
			for (E value : this.values)
				items.add(new ItemStack(this, 1, value.ordinal()));
		}
	}

	@Override
	public String getUnlocalizedNameInefficiently(ItemStack stack) {
		return this.getTranslationKey() + "."
				+ this.values[Math.min(stack.getItemDamage(), this.values.length - 1)].getName();
	}

}
