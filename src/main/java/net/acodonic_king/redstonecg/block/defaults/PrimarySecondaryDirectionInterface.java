package net.acodonic_king.redstonecg.block.defaults;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import org.apache.commons.lang3.tuple.Pair;

public interface PrimarySecondaryDirectionInterface {
    default Pair<Direction,Direction> getPrimarySecondaryDirections(LevelAccessor world, BlockPos pos){
        return Pair.of(getPrimaryDirection(world, pos), getSecondaryDirection(world, pos));
    };
    Direction getPrimaryDirection(LevelAccessor world, BlockPos pos);
    Direction getSecondaryDirection(LevelAccessor world, BlockPos pos);
}
