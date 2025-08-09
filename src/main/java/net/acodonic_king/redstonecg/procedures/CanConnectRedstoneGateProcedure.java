package net.acodonic_king.redstonecg.procedures;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class CanConnectRedstoneGateProcedure {
    public static boolean To1Gate(BlockState blockstate, Direction direction){
        Direction block_facing = LittleTools.getDirection(blockstate);
        if (direction == (block_facing.getOpposite())){
            return true;
        }
        return switch (LittleTools.getIntegerProperty(blockstate, "connection")) {
            case (0) -> direction == block_facing;
            case (1) -> direction == (block_facing.getClockWise(Direction.Axis.Y));
            case (2) -> direction == (block_facing.getCounterClockWise(Direction.Axis.Y));
            default -> false;
        };
    }
    public static boolean To2Gate(BlockState blockstate, Direction direction){
        Direction block_facing = LittleTools.getDirection(blockstate);
        return switch (LittleTools.getIntegerProperty(blockstate, "connection")) {
            case (0) -> direction != block_facing;
            case (1) -> direction != (block_facing.getCounterClockWise(Direction.Axis.Y));
            case (2) -> direction != (block_facing.getClockWise(Direction.Axis.Y));
            default -> false;
        };
    }
    public static boolean To2ABGate(BlockState blockstate, Direction direction){
        Direction block_facing = LittleTools.getDirection(blockstate);
        if (direction == block_facing.getOpposite()){
            return true;
        }
        int connection = LittleTools.getIntegerProperty(blockstate, "connection");
        Direction SideA = Direction.UP;
        Direction SideB = Direction.UP;
        if (connection < 2) {
            SideA = block_facing;
        } else if (connection < 4) {
            SideA = block_facing.getClockWise(Direction.Axis.Y);
        } else {
            SideA = block_facing.getCounterClockWise(Direction.Axis.Y);
        }
        if (connection == 5 || connection == 0){
            SideB = block_facing.getClockWise(Direction.Axis.Y);
        } else if (connection < 3){
            SideB = block_facing.getCounterClockWise(Direction.Axis.Y);
        } else {
            SideB = block_facing;
        }
        return SideA == direction || SideB == direction;
    }
    public static boolean To1_4Gate(BlockState blockstate, Direction direction){
        int connection = LittleTools.getIntegerProperty(blockstate, "connection");
        Direction block_facing = LittleTools.getDirection(blockstate);
        boolean output = false;
        if (connection > 2 && connection < 7 || connection > 10) {
            output = direction == block_facing;
        }
        if (connection > 6) {
            output = output || direction == (block_facing.getClockWise(Direction.Axis.Y));
        }
        connection &= 3;
        if(connection == 0 || connection == 2){
            output = output || direction == (block_facing.getOpposite());
        }
        if(connection == 1 || connection == 2){
            output = output || direction == (block_facing.getCounterClockWise(Direction.Axis.Y));
        }
        return output;
    }
    public static boolean To1_3Gate(BlockState blockstate, Direction direction){
        Direction block_facing = LittleTools.getDirection(blockstate);
        int connection = LittleTools.getIntegerProperty(blockstate, "connection");
        if (block_facing.getClockWise(Direction.Axis.Y) == direction) {
            return ((connection & 3) == 0) || ((connection & 3) == 2);
        } else if (block_facing == direction) {
            return !(connection == 0 || connection == 3 || connection == 4);
        } else if (block_facing.getCounterClockWise(Direction.Axis.Y) == direction) {
            return connection > 2;
        }
        return false;
    }
}
