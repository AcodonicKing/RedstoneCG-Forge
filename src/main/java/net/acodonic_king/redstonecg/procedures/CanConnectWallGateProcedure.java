package net.acodonic_king.redstonecg.procedures;

import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class CanConnectWallGateProcedure {
    public static boolean execute(BlockState blockState, int filter, ConnectionFace callConnectionFace){
        Property<?> _prop = blockState.getBlock().getStateDefinition().getProperty("rotation");
        Direction primary, secondary;
        if(_prop instanceof DirectionProperty _dp){
            primary = blockState.getValue(_dp);
            secondary = LittleTools.getDirection(blockState);
        } else {
            primary = LittleTools.getDirection(blockState);
            secondary = Direction.DOWN;
        }
        ConnectionFacePrimaryRange connectionFaceRange = new ConnectionFacePrimaryRange(secondary);
        filter = ConnectionFacePrimaryRange.rotateFilter(filter, primary);
        return connectionFaceRange.canConnectAvoid(callConnectionFace, filter);
    }
    public static boolean execute(Direction primary, Direction secondary, int filter, ConnectionFace callConnectionFace){
        ConnectionFacePrimaryRange connectionFaceRange = new ConnectionFacePrimaryRange(secondary);
        filter = ConnectionFacePrimaryRange.rotateFilter(filter, primary);
        return connectionFaceRange.canConnectAvoid(callConnectionFace, filter);
    }
    public static boolean To1Gate(BlockState blockstate, ConnectionFace callConnectionFace){
        int connection = LittleTools.getIntegerProperty(blockstate, "connection");
        connection = 4 << connection;
        if(connection == 16){connection = 2;}
        connection |= 1;
        return execute(blockstate, connection, callConnectionFace);
    }
    public static boolean To2Gate(BlockState blockstate, ConnectionFace callConnectionFace){
        int connection = switch (LittleTools.getIntegerProperty(blockstate, "connection")) {
            case (0) -> 0b1011;
            case (1) -> 0b1101;
            case (2) -> 0b0111;
            default -> 0b0000;
        };
        return execute(blockstate, connection, callConnectionFace);
    }
    public static boolean To2ABGate(BlockState blockstate, ConnectionFace callConnectionFace){
        int connection = LittleTools.getIntegerProperty(blockstate, "connection");
        connection = (connection > 2) ? (connection - 3) : connection;
        connection = switch (connection) {
            case (0) -> 0b1101;
            case (1) -> 0b0111;
            case (2) -> 0b1011;
            default -> 0b0000;
        };
        return execute(blockstate, connection, callConnectionFace);
    }
    public static boolean To4Gate(BlockState blockstate, ConnectionFace callConnectionFace){
        Direction Secondary = LittleTools.getDirection(blockstate);
        ConnectionFacePrimaryRange connectionFacePrimaryRange = new ConnectionFacePrimaryRange(Secondary);
        return connectionFacePrimaryRange.canConnect(callConnectionFace);
    }
    public static boolean To1_4Gate(BlockState blockstate, ConnectionFace callConnectionFace){
        int connection = LittleTools.getIntegerProperty(blockstate, "connection");
        connection ++;
        return execute(blockstate, connection, callConnectionFace);
    }
    public static boolean To1_3Gate(BlockState blockstate,  ConnectionFace callConnectionFace){
        int connection = LittleTools.getIntegerProperty(blockstate, "connection");
        connection ++;
        connection <<= 1;
        return execute(blockstate, connection, callConnectionFace);
    }
}
