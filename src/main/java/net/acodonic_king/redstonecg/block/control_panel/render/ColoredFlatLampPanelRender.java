package net.acodonic_king.redstonecg.block.control_panel.render;

import net.acodonic_king.redstonecg.block.control_panel.logic.ColoredLampPanelLogic;
import net.acodonic_king.redstonecg.block.control_panel.logic.DefaultPanelLogic;
import net.acodonic_king.redstonecg.block.entity.ControlPanelBlockEntityRenderer;
import net.acodonic_king.redstonecg.procedures.TextFormatClientTools;

import static net.acodonic_king.redstonecg.block.entity.DefaultAnalogIndicatorBlockEntityRenderer.text_rotate;

public class ColoredFlatLampPanelRender implements DefaultPanelRender{
    @Override
    public void render(ControlPanelBlockEntityRenderer ber, DefaultPanelLogic dpl) {
        if(dpl instanceof ColoredLampPanelLogic pl) {
            if(pl.BLOCK_STATE == null)
                return;
            ber.POSE_STACK.pushPose();
            ber.applySlotTransform();
            ber.POSE_STACK.translate(0.5f, 0.0f,0.5f);
            ber.POSE_STACK.scale(ber.slotSizeX(), Math.min(ber.slotSizeX(), ber.slotSizeZ()), ber.slotSizeZ());
            ber.POSE_STACK.translate(-0.5f, -0.120f,-0.5f);
            float r = (pl.COLOR[0] & 0xFF) / 255f;
            float g = (pl.COLOR[1] & 0xFF) / 255f;
            float b = (pl.COLOR[2] & 0xFF) / 255f;
            ber.renderBlockState(pl.BLOCK_STATE, r, g, b);
            ber.POSE_STACK.popPose();
        }
    }

    @Override
    public void renderText(ControlPanelBlockEntityRenderer ber, DefaultPanelLogic dpl) {
        if(dpl instanceof ColoredLampPanelLogic pl) {
            ber.POSE_STACK.pushPose();
            ber.applySlotTransform();
            ber.POSE_STACK.translate(0.5f, 0.0f,0.5f);
            ber.POSE_STACK.scale(ber.slotSizeX(), Math.min(ber.slotSizeX(), ber.slotSizeZ()), ber.slotSizeZ());
            ber.POSE_STACK.translate(-0.5f, -0.120f,-0.5f);
            ber.POSE_STACK.mulPoseMatrix(text_rotate.getMatrix());
            ber.POSE_STACK.translate(0, 0, -0.094f);
            TextFormatClientTools.fullText(
                    pl.RENDER_TEXT, 0xFFFFFF, 0.75f, 0.75f,
                    ber.FONT, ber.POSE_STACK, ber.BUFFER_SOURCE, ber.PACKED_LIGHT
            );
            ber.POSE_STACK.popPose();
        }
    }
}
