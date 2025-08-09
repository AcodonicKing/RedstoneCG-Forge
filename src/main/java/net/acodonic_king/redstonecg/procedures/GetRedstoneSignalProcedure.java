package net.acodonic_king.redstonecg.procedures;

import net.acodonic_king.redstonecg.block.defaults.DefaultRedstoneActionGate;
import net.acodonic_king.redstonecg.block.defaults.RedstoneSignalInterface;
import net.acodonic_king.redstonecg.block.defaults.WireInterface;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

public class GetRedstoneSignalProcedure {
	/**
	 * Reads redstone signal
	 * @param world
	 * @param requesterPos The position of the requester
	 * @param requesterFace Requesters Connection Face
	 * @return redstone signal in redstone units
	 */
	public static int execute(LevelAccessor world, BlockPos requesterPos, ConnectionFace requesterFace){
		BlockPos targetPos = requesterPos.relative(requesterFace.FACE);
		BlockState targetState = world.getBlockState(targetPos);
		Block targetBlock = targetState.getBlock();
		if(targetBlock instanceof RedstoneSignalInterface si){
			return si.getRedstonePower(world, targetPos, requesterFace);
		}
		int SidePower = world instanceof Level _lvl_getRedPow ? _lvl_getRedPow.getSignal(targetPos, requesterFace.FACE) : 0;
		if (SidePower == 0) {
			if (targetState.is(Blocks.REDSTONE_WIRE)) {
				SidePower = LittleTools.getIntegerProperty(targetState,"power");
			}
		}
		return SidePower;
	}

	/**
	 * Reads redstone signal based on the requester local direction
	 * @param world
	 * @param requesterPos
	 * @param requesterLocalDirection
	 * @return
	 */
	public static int execute(LevelAccessor world, BlockPos requesterPos, Direction requesterLocalDirection){
		ConnectionFace sourceFace = BlockFrameTransformUtils.getConnectionFace(world.getBlockState(requesterPos), requesterLocalDirection);
		return execute(world, requesterPos, sourceFace);
	}

	/**
	 * Reads redstone signal based on the requester world direction
	 * @param world
	 * @param requesterPos
	 * @param requesterWorldDirection
	 * @return
	 */
	public static int executeWorldDirection(LevelAccessor world, BlockPos requesterPos, Direction requesterWorldDirection){
		ConnectionFace sourceFace = BlockFrameTransformUtils.getConnectionFaceWorldSide(world.getBlockState(requesterPos), requesterWorldDirection);
		return execute(world, requesterPos, sourceFace);
	}

	/**
	 * Reads redstone signal as a wire.
	 * @param world
	 * @param requesterPos
	 * @param requesterFace
	 * @return
	 */
	public static int executeWire(LevelAccessor world, BlockPos requesterPos, ConnectionFace requesterFace){
		BlockPos targetPos = requesterPos.relative(requesterFace.FACE);
		BlockState targetState = world.getBlockState(targetPos);
		Block targetBlock = targetState.getBlock();
		if(targetBlock instanceof WireInterface si){
			return si.getWirePower(world, targetPos, requesterFace);
		} else if (targetBlock instanceof RedstoneSignalInterface si){
			return si.getRedstonePower(world, targetPos, requesterFace) << 4;
		}
		int SidePower = world instanceof Level _lvl_getRedPow ? _lvl_getRedPow.getSignal(targetPos, requesterFace.FACE) : 0;
		if (SidePower == 0) {
			if (targetState.is(Blocks.REDSTONE_WIRE)) {
				SidePower = LittleTools.getIntegerProperty(targetState,"power");
			}
		}
		return SidePower << 4;
	}
}
