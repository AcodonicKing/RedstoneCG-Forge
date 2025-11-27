package net.acodonic_king.redstonecg.block.parallel.hybrid;

import net.acodonic_king.redstonecg.block.defaults.DelayerBlockBase;
import net.acodonic_king.redstonecg.block.defaults.ParallelGateInterface;
import net.acodonic_king.redstonecg.procedures.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.apache.commons.lang3.tuple.Pair;

public class ParallelDelayerBlock extends DelayerBlockBase implements ParallelGateInterface {
    public static final IntegerProperty CONNECTION = IntegerProperty.create("connection",0,1);
    public ParallelDelayerBlock(){
        super();
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CONNECTION);
    }
    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
        ConnectionFace connectionFaceB = BlockFrameTransformUtils.canConnectRedstoneTargetConnectionFace(world, pos, side);
        return CanConnectWallGateProcedure.execute(
                getPrimarySecondaryDirections((LevelAccessor) world, pos),
                CanConnectWallGateProcedure.To2ABGateConnectionFilter(state.getValue(CONNECTION)),
                connectionFaceB
        );
    }
    @Override
    public int[] getSidePower(BlockState thisState, LevelAccessor world, BlockPos thisPos, Pair<Direction,Direction> dirs){
        int linePower = GetParallelSignalProcedure.getParallelLinePower(world, thisPos);
        ConnectionFace connectionFaceA = BlockFrameTransformUtils.getConnectionFace(thisState, Direction.SOUTH);
        int backPower = GetRedstoneSignalProcedure.execute(world, thisPos, connectionFaceA);
        if(thisState.getValue(CONNECTION) == 0){
            return new int[]{backPower,linePower};
        }
        return new int[]{linePower, backPower};
    }

    @Override
    public void neighborChanged(BlockState thisState, Level world, BlockPos thisPos, Block neighborBlock, BlockPos fromPos, boolean moving) {
        super.neighborChanged(thisState, world, thisPos, neighborBlock, fromPos, moving);
        world.scheduleTick(thisPos, thisState.getBlock(), 1);
        Direction updateDirection = BlockFrameTransformUtils.directionFromPositions(fromPos, thisPos);
        Direction direction = BlockFrameTransformUtils.getLocalDirectionFromWorld(world, thisPos, updateDirection);
        if(direction.getAxis() == Direction.Axis.X && !breakParallelLine(world, thisState, thisPos, updateDirection, false)){
            sendRedstoneUpdateInDirection(world, thisState.getBlock(), thisPos, updateDirection);
        }
    }

    @Override
    public boolean breakParallelLine(LevelAccessor world, BlockState thisState, BlockPos thisPos, Direction directedTo, boolean readOut) {
        return false;
    }

    @Override
    public int breakParallelLineSignal(LevelAccessor world, BlockState thisState, BlockPos thisPos, Direction directedTo) {
        return 0;
    }
}
