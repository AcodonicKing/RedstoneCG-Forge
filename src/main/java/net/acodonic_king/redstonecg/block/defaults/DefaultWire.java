package net.acodonic_king.redstonecg.block.defaults;

import net.acodonic_king.redstonecg.block.entity.RedCuWireBlockEntity;
import net.acodonic_king.redstonecg.init.RedstonecgModItems;
import net.acodonic_king.redstonecg.init.RedstonecgModVersionRides;
import net.acodonic_king.redstonecg.network.RedstonecgModVariables;
import net.acodonic_king.redstonecg.procedures.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.level.block.entity.BlockEntity;
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

public class DefaultWire extends SuperBlock implements EntityBlock, WireInterface, MeasurementProvider, FlooringInterface, RedstoneSignalInterface {
    public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.DOWN,Direction.NORTH,Direction.EAST,Direction.SOUTH,Direction.WEST,Direction.UP);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    //public static final BooleanProperty POWERCHANGE = BooleanProperty.create("powerchange");
    public DefaultWire(){
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
            case DOWN -> box(0, 0, 0, 16, 3, 16);
            case NORTH -> box(0, 0, 0, 16, 16, 3);
            case EAST -> box(13, 0, 0, 16, 16, 16);
            case SOUTH -> box(0, 0, 13, 16, 16, 16);
            case WEST -> box(0, 0, 0, 3, 16, 16);
            case UP -> box(0, 13, 0, 16, 16, 16);
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction clickedFace = context.getClickedFace().getOpposite();
        //Direction lookDirection = context.getHorizontalDirection();
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if(!GateBlockValidPlacementConditionProcedure.execute(world, pos, clickedFace)){return null;}
        boolean flag = context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER;
        world.scheduleTick(pos,this,1);
        return this.defaultBlockState().setValue(FACING, clickedFace).setValue(WATERLOGGED, flag);
    }

    /*public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }*/

    @Override
    public boolean canSurvive(BlockState blockstate, LevelReader worldIn, BlockPos pos) {
        if(RedstonecgModVariables.MapVariables.get((LevelAccessor) worldIn).canSurviveAnyCase){return true;}
        if (worldIn instanceof LevelAccessor world) {
            return GateBlockValidPlacementConditionProcedure.execute(world, pos, LittleTools.getDirection(blockstate));
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
    public boolean triggerEvent(BlockState state, Level world, BlockPos pos, int eventID, int eventParam) {
        super.triggerEvent(state, world, pos, eventID, eventParam);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity == null ? false : blockEntity.triggerEvent(eventID, eventParam);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RedCuWireBlockEntity(pos, state);
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
        ConnectionFace connectionFaceB = BlockFrameTransformUtils.canConnectRedstoneTargetConnectionFace(world, pos, side);
        ConnectionFacePrimaryRange connectionFaceRangeA = new ConnectionFacePrimaryRange(state.getValue(DefaultWire.FACING));
        return connectionFaceRangeA.canConnect(connectionFaceB);
    }

    @Override
    public void onTick(LevelAccessor world, BlockPos pos){
        onTick(world, pos, 0);
    }

    @Override
    public void onTick(LevelAccessor world, BlockPos pos, int power){}

    @Override
    public void tick(BlockState blockstate, ServerLevel world, BlockPos pos, RandomSource random) {
        super.tick(blockstate, world, pos, random);
        this.onTick(world, pos);
    }

    @Override
    public void neighborChanged(BlockState blockstate, Level world, BlockPos pos, Block neighborBlock, BlockPos fromPos, boolean moving) {
        super.neighborChanged(blockstate, world, pos, neighborBlock, fromPos, moving);
        this.onTick(world, pos);
    }

    @Override
    public InteractionResult use(BlockState blockstate, Level world, BlockPos pos, Player entity, InteractionHand hand, BlockHitResult hit) {
        super.use(blockstate, world, pos, entity, hand, hit);
        ItemStack itemStack = entity.getItemInHand(hand);
        if(!itemStack.isEmpty()){
            if(itemStack.is(RedstonecgModItems.ROTATION_BRACKET.get())){return InteractionResult.FAIL;}
        }
        if(AdventureProcedure.pinConfig(world, entity)){
            OnBlockRightClickedProcedure.execute(world, pos, blockstate);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    @Override
    public int getWirePower(LevelAccessor world, BlockPos pos, ConnectionFace requesterFace) {
        return 0;
    }

    @Override
    public String getMeasurement(LevelAccessor world, BlockState blockState, BlockPos pos){
        if(world.getBlockEntity(pos) instanceof RedCuWireBlockEntity wireEntity){
            return String.format("%1f",wireEntity.POWER/16.0);
        }
        return "";
    }

    @Override
    public void rotationBracket(LevelAccessor world, BlockPos pos, boolean clockwise){}

    @Override
    public int floorIt(Level level, BlockPos pos) {
        BlockState blockState = level.getBlockState(pos);
        blockState = blockState.setValue(FACING, Direction.DOWN);
        level.setBlock(pos, blockState, 34);
        //level.sendBlockUpdated(pos, blockState, blockState, 2);
        return 1;
    }

    @Override
    public int getRedstonePower(LevelAccessor world, BlockPos pos, ConnectionFace requesterFace) {
        return getWirePower(world, pos, requesterFace) >> 4;
    }

    @Override
    public ConnectionFace getOutputConnectionFace(LevelAccessor world, BlockPos pos, ConnectionFace requesterFace) {
        return requesterFace.getConnectable();
    }

    @Override
    public ConnectionFace getAnyConnectionFace(LevelAccessor world, BlockPos pos, ConnectionFace requesterFace) {
        BlockState blockState = world.getBlockState(pos);
        Direction localDirection = BlockFrameTransformUtils.getLocalDirectionFromWorld(blockState,requesterFace.FACE.getOpposite());
        return BlockFrameTransformUtils.getConnectionFace(blockState,localDirection);
    }

    @Override
    public int getSignal(BlockState blockstate, BlockGetter blockAccess, BlockPos pos, Direction direction) {
        ConnectionFace connectionFaceB = new ConnectionFace(direction); //temporary
        LevelAccessor world = (LevelAccessor) blockAccess;
        ConnectionFace connectionFaceA = getOutputConnectionFace(world, pos, connectionFaceB);
        connectionFaceB = BlockFrameTransformUtils.getRequesterConnectionFace(world, pos.relative(direction.getOpposite()), connectionFaceA, direction.getOpposite());
        return getRedstonePower(world, pos, connectionFaceB);
        /*if (connectionFaceA.canConnect(connectionFaceB)) {

        }
        return 0;*/
    }
}
