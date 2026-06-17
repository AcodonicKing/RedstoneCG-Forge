package net.acodonic_king.redstonecg.block.control_panel.render;

import net.acodonic_king.redstonecg.block.control_panel.logic.ArrowIndicatorPanelLogic;
import net.acodonic_king.redstonecg.block.control_panel.logic.DefaultPanelLogic;
import net.acodonic_king.redstonecg.block.entity.ControlPanelBlockEntityRenderer;
import net.acodonic_king.redstonecg.procedures.RCGMatrix;
import net.acodonic_king.redstonecg.procedures.RCGQuaternion;
import net.acodonic_king.redstonecg.procedures.TextFormatClientTools;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;

import static net.acodonic_king.redstonecg.block.entity.DefaultAnalogIndicatorBlockEntityRenderer.text_rotate;

public class ArrowIndicatorPanelRender implements DefaultPanelRender{
    RCGMatrix.M4F transformer = new RCGMatrix.M4F();
    @Override
    public void render(ControlPanelBlockEntityRenderer ber, DefaultPanelLogic dpl) {
        if(dpl instanceof ArrowIndicatorPanelLogic pl){
            if(pl.BLOCK == null)
                return;
            ber.POSE_STACK.pushPose();
            ber.applySlotTransform();
            ber.POSE_STACK.translate(0.5f, 0.0f,0.5f);
            ber.POSE_STACK.scale(ber.slotSizeX(), Math.min(ber.slotSizeX(), ber.slotSizeZ()), ber.slotSizeZ());
            ber.POSE_STACK.translate(-0.5f, 0.0f,-0.5f);
            ber.POSE_STACK.translate(0, -0.12f, 0);
            BakedModel model = ber.getModel(new ModelResourceLocation(pl.BLOCK,pl.BASE_MODEL));
            ber.render(model, 1, 1, 1);
            transformer
                    .identity()
                    .translate(pl.ARROW_MODEL_POSITION[0], pl.ARROW_MODEL_POSITION[1], pl.ARROW_MODEL_POSITION[2])
                    .rotateY(pl.ARROW_MODEL_POSITION[3])
            ;
            ber.POSE_STACK.mulPoseMatrix(transformer.getMatrix());
            model = ber.getModel(new ModelResourceLocation(pl.BLOCK, pl.ARROW_MODEL));
            ber.render(model, 1, 1, 1);
            ber.POSE_STACK.popPose();
        }
    }

    @Override
    public void renderText(ControlPanelBlockEntityRenderer ber, DefaultPanelLogic dpl) {
        if(dpl instanceof ArrowIndicatorPanelLogic pl){
            ber.POSE_STACK.pushPose();
            ber.applySlotTransform();
            ber.POSE_STACK.translate(0, -0.12f, 0);
            ber.POSE_STACK.mulPoseMatrix(text_rotate.getMatrix());
            TextFormatClientTools.fullText(
                    pl.RENDER_TEXT, 0xFFFFFF, ber.slotSizeX(), ber.slotSizeZ(),
                    ber.FONT, ber.POSE_STACK, ber.BUFFER_SOURCE, ber.PACKED_LIGHT
            );
            ber.POSE_STACK.popPose();
        }
    }
}
