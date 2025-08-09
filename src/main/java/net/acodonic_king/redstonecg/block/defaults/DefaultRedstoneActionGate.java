package net.acodonic_king.redstonecg.block.defaults;

import net.acodonic_king.redstonecg.procedures.BlockFrameTransformUtils;
import net.acodonic_king.redstonecg.procedures.ConnectionFace;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class DefaultRedstoneActionGate extends DefaultConnectableGate implements RedstoneSignalInterface {
    public DefaultRedstoneActionGate(){
        super();
    }
    public int onRedstoneUpdate(LevelAccessor world, BlockState blockState, BlockPos pos, BlockPos fromPos){
        return onRedstoneUpdate(world, blockState, pos);
    }
    public int onRedstoneUpdate(LevelAccessor world, BlockState blockState, BlockPos pos){
        return 0;
    }
    @Override
    public BlockState getStateForPlacementDirect(BlockPlaceContext context) {
        BlockState blockState = super.getStateForPlacementDirect(context);
        if(blockState == null){return null;}
        context.getLevel().scheduleTick(context.getClickedPos(),blockState.getBlock(),1);
        //emittedRedstonePower(context.getLevel(),blockState,context.getClickedPos());
        return blockState;
    }
    @Override
    public BlockState getStateForPlacementOpposite(BlockPlaceContext context) {
        BlockState blockState = super.getStateForPlacementOpposite(context);
        if(blockState == null){return null;}
        context.getLevel().scheduleTick(context.getClickedPos(),blockState.getBlock(),1);
        //emittedRedstonePower(context.getLevel(),blockState,context.getClickedPos());
        return blockState;
    }
    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }
    @Override
    public int getSignal(BlockState blockstate, BlockGetter blockAccess, BlockPos pos, Direction direction) {
        ConnectionFace connectionFaceB = new ConnectionFace(direction); //temporary
        LevelAccessor world = (LevelAccessor) blockAccess;
        if (blockstate.getBlock() instanceof RedstoneSignalInterface si){
            ConnectionFace connectionFaceA = si.getOutputConnectionFace(world, pos, connectionFaceB);
            connectionFaceB = BlockFrameTransformUtils.getRequesterConnectionFace(world, pos.relative(direction.getOpposite()), connectionFaceA, direction.getOpposite());

            return si.getRedstonePower(world, pos, connectionFaceB);
        }
        return 0;
    }
    @Override
    public void neighborChanged(BlockState blockstate, Level world, BlockPos pos, Block neighborBlock, BlockPos fromPos, boolean moving) {
        super.neighborChanged(blockstate, world, pos, neighborBlock, fromPos, moving);
        this.onRedstoneUpdate(world, blockstate, pos, fromPos);
    }
    public void sendRedstoneUpdateInDirection(LevelAccessor level, Block thisBlock, BlockPos thisPos, Direction direction){
        Level world = (Level) level;
        BlockPos neighborPos = thisPos.relative(direction);
        //RedstonecgMod.LOGGER.debug("Sending update to {}",neighborPos);
        BlockState bs = world.getBlockState(neighborPos);
        Block block = bs.getBlock();
        if (block instanceof DefaultConnectableGate nb){
            if(nb.isOutput(world, bs, neighborPos, direction.getOpposite())){return;}
        }
        if (block instanceof DefaultRedstoneActionGate nb){
            nb.onRedstoneUpdate(world, bs, neighborPos, thisPos);
        } else {
            //block.neighborChanged(bs,world,neighborPos,thisBlock,thisPos,false);
            world.neighborChanged(neighborPos,thisBlock,thisPos);
        }
    }
    @Override
    public void tick(BlockState blockstate, ServerLevel world, BlockPos pos, RandomSource random){
        onRedstoneUpdate(world,blockstate,pos);
    }
    @Override
    public boolean isOutput(LevelAccessor world, BlockState blockState, BlockPos pos, Direction direction){
        Direction dir = BlockFrameTransformUtils.getLocalDirectionFromWorld(blockState,direction);
        return dir == Direction.NORTH;
    }

    @Override
    public int getRedstonePower(LevelAccessor world, BlockPos pos, ConnectionFace requesterFace) {
        return 0;
    }

    @Override
    public ConnectionFace getOutputConnectionFace(LevelAccessor world, BlockPos pos, ConnectionFace requesterFace) {
        return BlockFrameTransformUtils.getConnectionFace(world.getBlockState(pos), Direction.NORTH);
    }

    @Override
    public ConnectionFace getAnyConnectionFace(LevelAccessor world, BlockPos pos, ConnectionFace requesterFace) {
        BlockState blockState = world.getBlockState(pos);
        Direction localDirection = BlockFrameTransformUtils.getLocalDirectionFromWorld(blockState,requesterFace.FACE.getOpposite());
        return BlockFrameTransformUtils.getConnectionFace(blockState,localDirection);
    }
}
