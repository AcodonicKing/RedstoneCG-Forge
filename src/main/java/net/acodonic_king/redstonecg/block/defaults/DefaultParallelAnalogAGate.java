package net.acodonic_king.redstonecg.block.defaults;

import net.acodonic_king.redstonecg.block.entity.DefaultAnalogGateBlockEntity;
import net.acodonic_king.redstonecg.procedures.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import javax.annotation.Nullable;

public class DefaultParallelAnalogAGate extends DefaultParallelGate implements EntityBlock {
    //public static final IntegerProperty POWER = IntegerProperty.create("power",0,15);
    public static final BooleanProperty VISIBLE_STATE = BooleanProperty.create("visible_state");
    public DefaultParallelAnalogAGate(){
        super();
    }
    @Override
    public BlockState getStateForPlacementDirect(BlockPlaceContext context){
        BlockState blockState = super.getStateForPlacementDirect(context);
        if(blockState != null){blockState = blockState.setValue(VISIBLE_STATE,false);}
        return blockState;
    }
    @Override
    public BlockState getStateForPlacementOpposite(BlockPlaceContext context){
        BlockState blockState = super.getStateForPlacementOpposite(context);
        if(blockState != null){blockState = blockState.setValue(VISIBLE_STATE,false);}
        return blockState;
    }
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DefaultAnalogGateBlockEntity(pos, state);
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(VISIBLE_STATE);
    }
    public int redstonePowerOperation(int LinePower, int BackPower){return 0;}

    @Override
    public int onRedstoneUpdate(LevelAccessor world, BlockState thisState, BlockPos thisPos){
        int linePower = GetParallelSignalProcedure.getParallelLinePower(world, thisPos);
        ConnectionFace connectionFaceA = BlockFrameTransformUtils.getConnectionFace(thisState, Direction.SOUTH);
        int backPower = GetRedstoneSignalProcedure.execute(world, thisPos, connectionFaceA);
        int power = redstonePowerOperation(linePower, backPower);
        if(world.getBlockEntity(thisPos) instanceof DefaultAnalogGateBlockEntity be){
            if(be.POWER == power){ return 0; }
            be.POWER = power;
            be.setChanged();
            updateVisibleState((Level) world, thisPos, thisState, power);
            sendRedstoneUpdateInDirection(world, thisState.getBlock(), thisPos, connectionFaceA.FACE.getOpposite());
        }
        return 0;
    }

    public void updateVisibleState(Level world, BlockPos thisPos, BlockState thisBlock, int power){
        boolean output = power > 0;
        if(output != thisBlock.getValue(VISIBLE_STATE)){
            world.setBlock(thisPos, thisBlock.setValue(VISIBLE_STATE, output), 2);
        }
    }

    @Override
    public int getRedstonePower(LevelAccessor world, BlockPos pos, ConnectionFace sourceFace) {
        ConnectionFace thisFace = getOutputConnectionFace(world, pos, sourceFace);
        if(thisFace.canConnect(sourceFace)){
            if(world.getBlockEntity(pos) instanceof DefaultAnalogGateBlockEntity be){
                return be.POWER;
            }
        }
        return 0;
    }

    @Override
    public String getMeasurement(LevelAccessor world, BlockState blockState, BlockPos pos){
        if(world.getBlockEntity(pos) instanceof DefaultAnalogGateBlockEntity blockEntity){
            return String.format("%1d",blockEntity.POWER);
        }
        return "";
    }
}
