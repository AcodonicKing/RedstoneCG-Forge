
package net.acodonic_king.redstonecg.block.normal.wire;

import net.acodonic_king.redstonecg.block.defaults.*;
import net.acodonic_king.redstonecg.block.entity.RedCuWireBlockEntity;
import net.acodonic_king.redstonecg.procedures.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import java.util.List;

public class RedstoneToRedCuConverterBlock extends DefaultWire implements OldInterface, PinMarkConnectionInterface {
	public static final IntegerProperty CONNECTION = IntegerProperty.create("connection",0,12);
	public static final DirectionProperty ROTATION = DirectionProperty.create("rotation", Direction.Plane.HORIZONTAL);

	public RedstoneToRedCuConverterBlock() {
		super();
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new RedCuWireBlockEntity(pos, state);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(CONNECTION, ROTATION);
	}

	@Override
	public int floorIt(Level level, BlockPos pos) {
		BlockState blockState = level.getBlockState(pos);
		Direction facing = blockState.getValue(FACING);
		if(facing.getAxis() != Direction.Axis.Y){
			blockState = blockState.setValue(ROTATION, facing);
		}
		blockState = blockState.setValue(FACING, Direction.DOWN);
		level.setBlock(pos, blockState, 34);
		//level.sendBlockUpdated(pos, blockState, blockState, 2);
		return 1;
	}

	@Override
	public boolean canConnectRedstone(BlockState blockState, BlockGetter world, BlockPos pos, Direction side) {
		int filter = blockState.getValue(CONNECTION);
		filter = RedCuWireCanConnectRedstoneProcedure.redstoneToRedCu_AllFilter(filter);
		filter = ConnectionFacePrimaryRange.rotateFilter(filter,blockState.getValue(ROTATION));
		ConnectionFace connectionFaceB = BlockFrameTransformUtils.canConnectRedstoneTargetConnectionFace(world, pos, side);
		ConnectionFacePrimaryRange connectionFaceA = new ConnectionFacePrimaryRange(blockState.getValue(DefaultWire.FACING));
		return connectionFaceA.canConnectAvoid(connectionFaceB,filter);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState blockState = super.getStateForPlacement(context);
		if(blockState == null){return null;}
		Direction clickedFace = context.getClickedFace().getOpposite();
		Direction lookDirection = context.getHorizontalDirection();
		if(clickedFace == Direction.UP){
			blockState = switch (lookDirection){
				case NORTH, SOUTH -> blockState.setValue(ROTATION, lookDirection.getOpposite());
				default -> blockState.setValue(ROTATION, lookDirection);
			};
		} else if (clickedFace == Direction.DOWN){
			blockState = blockState.setValue(ROTATION, lookDirection);
		}
		return blockState;
	}

