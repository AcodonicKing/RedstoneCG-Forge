package net.acodonic_king.redstonecg.procedures;

import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.BlockPos;

public class OnBlockRightClickedProcedure {
	public static int execute(LevelAccessor world, BlockPos pos) {
		return execute(world, pos, world.getBlockState(pos));
	}
	public static int execute(LevelAccessor world, BlockPos pos, BlockState blockstate) {return execute(world, pos, blockstate, 3);}
	public static int execute(LevelAccessor world, BlockPos pos, BlockState blockstate, int flags) {
		if (blockstate.getBlock().getStateDefinition().getProperty("connection") instanceof IntegerProperty _integerProp) {
			int connection = blockstate.getValue(_integerProp) + 1;
			if (_integerProp.getPossibleValues().contains(connection))
			{
				world.setBlock(pos, blockstate.setValue(_integerProp, connection), flags);
				return connection;
			} else {
				world.setBlock(pos, blockstate.setValue(_integerProp, 0), flags);
			}
		}
		return 0;
	}
}
