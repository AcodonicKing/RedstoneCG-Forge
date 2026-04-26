package net.acodonic_king.redstonecg.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.acodonic_king.redstonecg.procedures.BlockFrameTransformUtils;
import net.acodonic_king.redstonecg.procedures.RCGQuaternion;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Pair;

public class OrientationHolderBlockEntity extends SuperBlockEntity{
    public RCGQuaternion rotation = RCGQuaternion.Vector3F.rotateYP(0);
    public byte facingMode = 0;
    public RCGQuaternion facing = RCGQuaternion.Vector3F.rotateYP(0);
    public Direction FACING = Direction.DOWN;
    public Direction ROTATION = Direction.NORTH;

    public OrientationHolderBlockEntity(BlockEntityType blockEntityType, BlockPos position, BlockState state) {
        super(blockEntityType, position, state);
    }

    public Pair<Direction, Direction> getPrimarySecondaryDirections(){
        return Pair.of(ROTATION, FACING);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        byte state = (byte) BlockFrameTransformUtils.encodeDirectionToInt(this.FACING);
        state <<= 3;
        state |= (byte) BlockFrameTransformUtils.encodeDirectionToInt(this.ROTATION);
        tag.putByte("state", state);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("state")) {
            int state = (int) tag.getByte("state");
            this.ROTATION = BlockFrameTransformUtils.decodeIntToDirection(state & 7);
            state >>= 3;
            this.FACING = BlockFrameTransformUtils.decodeIntToDirection(state & 7);
        }
        modelUpdate();
    }

    public void modelUpdate(){
        this.rotation = getRotation(this.ROTATION);
        this.facing = getFacing(this.FACING);
        this.facingMode = getFacingMode(this.FACING);
    }

    public static RCGQuaternion getRotation(Direction rotation){
        return RCGQuaternion.Vector3F.rotateYP((float) -BlockFrameTransformUtils.getRadiansFromDirectionY(rotation));
    }

    public static RCGQuaternion getRotation(Direction rotation, float angle){
        return RCGQuaternion.Vector3F.rotateYP((float) (angle-BlockFrameTransformUtils.getRadiansFromDirectionY(rotation)));
    }

    public static RCGQuaternion getFacing(Direction facing){
        return switch (facing){
            case UP -> RCGQuaternion.Vector3F.rotateXP((float) Math.PI);
            case DOWN -> RCGQuaternion.Vector3F.rotateXP(0);
            case NORTH, SOUTH -> RCGQuaternion.Vector3F.rotateYP((float) BlockFrameTransformUtils.getRadiansFromDirectionY(facing));
            default -> RCGQuaternion.Vector3F.rotateYP((float) (Math.PI + BlockFrameTransformUtils.getRadiansFromDirectionY(facing)));
        };
    }

    public static RCGQuaternion getFacing(int facing){
        return switch (facing){
            case 1 -> RCGQuaternion.Vector3F.rotateYP(0f);
            case 2 -> RCGQuaternion.Vector3F.rotateYP((float) (Math.PI * 1.5));
            case 3 -> RCGQuaternion.Vector3F.rotateYP((float) Math.PI);
            case 4 -> RCGQuaternion.Vector3F.rotateYP((float) (Math.PI * 0.5));
            case 5 -> RCGQuaternion.Vector3F.rotateXP((float) Math.PI);
            default -> RCGQuaternion.Vector3F.rotateXP(0f);
        };
    }

    public static byte getFacingMode(Direction facing){
        return switch (facing){
            case UP -> 5;
            case DOWN -> 0;
            default -> 1;
        };
    }

    public static byte getFacingMode(int facing){
        return switch (facing){
            case 5 -> 5;
            case 0 -> 0;
            default -> 1;
        };
    }

    public static void setPoseStack(PoseStack poseStack, byte facingMode, RCGQuaternion facing, RCGQuaternion rotation){
        setFacingPoseStack(poseStack, facingMode, facing);
        setRotationPoseStack(poseStack, rotation);
    }

    public static void setFacingPoseStack(PoseStack poseStack, byte facingMode, RCGQuaternion facing){
        switch (facingMode){
            case 5 -> poseStack.mulPose(facing.quaternion);
            case 0 -> {}
            default -> {
                poseStack.mulPose(facing.quaternion);
                poseStack.mulPose(RCGQuaternion.Vector3F.rotateXP((float) (Math.PI * 0.5)).quaternion);
            }
        }
    }

    public static void setRotationPoseStack(PoseStack poseStack, RCGQuaternion rotation){
        poseStack.mulPose(rotation.quaternion);
    }

    public void setPoseStack(PoseStack poseStack){
        setPoseStack(poseStack, this.facingMode, this.facing, this.rotation);
    }
}
