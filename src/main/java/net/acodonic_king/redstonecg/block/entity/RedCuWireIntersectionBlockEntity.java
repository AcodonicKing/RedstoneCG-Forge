package net.acodonic_king.redstonecg.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

import static net.acodonic_king.redstonecg.init.RedstonecgModBlockEntities.REDCU_WIRE_INTERSECTION;

public class RedCuWireIntersectionBlockEntity extends SuperBlockEntity {
    public int POWER_A = 0;
    public int POWER_B = 0;
    public RedCuWireIntersectionBlockEntity(BlockPos position, BlockState state) {
        super(REDCU_WIRE_INTERSECTION.get(), position, state);
    }
    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putByte("power_a", (byte) (POWER_A & 0xFF));
        tag.putByte("power_b", (byte) (POWER_B & 0xFF));
    }
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        POWER_A = tag.getByte("power_a") & 0xFF;
        POWER_B = tag.getByte("power_b") & 0xFF;
    }
}
