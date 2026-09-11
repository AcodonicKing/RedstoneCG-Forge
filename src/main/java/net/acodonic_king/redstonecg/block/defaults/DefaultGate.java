package net.acodonic_king.redstonecg.block.defaults;

import net.acodonic_king.redstonecg.init.RedstonecgModItems;
import net.acodonic_king.redstonecg.init.RedstonecgModVersionRides;
import net.acodonic_king.redstonecg.network.RedstonecgModVariables;
import net.acodonic_king.redstonecg.procedures.GateBlockValidPlacementConditionProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.tuple.Pair;

public class DefaultGate extends SuperBlock implements SimpleWaterloggedBlock, FlooringInterface, PrimarySecondaryDirectionInterface, PrimarySecondaryDirectionBlockInterface {
    public static final DirectionProperty FACING = DirectionProperty.create("facing", new Direction[]{Direction.DOWN,Direction.NORTH,Direction.EAST,Direction.SOUTH,Direction.WEST,Direction.UP});
    public static final DirectionProperty ROTATION = DirectionProperty.create("rotation", Direction.Plane.HORIZONTAL);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public DefaultGate() {
        super(RedstonecgModVersionRides.defaultGateProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return state.getFluidState().isEmpty();
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return 0;
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case DOWN -> box(0, 0, 0, 16, 2, 16);
            case NORTH -> box(0, 0, 0, 16, 16, 2);
            case EAST -> box(14, 0, 0, 16, 16, 16);
            case SOUTH -> box(0, 0, 14, 16, 16, 16);
            case WEST -> box(0, 0, 0, 2, 16, 16);
            case UP -> box(0, 14, 0, 16, 16, 16);
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ROTATION, WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return getStateForPlacementDirect(context);
    }

    public BlockState getStateForPlacementDirect(BlockPlaceContext context) {
        Direction clickedFace = context.getClickedFace().getOpposite();
        Direction lookDirection = context.getHorizontalDirection();
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if(!GateBlockValidPlacementConditionProcedure.execute(world, pos, clickedFace)){return null;}
        boolean flag = context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER;
        BlockState blockState = this.defaultBlockState().setValue(FACING, clickedFace).setValue(WATERLOGGED, flag);
        //RedstonecgMod.LOGGER.debug("{} {}",clickedFace,context.getHorizontalDirection());
        if(clickedFace == Direction.UP){
            blockState = switch (lookDirection){
                case NORTH, SOUTH -> blockState.setValue(ROTATION, lookDirection.getOpposite());
                default -> blockState.setValue(ROTATION, lookDirection);
            };
        } else if (clickedFace == Direction.DOWN){
            blockState = blockState.setValue(ROTATION, lookDirection);
        } else {
            blockState = blockState.setValue(ROTATION, Direction.NORTH);
        }
        //RedstonecgMod.LOGGER.debug(blockState);
        return blockState;
    }

    public BlockState getStateForPlacementOpposite(BlockPlaceContext context) {
        Direction clickedFace = context.getClickedFace().getOpposite();
        Direction lookDirection = context.getHorizontalDirection();
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if(!GateBlockValidPlacementConditionProcedure.execute(world, pos, clickedFace)){return null;}
        boolean flag = context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER;
        BlockState blockState = this.defaultBlockState().setValue(FACING, clickedFace).setValue(WATERLOGGED, flag);
        //RedstonecgMod.LOGGER.debug("{} {}",clickedFace,context.getHorizontalDirection());
        if(clickedFace == Direction.UP){
            blockState = switch (lookDirection){
                case NORTH, SOUTH -> blockState.setValue(ROTATION, lookDirection);
                default -> blockState.setValue(ROTATION, lookDirection.getOpposite());
            };
        } else if (clickedFace == Direction.DOWN){
            blockState = blockState.setValue(ROTATION, lookDirection.getOpposite());
        } else {
            blockState = blockState.setValue(ROTATION, Direction.SOUTH);
        }
        //RedstonecgMod.LOGGER.debug(blockState);
        return blockState;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        Direction facing = state.getValue(FACING);
        Direction rotation = state.getValue(ROTATION);
        if (facing.getAxis() == Direction.Axis.Y){
            rotation = rot.rotate(rotation);
        } else {
            facing = rot.rotate(facing);
        }
        return state.setValue(FACING, facing).setValue(ROTATION, rotation);
    }

    /*@Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        Direction facing = state.getValue(FACING);
        Direction rotation = state.getValue(ROTATION);
        if (facing.getAxis() == Direction.Axis.Y){
            rotation = mirrorIn.mirror(rotation);
        } else {
            facing = mirrorIn.mirror(facing);
        }
        return state.setValue(FACING, facing).setValue(ROTATION, rotation);
    }*/

    @Override
    public boolean canSurvive(BlockState blockstate, LevelReader worldIn, BlockPos pos) {
        if(RedstonecgModVariables.MapVariables.get((LevelAccessor) worldIn).canSurviveAnyCase){return true;}
        if (worldIn instanceof LevelAccessor world) {
            return GateBlockValidPlacementConditionProcedure.execute(world, pos, blockstate.getValue(FACING));
        }
        return super.canSurvive(blockstate, worldIn, pos);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
        if (state.getValue(WATERLOGGED)) {
            world.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }
        return !state.canSurvive(world, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, world, currentPos, facingPos);
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public void neighborChanged(BlockState blockstate, Level world, BlockPos pos, Block neighborBlock, BlockPos fromPos, boolean moving) {
        super.neighborChanged(blockstate, world, pos, neighborBlock, fromPos, moving);
        //RedstonecgMod.LOGGER.debug("Update at {}, params {} {} {} {} {} {}",pos, blockstate, world, pos, neighborBlock, fromPos, moving);
    }

    @Override
    public InteractionResult use(BlockState blockstate, Level world, BlockPos pos, Player entity, InteractionHand hand, BlockHitResult hit) {
        super.use(blockstate, world, pos, entity, hand, hit);
        ItemStack itemStack = entity.getItemInHand(hand);
        //world.scheduleTick(pos, blockstate.getBlock(), 1);
        if(!itemStack.isEmpty()){
            if(itemStack.is(RedstonecgModItems.ROTATION_BRACKET.get())){return InteractionResult.FAIL;}
        }
        return InteractionResult.SUCCESS;
    }


    @Override
    public int floorIt(Level level, BlockPos pos) {
        BlockState blockState = level.getBlockState(pos);
        Direction facing = blockState.getValue(DefaultGate.FACING);
        if(facing.getAxis() != Direction.Axis.Y){
            blockState = blockState.setValue(DefaultGate.ROTATION, facing);
        }
        blockState = blockState.setValue(DefaultGate.FACING, Direction.DOWN);
        level.setBlock(pos,blockState,34);
        //level.sendBlockUpdated(pos, blockState, blockState, 2);
        return 1;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public Pair<Direction, Direction> getPrimarySecondaryDirections(LevelAccessor world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        return Pair.of(getPrimaryDirection(blockState), getSecondaryDirection(blockState));
    }

    @Override
    public Direction getPrimaryDirection(LevelAccessor world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        return blockState.getValue(ROTATION);
    }

    @Override
    public Direction getSecondaryDirection(LevelAccessor world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        return blockState.getValue(FACING);
    }

    @Override
    public DirectionProperty primaryDirectionProperty() {
        return ROTATION;
    }

    @Override
    public DirectionProperty secondaryDirectionProperty() {
        return FACING;
    }
}
