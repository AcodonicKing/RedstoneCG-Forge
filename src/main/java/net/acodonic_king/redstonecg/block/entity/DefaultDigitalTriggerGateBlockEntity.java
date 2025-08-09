package net.acodonic_king.redstonecg.block.entity;

import net.acodonic_king.redstonecg.init.RedstonecgModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class DefaultDigitalTriggerGateBlockEntity extends SuperBlockEntity {
    public boolean OUTPUT = false;
    public boolean UNLOCKED = true;
    public DefaultDigitalTriggerGateBlockEntity(BlockPos pos, BlockState state){
        super(RedstonecgModBlockEntities.DEFAULT_DIGITAL_TRIGGER_GATE.get(), pos, state);
    }
    // Save NBT
    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean("output", OUTPUT);
        tag.putBoolean("unlocked", UNLOCKED);
    }

    // Load NBT
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        OUTPUT = tag.getBoolean("output");
        UNLOCKED = tag.getBoolean("unlocked");
    }
}
