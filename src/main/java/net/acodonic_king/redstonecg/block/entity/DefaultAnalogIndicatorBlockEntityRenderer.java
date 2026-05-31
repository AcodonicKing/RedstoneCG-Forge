package net.acodonic_king.redstonecg.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public class DefaultAnalogIndicatorBlockEntityRenderer implements BlockEntityRenderer<DefaultAnalogIndicatorBlockEntity> {
    public static final ModelResourceLocation BASE_READ_MODEL = ArrowIndicatorBlockEntityRenderer.PINMARK_MODELS[4];
    public static final ModelResourceLocation SMOOTH_STONE_PLATE = new ModelResourceLocation(new ResourceLocation("redstonecg","smooth_stone_plate"),"facing=down,rotation=north,waterlogged=false");
    BlockEntityRendererProvider.Context context;
    public DefaultAnalogIndicatorBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super();
        this.context = context;
    }

    @Override
    public void render(DefaultAnalogIndicatorBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState blockState = blockEntity.getBlockState();

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);

        blockEntity.setPoseStack(poseStack);

        poseStack.translate(-0.5, -0.5, -0.5);
        VertexConsumer vc = bufferSource.getBuffer(RenderType.cutoutMipped());
        ModelBlockRenderer modelRenderer = Minecraft.getInstance().getBlockRenderer().getModelRenderer();
        ModelManager modelManager = Minecraft.getInstance().getModelManager();

        BakedModel model = modelManager.getModel(SMOOTH_STONE_PLATE);

        modelRenderer.renderModel(
                poseStack.last(),
                vc,
                blockState,
                model,
                1.0f, 1.0f, 1.0f,
                packedLight,
                packedOverlay
        );

        model = Minecraft.getInstance().getBlockRenderer().getBlockModel(blockState);
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
            model = modelManager.getModel(BASE_READ_MODEL);
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
