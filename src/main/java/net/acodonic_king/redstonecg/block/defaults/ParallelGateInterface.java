package net.acodonic_king.redstonecg.block.defaults;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public interface ParallelGateInterface {
    boolean breakParallelLine(LevelAccessor world, BlockState thisState, BlockPos thisPos, Direction directedTo, boolean readOut);
    int breakParallelLineSignal(LevelAccessor world, BlockState thisState, BlockPos thisPos, Direction directedTo);
}
