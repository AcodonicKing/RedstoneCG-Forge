
package net.acodonic_king.redstonecg.block.normal.hybrid;

import net.acodonic_king.redstonecg.block.defaults.DefaultConnectableGate;
import net.acodonic_king.redstonecg.block.defaults.DefaultRedstoneActionGate;
import net.acodonic_king.redstonecg.block.defaults.PinMarkConnectionInterface;
import net.acodonic_king.redstonecg.block.defaults.RedstoneSignalInterface;
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

public class ForwardPathSelectorBlock extends DefaultConnectableGate implements EntityBlock, RedstoneSignalInterface, PinMarkConnectionInterface {
	public static final IntegerProperty CONNECTION = IntegerProperty.create("connection",0,5);
	//public static final IntegerProperty POWER = IntegerProperty.create("power",0,15);
	public static final BooleanProperty DIRECTION = BooleanProperty.create("direction");
	public static final BooleanProperty VISIBLE_STATE = BooleanProperty.create("visible_state");

	public ForwardPathSelectorBlock() {
		super();
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(CONNECTION, VISIBLE_STATE, DIRECTION);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState blockState = super.getStateForPlacementOpposite(context);
		return blockState;
	}

	@Override
	public int getSignal(BlockState blockstate, BlockGetter blockAccess, BlockPos pos, Direction direction) {
		ConnectionFace connectionFaceB = new ConnectionFace(direction); //temporary
		LevelAccessor world = (LevelAccessor) blockAccess;
		ConnectionFace connectionFaceA = getOutputConnectionFace(world, pos, connectionFaceB);
		connectionFaceB = BlockFrameTransformUtils.getRequesterConnectionFace(world, pos.relative(direction.getOpposite()), connectionFaceA, direction.getOpposite());
		if(connectionFaceA.canConnect(connectionFaceB)){
			return getRedstonePower(world, pos, connectionFaceB);
		}
		return 0;
	}

