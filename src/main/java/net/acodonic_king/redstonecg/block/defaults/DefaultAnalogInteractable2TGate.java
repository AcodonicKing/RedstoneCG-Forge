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

public class DefaultAnalogInteractable2TGate extends DefaultAnalogInteractableGate implements PinMarkConnectionInterface {
    public static final IntegerProperty CONNECTION = IntegerProperty.create("connection",0,3);
    public DefaultAnalogInteractable2TGate(){super();}

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CONNECTION);
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
        byte connectionFaceB = BlockFrameTransformUtils.canConnectRedstoneTargetConnectionFace(world, pos, side);
        return CanConnectWallGateProcedure.To2TGate(state, connectionFaceB);
    }

    public int redstonePowerOperation(int[] SidePower){return 0;}

    @Override
    public int onRedstoneUpdate(LevelAccessor world, BlockState blockState, BlockPos pos, int recursion){
        Direction[] Sides = GetGateInputSidesProcedure.Get2TGateForth(blockState);
        int[] power = new int[Sides.length];
        int i = 0;
        for(Direction side: Sides){
            byte thisFace = BlockFrameTransformUtils.getConnectionFace(blockState, side);
            power[i] = GetRedstoneSignalProcedure.execute(world, pos, thisFace);
            i++;
        }
        int output = this.redstonePowerOperation(power);
        setPower(world, blockState, pos, output, recursion);
        return output;
    }

    @Override
    public int getConnection(BlockState bs) {
        return bs.getValue(CONNECTION);
    }

    @Override
    public int connectionFilter(int connection) {
        return CanConnectWallGateProcedure.To2TGateConnectionFilter(connection);
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
