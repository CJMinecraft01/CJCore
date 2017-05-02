package cjminecraft.core.network.energy;

import java.lang.reflect.Field;

import cjminecraft.core.CJCore;
import cjminecraft.core.energy.EnergyData;
import cjminecraft.core.energy.EnergyUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketReturnEnergyData implements IMessage {

	private boolean messageValid;

	private long energy;
	private long capacity;

	private boolean updateFields = false;
	private String className;
	private String modid;
	private String energyFieldName;
	private String capacityFieldName;

	public PacketReturnEnergyData() {
		this.messageValid = false;
	}

	public PacketReturnEnergyData(long energy, long capacity, boolean updateFields, String... args) {
		this.energy = energy;
		this.capacity = capacity;
		if (updateFields) {
			this.updateFields = true;
			this.className = args[0];
			this.energyFieldName = args[1];
			this.capacityFieldName = args[2];
		} else {
			this.modid = args[0];
			this.className = args[1];
		}
		this.messageValid = true;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		try {
			this.energy = buf.readLong();
			this.capacity = buf.readLong();
			this.className = ByteBufUtils.readUTF8String(buf);
			this.updateFields = buf.readBoolean();
			if (this.updateFields) {
				this.energyFieldName = ByteBufUtils.readUTF8String(buf);
				this.capacityFieldName = ByteBufUtils.readUTF8String(buf);
			} else
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
		buf.writeLong(this.energy);
		buf.writeLong(this.capacity);
		ByteBufUtils.writeUTF8String(buf, this.className);
		buf.writeBoolean(this.updateFields);
		if (this.updateFields) {
			ByteBufUtils.writeUTF8String(buf, this.energyFieldName);
			ByteBufUtils.writeUTF8String(buf, this.capacityFieldName);
		} else
			ByteBufUtils.writeUTF8String(buf, this.modid);
	}

	public static class Handler implements IMessageHandler<PacketReturnEnergyData, IMessage> {

		@Override
		public IMessage onMessage(PacketReturnEnergyData message, MessageContext ctx) {
			if (!message.messageValid && ctx.side != Side.CLIENT)
				return null;
			Minecraft.getMinecraft().addScheduledTask(() -> processMessage(message));
			return null;
		}

		void processMessage(PacketReturnEnergyData message) {
			if (message.updateFields) {
				try {
					Class clazz = Class.forName(message.className);
					Field energyField = clazz.getDeclaredField(message.energyFieldName);
					Field capacityField = clazz.getDeclaredField(message.capacityFieldName);
					energyField.setLong(clazz, message.energy);
					capacityField.setLong(clazz, message.capacity);
				} catch (Exception e) {
					CJCore.logger.catching(e);
					return;
				}
			} else {
				EnergyData data = new EnergyData().setEnergy(message.energy).setCapacity(message.capacity);
				EnergyUtils.addCachedEnergyData(message.modid, message.className, data);
			}
		}

	}

}
