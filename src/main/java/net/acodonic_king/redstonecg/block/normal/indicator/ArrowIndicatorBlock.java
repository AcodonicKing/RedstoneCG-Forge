package net.acodonic_king.redstonecg.block.normal.indicator;

import io.netty.buffer.Unpooled;
import net.acodonic_king.redstonecg.ModLoaderRider;
import net.acodonic_king.redstonecg.block.defaults.FlooringInterface;
import net.acodonic_king.redstonecg.block.defaults.PinMarkConnectionInterface;
import net.acodonic_king.redstonecg.block.defaults.RedstoneSignalInterface;
import net.acodonic_king.redstonecg.block.defaults.SuperBlock;
import net.acodonic_king.redstonecg.block.entity.ArrowIndicatorBlockEntity;
import net.acodonic_king.redstonecg.block.entity.DefaultAnalogIndicatorBlockEntity;
import net.acodonic_king.redstonecg.block.gui.analog_source.AnalogSourceGUIMenu;
import net.acodonic_king.redstonecg.block.gui.arrow_indicator.ArrowIndicatorGUIMenu;
import net.acodonic_king.redstonecg.init.RedstonecgModItems;
import net.acodonic_king.redstonecg.init.RedstonecgModVersionRides;
import net.acodonic_king.redstonecg.network.MessengerBlockEntityPigeon;
import net.acodonic_king.redstonecg.network.RedstonecgModVariables;
import net.acodonic_king.redstonecg.procedures.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
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
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class ArrowIndicatorBlock extends SuperBlock implements SimpleWaterloggedBlock, EntityBlock, FlooringInterface, RedstoneSignalInterface, PinMarkConnectionInterface {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final IntegerProperty CONNECTION = IntegerProperty.create("connection",0,15);
    //public static final IntegerProperty MODEL = IntegerProperty.create("model",0,11);
    public ArrowIndicatorBlock(){
        super(RedstonecgModVersionRides.defaultGateProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false));
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return state.getFluidState().isEmpty();
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter world, BlockPos pos) {
        //if(LittleTools.getIntegerProperty(state,"power") > 0){return 5;}
        return 0;
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (world.getBlockEntity(pos) instanceof ArrowIndicatorBlockEntity be) {
            return switch (be.FACING) {
                case DOWN -> box(0, 0, 0, 16, 2, 16);
                case NORTH -> box(0, 0, 0, 16, 16, 2);
                case EAST -> box(14, 0, 0, 16, 16, 16);
                case SOUTH -> box(0, 0, 14, 16, 16, 16);
                case WEST -> box(0, 0, 0, 2, 16, 16);
                case UP -> box(0, 14, 0, 16, 16, 16);
            };
        }
        return box(0, 0, 0, 16, 2, 16);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, CONNECTION);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        LocalThreadValueHolders.BlockPlaceContextHolder.set(context);
        Direction clickedFace = context.getClickedFace().getOpposite();
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if(!GateBlockValidPlacementConditionProcedure.execute(world, pos, clickedFace)){return null;}
        boolean flag = context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER;
        world.scheduleTick(pos,this,1);
        return this.defaultBlockState().setValue(WATERLOGGED, flag);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.isClientSide) {
            if (level.getBlockEntity(pos) instanceof ArrowIndicatorBlockEntity be) {
                BlockPlaceContext context = LocalThreadValueHolders.BlockPlaceContextHolder.get();
                //RedstonecgMod.LOGGER.debug(context);
                if(context == null){return;}
                Direction clickedFace = context.getClickedFace().getOpposite();
                Direction lookDirection = context.getHorizontalDirection();
                be.FACING = clickedFace;
                if(clickedFace == Direction.UP){
                    be.ROTATION = switch (lookDirection){
                        case NORTH, SOUTH -> lookDirection.getOpposite();
                        default -> lookDirection;
                    };
                } else if (clickedFace == Direction.DOWN){
                    be.ROTATION = lookDirection;
                }
                be.modelUpdate();
                be.setChanged();
                this.redstoneUpdate(level, pos);
            }
            LocalThreadValueHolders.BlockPlaceContextHolder.clear();
        }
    }
    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (level.isClientSide) return;
        if (!stack.hasTag()) return;
        CompoundTag tag = stack.getTag();
        if (!tag.contains("BlockParameterSet")) return;
        tag = tag.getCompound("BlockParameterSet");
        if (level.getBlockEntity(pos) instanceof ArrowIndicatorBlockEntity be){
            if (tag.contains("connection"))
                state = state.setValue(CONNECTION, tag.getInt("connection"));
            be.setParameterSet(tag);
            be.setChanged();
            level.sendBlockUpdated(pos, state, state, 3);
            level.scheduleTick(pos, state.getBlock(), 1);
        }
    }
    @Override
    public boolean canSurvive(BlockState blockstate, LevelReader worldIn, BlockPos pos) {
        if(RedstonecgModVariables.MapVariables.get((LevelAccessor) worldIn).canSurviveAnyCase){return true;}
        if (worldIn instanceof LevelAccessor world) {
            if (world.getBlockEntity(pos) instanceof ArrowIndicatorBlockEntity be) {
                return GateBlockValidPlacementConditionProcedure.execute(world, pos, be.FACING);
            }
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
    public boolean canConnectRedstone(BlockState blockState, BlockGetter world, BlockPos pos, Direction side) {
        if (world.getBlockEntity(pos) instanceof ArrowIndicatorBlockEntity be) {
            ConnectionFace connectionFaceB = BlockFrameTransformUtils.canConnectRedstoneTargetConnectionFace(world, pos, side);
            int connection = blockState.getValue(CONNECTION);
            connection ++;
            return CanConnectWallGateProcedure.execute(be.ROTATION, be.FACING, connection, connectionFaceB);
        }
        return false;
    }

    public void redstoneUpdate(LevelAccessor world, BlockPos pos){
        if (world.isClientSide())
            return;
        if (world.getBlockEntity(pos) instanceof ArrowIndicatorBlockEntity be) {
            //RedstonecgMod.LOGGER.debug("{} {} {} {} {}",be.FACING,be.ROTATION,be.facing,be.rotation,be.facingMode);
            BlockState ThisBlock = (world.getBlockState(pos));
            int power = 0;
            for (Direction side : GetGateInputSidesProcedure.Get1_4GateForth(ThisBlock)) {
                ConnectionFace connectionFaceA = BlockFrameTransformUtils.getConnectionFace(be.ROTATION,be.FACING,side);
                power = Math.max(power, GetRedstoneSignalProcedure.executeWire(world, pos, connectionFaceA));
            }
            if(be.BASE_READ){
                ConnectionFace connectionFaceA = BlockFrameTransformUtils.getConnectionFace(be.ROTATION,be.FACING,Direction.DOWN);
                connectionFaceA.CHANNEL = 4;
                power = Math.max(power, GetRedstoneSignalProcedure.executeWire(world, pos, connectionFaceA));
            }
            be.setRedCuSignal(power);
            ((Level)(world)).sendBlockUpdated(pos, ThisBlock, ThisBlock, 2);
            //LittleTools.setIntegerProperty(world, pos, power, "power", 2);
        }
    }

    @Override
    public void tick(BlockState blockstate, ServerLevel world, BlockPos pos, RandomSource random) {
        super.tick(blockstate, world, pos, random);
        this.redstoneUpdate(world, pos);
    }

    @Override
    public void neighborChanged(BlockState blockstate, Level world, BlockPos pos, Block neighborBlock, BlockPos fromPos, boolean moving) {
        super.neighborChanged(blockstate, world, pos, neighborBlock, fromPos, moving);
        this.redstoneUpdate(world, pos);
    }

    @Override
    public InteractionResult use(BlockState blockstate, Level world, BlockPos pos, Player entity, InteractionHand hand, BlockHitResult hit) {
        InteractionResult interactionResult = super.use(blockstate, world, pos, entity, hand, hit);
        if(interactionResult == InteractionResult.FAIL){return interactionResult;}
        ItemStack itemStack = entity.getItemInHand(hand);
        if(!itemStack.isEmpty()){
            if(itemStack.is(RedstonecgModItems.ROTATION_BRACKET.get())){return InteractionResult.FAIL;}
            if(itemStack.is(RedstonecgModItems.ARROW_INDICATOR.get())){
                if(world.getBlockEntity(pos) instanceof ArrowIndicatorBlockEntity be) {
                    CompoundTag tag = be.getParameterSet();
                    tag.putInt("connection", blockstate.getValue(CONNECTION));
                    itemStack.getOrCreateTag().put("BlockParameterSet", tag);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        /*if(entity.isCrouching()){
            if(AdventureProcedure.pinConfig(world, entity)){
                OnBlockRightClickedProcedure.execute(world, pos, blockstate);
                return InteractionResult.SUCCESS;
            }
        }*/
        if(AdventureProcedure.gateGUI(world, entity))
            if (entity instanceof ServerPlayer player) {
                ModLoaderRider.openMenu(player, new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return Component.literal("Arrow Indicator");
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
                        return new ArrowIndicatorGUIMenu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(pos));
                    }
                }, pos);
            }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ArrowIndicatorBlockEntity(pos, state);
    }

    public Direction getFacing(LevelAccessor level, BlockPos pos){
        if(level.getBlockEntity(pos) instanceof ArrowIndicatorBlockEntity be){
            return be.FACING;
        }
        return null;
    }

    public Direction getRotation(LevelAccessor level, BlockPos pos){
        if(level.getBlockEntity(pos) instanceof ArrowIndicatorBlockEntity be){
            return be.ROTATION;
        }
        return null;
    }

    @Override
    public int floorIt(Level level, BlockPos pos) {
        if(level.getBlockEntity(pos) instanceof ArrowIndicatorBlockEntity be){
            Direction facing = be.FACING;
            if(facing.getAxis() != Direction.Axis.Y){
                be.ROTATION = facing;
            }
            be.FACING = Direction.DOWN;
            be.setChanged();
            level.sendBlockUpdated(pos, be.getBlockState(), be.getBlockState(), 2);
            return 1;
        }
        return 0;
    }

    @Override
    public int getRedstonePower(LevelAccessor world, BlockPos pos, ConnectionFace requesterFace) {
        return 0;
    }

    @Override
    public ConnectionFace getOutputConnectionFace(LevelAccessor world, BlockPos pos, ConnectionFace requesterFace) {
        ConnectionFace connectionFace = requesterFace.getConnectable();
        connectionFace.CHANNEL = 5;
        return connectionFace;
    }

    @Override
    public ConnectionFace getAnyConnectionFace(LevelAccessor world, BlockPos pos, ConnectionFace requesterFace) {
        BlockState blockState = world.getBlockState(pos);
        Direction localDirection = BlockFrameTransformUtils.getLocalDirectionFromWorld(blockState,requesterFace.FACE.getOpposite());
        return BlockFrameTransformUtils.getConnectionFace(blockState,localDirection);
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        int connection = state.getValue(CONNECTION);
        connection++;
        int c2 = (connection & 2) << 2;
        int c8 = (connection & 8) >> 2;
        connection &= 0b0101;
        connection |= c2 | c8;
        return state.setValue(CONNECTION, connection);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return false;
    }

    @Override
    public int getConnection(BlockState bs) {
        return bs.getValue(CONNECTION);
    }

    @Override
    public int connectionFilter(int connection) {
        return CanConnectWallGateProcedure.To1_4GateConnectionFilter(connection);
    }
}
