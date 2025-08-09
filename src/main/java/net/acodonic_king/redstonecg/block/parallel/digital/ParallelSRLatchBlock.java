
package net.acodonic_king.redstonecg.block.parallel.digital;

import net.acodonic_king.redstonecg.block.defaults.DefaultParallelDigitalInteractableABGate;
import net.acodonic_king.redstonecg.block.entity.DefaultDigitalGateBlockEntity;
import net.acodonic_king.redstonecg.procedures.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

public class ParallelSRLatchBlock extends DefaultParallelDigitalInteractableABGate {
	public ParallelSRLatchBlock() {
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
		if((ASignal + BSignal) > 0){
			boolean output = (BSignal == 0) && (ASignal > 0);
            if (world.getBlockEntity(thisPos) instanceof DefaultDigitalGateBlockEntity be) {
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
