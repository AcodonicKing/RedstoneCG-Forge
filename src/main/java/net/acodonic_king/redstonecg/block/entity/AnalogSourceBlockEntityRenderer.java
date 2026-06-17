package net.acodonic_king.redstonecg.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.acodonic_king.redstonecg.block.defaults.DefaultGate;
import net.acodonic_king.redstonecg.procedures.RCGMatrix;
import net.acodonic_king.redstonecg.procedures.TextFormatClientTools;
import net.acodonic_king.redstonecg.procedures.TextFormatProcedure;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
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

public class AnalogSourceBlockEntityRenderer implements BlockEntityRenderer<AnalogSourceBlockEntity> {
    //public static final ModelResourceLocation BASE_OUT_MODEL = ArrowIndicatorBlockEntity.PINMARK_MODELS[4];
    public static final ModelResourceLocation KNOB_MODEL = new ModelResourceLocation(new ResourceLocation("redstonecg", "render_object_block"), "model=0");

    BlockEntityRendererProvider.Context context;
    public AnalogSourceBlockEntityRenderer(BlockEntityRendererProvider.Context context){
        super();
        this.context = context;
    }

    RCGMatrix.M4F transformer = new RCGMatrix.M4F();
    RCGMatrix.M4F transformer2 = new RCGMatrix.M4F();
    public static final RCGMatrix.M4F text_rotate = new RCGMatrix.M4F().rotateX(RCGMatrix.ANGLES[1]).translate(0, -0.5f, -0.13f);

    @Override
    public void render(AnalogSourceBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ModelManager modelManager = Minecraft.getInstance().getModelManager();
        BakedModel model = modelManager.getModel(KNOB_MODEL);
        ModelBlockRenderer modelRenderer = Minecraft.getInstance().getBlockRenderer().getModelRenderer();
        VertexConsumer vc = bufferSource.getBuffer(RenderType.cutoutMipped());
        Font font = Minecraft.getInstance().font;

        BlockState blockState = blockEntity.getBlockState();
        if(blockState.getBlock() instanceof DefaultGate dg){
            byte combination = OrientationHolderBlockEntity.getCombination(
                    blockState.getValue(DefaultGate.FACING),
                    blockState.getValue(DefaultGate.ROTATION)
            );
            OrientationHolderBlockEntity.getPoseStackMatrix(
                    transformer.identity().translate(0.5f, 0.5f, 0.5f),
                    combination
            );
        }
        TextFormatProcedure.ComposedText text = blockEntity.RENDER_TEXT;

        poseStack.pushPose();
        transformer2
                .set(transformer)
                .rotateY(blockEntity.ANGLE)
        ;
        poseStack.mulPoseMatrix(transformer2.getMatrix());
        modelRenderer.renderModel(
                poseStack.last(),
                vc,
                blockState,
                model,
                1.0f, 1.0f, 1.0f,
                packedLight,
                packedOverlay
        );
        if(text.lines() > 2){
            transformer2.identity().rotateY(RCGMatrix.ANGLES[2]).translate(0f, -0.375f, 0.5f).mul(text_rotate);
            poseStack.mulPoseMatrix(transformer2.getMatrix());
            TextFormatClientTools.midText(text, 0xFFFFFF, 0.5f, 0.5f, font, poseStack, bufferSource, packedLight);
        }
        poseStack.popPose();

        if(text.isEmpty())
            return;
        poseStack.pushPose();
        poseStack.mulPoseMatrix(transformer.translate(0.0f, -0.5f, 0.0f).mul(text_rotate).getMatrix());
        TextFormatClientTools.topText(text, 0xFFFFFF, 1f, 0.25f, 0.5f, font, poseStack, bufferSource, packedLight);
        poseStack.popPose();
        if(text.lines() > 1){
            poseStack.pushPose();
            poseStack.mulPoseMatrix(transformer.getMatrix());
            TextFormatClientTools.bottomText(text, 0xFFFFFF, 1f, 0.25f, 0.5f, font, poseStack, bufferSource, packedLight);
            poseStack.popPose();
        }
    }
}
