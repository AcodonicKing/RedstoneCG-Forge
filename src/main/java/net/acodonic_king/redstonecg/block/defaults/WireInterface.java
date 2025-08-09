package net.acodonic_king.redstonecg.block.defaults;

import net.acodonic_king.redstonecg.procedures.ConnectionFace;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;

public interface WireInterface {
    int getWirePower(LevelAccessor world, BlockPos pos, ConnectionFace requesterFace);
    void rotationBracket(LevelAccessor world, BlockPos pos, boolean clockwise);
    void onTick(LevelAccessor world, BlockPos pos);
    void onTick(LevelAccessor world, BlockPos pos, int power);
}
