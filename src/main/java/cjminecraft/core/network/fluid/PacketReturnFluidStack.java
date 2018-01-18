package cjminecraft.core.network.fluid;

import java.lang.reflect.Field;

import cjminecraft.core.CJCore;
import cjminecraft.core.energy.EnergyData;
import cjminecraft.core.energy.EnergyUtils;
import cjminecraft.core.fluid.FluidUtils;
import cjminecraft.core.network.energy.PacketReturnEnergyData;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketReturnFluidStack implements IMessage {

	private boolean messageValid;
	
	private FluidStack fluidStack;
	
	private boolean updateFields = false;
	private String className;
	private String modid;
	private String fluidStackFieldName;
	
	public PacketReturnFluidStack() {
		this.messageValid = false;
	}
	
	public PacketReturnFluidStack(FluidStack fluidStack, boolean updateFields, String... args) {
		this.fluidStack = fluidStack;
		this.updateFields = updateFields;
		if (updateFields) {
			this.updateFields = true;
			this.className = args[0];
			this.fluidStackFieldName = args[1];
		} else {
			this.modid = args[0];
			this.className = args[1];
		}
		this.messageValid = true;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		try {
			this.fluidStack = FluidStack.loadFluidStackFromNBT(ByteBufUtils.readTag(buf));
			this.className = ByteBufUtils.readUTF8String(buf);
			this.updateFields = buf.readBoolean();
			if(this.updateFields)
				this.fluidStackFieldName = ByteBufUtils.readUTF8String(buf);
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
		if(!this.messageValid)
			return;
		ByteBufUtils.writeTag(buf, this.fluidStack.writeToNBT(new NBTTagCompound()));
		ByteBufUtils.writeUTF8String(buf, this.className);
		buf.writeBoolean(this.updateFields);
		if(this.updateFields)
			ByteBufUtils.writeUTF8String(buf, this.fluidStackFieldName);
		else
			ByteBufUtils.writeUTF8String(buf, this.modid);
	}
	
	public static class Handler implements IMessageHandler<PacketReturnFluidStack, IMessage> {
		
		@Override
		public IMessage onMessage(PacketReturnFluidStack message, MessageContext ctx) {
			if (!message.messageValid && ctx.side != Side.CLIENT)
				return null;
			Minecraft.getMinecraft().addScheduledTask(() -> processMessage(message));
			return null;
		}

		void processMessage(PacketReturnFluidStack message) {
			if (message.updateFields) {
				try {
					Class clazz = Class.forName(message.className);
					Field fluidStackField = clazz.getDeclaredField(message.fluidStackFieldName);
					fluidStackField.set(clazz, message.fluidStack);
				} catch (Exception e) {
					CJCore.logger.catching(e);
					return;
				}
			} else {
				FluidUtils.addCachedFluidData(message.modid, message.className, new FluidTankInfo(message.fluidStack, 0));
			}
		}
	}
	
}
