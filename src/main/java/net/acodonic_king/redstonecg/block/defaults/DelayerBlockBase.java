package net.acodonic_king.redstonecg.block.defaults;

import io.netty.buffer.Unpooled;
import net.acodonic_king.redstonecg.ModLoaderRider;
import net.acodonic_king.redstonecg.block.entity.DefaultAnalogIndicatorBlockEntity;
import net.acodonic_king.redstonecg.block.entity.DelayerBlockEntity;
import net.acodonic_king.redstonecg.block.gui.delayer.DelayerGUIMenu;
import net.acodonic_king.redstonecg.init.RedstonecgModItems;
import net.acodonic_king.redstonecg.init.RedstonecgModVersionRides;
import net.acodonic_king.redstonecg.network.RedstonecgModVariables;
import net.acodonic_king.redstonecg.procedures.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
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
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

public class DelayerBlockBase extends SuperBlock implements SimpleWaterloggedBlock, EntityBlock, FlooringInterface, RedstoneSignalInterface, MeasurementProvider, CustomBlockPrimarySecondaryDirectionInterface {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final IntegerProperty DELAY = IntegerProperty.create("delay",1,8);
    public static final BooleanProperty POWERED = BooleanProperty.create("powered");
    public static final BooleanProperty LOCKED = BooleanProperty.create("locked");

