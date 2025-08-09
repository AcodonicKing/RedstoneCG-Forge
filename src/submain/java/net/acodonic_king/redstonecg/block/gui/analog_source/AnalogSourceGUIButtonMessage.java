
package net.acodonic_king.redstonecg.block.gui.analog_source;

import net.acodonic_king.redstonecg.RedstonecgMod;
import net.acodonic_king.redstonecg.block.normal.analog.AnalogSourceBlock;
import net.acodonic_king.redstonecg.default_gui_classes.ButtonMessage;
import net.acodonic_king.redstonecg.init.RedstonecgModVersionRides;
import net.acodonic_king.redstonecg.procedures.OnBlockRightClickedProcedure;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;

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
		//HashMap guistate = AnalogSourceGUIMenu.guistate;
		if (buttonID < 2){
			BlockState ThisBlock = world.getBlockState(pos);
			if (ThisBlock.getBlock() instanceof AnalogSourceBlock b) {
				int value = b.getPower(world, pos);
				value += buttonID + buttonID - 1;
				if (value < 0)
					value = 15;
				if (value > 15)
					value = 0;
				b.setPower(world, ThisBlock, pos, value);
			}
		}
		if (buttonID >= 8) {
			BlockState ThisBlock = world.getBlockState(pos);
			if (ThisBlock.getBlock() instanceof AnalogSourceBlock b) {
				b.setPower(world, ThisBlock, pos, buttonID - 8);
			}
		}
		if (buttonID == 2) {
			OnBlockRightClickedProcedure.execute(world, pos);
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
