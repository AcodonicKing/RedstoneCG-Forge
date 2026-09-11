package net.acodonic_king.redstonecg.block.defaults;

import net.acodonic_king.redstonecg.procedures.ConnectionFace;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;

public interface RedstoneSignalInterface {
    /**
     * Must provide redstone power in redstone units.
     * @param world
     * @param pos the position of redstone signal provider
     * @param requesterFace the connection face of the signal requester
     * @return redstone signal in redstone dust units
     */
    int getRedstonePower(LevelAccessor world, BlockPos pos, byte requesterFace);

    /**
     * Must provide relevant output Connection Face.
     *
     * @param world
     * @param pos
     * @param requesterFace the connection face of the requester
     * @return
     */
    byte getOutputConnectionFace(LevelAccessor world, BlockPos pos, byte requesterFace);

    /**
     * Must provide either input or output connection face
     *
     * @param world
     * @param pos
     * @param requesterFace
     * @return
     */
    byte getAnyConnectionFace(LevelAccessor world, BlockPos pos, byte requesterFace);
}
