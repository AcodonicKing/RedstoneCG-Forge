package net.acodonic_king.redstonecg.block.normal.wire;

import net.acodonic_king.redstonecg.block.defaults.*;
import net.acodonic_king.redstonecg.block.entity.HangingRedCuWireConnectorBlockEntity;
import net.acodonic_king.redstonecg.init.RedstonecgModItems;
import net.acodonic_king.redstonecg.init.RedstonecgModVersionRides;
import net.acodonic_king.redstonecg.network.MessengerBlockEntityPigeon;
import net.acodonic_king.redstonecg.network.RedstonecgModVariables;
import net.acodonic_king.redstonecg.procedures.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
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
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

import static net.acodonic_king.redstonecg.block.defaults.DefaultWire.getWireChainLimit;

public class HangingRedCuWireConnectorBlock extends SuperBlock implements EntityBlock, WireInterface, MeasurementProvider, FlooringInterface, RedstoneSignalInterface, PinMarkConnectionInterface, SupportingFaceInterface {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final IntegerProperty CONNECTION = IntegerProperty.create("connection",0,15);

    public HangingRedCuWireConnectorBlock(){
        super(RedstonecgModVersionRides.defaultGateProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false));
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
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Direction secondary = Direction.DOWN;
        if(world.getBlockEntity(pos) instanceof HangingRedCuWireConnectorBlockEntity be)
            secondary = be.FACING;
        return switch (secondary) {
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
        super.createBlockStateDefinition(builder);
        builder.add(CONNECTION, WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        LocalThreadValueHolders.BlockPlaceContextHolder.set(context);
        Direction clickedFace = context.getClickedFace().getOpposite();
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        //if(!GateBlockValidPlacementConditionProcedure.execute(world, pos, clickedFace)){return null;}
        boolean flag = context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER;
        world.scheduleTick(pos,this,1);
        return this.defaultBlockState().setValue(WATERLOGGED, flag);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.isClientSide) {
            if (level.getBlockEntity(pos) instanceof HangingRedCuWireConnectorBlockEntity be) {
                BlockPlaceContext context = LocalThreadValueHolders.BlockPlaceContextHolder.get();
                if(context == null){return;}
                be.FACING = context.getClickedFace().getOpposite();
                //RedstonecgMod.LOGGER.debug("{} {}",be.FACING,be.ROTATION);
                be.modelUpdate();
                be.setChanged();
                //this.redstoneUpdate(level, pos);
                level.sendBlockUpdated(pos, state, state, 2);
            }
            LocalThreadValueHolders.BlockPlaceContextHolder.clear();
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        //if (level.isClientSide) return;
        if (!state.is(newState.getBlock())) {
            int drop = 0;
            if (level.getBlockEntity(pos) instanceof HangingRedCuWireConnectorBlockEntity thisBE)
                drop = thisBE.removeAllConnectors(level);
            if(drop > 0){
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(RedstonecgModItems.REDCU_WIRE.get(),drop));
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos thisPos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (level.isClientSide) return;
        if (!stack.hasTag()) return;
        CompoundTag tag = stack.getTag();
        if (!tag.contains("TargetConnector")) return;
        tag = tag.getCompound("TargetConnector");
        if (level.getBlockEntity(thisPos) instanceof HangingRedCuWireConnectorBlockEntity thisBE){
            int[] targetIntPos = tag.getIntArray("position");
            BlockPos targetPos = new BlockPos(targetIntPos[0],targetIntPos[1],targetIntPos[2]);
            int[] relative = new int[]{
                    targetIntPos[0] - thisPos.getX(),
                    targetIntPos[1] - thisPos.getY(),
                    targetIntPos[2] - thisPos.getZ()
            };
            double distance = Math.sqrt(relative[0]*relative[0]+relative[1]*relative[1]+relative[2]*relative[2]);
            if(distance > RedstonecgModVariables.MapVariables.get(level).hangingRedCuWireMaxDistance)
                return;
            if(placer instanceof Player player)
                if(!player.getAbilities().instabuild){
                    int count = (int)distance;
                    if(count > LittleTools.hasItem(player,RedstonecgModItems.REDCU_WIRE.get()))
                        return;
                    LittleTools.removeItems(player, RedstonecgModItems.REDCU_WIRE.get(), count);
                }
            if (level.getBlockEntity(targetPos) instanceof HangingRedCuWireConnectorBlockEntity targetBE){
                targetBE.addConnectorAsTarget(thisPos);
                targetBE.setChanged();
                MessengerBlockEntityPigeon.send(new MessengerBlockEntityPigeon(targetPos, targetBE.getUpdateTag()));
                thisBE.addConnectorAsSource(targetPos);
                thisBE.setChanged();
                level.sendBlockUpdated(thisPos, state, state, 3);
                level.scheduleTick(thisPos, state.getBlock(), 1);
            }
        }
    }

    @Override
    public boolean canSurvive(BlockState blockstate, LevelReader worldIn, BlockPos pos) {
        if(RedstonecgModVariables.MapVariables.get((LevelAccessor) worldIn).canSurviveAnyCase){return true;}
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
        return new HangingRedCuWireConnectorBlockEntity(pos, state);
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
        ConnectionFace connectionFaceB = BlockFrameTransformUtils.canConnectRedstoneTargetConnectionFace(world, pos, side);
        Direction secondary = Direction.DOWN;
        if(world.getBlockEntity(pos) instanceof HangingRedCuWireConnectorBlockEntity be)
            secondary = be.FACING;
        ConnectionFacePrimaryRange connectionFaceA = new ConnectionFacePrimaryRange(secondary);
        int filter = CanConnectWallGateProcedure.To1_4GateConnectionFilter(state.getValue(CONNECTION));
        return connectionFaceA.canConnectAvoid(connectionFaceB,filter);
    }

    @Override
    public void onTick(LevelAccessor world, BlockPos pos, int recursion){
        onTick(world, pos, 0, recursion);
    }

    @Override
    public void onTick(LevelAccessor world, BlockPos pos, int power, int recursion){
        BlockState thisBlock = world.getBlockState(pos);
        if(world.getBlockEntity(pos) instanceof HangingRedCuWireConnectorBlockEntity wireEntity){
            ConnectionFacePrimaryRange connectionFaceRangeA = new ConnectionFacePrimaryRange(wireEntity.FACING);
            int filter = CanConnectWallGateProcedure.To1_4GateConnectionFilter(thisBlock.getValue(CONNECTION));
            List<ConnectionFace> connectionFaceList = connectionFaceRangeA.getList(filter);
            for(ConnectionFace connectionFaceA: connectionFaceList){
                int powerB = GetRedstoneSignalProcedure.executeWire(world, pos, connectionFaceA);
                power = Math.max(power, powerB);
            }
            //RedstonecgMod.LOGGER.debug("{} updated to {} (was {})",pos,power,wireEntity.POWER);
            if(wireEntity.BASE_READ){
                ConnectionFace connectionFaceA = new ConnectionFace(Direction.DOWN, wireEntity.FACING);
                connectionFaceA.CHANNEL = 4;
                int powerB = GetRedstoneSignalProcedure.executeWire(world, pos, connectionFaceA);
                power = Math.max(power, powerB);
            }
            power = Math.max(power, wireEntity.getConnectedPower(world));
            power = Math.max(0, power - 1);
            if(wireEntity.POWER == power){return;}
            wireEntity.POWER = power;
            wireEntity.setChanged();
            recursion++;
            boolean exceedsRecursion = recursion > getWireChainLimit(world);
            for(ConnectionFace connectionFaceA: connectionFaceList){
                BlockPos targetPos = pos.relative(connectionFaceA.FACE);
                BlockState bs = world.getBlockState(targetPos);
                Block targetBlock = bs.getBlock();
                if (targetBlock instanceof DefaultRedstoneActionGate nb){
                    if (exceedsRecursion) {
                        world.scheduleTick(targetPos, targetBlock, 1);
                        continue;
                    }
                    nb.onRedstoneUpdate(world, bs, targetPos, pos, recursion);
                } else if (targetBlock instanceof WireInterface nb) {
                    if (exceedsRecursion) {
                        world.scheduleTick(targetPos, targetBlock, 1);
                        continue;
                    }
                    nb.onTick(world, targetPos, recursion);
                } else {
                    ((Level) world).neighborChanged(targetPos,thisBlock.getBlock(),pos);
                }
            }
            if(wireEntity.BASE_READ){
                Direction face = BlockFrameTransformUtils.getWorldDirectionFromLocal(Pair.of(Direction.NORTH, wireEntity.FACING), Direction.DOWN);
                BlockPos targetPos = pos.relative(face);
                BlockState bs = world.getBlockState(targetPos);
                Block targetBlock = bs.getBlock();
                if (targetBlock instanceof DefaultRedstoneActionGate nb) {
                    if (exceedsRecursion)
                        world.scheduleTick(targetPos, targetBlock, 1);
                    else
                        nb.onRedstoneUpdate(world, bs, targetPos, pos, recursion);
                } else if (targetBlock instanceof WireInterface nb) {
                    if (exceedsRecursion)
                        world.scheduleTick(targetPos, targetBlock, 1);
                    else
                        nb.onTick(world, targetPos, recursion);
                } else {
                    ((Level) world).neighborChanged(targetPos, thisBlock.getBlock(), pos);
                }
            }
            if (exceedsRecursion) {
                wireEntity.scheduleTickTargets(world);
            } else {
                wireEntity.tickTargets(world, recursion);
            }
        }
    }

    @Override
    public void tick(BlockState blockstate, ServerLevel world, BlockPos pos, RandomSource random) {
        super.tick(blockstate, world, pos, random);
        this.onTick(world, pos, 0);
    }

    @Override
    public void neighborChanged(BlockState blockstate, Level world, BlockPos pos, Block neighborBlock, BlockPos fromPos, boolean moving) {
        super.neighborChanged(blockstate, world, pos, neighborBlock, fromPos, moving);
        this.onTick(world, pos, 0);
    }

    @Override
    public InteractionResult use(BlockState blockstate, Level world, BlockPos pos, Player entity, InteractionHand hand, BlockHitResult hit) {
        super.use(blockstate, world, pos, entity, hand, hit);
        ItemStack itemStack = entity.getItemInHand(hand);
        if(!itemStack.isEmpty()){
            if(itemStack.is(RedstonecgModItems.ROTATION_BRACKET.get())){return InteractionResult.FAIL;}
            if(itemStack.is(RedstonecgModItems.HANGING_REDCU_WIRE_CONNECTOR.get())){
                CompoundTag tag = new CompoundTag();
                int[] thisPos = new int[]{pos.getX(), pos.getY(), pos.getZ()};
                tag.putIntArray("position", thisPos);
                itemStack.getOrCreateTag().put("TargetConnector", tag);
                return InteractionResult.SUCCESS;
            }
        }
        if(AdventureProcedure.pinConfig(world, entity)){
            if(world.getBlockEntity(pos) instanceof HangingRedCuWireConnectorBlockEntity be)
                if (blockstate.getValue(CONNECTION) == 14) {
                    be.BASE_READ = !be.BASE_READ;
                    be.setChanged();
                }
            OnBlockRightClickedProcedure.execute(world, pos, blockstate);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public int getWirePower(LevelAccessor world, BlockPos pos, ConnectionFace requesterFace){
        if(world.getBlockEntity(pos) instanceof HangingRedCuWireConnectorBlockEntity be){
            BlockState blockState = world.getBlockState(pos);
            Direction secondary = be.FACING;
            Direction localDir = BlockFrameTransformUtils.getLocalDirectionFromWorld(Direction.NORTH, secondary, requesterFace.FACE.getOpposite());
            if(localDir == Direction.DOWN)
                return be.POWER;
            ConnectionFace connectionFace = new ConnectionFace(localDir, secondary);
            ConnectionFacePrimaryRange connectionFaceRange = new ConnectionFacePrimaryRange(secondary);
            int filter = CanConnectWallGateProcedure.To1_4GateConnectionFilter(blockState.getValue(CONNECTION));
            if(!connectionFaceRange.inRangeAvoid(connectionFace,filter)){return 0;}
            return be.POWER;
        }
        return 0;
    }

    @Override
    public String getMeasurement(LevelAccessor world, BlockState blockState, BlockPos pos){
        if(world.getBlockEntity(pos) instanceof HangingRedCuWireConnectorBlockEntity wireEntity){
            return String.format("%1f",wireEntity.POWER/16.0);
        }
        return "";
    }

    @Override
    public void rotationBracket(LevelAccessor world, BlockPos pos, boolean clockwise){
        BlockState blockState = world.getBlockState(pos);
        int connection = getConnection(blockState) + 1;
        connection &= 15;
        if (clockwise) {
            connection <<= 1;
            connection |= (connection >> 4) & 1;
        } else {
            connection |= (connection & 1) << 4;
            connection >>= 1;
        }
        connection -= 1;
        connection &= 15;
        blockState = blockState.setValue(CONNECTION,connection);
        world.setBlock(pos, blockState, 3);
    }

    @Override
    public int floorIt(Level level, BlockPos pos) {
        if(level.getBlockEntity(pos) instanceof HangingRedCuWireConnectorBlockEntity wireEntity){
            wireEntity.FACING = Direction.DOWN;
            wireEntity.setChanged();
        }
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

    @Override
    public int getConnection(BlockState bs) {
        return bs.getValue(CONNECTION);
    }

    @Override
    public int connectionFilter(int connection) {
        return CanConnectWallGateProcedure.To1_4GateConnectionFilter(connection);
    }

    @Override
    public boolean faceIsSupporting(LevelAccessor world, BlockPos blockPos, Direction face) {
        if(world.getBlockEntity(blockPos) instanceof HangingRedCuWireConnectorBlockEntity be)
            return be.FACING == face;
        return false;
    }
}
