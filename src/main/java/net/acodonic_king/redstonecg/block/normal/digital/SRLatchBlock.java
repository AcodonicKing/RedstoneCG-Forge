
package net.acodonic_king.redstonecg.block.normal.digital;

import net.acodonic_king.redstonecg.block.defaults.DefaultDigitalInteractable2ABGate;
import net.acodonic_king.redstonecg.procedures.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

public class SRLatchBlock extends DefaultDigitalInteractable2ABGate {
	public SRLatchBlock() {
		super();
	}
	@Override
	public int onRedstoneUpdate(LevelAccessor world, BlockState blockState, BlockPos pos, int recursion){
        Direction[] Sides = GetGateInputSidesProcedure.Get2ABGateForth(blockState);

		int[] power = {0,0};
		int i = 0;
		for(Direction side: Sides){
			byte thisFace = BlockFrameTransformUtils.getConnectionFace(blockState, side);
			power[i] = GetRedstoneSignalProcedure.execute(world, pos, thisFace);
			i++;
		}

		if (power[1] > 0) {
			setOutput(world, blockState, pos,false, recursion);
			return 0;
		}

		if (power[0] > 0) {
			setOutput(world, blockState, pos,true, recursion);
			return 15;
		}
		return 0;
	}
}
