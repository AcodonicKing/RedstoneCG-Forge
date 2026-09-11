package net.acodonic_king.redstonecg.block.defaults;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.apache.commons.lang3.tuple.Pair;

public interface PrimarySecondaryDirectionBlockInterface {
    DirectionProperty primaryDirectionProperty();
    DirectionProperty secondaryDirectionProperty();
    default Direction getPrimaryDirection(BlockState blockState){
        return blockState.getValue(primaryDirectionProperty());
    }
    default Direction getSecondaryDirection(BlockState blockState){
        return blockState.getValue(secondaryDirectionProperty());
    }
    default Pair<Direction,Direction> getPrimarySecondaryDirections(BlockState blockState){
        return Pair.of(getPrimaryDirection(blockState), getSecondaryDirection(blockState));
    }
}
