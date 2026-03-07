package net.acodonic_king.redstonecg.block.defaults;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;

public interface SupportingFaceInterface {
    boolean faceIsSupporting(LevelAccessor world, BlockPos blockPos, Direction face);
}
