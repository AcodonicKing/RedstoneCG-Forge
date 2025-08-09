package net.acodonic_king.redstonecg.block.defaults;

import net.acodonic_king.redstonecg.block.entity.DefaultDigitalGateBlockEntity;
import net.acodonic_king.redstonecg.block.entity.DefaultDigitalTriggerGateBlockEntity;
import net.acodonic_king.redstonecg.procedures.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class DefaultDigitalInteractibleTriggerGate extends DefaultRedstoneActionGate implements EntityBlock {
    public static final BooleanProperty VISIBLE_STATE = BooleanProperty.create("visible_state");
    public DefaultDigitalInteractibleTriggerGate(){
        super();
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(VISIBLE_STATE);
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
    @Override
    public InteractionResult use(BlockState blockstate, Level world, BlockPos pos, Player entity, InteractionHand hand, BlockHitResult hit) {
        InteractionResult interactionResult = super.use(blockstate, world, pos, entity, hand, hit);
        if(interactionResult == InteractionResult.FAIL){return interactionResult;}
        if(AdventureProcedure.pinConfig(world, entity)){
            OnBlockRightClickedProcedure.execute(world, pos, blockstate);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DefaultDigitalTriggerGateBlockEntity(pos, state);
    }
    public void setOutput(LevelAccessor level, BlockState state, BlockPos pos, boolean output){
        Level world = (Level) level;
        if (world.getBlockEntity(pos) instanceof DefaultDigitalTriggerGateBlockEntity be){
            if(be.OUTPUT != output){
                be.OUTPUT = output;
                be.setChanged();
                LittleTools.setBooleanProperty(world, pos, output, "visible_state",2);
                Direction direction = BlockFrameTransformUtils.getWorldDirectionFromLocalForward(state);
                this.sendRedstoneUpdateInDirection(level,state.getBlock(),pos,direction);
            }
        }
    }
    public boolean getOutput(LevelAccessor level, BlockPos pos){
        Level world = (Level) level;
        if (world.getBlockEntity(pos) instanceof DefaultDigitalGateBlockEntity be){
            return be.OUTPUT;
        }
        return false;
    }
    public void setUnlocked(LevelAccessor level, BlockPos pos, boolean unlocked){
        Level world = (Level) level;
        if (world.getBlockEntity(pos) instanceof DefaultDigitalTriggerGateBlockEntity be){
            if(be.UNLOCKED != unlocked){
                be.UNLOCKED = unlocked;
                be.setChanged();
                //world.setBlockEntity(be);
            }
        }
    }
    public boolean getUnlocked(LevelAccessor level, BlockPos pos){
        Level world = (Level) level;
        if (world.getBlockEntity(pos) instanceof DefaultDigitalTriggerGateBlockEntity be){
            return be.UNLOCKED;
        }
        return false;
    }
    @Override
    public int getRedstonePower(LevelAccessor world, BlockPos pos, ConnectionFace sourceFace) {
        ConnectionFace thisFace = getOutputConnectionFace(world, pos, sourceFace);
        if(thisFace.canConnect(sourceFace)){
            if(world.getBlockEntity(pos) instanceof DefaultDigitalTriggerGateBlockEntity be){
                return be.OUTPUT ? 15 : 0;
            }
        }
        return 0;
    }
}
