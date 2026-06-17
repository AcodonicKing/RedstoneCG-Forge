
package net.acodonic_king.redstonecg.block.gui.analog_source;

import net.acodonic_king.redstonecg.RedstonecgMod;
import net.acodonic_king.redstonecg.block.entity.AnalogSourceBlockEntity;
import net.acodonic_king.redstonecg.block.normal.analog.AnalogSourceBlock;
import net.acodonic_king.redstonecg.default_gui_classes.ButtonMessage;
import net.acodonic_king.redstonecg.init.RedstonecgModVersionRides;
import net.acodonic_king.redstonecg.procedures.OnBlockRightClickedProcedure;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;

import java.util.HashMap;

public class AnalogSourceGUIButtonMessage extends ButtonMessage {
	public static final ResourceLocation ID = new ResourceLocation(RedstonecgMod.MODID, "analog_source_gui_button_message");
	public AnalogSourceGUIButtonMessage(FriendlyByteBuf buffer) {
		super(buffer);
	}
	public AnalogSourceGUIButtonMessage(int buttonID, BlockPos pos) {
		super(buttonID, pos);
	}
	/*public static void handler(AnalogSourceGUIButtonMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> handleButtonAction(Objects.requireNonNull(context.getSender()), message.buttonID, message.pos));
		context.setPacketHandled(true);
	}*/
	public static void sendAndHandle(Player entity, AnalogSourceGUIButtonMessage msg){
		send(msg);
		msg.handleButtonAction(entity);
	}
	public static void sendAndHandle(Player entity, int buttonID, BlockPos pos){
		AnalogSourceGUIButtonMessage msg = new AnalogSourceGUIButtonMessage(buttonID, pos);
		//RedstonecgMod.PACKET_HANDLER.sendToServer(msg);
		send(msg);
		msg.handleButtonAction(entity);
	}

	@Override
	public void handleButtonAction(Player entity) {
		Level world = RedstonecgModVersionRides.getPlayerLevel(entity);
		if (!world.hasChunkAt(pos))
			return;
		HashMap guistate = AnalogSourceGUIMenu.guistate;
		BlockEntity blockEntity = world.getBlockEntity(pos);
		int[] range = new int[]{0,15};
		if(blockEntity instanceof AnalogSourceBlockEntity be)
			range = be.POWER_RANGE;
		if (buttonID < 2){
			BlockState ThisBlock = world.getBlockState(pos);
			if(blockEntity instanceof AnalogSourceBlockEntity be){
				be.adjustPower(buttonID + buttonID - 1);
				be.setChanged();
				world.updateNeighborsAt(pos, ThisBlock.getBlock());
			}
		}
		if (buttonID >= 8) {
			BlockState ThisBlock = world.getBlockState(pos);
			if (ThisBlock.getBlock() instanceof AnalogSourceBlock b) {
				b.setPower(world, ThisBlock, pos, entity, buttonID - 8);
			}
		}
		if (buttonID > 2 && buttonID < 7) {
			//OnBlockRightClickedProcedure.execute(world, pos);
			BlockState ThisBlock = world.getBlockState(pos);
			int i = buttonID - 3;
			int connection = ThisBlock.getValue(AnalogSourceBlock.CONNECTION) + 1;
			connection ^= 1 << i;
			connection--;
			if(connection < 0)
				connection = 14;
			ThisBlock = ThisBlock.setValue(AnalogSourceBlock.CONNECTION, connection);
			world.setBlock(pos, ThisBlock, 3);
		}
		if (buttonID == 2) {
			boolean be_changed = false;
			EditBox range_box_start = (EditBox)guistate.get("box:range_box_start");
			EditBox range_box_end = (EditBox)guistate.get("box:range_box_end");
			String range_start = this.tag.getString("range_start");
			String range_end = this.tag.getString("range_end");
			try {
				int value = Integer.parseInt(range_start);
				value = Mth.clamp(value, 0, 15);
				if(range[0] != value) {
					range[0] = value;
					be_changed = true;
				}
			} catch (NumberFormatException ignored) {}
			try {
				int value = Integer.parseInt(range_end);
				value = Mth.clamp(value, 0, 15);
				if(range[1] != value) {
					range[1] = value;
					be_changed = true;
				}
			} catch (NumberFormatException ignored) {}
			if(be_changed)
				if (blockEntity instanceof AnalogSourceBlockEntity be) {
					be.setPowerRange(range);
					be.setChanged();
					//MessengerBlockEntityPigeon.send(new MessengerBlockEntityPigeon(pos, blockEntity.getUpdateTag()));
					//world.scheduleTick(pos, world.getBlockState(pos).getBlock(), 1);
				}
			range_box_start.setValue(String.valueOf(range[0]));
			range_box_end.setValue(String.valueOf(range[1]));
		}
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	/*@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		RedstonecgMod.addNetworkMessage(AnalogSourceGUIButtonMessage.class, AnalogSourceGUIButtonMessage::buffer, AnalogSourceGUIButtonMessage::new, AnalogSourceGUIButtonMessage::handler);
	}*/
}
