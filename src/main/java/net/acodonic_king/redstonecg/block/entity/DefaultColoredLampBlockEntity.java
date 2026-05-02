package net.acodonic_king.redstonecg.block.entity;

import net.acodonic_king.redstonecg.ModLoaderRider;
import net.acodonic_king.redstonecg.init.RedstonecgModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;

public class DefaultColoredLampBlockEntity extends SuperBlockEntity {
    public byte[] COLOR = new byte[]{(byte) 242, (byte) 189, (byte) 116};
    //public byte BRIGHTNESS = 0;
    public Item ITEM = null;
    public DefaultColoredLampBlockEntity(BlockPos position, BlockState state) {
        super(RedstonecgModBlockEntities.DEFAULT_COLORED_LAMP.get(), position, state);
    }
    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putByteArray("color", COLOR);
        //tag.putInt("color", encodeColor());
        if(ITEM != null)
            tag.putString("item", ModLoaderRider.getItemRegistryName(ITEM).toString());
            //tag.put("item", new ItemStack(ITEM).save(new CompoundTag()));
    }
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        COLOR = tag.getByteArray("color");
        if(COLOR.length != 3)
            COLOR = new byte[]{(byte) 242, (byte) 189, (byte) 116};
        //setColor(tag.getInt("color"));
        if(tag.contains("item"))
            ITEM = ModLoaderRider.getItemFromRegistry(new ResourceLocation(tag.getString("item")));
            //ITEM = ItemStack.of(tag.getCompound("item")).getItem();
    }
    @Override
    public void handleUpdateTag(CompoundTag tag) {
        load(tag);
    }
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
    public void setColor(int c){
        for(int i = 2; i >= 0; i--){
            COLOR[i] = (byte) (c & 0xFF);
            c >>= 8;
        }
    }
    public void setItem(Item item){
        ITEM = item;
    }
}
