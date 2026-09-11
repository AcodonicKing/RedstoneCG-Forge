package net.acodonic_king.redstonecg.block.normal.wire;

import io.netty.buffer.Unpooled;
import net.acodonic_king.redstonecg.ModLoaderRider;
import net.acodonic_king.redstonecg.RedstonecgMod;
import net.acodonic_king.redstonecg.block.defaults.*;
import net.acodonic_king.redstonecg.block.entity.RedCuWireTransitionBlockEntity;
import net.acodonic_king.redstonecg.block.gui.redcu_wire_transition.RedCuWireTransitionGUIMenu;
import net.acodonic_king.redstonecg.init.RedstonecgModItems;
import net.acodonic_king.redstonecg.init.RedstonecgModVersionRides;
import net.acodonic_king.redstonecg.network.MessengerBlockEntityPigeon;
import net.acodonic_king.redstonecg.procedures.*;
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
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import static net.acodonic_king.redstonecg.block.defaults.DefaultWire.getWireChainLimit;

public class RedCuWireTransitionBlock extends SuperBlock implements EntityBlock, WireInterface, MeasurementProvider, RedstoneSignalInterface, SupportingFaceInterface {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final IntegerProperty MODEL = IntegerProperty.create("model", 0, 11);
    public RedCuWireTransitionBlock() {
        super(RedstonecgModVersionRides.defaultGateProperties);
        //this.registerDefaultState(this.stateDefinition.any());
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false));
    }
    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return state.getFluidState().isEmpty();
    }

    @Override
    public void tick(BlockState blockstate, ServerLevel world, BlockPos pos, RandomSource random) {
        super.tick(blockstate, world, pos, random);
        //RedstonecgMod.LOGGER.debug("ticked");
        if(world.getBlockEntity(pos) instanceof RedCuWireTransitionBlockEntity be){
            if(be.SHAPE == 0){
                for(byte side: "DNESWU".getBytes()) {
                    char char_side = (char) side;
                    Direction direction = RedCuWireTransitionBlockEntity.getDirectionCharacter(char_side);
                    byte connectionFaceA = ConnectionFace.primitiveAll(direction);
                    BlockState neighborState = world.getBlockState(pos.relative(direction));
                    if(!RedstonecgModVersionRides.isBlockStateSoftSolid(neighborState))
                        continue;
                    byte connectionFaceB = BlockFrameTransformUtils.getTargetBlockConnectionFace(world, pos, connectionFaceA, ConnectionFace.primitiveChannelMask(direction.getOpposite(), ConnectionFace.MASK_CHANNEL_C));
                    //RedstonecgMod.LOGGER.debug("{} {}",connectionFaceA,connectionFaceB);
                    int ch = ConnectionFace.toChannel(connectionFaceB);
                    if(ch < 4)
                        be.setSideCharacter(char_side, (byte) ch);
                }
                be.setChanged();
                be.shapeFind();
                //be.pathFind();
                //MessengerBlockEntityPigeon.send(new MessengerBlockEntityPigeon(pos, be.getUpdateTag()));
                //world.setBlock(pos, blockstate, 3);
                world.sendBlockUpdated(pos, blockstate, blockstate, 3);
            }
        }
        this.onTick(world, pos, 0);
    }

    @Override
    public void onTick(LevelAccessor world, BlockPos pos, int recursion){
        onTick(world, pos, 0, recursion);
    }

    @Override
    public void onTick(LevelAccessor world, BlockPos pos, int power, int recursion){
        if(world.isClientSide())
            return;
        if(world.getBlockEntity(pos) instanceof RedCuWireTransitionBlockEntity blockEntity){
            for(byte side: "DNESWU".getBytes()){
                char char_side = (char) side;
                byte connection = blockEntity.getSideCharacter(char_side);
                if(connection == 5){continue;}
                Direction direction = RedCuWireTransitionBlockEntity.getDirectionCharacter(char_side);
                byte connectionFaceA = ConnectionFace.primitiveChannel(direction, connection);
                int powerB = GetRedstoneSignalProcedure.executeWire(world, pos, connectionFaceA);
                power = Math.max(power, powerB);
            }
            power = Math.max(0, power - 1);
            if (blockEntity.POWER == power) {return;}
            blockEntity.POWER = power;
            blockEntity.setChanged();
            //MessengerBlockEntityPigeon.send(new MessengerBlockEntityPigeon(pos, blockEntity.getUpdateTag()));
            BlockState thisBlock = blockEntity.getBlockState();
            recursion++;
            boolean exceedsRecursion = recursion > getWireChainLimit(world);
            for(byte side: "DNESWU".getBytes()){
                char char_side = (char) side;
                byte connection = blockEntity.getSideCharacter(char_side);
                if(connection == 5){continue;}
                Direction direction = RedCuWireTransitionBlockEntity.getDirectionCharacter(char_side);
                BlockPos targetPos = pos.relative(direction);
                BlockState bs = world.getBlockState(targetPos);
                Block targetBlock = bs.getBlock();
                if (targetBlock instanceof DefaultRedstoneActionGate nb){
                    if (exceedsRecursion) {
                        world.scheduleTick(targetPos, targetBlock, 1);
                        continue;
                    }
                    nb.onRedstoneUpdate(world, bs, targetPos, pos, recursion);
                } else if (targetBlock instanceof WireInterface nb){
                    if (exceedsRecursion) {
                        world.scheduleTick(targetPos, targetBlock, 1);
                        continue;
                    }
                    nb.onTick(world, targetPos, recursion);
                } else {
                    Block nb = bs.getBlock();
                    //nb.neighborChanged(bs,(Level) world,targetPos,thisBlock.getBlock(),pos,false);
                    ((Level) world).neighborChanged(targetPos,thisBlock.getBlock(),pos);
                }
            }
        }
    }

    @Override
    public String getMeasurement(LevelAccessor world, BlockState blockState, BlockPos pos){
        if(world.getBlockEntity(pos) instanceof RedCuWireTransitionBlockEntity wireEntity){
            return String.format("%1f",wireEntity.POWER/16.0);
        }
        return "";
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
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if(world.getBlockEntity(pos) instanceof RedCuWireTransitionBlockEntity be){
            //RedstonecgMod.LOGGER.debug(be.SHAPE+" "+be.DOWN+" "+be.NORTH+" "+be.EAST+" "+be.SOUTH+" "+be.WEST+" "+be.UP);
            if(be.SHAPE != 0){
                VoxelShape shape = Shapes.empty();
                if((be.SHAPE & 0b01000000) > 0){
                    shape = box(0,0,0,16,16,16);
                    for (int i = 0; i < 6; i++) {
                        if (((be.SHAPE >> i) & 1) > 0) {
                            shape = Shapes.join(shape, getBox(i, 3), BooleanOp.AND);
                        }
                    }
                } else {
                    for (int i = 0; i < 6; i++) {
                        if (((be.SHAPE >> i) & 1) > 0) {
                            shape = Shapes.join(shape, getBox(i, 3), BooleanOp.OR);
                        }
                    }
                }
                if(be.WALLS != 0){
                    for (int i = 0; i < 6; i++) {
                        if (((be.WALLS >> i) & 1) > 0) {
                            shape = Shapes.join(shape, getBox(i, 2), BooleanOp.OR);
                        }
                    }
                }
                return shape;
            }
            return box(0,0,0,16,16,16);
        }
        return Shapes.empty();
    }
    private VoxelShape getBox(int c, int w){
        return switch (c) {
            case 0 -> box(0, 0, 0, 16, w, 16);
            case 1 -> box(0, 0, 0, 16, 16, w);
            case 2 -> box(16-w, 0, 0, 16, 16, 16);
            case 3 -> box(0, 0, 16-w, 16, 16, 16);
            case 4 -> box(0, 0, 0, w, 16, 16);
            case 5 -> box(0, 16-w, 0, 16, 16, 16);
            default -> box(0, 0, 0, 16, w, 16);
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(MODEL,WATERLOGGED);
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
        return new RedCuWireTransitionBlockEntity(pos, state);
    }

    @Override
    public int getWirePower(LevelAccessor world, BlockPos pos, byte requesterFace) {
        Direction direction = ConnectionFace.decodeFace(requesterFace).getOpposite();
        if(world.getBlockEntity(pos) instanceof RedCuWireTransitionBlockEntity be){
            char d = RedCuWireTransitionBlockEntity.getCharacterDirection(direction);
            byte c = be.getSideCharacter(d);
            if (c == ConnectionFace.toChannel(requesterFace) || ConnectionFace.connectsAll(requesterFace))
                return be.POWER;
        }
        return 0;
    }

    @Override
    public int getSignal(BlockState blockstate, BlockGetter blockAccess, BlockPos pos, Direction direction) {
        if(canConnectRedstone(blockstate, blockAccess, pos, direction)){
            if(blockAccess.getBlockEntity(pos) instanceof RedCuWireTransitionBlockEntity be)
                return be.POWER >> 4;
        }
        return 0;
    }

    @Override
    public void rotationBracket(LevelAccessor world, BlockPos pos, boolean clockwise){}

    @Override
    public InteractionResult use(BlockState blockstate, Level world, BlockPos pos, Player entity, InteractionHand hand, BlockHitResult hit) {
        super.use(blockstate, world, pos, entity, hand, hit);
        if(AdventureProcedure.gateGUI(world, entity))
            if (entity instanceof ServerPlayer player) {
                ModLoaderRider.openMenu(player, new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return Component.literal("RedCu Wire Transition");
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
                        return new RedCuWireTransitionGUIMenu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(pos));
                    }
                }, pos);
            }
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean canSurvive(BlockState blockstate, LevelReader worldIn, BlockPos pos) {
        if (worldIn.getBlockEntity(pos) instanceof RedCuWireTransitionBlockEntity be){
            //RedstonecgMod.LOGGER.debug(be.SHAPE);
            if(be.SHAPE == 0){return true;}
            byte i = 0;
            for(byte c: "DNESWU".getBytes()){
                //byte d = be.getSideCharacter((char) c);
                if(((be.SHAPE >> i) & 1) == 0){
                    i++;
                    continue;
                }
                Direction direction = RedCuWireTransitionBlockEntity.getDirectionCharacter((char) c);
                BlockPos targetPos = pos.relative(direction);
                //RedstonecgMod.LOGGER.debug(worldIn.getBlockState(targetPos).getBlock());
                BlockState bs = worldIn.getBlockState(targetPos);
                return RedstonecgModVersionRides.isBlockStateSoftSolid(bs);
            }
            return false;
        }
        return super.canSurvive(blockstate, worldIn, pos);
    }

    @Override
    public MenuProvider getMenuProvider(BlockState state, Level worldIn, BlockPos pos) {
        BlockEntity tileEntity = worldIn.getBlockEntity(pos);
        return tileEntity instanceof MenuProvider menuProvider ? menuProvider : null;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        boolean flag = level.getFluidState(context.getClickedPos()).getType() == Fluids.WATER;
        level.scheduleTick(context.getClickedPos(), this, 1);
        return this.defaultBlockState().setValue(WATERLOGGED, flag);
    }

    @Override
    public int getRedstonePower(LevelAccessor world, BlockPos pos, byte requesterFace) {
        return getWirePower(world, pos, requesterFace) >> 4;
    }

    @Override
    public byte getOutputConnectionFace(LevelAccessor world, BlockPos pos, byte requesterFace) {
        Direction direction = ConnectionFace.decodeFace(requesterFace).getOpposite();
        if(world.getBlockEntity(pos) instanceof RedCuWireTransitionBlockEntity be){
            char d = RedCuWireTransitionBlockEntity.getCharacterDirection(direction);
            byte c = be.getSideCharacter(d);
            return ConnectionFace.primitiveChannel(direction,c);
        }
        return ConnectionFace.primitiveNone(direction);
    }

    @Override
    public byte getAnyConnectionFace(LevelAccessor world, BlockPos pos, byte requesterFace) {
        return getOutputConnectionFace(world, pos, requesterFace);
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
        byte connectionFaceB = BlockFrameTransformUtils.canConnectRedstoneTargetConnectionFace(world, pos, side);
        byte connectionFaceA = getOutputConnectionFace((LevelAccessor) world, pos, connectionFaceB);
        return ConnectionFace.canConnect(connectionFaceA, connectionFaceB);
    }

    @Override
    public void neighborChanged(BlockState blockstate, Level world, BlockPos pos, Block neighborBlock, BlockPos fromPos, boolean moving) {
        super.neighborChanged(blockstate, world, pos, neighborBlock, fromPos, moving);
        this.onTick(world, pos, 0);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            if(level.getBlockEntity(pos) instanceof RedCuWireTransitionBlockEntity be){
                int amount = 0;
                for(int i = 0; i < 6; i++){
                    if(((be.WALLS >> i) & 1) > 0)
                        amount++;
                }
                ItemStack itemStack = new ItemStack(RedstonecgModItems.SMOOTH_STONE_PLATE.get(), amount);
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), itemStack);
                //Containers.dropContents(level, pos, NonNullList.of(itemStack));
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public boolean faceIsSupporting(LevelAccessor world, BlockPos blockPos, Direction face) {
        if(world.getBlockEntity(blockPos) instanceof RedCuWireTransitionBlockEntity be){
            if(be.WALLS != 0){
                int dir = BlockFrameTransformUtils.encodeDirectionToInt(face);
                return ((be.WALLS >> dir) & 1) > 0;
            }
        }
        return false;
    }

    /*@Override
    public int floorIt(Level level, BlockPos pos) {

        return 1;
    }*/
}
