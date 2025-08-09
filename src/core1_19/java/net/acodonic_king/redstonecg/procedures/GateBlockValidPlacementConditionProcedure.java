package net.acodonic_king.redstonecg.procedures;

import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class GateBlockValidPlacementConditionProcedure {
	public static boolean floor(LevelAccessor world, BlockPos pos) {
		boolean BlockBelowIsSolid = false;
		/*if (world instanceof Level _level)
			_level.updateNeighborsAt(pos, _level.getBlockState(pos).getBlock());*/
		BlockBelowIsSolid = world.getBlockState(pos.offset(0,-1,0)).getMaterial().isSolid();
		return BlockBelowIsSolid;
	}
	public static boolean wall(LevelAccessor world, BlockPos pos, Direction direction) {
		BlockPos Npos = pos.offset(direction.getNormal());
		BlockState blockState = world.getBlockState(Npos);
		return blockState.getMaterial().isSolid();
	}
	public static boolean execute(LevelAccessor world, BlockPos pos, Direction direction) {
		BlockPos Npos = pos.offset(direction.getNormal());
		BlockState blockState = world.getBlockState(Npos);
		return blockState.getMaterial().isSolid();
	}
}
