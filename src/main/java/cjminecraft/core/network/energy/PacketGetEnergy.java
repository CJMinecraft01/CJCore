package cjminecraft.core.network.energy;

import cjminecraft.core.CJCore;
import cjminecraft.core.energy.EnergyUnits.EnergyUnit;
import cjminecraft.core.energy.EnergyUtils;
import cjminecraft.core.network.PacketHandler;
import cjminecraft.core.util.NetworkUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketGetEnergy implements IMessage {

	private boolean messageValid;
	
	private EnergyUnit unit;
	private BlockPos pos;
	private EnumFacing side;
	
	private boolean updateFields = false;
	private String className;
	private String modid;
	private String energyFieldName;
	
	public PacketGetEnergy() {
		this.messageValid = false;
	}
	
	public PacketGetEnergy(EnergyUnit unit, BlockPos pos, EnumFacing side, boolean updateFields, String... args) {
		this.unit = unit;
		this.pos = pos;
		this.side = side;
		if(updateFields) {
			this.updateFields = true;
			this.className = args[0];
			this.energyFieldName = args[1];
		} else {
			this.modid = args[0];
			this.className = args[1];
		}
		this.messageValid = true;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		try {
			this.unit = NetworkUtils.readEnergyUnit(buf);
			this.pos = NetworkUtils.readBlockPos(buf);
			this.side = NetworkUtils.readEnumFacing(buf);
			this.className = ByteBufUtils.readUTF8String(buf);
			this.updateFields = buf.readBoolean();
			if(this.updateFields) 
				this.energyFieldName = ByteBufUtils.readUTF8String(buf);
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
		NetworkUtils.writeEnergyUnit(buf, this.unit);
		NetworkUtils.writeBlockPos(buf, this.pos);
		NetworkUtils.writeEnumFacing(buf, this.side);
		ByteBufUtils.writeUTF8String(buf, this.className);
		buf.writeBoolean(this.updateFields);
		if(this.updateFields)
			ByteBufUtils.writeUTF8String(buf, this.energyFieldName);
		else
			ByteBufUtils.writeUTF8String(buf, this.modid);
		
	}
	
	public static class Handler implements IMessageHandler<PacketGetEnergy, IMessage> {

		@Override
		public IMessage onMessage(PacketGetEnergy message, MessageContext ctx) {
			if(!message.messageValid && ctx.side != Side.SERVER)
				return null;
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> processMessage(message, ctx));
			return null;
		}
		
		void processMessage(PacketGetEnergy message, MessageContext ctx) {
			TileEntity te = ctx.getServerHandler().player.getServerWorld().getTileEntity(message.pos);
			if (te == null)
				return;
			if (!EnergyUtils.hasSupport(te, message.side))
				return;
			long energy = EnergyUtils.getEnergyStored(te, message.side, message.unit);
			if(message.updateFields)
				PacketHandler.INSTANCE.sendTo(new PacketReturnEnergy(energy, true, message.className, message.energyFieldName), ctx.getServerHandler().player);
			else
				PacketHandler.INSTANCE.sendTo(new PacketReturnEnergy(energy, false, message.modid, message.className), ctx.getServerHandler().player);
		}
		
	}

}
