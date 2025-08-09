
package net.acodonic_king.redstonecg.block.normal.digital;

import net.acodonic_king.redstonecg.block.defaults.DefaultDigitalInteractibleTriggerGate;
import net.acodonic_king.redstonecg.block.entity.DefaultDigitalTriggerGateBlockEntity;
import net.acodonic_king.redstonecg.procedures.*;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class TTriggerBlock extends DefaultDigitalInteractibleTriggerGate {
	//public static final BooleanProperty UNLOCKED = BooleanProperty.create("unlocked");
	public static final IntegerProperty CONNECTION = IntegerProperty.create("connection",0,2);

	public TTriggerBlock() {
		super();
	}

	@Override
	public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
		ConnectionFace connectionFaceB = BlockFrameTransformUtils.canConnectRedstoneTargetConnectionFace(world, pos, side);
		return CanConnectWallGateProcedure.To1Gate(state, connectionFaceB);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(CONNECTION);
	}

	@Override
	public int onRedstoneUpdate(LevelAccessor level, BlockState blockState, BlockPos pos){
		Level world = (Level) level;
		if (world.getBlockEntity(pos) instanceof DefaultDigitalTriggerGateBlockEntity be) {
			Direction Side = GetGateInputSidesProcedure.Get1GateForth(blockState);
			ConnectionFace thisFace = BlockFrameTransformUtils.getConnectionFace(blockState, Side);
			int SidePower = GetRedstoneSignalProcedure.execute(world, pos, thisFace);

			boolean output = be.OUTPUT;
			if (SidePower > 0) {
				if (be.UNLOCKED) {
					output = !output;
					be.UNLOCKED = false;
					setOutput(world,blockState,pos,output);
				}
			} else {
				be.UNLOCKED = true;
				be.setChanged();
			}
			if (output) {return 15;}
		}
		return 0;
	}
}