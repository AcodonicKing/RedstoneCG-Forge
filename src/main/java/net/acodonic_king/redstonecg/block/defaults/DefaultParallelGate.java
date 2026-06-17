package net.acodonic_king.redstonecg.block.defaults;

import net.acodonic_king.redstonecg.network.RedstonecgModVariables;
import net.acodonic_king.redstonecg.procedures.BlockFrameTransformUtils;
import net.acodonic_king.redstonecg.procedures.CanConnectWallGateProcedure;
import net.acodonic_king.redstonecg.procedures.ConnectionFace;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
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
    public static int getParallelChainLimit(LevelAccessor world){
        return RedstonecgModVariables.MapVariables.get(world).parallelChainLimit;
    }
    @Override
    public int onRedstoneUpdate(LevelAccessor world, BlockState thisState, BlockPos thisPos, BlockPos fromPos, int recursion){
        Direction updateDirection = BlockFrameTransformUtils.directionFromPositions(fromPos, thisPos);
        Direction direction = BlockFrameTransformUtils.getLocalDirectionFromWorld(thisState, updateDirection);
        if(direction.getAxis() == Direction.Axis.X && !breakParallelLine(world, thisState, thisPos, updateDirection, false)){
            recursion++;
            if(recursion > getParallelChainLimit(world))
                return 0;
            sendRedstoneUpdateParallel(world, thisState.getBlock(), thisPos, updateDirection, recursion);
        }
        return onRedstoneUpdate(world, thisState, thisPos, recursion);
    }
    public static void sendRedstoneUpdateParallel(LevelAccessor level, Block thisBlock, BlockPos thisPos, Direction direction, int recursion){
        Level world = (Level) level;
        if(direction == null) {
            world.blockUpdated(thisPos, thisBlock);
            return;
        }
        BlockPos neighborPos = thisPos.relative(direction);
        BlockState bs = world.getBlockState(neighborPos);
        Block block = bs.getBlock();
        if (block instanceof DefaultRedstoneActionGate nb){
            nb.onRedstoneUpdate(world, bs, neighborPos, thisPos, recursion);
        } else if (block instanceof ParallelGateInterface nb){
            nb.onRedstoneUpdate(world, bs, neighborPos, thisPos, recursion);
        } else if (block instanceof WireInterface wi) {
            wi.onTick(world, neighborPos, recursion);
        } else {
            world.neighborChanged(neighborPos,thisBlock,thisPos);
        }
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
