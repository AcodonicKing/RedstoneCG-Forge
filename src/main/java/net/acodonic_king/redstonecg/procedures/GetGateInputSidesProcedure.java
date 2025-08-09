package net.acodonic_king.redstonecg.procedures;

import net.minecraft.core.NonNullList;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;

public class GetGateInputSidesProcedure {
	public static Direction Get1Gate(BlockState blockstate){
		return Get1Gate(
				LittleTools.getDirection(blockstate),
				LittleTools.getIntegerProperty(blockstate, "connection")
		);
    }
	public static Direction Get1GateForth(BlockState blockstate){
		return Get1Gate(
				Direction.NORTH,
				LittleTools.getIntegerProperty(blockstate, "connection")
		);
	}
	public static Direction Get1Gate(Direction forth_direction, int connection){
		return switch (connection) {
			case (0) -> forth_direction.getOpposite();
			case (1) -> forth_direction.getCounterClockWise(Direction.Axis.Y);
			case (2) -> forth_direction.getClockWise(Direction.Axis.Y);
			default -> Direction.UP;
		};
	}
	public static Direction[] Get2Gate(BlockState blockstate){
		return Get2Gate(
				LittleTools.getDirection(blockstate),
				LittleTools.getIntegerProperty(blockstate, "connection")
		);
	}
	public static Direction[] Get2GateForth(BlockState blockstate){
		return Get2Gate(
				Direction.NORTH,
				LittleTools.getIntegerProperty(blockstate, "connection")
		);
	}
	public static Direction[] Get2Gate(Direction forth_direction, int connection){
		return switch (connection) {
			case (0) -> new Direction[]{forth_direction.getCounterClockWise(Direction.Axis.Y), forth_direction.getClockWise(Direction.Axis.Y)};
			case (1) -> new Direction[]{forth_direction.getOpposite(), forth_direction.getCounterClockWise(Direction.Axis.Y)};
			case (2) -> new Direction[]{forth_direction.getClockWise(Direction.Axis.Y), forth_direction.getOpposite()};
			default -> new Direction[]{Direction.UP, Direction.UP};
		};
	}
	public static Direction[] Get2ABGate(BlockState blockstate){
		return Get2ABGate(
				LittleTools.getDirection(blockstate),
				LittleTools.getIntegerProperty(blockstate, "connection")
		);
	}
	public static Direction[] Get2ABGateForth(BlockState blockstate){
		return Get2ABGate(
				Direction.NORTH,
				LittleTools.getIntegerProperty(blockstate, "connection")
		);
	}
	public static Direction[] Get2ABGate(Direction forth_direction, int connection){
        Direction SideA = Direction.UP;
		Direction SideB = Direction.UP;
		if (connection < 2) {
			SideA = forth_direction.getOpposite();
		} else if (connection < 4) {
			SideA = forth_direction.getCounterClockWise(Direction.Axis.Y);
		} else {
			SideA = forth_direction.getClockWise(Direction.Axis.Y);
		}
		if (connection == 5 || connection == 0){
			SideB = forth_direction.getCounterClockWise(Direction.Axis.Y);
		} else if (connection < 3){
			SideB = forth_direction.getClockWise(Direction.Axis.Y);
		} else {
			SideB = forth_direction.getOpposite();
		}
		return new Direction[]{SideA, SideB};
	}
	public static Direction[] Get3ABCGate(BlockState blockstate){
		return Get3ABCGate(
				LittleTools.getDirection(blockstate),
				LittleTools.getIntegerProperty(blockstate, "connection")
		);
	}
	public static Direction[] Get3ABCGateForth(BlockState blockstate){
		return Get3ABCGate(
				Direction.NORTH,
				LittleTools.getIntegerProperty(blockstate, "connection")
		);
	}
	public static Direction[] Get3ABCGate(Direction forth_direction, int connection){
        Direction SideA = Direction.UP;
		Direction SideB = Direction.UP;
		if (connection < 2) {
			SideA = forth_direction.getOpposite();
		} else if (connection < 4) {
			SideA = forth_direction.getCounterClockWise(Direction.Axis.Y);
		} else {
			SideA = forth_direction.getClockWise(Direction.Axis.Y);
		}
		if (connection == 5 || connection == 0){
			SideB = forth_direction.getCounterClockWise(Direction.Axis.Y);
		} else if (connection < 3){
			SideB = forth_direction.getClockWise(Direction.Axis.Y);
		} else {
			SideB = forth_direction.getOpposite();
		}
		connection = connection > 2 ? connection - 3 : connection;
		Direction SideC = switch(connection){
			case(0) -> forth_direction.getClockWise(Direction.Axis.Y);
			case(1) -> forth_direction.getCounterClockWise(Direction.Axis.Y);
			case(2) -> forth_direction.getOpposite();
			default -> Direction.UP;
		};
		return new Direction[]{SideA, SideB, SideC};
	}
	public static NonNullList<Direction> Get1_4Gate(BlockState blockstate){
		return Get1_4Gate(
				LittleTools.getDirection(blockstate),
				LittleTools.getIntegerProperty(blockstate, "connection")
		);
	}
	public static NonNullList<Direction> Get1_4GateForth(BlockState blockstate){
		return Get1_4Gate(
				Direction.NORTH,
				LittleTools.getIntegerProperty(blockstate, "connection")
		);
	}
	public static NonNullList<Direction> Get1_4Gate(Direction forth_direction, int connection){
		NonNullList<Direction> out = NonNullList.create();
		int connection3bit = connection & 3;
		if (connection3bit == 0 || connection3bit == 2) {
			out.add(forth_direction);
		}
		if (connection3bit == 1 || connection3bit == 2) {
			out.add(forth_direction.getClockWise(Direction.Axis.Y));
		}
		if (connection > 2 && connection < 7 || (connection > 10)) {
			out.add(forth_direction.getOpposite());
		}
		if (connection > 6) {
			out.add(forth_direction.getCounterClockWise(Direction.Axis.Y));
		}
		return out;
	}
	public static NonNullList<Direction> Get1_3Gate(BlockState blockstate){
		return Get1_3Gate(
				LittleTools.getDirection(blockstate),
				LittleTools.getIntegerProperty(blockstate, "connection")
		);
	}
	public static NonNullList<Direction> Get1_3GateForth(BlockState blockstate){
		return Get1_3Gate(
				Direction.NORTH,
				LittleTools.getIntegerProperty(blockstate, "connection")
		);
	}
	public static NonNullList<Direction> Get1_3Gate(Direction forth_direction, int connection){
        NonNullList<Direction> out = NonNullList.create();
		int connection3bit = connection & 3;
		if((connection3bit == 0) || (connection3bit == 2)){
			out.add(forth_direction.getCounterClockWise(Direction.Axis.Y));
		}
		if(!(connection == 0 || connection == 3 || connection == 4)){
			out.add(forth_direction.getOpposite());
		}
		if(connection > 2){
			out.add(forth_direction.getClockWise(Direction.Axis.Y));
		}
		return out;
	}
}
