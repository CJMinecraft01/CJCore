package cjminecraft.core.network.inventory;

import com.google.common.collect.ImmutableList;

import cjminecraft.core.CJCore;
import cjminecraft.core.inventory.InventoryUtils;
import cjminecraft.core.network.PacketHandler;
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

public class PacketGetInventory implements IMessage {

	private boolean messageValid;

	private BlockPos pos;
	private EnumFacing side;
	private boolean stacked;

	private boolean useSlots = false;
	private int fromSlot;
	private int toSlot;

	private boolean updateField = false;
	private String className;
	private String inventoryFieldName;
	private String modid;

	public PacketGetInventory() {
		this.messageValid = false;
	}

	public PacketGetInventory(BlockPos pos, EnumFacing side, boolean stacked, boolean updateField, String... args) {
		this.pos = pos;
		this.side = side;
		this.stacked = stacked;

		if (updateField) {
			this.updateField = true;
			this.className = args[0];
			this.inventoryFieldName = args[1];
		} else {
			this.modid = args[0];
			this.className = args[1];
		}
		this.messageValid = true;
	}

	public PacketGetInventory(BlockPos pos, EnumFacing side, boolean stacked, int fromSlot, int toSlot,
			boolean updateField, String... args) {
		this.pos = pos;
		this.side = side;
		this.stacked = stacked;

		this.useSlots = true;
		this.fromSlot = fromSlot;
		this.toSlot = toSlot;

		if (updateField) {
			this.updateField = true;
			this.className = args[0];
			this.inventoryFieldName = args[1];
		} else {
			this.modid = args[0];
			this.className = args[1];
		}
		
		this.messageValid = true;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		try {
			this.pos = NetworkUtils.readBlockPos(buf);
			this.side = NetworkUtils.readEnumFacing(buf);
			this.stacked = buf.readBoolean();

			this.useSlots = buf.readBoolean();
			if (this.useSlots) {
				this.fromSlot = buf.readInt();
				this.toSlot = buf.readInt();
			}

			this.updateField = buf.readBoolean();
			this.className = ByteBufUtils.readUTF8String(buf);
			if (this.updateField)
				this.inventoryFieldName = ByteBufUtils.readUTF8String(buf);
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
		NetworkUtils.writeBlockPos(buf, this.pos);
		NetworkUtils.writeEnumFacing(buf, this.side);
		buf.writeBoolean(this.stacked);

		buf.writeBoolean(this.useSlots);
		if (this.useSlots) {
			buf.writeInt(this.fromSlot);
			buf.writeInt(this.toSlot);
		}
		buf.writeBoolean(this.updateField);
		ByteBufUtils.writeUTF8String(buf, this.className);
		if (this.updateField)
			ByteBufUtils.writeUTF8String(buf, this.inventoryFieldName);
		else
			ByteBufUtils.writeUTF8String(buf, this.modid);
	}

	public static class Handler implements IMessageHandler<PacketGetInventory, IMessage> {

		@Override
		public IMessage onMessage(PacketGetInventory message, MessageContext ctx) {
			if (!message.messageValid && ctx.side != Side.SERVER)
				return null;
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler)
					.addScheduledTask(() -> processMessage(message, ctx));
			return null;
		}

		void processMessage(PacketGetInventory message, MessageContext ctx) {
			TileEntity te = ctx.getServerHandler().player.getServerWorld().getTileEntity(message.pos);
			if (te == null)
				return;
			if (!InventoryUtils.hasSupport(te, message.side))
				return;
			ImmutableList<ItemStack> inventory;
			if (message.stacked)
				if (message.useSlots)
					inventory = InventoryUtils.getInventoryStacked(te, message.fromSlot, message.toSlot, message.side);
				else
					inventory = InventoryUtils.getInventoryStacked(te, message.side);
			else if (message.useSlots)
				inventory = InventoryUtils.getInventory(te, message.fromSlot, message.toSlot, message.side);
			else
				inventory = InventoryUtils.getInventory(te, message.side);
			if (message.updateField)
				PacketHandler.INSTANCE.sendTo(
						new PacketReturnInventory(inventory, true, message.className, message.inventoryFieldName),
						ctx.getServerHandler().player);
			else
				PacketHandler.INSTANCE.sendTo(
						new PacketReturnInventory(inventory, false, message.modid, message.className),
						ctx.getServerHandler().player);
		}
	}

}
