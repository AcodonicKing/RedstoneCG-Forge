package net.acodonic_king.redstonecg.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.acodonic_king.redstonecg.ModLoaderRider;
import net.acodonic_king.redstonecg.block.defaults.WireInterface;
import net.acodonic_king.redstonecg.init.RedstonecgModBlockEntities;
import net.acodonic_king.redstonecg.network.MessengerBlockEntityPigeon;
import net.acodonic_king.redstonecg.network.RedstonecgModVariables;
import net.acodonic_king.redstonecg.procedures.BlockFrameTransformUtils;
import net.acodonic_king.redstonecg.procedures.RCGMatrix;
import net.acodonic_king.redstonecg.procedures.RCGQuaternion;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.*;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

public class HangingRedCuWireConnectorBlockEntity extends RedCuWireBlockEntity{
    // public int ID;
    public ResourceLocation BLOCK;
    public List<HangingRedCuWireConnectorPosition> TARGETS = new ArrayList<>();
    public boolean BASE_READ = false;
    public Direction FACING = Direction.DOWN;
    public byte facingMode = 0;
    public RCGQuaternion facing = new RCGQuaternion();
    public int REDCU_WIRE_LENGTH = 0;
    public AABB RENDER_BOUNDING_BOX;
    public HangingRedCuWireConnectorBlockEntity(BlockPos position, BlockState state) {
        super(RedstonecgModBlockEntities.HANGING_REDCU_WIRE_CONNECTOR.get(), position, state);
        BLOCK = ModLoaderRider.getBlockRegistryName(state.getBlock());
        makeRenderBoundingBox();
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if(!TARGETS.isEmpty()) {
            ListTag target_list = new ListTag();
            for (HangingRedCuWireConnectorPosition target : TARGETS) {
                target_list.add(target.save());
            }
            tag.put("targets", target_list);
        }
        byte state = (byte) BlockFrameTransformUtils.encodeDirectionToInt(this.FACING);
        state <<= 3;
        tag.putByte("state", state);
        tag.putBoolean("base_read", BASE_READ);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        TARGETS = new ArrayList<>();
        if(tag.contains("targets")){
            ListTag target_list = tag.getList("targets", Tag.TAG_COMPOUND);
            for(int i = 0; i < target_list.size(); i++){
                HangingRedCuWireConnectorPosition hangingRedCuWireConnectorPosition = HangingRedCuWireConnectorPosition.load(target_list.getCompound(i), this.getBlockPos());
                //if(hangingRedCuWireConnectorPosition.POSITION.length > 2)
                if(hangingRedCuWireConnectorPosition.RELATIVE.length > 2)
                    TARGETS.add(hangingRedCuWireConnectorPosition);
            }
            makeRenderBoundingBox();
        }
        if(tag.contains("base_read"))
            BASE_READ = tag.getBoolean("base_read");
        if(tag.contains("state")) {
            int state = (int) tag.getByte("state");
            state >>= 3;
            this.FACING = BlockFrameTransformUtils.decodeIntToDirection(state & 7);
            modelUpdate();
        }
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void modelUpdate(){
        getFacing(this.facing, this.FACING);
        this.facingMode = getFacingMode(this.FACING);
    }

    public static RCGQuaternion getFacing(RCGQuaternion quat, Direction facing){
        return switch (facing){
            case UP -> quat.identity().rotateX(RCGMatrix.ANGLES[2]);
            case DOWN -> quat.identity();
            case NORTH, SOUTH -> quat.identity().rotateY(BlockFrameTransformUtils.getRadiansFromDirectionY(facing));
            default -> quat.identity().rotateY(RCGMatrix.ANGLES[2] + BlockFrameTransformUtils.getRadiansFromDirectionY(facing));
        };
    }

    public static byte getFacingMode(Direction facing){
        return (byte) BlockFrameTransformUtils.encodeDirectionToInt(facing);
    }

    public static void setFacingPoseStack(PoseStack poseStack, byte facingMode, RCGQuaternion facing){
        switch (facingMode){
            case 5 -> poseStack.mulPose(facing.getQuaternion());
            case 0 -> {}
            default -> {
                final RCGQuaternion quat = new RCGQuaternion().rotateX(RCGMatrix.ANGLES[1]);
                poseStack.mulPose(facing.getQuaternion());
                poseStack.mulPose(quat.getQuaternion());
            }
        }
    }

    public void addConnectorAsTarget(BlockPos targetPos){
        TARGETS.add(new HangingRedCuWireConnectorPosition(targetPos, this.getBlockPos(), false));
        makeRenderBoundingBox();
    }
    public void addConnectorAsSource(BlockPos targetPos){
        HangingRedCuWireConnectorPosition target = new HangingRedCuWireConnectorPosition(targetPos, this.getBlockPos(), true);
        TARGETS.add(target);
        REDCU_WIRE_LENGTH += (int)target.DISTANCE;
        makeRenderBoundingBox();
    }
    public int removeConnectorThisSide(BlockPos targetPos){
        BlockPos thisPos = this.getBlockPos();
        for(int i = 0; i < TARGETS.size(); i++){
            HangingRedCuWireConnectorPosition target = TARGETS.get(i);
            if(target.isRelative(thisPos, targetPos)) {
            //if(target.samePosition(targetPos)) {
                TARGETS.remove(i);
                if(target.RENDER) {
                    REDCU_WIRE_LENGTH -= (int)target.DISTANCE;
                    if(REDCU_WIRE_LENGTH < 0){
                        target.DISTANCE += REDCU_WIRE_LENGTH;
                        REDCU_WIRE_LENGTH = 0;
                    }
                }
                return (int)target.DISTANCE;
            }
        }
        return 0;
    }
    public int removeAllConnectors(LevelAccessor world){
        BlockPos thisPos = this.getBlockPos();
        int length = 0;
        for(HangingRedCuWireConnectorPosition target: TARGETS){
            //if(world.getBlockEntity(target.getBlockPos()) instanceof HangingRedCuWireConnectorBlockEntity targetBE) {
            if(world.getBlockEntity(target.getRelative(thisPos)) instanceof HangingRedCuWireConnectorBlockEntity targetBE) {
                int l = targetBE.removeConnectorThisSide(thisPos);
                if(target.RENDER){
                    REDCU_WIRE_LENGTH -= l;
                    if(REDCU_WIRE_LENGTH < 0){
                        l += REDCU_WIRE_LENGTH;
                        REDCU_WIRE_LENGTH = 0;
                    }
                }
                length += l;
                targetBE.setChanged();
                MessengerBlockEntityPigeon.send(new MessengerBlockEntityPigeon(targetBE.getBlockPos(),targetBE.getUpdateTag()));
            }
        }
        TARGETS = new ArrayList<>();
        return length;
    }
    public int getPositionPower(LevelAccessor world, BlockPos targetPos, boolean render){
        if(targetPos.equals(this.getBlockPos()))
            return -1;
        for(HangingRedCuWireConnectorPosition target: TARGETS){
            if(target.samePosition(targetPos))
                return POWER;
        }
        HangingRedCuWireConnectorPosition target = new HangingRedCuWireConnectorPosition(targetPos, this.getBlockPos(), !render);
        if(target.DISTANCE > RedstonecgModVariables.MapVariables.get(world).hangingRedCuWireMaxDistance)
            return -1;
        TARGETS.add(target);
        makeRenderBoundingBox();
        return POWER;
    }
    public int getRelativePower(LevelAccessor world, BlockPos targetPos, boolean render){
        BlockPos thisPos = this.getBlockPos();
        if(targetPos.equals(thisPos))
            return -1;
        for(HangingRedCuWireConnectorPosition target: TARGETS){
            if(target.isRelative(thisPos, targetPos))
                return POWER;
        }
        HangingRedCuWireConnectorPosition target = new HangingRedCuWireConnectorPosition(targetPos, this.getBlockPos(), !render);
        if(target.DISTANCE > RedstonecgModVariables.MapVariables.get(world).hangingRedCuWireMaxDistance)
            return -1;
        TARGETS.add(target);
        makeRenderBoundingBox();
        return POWER;
    }
    public int getConnectedPower(LevelAccessor world){
        int power = 0;
        BlockPos thisPos = this.getBlockPos();
        List<BlockPos> removeTargets = new ArrayList<>();
        for(HangingRedCuWireConnectorPosition target: TARGETS) {
            BlockPos targetPos = target.getRelative(thisPos);
            if (world.getBlockEntity(targetPos) instanceof HangingRedCuWireConnectorBlockEntity targetBE) {
                int powerB = targetBE.getRelativePower(world, thisPos, target.RENDER);
                if(powerB == -1){
                    powerB = tryGetConnectedPositionPowerTarget(world, thisPos, target);
                    if(powerB > -1)
                        target.setRelative(thisPos);
                }
                if(powerB > -1){
                    //target.setPositionToRelative(thisPos);
                    powerB -= (int)(target.DISTANCE);
                    power = Math.max(power, powerB);
                    continue;
                }
            } else {
                int powerB = tryGetConnectedPositionPowerTarget(world, thisPos, target);
                if(powerB > -1) {
                    target.setRelative(thisPos);
                    powerB -= (int)(target.DISTANCE);
                    power = Math.max(power, powerB);
                    continue;
                }
            }
            /*BlockPos targetPos = target.getBlockPos();
            if (world.getBlockEntity(targetPos) instanceof HangingRedCuWireConnectorBlockEntity targetBE) {
                int powerB = targetBE.getPower(world, thisPos, target.RENDER);
                if(powerB == -1){
                    powerB = tryGetConnectedRelativePowerTarget(world, thisPos, target);
                    if(powerB == -1) {
                        removeTargets.add(targetPos);
                        continue;
                    }
                    target.setPositionToRelative(thisPos);
                }
                powerB -= (int)(target.DISTANCE);
                power = Math.max(power, powerB);
                continue;
            } else {
                int powerB = tryGetConnectedRelativePowerTarget(world, thisPos, target);
                if(powerB > -1){
                    target.setPositionToRelative(thisPos);
                    powerB -= (int)(target.DISTANCE);
                    power = Math.max(power, powerB);
                    continue;
                }
            }*/
            removeTargets.add(targetPos);
        }
        for(BlockPos pos: removeTargets)
            removeConnectorThisSide(pos);
        return power;
    }
    public int tryGetConnectedPositionPowerTarget(LevelAccessor world, BlockPos thisPos, HangingRedCuWireConnectorPosition target){
        BlockPos targetPos = target.getBlockPos();
        if (world.getBlockEntity(targetPos) instanceof HangingRedCuWireConnectorBlockEntity targetBE) {
            return targetBE.getPositionPower(world, thisPos, target.RENDER);
        }
        return -1;
    }
    public void tickTargets(LevelAccessor world, int recursion){
        for(HangingRedCuWireConnectorPosition target: TARGETS) {
            //BlockPos targetPos = target.getBlockPos();
            BlockPos targetPos = target.getRelative(this.getBlockPos());
            if (world.getBlockState(targetPos).getBlock() instanceof WireInterface nb) {
                nb.onTick(world, targetPos, recursion);
            }
        }
    }
    public void scheduleTickTargets(LevelAccessor world){
        for(HangingRedCuWireConnectorPosition target: TARGETS) {
            //BlockPos targetPos = target.getBlockPos();
            BlockPos targetPos = target.getRelative(this.getBlockPos());
            world.scheduleTick(targetPos, world.getBlockState(targetPos).getBlock(), 1);
        }
    }

    @Override
    protected HangingRedCuWireConnectorBlockEntity clone(){
        HangingRedCuWireConnectorBlockEntity be = new HangingRedCuWireConnectorBlockEntity(this.getBlockPos(), this.getBlockState());
        for(HangingRedCuWireConnectorPosition target: this.TARGETS)
            be.TARGETS.add(target.clone());
        be.BASE_READ = this.BASE_READ;
        be.FACING = this.FACING;
        be.facingMode = this.facingMode;
        be.facing = this.facing.clone();
        be.REDCU_WIRE_LENGTH = this.REDCU_WIRE_LENGTH;
        be.makeRenderBoundingBox();
        return be;
    }

    public static class HangingRedCuWireConnectorPosition{
        public int[] POSITION;
        public boolean RENDER = false;
        public float DISTANCE = 0.0f;
        public int[] RELATIVE;
        public RCGQuaternion ROTATION;
        //public int ID;
        public HangingRedCuWireConnectorPosition(){
            //POSITION = new int[0];
            //ID = -1;
        }
        public HangingRedCuWireConnectorPosition(BlockPos targetPos, BlockPos thisPos, boolean render){
            POSITION = new int[]{targetPos.getX(), targetPos.getY(), targetPos.getZ()};
            setRelative(targetPos, thisPos);
            setDistanceTo();
            RENDER = render;
            //ID = id;
        }
        @Override
        public HangingRedCuWireConnectorPosition clone(){
            HangingRedCuWireConnectorPosition inst = new HangingRedCuWireConnectorPosition();
            inst.POSITION = new int[3];
            System.arraycopy(this.POSITION, 0, inst.POSITION, 0, 3);
            inst.RENDER = this.RENDER;
            inst.DISTANCE = this.DISTANCE;
            if(this.RELATIVE != null){
                inst.RELATIVE = new int[3];
                System.arraycopy(this.RELATIVE, 0, inst.RELATIVE, 0, 3);
            }
            inst.ROTATION.set(this.ROTATION);
            return inst;
        }
        public boolean samePosition(BlockPos targetPos){
            return (POSITION[0] == targetPos.getX()) && (POSITION[1] == targetPos.getY()) && (POSITION[2] == targetPos.getZ());
        }
        public boolean isRelative(BlockPos thisPos, BlockPos targetPos){
            return (
                    (targetPos.getX() == thisPos.getX() + RELATIVE[0]) &&
                    (targetPos.getY() == thisPos.getY() + RELATIVE[1]) &&
                    (targetPos.getZ() == thisPos.getZ() + RELATIVE[2])
            );
        }
        public BlockPos getBlockPos(){
            return new BlockPos(POSITION[0],POSITION[1],POSITION[2]);
        }
        public BlockPos getRelative(BlockPos thisPos){
            return new BlockPos(
                    thisPos.getX() + RELATIVE[0],
                    thisPos.getY() + RELATIVE[1],
                    thisPos.getZ() + RELATIVE[2]
            );
        }
        public void setRelative(BlockPos thisPos){
            RELATIVE = new int[]{
                    POSITION[0] - thisPos.getX(),
                    POSITION[1] - thisPos.getY(),
                    POSITION[2] - thisPos.getZ(),
            };
        }
        public void setRelative(BlockPos targetPos, BlockPos thisPos){
            RELATIVE = new int[]{
                    targetPos.getX() - thisPos.getX(),
                    targetPos.getY() - thisPos.getY(),
                    targetPos.getZ() - thisPos.getZ(),
            };
        }
        public void setPositionToRelative(BlockPos thisPos){
            POSITION[0] = thisPos.getX() + RELATIVE[0];
            POSITION[1] = thisPos.getY() + RELATIVE[1];
            POSITION[2] = thisPos.getZ() + RELATIVE[2];
        }
        public void setDistanceTo(){
            DISTANCE = (float) Math.sqrt(RELATIVE[0]*RELATIVE[0] + RELATIVE[1]*RELATIVE[1] + RELATIVE[2]*RELATIVE[2]);
            float[] DIRECTION = new float[3];
            for(int i = 0; i < RELATIVE.length; i++){
                DIRECTION[i] = ((float) RELATIVE[i]) / DISTANCE;
            }
            ROTATION = new RCGQuaternion().zRotationTo(-DIRECTION[0], -DIRECTION[1], -DIRECTION[2]);
        }
        public static HangingRedCuWireConnectorPosition load(CompoundTag tag, BlockPos thisPos){
            HangingRedCuWireConnectorPosition obj = new HangingRedCuWireConnectorPosition();
            if(tag.contains("relative")) {
                obj.RELATIVE = tag.getIntArray("relative");
            }
            if(tag.contains("position")) {
                obj.POSITION = tag.getIntArray("position");
                if(obj.RELATIVE == null){
                    obj.RELATIVE = new int[3];
                    obj.RELATIVE[0] = obj.POSITION[0] - thisPos.getX();
                    obj.RELATIVE[1] = obj.POSITION[1] - thisPos.getY();
                    obj.RELATIVE[2] = obj.POSITION[2] - thisPos.getZ();
                }
            }
            if(tag.contains("position") || tag.contains("relative"))
                if(obj.POSITION == null){
                    obj.POSITION = new int[]{
                            thisPos.getX() + obj.RELATIVE[0],
                            thisPos.getY() + obj.RELATIVE[1],
                            thisPos.getZ() + obj.RELATIVE[2]
                    };
                }
            obj.setDistanceTo();
            if(tag.contains("render"))
                obj.RENDER = tag.getBoolean("render");
            return obj;
        }
        public CompoundTag save(){
            CompoundTag tag = new CompoundTag();
            //tag.putIntArray("position", POSITION);
            tag.putIntArray("relative", RELATIVE);
            tag.putBoolean("render", RENDER);
            return tag;
        }
    }

    public void makeRenderBoundingBox() {
        BlockPos pos = this.getBlockPos();
        int[] box = new int[6];
        box[0] = pos.getX();
        box[1] = pos.getY();
        box[2] = pos.getZ();
        box[3] = pos.getX();
        box[4] = pos.getY();
        box[5] = pos.getZ();
        for(HangingRedCuWireConnectorPosition target: TARGETS) {
            if (!target.RENDER)
                continue;
            /*box[0] = Math.max(box[0], target.POSITION[0]);
            box[1] = Math.max(box[1], target.POSITION[1]);
            box[2] = Math.max(box[2], target.POSITION[2]);
            box[3] = Math.min(box[3], target.POSITION[0]);
            box[4] = Math.min(box[4], target.POSITION[1]);
            box[5] = Math.min(box[5], target.POSITION[2]);*/
            box[0] = Math.max(box[0], pos.getX()+target.RELATIVE[0]);
            box[1] = Math.max(box[1], pos.getY()+target.RELATIVE[1]);
            box[2] = Math.max(box[2], pos.getZ()+target.RELATIVE[2]);
            box[3] = Math.min(box[3], pos.getX()+target.RELATIVE[0]);
            box[4] = Math.min(box[4], pos.getY()+target.RELATIVE[1]);
            box[5] = Math.min(box[5], pos.getZ()+target.RELATIVE[2]);
        }
        RENDER_BOUNDING_BOX = new AABB(
                box[0]-0.5, box[1]-0.5, box[2]-0.5,
                box[3]+0.5, box[4]+0.5, box[5]+0.5
        );
    }

    @Override
    public AABB getRenderBoundingBox() {
        return RENDER_BOUNDING_BOX;
    }
}
