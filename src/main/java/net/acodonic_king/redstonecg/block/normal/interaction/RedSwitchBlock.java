package net.acodonic_king.redstonecg.block.normal.interaction;

import net.acodonic_king.redstonecg.block.defaults.DefaultRedstoneActionGate;
import net.acodonic_king.redstonecg.block.defaults.PinMarkConnectionInterface;
import net.acodonic_king.redstonecg.block.entity.AnalogSourceBlockEntity;
import net.acodonic_king.redstonecg.block.entity.RedToggleBlockEntity;
import net.acodonic_king.redstonecg.init.RedstonecgModItems;
import net.acodonic_king.redstonecg.init.RedstonecgModVersionRides;
import net.acodonic_king.redstonecg.procedures.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.List;

public class RedSwitchBlock extends DefaultRedstoneActionGate implements EntityBlock, PinMarkConnectionInterface {
    public static final IntegerProperty CONNECTION = IntegerProperty.create("connection",0,14);
    public static final BooleanProperty STATE = BooleanProperty.create("state");

    public RedSwitchBlock(){
        super();
        this.registerDefaultState(super.defaultBlockState().setValue(STATE, false));
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
        ConnectionFace connectionFaceB = BlockFrameTransformUtils.canConnectRedstoneTargetConnectionFace(world, pos, side);
        return CanConnectWallGateProcedure.To1_4Gate(state, connectionFaceB);
    }

