package net.acodonic_king.redstonecg.block.entity;

import net.acodonic_king.redstonecg.procedures.BlockFrameTransformUtils;
import net.acodonic_king.redstonecg.procedures.RCGMatrix;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Pair;

public class OrientationHolderBlockEntity extends SuperBlockEntity{
    public byte combination = 0;

    public OrientationHolderBlockEntity(BlockEntityType blockEntityType, BlockPos position, BlockState state) {
        super(blockEntityType, position, state);
    }

    public Pair<Direction, Direction> getPrimarySecondaryDirections(){
        return Pair.of(getRotation(combination), getFacing(combination));
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putByte("state", getState(combination));
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("state")) {
            combination = getCombination(tag.getByte("state"));
        }
    }

    public static final RCGMatrix.M4F[] ROTATION_MATRIX = new RCGMatrix.M4F[]{
            new RCGMatrix.M4F(),
            new RCGMatrix.M4F().rotateY(RCGMatrix.ANGLES[3]),
            new RCGMatrix.M4F().rotateY(RCGMatrix.ANGLES[2]),
            new RCGMatrix.M4F().rotateY(RCGMatrix.ANGLES[1])
    };

    public static final RCGMatrix.M4F[] COMBINED_MATRIX = new RCGMatrix.M4F[]{
            new RCGMatrix.M4F(),
            new RCGMatrix.M4F().rotateX(RCGMatrix.ANGLES[1]),
            new RCGMatrix.M4F().rotateY(RCGMatrix.ANGLES[3]).rotateX(RCGMatrix.ANGLES[1]),
            new RCGMatrix.M4F().rotateY(RCGMatrix.ANGLES[2]).rotateX(RCGMatrix.ANGLES[1]),
            new RCGMatrix.M4F().rotateY(RCGMatrix.ANGLES[1]).rotateX(RCGMatrix.ANGLES[1]),
            new RCGMatrix.M4F().rotateX(RCGMatrix.ANGLES[2]),

            new RCGMatrix.M4F().rotateY(RCGMatrix.ANGLES[3]),
            new RCGMatrix.M4F().rotateX(RCGMatrix.ANGLES[1]).rotateY(RCGMatrix.ANGLES[3]),
            new RCGMatrix.M4F().rotateY(RCGMatrix.ANGLES[3]).rotateX(RCGMatrix.ANGLES[1]).rotateY(RCGMatrix.ANGLES[3]),
            new RCGMatrix.M4F().rotateY(RCGMatrix.ANGLES[2]).rotateX(RCGMatrix.ANGLES[1]).rotateY(RCGMatrix.ANGLES[3]),
            new RCGMatrix.M4F().rotateY(RCGMatrix.ANGLES[1]).rotateX(RCGMatrix.ANGLES[1]).rotateY(RCGMatrix.ANGLES[3]),
            new RCGMatrix.M4F().rotateX(RCGMatrix.ANGLES[2]).rotateY(RCGMatrix.ANGLES[3]),

            new RCGMatrix.M4F().rotateY(RCGMatrix.ANGLES[2]),
            new RCGMatrix.M4F().rotateX(RCGMatrix.ANGLES[1]).rotateY(RCGMatrix.ANGLES[2]),
            new RCGMatrix.M4F().rotateY(RCGMatrix.ANGLES[3]).rotateX(RCGMatrix.ANGLES[1]).rotateY(RCGMatrix.ANGLES[2]),
            new RCGMatrix.M4F().rotateY(RCGMatrix.ANGLES[2]).rotateX(RCGMatrix.ANGLES[1]).rotateY(RCGMatrix.ANGLES[2]),
            new RCGMatrix.M4F().rotateY(RCGMatrix.ANGLES[1]).rotateX(RCGMatrix.ANGLES[1]).rotateY(RCGMatrix.ANGLES[2]),
            new RCGMatrix.M4F().rotateX(RCGMatrix.ANGLES[2]).rotateY(RCGMatrix.ANGLES[2]),

            new RCGMatrix.M4F().rotateY(RCGMatrix.ANGLES[1]),
            new RCGMatrix.M4F().rotateX(RCGMatrix.ANGLES[1]).rotateY(RCGMatrix.ANGLES[1]),
            new RCGMatrix.M4F().rotateY(RCGMatrix.ANGLES[3]).rotateX(RCGMatrix.ANGLES[1]).rotateY(RCGMatrix.ANGLES[1]),
            new RCGMatrix.M4F().rotateY(RCGMatrix.ANGLES[2]).rotateX(RCGMatrix.ANGLES[1]).rotateY(RCGMatrix.ANGLES[1]),
            new RCGMatrix.M4F().rotateY(RCGMatrix.ANGLES[1]).rotateX(RCGMatrix.ANGLES[1]).rotateY(RCGMatrix.ANGLES[1]),
            new RCGMatrix.M4F().rotateX(RCGMatrix.ANGLES[2]).rotateY(RCGMatrix.ANGLES[1]),
    };

    public static byte getCombination(Direction facing, Direction rotation){
        int c = BlockFrameTransformUtils.encodeDirectionToInt(rotation) - 1;
        c *= 6;
        c += BlockFrameTransformUtils.encodeDirectionToInt(facing);
        return (byte) c;
    }

    public static byte getCombination(byte state){
        int c = (state & 7) - 1;
        c = (c & 3) * 6;
        c += (state >> 3) & 7;
        return (byte) c;
    }

    public static byte getState(byte combination){
        int s = getRotationIndex(combination);
        s |= getFacingIndex(combination) << 3;
        return (byte) s;
    }

    public static int getRotationIndex(byte combination){
        return (combination / 6) + 1;
    }

    public static int getFacingIndex(byte combination){
        return combination % 6;
    }

    public static Direction getRotation(byte combination){
        return BlockFrameTransformUtils.decodeIntToDirection(getRotationIndex(combination));
    }

    public static Direction getFacing(byte combination){
        return BlockFrameTransformUtils.decodeIntToDirection(getFacingIndex(combination));
    }

    public int getRotationIndex(){
        return getRotationIndex(combination);
    }

    public int getFacingIndex(){
        return getFacingIndex(combination);
    }

    public Direction getRotation(){
        return BlockFrameTransformUtils.decodeIntToDirection(getRotationIndex(combination));
    }

    public Direction getFacing(){
        return BlockFrameTransformUtils.decodeIntToDirection(getFacingIndex(combination));
    }

    public void setRotation(Direction rotation){
        int c = BlockFrameTransformUtils.encodeDirectionToInt(rotation) - 1;
        c *= 6;
        c += combination % 6;
        combination = (byte) c;
    }

    public void setFacing(Direction facing){
        int c = combination / 6;
        c *= 6;
        c += BlockFrameTransformUtils.encodeDirectionToInt(facing);
        combination = (byte) c;
    }

    public static RCGMatrix.M4F getPoseStackMatrix(RCGMatrix.M4F matrix, byte combination){
        return matrix.mul(COMBINED_MATRIX[combination]);
    }

    public static RCGMatrix.M4F getPoseStackMatrix(byte combination){
        return COMBINED_MATRIX[combination];
    }
}
