
package net.acodonic_king.redstonecg.block.normal.digital;

import net.acodonic_king.redstonecg.block.defaults.DefaultDigitalInteractable2ABGate;
import net.acodonic_king.redstonecg.procedures.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;


public class DLatchBlock extends DefaultDigitalInteractable2ABGate {
	public DLatchBlock() {
		super();
	}
	@Override
	public int onRedstoneUpdate(LevelAccessor world, BlockState blockState, BlockPos pos, int recursion){
        Direction[] Sides = GetGateInputSidesProcedure.Get2ABGateForth(blockState);

		int[] power = {0,0};
		int i = 0;
		for(Direction side: Sides){
			ConnectionFace thisFace = BlockFrameTransformUtils.getConnectionFace(blockState, side);
			power[i] = GetRedstoneSignalProcedure.execute(world, pos, thisFace);
			i++;
		}
		//RedstonecgMod.LOGGER.debug("{} {}", power[0], power[1]);

		if (power[1] > 0) {
			setOutput(world, blockState, pos, power[0] > 0, recursion);
		}

		return 0;
	}
}
