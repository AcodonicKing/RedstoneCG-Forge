package net.acodonic_king.redstonecg.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MessengerBlockEntityPigeonClient {
    public static void handle(MessengerBlockEntityPigeon message){
        ClientLevel world = Minecraft.getInstance().level;
        if (world == null) return;
        BlockEntity be = world.getBlockEntity(message.POS);
        if(be == null) return;
        be.handleUpdateTag(message.TAG);
        be.setChanged();
        world.sendBlockUpdated(be.getBlockPos(), be.getBlockState(), be.getBlockState(), 0);
    }
}
