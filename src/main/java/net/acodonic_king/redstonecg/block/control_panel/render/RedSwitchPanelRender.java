package net.acodonic_king.redstonecg.block.control_panel.render;

import net.acodonic_king.redstonecg.block.control_panel.logic.DefaultPanelLogic;
import net.acodonic_king.redstonecg.block.control_panel.logic.RedSwitchPanelLogic;
import net.acodonic_king.redstonecg.block.entity.ControlPanelBlockEntityRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;

public class RedSwitchPanelRender implements DefaultPanelRender{
    public static final ResourceLocation RENDER_OBJECT_BLOCK = new ResourceLocation("redstonecg", "render_object_block");

    @Override
    public void render(ControlPanelBlockEntityRenderer ber, DefaultPanelLogic dpl) {
        if(dpl instanceof RedSwitchPanelLogic pl) {
            ber.POSE_STACK.pushPose();
            ber.applySlotTransform();
            BakedModel model = ber.getModel(pl.OUTPUT ? modelOn() : modelOff());
            ber.POSE_STACK.translate(0.0f, -0.125f, 0.0f);
            ber.render(model, 1, 1, 1);
            ber.POSE_STACK.popPose();
        }
    }

    public ModelResourceLocation modelOff(){
        return new ModelResourceLocation(RENDER_OBJECT_BLOCK, "model=3");
    }

    public ModelResourceLocation modelOn(){
        return new ModelResourceLocation(RENDER_OBJECT_BLOCK, "model=4");
    }
}
