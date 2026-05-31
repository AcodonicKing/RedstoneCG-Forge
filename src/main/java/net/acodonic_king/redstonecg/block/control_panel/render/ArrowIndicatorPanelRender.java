package net.acodonic_king.redstonecg.block.control_panel.render;

import net.acodonic_king.redstonecg.block.control_panel.logic.ArrowIndicatorPanelLogic;
import net.acodonic_king.redstonecg.block.control_panel.logic.DefaultPanelLogic;
import net.acodonic_king.redstonecg.block.entity.ControlPanelBlockEntityRenderer;
import net.acodonic_king.redstonecg.procedures.RCGQuaternion;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;

public class ArrowIndicatorPanelRender implements DefaultPanelRender{
    @Override
    public void render(ControlPanelBlockEntityRenderer ber, DefaultPanelLogic dpl) {
        if(dpl instanceof ArrowIndicatorPanelLogic pl){
            if(pl.BLOCK == null)
                return;
            ber.POSE_STACK.pushPose();
            ber.applySlotTransform();
            ber.POSE_STACK.translate(0, -0.12f, 0);
            BakedModel model = ber.getModel(new ModelResourceLocation(pl.BLOCK,pl.BASE_MODEL));
            ber.render(model, 1, 1, 1);
            ber.POSE_STACK.translate(
                    pl.ARROW_MODEL_POSITION[0],
                    pl.ARROW_MODEL_POSITION[1],
                    pl.ARROW_MODEL_POSITION[2]
            );
            ber.POSE_STACK.mulPose(RCGQuaternion.Vector3F.rotateYP(pl.ARROW_MODEL_POSITION[3]).quaternion);
            model = ber.getModel(new ModelResourceLocation(pl.BLOCK, pl.ARROW_MODEL));
            ber.render(model, 1, 1, 1);
            ber.POSE_STACK.popPose();
        }
    }
}
