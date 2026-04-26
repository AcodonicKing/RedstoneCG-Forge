package net.acodonic_king.redstonecg.block.defaults;

import net.acodonic_king.redstonecg.procedures.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class DefaultDigitalInteractable3Gate extends DefaultDigitalInteractableGate implements PinMarkConnectionInterface{
    public DefaultDigitalInteractable3Gate(){super();}

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
        ConnectionFace connectionFaceB = BlockFrameTransformUtils.canConnectRedstoneTargetConnectionFace(world, pos, side);
        return CanConnectWallGateProcedure.To3Gate(state, connectionFaceB);
    }

    public boolean redstoneOutputOperation(int SideRightPower, int SideBackPower, int SideLeftPower){return false;}

    @Override
    public int onRedstoneUpdate(LevelAccessor world, BlockState blockState, BlockPos pos){
        Direction[] Sides = GetGateInputSidesProcedure.Get3GateForth(blockState);
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

    @Override
    public int getConnection(BlockState bs) {
        return 0;
    }

    @Override
    public int connectionFilter(int connection) {
        return CanConnectWallGateProcedure.To3GateConnectionFilter(connection);
    }
}
