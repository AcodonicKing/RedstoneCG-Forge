package net.acodonic_king.redstonecg.block.entity;

import net.acodonic_king.redstonecg.init.RedstonecgModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class DefaultAnalogGateBlockEntity extends SuperBlockEntity {
    public int POWER = 0;
    public DefaultAnalogGateBlockEntity(BlockPos pos, BlockState state) {
        super(RedstonecgModBlockEntities.DEFAULT_ANALOG_GATE.get(), pos, state);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putByte("power", (byte) POWER);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        POWER = tag.getByte("power");
    }
}
