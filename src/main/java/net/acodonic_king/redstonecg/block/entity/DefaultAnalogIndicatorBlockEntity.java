package net.acodonic_king.redstonecg.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.acodonic_king.redstonecg.init.RedstonecgModBlockEntities;
import net.acodonic_king.redstonecg.procedures.BlockFrameTransformUtils;
import net.acodonic_king.redstonecg.procedures.RCGQuaternion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Pair;

public class DefaultAnalogIndicatorBlockEntity extends SuperBlockEntity {
    public RCGQuaternion rotation = RCGQuaternion.Vector3F.rotateYP(0);
    public byte facingMode = 0;
    public RCGQuaternion facing = RCGQuaternion.Vector3F.rotateYP(0);
    public Direction FACING = Direction.DOWN;
    public Direction ROTATION = Direction.NORTH;
    public DefaultAnalogIndicatorBlockEntity(BlockPos pos, BlockState state){
        super(RedstonecgModBlockEntities.DEFAULT_ANALOG_INDICATOR.get(), pos, state);
        //modelUpdate();
    }
    public DefaultAnalogIndicatorBlockEntity(BlockEntityType blockEntityType, BlockPos pos, BlockState state){
        super(blockEntityType, pos, state);
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
        int state = (int) tag.getByte("state");
        this.ROTATION = BlockFrameTransformUtils.decodeIntToDirection(state & 7);
        state >>= 3;
        this.FACING = BlockFrameTransformUtils.decodeIntToDirection(state & 7);
        modelUpdate();
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void modelUpdate(){
        this.rotation = RCGQuaternion.Vector3F.rotateYP((float) -BlockFrameTransformUtils.getRadiansFromDirectionY(this.ROTATION));
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

    public static class DefaultAnalogIndicatorBlockEntityRenderer implements BlockEntityRenderer<DefaultAnalogIndicatorBlockEntity> {
        BlockEntityRendererProvider.Context context;
        public DefaultAnalogIndicatorBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
            super();
            this.context = context;
        }

        @Override
        public void render(DefaultAnalogIndicatorBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
            BlockState blockState = blockEntity.getBlockState();
            BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(blockState);

            poseStack.pushPose();
            poseStack.translate(0.5, 0.5, 0.5);

            //RedstonecgMod.LOGGER.debug("{} {} {} {} {}",blockEntity.FACING,blockEntity.ROTATION,blockEntity.facing,blockEntity.rotation,blockEntity.facingMode);
            switch (blockEntity.facingMode){
                case 5 -> poseStack.mulPose(blockEntity.facing.quaternion);
                case 0 -> {}
                default -> {
                    poseStack.mulPose(blockEntity.facing.quaternion);
                    poseStack.mulPose(RCGQuaternion.Vector3F.rotateXP((float) (Math.PI * 0.5)).quaternion);
                }
            }
            poseStack.mulPose(blockEntity.rotation.quaternion);

            poseStack.translate(-0.5, -0.5, -0.5);
            VertexConsumer vc = bufferSource.getBuffer(RenderType.cutoutMipped());
            Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(
                    poseStack.last(),
                    vc,
                    blockState,
                    model,
                    1.0f, 1.0f, 1.0f,
                    packedLight,
                    packedOverlay
            );

            poseStack.popPose();
        }
    }
}
