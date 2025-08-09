
package net.acodonic_king.redstonecg.block.normal.hybrid;

import net.acodonic_king.redstonecg.block.defaults.DefaultRedstoneActionGate;
import net.acodonic_king.redstonecg.block.entity.DefaultAnalogGateBlockEntity;
import net.acodonic_king.redstonecg.block.entity.DefaultDigitalGateBlockEntity;
import net.acodonic_king.redstonecg.procedures.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import javax.annotation.Nullable;

public class OneWayThroughGateBlock extends DefaultRedstoneActionGate implements EntityBlock {
    public static final IntegerProperty CONNECTION = IntegerProperty.create("connection",0,5);
    //public static final IntegerProperty POWER = IntegerProperty.create("power",0,15);
    public static final BooleanProperty ENABLED = BooleanProperty.create("enabled");
    public static final BooleanProperty VISIBLE_STATE = BooleanProperty.create("visible_state");

    public OneWayThroughGateBlock() {
        super();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CONNECTION, VISIBLE_STATE, ENABLED);
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
        ConnectionFace connectionFaceB = BlockFrameTransformUtils.canConnectRedstoneTargetConnectionFace(world, pos, side);
        return CanConnectWallGateProcedure.To2ABGate(state, connectionFaceB);
    }

    @Override
    public void tick(BlockState blockstate, ServerLevel world, BlockPos pos, RandomSource random){
        LittleTools.setBooleanProperty(world, pos, false, "visible_state", 2);
        onRedstoneUpdate(world,blockstate,pos);
    }

    @Override
    public int onRedstoneUpdate(LevelAccessor world, BlockState blockState, BlockPos pos){
        Direction[] Sides = GetGateInputSidesProcedure.Get2ABGateForth(blockState);
        int SideCPower = GetRedstoneSignalProcedure.execute(world, pos, Direction.NORTH);
        int SideDPower = GetRedstoneSignalProcedure.execute(world, pos, Sides[0]);
        blockState = blockState.setValue(ENABLED, SideCPower > 0);
        if (world.getBlockEntity(pos) instanceof DefaultAnalogGateBlockEntity be){
            if(be.POWER != SideDPower){
                be.POWER = SideDPower;
                be.setChanged();
                blockState = blockState.setValue(VISIBLE_STATE, SideDPower > 0);
            }
        }
        world.setBlock(pos, blockState, 2);
        Direction direction = BlockFrameTransformUtils.getWorldDirectionFromLocal(blockState, Sides[1]);
        //RedstonecgMod.LOGGER.debug("in direction {} {} {}",Sides[1],direction,pos);
        this.sendRedstoneUpdateInDirection(world,blockState.getBlock(),pos,direction);
        if(SideCPower > 0){return SideDPower;}
        return 0;
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

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState blockState = super.getStateForPlacementOpposite(context);
        return blockState;
    }

    @Override
    public boolean isOutput(LevelAccessor world, BlockState blockState, BlockPos pos, Direction direction){
        Direction[] Sides = GetGateInputSidesProcedure.Get2ABGateForth(blockState);
        Direction dir = BlockFrameTransformUtils.getLocalDirectionFromWorld(blockState,direction);
        return Sides[1] == dir;
    }

    @Override
    public ConnectionFace getOutputConnectionFace(LevelAccessor world, BlockPos pos, ConnectionFace requesterFace) {
        BlockState thisState = world.getBlockState(pos);
        Direction[] Sides = GetGateInputSidesProcedure.Get2ABGateForth(thisState);
        return BlockFrameTransformUtils.getConnectionFace(thisState, Sides[1]);
    }

    @Override
    public int getRedstonePower(LevelAccessor world, BlockPos pos, ConnectionFace sourceFace) {
        if(world.getBlockState(pos).getValue(ENABLED)) {
            ConnectionFace thisFace = getOutputConnectionFace(world, pos, sourceFace);
            if (thisFace.canConnect(sourceFace)) {
                if (world.getBlockEntity(pos) instanceof DefaultAnalogGateBlockEntity be) {
                    return be.POWER;
                }
            }
        }
        return 0;
    }

}
