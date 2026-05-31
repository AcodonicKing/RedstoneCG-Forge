package net.acodonic_king.redstonecg.block.control_panel.logic;

import net.acodonic_king.redstonecg.block.defaults.PowerIntegerPropertyInterface;
import net.acodonic_king.redstonecg.block.entity.ControlPanelBlockEntity;
import net.acodonic_king.redstonecg.block.entity.DefaultColoredLampBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;

public class ColorfulLampPanelLogic extends ColoredLampPanelLogic{
    public ColorfulLampPanelLogic(ItemStack itemStack, int slot) {
        super(itemStack, slot);
    }
    @Override
    public ItemStack saveStack(){
        CompoundTag compound = ITEM_STACK.getOrCreateTag();
        CompoundTag tag = new CompoundTag();
        if (compound.contains("BlockParameterSet"))
            tag = compound.getCompound("BlockParameterSet");
        if (BLOCK_STATE.getBlock() instanceof PowerIntegerPropertyInterface b)
            tag.putInt("power", BLOCK_STATE.getValue(b.getPowerIntegerProperty()));
        tag.putInt("color", DefaultColoredLampBlockEntity.getColor(COLOR));
        compound.put("BlockParameterSet", tag);
        return ITEM_STACK;
    }
    @Override
    public boolean receiveRedstone(ControlPanelBlockEntity be, LevelAccessor world, BlockPos pos, int power){
        if (BLOCK_STATE.getBlock() instanceof PowerIntegerPropertyInterface b) {
            if(BLOCK_STATE.getValue(b.getPowerIntegerProperty()) == power)
                return false;
            BLOCK_STATE = BLOCK_STATE.setValue(b.getPowerIntegerProperty(), power);
            if(power == 15)
                DefaultColoredLampBlockEntity.setColor(DyeColor.WHITE.getTextColor(), COLOR);
            else
                DefaultColoredLampBlockEntity.setColor(DyeColor.byId(power).getTextColor(), COLOR);
        } else
            return false;
        saveStack();
        return true;
    }
}
