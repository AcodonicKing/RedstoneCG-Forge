package net.acodonic_king.redstonecg.procedures;

import net.acodonic_king.redstonecg.block.defaults.RedstoneSignalInterface;
import net.acodonic_king.redstonecg.init.RedstonecgModVersionRides;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.apache.commons.lang3.tuple.Pair;

public class BlockFrameTransformUtils {
    public static Direction[] all(BlockState blockState){
        Direction directionA = LittleTools.getDirection(blockState);
        Direction directionB = LittleTools.getDirectionB(blockState);
        directionB = directionB.getCounterClockWise(Direction.Axis.X);
        Direction[] directions = {Direction.NORTH,Direction.NORTH,Direction.NORTH,Direction.NORTH};
        for(int i = 0; i < 4; i++){
            directions[i] = rotateDirectionClockwiseY(directionB, directionA);
            directionB.getClockWise(Direction.Axis.Z);
        }
        return directions;
    }
    public static Pair<Direction, Direction> getPrimarySecondaryDirections(BlockState blockState) {
        Property<?> prop = blockState.getBlock().getStateDefinition().getProperty("rotation");

        Direction primary;
        Direction secondary;

        if (prop instanceof DirectionProperty dirProp) {
            primary = blockState.getValue(dirProp);
            secondary = LittleTools.getDirection(blockState);
        } else {
            primary = LittleTools.getDirection(blockState);
            secondary = Direction.DOWN;
        }

        return Pair.of(primary, secondary);
    }


    /**
     * Provides Connection Face of the requester.
     * @param world
     * @param requesterPos
     * @param sourceFace
     * @return
     */
    public static ConnectionFace getRequesterConnectionFace(LevelAccessor world, BlockPos requesterPos, ConnectionFace sourceFace, Direction direction){
        BlockState requesterState = world.getBlockState(requesterPos);
        Block requesterBlock = requesterState.getBlock();
        if(requesterBlock instanceof RedstoneSignalInterface si){
            return si.getOutputConnectionFace(world, requesterPos, sourceFace);
        }
        return getGenericTargetBlockConnectionFace(requesterState, direction);
    }

    /**
     * Returns the ConnectionFace for a given local direction of the block.
     * Converts the local frame direction (Forward/Right/Back/Left) into world direction using primary rotation.
     *
     * Local frame mapping:
     * - NORTH = Forward
     * - EAST  = Right
     * - SOUTH = Back
     * - WEST  = Left
     *
     * @param blockState The block state to extract orientation from.
     * @param localDir The local direction (relative to the block's frame).
     * @return The ConnectionFace representing that side in world coordinates.
     */
    public static ConnectionFace getConnectionFace(BlockState blockState, Direction localDir){
        Pair<Direction, Direction> dirs = getPrimarySecondaryDirections(blockState);
        return getConnectionFace(dirs.getLeft(),dirs.getRight(),localDir);
    }

    public static ConnectionFace getConnectionFaceWorldSide(BlockState blockState, Direction worldDir){
        Pair<Direction, Direction> dirs = getPrimarySecondaryDirections(blockState);
        return getConnectionFaceWorldSide(dirs.getLeft(),dirs.getRight(),worldDir);
    }

    /**
     * Returns the ConnectionFace for a given local direction of the block.
     * Converts the local frame direction (Forward/Right/Back/Left) into world direction using primary rotation.
     *
     * Local frame mapping:
     * - NORTH = Forward
     * - EAST  = Right
     * - SOUTH = Back
     * - WEST  = Left
     *
     * @param primary The primary direction defining block orientation (rotation).
     * @param secondary The secondary direction defining block orientation (facing).
     * @param localDir The local direction (relative to the block's frame).
     * @return The ConnectionFace representing that side in world coordinates.
     */
    public static ConnectionFace getConnectionFace(Direction primary, Direction secondary, Direction localDir){
        localDir = rotateDirectionClockwiseY(primary, localDir);
        return new ConnectionFace(localDir,secondary);
    }

    public static ConnectionFace getConnectionFaceWorldSide(Direction primary, Direction secondary, Direction worldDir){
        Direction localDir = getLocalDirectionFromWorld(primary, secondary, worldDir);
        return getConnectionFace(primary, secondary, localDir);
    }

    /**
     * Returns the ConnectionFace for the block's local "forward" direction.
     * In the local frame: NORTH = Forward.
     *
     * @param blockState The block state to extract orientation from.
     * @return The ConnectionFace representing the block's output direction.
     */
    public static ConnectionFace getForwardConnectionFace(BlockState blockState){
        return getConnectionFace(blockState,Direction.NORTH);
    }

