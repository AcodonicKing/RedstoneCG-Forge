
package net.acodonic_king.redstonecg.block.parallel.analog;

import net.acodonic_king.redstonecg.block.defaults.DefaultParallelAnalogInteractableABGate;
import net.acodonic_king.redstonecg.block.entity.DefaultAnalogGateBlockEntity;
import net.acodonic_king.redstonecg.procedures.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

public class ParallelAnalogMemoryBlock extends DefaultParallelAnalogInteractableABGate {
	public ParallelAnalogMemoryBlock() {
		super();
	}

	@Override
	public int onRedstoneUpdate(LevelAccessor world, BlockState thisState, BlockPos thisPos){
		int linePower = GetParallelSignalProcedure.getParallelLinePower(world, thisPos);
		ConnectionFace connectionFaceA = BlockFrameTransformUtils.getConnectionFace(thisState, Direction.SOUTH);
		int backPower = GetRedstoneSignalProcedure.execute(world, thisPos, connectionFaceA);
		int ASignal = linePower;
		int BSignal = backPower;
		if(thisState.getValue(CONNECTION) == 0){
			ASignal = backPower;
			BSignal = linePower;
		}
		if(BSignal > 0) {
			if (world.getBlockEntity(thisPos) instanceof DefaultAnalogGateBlockEntity be) {
				if (be.POWER == ASignal) {
					return 0;
				}
				be.POWER = ASignal;
				be.setChanged();
				updateVisibleState((Level) world, thisPos, thisState, ASignal);
				sendRedstoneUpdateInDirection(world, thisState.getBlock(), thisPos, connectionFaceA.FACE.getOpposite());
			}
		}
		return 0;
	}
}
