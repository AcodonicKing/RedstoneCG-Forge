package net.acodonic_king.redstonecg.block.defaults;

import net.minecraft.world.level.block.state.BlockState;

public interface PinMarkConnectionInterface {
    int getConnection(BlockState bs);
    int connectionFilter(int connection);
}
