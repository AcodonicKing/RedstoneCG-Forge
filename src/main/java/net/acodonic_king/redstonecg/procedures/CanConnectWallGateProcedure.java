package net.acodonic_king.redstonecg.procedures;

import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.apache.commons.lang3.tuple.Pair;

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
    public static boolean execute(Pair<Direction,Direction> dirs, int filter, ConnectionFace callConnectionFace){
        return execute(dirs.getLeft(), dirs.getRight(), filter, callConnectionFace);
    }
    public static boolean execute(Direction primary, Direction secondary, int filter, ConnectionFace callConnectionFace){
        ConnectionFacePrimaryRange connectionFaceRange = new ConnectionFacePrimaryRange(secondary);
        filter = ConnectionFacePrimaryRange.rotateFilter(filter, primary);
        return connectionFaceRange.canConnectAvoid(callConnectionFace, filter);
    }
    public static int To1GateConnectionFilter(int connection){
        connection = 4 << connection;
        if(connection == 16){connection = 2;}
        connection |= 1;
        return connection;
    }
    public static int To2GateConnectionFilter(int connection){
        connection = switch (connection) {
            case (0) -> 0b1011;
            case (1) -> 0b1101;
            case (2) -> 0b0111;
            default -> 0b0000;
        };
        return connection;
    }
    public static int To2TGateConnectionFilter(int connection){
        connection = switch (connection) {
            case (0) -> 0b1011;
            case (1) -> 0b1101;
            case (2) -> 0b0111;
            case (3) -> 0b1111;
            default -> 0b0000;
        };
        return connection;
    }
    public static int To2ABGateConnectionFilter(int connection){
        connection = (connection > 2) ? (connection - 3) : connection;
        connection = switch (connection) {
            case (0) -> 0b1101;
            case (1) -> 0b0111;
            case (2) -> 0b1011;
            default -> 0b0000;
        };
        return connection;
    }
    public static int To3GateConnectionFilter(int connection){
        return 0b1111;
    }
    public static int To3ABCGateConnectionFilter(int connection){
        return 0b1111;
    }
    public static int To1_4GateConnectionFilter(int connection){
        connection ++;
        return connection;
    }
    public static int To1_3GateConnectionFilter(int connection){
        connection ++;
        connection <<= 1;
        return connection;
    }
    public static boolean To1Gate(BlockState blockstate, ConnectionFace callConnectionFace){
        int connection = To1GateConnectionFilter(LittleTools.getIntegerProperty(blockstate, "connection"));
        return execute(blockstate, connection, callConnectionFace);
    }
    public static boolean To2Gate(BlockState blockstate, ConnectionFace callConnectionFace){
        int connection = To2GateConnectionFilter(LittleTools.getIntegerProperty(blockstate, "connection"));
        return execute(blockstate, connection, callConnectionFace);
    }
    public static boolean To2TGate(BlockState blockstate, ConnectionFace callConnectionFace){
        int connection = To2TGateConnectionFilter(LittleTools.getIntegerProperty(blockstate, "connection"));
        return execute(blockstate, connection, callConnectionFace);
    }
    public static boolean To2ABGate(BlockState blockstate, ConnectionFace callConnectionFace){
        int connection = To2ABGateConnectionFilter(LittleTools.getIntegerProperty(blockstate, "connection"));
        return execute(blockstate, connection, callConnectionFace);
    }
    public static boolean To3Gate(BlockState blockstate, ConnectionFace callConnectionFace){
        int connection = To3GateConnectionFilter(0);
        return execute(blockstate, connection, callConnectionFace);
    }
    public static boolean To3ABCGate(BlockState blockstate, ConnectionFace callConnectionFace){
        int connection = To3ABCGateConnectionFilter(LittleTools.getIntegerProperty(blockstate, "connection"));
        return execute(blockstate, connection, callConnectionFace);
    }
    public static boolean To4Gate(Direction Secondary, ConnectionFace callConnectionFace){
        ConnectionFacePrimaryRange connectionFacePrimaryRange = new ConnectionFacePrimaryRange(Secondary);
        return connectionFacePrimaryRange.canConnect(callConnectionFace);
    }
    public static boolean To4Gate(BlockState blockstate, ConnectionFace callConnectionFace){
        Direction Secondary = LittleTools.getDirection(blockstate);
        return To4Gate(Secondary, callConnectionFace);
    }
    public static boolean To1_4Gate(BlockState blockstate, ConnectionFace callConnectionFace){
        int connection = To1_4GateConnectionFilter(LittleTools.getIntegerProperty(blockstate, "connection"));
        return execute(blockstate, connection, callConnectionFace);
    }
    public static boolean To1_3Gate(BlockState blockstate,  ConnectionFace callConnectionFace){
        int connection = To1_3GateConnectionFilter(LittleTools.getIntegerProperty(blockstate, "connection"));
        return execute(blockstate, connection, callConnectionFace);
    }
}
