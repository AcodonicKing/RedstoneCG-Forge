
package net.acodonic_king.redstonecg.block.parallel.digital;

import net.acodonic_king.redstonecg.block.defaults.DefaultParallelDigitalInteractableABGate;
import net.acodonic_king.redstonecg.block.entity.DefaultDigitalGateBlockEntity;
import net.acodonic_king.redstonecg.procedures.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class ParallelDLatchBlock extends DefaultParallelDigitalInteractableABGate {
	public ParallelDLatchBlock() {
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
			if (world.getBlockEntity(thisPos) instanceof DefaultDigitalGateBlockEntity be) {
				boolean output = ASignal > 0;
				if (be.OUTPUT == output) {
					return 0;
				}
				be.OUTPUT = output;
				be.setChanged();
				updateVisibleState((Level) world, thisPos, thisState, output);
				sendRedstoneUpdateInDirection(world, thisState.getBlock(), thisPos, connectionFaceA.FACE.getOpposite());
			}
		}
		return 0;
	}
}
