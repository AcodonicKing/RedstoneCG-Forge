
package net.acodonic_king.redstonecg.block.parallel.hybrid;

import net.acodonic_king.redstonecg.block.defaults.DefaultParallelGateWithAlternate;
import net.acodonic_king.redstonecg.block.defaults.DefaultRedstoneActionGate;
import net.acodonic_king.redstonecg.block.entity.DefaultAnalogGateBlockEntity;
import net.acodonic_king.redstonecg.procedures.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class ParallelPathSelectorBlock extends DefaultParallelGateWithAlternate implements EntityBlock {
	public static final BooleanProperty VISIBLE_STATE = BooleanProperty.create("visible_state");
	public static final IntegerProperty CONNECTION = IntegerProperty.create("connection",0,3);
	public static final BooleanProperty DIRECTION = BooleanProperty.create("direction");

	public ParallelPathSelectorBlock() {
		super();
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new DefaultAnalogGateBlockEntity(pos, state);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(VISIBLE_STATE, CONNECTION, DIRECTION);
	}

	@Override
	public int onRedstoneUpdate(LevelAccessor world, BlockState thisState, BlockPos thisPos){
		int linePower = GetParallelSignalProcedure.getParallelLinePower(world, thisPos);
		ConnectionFace connectionFaceA = BlockFrameTransformUtils.getConnectionFace(thisState, Direction.NORTH);
		int backPower = GetRedstoneSignalProcedure.execute(world, thisPos, connectionFaceA);
		int Changed = (((linePower > 0) != thisState.getValue(DIRECTION)) ? 1 : 0);
		//RedstonecgMod.LOGGER.debug("Received update {} {} {} {} {}", thisPos, linePower, backPower, thisState.getValue(DIRECTION), Changed);
		if((Changed & 1) > 0){
			thisState = thisState.setValue(DIRECTION, (linePower > 0));
		}
		if(world.getBlockEntity(thisPos) instanceof DefaultAnalogGateBlockEntity be){
			if(be.POWER != backPower){
				Changed |= 2;
				be.POWER = backPower;
				be.setChanged();
				thisState = thisState.setValue(VISIBLE_STATE, backPower > 0);
			}
		}
		if(Changed > 0) {
			//RedstonecgMod.LOGGER.debug("Block updated to {}", thisState);
			world.setBlock(thisPos, thisState, 2);
			sendRedstoneUpdateInDirection(world, thisState.getBlock(), thisPos, connectionFaceA.FACE.getOpposite());
            Direction alternateDirection = getAlternateReadDirection(thisState, thisState.getValue(CONNECTION));
            BlockPos targetPos = thisPos.relative(alternateDirection.getOpposite());
            updateAlternate(world, thisPos, targetPos);
        }
		return 0;
	}

	public void updateAlternate(LevelAccessor world, BlockPos thisPos, BlockPos targetPos){
		BlockState targetState = world.getBlockState(targetPos);
		if(targetState.getBlock() instanceof ParallelPathSelectorBlock block){
			block.receiveAlternateUpdate(world,targetState,targetPos,thisPos);
		}
	}

	public void receiveAlternateUpdate(LevelAccessor world, BlockState thisState, BlockPos thisPos, BlockPos fromPos){
		int connection = thisState.getValue(CONNECTION); //Where pin A and pin B?
		boolean direction = thisState.getValue(DIRECTION); //Alternate goes to pin B (false) or pin A (true)?
		boolean pinIsAlternate = direction ? (connection > 1) : (connection < 2);
		if(pinIsAlternate){
			Direction alternateDirection = getAlternateReadDirection(thisState, connection).getOpposite();
			BlockPos targetPos = thisPos.relative(alternateDirection);
			if(targetPos == fromPos){ return; }
			updateAlternate(world, thisPos, targetPos);
		} else {
			Direction updateDirection = BlockFrameTransformUtils.getWorldDirectionFromLocal(thisState, Direction.SOUTH);
			sendRedstoneUpdateInDirection(world, thisState.getBlock(), thisPos, updateDirection);
		}
	}

	public Direction getAlternateReadDirection(BlockState thisState, int connection){
		Direction alternateDirection = ((connection & 1) > 0) ? Direction.WEST : Direction.EAST;
		return BlockFrameTransformUtils.getWorldDirectionFromLocal(thisState, alternateDirection);
	}

	/*@Override
	public int getRedstonePower(LevelAccessor world, BlockState thisState, BlockPos thisPos){
		int connection = thisState.getValue(CONNECTION);
		boolean direction = thisState.getValue(DIRECTION);
		boolean isB = (connection & 2) > 0;
		if(isB == direction){
			if(world.getBlockEntity(thisPos) instanceof DefaultAnalogGateBlockEntity be){
				return be.POWER;
			}
		} else {
			Direction alternateDirection = getAlternateReadDirection(thisState, connection);
			return getAlternateInput(world, thisPos, alternateDirection);
		}
		return 0;
	}*/

	@Override
	public int giveAlternateInput(LevelAccessor world, BlockPos thisPos, BlockState thisState, Direction side) {
		Direction localSide = BlockFrameTransformUtils.getLocalDirectionFromWorld(thisState, side);
		if(localSide.getAxis() != Direction.Axis.X){return 0;}
		int connection = thisState.getValue(CONNECTION);
		Direction alternateDirection = ((connection & 1) > 0) ? Direction.WEST : Direction.EAST;
		if(localSide == alternateDirection){return 0;}
		boolean direction = thisState.getValue(DIRECTION);
		boolean isB = connection < 2;
		if(isB == direction){
			if(world.getBlockEntity(thisPos) instanceof DefaultAnalogGateBlockEntity be){
				return be.POWER;
			}
		} else {
			return getAlternateInput(world, thisPos, side.getOpposite());
		}
		return 0;
	}
	@Override
	public InteractionResult use(BlockState blockstate, Level world, BlockPos pos, Player entity, InteractionHand hand, BlockHitResult hit) {
		InteractionResult interactionResult = super.use(blockstate, world, pos, entity, hand, hit);
		if(interactionResult == InteractionResult.FAIL){return interactionResult;}
		OnBlockRightClickedProcedure.execute(world, pos, blockstate);
		return InteractionResult.SUCCESS;
	}
	@Override
	public int getRedstonePower(LevelAccessor world, BlockPos thisPos, ConnectionFace requesterFace){
		ConnectionFace connectionFaceA = getOutputConnectionFace(world, thisPos, requesterFace);
		if(!connectionFaceA.canConnect(requesterFace)){return 0;}
		BlockState thisState = world.getBlockState(thisPos);
		int connection = thisState.getValue(CONNECTION);
		boolean direction = thisState.getValue(DIRECTION);
		boolean isB = (connection & 2) > 0;
		if(isB == direction){
			if(world.getBlockEntity(thisPos) instanceof DefaultAnalogGateBlockEntity be){
				return be.POWER;
			}
		} else {
			Direction alternateDirection = getAlternateReadDirection(thisState, connection);
			return getAlternateInput(world, thisPos, alternateDirection);
		}
		return 0;
	}

	@Override
	public ConnectionFace getOutputConnectionFace(LevelAccessor world, BlockPos pos, ConnectionFace requesterFace) {
		return BlockFrameTransformUtils.getConnectionFace(world.getBlockState(pos), Direction.SOUTH);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState blockState = super.getStateForPlacementOpposite(context);
		return blockState;
	}
	@Override
	public boolean isOutput(LevelAccessor world, BlockState blockState, BlockPos pos, Direction direction){
		Direction dir = BlockFrameTransformUtils.getLocalDirectionFromWorld(blockState,direction);
		return dir == Direction.SOUTH;
	}
	@Override
	public String getMeasurement(LevelAccessor world, BlockState blockState, BlockPos pos){
		if(world.getBlockEntity(pos) instanceof DefaultAnalogGateBlockEntity blockEntity){
			return String.format("%1d",blockEntity.POWER);
		}
		return "";
	}
}
