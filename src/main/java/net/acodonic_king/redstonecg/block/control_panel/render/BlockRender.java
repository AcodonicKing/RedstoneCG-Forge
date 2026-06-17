package net.acodonic_king.redstonecg.block.control_panel.render;

import net.acodonic_king.redstonecg.block.control_panel.BlockStateRenderParams;
import net.acodonic_king.redstonecg.block.control_panel.ComposedTextInterface;
import net.acodonic_king.redstonecg.block.control_panel.logic.DefaultPanelLogic;
import net.acodonic_king.redstonecg.block.entity.ControlPanelBlockEntityRenderer;

import static net.acodonic_king.redstonecg.block.control_panel.render.ColoredLampPanelRender.renderBlockState;
import static net.acodonic_king.redstonecg.block.control_panel.render.ColoredLampPanelRender.textRender;

public class BlockRender implements DefaultPanelRender {
    @Override
    public void render(ControlPanelBlockEntityRenderer ber, DefaultPanelLogic dpl) {
        if(dpl instanceof BlockStateRenderParams pl)
            renderBlockState(ber, pl);
    }
    @Override
    public void renderText(ControlPanelBlockEntityRenderer ber, DefaultPanelLogic dpl){
        if(dpl instanceof ComposedTextInterface ti)
            textRender(ber, ti.getComposedText());
    }
}
