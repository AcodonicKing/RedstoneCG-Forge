package net.acodonic_king.redstonecg.procedures;

import net.acodonic_king.redstonecg.block.defaults.PinMarkConnectionInterface;
import net.acodonic_king.redstonecg.block.defaults.PrimarySecondaryDirectionBlockInterface;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.apache.commons.lang3.tuple.Pair;
import static net.acodonic_king.redstonecg.procedures.ConnectionFacePrimaryRange.*;

public class CanConnectWallGateProcedure {
    public static int getConnection(BlockState blockState){
        if(blockState.getBlock() instanceof PinMarkConnectionInterface b)
            return b.getConnection(blockState);
        return LittleTools.getIntegerProperty(blockState, "connection");
    }
    public static boolean execute(BlockState blockState, int filter, byte callConnectionFace){
        Pair<Direction, Direction> directions = BlockFrameTransformUtils.getPrimarySecondaryDirections(blockState);
        return execute(directions, filter, callConnectionFace);
        /*Property<?> _prop = blockState.getBlock().getStateDefinition().getProperty("rotation");
        Direction primary, secondary;
        if(_prop instanceof DirectionProperty _dp){
            primary = blockState.getValue(_dp);
            secondary = LittleTools.getDirection(blockState);
        } else {
            primary = LittleTools.getDirection(blockState);
            secondary = Direction.DOWN;
        }
        return canConnectAllow(primitive(secondary), callConnectionFace, rotateFilter(filter, primary));*/
    }
    public static boolean execute(Pair<Direction,Direction> dirs, int filter, byte callConnectionFace){
        return execute(dirs.getLeft(), dirs.getRight(), filter, callConnectionFace);
    }
    public static boolean execute(Direction primary, Direction secondary, int filter, byte callConnectionFace){
        return canConnectAllow(primitive(secondary), callConnectionFace, rotateFilter(filter, primary));
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
        return connection & 0b1111;
    }
    public static int To1_3GateConnectionFilter(int connection){
        connection ++;
        connection <<= 1;
        return connection;
    }
    public static boolean To1Gate(BlockState blockstate, byte callConnectionFace){
        int filter = To1GateConnectionFilter(getConnection(blockstate));
        return execute(blockstate, filter, callConnectionFace);
    }
    public static boolean To1Gate(Direction primary, Direction secondary, int connection, byte callConnectionFace){
        int filter = To1GateConnectionFilter(connection);
        return execute(primary, secondary, filter, callConnectionFace);
    }
    public static boolean To2Gate(BlockState blockstate, byte callConnectionFace){
        int filter = To2GateConnectionFilter(getConnection(blockstate));
        return execute(blockstate, filter, callConnectionFace);
    }
    public static boolean To2Gate(Direction primary, Direction secondary, int connection, byte callConnectionFace){
        int filter = To2GateConnectionFilter(connection);
        return execute(primary, secondary, filter, callConnectionFace);
    }
    public static boolean To2TGate(BlockState blockstate, byte callConnectionFace){
        int filter = To2TGateConnectionFilter(getConnection(blockstate));
        return execute(blockstate, filter, callConnectionFace);
    }
    public static boolean To2TGate(Direction primary, Direction secondary, int connection, byte callConnectionFace){
        int filter = To2TGateConnectionFilter(connection);
        return execute(primary, secondary, filter, callConnectionFace);
    }
    public static boolean To2ABGate(BlockState blockstate, byte callConnectionFace){
        int filter = To2ABGateConnectionFilter(getConnection(blockstate));
        return execute(blockstate, filter, callConnectionFace);
    }
    public static boolean To2ABGate(Direction primary, Direction secondary, int connection, byte callConnectionFace){
        int filter = To2ABGateConnectionFilter(connection);
        return execute(primary, secondary, filter, callConnectionFace);
    }
    public static boolean To3Gate(BlockState blockstate, byte callConnectionFace){
        int filter = To3GateConnectionFilter(0);
        return execute(blockstate, filter, callConnectionFace);
    }
    public static boolean To3Gate(Direction primary, Direction secondary, int connection, byte callConnectionFace){
        int filter = To3GateConnectionFilter(connection);
        return execute(primary, secondary, filter, callConnectionFace);
    }
    public static boolean To3ABCGate(BlockState blockstate, byte callConnectionFace){
        int filter = To3ABCGateConnectionFilter(getConnection(blockstate));
        return execute(blockstate, filter, callConnectionFace);
    }
    public static boolean To3ABCGate(Direction primary, Direction secondary, int connection, byte callConnectionFace){
        int filter = To3ABCGateConnectionFilter(connection);
        return execute(primary, secondary, filter, callConnectionFace);
    }
    public static boolean To4Gate(Direction secondary, byte callConnectionFace){
        return canConnect(primitive(secondary), callConnectionFace);
        /*ConnectionFacePrimaryRange connectionFacePrimaryRange = new ConnectionFacePrimaryRange(Secondary);
        return connectionFacePrimaryRange.canConnect(callConnectionFace);*/
    }
    public static boolean To4Gate(BlockState blockstate, byte callConnectionFace){
        Direction secondary;
        if(blockstate.getBlock() instanceof PrimarySecondaryDirectionBlockInterface b)
            secondary = b.getSecondaryDirection(blockstate);
        else
            secondary = LittleTools.getDirection(blockstate);
        return To4Gate(secondary, callConnectionFace);
    }
    public static boolean To1_4Gate(BlockState blockstate, byte callConnectionFace){
        int filter = To1_4GateConnectionFilter(getConnection(blockstate));
        return execute(blockstate, filter, callConnectionFace);
    }
    public static boolean To1_4Gate(Direction primary, Direction secondary, int connection, byte callConnectionFace){
        int filter = To1_4GateConnectionFilter(connection);
        return execute(primary, secondary, filter, callConnectionFace);
    }
    public static boolean To1_3Gate(BlockState blockstate,  byte callConnectionFace){
        int filter = To1_3GateConnectionFilter(getConnection(blockstate));
        return execute(blockstate, filter, callConnectionFace);
    }
    public static boolean To1_3Gate(Direction primary, Direction secondary, int connection, byte callConnectionFace){
        int filter = To1_3GateConnectionFilter(connection);
        return execute(primary, secondary, filter, callConnectionFace);
    }
}