	@Override
	public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
		ConnectionFace connectionFaceB = BlockFrameTransformUtils.canConnectRedstoneTargetConnectionFace(world, pos, side);
		return CanConnectWallGateProcedure.To4Gate(state, connectionFaceB);
	}

	@Override
	public void neighborChanged(BlockState blockstate, Level world, BlockPos pos, Block neighborBlock, BlockPos fromPos, boolean moving) {
		super.neighborChanged(blockstate, world, pos, neighborBlock, fromPos, moving);

		Direction[] Sides = GetGateInputSidesProcedure.Get3ABCGateForth(blockstate);
		int SideCPower = GetRedstoneSignalProcedure.execute(world, pos, Sides[2]);
		int SideDPower = GetRedstoneSignalProcedure.execute(world, pos, Direction.NORTH);
		blockstate = blockstate.setValue(DIRECTION, SideCPower > 0);
		if (world.getBlockEntity(pos) instanceof DefaultAnalogGateBlockEntity be){
			if(be.POWER != SideDPower){
				be.POWER = SideDPower;
				be.setChanged();
				blockstate = blockstate.setValue(VISIBLE_STATE, SideDPower > 0);
			}
		}
		world.setBlock(pos, blockstate, 2);

		Direction direction = BlockFrameTransformUtils.getWorldDirectionFromLocal(blockstate,Sides[0]);
		updateInDirection(world, blockstate.getBlock(), pos, direction);
		direction = BlockFrameTransformUtils.getWorldDirectionFromLocal(blockstate,Sides[1]);
		updateInDirection(world, blockstate.getBlock(), pos, direction);
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

	@Override
	public boolean isOutput(LevelAccessor world, BlockState blockState, BlockPos pos, Direction direction){
		Direction[] Sides = GetGateInputSidesProcedure.Get3ABCGateForth(blockState);
		Direction dir = BlockFrameTransformUtils.getLocalDirectionFromWorld(blockState,direction);
		return (Sides[0] == dir) || (Sides[1] == dir);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new DefaultAnalogGateBlockEntity(pos, state);
	}

	public void updateInDirection(LevelAccessor level, Block thisBlock, BlockPos thisPos, Direction direction){
		Level world = (Level) level;
		BlockPos neighborPos = thisPos.offset(direction.getNormal());
		//RedstonecgMod.LOGGER.debug("Sending update to {}",neighborPos);
		BlockState bs = world.getBlockState(neighborPos);
		Block block = bs.getBlock();
		if (block instanceof DefaultConnectableGate nb){
			if(nb.isOutput(world, bs, neighborPos, direction.getOpposite())){return;}
		}
		if (block instanceof DefaultRedstoneActionGate nb){
			nb.onRedstoneUpdate(world, bs, neighborPos, thisPos);
		} else {
			world.neighborChanged(neighborPos,thisBlock,thisPos);
			//block.neighborChanged(bs,world,neighborPos,thisBlock,thisPos,false);
		}
	}

	@Override
	public boolean isSignalSource(BlockState state) {
		return true;
	}

	@Override
	public void tick(BlockState blockstate, ServerLevel world, BlockPos pos, RandomSource random){
		LittleTools.setBooleanProperty(world, pos, false, "visible_state", 2);
		Block nb = blockstate.getBlock();
		world.neighborChanged(pos,nb,pos);
		//nb.neighborChanged(blockstate,world,pos,nb,pos,false);
	}

	@Override
	public String getMeasurement(LevelAccessor world, BlockState blockState, BlockPos pos){
		if(world.getBlockEntity(pos) instanceof DefaultAnalogGateBlockEntity be){
			int SideAPower = 0;
			int SideBPower = 0;
			if (world.getBlockState(pos).getValue(DIRECTION)) {
				SideBPower = be.POWER;
			} else {
				SideAPower = be.POWER;
			}
			return String.format("\nA= %1d B= %2d",SideAPower,SideBPower);
		}
		return "";
	}

	@Override
	public int getRedstonePower(LevelAccessor world, BlockPos pos, ConnectionFace requesterFace) {
		BlockState thisState = world.getBlockState(pos);
		Direction localDir = BlockFrameTransformUtils.getLocalDirectionFromWorld(thisState, requesterFace.FACE.getOpposite());
		Direction[] Sides = GetGateInputSidesProcedure.Get3ABCGateForth(thisState);
		if(localDir != Sides[0] && localDir != Sides[1]){return 0;}
		ConnectionFace thisFace = BlockFrameTransformUtils.getConnectionFace(thisState, localDir);
		if(!thisFace.canConnect(requesterFace)){return 0;}
		if(world.getBlockEntity(pos) instanceof DefaultAnalogGateBlockEntity be) {
			int SideAPower = 0;
			int SideBPower = 0;
			if (world.getBlockState(pos).getValue(DIRECTION)) {
				SideBPower = be.POWER;
			} else {
				SideAPower = be.POWER;
			}
			if(localDir == Sides[0]){return SideAPower;}
			return SideBPower;
		}
		return 0;
	}

	@Override
	public ConnectionFace getOutputConnectionFace(LevelAccessor world, BlockPos pos, ConnectionFace requesterFace) {
		BlockState thisState = world.getBlockState(pos);
		ConnectionFace thisFace = requesterFace.getConnectable();
		Direction localDir = BlockFrameTransformUtils.getLocalDirectionFromWorld(thisState, thisFace.FACE);
		Direction[] Sides = GetGateInputSidesProcedure.Get3ABCGateForth(thisState);
		if(localDir == Sides[0] || localDir == Sides[1]){
			return thisFace;
		}
		thisFace.CHANNEL = 5;
		return thisFace;
	}

	@Override
	public ConnectionFace getAnyConnectionFace(LevelAccessor world, BlockPos pos, ConnectionFace requesterFace) {
		BlockState blockState = world.getBlockState(pos);
		Direction localDirection = BlockFrameTransformUtils.getLocalDirectionFromWorld(blockState,requesterFace.FACE.getOpposite());
		return BlockFrameTransformUtils.getConnectionFace(blockState,localDirection);
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
