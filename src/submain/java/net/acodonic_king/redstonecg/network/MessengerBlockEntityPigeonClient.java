package net.acodonic_king.redstonecg.network;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class MessengerBlockEntityPigeonClient {
    public static void handle(MessengerBlockEntityPigeon message){
        Level world = Minecraft.getInstance().level;
        if (world == null) return;
        BlockEntity be = world.getBlockEntity(message.POS);
        if(be == null) return;
        be.handleUpdateTag(message.TAG);
        be.setChanged();
    }
}
