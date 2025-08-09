package net.acodonic_king.redstonecg.block.defaults;

import net.acodonic_king.redstonecg.block.entity.DefaultDigitalGateBlockEntity;
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

public class DefaultParallelDigitalAGate extends DefaultParallelGate implements EntityBlock {
    //public static final BooleanProperty OUTPUT = BooleanProperty.create("output");
    public static final BooleanProperty VISIBLE_STATE = BooleanProperty.create("visible_state");
    public DefaultParallelDigitalAGate(){
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
        return new DefaultDigitalGateBlockEntity(pos, state);
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(VISIBLE_STATE);
    }
    public boolean redstoneOutputOperation(int LinePower, int BackPower){return false;}

    @Override
    public int onRedstoneUpdate(LevelAccessor world, BlockState thisState, BlockPos thisPos){
        int linePower = GetParallelSignalProcedure.getParallelLinePower(world, thisPos);
        ConnectionFace connectionFaceA = BlockFrameTransformUtils.getConnectionFace(thisState, Direction.SOUTH);
        int backPower = GetRedstoneSignalProcedure.execute(world, thisPos, connectionFaceA);
        boolean output = redstoneOutputOperation(linePower, backPower);
        //RedstonecgMod.LOGGER.debug("{} {} {} {}", linePower, backPower, output, thisPos);
        if(world.getBlockEntity(thisPos) instanceof DefaultDigitalGateBlockEntity be){
            if(be.OUTPUT == output){ return 0; }
            be.OUTPUT = output;
            be.setChanged();
            updateVisibleState((Level) world, thisPos, thisState, output);
            sendRedstoneUpdateInDirection(world, thisState.getBlock(), thisPos, connectionFaceA.FACE.getOpposite());
        }
        return 0;
    }

    public void updateVisibleState(Level world, BlockPos thisPos, BlockState thisBlock, boolean output){
        if(output != thisBlock.getValue(VISIBLE_STATE)){
            world.setBlock(thisPos, thisBlock.setValue(VISIBLE_STATE, output), 2);
        }
    }

    @Override
    public int getRedstonePower(LevelAccessor world, BlockPos pos, ConnectionFace sourceFace) {
        ConnectionFace thisFace = getOutputConnectionFace(world, pos, sourceFace);
        if(thisFace.canConnect(sourceFace)){
            if(world.getBlockEntity(pos) instanceof DefaultDigitalGateBlockEntity be){
                return be.OUTPUT ? 15 : 0;
            }
        }
        return 0;
    }

    @Override
    public String getMeasurement(LevelAccessor world, BlockState blockState, BlockPos pos){
        if(world.getBlockEntity(pos) instanceof DefaultDigitalGateBlockEntity blockEntity){
            return String.format("%1d",blockEntity.OUTPUT ? 15 : 0);
        }
        return "";
    }
}
