
package net.acodonic_king.redstonecg.block.parallel.hybrid;

import net.acodonic_king.redstonecg.block.entity.DefaultAnalogGateBlockEntity;
import net.acodonic_king.redstonecg.procedures.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class ParallelOneWayThroughNotGateBlock extends ParallelOneWayThroughGateBlock {
	public static final BooleanProperty ENABLED = BooleanProperty.create("enabled");
	public ParallelOneWayThroughNotGateBlock() {
		super();
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
		thisState = thisState.setValue(ENABLED, BSignal == 0);
		world.setBlock(thisPos, thisState, 2);
		if(BSignal > 0){ ASignal = 0; }
		if (world.getBlockEntity(thisPos) instanceof DefaultAnalogGateBlockEntity be) {
			be.POWER = ASignal;
			be.setChanged();
			sendRedstoneUpdateInDirection(world, thisState.getBlock(), thisPos, connectionFaceA.FACE.getOpposite());
		}
		return 0;
	}
}
