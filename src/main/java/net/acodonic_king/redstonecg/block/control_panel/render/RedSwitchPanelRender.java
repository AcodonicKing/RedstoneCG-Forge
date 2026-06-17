package net.acodonic_king.redstonecg.block.control_panel.render;

import net.acodonic_king.redstonecg.block.control_panel.logic.DefaultPanelLogic;
import net.acodonic_king.redstonecg.block.control_panel.logic.RedSwitchPanelLogic;
import net.acodonic_king.redstonecg.block.entity.ControlPanelBlockEntityRenderer;
import net.acodonic_king.redstonecg.procedures.RCGMatrix;
import net.acodonic_king.redstonecg.procedures.TextFormatClientTools;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;

import static net.acodonic_king.redstonecg.block.entity.AnalogSourceBlockEntityRenderer.text_rotate;

public class RedSwitchPanelRender implements DefaultPanelRender{
    public static final ResourceLocation RENDER_OBJECT_BLOCK = new ResourceLocation("redstonecg", "render_object_block");
    RCGMatrix.M4F transformer = new RCGMatrix.M4F();
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

    @Override
    public void renderText(ControlPanelBlockEntityRenderer ber, DefaultPanelLogic dpl) {
        if(dpl instanceof RedSwitchPanelLogic pl) {
            if(pl.RENDER_TEXT.isEmpty())
                return;
            float z = ber.slotSizeZ() / 2;
            float h = (ber.slotSizeZ() - 0.5f) / 2;
            ber.POSE_STACK.pushPose();
            ber.applySlotTransform();
            transformer.identity().translate(0.5f, -0.125f, 0.5f).mul(text_rotate);
            ber.POSE_STACK.mulPoseMatrix(transformer.getMatrix());
            TextFormatClientTools.topText(
                    pl.RENDER_TEXT, 0xFFFFFF, ber.slotSizeX(), h, z, ber.FONT,
                    ber.POSE_STACK, ber.BUFFER_SOURCE, ber.PACKED_LIGHT
            );
            ber.POSE_STACK.popPose();
            if(pl.RENDER_TEXT.lines() > 1){
                ber.POSE_STACK.pushPose();
                ber.applySlotTransform();
                ber.POSE_STACK.mulPoseMatrix(transformer.getMatrix());
                TextFormatClientTools.bottomText(
                        pl.RENDER_TEXT, 0xFFFFFF, ber.slotSizeX(), h, z, ber.FONT,
                        ber.POSE_STACK, ber.BUFFER_SOURCE, ber.PACKED_LIGHT
                );
                ber.POSE_STACK.popPose();
            }
            if(pl.RENDER_TEXT.lines() > 2){
                ber.POSE_STACK.pushPose();
                ber.applySlotTransform();
                transformer.identity().translate(0.5f, -0.125f, 1.0f).mul(text_rotate);
                pl.stateTransformer(transformer, pl.OUTPUT);
                ber.POSE_STACK.mulPoseMatrix(transformer.getMatrix());
                TextFormatClientTools.midText(
                        pl.RENDER_TEXT, 0xFFFFFF, 0.5f, 0.5f, ber.FONT,
                        ber.POSE_STACK, ber.BUFFER_SOURCE, ber.PACKED_LIGHT
                );
                ber.POSE_STACK.popPose();
            }
        }
    }

    public ModelResourceLocation modelOff(){
        return new ModelResourceLocation(RENDER_OBJECT_BLOCK, "model=3");
    }

    public ModelResourceLocation modelOn(){
        return new ModelResourceLocation(RENDER_OBJECT_BLOCK, "model=4");
    }
}
