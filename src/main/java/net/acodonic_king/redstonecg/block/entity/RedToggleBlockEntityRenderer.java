package net.acodonic_king.redstonecg.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.acodonic_king.redstonecg.block.defaults.DefaultGate;
import net.acodonic_king.redstonecg.block.normal.interaction.RedSwitchBlock;
import net.acodonic_king.redstonecg.procedures.RCGMatrix;
import net.acodonic_king.redstonecg.procedures.TextFormatClientTools;
import net.acodonic_king.redstonecg.procedures.TextFormatProcedure;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

public class RedToggleBlockEntityRenderer implements BlockEntityRenderer<RedToggleBlockEntity> {
    BlockEntityRendererProvider.Context context;
    public RedToggleBlockEntityRenderer(BlockEntityRendererProvider.Context context){
        super();
        this.context = context;
    }

    RCGMatrix.M4F transformer = new RCGMatrix.M4F();
    RCGMatrix.M4F transformer2 = new RCGMatrix.M4F();
    @Override
    public void render(RedToggleBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if(blockEntity.RENDER_TEXT.isEmpty())
            return;
        BlockState blockState = blockEntity.getBlockState();
        if(blockState.getBlock() instanceof DefaultGate dg){
            byte combination = OrientationHolderBlockEntity.getCombination(
                    blockState.getValue(DefaultGate.FACING),
                    blockState.getValue(DefaultGate.ROTATION)
            );
            transformer.identity().translate(0.5f, 0.0f, 0.5f);
            OrientationHolderBlockEntity.getPoseStackMatrix(transformer, combination);
            transformer.mul(AnalogSourceBlockEntityRenderer.text_rotate);
        }
        Font font = Minecraft.getInstance().font;
        TextFormatProcedure.ComposedText text = blockEntity.RENDER_TEXT;

        {
            poseStack.pushPose();
            poseStack.mulPoseMatrix(transformer.getMatrix());
            TextFormatClientTools.topText(text, 0xFFFFFF, 1f, 0.25f, 0.5f, font, poseStack, bufferSource, packedLight);
            poseStack.popPose();
        }
        if(text.lines() > 1){
            poseStack.pushPose();
            poseStack.mulPoseMatrix(transformer.getMatrix());
            TextFormatClientTools.bottomText(text, 0xFFFFFF, 1f, 0.25f, 0.5f, font, poseStack, bufferSource, packedLight);
            poseStack.popPose();
        }
        if(text.lines() > 2){
            poseStack.pushPose();
            poseStack.mulPoseMatrix(transformer.getMatrix());
            poseStack.translate(0, 0.5f, 0);
            if(blockState.getBlock() instanceof RedSwitchBlock sb) {
                sb.stateTransformer(transformer2.identity(), blockState);
                poseStack.mulPoseMatrix(transformer2.getMatrix());
            }
            TextFormatClientTools.midText(text, 0xFFFFFF, 0.5f, 0.5f, font, poseStack, bufferSource, packedLight);
            poseStack.popPose();
        }
    }
}
