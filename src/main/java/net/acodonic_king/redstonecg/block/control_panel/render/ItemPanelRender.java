package net.acodonic_king.redstonecg.block.control_panel.render;

import net.acodonic_king.redstonecg.block.control_panel.logic.DefaultPanelLogic;
import net.acodonic_king.redstonecg.block.entity.ControlPanelBlockEntityRenderer;
import net.acodonic_king.redstonecg.procedures.RCGMatrix;

public class ItemPanelRender implements DefaultPanelRender{
    @Override
    public void render(ControlPanelBlockEntityRenderer ber, DefaultPanelLogic dpl) {
        ber.POSE_STACK.pushPose();
        ber.applySlotTransform();
        RCGMatrix.M4F mat = new RCGMatrix.M4F()
                .translate(0.5f, 0.03125f, 0.5f)
                .scale(0.5f,0.5f,0.5f)
                .rotateX((float) (Math.PI * 0.5))
                .rotateZ((float) (Math.PI))
                ;
        ber.POSE_STACK.mulPoseMatrix(mat.getMatrix());
        ber.renderItem(dpl.ITEM_STACK.getItem());
        ber.POSE_STACK.popPose();
    }
}
