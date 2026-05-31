package net.acodonic_king.redstonecg.block.control_panel.render;

import net.acodonic_king.redstonecg.block.control_panel.logic.AnalogSourcePanelLogic;
import net.acodonic_king.redstonecg.block.control_panel.logic.DefaultPanelLogic;
import net.acodonic_king.redstonecg.block.entity.AnalogSourceBlockEntityRenderer;
import net.acodonic_king.redstonecg.block.entity.ControlPanelBlockEntityRenderer;
import net.acodonic_king.redstonecg.procedures.RCGMatrix;
import net.minecraft.client.resources.model.BakedModel;

public class AnalogSourcePanelRender implements DefaultPanelRender{
    @Override
    public void render(ControlPanelBlockEntityRenderer ber, DefaultPanelLogic dpl) {
        ber.POSE_STACK.pushPose();
        ber.applySlotTransform();
        BakedModel model = ber.getModel(AnalogSourceBlockEntityRenderer.KNOB_MODEL);
        float angle = getAngle(dpl);
        RCGMatrix.M4F mat = new RCGMatrix.M4F().translate(0.5f, 0.375f, 0.5f).rotateY(angle);
        ber.POSE_STACK.mulPoseMatrix(mat.getMatrix());
        ber.render(model, 1, 1, 1);
        ber.POSE_STACK.popPose();
    }
    public float getAngle(DefaultPanelLogic dpl){
        if(dpl instanceof AnalogSourcePanelLogic pl){
            float angle = (float) (pl.POWER - pl.POWER_RANGE[0]) / (pl.POWER_RANGE[1] - pl.POWER_RANGE[0] + 1);
            angle = 0.5f - angle;
            angle *= (float) (2 * Math.PI);
            return angle;
        }
        return 0.0f;
    }
}