    /**
     * Gets the ConnectionFace for a neighboring (target non-gate) block from the perspective of the querying block.
     * Treats most blocks as having generic connection capability.
     * Redstone wire is treated specially with channel 2 and facing down.
     *
     * @param neighborState The block state.
     * @param direction Direction from the querying block to the target block.
     * @return A simplified ConnectionFace for use in connection checking.
     */
    public static ConnectionFace getGenericTargetBlockConnectionFace(BlockState neighborState, Direction direction){
        ConnectionFace connectionFace = new ConnectionFace(direction.getOpposite());
        if (neighborState.is(Blocks.REDSTONE_WIRE)) {
            connectionFace.CHANNEL = (direction == Direction.UP || direction == Direction.DOWN) ? 5 : 2;
        }
        return connectionFace;
    }

    /*public static ConnectionFace getTargetBlockConnectionFace(LevelAccessor world, BlockPos pos, BlockState neighborState, Direction direction){
        Block block = neighborState.getBlock();
        if(block instanceof RedstoneSignalInterface gateBlock){
            return gateBlock.getAnyConnectionFace(world, pos, neighborState,direction.getOpposite());
        }
        return getGenericTargetBlockConnectionFace(neighborState, direction);
    }*/

    /**
     * Gets the ConnectionFace for a neighboring (target) block from the perspective of the querying block.
     * If the neighboring block is a connectable gate or a connectable wire, delegates to its own method.
     * Otherwise falls back to default interpretation based on block type (e.g., redstone wire).
     *
     * //@param neighborState The state of the neighboring block (the target block).
     * //@param direction The direction **from the querying block to the target block**.
     * @return The ConnectionFace that represents the target block's relevant output/input face.
     */
    public static ConnectionFace getTargetBlockConnectionFace(LevelAccessor world, BlockPos requesterPos, ConnectionFace requesterFace){
        BlockPos pos = requesterPos.relative(requesterFace.FACE);
        BlockState neighborState = world.getBlockState(pos);
        if(neighborState.getBlock() instanceof RedstoneSignalInterface gateBlock){
            return gateBlock.getAnyConnectionFace(world, pos, requesterFace);
        }
        return getGenericTargetBlockConnectionFace(neighborState, requesterFace.FACE);
    }

    public static ConnectionFace getGenericTargetGateConnectionFace(BlockState blockState, Direction direction){
        Direction secondary = LittleTools.getDirection(blockState);
        int channel = 5;
        if(secondary != direction && secondary != direction.getOpposite()) {
            channel = switch (secondary) {
                case DOWN -> 2;
                case NORTH -> switch (direction) {
                    case UP, DOWN -> 0;
                    case EAST, WEST -> 1;
                    default -> 5;
                };
                case EAST -> 1;
                case SOUTH -> switch (direction) {
                    case UP, DOWN -> 2;
                    case EAST, WEST -> 3;
                    default -> 5;
                };
                case WEST -> 3;
                case UP -> 0;
            };
        }
        return new ConnectionFace(direction,channel);
    }

    /**
     * Resolves the ConnectionFace of a neighboring block in the direction of redstone connection.
     *
     * @param world The block access (level).
     * @param pos   The position of the block checking redstone connection.
     * @param direction  The direction from the neighbor block to the current (i.e., redstone input/output).
     * @return The ConnectionFace of the neighbor block.
     */
    public static ConnectionFace canConnectRedstoneTargetConnectionFace(BlockGetter world, BlockPos pos, Direction direction) {
        if (direction == null) {
            return new ConnectionFace(Direction.UP, 5);
        }
        ConnectionFace connectionFaceA = new ConnectionFace(direction.getOpposite());
        return getTargetBlockConnectionFace((LevelAccessor) world, pos, connectionFaceA);
    }

    /**
     * Rotates a horizontal direction by another horizontal direction clockwise around the Y axis.
     * For example, rotating NORTH by EAST (90°) results in EAST.
     *
     * @param base The original direction to rotate.
     * @param rotation The direction indicating how much to rotate:
     *                 - NORTH = 0°
     *                 - EAST  = 90°
     *                 - SOUTH = 180°
     *                 - WEST  = 270°
     * @return The rotated direction.
     */
    public static Direction rotateDirectionClockwiseY(Direction base, Direction rotation){
        return switch (rotation){
            case NORTH -> base;
            case EAST -> base.getClockWise(Direction.Axis.Y);
            case SOUTH -> base.getClockWise(Direction.Axis.Y).getClockWise(Direction.Axis.Y);
            case WEST -> base.getCounterClockWise(Direction.Axis.Y);
            default -> rotation;
        };
    }

