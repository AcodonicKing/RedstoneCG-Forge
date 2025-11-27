package net.acodonic_king.redstonecg.procedures;

import net.acodonic_king.redstonecg.block.defaults.DefaultParallelGate;
import net.acodonic_king.redstonecg.block.defaults.ParallelGateInterface;
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
	public static boolean onSameParallelLine(LevelAccessor world, BlockPos thisPos, BlockPos targetPos){
		Pair<Direction,Direction> thisPairPS = BlockFrameTransformUtils.getPrimarySecondaryDirections(world, thisPos);
		Pair<Direction,Direction> targetPairPS = BlockFrameTransformUtils.getPrimarySecondaryDirections(world, targetPos);
		if(thisPairPS.getRight() != targetPairPS.getRight()){return false;}
		return getParallelAxis(thisPairPS) == getParallelAxis(targetPairPS);
	}
	public static int getParallelLinePower(LevelAccessor world, BlockPos pos, Pair<Direction,Direction> pairPS){
		Direction.Axis axis = getParallelAxis(pairPS);
		int power = 0;
		for(Direction.AxisDirection axisDirection: Direction.AxisDirection.values()){
			Direction direction = Direction.fromAxisAndDirection(axis, axisDirection);
			//BlockPos targetPos = pos.relative(direction);
			int powerB = getParallelLinePowerInDirection(world, pos, direction);
			power = Math.max(power, powerB);
		}
		return power;
	}
	public static int getParallelLinePower(LevelAccessor world, BlockPos pos){
		return getParallelLinePower(world, pos, BlockFrameTransformUtils.getPrimarySecondaryDirections(world, pos));
	}
	public static int getParallelLinePowerInDirection(LevelAccessor world, BlockPos pos, Direction direction){
		BlockState thisState = world.getBlockState(pos);
		if(thisState.getBlock() instanceof ParallelGateInterface){
			BlockPos targetPos = pos.relative(direction);
			BlockState targetState = world.getBlockState(targetPos);
			if(targetState.getBlock() instanceof ParallelGateInterface ts){
				if(ts.breakParallelLine(world, targetState, targetPos, direction, true)){
					return ts.breakParallelLineSignal(world, targetState, targetPos, direction);
				}
				if(onSameParallelLine(world, pos, targetPos)){
					return getParallelLinePowerInDirection(world, targetPos, direction);
				}
			}
		}
		return GetRedstoneSignalProcedure.executeWorldDirection(world, pos, direction);
	}
}
