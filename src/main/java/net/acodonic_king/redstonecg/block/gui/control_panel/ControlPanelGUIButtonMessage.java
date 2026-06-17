
package net.acodonic_king.redstonecg.block.gui.control_panel;

import net.acodonic_king.redstonecg.RedstonecgMod;
import net.acodonic_king.redstonecg.block.normal.interaction.ControlPanelBlock;
import net.acodonic_king.redstonecg.block.entity.ControlPanelBlockEntity;
import net.acodonic_king.redstonecg.default_gui_classes.ButtonMessage;
import net.acodonic_king.redstonecg.init.RedstonecgModVersionRides;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;

public class ControlPanelGUIButtonMessage extends ButtonMessage {
	public static final ResourceLocation ID = new ResourceLocation(RedstonecgMod.MODID, "control_panel_gui_button_message");
	public ControlPanelGUIButtonMessage(FriendlyByteBuf buffer) {
		super(buffer);
	}
	public ControlPanelGUIButtonMessage(int buttonID, BlockPos pos) {
		super(buttonID, pos);
	}
	/*public static void handler(RedCuCrafterGUIButtonMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> handleButtonAction(Objects.requireNonNull(context.getSender()), message.buttonID, message.pos));
		context.setPacketHandled(true);
	}*/
	public static void sendAndHandle(Player entity, int buttonID, BlockPos pos){
		ControlPanelGUIButtonMessage msg = new ControlPanelGUIButtonMessage(buttonID, pos);
		send(msg);
		msg.handleButtonAction(entity);
	}
	public static void sendAndHandle(Player entity, ControlPanelGUIButtonMessage msg){
		send(msg);
		msg.handleButtonAction(entity);
	}
	@Override
	public void handleButtonAction(Player entity) {
		Level world = RedstonecgModVersionRides.getPlayerLevel(entity);
		HashMap guistate = ControlPanelGUIMenu.guistate;
		if (!world.hasChunkAt(pos))
			return;
		if (buttonID == 0){
			if(world.getBlockEntity(pos) instanceof ControlPanelBlockEntity be) {
				CompoundTag compoundTag = this.tag;
				be.CONNECTION = compoundTag.getByte("connection");
				be.setChanged();
				world.updateNeighborsAt(pos, be.getBlockState().getBlock());
			}
		} else if (buttonID < 4) {
			int i = buttonID - 1;
			BlockState blockState = world.getBlockState(pos);
			((ControlPanelBlock)blockState.getBlock()).changeModel(world, blockState, pos, i);
		}
		if (buttonID >= 10){
			if(world.getBlockEntity(pos) instanceof ControlPanelBlockEntity be) {
				CompoundTag compoundTag = this.tag;
				int i = buttonID - 10;
				be.SLOT_ANGLES[i] = compoundTag.getFloat("slot_angle");
				be.setChanged();
				world.updateNeighborsAt(pos, be.getBlockState().getBlock());
			}
		}
	}
	@Override
	public ResourceLocation id() {
		return ID;
	}
}
