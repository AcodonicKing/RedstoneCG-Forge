package net.acodonic_king.redstonecg.block.control_panel.logic;

import net.acodonic_king.redstonecg.block.control_panel.ComposedTextInterface;
import net.acodonic_king.redstonecg.block.entity.ControlPanelBlockEntity;
import net.acodonic_king.redstonecg.block.normal.interaction.RedSwitchBlock;
import net.acodonic_king.redstonecg.init.RedstonecgModBlocks;
import net.acodonic_king.redstonecg.procedures.RCGMatrix;
import net.acodonic_king.redstonecg.procedures.TextFormatProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;

public class RedSwitchPanelLogic extends DefaultPanelLogic implements ComposedTextInterface {
    public boolean OUTPUT = false;
    public TextFormatProcedure.ComposedText RENDER_TEXT = new TextFormatProcedure.ComposedText();
    public RedSwitchPanelLogic(ItemStack itemStack, int slot) {
        super(itemStack, slot);
        loadStack(itemStack);
    }

    @Override
    public ItemStack saveStack(){
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("output", OUTPUT);
        ITEM_STACK.getOrCreateTag().put("BlockParameterSet", tag);
        return ITEM_STACK;
    }

    @Override
    public void loadStack(ItemStack itemStack) {
        ITEM_STACK = itemStack;
        CompoundTag tag = itemStack.getTag();
        if(tag != null) {
            if (tag.contains("BlockParameterSet")) {
                tag = tag.getCompound("BlockParameterSet");
                if (tag.contains("output"))
                    OUTPUT = tag.getBoolean("output");
            }
        }
        String name = TextFormatProcedure.getCustomItemName(itemStack);
        if(name.isEmpty())
            RENDER_TEXT.clear();
        else
            RENDER_TEXT.load(name);
    }

    @Override
    public InteractionResult use(ControlPanelBlockEntity be, LevelAccessor world, BlockPos pos, Player player){
        OUTPUT = !OUTPUT;
        playSound(world, pos);
        saveStack();
        return InteractionResult.SUCCESS;
    }

    public void playSound(LevelAccessor world, BlockPos pos){
        world.playSound(null, pos, OUTPUT ? SoundEvents.STONE_BUTTON_CLICK_ON : SoundEvents.STONE_BUTTON_CLICK_OFF, SoundSource.BLOCKS, 1.0f, 1.0f);
    }

    @Override
    public int provideRedstone(ControlPanelBlockEntity be, LevelAccessor world, BlockPos pos){
        return OUTPUT ? 15 : 0;
    }

    public RCGMatrix.M4F stateTransformer(RCGMatrix.M4F mat, boolean state){
        return ((RedSwitchBlock)RedstonecgModBlocks.RED_SWITCH.get()).stateTransformer(mat, state);
    }

    @Override
    public TextFormatProcedure.ComposedText getComposedText() {
        return RENDER_TEXT;
    }
}
