package cjminecraft.core.network.energy;

import java.lang.reflect.Field;

import cjminecraft.core.CJCore;
import cjminecraft.core.energy.EnergyData;
import cjminecraft.core.energy.EnergyUnits.EnergyUnit;
import cjminecraft.core.util.NetworkUtils;
import cjminecraft.core.energy.EnergyUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketReturnCapacity implements IMessage {

	private boolean messageValid;

	private long capacity;

	private boolean updateFields = false;
	private String className;
	private String capacityFieldName;
	private String modid;

	public PacketReturnCapacity() {
		this.messageValid = false;
	}

	public PacketReturnCapacity(long capacity, boolean updateFields, String... args) {
		this.capacity = capacity;
		if (updateFields) {
			this.updateFields = true;
			this.className = args[0];
			this.capacityFieldName = args[1];
		} else {
			this.modid = args[0];
			this.className = args[1];
		}
		this.messageValid = true;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		try {
			this.capacity = buf.readLong();
			this.className = ByteBufUtils.readUTF8String(buf);
			this.updateFields = buf.readBoolean();
			if (this.updateFields)
				this.capacityFieldName = ByteBufUtils.readUTF8String(buf);
			else
				this.modid = ByteBufUtils.readUTF8String(buf);

		} catch (IndexOutOfBoundsException ioe) {
			CJCore.logger.catching(ioe);
			return;
		}
		this.messageValid = true;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		if (!this.messageValid)
			return;
		buf.writeLong(this.capacity);
		ByteBufUtils.writeUTF8String(buf, this.className);
		buf.writeBoolean(this.updateFields);
		if (this.updateFields)
			ByteBufUtils.writeUTF8String(buf, this.capacityFieldName);
		else
			ByteBufUtils.writeUTF8String(buf, this.modid);
	}

	public static class Handler implements IMessageHandler<PacketReturnCapacity, IMessage> {

		@Override
		public IMessage onMessage(PacketReturnCapacity message, MessageContext ctx) {
			if (!message.messageValid && ctx.side != Side.CLIENT)
				return null;
			Minecraft.getMinecraft().addScheduledTask(() -> processMessage(message));
			return null;
		}

		void processMessage(PacketReturnCapacity message) {
			if (message.updateFields) {
				try {
					Class clazz = Class.forName(message.className);
					Field capacityField = clazz.getDeclaredField(message.capacityFieldName);
					capacityField.setLong(clazz, message.capacity);
				} catch (Exception e) {
					CJCore.logger.catching(e);
					return;
				}
			} else {
				EnergyData data = new EnergyData().setCapacity(message.capacity);
				EnergyUtils.addCachedEnergyData(message.modid, message.className, data);
			}
		}

	}

}
