package net.acodonic_king.redstonecg.block.control_panel.render;

import net.acodonic_king.redstonecg.block.control_panel.logic.DefaultPanelLogic;
import net.acodonic_king.redstonecg.block.control_panel.logic.LeverPanelLogic;
import net.acodonic_king.redstonecg.block.entity.ControlPanelBlockEntityRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;

public class LeverPanelRender implements DefaultPanelRender{
    @Override
    public void render(ControlPanelBlockEntityRenderer ber, DefaultPanelLogic dpl) {
        if(dpl instanceof LeverPanelLogic pl) {
            if(pl.MODEL == null)
                return;
            ber.POSE_STACK.pushPose();
            ber.applySlotTransform();
            BakedModel model = ber.getModel(pl.OUTPUT ? modelOn(pl) : modelOff(pl));
            ber.render(model, 1, 1, 1);
            ber.POSE_STACK.popPose();
        }
    }

    public ModelResourceLocation modelOff(LeverPanelLogic pl){
        return new ModelResourceLocation(pl.MODEL, "face=floor,facing=north,powered=false");
    }

    public ModelResourceLocation modelOn(LeverPanelLogic pl){
        return new ModelResourceLocation(pl.MODEL, "face=floor,facing=north,powered=true");
    }
}
