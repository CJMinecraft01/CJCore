package cjminecraft.core.network.inventory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import cjminecraft.core.CJCore;
import cjminecraft.core.inventory.InventoryUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketReturnInventory implements IMessage {

	private boolean messageValid;

	private ImmutableList<ItemStack> inventory;

	private boolean updateField = false;
	private String className;
	private String inventoryFieldName;
	private String modid;

	public PacketReturnInventory() {
		this.messageValid = false;
	}

	public PacketReturnInventory(ImmutableList<ItemStack> inventory, boolean updateFields, String... args) {
		this.inventory = inventory;
		if (updateFields) {
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
			List<ItemStack> inv = new ArrayList<ItemStack>();
			int size = buf.readInt();
			for (int i = 0; i < size; i++) {
				ItemStack stack = new ItemStack(ByteBufUtils.readTag(buf));
				stack.setCount(buf.readInt());
				inv.add(stack);
			}
			this.inventory = ImmutableList.<ItemStack>copyOf(inv);
			this.className = ByteBufUtils.readUTF8String(buf);
			this.updateField = buf.readBoolean();
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
		buf.writeInt(this.inventory.size());
		for (ItemStack stack : this.inventory) {
			NBTTagCompound nbt = stack.serializeNBT();
			nbt.removeTag("count");
			ByteBufUtils.writeTag(buf, nbt);
			buf.writeInt(stack.getCount());
		}
		ByteBufUtils.writeUTF8String(buf, this.className);
		buf.writeBoolean(this.updateField);
		if (this.updateField)
			ByteBufUtils.writeUTF8String(buf, this.inventoryFieldName);
		else
			ByteBufUtils.writeUTF8String(buf, this.modid);
	}

	public static class Handler implements IMessageHandler<PacketReturnInventory, IMessage> {

		@Override
		public IMessage onMessage(PacketReturnInventory message, MessageContext ctx) {
			if (!message.messageValid && ctx.side != Side.CLIENT)
				return null;
			Minecraft.getMinecraft().addScheduledTask(() -> processMessage(message));
			return null;
		}

		void processMessage(PacketReturnInventory message) {
			if (message.updateField) {
				try {
					Class clazz = Class.forName(message.className);
					Field inventoryField = clazz.getDeclaredField(message.inventoryFieldName);
					inventoryField.set(clazz, message.inventory);
				} catch (Exception e) {
					CJCore.logger.catching(e);
					return;
				}
			} else {
				InventoryUtils.addCachedInventoryData(message.modid, message.className, message.inventory);
			}
		}

	}

}
