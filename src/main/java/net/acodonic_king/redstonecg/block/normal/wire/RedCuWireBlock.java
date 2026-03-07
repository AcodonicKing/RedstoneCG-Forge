
package net.acodonic_king.redstonecg.block.normal.wire;

import net.acodonic_king.redstonecg.block.defaults.*;
import net.acodonic_king.redstonecg.block.entity.RedCuWireBlockEntity;
import net.acodonic_king.redstonecg.procedures.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import java.util.List;

public class RedCuWireBlock extends DefaultWire implements OldInterface, PinMarkConnectionInterface {
	public static final IntegerProperty CONNECTION = IntegerProperty.create("connection",0,10);

	public RedCuWireBlock() {
		super();
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(CONNECTION);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState blockState = super.getStateForPlacement(context);
		if(blockState == null){return null;}
		Direction lookDirection = context.getHorizontalDirection();
		int connection = switch (lookDirection){
			case NORTH, SOUTH -> 1;
			default -> 0;
		};
		blockState = blockState.setValue(RedCuWireBlock.CONNECTION, connection);
		return blockState;
	}

	@Override
	public void rotationBracket(LevelAccessor world, BlockPos pos, boolean clockwise){
		BlockState blockState = world.getBlockState(pos);
		if(blockState.getBlock() instanceof RedCuWireBlock){
			int connection = blockState.getValue(RedCuWireBlock.CONNECTION);
			if(connection == 10) {
				return;
			} else if(connection >= 6){
				if(clockwise){
					connection--;
					if(connection < 6){connection = 9;}
				} else {
					connection++;
					if(connection > 9){connection = 6;}
				}
			} else if(connection >= 2){
				if(clockwise){
					connection--;
					if(connection < 2){connection = 5;}
				} else {
					connection++;
					if(connection > 5){connection = 2;}
				}
			} else {
				connection = (connection == 0) ? 1 : 0;
			}
			blockState = blockState.setValue(RedCuWireBlock.CONNECTION, connection);
			world.setBlock(pos, blockState, 3);
		}

	}

