package net.acodonic_king.redstonecg.block.control_panel.render;

import net.acodonic_king.redstonecg.block.control_panel.logic.AnalogIndicatorPanelLogic;
import net.acodonic_king.redstonecg.block.control_panel.logic.DefaultPanelLogic;
import net.acodonic_king.redstonecg.block.entity.ControlPanelBlockEntityRenderer;
import net.minecraft.client.resources.model.BakedModel;

public class AnalogIndicatorPanelRender implements DefaultPanelRender{
    @Override
    public void render(ControlPanelBlockEntityRenderer ber, DefaultPanelLogic dpl) {
        if(dpl instanceof AnalogIndicatorPanelLogic pl) {
            if(pl.BLOCK_STATE == null)
                return;
            ber.POSE_STACK.pushPose();
            ber.applySlotTransform();
            ber.POSE_STACK.translate(0, -0.120f, 0);
            ber.renderBlockState(pl.BLOCK_STATE, 1, 1, 1);
            ber.POSE_STACK.popPose();
        }
    }
}
