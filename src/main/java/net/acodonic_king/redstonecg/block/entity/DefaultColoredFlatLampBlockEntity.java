package net.acodonic_king.redstonecg.block.entity;

import net.acodonic_king.redstonecg.ModLoaderRider;
import net.acodonic_king.redstonecg.block.defaults.StainLampInterface;
import net.acodonic_king.redstonecg.init.RedstonecgModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.BeaconBeamBlock;
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
    public int getColor(){
        int c = 0;
        for(int i = 0; i <= 2; i++){
            c <<= 8;
            c |= COLOR[i] & 0xFF;
        }
        return c;
    }
    public void setItem(Item item){
        ITEM = item;
    }
    public Item getItem(){
        return ITEM;
    }
    public CompoundTag getParameterSet(){
        CompoundTag tag = new CompoundTag();
        if(ITEM != null)
            tag.putString("item", ModLoaderRider.getItemRegistryName(ITEM).toString());
        else
            tag.putInt("color", getColor());
        return tag;
    }
    public void setParameterSet(CompoundTag tag){
        if(tag.contains("item")) {
            ITEM = ModLoaderRider.getItemFromRegistry(new ResourceLocation(tag.getString("item")));
            if (ITEM instanceof BlockItem bi) {
                if (bi.getBlock() instanceof BeaconBeamBlock bl) {
                    setColor(bl.getColor().getTextColor());
                } else if (bi.getBlock() instanceof StainLampInterface bl) {
                    setColor(bl.getLampStainColor());
                }
            } else if (ITEM instanceof StainLampInterface bl) {
                setColor(bl.getLampStainColor());
            }
        }
        if(tag.contains("color"))
            setColor(tag.getInt("color"));
    }
}
