package net.acodonic_king.redstonecg.block.defaults;

import net.acodonic_king.redstonecg.procedures.BlockFrameTransformUtils;
import net.acodonic_king.redstonecg.procedures.CanConnectWallGateProcedure;
import net.acodonic_king.redstonecg.procedures.ConnectionFace;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class DefaultParallelGate extends DefaultRedstoneActionGate implements ParallelGateInterface{
    public DefaultParallelGate() {
        super();
    }
    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
        ConnectionFace connectionFaceB = BlockFrameTransformUtils.canConnectRedstoneTargetConnectionFace(world, pos, side);
        return CanConnectWallGateProcedure.To4Gate(state, connectionFaceB);
    }
    @Override
    public int onRedstoneUpdate(LevelAccessor world, BlockState thisState, BlockPos thisPos, BlockPos fromPos, int recursion){
        Direction updateDirection = BlockFrameTransformUtils.directionFromPositions(fromPos, thisPos);
        Direction direction = BlockFrameTransformUtils.getLocalDirectionFromWorld(thisState, updateDirection);
        if(direction.getAxis() == Direction.Axis.X && !breakParallelLine(world, thisState, thisPos, updateDirection, false)){
            sendRedstoneUpdateInDirection(world, thisState.getBlock(), thisPos, updateDirection, 0);
        }
        return onRedstoneUpdate(world, thisState, thisPos, recursion);
    }
    @Override
    public boolean breakParallelLine(LevelAccessor world, BlockState thisState, BlockPos thisPos, Direction directedTo, boolean readOut){
        return false;
    }
    @Override
    public int breakParallelLineSignal(LevelAccessor world, BlockState thisState, BlockPos thisPos, Direction directedTo){
        return 0;
    }
}
