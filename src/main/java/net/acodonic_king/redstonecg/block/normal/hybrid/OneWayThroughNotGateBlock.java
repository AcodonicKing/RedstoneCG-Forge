
package net.acodonic_king.redstonecg.block.normal.hybrid;

import net.acodonic_king.redstonecg.block.entity.DefaultAnalogGateBlockEntity;
import net.acodonic_king.redstonecg.procedures.*;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

public class OneWayThroughNotGateBlock extends OneWayThroughGateBlock {
	public OneWayThroughNotGateBlock() {
		super();
	}

	@Override
	public int onRedstoneUpdate(LevelAccessor world, BlockState blockState, BlockPos pos){
		Direction[] Sides = GetGateInputSidesProcedure.Get2ABGateForth(blockState);
		int SideCPower = GetRedstoneSignalProcedure.execute(world, pos, Direction.NORTH);
		int SideDPower = GetRedstoneSignalProcedure.execute(world, pos, Sides[0]);
		blockState = blockState.setValue(ENABLED, SideCPower == 0);
		if (world.getBlockEntity(pos) instanceof DefaultAnalogGateBlockEntity be){
			if(be.POWER != SideDPower){
				be.POWER = SideDPower;
				be.setChanged();
				blockState = blockState.setValue(VISIBLE_STATE, SideDPower > 0);
			}
		}
		world.setBlock(pos, blockState, 2);
		Direction direction = BlockFrameTransformUtils.getWorldDirectionFromLocal(blockState, Sides[1]);
		//RedstonecgMod.LOGGER.debug("in direction {} {} {}",Sides[1],direction,pos);
		this.sendRedstoneUpdateInDirection(world,blockState.getBlock(),pos,direction);
		if(SideCPower > 0){return SideDPower;}
		return 0;
	}
}
