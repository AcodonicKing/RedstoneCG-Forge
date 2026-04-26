package net.acodonic_king.redstonecg.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.acodonic_king.redstonecg.init.RedstonecgModBlockEntities;
import net.acodonic_king.redstonecg.procedures.BlockFrameTransformUtils;
import net.acodonic_king.redstonecg.procedures.RCGQuaternion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Pair;

public class DefaultAnalogIndicatorBlockEntity extends OrientationHolderBlockEntity {
    public boolean BASE_READ = false;
    public DefaultAnalogIndicatorBlockEntity(BlockPos pos, BlockState state){
        super(RedstonecgModBlockEntities.DEFAULT_ANALOG_INDICATOR.get(), pos, state);
        //modelUpdate();
    }
    public DefaultAnalogIndicatorBlockEntity(BlockEntityType blockEntityType, BlockPos pos, BlockState state){
        super(blockEntityType, pos, state);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean("base_read", BASE_READ);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("base_read"))
            BASE_READ = tag.getBoolean("base_read");
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public static class DefaultAnalogIndicatorBlockEntityRenderer implements BlockEntityRenderer<DefaultAnalogIndicatorBlockEntity> {
        public static final ModelResourceLocation BASE_READ_MODEL = ArrowIndicatorBlockEntity.PINMARK_MODELS[4];
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

            blockEntity.setPoseStack(poseStack);

            poseStack.translate(-0.5, -0.5, -0.5);
            VertexConsumer vc = bufferSource.getBuffer(RenderType.cutoutMipped());
            ModelBlockRenderer modelRenderer = Minecraft.getInstance().getBlockRenderer().getModelRenderer();
            modelRenderer.renderModel(
                    poseStack.last(),
                    vc,
                    blockState,
                    model,
                    1.0f, 1.0f, 1.0f,
                    packedLight,
                    packedOverlay
            );
            if(blockEntity.BASE_READ){
                model = Minecraft.getInstance().getModelManager().getModel(BASE_READ_MODEL);
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
            poseStack.popPose();
        }
    }
}
