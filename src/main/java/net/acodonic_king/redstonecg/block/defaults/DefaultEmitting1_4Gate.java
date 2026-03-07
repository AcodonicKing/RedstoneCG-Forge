package net.acodonic_king.redstonecg.block.defaults;

import net.acodonic_king.redstonecg.procedures.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class DefaultEmitting1_4Gate extends DefaultRedstoneActionGate implements PinMarkConnectionInterface {
    public static final IntegerProperty CONNECTION = IntegerProperty.create("connection",0,14);
    //public static final IntegerProperty POWER = IntegerProperty.create("power",0,15);
    public DefaultEmitting1_4Gate(){
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
        return CanConnectWallGateProcedure.To1_4Gate(state, connectionFaceB);
    }

    @Override
    public ConnectionFace getOutputConnectionFace(LevelAccessor world, BlockPos pos, ConnectionFace requesterFace) {
        BlockState blockState = world.getBlockState(pos);
        ConnectionFace connectionFaceA = requesterFace.getConnectable();
        if(!CanConnectWallGateProcedure.To1_4Gate(blockState, requesterFace))
            connectionFaceA.CHANNEL = 5;
        return connectionFaceA;
    }

    @Override
    public int getConnection(BlockState bs) {
        return bs.getValue(CONNECTION);
    }

    @Override
    public int connectionFilter(int connection) {
        return CanConnectWallGateProcedure.To1_4GateConnectionFilter(connection);
    }

    /*@Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        BlockState nstate = super.mirror(state, mirrorIn);
        int connection = state.getValue(CONNECTION);
        connection++;
        int c2 = (connection & 2) << 2;
        int c8 = (connection & 8) >> 2;
        connection &= 0b0101;
        connection |= c2 | c8;
        return nstate.setValue(CONNECTION, connection);
    }*/
}
