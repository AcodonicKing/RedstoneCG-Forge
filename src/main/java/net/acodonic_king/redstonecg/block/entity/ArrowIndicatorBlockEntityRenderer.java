package net.acodonic_king.redstonecg.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.acodonic_king.redstonecg.block.defaults.PinMarkConnectionInterface;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public class ArrowIndicatorBlockEntityRenderer implements BlockEntityRenderer<ArrowIndicatorBlockEntity> {
    BlockEntityRendererProvider.Context context;
    public static final ModelResourceLocation[] PINMARK_MODELS = new ModelResourceLocation[]{
            new ModelResourceLocation(new ResourceLocation("redstonecg", "arrow_indicator"), "connection=8,waterlogged=false"),
            new ModelResourceLocation(new ResourceLocation("redstonecg", "arrow_indicator"), "connection=9,waterlogged=false"),
            new ModelResourceLocation(new ResourceLocation("redstonecg", "arrow_indicator"), "connection=10,waterlogged=false"),
            new ModelResourceLocation(new ResourceLocation("redstonecg", "arrow_indicator"), "connection=11,waterlogged=false"),
            new ModelResourceLocation(new ResourceLocation("redstonecg", "arrow_indicator"), "connection=12,waterlogged=false"),
    };
    public ArrowIndicatorBlockEntityRenderer(BlockEntityRendererProvider.Context context){
        super();
        this.context = context;
    }
    @Override
    public void render(ArrowIndicatorBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ModelManager modelManager = Minecraft.getInstance().getModelManager();
        BlockState blockState = blockEntity.getBlockState();
        ModelBlockRenderer modelRenderer = Minecraft.getInstance().getBlockRenderer().getModelRenderer();
        BakedModel model;
        VertexConsumer vc = bufferSource.getBuffer(RenderType.cutoutMipped());

        renderPose(blockEntity, poseStack);
        model = modelManager.getModel(new ModelResourceLocation(blockEntity.BLOCK, blockEntity.BASE_MODEL));
        renderModel(blockEntity, modelRenderer, vc, blockState, model, poseStack, packedLight, packedOverlay);
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
            model = modelManager.getModel(PINMARK_MODELS[i]);
            renderModel(blockEntity, modelRenderer, vc, blockState, model, poseStack, packedLight, packedOverlay);
        }
        poseStack.translate(
                blockEntity.ARROW_MODEL_POSITION[0],
                blockEntity.ARROW_MODEL_POSITION[1],
                blockEntity.ARROW_MODEL_POSITION[2]
        );
        poseStack.mulPose(RCGQuaternion.Vector3F.rotateYP(blockEntity.ARROW_MODEL_POSITION[3]).quaternion);
        model = modelManager.getModel(new ModelResourceLocation(blockEntity.BLOCK, blockEntity.ARROW_MODEL));
        renderModel(blockEntity, modelRenderer, vc, blockState, model, poseStack, packedLight, packedOverlay);
        poseStack.popPose();
    }
    private void renderPose(ArrowIndicatorBlockEntity blockEntity, PoseStack poseStack){
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
        poseStack.mulPose(blockEntity.rotation.quaternion);
        poseStack.translate(-0.5, -0.5, -0.5);
    }
    private void renderModel(
            ArrowIndicatorBlockEntity blockEntity,
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
}
