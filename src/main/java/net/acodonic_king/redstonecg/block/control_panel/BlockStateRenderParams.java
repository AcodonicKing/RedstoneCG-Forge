package net.acodonic_king.redstonecg.block.control_panel;

import net.minecraft.world.level.block.state.BlockState;

public interface BlockStateRenderParams {
    BlockState getBlockState();
    float scale();
    float move();
    float r();
    float g();
    float b();
}
