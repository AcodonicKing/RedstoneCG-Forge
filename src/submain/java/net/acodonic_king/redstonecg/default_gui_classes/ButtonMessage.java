package net.acodonic_king.redstonecg.default_gui_classes;

import net.acodonic_king.redstonecg.RedstonecgMod;
import net.acodonic_king.redstonecg.init.RedstonecgModNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class ButtonMessage {
    public final int buttonID;
    public final BlockPos pos;
    public CompoundTag tag = new CompoundTag();
    public ButtonMessage(FriendlyByteBuf buffer) {
        this.buttonID = buffer.readInt();
        int x, y, z;
        x = buffer.readInt();
        y = buffer.readInt();
        z = buffer.readInt();
        this.pos = new BlockPos(x, y, z);
        this.tag = buffer.readNbt();
    }
    public ButtonMessage(int buttonID, BlockPos pos) {
        this.buttonID = buttonID;
        this.pos = pos;
    }
    public static void buffer(ButtonMessage message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.buttonID);
        buffer.writeInt(message.pos.getX());
        buffer.writeInt(message.pos.getY());
        buffer.writeInt(message.pos.getZ());
        buffer.writeNbt(message.tag);
    }
    public static void send(ButtonMessage msg){
        RedstonecgModNetworking.PACKET_HANDLER.sendToServer(msg);
    }
    public void handleButtonAction(Player entity){}
    public static void handleData(ButtonMessage message, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> message.handleButtonAction(Objects.requireNonNull(context.getSender())));
        context.setPacketHandled(true);
    }
    public ResourceLocation id() { //NeoForge requires this
        return null;
    }
}
