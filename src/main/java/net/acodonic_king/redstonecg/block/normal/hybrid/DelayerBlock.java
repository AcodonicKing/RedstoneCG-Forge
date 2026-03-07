package net.acodonic_king.redstonecg.block.normal.hybrid;

import net.acodonic_king.redstonecg.block.defaults.DelayerBlockBase;
import net.acodonic_king.redstonecg.block.defaults.PinMarkConnectionInterface;
import net.acodonic_king.redstonecg.block.entity.DefaultAnalogGateBlockEntity;
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

public class DelayerBlock extends DelayerBlockBase implements PinMarkConnectionInterface {
    public static final IntegerProperty CONNECTION = IntegerProperty.create("connection",0,5);
    public DelayerBlock(){
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
    public int[] getSidePower(BlockState blockState, LevelAccessor world, BlockPos pos, Pair<Direction,Direction> dirs){
        Direction[] Sides = GetGateInputSidesProcedure.Get2ABGateForth(blockState);
        int[] power = {0,0};
        int i = 0;
        for(Direction side: Sides) {
            ConnectionFace thisFace = BlockFrameTransformUtils.getConnectionFace(dirs, side);
            power[i] = GetRedstoneSignalProcedure.execute(world, pos, thisFace);
            i++;
        }
        return power;
    }

    @Override
    public int getConnection(BlockState bs) {
        return bs.getValue(CONNECTION);
    }

    @Override
    public int connectionFilter(int connection) {
        return CanConnectWallGateProcedure.To2ABGateConnectionFilter(connection);
    }
}
