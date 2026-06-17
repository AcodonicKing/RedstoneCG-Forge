package net.acodonic_king.redstonecg.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.acodonic_king.redstonecg.procedures.RCGMatrix;
import net.acodonic_king.redstonecg.procedures.RCGQuaternion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.world.level.block.state.BlockState;

import static net.acodonic_king.redstonecg.block.entity.DefaultAnalogIndicatorBlockEntityRenderer.lampTextRender;

public class DefaultColoredFlatLampBlockEntityRenderer implements BlockEntityRenderer<DefaultColoredFlatLampBlockEntity> {
    BlockEntityRendererProvider.Context context;
    public DefaultColoredFlatLampBlockEntityRenderer(BlockEntityRendererProvider.Context context){
        super();
        this.context = context;
    }

    RCGMatrix.M4F transformer = new RCGMatrix.M4F();

    @Override
    public void render(DefaultColoredFlatLampBlockEntity blockEntity, float v, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState blockState = blockEntity.getBlockState();

        ModelManager modelManager = Minecraft.getInstance().getModelManager();
        BakedModel model;

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);

        OrientationHolderBlockEntity.getPoseStackMatrix(
                transformer.identity(),
                blockEntity.combination
        );
        poseStack.mulPoseMatrix(transformer.getMatrix());

        poseStack.translate(-0.5, -0.5, -0.5);

        float r = (blockEntity.COLOR[0] & 0xFF) / 255f;
        float g = (blockEntity.COLOR[1] & 0xFF) / 255f;
        float b = (blockEntity.COLOR[2] & 0xFF) / 255f;

        VertexConsumer vc = bufferSource.getBuffer(RenderType.cutoutMipped());
        ModelBlockRenderer modelRenderer = Minecraft.getInstance().getBlockRenderer().getModelRenderer();

        model = modelManager.getModel(DefaultAnalogIndicatorBlockEntityRenderer.SMOOTH_STONE_PLATE);
        modelRenderer.renderModel(
                poseStack.last(),
                vc,
                blockState,
                model,
                r, g, b,
                packedLight,
                packedOverlay
        );

        model = Minecraft.getInstance().getBlockRenderer().getBlockModel(blockState);
        modelRenderer.renderModel(
                poseStack.last(),
                vc,
                blockState,
                model,
                r, g, b,
                packedLight,
                packedOverlay
        );

        if(blockEntity.BASE_READ){
            model = Minecraft.getInstance().getModelManager().getModel(DefaultAnalogIndicatorBlockEntityRenderer.BASE_READ_MODEL);
            modelRenderer.renderModel(
                    poseStack.last(),
                    vc,
                    blockState,
                    model,
                    r, g, b,
                    packedLight,
                    packedOverlay
            );
        }
        if(!blockEntity.RENDER_TEXT.isEmpty())
            lampTextRender(blockEntity, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }
}
