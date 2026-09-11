package net.acodonic_king.redstonecg.block.defaults;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;

public interface WireInterface {
    int getWirePower(LevelAccessor world, BlockPos pos, byte requesterFace);
    void rotationBracket(LevelAccessor world, BlockPos pos, boolean clockwise);
    void onTick(LevelAccessor world, BlockPos pos, int recursion);
    void onTick(LevelAccessor world, BlockPos pos, int power, int recursion);
}
