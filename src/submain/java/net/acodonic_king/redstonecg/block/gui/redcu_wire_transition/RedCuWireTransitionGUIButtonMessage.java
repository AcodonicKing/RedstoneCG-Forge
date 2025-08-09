package net.acodonic_king.redstonecg.block.gui.redcu_wire_transition;

import net.acodonic_king.redstonecg.block.entity.RedCuWireTransitionBlockEntity;
import net.acodonic_king.redstonecg.init.RedstonecgModItems;
import net.acodonic_king.redstonecg.init.RedstonecgModNetworking;
import net.acodonic_king.redstonecg.init.RedstonecgModVersionRides;
import net.acodonic_king.redstonecg.procedures.AdventureProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class RedCuWireTransitionGUIButtonMessage {
    public final String button;
    public final BlockPos pos;

    public static void buffer(RedCuWireTransitionGUIButtonMessage message, FriendlyByteBuf buffer) {
        buffer.writeUtf(message.button);
        buffer.writeInt(message.pos.getX());
        buffer.writeInt(message.pos.getY());
        buffer.writeInt(message.pos.getZ());
    }
    public RedCuWireTransitionGUIButtonMessage(FriendlyByteBuf buffer) {
        this.button = buffer.readUtf();
        int x, y, z;
        x = buffer.readInt();
        y = buffer.readInt();
        z = buffer.readInt();
        this.pos = new BlockPos(x, y, z);
    }
    public RedCuWireTransitionGUIButtonMessage(String button, BlockPos pos) {
        this.button = button;
        this.pos = pos;
    }
    public static void send(RedCuWireTransitionGUIButtonMessage msg){
        RedstonecgModNetworking.PACKET_HANDLER.sendToServer(msg);
    }
    public static void handleData(RedCuWireTransitionGUIButtonMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> handleButtonAction(Objects.requireNonNull(context.getSender()), message.button, message.pos));
        context.setPacketHandled(true);
    }
    public static void sendAndHandle(Player entity, String button, BlockPos pos){
        send(new RedCuWireTransitionGUIButtonMessage(button, pos));
        handleButtonAction(entity, button, pos);
    }
    public static void handleButtonAction(Player entity, String button, BlockPos pos) {
        Level world = RedstonecgModVersionRides.getPlayerLevel(entity);
        if (!world.hasChunkAt(pos))
            return;
        if (world.isClientSide())
            return;
        //RedstonecgMod.LOGGER.debug("{} {}", button, pos);
        if(RedCuWireTransitionGUIScreen.button_locations.containsKey(button)){
            if(world.getBlockEntity(pos) instanceof RedCuWireTransitionBlockEntity be){
                String node = button;
                char side = node.charAt(0);
                byte val = be.getSideCharacter(side);
                byte set_to = (byte) (node.charAt(1) - '0');
                if(val == set_to){set_to = 5;}
                be.setSideCharacter(side, set_to);
                be.setChanged();
                BlockState bs = world.getBlockState(pos);
                world.sendBlockUpdated(pos, bs ,bs ,3);
            }
        }
        if(RedCuWireTransitionGUIScreen.button_smooth_stone_locations.containsKey(button)){
            if(world.getBlockEntity(pos) instanceof RedCuWireTransitionBlockEntity be) {
                int val = 1 << RedCuWireTransitionBlockEntity.getShapeIndexCharacter(button.charAt(4));
                Item item = RedstonecgModItems.SMOOTH_STONE_PLATE.get();
                boolean creative = AdventureProcedure.getGameMode(entity) == GameType.CREATIVE;
                if ((be.WALLS & val) > 0) {
                    if(creative)
                        be.WALLS &= (byte) ~val;
                    else if(insertOneItem(entity, item))
                        be.WALLS &= (byte) ~val;
                } else {
                    if(creative)
                        be.WALLS |= (byte) val;
                    else if(extractOneItem(entity, item))
                        be.WALLS |= (byte) val;
                }
                be.setChanged();
                BlockState bs = world.getBlockState(pos);
                world.sendBlockUpdated(pos, bs, bs, 3);
            }
        }
    }
    public static boolean extractOneItem(Player player, Item targetItem) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty() && stack.getItem() == targetItem) {
                stack.shrink(1);
                if (stack.isEmpty()) {
                    player.getInventory().setItem(i, ItemStack.EMPTY);
                }
                return true;
            }
        }
        return false; // Item not found
    }
    public static boolean insertOneItem(Player player, Item itemToInsert) {
        ItemStack toInsert = new ItemStack(itemToInsert, 1);
        boolean inserted = player.getInventory().add(toInsert);
        return inserted;
    }
}
