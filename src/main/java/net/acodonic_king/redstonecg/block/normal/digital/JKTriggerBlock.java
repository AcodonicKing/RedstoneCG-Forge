
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

public class JKTriggerBlock extends DefaultDigitalInteractibleTriggerGate {
	public static final IntegerProperty CONNECTION = IntegerProperty.create("connection",0,5);
	public JKTriggerBlock() {
		super();
	}
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(CONNECTION);
	}
	@Override
	public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
		ConnectionFace connectionFaceB = BlockFrameTransformUtils.canConnectRedstoneTargetConnectionFace(world, pos, side);
		return CanConnectWallGateProcedure.To4Gate(state, connectionFaceB);
	}
	@Override
	public int onRedstoneUpdate(LevelAccessor level, BlockState blockState, BlockPos pos){
		Level world = (Level) level;
		if (world.getBlockEntity(pos) instanceof DefaultDigitalTriggerGateBlockEntity be) {
			Direction[] Sides = GetGateInputSidesProcedure.Get3ABCGateForth(blockState);
			int [] power = {0,0,0};
			int i = 0;
			for(Direction side: Sides){
				ConnectionFace thisFace = BlockFrameTransformUtils.getConnectionFace(blockState, side);
				power[i] = GetRedstoneSignalProcedure.execute(world, pos, thisFace);
				i++;
			}
			boolean output = be.OUTPUT;
			if(power[2] > 0) {
				if (be.UNLOCKED) {
					boolean SideAPower = power[0] > 0;
					boolean SideBPower = power[1] > 0;
					if (SideAPower && SideBPower) {
						output = !output;
					} else if (SideAPower) {
						output = true;
					} else if (SideBPower) {
						output = false;
					}
					setOutput(world,blockState,pos,output);
					be.UNLOCKED = false;
					be.setChanged();
				}
			} else {
				be.UNLOCKED = true;
				be.setChanged();
			}
		}
		return 0;
	}
}
