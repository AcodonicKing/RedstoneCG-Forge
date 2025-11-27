package net.acodonic_king.redstonecg.block.gui.delayer;

import net.acodonic_king.redstonecg.RedstonecgMod;
import net.acodonic_king.redstonecg.block.defaults.DelayerBlockBase;
import net.acodonic_king.redstonecg.default_gui_classes.ButtonMessage;
import net.acodonic_king.redstonecg.init.RedstonecgModVersionRides;
import net.acodonic_king.redstonecg.procedures.OnBlockRightClickedProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class DelayerGUIButtonMessage extends ButtonMessage {
    public static final ResourceLocation ID = new ResourceLocation(RedstonecgMod.MODID, "delayer_gui_button_message");
    public DelayerGUIButtonMessage(FriendlyByteBuf buffer) {
        super(buffer);
    }
    public DelayerGUIButtonMessage(int buttonID, BlockPos pos) {
        super(buttonID, pos);
    }
    public static void sendAndHandle(Player entity, int buttonID, BlockPos pos){
        DelayerGUIButtonMessage msg = new DelayerGUIButtonMessage(buttonID, pos);
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
            if (ThisBlock.getBlock() instanceof DelayerBlockBase b) {
                int value = ThisBlock.getValue(DelayerBlockBase.DELAY);
                value += buttonID + buttonID - 1;
                if (value < 1)
                    value = 8;
                if (value > 8)
                    value = 1;
                ThisBlock = ThisBlock.setValue(DelayerBlockBase.DELAY, value);
                world.setBlock(pos, ThisBlock, 2);
                world.sendBlockUpdated(pos, ThisBlock, ThisBlock, 3);
            }
        }
        if (buttonID >= 8) {
            BlockState ThisBlock = world.getBlockState(pos);
            if (ThisBlock.getBlock() instanceof DelayerBlockBase b) {
                int value = buttonID - 8;
                if (value < 1)
                    value = 8;
                if (value > 8)
                    value = 1;
                ThisBlock = ThisBlock.setValue(DelayerBlockBase.DELAY, value);
                world.setBlock(pos, ThisBlock, 2);
                world.sendBlockUpdated(pos, ThisBlock, ThisBlock, 3);
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
}
