package net.acodonic_king.redstonecg.block.control_panel.render;

import net.acodonic_king.redstonecg.block.control_panel.logic.DefaultPanelLogic;
import net.acodonic_king.redstonecg.block.entity.ControlPanelBlockEntityRenderer;

public interface DefaultPanelRender {
    void render(ControlPanelBlockEntityRenderer ber, DefaultPanelLogic dpl);
}
