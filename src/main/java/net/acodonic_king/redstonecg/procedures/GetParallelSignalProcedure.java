package net.acodonic_king.redstonecg.procedures;

import net.acodonic_king.redstonecg.block.defaults.DefaultParallelGate;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import org.apache.commons.lang3.tuple.Pair;

public class GetParallelSignalProcedure {
	public static Direction.Axis getParallelAxis(BlockState blockState){
		Pair<Direction,Direction> pairPS = BlockFrameTransformUtils.getPrimarySecondaryDirections(blockState);
		return getParallelAxis(pairPS);
	}
	public static Direction.Axis getParallelAxis(Pair<Direction,Direction> pairPS){
		return getParallelAxis(pairPS.getLeft(), pairPS.getRight());
	}
	public static Direction.Axis getParallelAxis(Direction primary, Direction secondary){
		return switch (secondary){
			case DOWN, UP -> switch (primary){
				case NORTH, SOUTH -> Direction.Axis.X;
				default -> Direction.Axis.Z;
			};
			case NORTH, SOUTH -> switch (primary){
				case NORTH, SOUTH -> Direction.Axis.X;
				default -> Direction.Axis.Y;
			};
			default -> switch (primary){
				case NORTH, SOUTH -> Direction.Axis.Z;
				default -> Direction.Axis.Y;
			};
		};
	}
	public static boolean onSameParallelLine(BlockState thisState, BlockState targetState){
		Pair<Direction,Direction> thisPairPS = BlockFrameTransformUtils.getPrimarySecondaryDirections(thisState);
		Pair<Direction,Direction> targetPairPS = BlockFrameTransformUtils.getPrimarySecondaryDirections(targetState);
		if(thisPairPS.getRight() != targetPairPS.getRight()){return false;}
		return getParallelAxis(thisPairPS) == getParallelAxis(targetPairPS);
	}
	public static int getParallelLinePower(LevelAccessor world, BlockPos pos){
		BlockState thisState = world.getBlockState(pos);
		Direction.Axis axis = getParallelAxis(thisState);
		int power = 0;
		for(Direction.AxisDirection axisDirection: Direction.AxisDirection.values()){
			Direction direction = Direction.fromAxisAndDirection(axis, axisDirection);
			//BlockPos targetPos = pos.relative(direction);
			int powerB = getParallelLinePowerInDirection(world, pos, direction);
			power = Math.max(power, powerB);
		}
		return power;
	}
	public static int getParallelLinePowerInDirection(LevelAccessor world, BlockPos pos, Direction direction){
		BlockState thisState = world.getBlockState(pos);
		if(thisState.getBlock() instanceof DefaultParallelGate){
			BlockPos targetPos = pos.relative(direction);
			BlockState targetState = world.getBlockState(targetPos);
			if(targetState.getBlock() instanceof DefaultParallelGate ts){
				if(ts.breakParallelLine(world, targetState, targetPos, direction, true)){
					return ts.breakParallelLineSignal(world, targetState, targetPos, direction);
				}
				if(onSameParallelLine(thisState,targetState)){
					return getParallelLinePowerInDirection(world, targetPos, direction);
				}
			}
		}
		return GetRedstoneSignalProcedure.executeWorldDirection(world, pos, direction);
	}
}
