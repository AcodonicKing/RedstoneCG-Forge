
package net.acodonic_king.redstonecg.block.normal.wire;

import net.acodonic_king.redstonecg.block.defaults.DefaultRedstoneActionGate;
import net.acodonic_king.redstonecg.block.defaults.DefaultWire;
import net.acodonic_king.redstonecg.block.defaults.PinMarkConnectionInterface;
import net.acodonic_king.redstonecg.block.defaults.WireInterface;
import net.acodonic_king.redstonecg.block.entity.RedCuWireIntersectionBlockEntity;
import net.acodonic_king.redstonecg.procedures.*;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.Level;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import java.util.List;

public class RedCuWireIntersectionBlock extends DefaultWire implements PinMarkConnectionInterface {
	public static final IntegerProperty CONNECTION = IntegerProperty.create("connection",0,3);

	public RedCuWireIntersectionBlock() {
		super();
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new RedCuWireIntersectionBlockEntity(pos, state);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(CONNECTION);
	}

	@Override
	public void onTick(LevelAccessor world, BlockPos pos){
		BlockState thisBlock = world.getBlockState(pos);
		ConnectionFacePrimaryRange connectionFaceRangeA = new ConnectionFacePrimaryRange(thisBlock.getValue(DefaultWire.FACING));
		List<ConnectionFace> connectionFaceList = connectionFaceRangeA.getList();
		int mask = RedCuWireCanConnectRedstoneProcedure.wireIntersectionConnectionMask(thisBlock.getValue(CONNECTION));
		int[] power = {0,0};
		for(int i = 0; i < 4; i++){
			//Direction direction = BlockFrameTransformUtils.decodeIntToDirection(i+1);
			ConnectionFace connectionFaceA = connectionFaceList.get(i);
			int j = (mask >> i) & 1;
			int powerB = GetRedstoneSignalProcedure.executeWire(world, pos, connectionFaceA);
			power[j] = Math.max(power[j], powerB);
		}
		if(world.getBlockEntity(pos) instanceof RedCuWireIntersectionBlockEntity wireEntity){
			//RedstonecgMod.LOGGER.debug("{} {}", power[0], power[1]);
			power[0] = Math.max(0, power[0] - 1);
			power[1] = Math.max(0, power[1] - 1);
			if(wireEntity.POWER_A == power[0]){power[0] = 0;}
			else{
				wireEntity.POWER_A = power[0];
				power[0] = 1;
			}
			if(wireEntity.POWER_B == power[1]){power[1] = 0;}
			else{
				wireEntity.POWER_B = power[1];
				power[1] = 1;
			}
			wireEntity.setChanged();
		} else {
			return;
		}
		for(int i = 0; i < 4; i++){
			int j = (mask >> i) & 1;
			if(power[j] == 0){continue;}
			ConnectionFace connectionFaceA = connectionFaceList.get(i);
			BlockPos targetPos = pos.offset(connectionFaceA.FACE.getNormal());
			BlockState bs = world.getBlockState(targetPos);
			Block targetBlock = bs.getBlock();
			if (targetBlock instanceof DefaultRedstoneActionGate nb){
				nb.onRedstoneUpdate(world, bs, targetPos, pos);
			} else if (targetBlock instanceof WireInterface nb){
				nb.onTick(world, targetPos);
			} else {
				Block nb = bs.getBlock();
				//nb.neighborChanged(bs,(Level) world,targetPos,thisBlock.getBlock(),pos,false);
				((Level) world).neighborChanged(targetPos,thisBlock.getBlock(),pos);
			}
		}
	}

	@Override
	public int getWirePower(LevelAccessor world, BlockPos pos, ConnectionFace requesterFace){
		BlockEntity blockEntity = world.getBlockEntity(pos);
		BlockState blockState = blockEntity.getBlockState();
		Direction facing = blockState.getValue(DefaultWire.FACING);
		ConnectionFacePrimaryRange connectionFaceRange = new ConnectionFacePrimaryRange(facing);
		if(!connectionFaceRange.canConnect(requesterFace))
			return 0;
		int connection = blockState.getValue(CONNECTION);
		Direction direction = BlockFrameTransformUtils.getLocalDirectionFromWorld(Direction.NORTH, facing, requesterFace.FACE.getOpposite());
		if(blockEntity instanceof RedCuWireIntersectionBlockEntity wireEntity){
			boolean isB = RedCuWireCanConnectRedstoneProcedure.wireIntersectionDirectionIsB(connection, direction);
			//RedstonecgMod.LOGGER.debug("is B {} on {}",isB,direction);
			if(isB){
				return wireEntity.POWER_B;
			}
			return wireEntity.POWER_A;
		}
		return 0;
	}

	@Override
	public String getMeasurement(LevelAccessor world, BlockState blockState, BlockPos pos){
		if(world.getBlockEntity(pos) instanceof RedCuWireIntersectionBlockEntity wireEntity){
			return String.format("\nA= %.4f B= %.4f",wireEntity.POWER_A/16.0,wireEntity.POWER_B/16.0);
		}
		return "";
	}

	@Override
	public void onPlace(BlockState blockstate, Level world, BlockPos pos, BlockState oldState, boolean moving) {
		super.onPlace(blockstate, world, pos, oldState, moving);
	}

	@Override
	public void rotationBracket(LevelAccessor world, BlockPos pos, boolean clockwise) {
		BlockState blockState = world.getBlockState(pos);
		if (blockState.getBlock() instanceof RedCuWireBlock) {
			int connection = blockState.getValue(CONNECTION);
			if(connection < 2){
				connection = (connection == 0) ? 1: 0;
			} else {
				connection = (connection == 2) ? 3: 2;
			}
			blockState = blockState.setValue(CONNECTION, connection);
			world.setBlock(pos, blockState, 3);
		}
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		if (rot == Rotation.NONE)
			return state;
		Direction facing = state.getValue(FACING);
		if (facing.getAxis() == Direction.Axis.Y){
			if (rot == Rotation.CLOCKWISE_180)
				return state;
			int connection = state.getValue(CONNECTION);
			if(connection < 2){
				connection = (connection == 0) ? 1: 0;
			} else {
				connection = (connection == 2) ? 3: 2;
			}
			state = state.setValue(CONNECTION, connection);
		} else {
			state = state.setValue(FACING, rot.rotate(facing));
		}
		return state;
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		if(mirrorIn == Mirror.NONE)
			return state;
		Direction facing = state.getValue(FACING);
		int connection = state.getValue(RedCuWireBlock.CONNECTION);
		if(connection >= 2)
			connection = (connection == 2) ? 3: 2;
		if(facing.getAxis() != Direction.Axis.Y)
			facing = mirrorIn.mirror(facing);
		return state.setValue(FACING, facing).setValue(CONNECTION, connection);
	}

	@Override
	public ConnectionFace getOutputConnectionFace(LevelAccessor world, BlockPos pos, ConnectionFace requesterFace) {
		BlockState thisBlock = world.getBlockState(pos);
		ConnectionFacePrimaryRange connectionFaceRange = new ConnectionFacePrimaryRange(thisBlock.getValue(DefaultWire.FACING));
		ConnectionFace out = requesterFace.getConnectable();
		if(!connectionFaceRange.canConnect(requesterFace))
			out.CHANNEL = 5;
		return out;
		//int mask = RedCuWireCanConnectRedstoneProcedure.wireIntersectionConnectionMask(thisBlock.getValue(CONNECTION));
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
