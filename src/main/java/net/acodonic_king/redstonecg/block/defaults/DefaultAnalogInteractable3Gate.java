package net.acodonic_king.redstonecg.block.defaults;

import net.acodonic_king.redstonecg.procedures.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class DefaultAnalogInteractable3Gate extends DefaultAnalogInteractableGate implements PinMarkConnectionInterface{
    public DefaultAnalogInteractable3Gate(){super();}

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
        byte connectionFaceB = BlockFrameTransformUtils.canConnectRedstoneTargetConnectionFace(world, pos, side);
        return CanConnectWallGateProcedure.To3Gate(state, connectionFaceB);
    }

    public int redstonePowerOperation(int SideRightPower, int SideBackPower, int SideLeftPower){return 0;}

    @Override
    public int onRedstoneUpdate(LevelAccessor world, BlockState blockState, BlockPos pos, int recursion){
        Direction[] Sides = GetGateInputSidesProcedure.Get3GateForth(blockState);
        int [] power = {0,0,0};
        int i = 0;
        for(Direction side: Sides){
            byte thisFace = BlockFrameTransformUtils.getConnectionFace(blockState, side);
            power[i] = GetRedstoneSignalProcedure.execute(world, pos, thisFace);
            i++;
        }
        int output = this.redstonePowerOperation(power[0], power[1], power[2]);
        setPower(world, blockState, pos, output, recursion);
        return output;
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