    @Override
    public ConnectionFace getOutputConnectionFace(LevelAccessor world, BlockPos pos, ConnectionFace requesterFace) {
        BlockState blockState = world.getBlockState(pos);
        ConnectionFace connectionFaceA = requesterFace.getConnectable();
        if(!CanConnectWallGateProcedure.To1_4Gate(blockState, requesterFace))
            connectionFaceA.CHANNEL = 5;
        return connectionFaceA;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CONNECTION, STATE);
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
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case DOWN -> Shapes.join(box(0, 0, 0, 16, 2, 16),box(4, 2, 4, 12, 4, 12), BooleanOp.OR);
            case NORTH -> Shapes.join(box(0, 0, 0, 16, 16, 2),box(4, 4, 2, 12, 12, 4),BooleanOp.OR);
            case EAST -> Shapes.join(box(14, 0, 0, 16, 16, 16), box(12, 4, 4, 14, 12, 12),BooleanOp.OR);
            case SOUTH -> Shapes.join(box(0, 0, 14, 16, 16, 16),box(4, 4, 12, 12, 12, 14),BooleanOp.OR);
            case WEST -> Shapes.join(box(0, 0, 0, 2, 16, 16),box(2, 4, 4, 4, 12, 12),BooleanOp.OR);
            case UP -> Shapes.join(box(0, 14, 0, 16, 16, 16),box(4, 12, 4, 12, 14, 12),BooleanOp.OR);
        };
    }

    @Override
    public InteractionResult use(BlockState blockstate, Level world, BlockPos pos, Player entity, InteractionHand hand, BlockHitResult hit) {
        InteractionResult interactionResult = super.use(blockstate, world, pos, entity, hand, hit);
        if(interactionResult == InteractionResult.FAIL){return interactionResult;}
        ItemStack itemStack = entity.getItemInHand(hand);
        if(!itemStack.isEmpty()){
            if(itemStack.is(RedstonecgModItems.ROTATION_BRACKET.get())){return InteractionResult.FAIL;}
        }
        if(AdventureProcedure.valueConfig(world, entity)) {
            Vec3 hitPos = hit.getLocation().subtract(RedstonecgModVersionRides.getBlockPosCenter(pos));
            if (switch (blockstate.getValue(FACING)) {
                case DOWN, UP -> (-0.25 <= hitPos.x && hitPos.x <= 0.25) && (-0.25 <= hitPos.z && hitPos.z <= 0.25);
                case EAST, WEST -> (-0.25 <= hitPos.y && hitPos.y <= 0.25) && (-0.25 <= hitPos.z && hitPos.z <= 0.25);
                case NORTH, SOUTH -> (-0.25 <= hitPos.x && hitPos.x <= 0.25) && (-0.25 <= hitPos.y && hitPos.y <= 0.25);
            }) {
                toggleState(world, blockstate, pos, entity);
                return InteractionResult.SUCCESS;
            }
        }
        OnBlockRightClickedProcedure.execute(world, pos, blockstate);
        return InteractionResult.SUCCESS;
    }

    public void toggleState(LevelAccessor level, BlockState blockState, BlockPos pos, Player player){
        boolean state = blockState.getValue(STATE);
        setState(level, blockState, pos, null, !state);
    }

    public void setState(LevelAccessor level, BlockState blockState, BlockPos pos, Player player, boolean state){
        Level world = (Level) level;
        if(state == blockState.getValue(STATE))
            return;
        level.playSound(null, pos, state ? SoundEvents.STONE_BUTTON_CLICK_ON : SoundEvents.STONE_BUTTON_CLICK_OFF, SoundSource.BLOCKS, 1.0f, 1.0f);
        world.setBlock(pos, blockState.setValue(STATE, state), 2);
        world.updateNeighborsAt(pos, blockState.getBlock());
        /*for(Direction localDirection: GetGateInputSidesProcedure.Get1_4Gate(blockState)){
            Direction worldDirection = BlockFrameTransformUtils.getWorldDirectionFromLocal(blockState, localDirection);
            sendRedstoneUpdateInDirection(level, blockState.getBlock(), pos, worldDirection);
        }*/
    }

    @Override
    public int getRedstonePower(LevelAccessor world, BlockPos pos, ConnectionFace requesterFace) {
        BlockState blockState = world.getBlockState(pos);
        if(!CanConnectWallGateProcedure.To1_4Gate(blockState, requesterFace))
            return 0;
        return world.getBlockState(pos).getValue(STATE) ? 15 : 0;
    }

    @Override
    public boolean isOutput(LevelAccessor world, BlockState blockState, BlockPos pos, Direction direction){
        Direction dir = BlockFrameTransformUtils.getLocalDirectionFromWorld(blockState,direction);
        for(Direction localDirection: GetGateInputSidesProcedure.Get1_4Gate(blockState))
            if(dir == localDirection)
                return true;
        return false;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level world, BlockPos pos) {
        return blockState.getValue(STATE) ? 15 : 0;
    }

    @Override
    public String getMeasurement(LevelAccessor world, BlockState blockState, BlockPos pos){
        return String.format("%1d",blockState.getValue(STATE) ? 15 : 0);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RedToggleBlockEntity(pos, state);
    }

    @Override
    public boolean triggerEvent(BlockState state, Level world, BlockPos pos, int eventID, int eventParam) {
        super.triggerEvent(state, world, pos, eventID, eventParam);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity == null ? false : blockEntity.triggerEvent(eventID, eventParam);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (level.isClientSide) return;
        if (level.getBlockEntity(pos) instanceof RedToggleBlockEntity be){
            be.setName(stack);
            be.setChanged();
            level.setBlock(pos, state, 3);
            level.scheduleTick(pos, state.getBlock(), 1);
        }
    }

    @Override
    public List<ItemStack> getDrops(List<ItemStack> drops, BlockState state, BlockEntity entity) {
        drops.clear();
        ItemStack stack = new ItemStack(asItem());
        if(entity instanceof RedToggleBlockEntity sbe)
            if(!sbe.CUSTOM_NAME.isEmpty())
                stack.setHoverName(Component.literal(sbe.CUSTOM_NAME));
        drops.add(stack);
        return drops;
    }

    public RCGMatrix.M4F stateTransformer(RCGMatrix.M4F mat, BlockState state){
        return stateTransformer(mat, state.getValue(STATE));
    }

    public RCGMatrix.M4F stateTransformer(RCGMatrix.M4F mat, boolean state){
        if(state)
            mat.rotateZ(RCGMatrix.ANGLES[1] * 0.5f);
        mat.translate(0,0,-0.125f);
        return mat;
    }
}
