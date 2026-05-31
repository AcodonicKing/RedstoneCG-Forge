
package net.acodonic_king.redstonecg.block.normal.hybrid;

import net.acodonic_king.redstonecg.block.defaults.DefaultRedstoneActionGate;
import net.acodonic_king.redstonecg.block.defaults.PinMarkConnectionInterface;
import net.acodonic_king.redstonecg.block.entity.DefaultAnalogGateBlockEntity;
import net.acodonic_king.redstonecg.procedures.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import javax.annotation.Nullable;

public class ReversedPathSelectorBlock extends DefaultRedstoneActionGate implements EntityBlock, PinMarkConnectionInterface {
	public static final IntegerProperty CONNECTION = IntegerProperty.create("connection",0,5);
	public static final BooleanProperty DIRECTION = BooleanProperty.create("direction");
	public static final BooleanProperty VISIBLE_STATE = BooleanProperty.create("visible_state");

	public ReversedPathSelectorBlock() {
		super();
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(CONNECTION, VISIBLE_STATE, DIRECTION);
	}

	@Override
	public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
		ConnectionFace connectionFaceB = BlockFrameTransformUtils.canConnectRedstoneTargetConnectionFace(world, pos, side);
		return CanConnectWallGateProcedure.To4Gate(state, connectionFaceB);
	}

	@Override
	public void tick(BlockState blockstate, ServerLevel world, BlockPos pos, RandomSource random){
		LittleTools.setBooleanProperty(world, pos, false, "visible_state", 2);
		onRedstoneUpdate(world,blockstate,pos, 0);
	}

	@Override
	public int onRedstoneUpdate(LevelAccessor world, BlockState blockState, BlockPos pos, int recursion){
		Direction[] Sides = GetGateInputSidesProcedure.Get3ABCGateForth(blockState);
		int SideCPower = GetRedstoneSignalProcedure.execute(world, pos, Sides[2]);
		int SideDPower = 0;
		LittleTools.setBooleanProperty(world, pos, SideCPower > 0, "direction");
		if (SideCPower > 0) {
			SideDPower = GetRedstoneSignalProcedure.execute(world, pos, Sides[1]);
		} else {
			SideDPower = GetRedstoneSignalProcedure.execute(world, pos, Sides[0]);
		}
		setPower(world, blockState, pos, SideDPower, recursion);
		return 0;
	}

	@Override
	public InteractionResult use(BlockState blockstate, Level world, BlockPos pos, Player entity, InteractionHand hand, BlockHitResult hit) {
		InteractionResult interactionResult = super.use(blockstate, world, pos, entity, hand, hit);
		if(interactionResult == InteractionResult.FAIL){return interactionResult;}
		if(AdventureProcedure.pinConfig(world, entity)){
			OnBlockRightClickedProcedure.execute(world, pos, blockstate);
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.FAIL;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new DefaultAnalogGateBlockEntity(pos, state);
	}

	public void setPower(LevelAccessor level, BlockState state, BlockPos pos, int power, int recursion){
		Level world = (Level) level;
		if (world.getBlockEntity(pos) instanceof DefaultAnalogGateBlockEntity be){
			if(be.POWER != power){
				be.POWER = power;
				LittleTools.setBooleanProperty(world, pos, power > 0, "visible_state");
				Direction direction = BlockFrameTransformUtils.getWorldDirectionFromLocalForward(state);
				this.sendRedstoneUpdateInDirection(level,state.getBlock(),pos,direction, recursion);
			}
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState blockState = super.getStateForPlacementDirect(context);
		return blockState;
	}

	@Override
	public String getMeasurement(LevelAccessor world, BlockState blockState, BlockPos pos){
		if(world.getBlockEntity(pos) instanceof DefaultAnalogGateBlockEntity blockEntity){
			return String.format("%1d",blockEntity.POWER);
		}
		return "";
	}

	@Override
	public int getRedstonePower(LevelAccessor world, BlockPos pos, ConnectionFace sourceFace) {
		ConnectionFace thisFace = getOutputConnectionFace(world, pos, sourceFace);
		if(thisFace.canConnect(sourceFace)){
			if(world.getBlockEntity(pos) instanceof DefaultAnalogGateBlockEntity be){
				return be.POWER;
			}
		}
		return 0;
	}

	@Override
	public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos){
		if(world.getBlockEntity(pos) instanceof DefaultAnalogGateBlockEntity be){
			return be.POWER;
		}
		return 0;
	}

	@Override
	public int getConnection(BlockState bs) {
		return bs.getValue(CONNECTION);
	}

	@Override
	public int connectionFilter(int connection) {
		return 0b1111;
	}
}
