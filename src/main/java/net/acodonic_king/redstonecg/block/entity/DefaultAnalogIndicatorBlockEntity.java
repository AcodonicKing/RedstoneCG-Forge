package net.acodonic_king.redstonecg.block.entity;

import net.acodonic_king.redstonecg.init.RedstonecgModBlockEntities;
import net.acodonic_king.redstonecg.init.RedstonecgModBlocks;
import net.acodonic_king.redstonecg.procedures.TextFormatProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class DefaultAnalogIndicatorBlockEntity extends OrientationHolderBlockEntity {
    public boolean BASE_READ = false;
    public float TEXT_Z = 0f;
    public String CUSTOM_NAME = "";
    public TextFormatProcedure.ComposedText RENDER_TEXT = new TextFormatProcedure.ComposedText();
    public DefaultAnalogIndicatorBlockEntity(BlockPos pos, BlockState state){
        super(RedstonecgModBlockEntities.DEFAULT_ANALOG_INDICATOR.get(), pos, state);
        if(
                state.is(RedstonecgModBlocks.UNIVERSAL_INDICATOR.get()) ||
                state.is(RedstonecgModBlocks.FLAT_LAMP_INDICATOR.get())
        )
            TEXT_Z = 0.094f;
        if(state.is(RedstonecgModBlocks.BAR_INDICATOR.get()))
            TEXT_Z = 0.25f;
        //modelUpdate();
    }
    public DefaultAnalogIndicatorBlockEntity(BlockEntityType blockEntityType, BlockPos pos, BlockState state){
        super(blockEntityType, pos, state);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean("base_read", BASE_READ);
        if(!CUSTOM_NAME.isEmpty())
            tag.putString("CustomName", CUSTOM_NAME);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("base_read"))
            BASE_READ = tag.getBoolean("base_read");
        if(tag.contains("CustomName")) setName(tag.getString("CustomName"));
    }

    public void setName(ItemStack stack){
        CUSTOM_NAME = TextFormatProcedure.getCustomItemName(stack);
        setText();
    }

    public void setName(String name){
        CUSTOM_NAME = name;
        setText();
    }

    public void setText(){
        if(CUSTOM_NAME.isEmpty())
            RENDER_TEXT.clear();
        else
            RENDER_TEXT.load(CUSTOM_NAME);
    }

    public float textZ(){
        return TEXT_Z;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
