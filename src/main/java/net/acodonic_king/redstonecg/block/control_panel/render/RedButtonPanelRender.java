package net.acodonic_king.redstonecg.block.control_panel.render;

import net.minecraft.client.resources.model.ModelResourceLocation;

public class RedButtonPanelRender extends RedSwitchPanelRender{
    @Override
    public ModelResourceLocation modelOff(){
        return new ModelResourceLocation(RENDER_OBJECT_BLOCK, "model=1");
    }
    @Override
    public ModelResourceLocation modelOn(){
        return new ModelResourceLocation(RENDER_OBJECT_BLOCK, "model=2");
    }
}
