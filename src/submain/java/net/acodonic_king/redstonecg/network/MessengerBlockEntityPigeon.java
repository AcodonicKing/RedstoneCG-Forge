package net.acodonic_king.redstonecg.network;

import net.acodonic_king.redstonecg.RedstonecgMod;
import net.acodonic_king.redstonecg.init.RedstonecgModNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

//Minecraft... you have the worst synchronization issues possible.

public class MessengerBlockEntityPigeon {
    public final BlockPos POS;
    public final CompoundTag TAG;
    public static final ResourceLocation ID = new ResourceLocation(RedstonecgMod.MODID, "messenger_block_entity_pigeon");
    public MessengerBlockEntityPigeon(FriendlyByteBuf buffer){
        this.POS = buffer.readBlockPos();
        this.TAG = buffer.readNbt();
    }
    public MessengerBlockEntityPigeon(BlockPos pos, CompoundTag tag){
        this.POS = pos;
        this.TAG = tag;
    }
    public static void buffer(MessengerBlockEntityPigeon message, FriendlyByteBuf buffer){
        buffer.writeBlockPos(message.POS);
        buffer.writeNbt(message.TAG);
    }
    public static void send(MessengerBlockEntityPigeon msg){
        try {
            RedstonecgModNetworking.PACKET_HANDLER.sendToServer(msg);
        } catch (java.lang.NullPointerException ignored) {

        }
    }
    public static void handleData(MessengerBlockEntityPigeon message, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            Level world = Minecraft.getInstance().level;
            if (world == null) return;
            BlockEntity be = world.getBlockEntity(message.POS);
            if(be == null) return;
            be.load(message.TAG);
            be.setChanged();
        });
        context.setPacketHandled(true);
    }
}
