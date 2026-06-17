package net.acodonic_king.redstonecg.block.control_panel.render;

import net.acodonic_king.redstonecg.block.control_panel.BlockStateRenderParams;
import net.acodonic_king.redstonecg.block.control_panel.logic.ColoredLampPanelLogic;
import net.acodonic_king.redstonecg.block.control_panel.logic.DefaultPanelLogic;
import net.acodonic_king.redstonecg.block.entity.ControlPanelBlockEntityRenderer;
import net.acodonic_king.redstonecg.procedures.RCGMatrix;
import net.acodonic_king.redstonecg.procedures.TextFormatClientTools;
import net.acodonic_king.redstonecg.procedures.TextFormatProcedure;

import static net.acodonic_king.redstonecg.block.entity.AnalogSourceBlockEntityRenderer.text_rotate;

public class ColoredLampPanelRender implements DefaultPanelRender{
    static RCGMatrix.M4F transformer = new RCGMatrix.M4F();

    @Override
    public void render(ControlPanelBlockEntityRenderer ber, DefaultPanelLogic dpl) {
        if(dpl instanceof ColoredLampPanelLogic pl)
            renderBlockState(ber, pl);
    }

    @Override
    public void renderText(ControlPanelBlockEntityRenderer ber, DefaultPanelLogic dpl) {
        if(dpl instanceof ColoredLampPanelLogic pl)
            textRender(ber, pl.RENDER_TEXT);
    }

    public static void renderBlockState(ControlPanelBlockEntityRenderer ber, BlockStateRenderParams params){
        if(params.getBlockState() == null)
            return;
        ber.POSE_STACK.pushPose();
        ber.applySlotTransform();
        ber.POSE_STACK.translate(params.move(), -params.move(), params.move());
        ber.POSE_STACK.scale(params.scale(), params.scale(), params.scale());
        ber.renderBlockState(params.getBlockState(), params.r(), params.g(), params.b());
        ber.POSE_STACK.popPose();
    }

    public static void textRender(ControlPanelBlockEntityRenderer ber, TextFormatProcedure.ComposedText text){
        if(text.isEmpty())
            return;
        float z = ber.slotSizeZ() / 2;
        float h = (ber.slotSizeZ() - 0.5f) / 2;
        ber.POSE_STACK.pushPose();
        ber.applySlotTransform();
        transformer.identity().translate(0.5f, -0.125f, 0.5f).mul(text_rotate);
        ber.POSE_STACK.mulPoseMatrix(transformer.getMatrix());
        TextFormatClientTools.topText(
                text, 0xFFFFFF, ber.slotSizeX(), h, z, ber.FONT,
                ber.POSE_STACK, ber.BUFFER_SOURCE, ber.PACKED_LIGHT
        );
        ber.POSE_STACK.popPose();
        if(text.lines() > 1){
            ber.POSE_STACK.pushPose();
            ber.applySlotTransform();
            ber.POSE_STACK.mulPoseMatrix(transformer.getMatrix());
            TextFormatClientTools.bottomText(
                    text, 0xFFFFFF, ber.slotSizeX(), h, z, ber.FONT,
                    ber.POSE_STACK, ber.BUFFER_SOURCE, ber.PACKED_LIGHT
            );
            ber.POSE_STACK.popPose();
        }
        if(text.lines() > 2){
            ber.POSE_STACK.pushPose();
            ber.applySlotTransform();
            transformer.identity().translate(0.5f, 0.125f, 1.0f).mul(text_rotate);
            ber.POSE_STACK.mulPoseMatrix(transformer.getMatrix());
            TextFormatClientTools.midText(
                    text, 0xFFFFFF, 0.5f, 0.5f, ber.FONT,
                    ber.POSE_STACK, ber.BUFFER_SOURCE, ber.PACKED_LIGHT
            );
            ber.POSE_STACK.popPose();
        }
    }
}
