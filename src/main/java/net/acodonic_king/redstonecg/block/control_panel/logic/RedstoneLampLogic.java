package net.acodonic_king.redstonecg.block.control_panel.logic;

import net.acodonic_king.redstonecg.RedstonecgMod;
import net.acodonic_king.redstonecg.block.control_panel.BlockStateRenderParams;
import net.acodonic_king.redstonecg.block.control_panel.ComposedTextInterface;
import net.acodonic_king.redstonecg.block.entity.ControlPanelBlockEntity;
import net.acodonic_king.redstonecg.procedures.LittleTools;
import net.acodonic_king.redstonecg.procedures.TextFormatProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class RedstoneLampLogic extends DefaultPanelLogic implements BlockStateRenderParams, ComposedTextInterface {
    public boolean STATE = false;
    public BlockState BLOCK_STATE = null;
    public TextFormatProcedure.ComposedText RENDER_TEXT = new TextFormatProcedure.ComposedText();
    public RedstoneLampLogic(ItemStack itemStack, int slot) {
        super(itemStack, slot);
        loadStack(itemStack);
    }
    @Override
    public ItemStack saveStack(){
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("state", STATE);
        ITEM_STACK.getOrCreateTag().put("BlockParameterSet", tag);
        return ITEM_STACK;
    }

    @Override
    public void loadStack(ItemStack itemStack) {
        ITEM_STACK = itemStack;
        if(itemStack.getItem() instanceof BlockItem bi) {
            Block block = bi.getBlock();
            BLOCK_STATE = block.getStateDefinition().any();
        }
        CompoundTag tag = itemStack.getOrCreateTag();
        if (tag.contains("BlockParameterSet")) {
            tag = tag.getCompound("BlockParameterSet");
            if (tag.contains("state"))
                setState(tag.getBoolean("state"));
        }
        String name = TextFormatProcedure.getCustomItemName(itemStack);
        if(name.isEmpty())
            RENDER_TEXT.clear();
        else
            RENDER_TEXT.load(name);
    }

    public void setState(boolean state){
        STATE = state;
        if(BLOCK_STATE != null)
            BLOCK_STATE = LittleTools.setBooleanProperty(BLOCK_STATE, state, "lit");
    }

    @Override
    public boolean receiveRedstone(ControlPanelBlockEntity be, LevelAccessor world, BlockPos pos, int power){
        boolean state = power > 0;
        setState(state);
        return true;
    }

    @Override
    public BlockState getBlockState() {
        return BLOCK_STATE;
    }

    @Override
    public float scale() {
        return 0.5f;
    }

    @Override
    public float move() {
        return 0.25f;
    }

    @Override
    public float r() {
        return 1;
    }

    @Override
    public float g() {
        return 1;
    }

    @Override
    public float b() {
        return 1;
    }

    @Override
    public TextFormatProcedure.ComposedText getComposedText() {
        return RENDER_TEXT;
    }
}
