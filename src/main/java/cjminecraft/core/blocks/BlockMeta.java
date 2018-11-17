package cjminecraft.core.blocks;

import java.util.List;

import cjminecraft.core.items.ItemBlockMeta;
import cjminecraft.core.items.ItemMeta;
import cjminecraft.core.util.EnumUtils;
import cjminecraft.core.util.registries.ICustomItemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

/**
 * A block which has metadata (the {@link PropertyEnum} is setup automatically)
 * 
 * @author CJMinecraft
 *
 * @param <E>
 *            The enum which the {@link PropertyEnum} uses
 */
public class BlockMeta<E extends Enum<E> & IStringSerializable> extends Block implements ICustomItemBlock {

	private PropertyEnum<E> variant;

	private final Class<E> enumClass;
	private E[] values;

	/**
	 * Create a new meta block using the different values of the given enum
	 * class and the given name of the property
	 * 
	 * @param enumClass
	 *            The enum class containing the different meta values used by
	 *            the {@link PropertyEnum}
	 * @param propertyName
	 *            The name of the {@link PropertyEnum}
	 * @param material
	 *            The material of the block
	 */
	public BlockMeta(final Class<E> enumClass, String propertyName, Material material) {
		this(enumClass, propertyName, material, material.getMaterialMapColor());
	}

	/**
	 * Create a new meta block using the different values of the given enum
	 * class and the given name of the property
	 * 
	 * @param enumClass
	 *            The enum class containing the different meta values used by
	 *            the {@link PropertyEnum}
	 * @param propertyName
	 *            The name of the {@link PropertyEnum}
	 * @param material
	 *            The material of the block
	 * @param mapColor
	 *            The color of this block on a map
	 */
	public BlockMeta(final Class<E> enumClass, String propertyName, Material material, MapColor mapColor) {
		super(material, mapColor);
		this.enumClass = enumClass;
		this.values = EnumUtils.getEnumValues(enumClass);
		this.variant = PropertyEnum.<E>create(propertyName, enumClass);
		this.setDefaultState(this.blockState.getBaseState().withProperty(this.variant, this.values[0]));
	}

	/**
	 * @return The variant {@link PropertyEnum}
	 */
	public PropertyEnum<E> getVariant() {
		return this.variant;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { this.variant });
	}

	@Override
	public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> items) {
		for (E value : this.values)
			items.add(new ItemStack(this, 1, value.ordinal()));
	}
	
	@Override
	public int damageDropped(IBlockState state) {
		return state.getValue(this.variant).ordinal();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(this.variant, this.values[meta]);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(this.variant).ordinal();
	}

	@Override
	public ItemBlock getCustomItemBlock() {
		return new ItemBlockMeta<E>(this.enumClass, this);
	}

}
