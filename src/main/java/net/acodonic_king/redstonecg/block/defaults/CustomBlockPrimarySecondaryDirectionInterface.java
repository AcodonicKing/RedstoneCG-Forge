package net.acodonic_king.redstonecg.block.defaults;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import org.apache.commons.lang3.tuple.Pair;

public interface CustomBlockPrimarySecondaryDirectionInterface {
    Pair<Direction,Direction> getPrimarySecondaryDirections(LevelAccessor world, BlockPos pos);
}
