package cjminecraft.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import cjminecraft.core.CJCore;
import net.minecraftforge.classloading.FMLForgePlugin;
import net.minecraftforge.common.util.EnumHelper;

/**
 * Utilities for enums using reflection
 * 
 * @author CJMinecraft
 *
 */
public class EnumUtils {

	/**
	 * Gets the values of the given enum class. This code was extracted from the
	 * {@link EnumHelper#addEnum(Class, String, Class[], Object...)}
	 * 
	 * @param enumClass
	 *            The enum to get the values from
	 * @return the values of that enum (or null if there was an error)
	 */
	@Nullable
	public static <E extends Enum<E>> E[] getEnumValues(Class<E> enumClass) {
		Field valuesField = null;
		Field[] fields = enumClass.getDeclaredFields();

		for (Field field : fields) {
			String name = field.getName();
			if (name.equals("$VALUES") || name.equals("ENUM$VALUES")) {
				valuesField = field;
				break;
			}
		}

		int flags = (FMLForgePlugin.RUNTIME_DEOBF ? Modifier.PUBLIC : Modifier.PRIVATE) | Modifier.STATIC
				| Modifier.FINAL | 0x1000 /* SYNTHETIC */;
		if (valuesField == null) {
			String valueType = String.format("[L%s;", enumClass.getName().replace('.', '/'));

			for (Field field : fields) {
				if ((field.getModifiers() & flags) == flags
						&& field.getType().getName().replace('.', '/').equals(valueType)) {
					valuesField = field;
					break;
				}
			}
		}

		if (valuesField == null) {
			final List<String> lines = Lists.newArrayList();
			lines.add(String.format("Could not find $VALUES field for enum: %s", enumClass.getName()));
			lines.add(String.format("Runtime Deobf: %s", FMLForgePlugin.RUNTIME_DEOBF));
			lines.add(
					String.format("Flags: %s", String.format("%16s", Integer.toBinaryString(flags)).replace(' ', '0')));
			lines.add("Fields:");
			for (Field field : fields) {
				String mods = String.format("%16s", Integer.toBinaryString(field.getModifiers())).replace(' ', '0');
				lines.add(String.format("       %s %s: %s", mods, field.getName(), field.getType().getName()));
			}

			for (String line : lines)
				CJCore.logger.fatal(line);
			return null;
		}

		valuesField.setAccessible(true);

		try {
			return (E[]) valuesField.get(null);
		} catch (Exception e) {
			CJCore.logger.fatal("Error getting values of enum", e);
			throw new RuntimeException(e);
		}
	}

}
