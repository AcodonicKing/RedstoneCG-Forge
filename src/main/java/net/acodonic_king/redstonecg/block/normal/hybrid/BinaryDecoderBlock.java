package net.acodonic_king.redstonecg.block.normal.hybrid;

import net.acodonic_king.redstonecg.block.defaults.DefaultAnalogInteractable3ABCGate;
import net.acodonic_king.redstonecg.block.entity.DefaultAnalogGateBlockEntity;
import net.acodonic_king.redstonecg.procedures.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class BinaryDecoderBlock extends DefaultAnalogInteractable3ABCGate {
    public BinaryDecoderBlock(){
        super();
    }

    @Override
    public int onRedstoneUpdate(LevelAccessor world, BlockState blockState, BlockPos pos, int recursion){
        Direction[] Sides = GetGateInputSidesProcedure.Get3ABCGateForth(blockState);
        int[] power = {0,0};
        for(int i = 0; i < 2; i++){
            Direction side = Sides[i];
            ConnectionFace thisFace = BlockFrameTransformUtils.getConnectionFace(blockState, side);
            power[i] = GetRedstoneSignalProcedure.execute(world, pos, thisFace);
        }

        boolean state = (power[0] & power[1]) > 0;
        if(state)
            power[0] &= ~power[1];

        setPower(world, blockState, pos, power[0], recursion);
        LittleTools.setBooleanProperty(world, pos, state, "visible_state", 2);
        Direction direction = BlockFrameTransformUtils.getWorldDirectionFromLocal(blockState, Sides[2]);
        this.sendRedstoneUpdateInDirection(world,blockState.getBlock(),pos,direction, recursion);
        return power[0];
    }

    @Override
    public void setPower(LevelAccessor level, BlockState state, BlockPos pos, int power, int recursion){
        power = Math.max(0, Math.min(power, 15));
        Level world = (Level) level;
        if (world.getBlockEntity(pos) instanceof DefaultAnalogGateBlockEntity be){
            if(be.POWER != power){
                be.POWER = power;
                be.setChanged();
                Direction direction = BlockFrameTransformUtils.getWorldDirectionFromLocalForward(state);
                this.sendRedstoneUpdateInDirection(level,state.getBlock(),pos,direction, recursion);
            }
        }
    }

    @Override
    public String getMeasurement(LevelAccessor world, BlockState blockState, BlockPos pos){
        if(world.getBlockEntity(pos) instanceof DefaultAnalogGateBlockEntity blockEntity){
            return "%1d, %2d".formatted(blockEntity.POWER, blockState.getValue(VISIBLE_STATE) ? 15 : 0);
        }
        return "";
    }

    @Override
    public int getRedstonePower(LevelAccessor world, BlockPos pos, ConnectionFace sourceFace) {
        BlockState blockState = world.getBlockState(pos);
        ConnectionFace bus = BlockFrameTransformUtils.getConnectionFace(blockState, Direction.NORTH);
        Direction lead_side = GetGateInputSidesProcedure.Get3ABCGate(Direction.NORTH, blockState.getValue(CONNECTION), 3);
        ConnectionFace lead = BlockFrameTransformUtils.getConnectionFace(blockState, lead_side);
        if(bus.canConnect(sourceFace)){
            if(world.getBlockEntity(pos) instanceof DefaultAnalogGateBlockEntity be){
                return be.POWER;
            }
        }
        if(lead.canConnect(sourceFace)){
            return blockState.getValue(VISIBLE_STATE) ? 15 : 0;
        }
        return 0;
    }

    /*@Override
    public ConnectionFace getOutputConnectionFace(LevelAccessor world, BlockPos pos, ConnectionFace requesterFace) {
        BlockState blockState = world.getBlockState(pos);
        Direction lead_side = GetGateInputSidesProcedure.Get3ABCGate(Direction.NORTH, blockState.getValue(CONNECTION), 3);
        ConnectionFace lead = BlockFrameTransformUtils.getConnectionFace(blockState, lead_side);
        if(lead.canConnect(requesterFace))
            return lead;
        return  BlockFrameTransformUtils.getConnectionFace(blockState, Direction.NORTH);
    }*/
}
