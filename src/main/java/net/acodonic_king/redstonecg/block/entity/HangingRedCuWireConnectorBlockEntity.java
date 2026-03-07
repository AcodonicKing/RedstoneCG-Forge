package net.acodonic_king.redstonecg.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.acodonic_king.redstonecg.ModLoaderRider;
import net.acodonic_king.redstonecg.RedstonecgMod;
import net.acodonic_king.redstonecg.block.defaults.PinMarkConnectionInterface;
import net.acodonic_king.redstonecg.block.defaults.WireInterface;
import net.acodonic_king.redstonecg.init.RedstonecgModBlockEntities;
import net.acodonic_king.redstonecg.network.MessengerBlockEntityPigeon;
import net.acodonic_king.redstonecg.network.RedstonecgModVariables;
import net.acodonic_king.redstonecg.procedures.BlockFrameTransformUtils;
import net.acodonic_king.redstonecg.procedures.RCGQuaternion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
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
    public ModelResourceLocation[] PINMARK_MODELS;
    public ModelResourceLocation CONNECTOR;
    public ModelResourceLocation WIRE;
    public List<HangingRedCuWireConnectorPosition> TARGETS = new ArrayList<>();
    public boolean BASE_READ = false;
    public Direction FACING = Direction.DOWN;
    public byte facingMode = 0;
    public RCGQuaternion facing = RCGQuaternion.Vector3F.rotateYP(0);
    public int REDCU_WIRE_LENGTH = 0;
    private AABB RENDER_BOUNDING_BOX;
    public HangingRedCuWireConnectorBlockEntity(BlockPos position, BlockState state) {
        super(RedstonecgModBlockEntities.HANGING_REDCU_WIRE_CONNECTOR.get(), position, state);
        BLOCK = ModLoaderRider.getBlockRegistryName(state.getBlock());
        CONNECTOR = new ModelResourceLocation(BLOCK, "connection=0,waterlogged=false");
        WIRE = new ModelResourceLocation(BLOCK, "connection=1,waterlogged=false");
        PINMARK_MODELS = new ModelResourceLocation[]{
                new ModelResourceLocation(BLOCK, "connection=8,waterlogged=false"),
                new ModelResourceLocation(BLOCK, "connection=9,waterlogged=false"),
                new ModelResourceLocation(BLOCK, "connection=10,waterlogged=false"),
                new ModelResourceLocation(BLOCK, "connection=11,waterlogged=false"),
                new ModelResourceLocation(BLOCK, "connection=12,waterlogged=false"),
        };
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
                if(hangingRedCuWireConnectorPosition.POSITION.length > 2)
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
        switch (this.FACING){
            case UP -> {
                this.facingMode = 5;
                this.facing = RCGQuaternion.Vector3F.rotateXP((float) Math.PI);
            }
            case DOWN -> this.facingMode = 0;
            default -> {
                this.facingMode = 1;
                this.facing = switch (this.FACING){
                    case NORTH, SOUTH -> RCGQuaternion.Vector3F.rotateYP((float) BlockFrameTransformUtils.getRadiansFromDirectionY(this.FACING));
                    default -> RCGQuaternion.Vector3F.rotateYP((float) (Math.PI + BlockFrameTransformUtils.getRadiansFromDirectionY(this.FACING)));
                };
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
        for(int i = 0; i < TARGETS.size(); i++){
            HangingRedCuWireConnectorPosition target = TARGETS.get(i);
            if(target.samePosition(targetPos)) {
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
            if(world.getBlockEntity(target.getBlockPos()) instanceof HangingRedCuWireConnectorBlockEntity targetBE) {
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
    public int getPower(LevelAccessor world, BlockPos targetPos, boolean render){
        for(HangingRedCuWireConnectorPosition target: TARGETS){
            if(target.samePosition(targetPos))
                return POWER;
        }
        if(targetPos.equals(this.getBlockPos()))
            return -1;
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
            BlockPos targetPos = target.getBlockPos();
            if (world.getBlockEntity(targetPos) instanceof HangingRedCuWireConnectorBlockEntity targetBE) {
                int powerB = targetBE.getPower(world, thisPos, target.RENDER);
                if(powerB == -1){
                    removeTargets.add(targetPos);
                    continue;
                }
                powerB -= (int)(target.DISTANCE);
                power = Math.max(power, powerB);
                continue;
            }
            removeTargets.add(targetPos);
        }
        for(BlockPos pos: removeTargets)
            removeConnectorThisSide(pos);
        return power;
    }
    public void tickTargets(LevelAccessor world){
        for(HangingRedCuWireConnectorPosition target: TARGETS) {
            BlockPos targetPos = target.getBlockPos();
            if (world.getBlockState(targetPos).getBlock() instanceof WireInterface nb) {
                nb.onTick(world, targetPos);
            }
        }
    }

    public static class HangingRedCuWireConnectorPosition{
        public int[] POSITION;
        public boolean RENDER = false;
        public float DISTANCE = 0.0f;
        public float[] DIRECTION;
        public RCGQuaternion ROTATION;
        //public int ID;
        public HangingRedCuWireConnectorPosition(){
            POSITION = new int[0];
            //ID = -1;
        }
        public HangingRedCuWireConnectorPosition(BlockPos targetPos, BlockPos thisPos, boolean render){
            POSITION = new int[]{targetPos.getX(), targetPos.getY(), targetPos.getZ()};
            setDistanceTo(thisPos);
            RENDER = render;
            //ID = id;
        }
        public boolean samePosition(BlockPos targetPos){
            return (POSITION[0] == targetPos.getX()) && (POSITION[1] == targetPos.getY()) && (POSITION[2] == targetPos.getZ());
        }
        public BlockPos getBlockPos(){
            return new BlockPos(POSITION[0],POSITION[1],POSITION[2]);
        }
        public void setDistanceTo(BlockPos thisPos){
            DIRECTION = new float[]{
                    POSITION[0] - thisPos.getX(),
                    POSITION[1] - thisPos.getY(),
                    POSITION[2] - thisPos.getZ(),
            };
            DISTANCE = (float) Math.sqrt(DIRECTION[0]*DIRECTION[0] + DIRECTION[1]*DIRECTION[1] + DIRECTION[2]*DIRECTION[2]);
            for(int i = 0; i < DIRECTION.length; i++){
                DIRECTION[i] /= DISTANCE;
            }
            ROTATION = new RCGQuaternion().zRotationTo(-DIRECTION[0], -DIRECTION[1], -DIRECTION[2]);
        }
        public static HangingRedCuWireConnectorPosition load(CompoundTag tag, BlockPos thisPos){
            HangingRedCuWireConnectorPosition obj = new HangingRedCuWireConnectorPosition();
            if(tag.contains("position")) {
                obj.POSITION = tag.getIntArray("position");
                obj.setDistanceTo(thisPos);
            }
            if(tag.contains("render"))
                obj.RENDER = tag.getBoolean("render");
            return obj;
        }
        public CompoundTag save(){
            CompoundTag tag = new CompoundTag();
            tag.putIntArray("position", POSITION);
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
            box[0] = Math.max(box[0], target.POSITION[0]);
            box[1] = Math.max(box[1], target.POSITION[1]);
            box[2] = Math.max(box[2], target.POSITION[2]);
            box[3] = Math.min(box[3], target.POSITION[0]);
            box[4] = Math.min(box[4], target.POSITION[1]);
            box[5] = Math.min(box[5], target.POSITION[2]);
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


    public static class HangingRedCuWireConnectorBlockEntityRenderer implements BlockEntityRenderer<HangingRedCuWireConnectorBlockEntity> {
        BlockEntityRendererProvider.Context context;
        public HangingRedCuWireConnectorBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
            super();
            this.context = context;
        }

        @Override
        public boolean shouldRenderOffScreen(HangingRedCuWireConnectorBlockEntity be) {
            return true;
        }

        @Override
        public void render(HangingRedCuWireConnectorBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
            ModelManager modelManager = Minecraft.getInstance().getModelManager();
            BlockState blockState = blockEntity.getBlockState();
            ModelBlockRenderer modelRenderer = Minecraft.getInstance().getBlockRenderer().getModelRenderer();
            BakedModel model = modelManager.getModel(blockEntity.WIRE);
            VertexConsumer vc = bufferSource.getBuffer(RenderType.cutoutMipped());

            BlockPos thisPos = blockEntity.getBlockPos();
            for(HangingRedCuWireConnectorPosition target: blockEntity.TARGETS){
                if(!target.RENDER)
                    continue;
                poseStack.pushPose();
                poseStack.translate(
                        target.POSITION[0]-thisPos.getX()+0.5,
                        target.POSITION[1]-thisPos.getY()+0.5,
                        target.POSITION[2]-thisPos.getZ()+0.5
                );
                poseStack.mulPose(target.ROTATION.quaternion);
                poseStack.scale(1f, 1f, target.DISTANCE);
                renderModel(blockEntity, modelRenderer, vc, blockState, model, poseStack, packedLight, packedOverlay);
                poseStack.popPose();
            }

            renderPose(blockEntity, poseStack);
            model = modelManager.getModel(blockEntity.CONNECTOR);
            renderModel(blockEntity, modelRenderer, vc, blockState, model, poseStack, packedLight, packedOverlay);
            if(blockEntity.PINMARK_MODELS.length != 0) {
                int connection = 0;
                if (blockState.getBlock() instanceof PinMarkConnectionInterface pmci) {
                    connection = pmci.getConnection(blockState);
                    connection = pmci.connectionFilter(connection);
                }
                if (blockEntity.BASE_READ)
                    connection |= 16;
                for (int i = 0; i < 5; i++) {
                    boolean v = (connection & 1) == 0;
                    connection >>= 1;
                    if (v)
                        continue;
                    model = modelManager.getModel(blockEntity.PINMARK_MODELS[i]);
                    renderModel(blockEntity, modelRenderer, vc, blockState, model, poseStack, packedLight, packedOverlay);
                }
            }
            poseStack.popPose();
        }
        private void renderPose(HangingRedCuWireConnectorBlockEntity blockEntity, PoseStack poseStack){
            poseStack.pushPose();
            poseStack.translate(0.5, 0.5, 0.5);
            switch (blockEntity.facingMode){
                case 5 -> poseStack.mulPose(blockEntity.facing.quaternion);
                case 0 -> {}
                default -> {
                    poseStack.mulPose(blockEntity.facing.quaternion);
                    poseStack.mulPose(RCGQuaternion.Vector3F.rotateXP((float) (Math.PI * 0.5)).quaternion);
                }
            }
            poseStack.translate(-0.5, -0.5, -0.5);
        }
        private void renderModel(
                HangingRedCuWireConnectorBlockEntity blockEntity,
                ModelBlockRenderer modelRenderer,
                VertexConsumer vc,
                BlockState blockState,
                BakedModel model,
                PoseStack poseStack,
                int packedLight, int packedOverlay
        ){
            modelRenderer.renderModel(
                    poseStack.last(),
                    vc,
                    blockState,
                    model,
                    1.0f, 1.0f, 1.0f,
                    packedLight,
                    packedOverlay
            );
        }
        //Override
        public AABB getRenderBoundingBox(HangingRedCuWireConnectorBlockEntity be) {
            return be.RENDER_BOUNDING_BOX;
        }
    }
}
