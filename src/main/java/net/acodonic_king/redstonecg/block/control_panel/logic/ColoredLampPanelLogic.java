package net.acodonic_king.redstonecg.block.control_panel.logic;

import net.acodonic_king.redstonecg.ModLoaderRider;
import net.acodonic_king.redstonecg.block.defaults.*;
import net.acodonic_king.redstonecg.block.entity.ControlPanelBlockEntity;
import net.acodonic_king.redstonecg.block.entity.DefaultColoredLampBlockEntity;
import net.acodonic_king.redstonecg.block.normal.indicator.ColoredLampBlock;
import net.acodonic_king.redstonecg.block.normal.indicator.ColorfulLampBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BeaconBeamBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ColoredLampPanelLogic extends DefaultPanelLogic{
    public BlockState BLOCK_STATE;
    public byte[] COLOR = new byte[]{(byte) 242, (byte) 189, (byte) 116};
    public ColoredLampPanelLogic(ItemStack itemStack, int slot) {
        super(itemStack, slot);
        if(itemStack.getItem() instanceof BlockItem bi){
            Block block = bi.getBlock();
            BLOCK_STATE = block.getStateDefinition().any();
            if(block instanceof DefaultIndicatorInteractableGate g)
                BLOCK_STATE = BLOCK_STATE.setValue(DefaultIndicatorInteractableGate.CONNECTION, 15);
            loadStack(itemStack);
        }
    }
    @Override
    public ItemStack saveStack(){
        CompoundTag compound = ITEM_STACK.getOrCreateTag();
        CompoundTag tag = new CompoundTag();
        if (compound.contains("BlockParameterSet"))
            tag = compound.getCompound("BlockParameterSet");
        if (BLOCK_STATE.getBlock() instanceof PowerIntegerPropertyInterface b)
            tag.putInt("power", BLOCK_STATE.getValue(b.getPowerIntegerProperty()));
        if(tag.contains("color"))
            tag.putInt("color", DefaultColoredLampBlockEntity.getColor(COLOR));
        compound.put("BlockParameterSet", tag);
        return ITEM_STACK;
    }

    @Override
    public void loadStack(ItemStack itemStack) {
        ITEM_STACK = itemStack;
        CompoundTag tag = itemStack.getTag();
        if (tag != null && BLOCK_STATE != null) {
            if (tag.contains("BlockParameterSet")) {
                tag = tag.getCompound("BlockParameterSet");
                if (tag.contains("power"))
                    if (BLOCK_STATE.getBlock() instanceof PowerIntegerPropertyInterface b)
                        BLOCK_STATE = BLOCK_STATE.setValue(b.getPowerIntegerProperty(), tag.getInt("power"));
                if(tag.contains("color"))
                    DefaultColoredLampBlockEntity.setColor(tag.getInt("color"), COLOR);
                if(tag.contains("item")){
                    Item item = ModLoaderRider.getItemFromRegistry(new ResourceLocation(tag.getString("item")));
                    if (item instanceof BlockItem bi) {
                        if (bi.getBlock() instanceof BeaconBeamBlock bl) {
                            DefaultColoredLampBlockEntity.setColor(bl.getColor().getTextColor(), COLOR);
                        } else if (bi.getBlock() instanceof StainLampInterface bl) {
                            DefaultColoredLampBlockEntity.setColor(bl.getLampStainColor(), COLOR);
                        }
                    } else if (item instanceof StainLampInterface bl) {
                        DefaultColoredLampBlockEntity.setColor(bl.getLampStainColor(), COLOR);
                    }
                }
            }
        }
    }

    @Override
    public boolean receiveRedstone(ControlPanelBlockEntity be, LevelAccessor world, BlockPos pos, int power){
        if (BLOCK_STATE.getBlock() instanceof PowerIntegerPropertyInterface b) {
            if(BLOCK_STATE.getValue(b.getPowerIntegerProperty()) == power)
                return false;
            BLOCK_STATE = BLOCK_STATE.setValue(b.getPowerIntegerProperty(), power);
        } else
            return false;
        saveStack();
        return true;
    }
}
