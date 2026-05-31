package net.acodonic_king.redstonecg.block.defaults;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;

public interface RotationBracketInterface {
    void rotationBracket(LevelAccessor world, BlockPos pos, boolean cw);
}
