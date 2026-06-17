package net.acodonic_king.redstonecg.block.normal.interaction;

import io.netty.buffer.Unpooled;
import net.acodonic_king.redstonecg.ModLoaderRider;
import net.acodonic_king.redstonecg.block.defaults.RedstoneSignalInterface;
import net.acodonic_king.redstonecg.block.defaults.RotationBracketInterface;
import net.acodonic_king.redstonecg.block.defaults.SuperBlock;
import net.acodonic_king.redstonecg.block.entity.ControlPanelBlockEntity;
import net.acodonic_king.redstonecg.block.gui.control_panel.ControlPanelGUIMenu;
import net.acodonic_king.redstonecg.init.RedstonecgModItems;
import net.acodonic_king.redstonecg.init.RedstonecgModVersionRides;
import net.acodonic_king.redstonecg.procedures.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
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
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

import static net.acodonic_king.redstonecg.procedures.RightAngleRotation.*;

public class ControlPanelBlock extends SuperBlock implements SimpleWaterloggedBlock, EntityBlock, RotationBracketInterface, RedstoneSignalInterface {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final IntegerProperty ORIENTATION = IntegerProperty.create("orientation", 0, 41);

    public ControlPanelBlock(){
        super(RedstonecgModVersionRides.defaultGateProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        //return getShape(state, world, pos, context, 4);
        int orientation = state.getValue(ORIENTATION);
        VoxelShapeBuilder builder = getAllInteractionBox(orientation, 0);
        applyBase(orientation, builder);
        orientationTransform(orientation, builder);
        return builder.build();
    }

    public VoxelShapeBuilder getInteractionBox(int orientation, int slot, float add){
        VoxelShapeBuilder.BoxOperation box = new VoxelShapeBuilder.BoxOperation();
        if (orientation < 6)
            box.start(4-add, 10-add, 4-add).end(12+add, 14+add, 12+add);
        else if (orientation < 30)
            box.start(4-add, 11-add, 4-add).end(12+add, 16+add, 12+add);
        else
            box.start(4-add, 4-add, 4-add).end(12+add, 12+add, 12+add);
        return new VoxelShapeBuilder().first(box);
    }

    public VoxelShapeBuilder getAllInteractionBox(int orientation, float add){
        return getInteractionBox(orientation, 0, add);
    }

    public VoxelShapeBuilder applyBase(int orientation, VoxelShapeBuilder builder){
        if (orientation < 6)
            builder.OR(0, 0, 0, 16, 10, 16);
        else if (orientation < 30)
            builder.OR(0, 0, 0, 16, 11, 16);
        else {
            if (orientation > 33 && orientation < 38)
                builder.OR(0, 0, 0, 2, 16, 16);
            else
                builder.OR(0, 0, 0, 16, 2, 16);
            builder.OR(0, 0, 0, 16, 16, 2);
        }
        return builder;
    }

    public VoxelShapeBuilder orientationTransform(int orientation, VoxelShapeBuilder builder){
        if (orientation < 6) {
            if(orientation == 5)
                builder.rotateX(CW2, 8, 8, 8);
            else if(orientation > 0) {
                builder.rotateX(CW1, 8, 8, 8);
                builder.rotateY(CCW_MAP[orientation - 1], 8, 8, 8);
            }
        } else if (orientation < 30) {
            orientation -= 6;
            builder.rotateY(CCW_MAP[orientation & 3], 8, 8, 8);
            orientation >>= 2;
            if(orientation == 5)
                builder.rotateX(CW2, 8, 8, 8);
            else if(orientation > 0) {
                builder.rotateX(CW1, 8, 8, 8);
                builder.rotateY(CCW_MAP[orientation - 1], 8, 8, 8);
            }
        } else {
            if(orientation > 37)
                builder.rotateX(CW1, 8, 8, 8);
            builder.rotateY(CCW_MAP[(orientation - 30) & 3], 8, 8, 8);
        }
        return builder;
    }

    public VoxelShape getFinalInteractionBox(int orientation, int slot, float add){
        return orientationTransform(orientation, getInteractionBox(orientation, slot, add)).build();
    }

    public int getModel(int orientation){
        if(orientation < 6)
            return 0;
        if(orientation < 30)
            return 1;
        return 2;
    }
    public int getModel(BlockState blockState){
        return getModel(blockState.getValue(ORIENTATION));
    }
    public static int getModelStatic(int orientation){
        if(orientation < 6)
            return 0;
        if(orientation < 30)
            return 1;
        return 2;
    }
    public void changeModel(LevelAccessor world, BlockState blockstate, BlockPos pos){
        changeModel(world, blockstate, pos, (getModel(blockstate) + 1) % 3);
    }
    public void changeModel(LevelAccessor world, BlockState blockstate, BlockPos pos, int model){
        int orientation = blockstate.getValue(ORIENTATION);
        if(model == getModel(orientation))
            return;
        if(orientation < 6) {
            if(model == 1)
                orientation = (orientation << 2) + 6;
            if(model == 2)
                orientation = switch (orientation){
                    case 0 -> 30;
                    case 5 -> 38;
                    default -> 33 + orientation;
                };
        } else if (orientation < 30) {
            if(model == 0)
                orientation = (orientation - 6) >> 2;
            if(model == 2) {
                int o = (orientation - 6) >> 2;
                orientation = switch (o) {
                    case 0 -> 30;
                    case 5 -> 38;
                    default -> o + 33;
                };
            }
        } else {
            if(model == 0) {
                if (orientation < 34)
                    orientation = 0;
                else if (orientation < 38)
                    orientation -= 34;
                else
                    orientation = 5;
            }
            if(model == 1) {
                if (orientation < 34)
                    orientation -= 24;
                else if (orientation < 38){
                    orientation = ((orientation - 34) << 2) + 13;

                }
            }
        }
        world.setBlock(pos, blockstate.setValue(ORIENTATION, orientation), 3);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, ORIENTATION);
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
        boolean flag = context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER;
        BlockState blockState = this.defaultBlockState().setValue(WATERLOGGED, flag);
        blockState = blockState.setValue(ORIENTATION, BlockFrameTransformUtils.encodeDirectionToInt(clickedFace));
        /*if(clickedFace == Direction.UP){
            blockState = switch (lookDirection){
                case NORTH, SOUTH -> blockState.setValue(ROTATION, lookDirection.getOpposite());
                default -> blockState.setValue(ROTATION, lookDirection);
            };
        } else if (clickedFace == Direction.DOWN){
            blockState = blockState.setValue(ROTATION, lookDirection);
        } else {
            blockState = blockState.setValue(ROTATION, Direction.NORTH);
        }*/
        return blockState;
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
    public InteractionResult use(BlockState blockstate, Level world, BlockPos pos, Player entity, InteractionHand hand, BlockHitResult hit) {
        super.use(blockstate, world, pos, entity, hand, hit);
        ItemStack itemStack = entity.getItemInHand(hand);
        InteractionResult result = useSlot(blockstate, world, pos, entity, hand, hit);
        if(result != InteractionResult.PASS)
            return result;
        if(!itemStack.isEmpty()) {
            if (itemStack.is(RedstonecgModItems.ROTATION_BRACKET.get()))
                return InteractionResult.FAIL;
            if (itemStack.is(RedstonecgModItems.CONTROL_PANEL.get()) || itemStack.is(RedstonecgModItems.CODED_CONTROL_PANEL.get())){
                //changeModel(world, blockstate, pos);
                if(world.getBlockEntity(pos) instanceof ControlPanelBlockEntity be){
                    CompoundTag tag = be.getParameterSet();
                    tag.putByte("orientation", (byte) ((int) blockstate.getValue(ORIENTATION)));
                    itemStack.getOrCreateTag().put("BlockParameterSet", tag);
                }
                return InteractionResult.SUCCESS;
            }
        } else if(entity.isCrouching() && AdventureProcedure.pinConfig(world, entity)){
            changeModel(world, blockstate, pos);
            return InteractionResult.SUCCESS;
        }
        if(AdventureProcedure.gateGUI(world, entity))
            if (entity instanceof ServerPlayer player) {
                ModLoaderRider.openMenu(player, new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return Component.literal("Control Panel");
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
                        return new ControlPanelGUIMenu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(pos));
                    }
                }, pos);
            }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (level.isClientSide) return;
        if(level.getBlockEntity(pos) instanceof ControlPanelBlockEntity be){
            if(stack.hasTag()) {
                CompoundTag tag = stack.getTag();
                if (tag.contains("BlockParameterSet")) {
                    tag = tag.getCompound("BlockParameterSet");
                    if(tag.contains("orientation"))
                        state = state.setValue(ORIENTATION, tag.getByte("orientation") & 0xFF);
                    be.setParameterSet(tag, placer);
                }
            }
            be.setChanged();
            level.setBlock(pos, state, 3);
            level.scheduleTick(pos, state.getBlock(), 1);
        }
    }

    public InteractionResult useSlot(BlockState blockstate, Level world, BlockPos pos, Player entity, InteractionHand hand, BlockHitResult hit){
        if(AdventureProcedure.valueConfig(world, entity)) {
            Vec3 hitPos = hit.getLocation().subtract(RedstonecgModVersionRides.getBlockPosCenter(pos));
            hitPos = hitPos.add(0.5, 0.5, 0.5);
            VoxelShape hitBox = getFinalInteractionBox(blockstate.getValue(ORIENTATION), 0, 0.1f);
            if(hitBox.bounds().contains(hitPos)){
                if(world.getBlockEntity(pos) instanceof ControlPanelBlockEntity be) {
                    InteractionResult result = be.usePanelSlot(0, world, pos, entity);
                    if(result != InteractionResult.PASS){
                        be.syncInventory(pos);
                        be.setChanged();
                        world.updateNeighborsAt(pos, blockstate.getBlock());
                        //world.sendBlockUpdated(pos, blockstate, blockstate, 3);
                        return result;
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void tick(BlockState blockState, ServerLevel level, BlockPos pos, RandomSource random){
        if(level.getBlockEntity(pos) instanceof ControlPanelBlockEntity be)
            if(be.finishTick(level, pos)) {
                be.syncInventory(pos);
                level.updateNeighborsAt(pos, blockState.getBlock());
            }
    }

    @Override
    public int getSignal(BlockState blockstate, BlockGetter blockAccess, BlockPos pos, Direction direction) {
        LevelAccessor world = (LevelAccessor) blockAccess;
        if(blockAccess.getBlockEntity(pos) instanceof ControlPanelBlockEntity be){
            ConnectionFace connectionFaceB = new ConnectionFace(direction); //temporary
            ConnectionFace connectionFaceA = be.getConnectionFace(world.getBlockState(pos).getValue(ORIENTATION), connectionFaceB);
            connectionFaceB = BlockFrameTransformUtils.getRequesterConnectionFace(world, pos.relative(direction.getOpposite()), connectionFaceA, direction.getOpposite());
            connectionFaceA = be.getConnectionFace(world.getBlockState(pos).getValue(ORIENTATION), connectionFaceB);
            //RedstonecgMod.LOGGER.debug("s "+connectionFaceA+" "+connectionFaceB);
            if(connectionFaceA.canConnect(connectionFaceB))
                return be.provideRedstone((LevelAccessor) blockAccess, pos);
        }
        return 0;
    }

    @Override
    public void neighborChanged(BlockState blockstate, Level world, BlockPos pos, Block neighborBlock, BlockPos fromPos, boolean moving) {
        super.neighborChanged(blockstate, world, pos, neighborBlock, fromPos, moving);
        //RedstonecgMod.LOGGER.debug(pos+" "+fromPos);
        onRedstone(blockstate, world, pos);
    }

    public int onRedstone(BlockState blockstate, LevelAccessor world, BlockPos pos){
        int power = 0;
        if(world.isClientSide())
            return 0;
        if(world.getBlockEntity(pos) instanceof ControlPanelBlockEntity be){
            List<ConnectionFace> connectionFaces = be.getConnectionFaces(blockstate.getValue(ORIENTATION));
            for(ConnectionFace connectionFace: connectionFaces)
                power = Math.max(power, GetRedstoneSignalProcedure.execute(world, pos, connectionFace));
            if(be.receiveRedstone(world, pos, power))
                be.syncInventory(pos);
        }
        return power;
    }

    @Override
    public MenuProvider getMenuProvider(BlockState state, Level worldIn, BlockPos pos) {
        BlockEntity tileEntity = worldIn.getBlockEntity(pos);
        return tileEntity instanceof MenuProvider menuProvider ? menuProvider : null;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ControlPanelBlockEntity(pos, state, 1);
    }

    @Override
    public boolean triggerEvent(BlockState state, Level world, BlockPos pos, int eventID, int eventParam) {
        super.triggerEvent(state, world, pos, eventID, eventParam);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity == null ? false : blockEntity.triggerEvent(eventID, eventParam);
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof ControlPanelBlockEntity be) {
                Containers.dropContents(world, pos, be);
                world.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, world, pos, newState, isMoving);
        }
    }

    @Override
    public void rotationBracket(LevelAccessor world, BlockPos pos, boolean cw) {
        BlockState blockState = world.getBlockState(pos);
        int orientation = blockState.getValue(ORIENTATION);
        for(int o = 6; o < 41; o += 4)
            orientation = adjust(orientation, o, o+3, cw);
        world.setBlock(pos, blockState.setValue(ORIENTATION, orientation), 3);
    }

    private int adjust(int current, int from, int to, boolean cw){
        if(current < from || current > to)
            return current;
        current += cw ? 1 : -1;
        if(current < from)
            current = to;
        if(current > to)
            current = from;
        return current;
    }

    @Override
    public int getRedstonePower(LevelAccessor world, BlockPos pos, ConnectionFace requesterFace) {
        if(world.getBlockEntity(pos) instanceof ControlPanelBlockEntity be){
            ConnectionFace thisFace = be.getConnectionFace(world.getBlockState(pos).getValue(ORIENTATION), requesterFace);
            //RedstonecgMod.LOGGER.debug("p "+thisFace+" "+requesterFace);
            if(thisFace.canConnect(requesterFace))
                return be.provideRedstone(world, pos);
        }
        return 0;
    }

    @Override
    public ConnectionFace getOutputConnectionFace(LevelAccessor world, BlockPos pos, ConnectionFace requesterFace) {
        if(world.getBlockEntity(pos) instanceof ControlPanelBlockEntity be) {
            ConnectionFace thisFace = be.getConnectionFace(world.getBlockState(pos).getValue(ORIENTATION), requesterFace);
            //RedstonecgMod.LOGGER.debug("o "+thisFace+" "+requesterFace);
            return thisFace;
        }
        return new ConnectionFace(requesterFace.FACE, 5);
    }

    @Override
    public ConnectionFace getAnyConnectionFace(LevelAccessor world, BlockPos pos, ConnectionFace requesterFace) {
        return getOutputConnectionFace(world, pos, requesterFace);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level world, BlockPos pos) {
        if(world.getBlockEntity(pos) instanceof ControlPanelBlockEntity be)
            return be.provideRedstone(world, pos);
        return 0;
    }

    public static Direction getFacing(int orientation){
        if(orientation < 6)
            return BlockFrameTransformUtils.decodeIntToDirection(orientation);
        if(orientation < 30)
            return BlockFrameTransformUtils.decodeIntToDirection((orientation - 6) >> 2);
        if(orientation < 34)
            return Direction.DOWN;
        if(orientation < 38)
            return BlockFrameTransformUtils.decodeIntToDirection(orientation - 33);
        return Direction.UP;
    }

    public static int setFacing(int orientation, Direction facing){
        int f = BlockFrameTransformUtils.encodeDirectionToInt(facing);
        if(orientation < 6)
            return f;
        if(orientation < 30)
            return (((orientation - 6) & 3) | (f << 2)) + 6;
        int o = (orientation - 30) & 3;
        if(f == 0)
            return 30 + o;
        if(f == 5)
            return 38 + o;
        return 33 + f;
    }

    public static Direction getRotation(int orientation){
        if(orientation < 6)
            return Direction.NORTH;
        return BlockFrameTransformUtils.decodeIntToDirection(((orientation - 6) & 3) + 1);
    }

    public static int setRotation(int orientation, Direction rotation){
        if(orientation < 6)
            return orientation;
        int r = BlockFrameTransformUtils.encodeDirectionToInt(rotation) - 1;
        return ((orientation - 6) & 12) | r;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        int orientation = state.getValue(ORIENTATION);
        Direction facing = getFacing(orientation);
        if (facing.getAxis() == Direction.Axis.Y){
            orientation = setRotation(orientation, rot.rotate(getRotation(orientation)));
        } else {
            orientation = setFacing(orientation, rot.rotate(facing));
        }
        return state.setValue(ORIENTATION, orientation);
    }
}