	@Override
	public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
		ConnectionFace connectionFaceB = BlockFrameTransformUtils.canConnectRedstoneTargetConnectionFace(world, pos, side);
		ConnectionFacePrimaryRange connectionFaceA = new ConnectionFacePrimaryRange(state.getValue(DefaultWire.FACING));
		int filter = RedCuWireCanConnectRedstoneProcedure.wireConnectionFilter(state.getValue(RedCuWireBlock.CONNECTION));
		return connectionFaceA.canConnectAvoid(connectionFaceB,filter);
	}

	@Override
	public void onPlace(BlockState blockstate, Level world, BlockPos pos, BlockState oldState, boolean moving) {
		super.onPlace(blockstate, world, pos, oldState, moving);
	}

	@Override
	public void onTick(LevelAccessor world, BlockPos pos, int power){
		BlockState thisBlock = world.getBlockState(pos);
		ConnectionFacePrimaryRange connectionFaceRangeA = new ConnectionFacePrimaryRange(thisBlock.getValue(DefaultWire.FACING));
		int filter = RedCuWireCanConnectRedstoneProcedure.wireConnectionFilter(thisBlock.getValue(RedCuWireBlock.CONNECTION));
		List<ConnectionFace> connectionFaceList = connectionFaceRangeA.getList(filter);
		for(ConnectionFace connectionFaceA: connectionFaceList){
			int powerB = GetRedstoneSignalProcedure.executeWire(world, pos, connectionFaceA);
			//RedstonecgMod.LOGGER.debug("power {} at {}", powerB, targetPos);
			power = Math.max(power, powerB);
		}
		power = Math.max(0, power - 1);
		if(world.getBlockEntity(pos) instanceof RedCuWireBlockEntity wireEntity){
			//RedstonecgMod.LOGGER.debug("{} updated to {} (was {})",pos,power,wireEntity.POWER);
			if(wireEntity.POWER == power){return;}
			wireEntity.POWER = power;
			wireEntity.setChanged();
			//world.setBlock(pos, thisBlock, 2);
			//((Level) world).sendBlockUpdated(pos, thisBlock, thisBlock, 2);
			for(ConnectionFace connectionFaceA: connectionFaceList){
				BlockPos targetPos = pos.relative(connectionFaceA.FACE);
				//RedstonecgMod.LOGGER.debug("Sending RedCu wire update to {}", targetPos);
				BlockState bs = world.getBlockState(targetPos);
				Block targetBlock = bs.getBlock();
				if (targetBlock instanceof DefaultRedstoneActionGate nb){
					nb.onRedstoneUpdate(world, bs, targetPos, pos);
				} else if (targetBlock instanceof WireInterface nb) {
					nb.onTick(world, targetPos);
				} else {
					Block nb = bs.getBlock();
					//nb.neighborChanged(bs,(Level) world,targetPos,thisBlock.getBlock(),pos,false);
					((Level) world).neighborChanged(targetPos,thisBlock.getBlock(),pos);
				}
			}
		}
	}

	@Override
	public int getWirePower(LevelAccessor world, BlockPos pos, ConnectionFace requesterFace){
		BlockState blockState = world.getBlockState(pos);
		Direction secondary = blockState.getValue(DefaultWire.FACING);
		Direction localDir = BlockFrameTransformUtils.getLocalDirectionFromWorld(Direction.NORTH, secondary, requesterFace.FACE.getOpposite());
		ConnectionFace connectionFace = new ConnectionFace(localDir, secondary);
		ConnectionFacePrimaryRange connectionFaceRange = new ConnectionFacePrimaryRange(secondary);
		int filter = RedCuWireCanConnectRedstoneProcedure.wireConnectionFilter(blockState.getValue(RedCuWireBlock.CONNECTION));
		if(!connectionFaceRange.inRangeAvoid(connectionFace,filter)){return 0;}
		if(world.getBlockEntity(pos) instanceof RedCuWireBlockEntity be){
			return be.POWER;
		}
		return 0;
	}

	@Override
	public void neighborChanged(BlockState blockstate, Level world, BlockPos pos, Block neighborBlock, BlockPos fromPos, boolean moving) {
		super.neighborChanged(blockstate, world, pos, neighborBlock, fromPos, moving);
		this.onTick(world, pos);
	}

	@Override
	public int oldVersion(Level level, BlockPos pos) {
		BlockState blockState = level.getBlockState(pos);
		if(blockState.getBlock() instanceof RedCuWireBlock wb){
			int connection = blockState.getValue(CONNECTION);
			if(0 <= connection && connection <= 3){
				connection = switch (connection){
					case 1 -> 2;
					case 2 -> 6;
					case 3 -> 10;
					default -> 0;
				};
				blockState = blockState.setValue(CONNECTION, connection);
				level.setBlock(pos, blockState, 2);
				return 1;
			}
		}
		return 0;
	}

	@Override
	public ConnectionFace getOutputConnectionFace(LevelAccessor world, BlockPos pos, ConnectionFace requesterFace) {
		BlockState blockState = world.getBlockState(pos);
		Direction secondary = blockState.getValue(DefaultWire.FACING);
		ConnectionFace connectionFace = requesterFace.getConnectable();
		ConnectionFacePrimaryRange connectionFaceRange = new ConnectionFacePrimaryRange(secondary);
		int filter = RedCuWireCanConnectRedstoneProcedure.wireConnectionFilter(blockState.getValue(RedCuWireBlock.CONNECTION));
		if(!connectionFaceRange.canConnectAvoid(requesterFace,filter)){
			connectionFace.CHANNEL = 5;
		}
		return connectionFace;
	}

	@Override
	public ConnectionFace getAnyConnectionFace(LevelAccessor world, BlockPos pos, ConnectionFace requesterFace) {
		BlockState blockState = world.getBlockState(pos);
		Direction secondary = blockState.getValue(DefaultWire.FACING);
		ConnectionFace connectionFace = requesterFace.getConnectable();
		ConnectionFacePrimaryRange connectionFaceRange = new ConnectionFacePrimaryRange(secondary);
		int filter = RedCuWireCanConnectRedstoneProcedure.wireConnectionFilter(blockState.getValue(RedCuWireBlock.CONNECTION));
		if(!connectionFaceRange.canConnectAvoid(requesterFace,filter)){
			connectionFace.CHANNEL = 5;
			return connectionFace;
		}
		Direction localDirection = BlockFrameTransformUtils.getLocalDirectionFromWorld(Direction.NORTH, secondary, requesterFace.FACE.getOpposite());
		return new ConnectionFace(localDirection, secondary);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		if(rot == Rotation.NONE)
			return state;
		Direction facing = state.getValue(FACING);
		if (facing.getAxis() == Direction.Axis.Y){
			int connection = state.getValue(RedCuWireBlock.CONNECTION);
			if(connection == 10) {
				return state;
			} else if(connection >= 6){
				if(rot == Rotation.CLOCKWISE_90 || rot == Rotation.CLOCKWISE_180){
					connection--;
					if(connection < 6){connection = 9;}
				} else if (rot == Rotation.COUNTERCLOCKWISE_90){
					connection++;
					if(connection > 9){connection = 6;}
				}
				if(rot == Rotation.CLOCKWISE_180){
					connection--;
					if(connection < 6){connection = 9;}
				}
			} else if(connection >= 2){
				if(rot == Rotation.CLOCKWISE_90 || rot == Rotation.CLOCKWISE_180){
					connection--;
					if(connection < 2){connection = 5;}
				} else if (rot == Rotation.COUNTERCLOCKWISE_90) {
					connection++;
					if(connection > 5){connection = 2;}
				}
				if(rot == Rotation.CLOCKWISE_180){
					connection--;
					if(connection < 2){connection = 5;}
				}
			} else if(rot != Rotation.CLOCKWISE_180) {
				connection = (connection == 0) ? 1 : 0;
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
		if (facing.getAxis() == Direction.Axis.Y && mirrorIn == Mirror.LEFT_RIGHT){
			connection = switch (connection){
				case 2 -> 3;
				case 3 -> 2;
				case 4 -> 5;
				case 5 -> 4;
				case 6 -> 8;
				case 8 -> 6;
				default -> connection;
			};
		} else {
			facing = mirrorIn.mirror(facing);
			connection = switch (connection){
				case 2 -> 5;
				case 3 -> 4;
				case 4 -> 3;
				case 5 -> 2;
				case 7 -> 9;
				case 9 -> 7;
				default -> connection;
			};
		}
		return state.setValue(FACING, facing).setValue(CONNECTION, connection);
	}

	@Override
	public int getConnection(BlockState bs) {
		return bs.getValue(CONNECTION);
	}

	@Override
	public int connectionFilter(int connection) {
		return RedCuWireCanConnectRedstoneProcedure.wireConnectionFilter(connection);
	}
}
