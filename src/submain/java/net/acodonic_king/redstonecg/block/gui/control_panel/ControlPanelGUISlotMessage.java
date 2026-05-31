
package net.acodonic_king.redstonecg.block.gui.control_panel;

import net.acodonic_king.redstonecg.block.entity.ControlPanelBlockEntity;
import net.acodonic_king.redstonecg.init.RedstonecgModNetworking;
import net.acodonic_king.redstonecg.init.RedstonecgModVersionRides;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.function.Supplier;

public class ControlPanelGUISlotMessage {
	private final int slotID, changeType, meta;
	private BlockPos pos;
	private ItemStack stack;

	public ControlPanelGUISlotMessage(int slotID, BlockPos pos, int changeType, int meta, ItemStack stack) {
		this.slotID = slotID;
		this.pos = pos;
		this.changeType = changeType;
		this.meta = meta;
		this.stack = stack;
	}

	public static void send(ControlPanelGUISlotMessage msg){
		RedstonecgModNetworking.PACKET_HANDLER.sendToServer(msg);
	}

	public ControlPanelGUISlotMessage(FriendlyByteBuf buffer) {
		this.slotID = buffer.readInt();
		int x = buffer.readInt();
		int y = buffer.readInt();
		int z = buffer.readInt();
		this.pos = new BlockPos(x, y, z);
		this.changeType = buffer.readInt();
		this.meta = buffer.readInt();
		this.stack = buffer.readItem();
	}

	public static void buffer(ControlPanelGUISlotMessage message, FriendlyByteBuf buffer) {
		buffer.writeInt(message.slotID);
		buffer.writeInt(message.pos.getX());
		buffer.writeInt(message.pos.getY());
		buffer.writeInt(message.pos.getZ());
		buffer.writeInt(message.changeType);
		buffer.writeInt(message.meta);
		buffer.writeItem(message.stack);
	}

	public static void handleData(ControlPanelGUISlotMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			Player entity = context.getSender();
			message.handleSlotAction(entity);
		});
		context.setPacketHandled(true);
	}

	public void handleSlotAction(Player entity) {
		int slot = this.slotID;
		Level world = RedstonecgModVersionRides.getPlayerLevel(entity);
		HashMap guistate = ControlPanelGUIMenu.guistate;
		// security measure to prevent arbitrary chunk generation
		if (!world.hasChunkAt(pos))
			return;
		if (world.isClientSide())
			return;
		if(world.getBlockEntity(pos) instanceof ControlPanelBlockEntity be){
			//be.syncInventory(pos);
			//RedstonecgMod.LOGGER.debug("sm "+be.stacks.get(slot));
			be.initPanelLogicPredict(this.stack, slot);
			world.updateNeighborsAt(pos, world.getBlockState(pos).getBlock());
		}
	}
}
