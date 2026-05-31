package net.acodonic_king.redstonecg.block.defaults;

import net.acodonic_king.redstonecg.procedures.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class DefaultDigitalInteractable1Gate extends DefaultDigitalInteractableGate implements PinMarkConnectionInterface {
    public static final IntegerProperty CONNECTION = IntegerProperty.create("connection",0,2);
    public DefaultDigitalInteractable1Gate(){super();}

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CONNECTION);
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
        ConnectionFace connectionFaceB = BlockFrameTransformUtils.canConnectRedstoneTargetConnectionFace(world, pos, side);
        return CanConnectWallGateProcedure.To1Gate(state, connectionFaceB);
    }
    public boolean redstoneOutputOperation(int SidePower){return false;}

    @Override
    public int onRedstoneUpdate(LevelAccessor world, BlockState blockState, BlockPos pos, int recursion){
        Direction Side = GetGateInputSidesProcedure.Get1GateForth(blockState);
        ConnectionFace thisFace = BlockFrameTransformUtils.getConnectionFace(blockState, Side);
        int SidePower = GetRedstoneSignalProcedure.execute(world, pos, thisFace);
        boolean output = this.redstoneOutputOperation(SidePower);
        setOutput(world, blockState, pos, output, recursion);
        if (output) {return 15;}
        return 0;
    }

    @Override
    public int getConnection(BlockState bs) {
        return bs.getValue(CONNECTION);
    }

    @Override
    public int connectionFilter(int connection) {
        return CanConnectWallGateProcedure.To1GateConnectionFilter(connection);
    }

    /*@Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        BlockState nstate = super.mirror(state, mirrorIn);
        int connection = state.getValue(CONNECTION);
        if(connection == 0)
            return nstate;
        connection = connection == 1 ? 2 : 1;
        return nstate.setValue(CONNECTION, connection);
    }*/
}
