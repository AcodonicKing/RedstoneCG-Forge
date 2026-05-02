package net.acodonic_king.redstonecg.block.entity;

import net.acodonic_king.redstonecg.ModLoaderRider;
import net.acodonic_king.redstonecg.init.RedstonecgModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;

public class DefaultColoredFlatLampBlockEntity extends DefaultAnalogIndicatorBlockEntity{
    public byte[] COLOR = new byte[]{(byte) 242, (byte) 189, (byte) 116};
    public Item ITEM = null;
    public DefaultColoredFlatLampBlockEntity(BlockPos pos, BlockState state) {
        super(RedstonecgModBlockEntities.DEFAULT_COLORED_FLAT_LAMP.get(), pos, state);
    }
    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putByteArray("color", COLOR);
        if(ITEM != null)
            tag.putString("item", ModLoaderRider.getItemRegistryName(ITEM).toString());
    }
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        COLOR = tag.getByteArray("color");
        if(COLOR.length == 0)
            COLOR = new byte[]{(byte) 242, (byte) 189, (byte) 116};
        if(tag.contains("item"))
            ITEM = ModLoaderRider.getItemFromRegistry(new ResourceLocation(tag.getString("item")));
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
