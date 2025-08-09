package net.acodonic_king.redstonecg.block.defaults;

import net.acodonic_king.redstonecg.procedures.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class DefaultDigitalInteractable3ABCGate extends DefaultDigitalInteractableGate {
    public static final IntegerProperty CONNECTION = IntegerProperty.create("connection",0,5);
    public DefaultDigitalInteractable3ABCGate(){super();}

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CONNECTION);
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
        ConnectionFace connectionFaceB = BlockFrameTransformUtils.canConnectRedstoneTargetConnectionFace(world, pos, side);
        return CanConnectWallGateProcedure.To4Gate(state, connectionFaceB);
    }

    public boolean redstoneOutputOperation(int SideAPower, int SideBPower, int SideCPower){return false;}

    @Override
    public int onRedstoneUpdate(LevelAccessor world, BlockState blockState, BlockPos pos){
        Direction[] Sides = GetGateInputSidesProcedure.Get3ABCGateForth(blockState);
        int [] power = {0,0,0};
        int i = 0;
        for(Direction side: Sides){
            ConnectionFace thisFace = BlockFrameTransformUtils.getConnectionFace(blockState, side);
            power[i] = GetRedstoneSignalProcedure.execute(world, pos, thisFace);
            i++;
        }
        boolean output = this.redstoneOutputOperation(power[0], power[1], power[2]);

        setOutput(world, blockState, pos, output);
        if (output) {return 15;}
        return 0;
    }

    /*@Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        BlockState nstate = super.mirror(state, mirrorIn);
        int connection = state.getValue(CONNECTION);
        if (connection == 0 || connection == 1){
            connection = connection == 0 ? 1 : 0;
        } else if (connection == 2 || connection == 5){
            connection = connection == 2 ? 5 : 2;
        } else if (connection == 3 || connection == 4){
            connection = connection == 3 ? 4 : 3;
        }
        return nstate.setValue(CONNECTION, connection);
    }*/
}