	@Override
	public void onTick(LevelAccessor world, BlockPos pos, int recursion){
		BlockState thisBlock = world.getBlockState(pos);
		ConnectionFacePrimaryRange connectionFaceRangeA = new ConnectionFacePrimaryRange(thisBlock.getValue(DefaultWire.FACING));
		List<ConnectionFace> connectionFaceList = connectionFaceRangeA.getList();
		int connection = thisBlock.getValue(RedstoneToRedCuConverterBlock.CONNECTION);
		Direction rotation = thisBlock.getValue(ROTATION);
		int filterRedCu = RedCuWireCanConnectRedstoneProcedure.redstoneToRedCu_RedCuFilter(connection);
		int filterRedstone = RedCuWireCanConnectRedstoneProcedure.redstoneToRedCu_RedstoneFilter(connection);
		filterRedCu = ConnectionFacePrimaryRange.rotateFilter(filterRedCu, rotation);
		filterRedstone = ConnectionFacePrimaryRange.rotateFilter(filterRedstone, rotation);
		int power = 0;
		for(int i = 0; i < 4; i++){
			boolean isRedCu = ((filterRedCu >> i) & 1) > 0;
			boolean isRedstone = ((filterRedstone >> i) & 1) > 0;
			//RedstonecgMod.LOGGER.debug("{} {} {}",i,isRedCu,isRedstone);
			if(!(isRedCu || isRedstone)){continue;}
			ConnectionFace connectionFaceA = connectionFaceList.get(i);
			int powerB = 0;
			if(isRedCu){
				powerB = GetRedstoneSignalProcedure.executeWire(world, pos, connectionFaceA);
			}
			if(isRedstone){
				powerB = GetRedstoneSignalProcedure.execute(world, pos, connectionFaceA);
				powerB = (powerB << 4) + 16;
			}

			power = Math.max(power, powerB);
		}
		power = Math.max(0, power - 1);
		power = Math.min(power, 255);
		if(world.getBlockEntity(pos) instanceof RedCuWireBlockEntity wireEntity){
			//RedstonecgMod.LOGGER.debug("{} updated to {} (was {})",pos,power,wireEntity.POWER);
			if(wireEntity.POWER == power){return;}
			wireEntity.POWER = power;
			wireEntity.setChanged();
			//((Level) world).sendBlockUpdated(pos, thisBlock, thisBlock, 2);
			recursion++;
			boolean exceedsRecursion = recursion > getWireChainLimit(world);
			for(int i = 0; i < 4; i++){
				if(((filterRedCu >> i) & 1) == 0){continue;}
				ConnectionFace connectionFaceA = connectionFaceList.get(i);
				BlockPos targetPos = pos.offset(connectionFaceA.FACE.getNormal());
				//RedstonecgMod.LOGGER.debug("Sending RedCu wire update to {}", targetPos);
				BlockState bs = world.getBlockState(targetPos);
				Block targetBlock = bs.getBlock();
				if (targetBlock instanceof DefaultRedstoneActionGate nb){
					if (exceedsRecursion) {
						world.scheduleTick(targetPos, targetBlock, 1);
						continue;
					}
					nb.onRedstoneUpdate(world, bs, targetPos, pos, recursion);
				} else if (targetBlock instanceof WireInterface nb){
					if (exceedsRecursion) {
						world.scheduleTick(targetPos, targetBlock, 1);
						continue;
					}
					nb.onTick(world, targetPos, recursion);
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
		BlockEntity blockEntity = world.getBlockEntity(pos);
		BlockState blockState = blockEntity.getBlockState();
		ConnectionFacePrimaryRange connectionFaceRange = new ConnectionFacePrimaryRange(blockState.getValue(DefaultWire.FACING));
		int filter = blockState.getValue(CONNECTION);
		filter = RedCuWireCanConnectRedstoneProcedure.redstoneToRedCu_RedCuFilter(filter);
		filter = ConnectionFacePrimaryRange.rotateFilter(filter,blockState.getValue(ROTATION));
		//RedstonecgMod.LOGGER.debug("{} {} {}", filter, requesterFace, pos);
		if(!connectionFaceRange.canConnectAvoid(requesterFace,filter))
			return 0;
		if (blockEntity instanceof RedCuWireBlockEntity wireEntity) {
			return wireEntity.POWER;
		}
		return 0;
	}

	@Override
	public void rotationBracket(LevelAccessor world, BlockPos pos, boolean clockwise){
		BlockState blockState = world.getBlockState(pos);
		if(blockState.getBlock() instanceof RedstoneToRedCuConverterBlock){
			Direction rotation = blockState.getValue(RedstoneToRedCuConverterBlock.ROTATION);
			if(clockwise){rotation = rotation.getClockWise(Direction.Axis.Y);}
			else{rotation = rotation.getCounterClockWise(Direction.Axis.Y);}
			blockState = blockState.setValue(RedstoneToRedCuConverterBlock.ROTATION, rotation);
			world.setBlock(pos, blockState, 3);
		}
	}

	@Override
	public ConnectionFace getOutputConnectionFace(LevelAccessor world, BlockPos pos, ConnectionFace requesterFace) {
		BlockState blockState = world.getBlockState(pos);
		ConnectionFace connectionFace = requesterFace.getConnectable();
		ConnectionFacePrimaryRange connectionFaceRange = new ConnectionFacePrimaryRange(blockState.getValue(DefaultWire.FACING));
		int filter = blockState.getValue(CONNECTION);
		filter = RedCuWireCanConnectRedstoneProcedure.redstoneToRedCu_RedCuFilter(filter);
		filter = ConnectionFacePrimaryRange.rotateFilter(filter,blockState.getValue(ROTATION));
		if(!connectionFaceRange.inRangeAvoid(requesterFace,filter)){
			connectionFace.CHANNEL = 5;
		}
		return connectionFace;
	}

	@Override
	public int oldVersion(Level level, BlockPos pos) {
		BlockState blockState = level.getBlockState(pos);
		if(blockState.getBlock() instanceof RedstoneToRedCuConverterBlock){
			int connection = blockState.getValue(CONNECTION);
			if(connection > 3 && connection < 6){
				connection += 2;
			} else if (connection == 6){
				connection = 9;
			} else if (connection == 7){
				connection = 8;
			} else if (connection == 8){
				connection = 9;
				blockState = blockState.setValue(
						ROTATION,
						blockState
								.getValue(ROTATION)
								.getClockWise(Direction.Axis.Y)
				);
			} else if (connection > 8){
				connection++;
			}
			blockState = blockState.setValue(CONNECTION, connection);
			level.setBlock(pos, blockState, 2);
			return 1;
		}
		return 0;
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		Direction facing = state.getValue(FACING);
		Direction rotation = state.getValue(ROTATION);
		if (facing.getAxis() == Direction.Axis.Y){
			rotation = rot.rotate(rotation);
		} else {
			facing = rot.rotate(facing);
		}
		return state.setValue(FACING, facing).setValue(ROTATION, rotation);
	}

	@Override
	public int getConnection(BlockState bs) {
		return bs.getValue(CONNECTION);
	}

	@Override
	public int connectionFilter(int connection) {
		return RedCuWireCanConnectRedstoneProcedure.redstoneToRedCu_AllFilter(connection);
	}
}
