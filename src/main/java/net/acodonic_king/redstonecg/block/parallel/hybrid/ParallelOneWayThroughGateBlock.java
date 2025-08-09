
package net.acodonic_king.redstonecg.block.parallel.hybrid;

import net.acodonic_king.redstonecg.block.defaults.DefaultParallelAnalogInteractableABGate;
import net.acodonic_king.redstonecg.block.defaults.DefaultRedstoneActionGate;
import net.acodonic_king.redstonecg.block.entity.DefaultAnalogGateBlockEntity;
import net.acodonic_king.redstonecg.procedures.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

public class ParallelOneWayThroughGateBlock extends DefaultParallelAnalogInteractableABGate {
	public static final BooleanProperty ENABLED = BooleanProperty.create("enabled");
	public ParallelOneWayThroughGateBlock() {
		super();
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(ENABLED);
	}

	@Override
	public int onRedstoneUpdate(LevelAccessor world, BlockState thisState, BlockPos thisPos){
		int linePower = GetParallelSignalProcedure.getParallelLinePower(world, thisPos);
		ConnectionFace connectionFaceA = BlockFrameTransformUtils.getConnectionFace(thisState, Direction.NORTH);
		int backPower = GetRedstoneSignalProcedure.execute(world, thisPos, connectionFaceA);
		int ASignal = linePower;
		int BSignal = backPower;
		if(thisState.getValue(CONNECTION) == 0){
			ASignal = backPower;
			BSignal = linePower;
		}
		thisState = thisState.setValue(VISIBLE_STATE, ASignal > 0);
		thisState = thisState.setValue(ENABLED, BSignal > 0);
		world.setBlock(thisPos, thisState, 2);
		if(BSignal == 0){ ASignal = 0; }
		if (world.getBlockEntity(thisPos) instanceof DefaultAnalogGateBlockEntity be) {
			be.POWER = ASignal;
			be.setChanged();
			sendRedstoneUpdateInDirection(world, thisState.getBlock(), thisPos, connectionFaceA.FACE.getOpposite());
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

}
