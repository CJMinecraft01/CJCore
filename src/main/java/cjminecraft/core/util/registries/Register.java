package cjminecraft.core.util.registries;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.jcraft.jorbis.Block;

import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

/**
 * When applied to a class it will mark the class as one which utilises the
 * other register annotations and thus will mean that the other annotations are
 * used to trigger objects to get registered.
 * 
 * @author CJMinecraft
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Register {

	/**
	 * @return The modid of the mod which is registering objects in order to set
	 *         registry names correctly
	 */
	String modid();

	/**
	 * Will register a block with the given registry name. The unlocalized name
	 * will be set to the {@link #registryName()} by default. This can be
	 * disabled by setting the {@link #unlocalizedName()}. The unlocalized name
	 * can optionally not be set by changing {@link #setUnlocalizedName()}
	 * 
	 * @author CJMinecraft
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.FIELD })
	public static @interface RegisterBlock {

		String registryName();

		String unlocalizedName() default "";

		boolean setUnlocalizedName() default true;

	}

	/**
	 * Will register an item with the given registry name. The unlocalized name
	 * will be set to the {@link #registryName()} by default. This can be
	 * disabled by setting the {@link #unlocalizedName()}. The unlocalized name
	 * can optionally not be set by changing {@link #setUnlocalizedName()}
	 * 
	 * @author CJMinecraft
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.FIELD })
	public static @interface RegisterItem {

		String registryName();

		String unlocalizedName() default "";

		boolean setUnlocalizedName() default true;

	}

	/**
	 * Will register an item block with the given registry name. The unlocalized
	 * name will be set to the {@link #registryName()} by default. This can be
	 * disabled by setting the {@link #unlocalizedName()}. The unlocalized name
	 * can optionally not be set by changing {@link #setUnlocalizedName()}. <br>
	 * <br>
	 * If a custom item block is required, set {@link #customItemBlock()} to
	 * <code>true</code> (normally <code>false</code>). Ensure the block has the
	 * {@link ICustomItemBlock} interface on it so the custom item block is
	 * registered
	 * 
	 * @author CJMinecraft
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.FIELD })
	public static @interface RegisterItemBlock {

		String registryName();

		String unlocalizedName() default "";

		boolean customItemBlock() default false;

		boolean setUnlocalizedName() default true;

	}

	/**
	 * Will register a TESR ({@link TileEntitySpecialRenderer}) using the given
	 * {@link #tileEntityClass()} and {@link #renderClass()}
	 * 
	 * @author CJMinecraft
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.FIELD })
	public static @interface RegisterTESR {

		Class tileEntityClass();

		Class renderClass();

	}

	/**
	 * Will register a {@link TileEntity} using the given
	 * {@link #tileEntityClass()} and {@link #key()}
	 * 
	 * @author CJMinecraft
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.FIELD })
	public static @interface RegisterTileEntity {

		Class tileEntityClass();

		String key();

	}

	/**
	 * Will register an item or blocks render. <br>
	 * <br>
	 * If it has variants, change {@link #hasVariants()} and set the variants
	 * using {@link #variants()}.
	 * 
	 * @author CJMinecraft
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.FIELD })
	public static @interface RegisterRender {

		boolean hasVariants() default false;

		String[] variants() default {};
	}

	/**
	 * States that the given method is where blocks will be initialised. Will
	 * get called before the blocks are registered
	 * 
	 * @author CJMinecraft
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD })
	public static @interface RegisterBlockInit {
	}

	/**
	 * States that the given method is where items will be initialised. Will get
	 * called before the items are registered
	 * 
	 * @author CJMinecraft
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD })
	public static @interface RegisterItemInit {
	}

}
