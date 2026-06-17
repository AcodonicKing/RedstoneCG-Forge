package net.acodonic_king.redstonecg.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.acodonic_king.redstonecg.block.defaults.PinMarkConnectionInterface;
import net.acodonic_king.redstonecg.procedures.RCGMatrix;
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

import static net.acodonic_king.redstonecg.block.entity.DefaultAnalogIndicatorBlockEntityRenderer.lampTextRender;

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
    RCGMatrix.M4F transformer = new RCGMatrix.M4F();
    RCGMatrix.M4F transformer2 = new RCGMatrix.M4F();
    @Override
    public void render(ArrowIndicatorBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ModelManager modelManager = Minecraft.getInstance().getModelManager();
        BlockState blockState = blockEntity.getBlockState();
        ModelBlockRenderer modelRenderer = Minecraft.getInstance().getBlockRenderer().getModelRenderer();
        BakedModel model;
        VertexConsumer vc = bufferSource.getBuffer(RenderType.cutoutMipped());

        OrientationHolderBlockEntity.getPoseStackMatrix(
                transformer.identity(),
                blockEntity.combination
        );
        renderPose(poseStack);
        model = modelManager.getModel(DefaultAnalogIndicatorBlockEntityRenderer.SMOOTH_STONE_PLATE);
        renderModel(blockEntity, modelRenderer, vc, blockState, model, poseStack, packedLight, packedOverlay);
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
        poseStack.mulPoseMatrix(transformer2.identity().rotateY(blockEntity.ARROW_MODEL_POSITION[3]).getMatrix());
        model = modelManager.getModel(new ModelResourceLocation(blockEntity.BLOCK, blockEntity.ARROW_MODEL));
        renderModel(blockEntity, modelRenderer, vc, blockState, model, poseStack, packedLight, packedOverlay);
        poseStack.popPose();

        renderPose(poseStack);
        if(!blockEntity.RENDER_TEXT.isEmpty())
            lampTextRender(blockEntity, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }
    private void renderPose(PoseStack poseStack){
        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPoseMatrix(transformer.getMatrix());
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
