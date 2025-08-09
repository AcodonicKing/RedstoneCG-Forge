
package net.acodonic_king.redstonecg.block.parallel.wire;

import net.acodonic_king.redstonecg.block.defaults.DefaultParallelGate;
import net.acodonic_king.redstonecg.block.defaults.DefaultRedstoneActionGate;
import net.acodonic_king.redstonecg.block.defaults.OldInterface;
import net.acodonic_king.redstonecg.block.defaults.RedstoneSignalInterface;
import net.acodonic_king.redstonecg.block.entity.DefaultAnalogGateBlockEntity;
import net.acodonic_king.redstonecg.procedures.*;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import javax.annotation.Nullable;

public class ParallelLineOutputBlock extends DefaultParallelGate implements EntityBlock, OldInterface {
	public static final IntegerProperty CONNECTION = IntegerProperty.create("connection",0,6);

	public ParallelLineOutputBlock() {
		super();
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new DefaultAnalogGateBlockEntity(pos, state);
	}

	@Override
	public int getRedstonePower(LevelAccessor world, BlockPos pos, ConnectionFace requesterFace) {
		BlockState blockState = world.getBlockState(pos);
		if(!CanConnectWallGateProcedure.To1_3Gate(blockState, requesterFace))
			return 0;
		if(world.getBlockEntity(pos) instanceof DefaultAnalogGateBlockEntity be){
			return be.POWER;
		}
		return 0;
	}

	@Override
	public ConnectionFace getOutputConnectionFace(LevelAccessor world, BlockPos pos, ConnectionFace requesterFace) {
		BlockState blockState = world.getBlockState(pos);
		ConnectionFace connectionFaceA = requesterFace.getConnectable();
		if(!CanConnectWallGateProcedure.To1_3Gate(blockState, requesterFace))
			connectionFaceA.CHANNEL = 5;
		return connectionFaceA;
	}

	/*@Override
	public int getSignal(BlockState blockstate, BlockGetter blockAccess, BlockPos pos, Direction direction) {
		Level world = (Level) blockAccess;
		BlockPos CallPos = pos.relative(direction.getOpposite());
		BlockState block = world.getBlockState(CallPos);
		//RedstonecgMod.LOGGER.debug("getSignal {} {} {} {}", pos, direction, CallPos, block);
		if(!(block.getBlock() instanceof DefaultRedstoneActionGate)) {
			if (!canConnectRedstone(blockstate, blockAccess, pos, direction)) {
				return 0;
			}
		}
		return this.getRedstonePower(world, blockstate, pos);
	}*/

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(CONNECTION);
	}

	@Override
	public boolean breakParallelLine(LevelAccessor world, BlockState thisState, BlockPos thisPos, Direction directedTo, boolean readOut){
		return true;
	}

	@Override
	public int onRedstoneUpdate(LevelAccessor world, BlockState thisState, BlockPos thisPos){
		Direction direction = BlockFrameTransformUtils.getWorldDirectionFromLocalForward(thisState);
		int linePower = GetParallelSignalProcedure.getParallelLinePowerInDirection(world, thisPos.relative(direction), direction);
		//RedstonecgMod.LOGGER.debug("Update {} {} {}", linePower, thisPos, direction);
		if(world.getBlockEntity(thisPos) instanceof DefaultAnalogGateBlockEntity be){
			if(be.POWER == linePower){ return 0; }
			be.POWER = linePower;
			be.setChanged();
			int connection = thisState.getValue(CONNECTION);
			connection = (connection + 1) << 1;
			connection = ConnectionFacePrimaryRange.rotateFilter(connection, thisState.getValue(ROTATION));
			ConnectionFacePrimaryRange connectionFaceRange = new ConnectionFacePrimaryRange(thisState.getValue(FACING));
			for(ConnectionFace connectionFaceA: connectionFaceRange.getList(connection)){
				//RedstonecgMod.LOGGER.debug("Updating in {}", connectionFaceA.FACE);
				sendRedstoneUpdateInDirection(world, thisState.getBlock(), thisPos, connectionFaceA.FACE);
			}
		}
		return 0;
	}

	@Override
	public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
		ConnectionFace connectionFaceB = BlockFrameTransformUtils.canConnectRedstoneTargetConnectionFace(world, pos, side);
		return CanConnectWallGateProcedure.To1_3Gate(state, connectionFaceB);
	}

	@Override
	public boolean isOutput(LevelAccessor world, BlockState blockState, BlockPos pos, Direction direction){
		ConnectionFace connectionFaceB = new ConnectionFace(direction.getOpposite());
		return CanConnectWallGateProcedure.To1_3Gate(blockState, connectionFaceB);
	}

	@Override
	public InteractionResult use(BlockState blockstate, Level world, BlockPos pos, Player entity, InteractionHand hand, BlockHitResult hit) {
		InteractionResult interactionResult = super.use(blockstate, world, pos, entity, hand, hit);
		if(interactionResult == InteractionResult.FAIL){return interactionResult;}
		OnBlockRightClickedProcedure.execute(world, pos, blockstate);
		return InteractionResult.SUCCESS;
	}

	@Override
	public void neighborChanged(BlockState blockstate, Level world, BlockPos pos, Block neighborBlock, BlockPos fromPos, boolean moving) {
		super.neighborChanged(blockstate, world, pos, neighborBlock, fromPos, moving);
	}

	@Override
	public int onRedstoneUpdate(LevelAccessor world, BlockState thisState, BlockPos thisPos, BlockPos fromPos){
		Direction updateDirection = BlockFrameTransformUtils.directionFromPositions(fromPos, thisPos);
		Direction direction = BlockFrameTransformUtils.getLocalDirectionFromWorld(thisState, updateDirection);
		if(direction.getAxis() == Direction.Axis.Z && !breakParallelLine(world, thisState, thisPos, updateDirection, false)){
			sendRedstoneUpdateInDirection(world, thisState.getBlock(), thisPos, updateDirection);
		}
		return onRedstoneUpdate(world, thisState, thisPos);
	}

	@Override
	public int oldVersion(Level level, BlockPos pos) {
		BlockState blockState = level.getBlockState(pos);
		if(blockState.getBlock() instanceof ParallelLineOutputBlock pl){
			int connection = blockState.getValue(CONNECTION);
			connection++;
			int newConnection = 0;
			if((connection & 4) > 0)
				newConnection |= 1;
			if((connection & 2) > 0)
				newConnection |= 2;
			if((connection & 1) > 0)
				newConnection |= 4;
			newConnection--;
			blockState = blockState.setValue(CONNECTION, newConnection);
			level.setBlock(pos, blockState, 2);
			return 1;
		}
		return 0;
	}
}
