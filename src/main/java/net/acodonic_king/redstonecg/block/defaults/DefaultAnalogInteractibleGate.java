package net.acodonic_king.redstonecg.block.defaults;

import net.acodonic_king.redstonecg.block.entity.DefaultAnalogGateBlockEntity;
import net.acodonic_king.redstonecg.block.entity.DefaultDigitalGateBlockEntity;
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

public class DefaultAnalogInteractibleGate extends DefaultRedstoneActionGate implements EntityBlock {
    //public static final IntegerProperty POWER = IntegerProperty.create("power",0,15);
    public static final BooleanProperty VISIBLE_STATE = BooleanProperty.create("visible_state");
    public DefaultAnalogInteractibleGate(){super();}
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
        return new DefaultAnalogGateBlockEntity(pos, state);
    }
    public void setPower(LevelAccessor level, BlockState state, BlockPos pos, int power){
        power = Math.max(0, Math.min(power, 15));
        Level world = (Level) level;
        if (world.getBlockEntity(pos) instanceof DefaultAnalogGateBlockEntity be){
            if(be.POWER != power){
                be.POWER = power;
                be.setChanged();
                LittleTools.setBooleanProperty(world, pos, power > 0, "visible_state", 2);
                //if (world.isClientSide) return;
                //RedstonecgMod.LOGGER.debug("Client? {}",world.isClientSide);
                Direction direction = BlockFrameTransformUtils.getWorldDirectionFromLocalForward(state);
                this.sendRedstoneUpdateInDirection(level,state.getBlock(),pos,direction);
            }
        }
    }
    public int getPower(LevelAccessor level, BlockPos pos){
        Level world = (Level) level;
        if (world.getBlockEntity(pos) instanceof DefaultAnalogGateBlockEntity be){
            return be.POWER;
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
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos){
        if(world.getBlockEntity(pos) instanceof DefaultAnalogGateBlockEntity be){
            return be.POWER;
        }
        return 0;
    }
}