    /**
     * Rotates a horizontal direction by another horizontal direction counterclockwise around the Y axis.
     * For example, rotating EAST by EAST (90° CCW) results in NORTH.
     *
     * @param base The original direction to rotate.
     * @param rotation The direction indicating how much to rotate:
     *                 - NORTH = 0°
     *                 - EAST  = 90°
     *                 - SOUTH = 180°
     *                 - WEST  = 270°
     * @return The rotated direction.
     */
    public static Direction rotateDirectionCounterClockwiseY(Direction base, Direction rotation){
        return switch (rotation){
            case NORTH -> base;
            case EAST -> base.getCounterClockWise(Direction.Axis.Y);
            case SOUTH -> base.getClockWise(Direction.Axis.Y).getClockWise(Direction.Axis.Y);
            case WEST -> base.getClockWise(Direction.Axis.Y);
            default -> rotation;
        };
    }
    /**
     * Transforms a local direction (from the block’s frame) into world-space.
     *
     * @param blockState The block state containing rotation properties.
     * @param localDir The direction in the block’s local frame (NORTH = forward).
     * @return The corresponding direction in the world frame.
     */
    public static Direction getWorldDirectionFromLocal(BlockState blockState, Direction localDir) {
        Pair<Direction, Direction> dirs = getPrimarySecondaryDirections(blockState);
        Direction primary = dirs.getLeft();
        Direction secondary = dirs.getRight();
        Direction rotated = rotateDirectionClockwiseY(primary, localDir);
        if (rotated == Direction.UP) {
            return secondary.getOpposite();
        } else if (secondary == Direction.DOWN) {
            return rotated;
        } else if (secondary == Direction.UP) {
            return (rotated == Direction.EAST || rotated == Direction.WEST)
                    ? rotated
                    : rotated.getOpposite();
        } else {
            rotated = rotated.getCounterClockWise(Direction.Axis.X);
            return rotateDirectionClockwiseY(rotated, secondary);
        }
    }
    /**
     * Shortcut for getting the world-facing direction of the block’s local forward direction.
     * Equivalent to calling {@code getWorldDirectionFromLocal(state, Direction.NORTH)}.
     *
     * @param blockState The block state.
     * @return The direction in world space that the block’s forward face points to.
     */
    public static Direction getWorldDirectionFromLocalForward(BlockState blockState){
        return getWorldDirectionFromLocal(blockState, Direction.NORTH);
    }
    /**
     * Converts a world-space direction to the corresponding local frame direction
     * of a block.
     *
     * @param blockState   The block state.
     * @param worldDir A direction in world space (e.g., Direction.NORTH).
     * @return The equivalent direction in the block’s local frame.
     */
    public static Direction getLocalDirectionFromWorld(BlockState blockState, Direction worldDir){
        Pair<Direction, Direction> dirs = getPrimarySecondaryDirections(blockState);
        return getLocalDirectionFromWorld(dirs.getLeft(), dirs.getRight(), worldDir);
    }
    public static Direction getLocalDirectionFromWorld(Direction primary, Direction secondary, Direction worldDir){
        if(secondary == Direction.DOWN){
            return rotateDirectionCounterClockwiseY(worldDir,primary);
        } else if (secondary == Direction.UP){
            return rotateDirectionCounterClockwiseY(worldDir,primary.getOpposite());
        } else {
            Direction rotated = rotateDirectionCounterClockwiseY(worldDir, secondary);
            rotated = rotated.getClockWise(Direction.Axis.X);
            return rotateDirectionCounterClockwiseY(rotated, primary);
        }
    }

    public static float getDegreesFromDirectionY(Direction dir) {
        return switch (dir) {
            case EAST -> 90f;
            case SOUTH -> 180f;
            case WEST -> 270f;
            default -> 0f;
        };
    }

    public static double getRadiansFromDirectionY(Direction dir) {
        return switch (dir) {
            case EAST -> Math.PI * 0.5;
            case SOUTH -> Math.PI;
            case WEST -> Math.PI * 1.5;
            default -> 0;
        };
    }

    public static int encodeDirectionToInt(Direction direction){
        return switch (direction){
            case DOWN -> 0;
            case NORTH -> 1;
            case EAST -> 2;
            case SOUTH -> 3;
            case WEST -> 4;
            case UP -> 5;
        };
    }

    public static Direction decodeIntToDirection(int direction){
        return switch (direction){
            case 0 -> Direction.DOWN;
            case 1 -> Direction.NORTH;
            case 2 -> Direction.EAST;
            case 3 -> Direction.SOUTH;
            case 4 -> Direction.WEST;
            default -> Direction.UP;
        };
    }

    public static Direction directionFromPositions(BlockPos fromPos, BlockPos toPos){
        int dx = toPos.getX() - fromPos.getX();
        int dy = toPos.getY() - fromPos.getY();
        int dz = toPos.getZ() - fromPos.getZ();
        return RedstonecgModVersionRides.directionFromDelta(dx, dy, dz);
        //return Direction.fromDelta(dx, dy, dz);
        //return Direction.fromNormal(dx, dy, dz);
    }
}
