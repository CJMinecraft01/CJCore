package cjminecraft.core.util.registries;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Register {

	String modid();

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.FIELD })
	public static @interface RegisterBlock {

		String registryName();

		String unlocalizedName() default "";

	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	public static @interface RegisterItem {

	    String registryName();

	    String unlocalizedName() default "";

	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	public static @interface RegisterItemBlock {

	    String registryName();

	    String unlocalizedName() default "";

	    boolean customItemBlock() default false;

	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	public static @interface RegisterTESR {

	    Class tileEntityClass();
	    Class renderClass();

	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	public static @interface RegisterRender {

	    boolean hasVariants() default false;

	    String[] variants() default {};
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD })
	public static @interface RegisterBlockInit {}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD })
	public static @interface RegisterItemInit {}

}
