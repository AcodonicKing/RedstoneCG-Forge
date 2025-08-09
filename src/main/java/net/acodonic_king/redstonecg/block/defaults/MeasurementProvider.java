package net.acodonic_king.redstonecg.block.defaults;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public interface MeasurementProvider {
    String getMeasurement(LevelAccessor world, BlockState blockState, BlockPos pos);
}
