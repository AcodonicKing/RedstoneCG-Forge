package net.acodonic_king.redstonecg.procedures;

import net.acodonic_king.redstonecg.init.RedstonecgModVersionRides;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class GateBlockValidPlacementConditionProcedure {
	/*public static boolean floor(LevelAccessor world, BlockPos pos) {
		return RedstonecgModVersionRides.isBlockStateHardSolid(
				world.getBlockState(
						pos.offset(0,-1,0)
				)
		);
	}
	public static boolean wall(LevelAccessor world, BlockPos pos, Direction direction) {
		BlockPos Npos = pos.relative(direction);
		BlockState blockState = world.getBlockState(Npos);
		return RedstonecgModVersionRides.isBlockStateHardSolid(blockState);
	}*/
	public static boolean execute(LevelAccessor world, BlockPos pos, Direction direction) {
		BlockPos Npos = pos.relative(direction);
		BlockState blockState = world.getBlockState(Npos);
		return RedstonecgModVersionRides.isBlockStateHardSolid(blockState);
	}
}