    public DelayerBlockBase() {
        super(RedstonecgModVersionRides.defaultGateProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false));
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED, DELAY, POWERED, LOCKED);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DelayerBlockEntity(pos, state);
    }

    @Override
    public int getRedstonePower(LevelAccessor world, BlockPos pos, ConnectionFace sourceFace) {
        ConnectionFace thisFace = getOutputConnectionFace(world, pos, sourceFace);
        if (thisFace.canConnect(sourceFace)) {
            if (world.getBlockEntity(pos) instanceof DelayerBlockEntity be) {
                return be.getSignal(world.getBlockState(pos).getValue(DELAY) - 1);
            }
        }
        return 0;
    }

    @Override
    public int getSignal(BlockState blockstate, BlockGetter blockAccess, BlockPos pos, Direction direction) {
        ConnectionFace connectionFaceB = new ConnectionFace(direction); //temporary
        LevelAccessor world = (LevelAccessor) blockAccess;
        ConnectionFace connectionFaceA = getOutputConnectionFace(world, pos, connectionFaceB);
        connectionFaceB = BlockFrameTransformUtils.getRequesterConnectionFace(world, pos.relative(direction.getOpposite()), connectionFaceA, direction.getOpposite());
        return getRedstonePower(world, pos, connectionFaceB);
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public void neighborChanged(BlockState blockstate, Level world, BlockPos pos, Block neighborBlock, BlockPos fromPos, boolean moving) {
        super.neighborChanged(blockstate, world, pos, neighborBlock, fromPos, moving);
        world.scheduleTick(pos, world.getBlockState(pos).getBlock(), 1);
    }

    public Pair<Direction,Direction> getPrimarySecondaryDirections(LevelAccessor world, BlockPos pos){
        Pair<Direction,Direction> dirs = BlockFrameTransformUtils.getDefaultPrimarySecondaryDirections();
        if(world.getBlockEntity(pos) instanceof DelayerBlockEntity be)
            dirs = be.getPrimarySecondaryDirections();
        return dirs;
    }

    public int[] getSidePower(BlockState blockState, LevelAccessor world, BlockPos pos, Pair<Direction,Direction> dirs){
        return new int[]{0,0};
    }

    @Override
    public void tick(BlockState blockState, ServerLevel world, BlockPos pos, RandomSource random) {
        super.tick(blockState, world, pos, random);
        Pair<Direction,Direction> dirs = getPrimarySecondaryDirections(world, pos);
        int[] power = getSidePower(blockState, world, pos, dirs);
        boolean locked = power[1] > 0;
        boolean change = locked != blockState.getValue(LOCKED);
        //blockState = blockState.setValue(LOCKED, locked);
        if(locked){
            if(change)
                world.setBlock(pos, blockState.setValue(LOCKED, true), 2);
            //world.scheduleTick(pos, blockState.getBlock(), 1);
            return;
        }
        int signal = 0;
        boolean nextTick = false;
        if(world.getBlockEntity(pos) instanceof DelayerBlockEntity be) {
            int d = blockState.getValue(DELAY) - 1;
            signal = be.signalMemory(power[0], d);
            nextTick = !be.singleSignal(d);
            be.setChanged();
            world.setBlockEntity(be);
            change = true;
        }
        locked = signal > 0;
        change = change || (locked != blockState.getValue(POWERED));
        if(change){
            blockState = blockState.setValue(LOCKED, false).setValue(POWERED, locked);
            world.setBlock(pos, blockState, 2);
            world.sendBlockUpdated(pos, blockState, blockState, 3);
        }
        Direction direction = BlockFrameTransformUtils.getWorldDirectionFromLocalForward(dirs);
        sendRedstoneUpdateInDirection(world, blockState.getBlock(), pos, direction, 0);
        if(nextTick)
            world.scheduleTick(pos, blockState.getBlock(), 1);
    }

    public void sendRedstoneUpdateInDirection(LevelAccessor level, Block thisBlock, BlockPos thisPos, Direction direction, int recursion){
        Level world = (Level) level;
        if(direction == null) {
            world.blockUpdated(thisPos, thisBlock);
            return;
        }
        BlockPos neighborPos = thisPos.relative(direction);
        //RedstonecgMod.LOGGER.debug("Sending update to {}",neighborPos);
        BlockState bs = world.getBlockState(neighborPos);
        Block block = bs.getBlock();
        if (block instanceof DefaultConnectableGate nb){
            if(nb.isOutput(world, bs, neighborPos, direction.getOpposite())){return;}
        }
        if (block instanceof DefaultRedstoneActionGate nb){
            nb.onRedstoneUpdate(world, bs, neighborPos, thisPos, recursion);
        } else {
            //block.neighborChanged(bs,world,neighborPos,thisBlock,thisPos,false);
            world.neighborChanged(neighborPos,thisBlock,thisPos);
        }
    }

    @Override
    public ConnectionFace getOutputConnectionFace(LevelAccessor world, BlockPos pos, ConnectionFace requesterFace) {
        return BlockFrameTransformUtils.getConnectionFace(getPrimarySecondaryDirections(world, pos), Direction.NORTH);
    }

    @Override
    public ConnectionFace getAnyConnectionFace(LevelAccessor world, BlockPos pos, ConnectionFace requesterFace) {
        Pair<Direction, Direction> dirs = getPrimarySecondaryDirections(world, pos);
        Direction localDirection = BlockFrameTransformUtils.getLocalDirectionFromWorld(dirs,requesterFace.FACE.getOpposite());
        return BlockFrameTransformUtils.getConnectionFace(dirs,localDirection);
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
        return 0;
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (world.getBlockEntity(pos) instanceof DelayerBlockEntity be) {
            return switch (be.getFacingIndex()) {
                case 0 -> box(0, 0, 0, 16, 2, 16);
                case 1 -> box(0, 0, 0, 16, 16, 2);
                case 2 -> box(14, 0, 0, 16, 16, 16);
                case 3 -> box(0, 0, 14, 16, 16, 16);
                case 4 -> box(0, 0, 0, 2, 16, 16);
                default -> box(0, 14, 0, 16, 16, 16);
            };
        }
        return box(0, 0, 0, 16, 2, 16);
    }

    @Override
    public int floorIt(Level level, BlockPos pos) {
        if(level.getBlockEntity(pos) instanceof DelayerBlockEntity be){
            Direction facing = be.getFacing();
            if(facing.getAxis() != Direction.Axis.Y){
                be.setRotation(facing);
            }
            be.setFacing(Direction.DOWN);
            be.setChanged();
            level.sendBlockUpdated(pos, be.getBlockState(), be.getBlockState(), 2);
            return 1;
        }
        return 0;
    }

    @Override
    public InteractionResult use(BlockState blockstate, Level world, BlockPos pos, Player entity, InteractionHand hand, BlockHitResult hit) {
        InteractionResult interactionResult = super.use(blockstate, world, pos, entity, hand, hit);
        if(interactionResult == InteractionResult.FAIL){return interactionResult;}
        ItemStack itemStack = entity.getItemInHand(hand);
        if(!itemStack.isEmpty()){
            if(itemStack.is(RedstonecgModItems.ROTATION_BRACKET.get())){return InteractionResult.FAIL;}
            if(itemStack.is(blockstate.getBlock().asItem())){
                if(!AdventureProcedure.valueConfig(world, entity))
                    return InteractionResult.FAIL;
                int delay = blockstate.getValue(DELAY) + 1;
                if(delay > 8)
                    delay = 1;
                world.setBlock(pos, blockstate.setValue(DELAY, delay), 2);
                return InteractionResult.SUCCESS;
            }
        }
        /*if(entity.isCrouching()){
            if(AdventureProcedure.pinConfig(world, entity)){
                OnBlockRightClickedProcedure.execute(world, pos, blockstate);
                return InteractionResult.SUCCESS;
            }
        }*/
        if(AdventureProcedure.gateGUI(world, entity)){
            if (entity instanceof ServerPlayer player) {
                ModLoaderRider.openMenu(player, new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return Component.literal("Delayer");
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
                        return new DelayerGUIMenu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(pos));
                    }
                }, pos);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.FAIL;
    }

    @Override
    public boolean canSurvive(BlockState blockstate, LevelReader worldIn, BlockPos pos) {
        if(RedstonecgModVariables.MapVariables.get((LevelAccessor) worldIn).canSurviveAnyCase){return true;}
        if (worldIn instanceof LevelAccessor world) {
            if (world.getBlockEntity(pos) instanceof DefaultAnalogIndicatorBlockEntity be) {
                return GateBlockValidPlacementConditionProcedure.execute(world, pos, be.getFacing());
            }
        }
        return super.canSurvive(blockstate, worldIn, pos);
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
            if (level.getBlockEntity(pos) instanceof DelayerBlockEntity be) {
                BlockPlaceContext context = LocalThreadValueHolders.BlockPlaceContextHolder.get();
                //RedstonecgMod.LOGGER.debug(context);
                if(context == null){return;}
                Direction clickedFace = context.getClickedFace().getOpposite();
                Direction lookDirection = context.getHorizontalDirection();

                /*be.FACING = clickedFace;
                if(clickedFace == Direction.UP){
                    be.ROTATION = switch (lookDirection){
                        case NORTH, SOUTH -> lookDirection.getOpposite();
                        default -> lookDirection;
                    };
                } else if (clickedFace == Direction.DOWN){
                    be.ROTATION = lookDirection;
                }
                be.modelUpdate();*/

                be.setFacing(clickedFace);
                if(clickedFace == Direction.UP){
                    be.setRotation(switch (lookDirection){
                        case NORTH, SOUTH -> lookDirection.getOpposite();
                        default -> lookDirection;
                    });
                } else if (clickedFace == Direction.DOWN){
                    be.setRotation(lookDirection);
                }

                be.setChanged();
                level.sendBlockUpdated(pos, state, state, 2);
            }
            LocalThreadValueHolders.BlockPlaceContextHolder.clear();
        }
    }

    @Override
    public String getMeasurement(LevelAccessor world, BlockState blockState, BlockPos pos) {
        if(world.getBlockEntity(pos) instanceof DelayerBlockEntity blockEntity){
            return String.format("%1d",blockEntity.getSignal(blockState.getValue(DELAY) - 1));
        }
        return "";
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos){
        if(world.getBlockEntity(pos) instanceof DelayerBlockEntity blockEntity){
            return blockEntity.getSignal(state.getValue(DELAY) - 1);
        }
        return 0;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }
}
