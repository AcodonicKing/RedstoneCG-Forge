package net.acodonic_king.redstonecg.block.gui.arrow_indicator;

import net.acodonic_king.redstonecg.RedstonecgMod;
import net.acodonic_king.redstonecg.block.entity.ArrowIndicatorBlockEntity;
import net.acodonic_king.redstonecg.block.normal.analog.AnalogSourceBlock;
import net.acodonic_king.redstonecg.block.normal.indicator.ArrowIndicatorBlock;
import net.acodonic_king.redstonecg.default_gui_classes.ButtonMessage;
import net.acodonic_king.redstonecg.init.RedstonecgModVersionRides;
import net.acodonic_king.redstonecg.network.MessengerBlockEntityPigeon;
import net.acodonic_king.redstonecg.procedures.OnBlockRightClickedProcedure;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;

public class ArrowIndicatorGUIButtonMessage extends ButtonMessage {
    public static final ResourceLocation ID = new ResourceLocation(RedstonecgMod.MODID, "arrow_indicator_gui_button_message");
    public ArrowIndicatorGUIButtonMessage(FriendlyByteBuf buffer) {
        super(buffer);
    }
    public ArrowIndicatorGUIButtonMessage(int buttonID, BlockPos pos) {
        super(buttonID, pos);
    }

    public static void sendAndHandle(Player entity, ArrowIndicatorGUIButtonMessage msg){
        send(msg);
        msg.handleButtonAction(entity);
    }
    public static void sendAndHandle(Player entity, int buttonID, BlockPos pos){
        ArrowIndicatorGUIButtonMessage msg = new ArrowIndicatorGUIButtonMessage(buttonID, pos);
        //RedstonecgMod.PACKET_HANDLER.sendToServer(msg);
        send(msg);
        msg.handleButtonAction(entity);
    }

    @Override
    public void handleButtonAction(Player entity) {
        Level world = RedstonecgModVersionRides.getPlayerLevel(entity);
        if (!world.hasChunkAt(pos))
            return;
        HashMap<String, Object> guistate = ArrowIndicatorGUIMenu.guistate;
        boolean be_changed = false;
        BlockEntity blockEntity = world.getBlockEntity(pos);
        int[] range = new int[2];
        if (blockEntity instanceof ArrowIndicatorBlockEntity be)
            range = be.getRange();
        if (buttonID == 2) {
            OnBlockRightClickedProcedure.execute(world, pos);
        }
        if (buttonID >= 3 && buttonID <= 5)
            if (blockEntity instanceof ArrowIndicatorBlockEntity be) {
                be.setModelBase(buttonID - 3);
                be.setRange(range[0], range[1]);
                be_changed = true;
            }
        if (buttonID == 6)
            if (blockEntity instanceof ArrowIndicatorBlockEntity be) {
                be.setModelGlass(!be.isModelGlass());
                be_changed = true;
            }
        if (buttonID == 7)
            if (blockEntity instanceof ArrowIndicatorBlockEntity be) {
                be.BASE_READ = !be.BASE_READ;
                be_changed = true;
            }
        if (buttonID > 7 && buttonID < 13) {
            //OnBlockRightClickedProcedure.execute(world, pos);
            int i = buttonID - 8;
            if(i == 4){
                if (blockEntity instanceof ArrowIndicatorBlockEntity be) {
                    be.BASE_READ = !be.BASE_READ;
                    be_changed = true;
                }
            } else {
                BlockState ThisBlock = world.getBlockState(pos);
                int connection = ThisBlock.getValue(ArrowIndicatorBlock.CONNECTION) + 1;
                if(connection == 16)
                    connection = 0;
                //RedstonecgMod.LOGGER.debug(connection+" "+i);
                connection ^= 1 << i;
                connection--;
                if (connection < 0) {
                    connection = 15;
                }
                ThisBlock = ThisBlock.setValue(ArrowIndicatorBlock.CONNECTION, connection);
                world.setBlock(pos, ThisBlock, 3);
            }
        }
        if (buttonID == 0) {
            EditBox range_box_start = (EditBox)guistate.get("box:range_box_start");
            EditBox range_box_end = (EditBox)guistate.get("box:range_box_end");
            String range_start = this.tag.getString("range_start");
            String range_end = this.tag.getString("range_end");
            try {
                float value = Float.parseFloat(range_start);
                value = Mth.clamp(value, -999.9999f, 999.9999f);
                int h = (int) (value * 16);
                if(range[0] != h) {
                    range[0] = h;
                    be_changed = true;
                }
            } catch (NumberFormatException ignored) {}
            try {
                float value = Float.parseFloat(range_end);
                value = Mth.clamp(value, -999.9999f, 999.9999f);
                int h = (int) (value * 16);
                if(range[1] != h) {
                    range[1] = h;
                    be_changed = true;
                }
            } catch (NumberFormatException ignored) {}
            if(be_changed)
                if (blockEntity instanceof ArrowIndicatorBlockEntity be) {
                    be.setRange(range[0], range[1]);
                }
            range_box_start.setValue(String.valueOf(((float)range[0])/16.0f));
            range_box_end.setValue(String.valueOf(((float)range[1])/16.0f));
        }
        if(be_changed) {
            blockEntity.setChanged();
            MessengerBlockEntityPigeon.send(new MessengerBlockEntityPigeon(pos, blockEntity.getUpdateTag()));
            world.scheduleTick(pos, world.getBlockState(pos).getBlock(), 2);
        }
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
