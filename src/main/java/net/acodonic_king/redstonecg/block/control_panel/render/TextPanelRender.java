package net.acodonic_king.redstonecg.block.control_panel.render;

import net.acodonic_king.redstonecg.block.control_panel.PanelRenderRegistry;
import net.acodonic_king.redstonecg.block.control_panel.logic.DefaultPanelLogic;
import net.acodonic_king.redstonecg.block.control_panel.logic.TextPanelLogic;
import net.acodonic_king.redstonecg.block.entity.ControlPanelBlockEntity;
import net.acodonic_king.redstonecg.block.entity.ControlPanelBlockEntityRenderer;
import net.acodonic_king.redstonecg.procedures.TextFormatClientTools;
import net.minecraft.network.chat.Component;

public class TextPanelRender implements DefaultPanelRender{
    @Override
    public void render(ControlPanelBlockEntityRenderer ber, DefaultPanelLogic dpl) {
        if(dpl instanceof TextPanelLogic pl){
            if(!pl.CUSTOM_TEXT){
                PanelRenderRegistry.ITEM_PANEL_RENDER.render(ber, dpl);
                return;
            }
            ber.POSE_STACK.pushPose();
            ber.applySlotTransform();

            int width = TextFormatClientTools.getWidth(pl.TEXT, ber.FONT);
            int height = TextFormatClientTools.getHeight(pl.TEXT);
            pl.calcPaper(width, height);
            int paper_width = pl.getWidth();
            int paper_height = pl.getHeight();
            int size = Math.max(paper_width, paper_height);
            float scale = Math.min(ber.slotSizeX(), ber.slotSizeZ()) / size;
            ber.TRANSFORM_MATRIX
                    .identity()
                    .translate(0.5f,0.015f,0.5f)
                    .rotateX(ControlPanelBlockEntity.ANGLES[1])
                    .scale(scale, scale, 1)
                    .translate(-size*0.5f,-size*0.5f, 0f)
            ;
            ber.POSE_STACK.mulPoseMatrix(ber.TRANSFORM_MATRIX.getMatrix());

            ControlPanelBlockEntityRenderer.Rectangle.setPoseStack();
            ControlPanelBlockEntityRenderer.Rectangle.setTexture(pl.getPaper());
            int paper_y = (size - paper_height) / 2;
            int paper_x = (size - paper_width) / 2;
            for(TextPanelLogic.Piece piece: pl.PIECES) {
                piece.buildVertex((geom) -> {
                    ControlPanelBlockEntityRenderer.Rectangle.addVertex(geom[0] + paper_x,geom[1] + paper_y, 0.005f,0f,1f,0f,geom[2],geom[3]);
                });
            }
            ControlPanelBlockEntityRenderer.Rectangle.end();
            ber.POSE_STACK.popPose();
        }
    }

    @Override
    public void renderText(ControlPanelBlockEntityRenderer ber, DefaultPanelLogic dpl) {
        if(dpl instanceof TextPanelLogic pl){
            if(!pl.CUSTOM_TEXT)
                return;
            ber.POSE_STACK.pushPose();
            ber.applySlotTransform();

            int height = TextFormatClientTools.getHeight(pl.TEXT);
            int paper_width = pl.getWidth();
            int paper_height = pl.getHeight();
            int size = Math.max(paper_width, paper_height);
            float scale = Math.min(ber.slotSizeX(), ber.slotSizeZ()) / size;
            ber.TRANSFORM_MATRIX
                    .identity()
                    .translate(0.5f,0.015f,0.5f)
                    .rotateX(ControlPanelBlockEntity.ANGLES[1])
                    .scale(scale, scale, 1)
                    .translate(-size*0.5f,-size*0.5f, 0f)
            ;
            ber.POSE_STACK.mulPoseMatrix(ber.TRANSFORM_MATRIX.getMatrix());

            int y = 2 + ((size - height) / 2);
            for(Component line: pl.TEXT.getLines()) {
                int x = TextFormatClientTools.getWidth(line, ber.FONT);
                x = (size - x) / 2;
                ber.renderText(line.getVisualOrderText(), x, y, pl.getTextColor());
                y += 10;
            }
            ber.POSE_STACK.popPose();
        }
    }
}
