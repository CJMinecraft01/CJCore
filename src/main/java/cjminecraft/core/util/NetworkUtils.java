package cjminecraft.core.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import cjminecraft.core.energy.EnergyUnits;
import cjminecraft.core.energy.EnergyUnits.EnergyUnit;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;

/**
 * Utility class for network messages
 * 
 * @author CJMinecraft
 *
 */
public class NetworkUtils {

	/**
	 * Writes the {@link BlockPos} to the {@link ByteBuf}
	 * 
	 * @param buf
	 *            The {@link ByteBuf} to write to
	 * @param pos
	 *            The {@link BlockPos} to write
	 */
	public static void writeBlockPos(ByteBuf buf, BlockPos pos) {
		buf.writeInt(pos.getX());
		buf.writeInt(pos.getY());
		buf.writeInt(pos.getZ());
	}

	/**
	 * Read a {@link BlockPos} from a {@link ByteBuf}
	 * 
	 * @param buf
	 *            The {@link ByteBuf} to read from
	 * @return The {@link BlockPos} read from the {@link ByteBuf}
	 */
	public static BlockPos readBlockPos(ByteBuf buf) {
		return new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
	}

	/**
	 * Writes the {@link EnumFacing} to the {@link ByteBuf}
	 * 
	 * @param buf
	 *            The {@link ByteBuf} to write to
	 * @param facing
	 *            The {@link EnumFacing} to write
	 */
	public static void writeEnumFacing(ByteBuf buf, @Nullable EnumFacing facing) {
		ByteBufUtils.writeUTF8String(buf, facing == null ? "null" : facing.getName2());
	}

	/**
	 * Read a {@link EnumFacing} from a {@link ByteBuf}
	 * 
	 * @param buf
	 *            The {@link ByteBuf} to read from
	 * @return The {@link EnumFacing} read from the {@link ByteBuf}
	 */
	@Nullable
	public static EnumFacing readEnumFacing(ByteBuf buf) {
		String face = ByteBufUtils.readUTF8String(buf);
		return face == "null" ? null : EnumFacing.byName(face);
	}

	/**
	 * Writes the {@link EnergyUnit} to the {@link ByteBuf}
	 * 
	 * @param buf
	 *            The {@link ByteBuf} to write to
	 * @param unit
	 *            The {@link EnergyUnit} to write
	 */
	public static void writeEnergyUnit(ByteBuf buf, @Nonnull EnergyUnit unit) {
		ByteBufUtils.writeUTF8String(buf, unit.getUnlocalizedName());
	}

	/**
	 * Reads a {@link EnergyUnit} from a {@link ByteBuf}
	 * 
	 * @param buf
	 *            The {@link ByteBuf} to read from
	 * @return The {@link EnergyUnit} from the {@link ByteBuf}
	 */
	@Nonnull
	public static EnergyUnit readEnergyUnit(ByteBuf buf) {
		return EnergyUnits.byUnlocalizedName(ByteBufUtils.readUTF8String(buf));
	}

}
