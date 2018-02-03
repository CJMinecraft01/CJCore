package cjminecraft.core.network.fluid;

import cjminecraft.core.CJCore;
import cjminecraft.core.energy.EnergyUtils;
import cjminecraft.core.fluid.FluidUtils;
import cjminecraft.core.network.PacketHandler;
import cjminecraft.core.network.energy.PacketReturnEnergyData;
import cjminecraft.core.util.NetworkUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketGetFluidData implements IMessage {
	
	private boolean messageValid;

	private int tankIndex;
	private BlockPos pos;
	private EnumFacing side;
	
	private boolean updateFields = false;
	private String className;
	private String modid;
	private String capacityFieldName;
	private String fluidStackFieldName;
	
	public PacketGetFluidData() {
		this.messageValid = false;
	}
	
	public PacketGetFluidData(int tankIndex, BlockPos pos, EnumFacing side, boolean updateFields, String... args) {
		this.tankIndex = tankIndex;
		this.pos = pos;
		this.side = side;
		if(updateFields) {
			this.updateFields = true;
			this.className = args[0];
			this.capacityFieldName = args[1];
			this.fluidStackFieldName = args[2];
		} else {
			this.modid = args[0];
			this.className = args[1];
		}
		this.messageValid = true;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		try {
			this.tankIndex = buf.readInt();
			this.pos = NetworkUtils.readBlockPos(buf);
			this.side = NetworkUtils.readEnumFacing(buf);
			this.className = ByteBufUtils.readUTF8String(buf);
			this.updateFields = buf.readBoolean();
			if(this.updateFields) {
				this.capacityFieldName = ByteBufUtils.readUTF8String(buf);
				this.fluidStackFieldName = ByteBufUtils.readUTF8String(buf);
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
		if(!this.messageValid)
			return;
		buf.writeInt(this.tankIndex);
		NetworkUtils.writeBlockPos(buf, this.pos);
		NetworkUtils.writeEnumFacing(buf, this.side);
		ByteBufUtils.writeUTF8String(buf, this.className);
		buf.writeBoolean(this.updateFields);
		if(this.updateFields) {
			ByteBufUtils.writeUTF8String(buf, this.capacityFieldName);
			ByteBufUtils.writeUTF8String(buf, this.fluidStackFieldName);
		} else
			ByteBufUtils.writeUTF8String(buf, this.modid);
	}
	
	public static class Handler implements IMessageHandler<PacketGetFluidData, IMessage> {
		
		@Override
		public IMessage onMessage(PacketGetFluidData message, MessageContext ctx) {
			if(!message.messageValid && ctx.side != Side.SERVER)
				return null;
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> processMessage(message, ctx));
			return null;
		}
		
		void processMessage(PacketGetFluidData message, MessageContext ctx) {
			if(!ctx.getServerHandler().player.getServerWorld().isBlockLoaded(message.pos))
				return;
			TileEntity te = ctx.getServerHandler().player.getServerWorld().getTileEntity(message.pos);
			if (te == null)
				return;
			if (!FluidUtils.hasSupport(te, message.side))
				return;
			int capacity = FluidUtils.getCapacity(te, message.side, message.tankIndex);
			FluidStack fluidStack = FluidUtils.getFluidStack(te, message.side, message.tankIndex);
			if(message.updateFields)
				PacketHandler.INSTANCE.sendTo(new PacketReturnFluidData(capacity, fluidStack, true, message.className, message.capacityFieldName, message.fluidStackFieldName), ctx.getServerHandler().player);
			else
				PacketHandler.INSTANCE.sendTo(new PacketReturnFluidData(capacity, fluidStack, false, message.modid, message.className), ctx.getServerHandler().player);
		}
		
	}
	
}
