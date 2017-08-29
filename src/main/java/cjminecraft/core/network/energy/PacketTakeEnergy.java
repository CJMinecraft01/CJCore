package cjminecraft.core.network.energy;

import cjminecraft.core.CJCore;
import cjminecraft.core.energy.EnergyUnits.EnergyUnit;
import cjminecraft.core.inventory.InventoryUtils;
import cjminecraft.core.energy.EnergyUtils;
import cjminecraft.core.util.NetworkUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketTakeEnergy implements IMessage {

	private boolean messageValid;

	private boolean isItem = false;
	private ItemStack stack;
	private BlockPos pos;
	private long energy;
	private EnergyUnit unit;
	private boolean simulate;
	private EnumFacing from;

	public PacketTakeEnergy() {
		this.messageValid = false;
	}

	public PacketTakeEnergy(BlockPos pos, long energy, EnergyUnit unit, boolean simulate, EnumFacing from) {
		this.pos = pos;
		this.energy = energy;
		this.unit = unit;
		this.simulate = simulate;
		this.from = from;
		this.messageValid = true;
	}

	public PacketTakeEnergy(ItemStack stack, long energy, EnergyUnit unit, boolean simulate, EnumFacing from) {
		this.stack = stack;
		this.isItem = true;
		this.energy = energy;
		this.unit = unit;
		this.simulate = simulate;
		this.from = from;
		this.messageValid = true;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		try {
			this.isItem = buf.readBoolean();
			if (!this.isItem)
				this.pos = NetworkUtils.readBlockPos(buf);
			else
				this.stack = ByteBufUtils.readItemStack(buf);
			this.energy = buf.readLong();
			this.unit = NetworkUtils.readEnergyUnit(buf);
			this.simulate = buf.readBoolean();
			this.from = NetworkUtils.readEnumFacing(buf);
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
		buf.writeBoolean(this.isItem);
		if (!this.isItem)
			NetworkUtils.writeBlockPos(buf, this.pos);
		else
			ByteBufUtils.writeItemStack(buf, this.stack);
		buf.writeLong(this.energy);
		NetworkUtils.writeEnergyUnit(buf, this.unit);
		buf.writeBoolean(this.simulate);
		NetworkUtils.writeEnumFacing(buf, this.from);
	}

	public static class Handler implements IMessageHandler<PacketTakeEnergy, IMessage> {

		@Override
		public IMessage onMessage(PacketTakeEnergy message, MessageContext ctx) {
			if (!message.messageValid && ctx.side != Side.SERVER)
				return null;
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler)
					.addScheduledTask(() -> processMessage(message, ctx));
			return null;
		}

		void processMessage(PacketTakeEnergy message, MessageContext ctx) {
			if (!message.isItem)
				EnergyUtils.takeEnergy(ctx.getServerHandler().playerEntity.getServerWorld().getTileEntity(message.pos),
						message.energy, message.unit, message.simulate, message.from);
			ItemStack stack = InventoryUtils.findInInventory(message.stack, ctx.getServerHandler().playerEntity, false, false, 0, 40);
			EnergyUtils.takeEnergy(stack, message.energy, message.unit, message.simulate, message.from);
		}
	}

}
