package net.acodonic_king.redstonecg.block;

import io.netty.buffer.Unpooled;
import net.acodonic_king.redstonecg.ModLoaderRider;
import net.acodonic_king.redstonecg.RedstonecgMod;
import net.acodonic_king.redstonecg.block.defaults.RedstoneSignalInterface;
import net.acodonic_king.redstonecg.block.defaults.RotationBracketInterface;
import net.acodonic_king.redstonecg.block.defaults.SuperBlock;
import net.acodonic_king.redstonecg.block.entity.ControlPanelBlockEntity;
import net.acodonic_king.redstonecg.block.gui.control_panel.ControlPanelGUIMenu;
import net.acodonic_king.redstonecg.init.RedstonecgModItems;
import net.acodonic_king.redstonecg.init.RedstonecgModVersionRides;
import net.acodonic_king.redstonecg.procedures.AdventureProcedure;
import net.acodonic_king.redstonecg.procedures.BlockFrameTransformUtils;
import net.acodonic_king.redstonecg.procedures.ConnectionFace;
import net.acodonic_king.redstonecg.procedures.GetRedstoneSignalProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
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
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class ControlPanelBlock extends SuperBlock implements SimpleWaterloggedBlock, EntityBlock, RotationBracketInterface, RedstoneSignalInterface {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final IntegerProperty ORIENTATION = IntegerProperty.create("orientation", 0, 41);

    public ControlPanelBlock(){
        super(RedstonecgModVersionRides.defaultGateProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return getShape(state, world, pos, context, 4);
    }

    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context, int size) {
        int orientation = state.getValue(ORIENTATION);
        if(orientation < 6) {
            return Shapes.join(switch (orientation) {
                case 0 -> box(0, 0, 0, 16, 10, 16);
                case 1 -> box(0, 0, 0, 16, 16, 10);
                case 2 -> box(6, 0, 0, 16, 16, 16);
                case 3 -> box(0, 0, 6, 16, 16, 16);
                case 4 -> box(0, 0, 0, 10, 16, 16);
                default -> box(0, 6, 0, 16, 16, 16);
            }, getInteractionBox(orientation, size, 10, 14), BooleanOp.OR);
        } else if (orientation < 30) {
            int o = (orientation - 6) >> 2;
            return Shapes.join(switch (o) {
                case 0 -> box(0, 0, 0, 16, 11, 16);
                case 1 -> box(0, 0, 0, 16, 16, 11);
                case 2 -> box(5, 0, 0, 16, 16, 16);
                case 3 -> box(0, 0, 5, 16, 16, 16);
                case 4 -> box(0, 0, 0, 11, 16, 16);
                default -> box(0, 5, 0, 16, 16, 16);
            }, getInteractionBox(o, size, 11, 16), BooleanOp.OR);
        } else if (orientation < 42) {
        /*VoxelShape shape = switch (orientation){
            case 30 -> box(0, 0, 0, 16, 8, 8);
            case 31 -> box(8, 0, 0, 16, 8, 16);
            case 32 -> box(0, 0, 8, 16, 8, 16);
            case 33 -> box(0, 0, 0, 8, 8, 16);

            case 34 -> box(0, 0, 0, 8, 16, 8);
            case 35 -> box(8, 0, 0, 16, 16, 8);
            case 36 -> box(8, 0, 8, 16, 16, 16);
            case 37 -> box(0, 0, 8, 8, 16, 16);

            case 38 -> box(0, 8, 0, 16, 16, 8);
            case 39 -> box(8, 8, 0, 16, 16, 16);
            case 40 -> box(0, 8, 8, 16, 16, 16);
            case 41 -> box(0, 8, 0, 8, 16, 16);

            default -> box(0, 0, 0, 16, 16, 16);
        };*/
            VoxelShape shape1;
            if(orientation < 34)
                shape1 = box(0, 0, 0, 16, 2, 16);
            else if (orientation < 38)
                shape1 = getWall(orientation - 34);
            else
                shape1 = box(0, 14, 0, 16, 16, 16);

            VoxelShape shape2;
            if(orientation < 34)
                shape2 = getWall(orientation - 30);
            else if (orientation < 38)
                shape2 = getWall(orientation - 35);
            else
                shape2 = getWall(orientation - 38);

            return Shapes.join(Shapes.join(shape1, shape2, BooleanOp.OR), getInteractionBox(6, size, 4, 12), BooleanOp.OR);
        }
        return box(0, 0, 0, 16, 16, 16);
    }

    private VoxelShape getWall(int o){
        return switch (o & 3){
            case 0 -> box(0, 0, 0, 16, 16, 2);
            case 1 -> box(14, 0, 0, 16, 16, 16);
            case 2 -> box(0, 0, 14, 16, 16, 16);
            case 3 -> box(0, 0, 0, 2, 16, 16);
            default -> box(0, 0, 0, 16, 2, 16);
        };
    }

    public VoxelShape getInteractionBox(int orientation, int size, int ys, int ye){
        return switch (orientation) {
            case 0 -> box(8-size, ys, 8-size, 8+size, ye, 8+size);
            case 1 -> box(8-size, 8-size, ys, 8+size, 8+size, ye);
            case 2 -> box(16-ye, 8-size, 8-size, 16-ys, 8+size, 8+size);
            case 3 -> box(8-size, 8-size, 16-ye, 8+size, 8+size, 16-ys);
            case 4 -> box(ys, 8-size, 8-size, ye, 8+size, 8+size);
            case 5 -> box(8-size, 16-ye, 8-size, 8+size, 16-ys, 8+size);
            default -> box(8-size, 8-size, 8-size, 8+size, 8+size, 8+size);
        };
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
        if(AdventureProcedure.valueConfig(world, entity)) {
            Vec3 hitPos = hit.getLocation().subtract(RedstonecgModVersionRides.getBlockPosCenter(pos));
            hitPos = hitPos.scale(0.99).add(0.5, 0.5, 0.5);
            VoxelShape hitBox = getInteractionBox(6, 4, 4, 12);
            int orientation = blockstate.getValue(ORIENTATION);
            if(orientation < 6)
                hitBox = getInteractionBox(orientation, 4, 10, 14);
            else if (orientation < 30) {
                int o = (orientation - 6) >> 2;
                hitBox = getInteractionBox(o, 4, 11, 16);
            }
            //RedstonecgMod.LOGGER.debug(hitBox.bounds()+" "+hitPos);
            if(hitBox.bounds().contains(hitPos)){
            //if((-0.25 <= hitPos.x && hitPos.x <= 0.25) && (-0.25 <= hitPos.z && hitPos.z <= 0.25)){
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
        if(!itemStack.isEmpty()) {
            if (itemStack.is(RedstonecgModItems.ROTATION_BRACKET.get()))
                return InteractionResult.FAIL;
            if (itemStack.is(RedstonecgModItems.CONTROL_PANEL.get())){
                int orientation = blockstate.getValue(ORIENTATION);
                if(orientation < 6)
                    orientation = (orientation << 2) + 6;
                else if (orientation < 30) {
                    int o = (orientation - 6) >> 2;
                    orientation = switch (o){
                        case 0 -> 30;
                        case 1 -> 34;
                        case 2 -> 35;
                        case 3 -> 36;
                        case 4 -> 37;
                        case 5 -> 38;
                        default -> 30;
                    };
                } else if (orientation < 34)
                    orientation = 0;
                else if (orientation < 38)
                    orientation -= 34;
                else
                    orientation = 5;
                world.setBlock(pos, blockstate.setValue(ORIENTATION, orientation), 3);
                return InteractionResult.SUCCESS;
            }
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
    public void tick(BlockState blockState, ServerLevel level, BlockPos pos, RandomSource random){
        if(level.getBlockEntity(pos) instanceof ControlPanelBlockEntity be)
            if(be.nextTick(level, pos)) {
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

    public void onRedstone(BlockState blockstate, LevelAccessor world, BlockPos pos){
        if(world.isClientSide())
            return;
        if(world.getBlockEntity(pos) instanceof ControlPanelBlockEntity be){
            List<ConnectionFace> connectionFaces = be.getConnectionFaces(blockstate.getValue(ORIENTATION));
            int power = 0;
            for(ConnectionFace connectionFace: connectionFaces) {
                //RedstonecgMod.LOGGER.debug(connectionFace);
                power = Math.max(power, GetRedstoneSignalProcedure.execute(world, pos, connectionFace));
            }
            if(be.receiveRedstone(world, pos, power)) {
                be.syncInventory(pos);
            }
        }
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
}
