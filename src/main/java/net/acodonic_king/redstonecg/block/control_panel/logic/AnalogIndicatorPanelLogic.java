package net.acodonic_king.redstonecg.block.control_panel.logic;

import net.acodonic_king.redstonecg.RedstonecgMod;
import net.acodonic_king.redstonecg.block.control_panel.ComposedTextInterface;
import net.acodonic_king.redstonecg.block.defaults.DefaultIndicatorBooleanInteractableGate;
import net.acodonic_king.redstonecg.block.defaults.DefaultIndicatorInteractableGate;
import net.acodonic_king.redstonecg.block.defaults.DefaultIndicatorRedstoneInteractableGate;
import net.acodonic_king.redstonecg.block.defaults.PowerIntegerPropertyInterface;
import net.acodonic_king.redstonecg.block.entity.ControlPanelBlockEntity;
import net.acodonic_king.redstonecg.procedures.TextFormatProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class AnalogIndicatorPanelLogic extends DefaultPanelLogic implements ComposedTextInterface {
    public BlockState BLOCK_STATE;
    public TextFormatProcedure.ComposedText RENDER_TEXT = new TextFormatProcedure.ComposedText();
    public float TEXT_Z = 0.0f;
    public int POWER = 0;

    public AnalogIndicatorPanelLogic(ItemStack itemStack, int slot, float text_z) {
        super(itemStack, slot);
        TEXT_Z = text_z;
        loadStack(itemStack);
    }

    @Override
    public ItemStack saveStack(){
        CompoundTag tag = new CompoundTag();
        tag.putInt("power", POWER);
        ITEM_STACK.getOrCreateTag().put("BlockParameterSet", tag);
        //RedstonecgMod.LOGGER.debug(SLOT+" "+ITEM_STACK.getTag()+"\n");
        return ITEM_STACK;
    }

    @Override
    public void loadStack(ItemStack itemStack){
        ITEM_STACK = itemStack;
        if(itemStack.getItem() instanceof BlockItem bi){
            BLOCK_STATE = bi.getBlock().getStateDefinition().any()
                    .setValue(DefaultIndicatorInteractableGate.CONNECTION, 15);
        }
        CompoundTag tag = itemStack.getTag();
        if(tag != null && BLOCK_STATE != null){
            if(tag.contains("BlockParameterSet")){
                tag = tag.getCompound("BlockParameterSet");
                if(tag.contains("power"))
                    receiveRedstone(tag.getInt("power"));
                else
                    receiveRedstone(0);
            }
        }
        String name = TextFormatProcedure.getCustomItemName(itemStack);
        if(name.isEmpty())
            RENDER_TEXT.clear();
        else
            RENDER_TEXT.load(name);
    }

    public boolean receiveRedstone(int power){
        /*if(POWER == power)
            return false;*/
        POWER = power;
        if (BLOCK_STATE == null)
            return false;
        if (BLOCK_STATE.getBlock() instanceof PowerIntegerPropertyInterface b) {
            BLOCK_STATE = BLOCK_STATE.setValue(b.getPowerIntegerProperty(), power);
        } else if(BLOCK_STATE.getBlock() instanceof DefaultIndicatorBooleanInteractableGate) {
            BLOCK_STATE = BLOCK_STATE.setValue(DefaultIndicatorBooleanInteractableGate.STATE, power > 0);
        } else
            return false;
        //RedstonecgMod.LOGGER.debug(power+" "+BLOCK_STATE);
        saveStack();
        return true;
    }

    @Override
    public boolean receiveRedstone(ControlPanelBlockEntity be, LevelAccessor world, BlockPos pos, int power){
        return receiveRedstone(power);
    }

    @Override
    public TextFormatProcedure.ComposedText getComposedText() {
        return RENDER_TEXT;
    }
}
