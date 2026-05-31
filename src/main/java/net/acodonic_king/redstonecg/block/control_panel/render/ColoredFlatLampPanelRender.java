package net.acodonic_king.redstonecg.block.control_panel.render;

import net.acodonic_king.redstonecg.block.control_panel.logic.ColoredLampPanelLogic;
import net.acodonic_king.redstonecg.block.control_panel.logic.DefaultPanelLogic;
import net.acodonic_king.redstonecg.block.entity.ControlPanelBlockEntityRenderer;

public class ColoredFlatLampPanelRender implements DefaultPanelRender{
    @Override
    public void render(ControlPanelBlockEntityRenderer ber, DefaultPanelLogic dpl) {
        if(dpl instanceof ColoredLampPanelLogic pl) {
            if(pl.BLOCK_STATE == null)
                return;
            ber.POSE_STACK.pushPose();
            ber.applySlotTransform();
            ber.POSE_STACK.translate(0, -0.120f, 0);
            float r = (pl.COLOR[0] & 0xFF) / 255f;
            float g = (pl.COLOR[1] & 0xFF) / 255f;
            float b = (pl.COLOR[2] & 0xFF) / 255f;
            ber.renderBlockState(pl.BLOCK_STATE, r, g, b);
            ber.POSE_STACK.popPose();
        }
    }
}
