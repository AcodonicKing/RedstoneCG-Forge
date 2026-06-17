package net.acodonic_king.redstonecg.block.entity;

import net.acodonic_king.redstonecg.init.RedstonecgModBlockEntities;
import net.acodonic_king.redstonecg.procedures.TextFormatProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class RedToggleBlockEntity extends SuperBlockEntity {
    public String CUSTOM_NAME = "";
    public TextFormatProcedure.ComposedText RENDER_TEXT = new TextFormatProcedure.ComposedText();
    public RedToggleBlockEntity(BlockPos position, BlockState state) {
        super(RedstonecgModBlockEntities.RED_TOGGLE.get(), position, state);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if(!CUSTOM_NAME.isEmpty())
            tag.putString("CustomName", CUSTOM_NAME);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("CustomName")) setName(tag.getString("CustomName"));
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
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
}
