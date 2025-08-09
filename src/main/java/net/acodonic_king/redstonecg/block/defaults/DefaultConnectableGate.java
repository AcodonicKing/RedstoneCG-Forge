package net.acodonic_king.redstonecg.block.defaults;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class DefaultConnectableGate extends DefaultGate implements MeasurementProvider{
    public DefaultConnectableGate(){
        super();
    }
    public boolean isOutput(LevelAccessor world, BlockState blockState, BlockPos pos, Direction direction){
        return false;
    }
    @Override
    public String getMeasurement(LevelAccessor world, BlockState blockState, BlockPos pos){
        return "";
    }
}
