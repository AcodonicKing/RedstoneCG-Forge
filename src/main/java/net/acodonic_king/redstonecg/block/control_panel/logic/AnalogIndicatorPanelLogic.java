package net.acodonic_king.redstonecg.block.control_panel.logic;

import net.acodonic_king.redstonecg.block.defaults.DefaultIndicatorBooleanInteractableGate;
import net.acodonic_king.redstonecg.block.defaults.DefaultIndicatorInteractableGate;
import net.acodonic_king.redstonecg.block.defaults.DefaultIndicatorRedstoneInteractableGate;
import net.acodonic_king.redstonecg.block.defaults.PowerIntegerPropertyInterface;
import net.acodonic_king.redstonecg.block.entity.ControlPanelBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class AnalogIndicatorPanelLogic extends DefaultPanelLogic{
    public BlockState BLOCK_STATE;

    public AnalogIndicatorPanelLogic(ItemStack itemStack, int slot) {
        super(itemStack, slot);
        if(itemStack.getItem() instanceof BlockItem bi){
            BLOCK_STATE = bi.getBlock().getStateDefinition().any()
                    .setValue(DefaultIndicatorInteractableGate.CONNECTION, 15);
            loadStack(itemStack);
        }
    }

    @Override
    public ItemStack saveStack(){
        CompoundTag tag = new CompoundTag();
        if(BLOCK_STATE.getBlock() instanceof DefaultIndicatorRedstoneInteractableGate)
            tag.putInt("power", BLOCK_STATE.getValue(DefaultIndicatorRedstoneInteractableGate.POWER));
        if(BLOCK_STATE.getBlock() instanceof DefaultIndicatorBooleanInteractableGate)
            tag.putBoolean("state", BLOCK_STATE.getValue(DefaultIndicatorBooleanInteractableGate.STATE));
        ITEM_STACK.getOrCreateTag().put("BlockParameterSet", tag);
        return ITEM_STACK;
    }

    @Override
    public void loadStack(ItemStack itemStack){
        ITEM_STACK = itemStack;
        CompoundTag tag = itemStack.getTag();
        if(tag != null && BLOCK_STATE != null){
            if(tag.contains("BlockParameterSet")){
                tag = tag.getCompound("BlockParameterSet");
                if(tag.contains("power"))
                    BLOCK_STATE = BLOCK_STATE.setValue(DefaultIndicatorRedstoneInteractableGate.POWER, tag.getInt("power"));
                if(tag.contains("state"))
                    BLOCK_STATE = BLOCK_STATE.setValue(DefaultIndicatorBooleanInteractableGate.STATE, tag.getBoolean("state"));
            }
        }
    }

    @Override
    public boolean receiveRedstone(ControlPanelBlockEntity be, LevelAccessor world, BlockPos pos, int power){
        if (BLOCK_STATE.getBlock() instanceof PowerIntegerPropertyInterface b) {
            if(BLOCK_STATE.getValue(b.getPowerIntegerProperty()) == power)
                return false;
            BLOCK_STATE = BLOCK_STATE.setValue(b.getPowerIntegerProperty(), power);
        } else if(BLOCK_STATE.getBlock() instanceof DefaultIndicatorBooleanInteractableGate) {
            if(BLOCK_STATE.getValue(DefaultIndicatorBooleanInteractableGate.STATE) == (power > 0))
                return false;
            BLOCK_STATE = BLOCK_STATE.setValue(DefaultIndicatorBooleanInteractableGate.STATE, power > 0);
        } else
            return false;
        saveStack();
        return true;
    }
}
