package net.acodonic_king.redstonecg.block.entity;

import net.acodonic_king.redstonecg.init.RedstonecgModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class DefaultDigitalGateBlockEntity extends SuperBlockEntity {
    public boolean OUTPUT = false;
    public DefaultDigitalGateBlockEntity(BlockPos pos, BlockState state) {
        super(RedstonecgModBlockEntities.DEFAULT_DIGITAL_GATE.get(), pos, state);
    }

    // Save NBT
    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean("power", OUTPUT);
    }

    // Load NBT
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        OUTPUT = tag.getBoolean("power");
    }
}
