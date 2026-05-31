
package net.acodonic_king.redstonecg.block.normal.analog;

import net.acodonic_king.redstonecg.ModLoaderRider;
import net.acodonic_king.redstonecg.RedstonecgMod;
import net.acodonic_king.redstonecg.block.defaults.DefaultEmitting1_4Gate;
import net.acodonic_king.redstonecg.block.defaults.DefaultRedstoneActionGate;
import net.acodonic_king.redstonecg.block.defaults.PinMarkConnectionInterface;
import net.acodonic_king.redstonecg.block.entity.ArrowIndicatorBlockEntity;
import net.acodonic_king.redstonecg.init.RedstonecgModItems;
import net.acodonic_king.redstonecg.init.RedstonecgModVersionRides;
import net.acodonic_king.redstonecg.procedures.AdventureProcedure;
import net.acodonic_king.redstonecg.procedures.BlockFrameTransformUtils;
import net.acodonic_king.redstonecg.procedures.CanConnectWallGateProcedure;
import net.acodonic_king.redstonecg.procedures.ConnectionFace;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.Level;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.Containers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;

import net.acodonic_king.redstonecg.block.gui.analog_source.AnalogSourceGUIMenu;
import net.acodonic_king.redstonecg.block.entity.AnalogSourceBlockEntity;

import io.netty.buffer.Unpooled;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AnalogSourceBlock extends DefaultRedstoneActionGate implements EntityBlock, PinMarkConnectionInterface {
	public static final IntegerProperty CONNECTION = IntegerProperty.create("connection",0,14);

	public AnalogSourceBlock() {
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
		return CanConnectWallGateProcedure.To1_4Gate(state, connectionFaceB);
	}

	@Override
	public ConnectionFace getOutputConnectionFace(LevelAccessor world, BlockPos pos, ConnectionFace requesterFace) {
		BlockState blockState = world.getBlockState(pos);
		ConnectionFace connectionFaceA = requesterFace.getConnectable();
		if(!CanConnectWallGateProcedure.To1_4Gate(blockState, requesterFace))
			connectionFaceA.CHANNEL = 5;
		return connectionFaceA;
	}

	@Override
	public int getConnection(BlockState bs) {
		return bs.getValue(CONNECTION);
	}

	@Override
	public int connectionFilter(int connection) {
		return CanConnectWallGateProcedure.To1_4GateConnectionFilter(connection);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return switch (state.getValue(FACING)) {
			case DOWN -> Shapes.join(box(0, 0, 0, 16, 2, 16),box(4, 2, 4, 12, 4, 12),BooleanOp.OR);
			case NORTH -> Shapes.join(box(0, 0, 0, 16, 16, 2),box(4, 4, 2, 12, 12, 4),BooleanOp.OR);
			case EAST -> Shapes.join(box(14, 0, 0, 16, 16, 16), box(12, 4, 4, 14, 12, 12),BooleanOp.OR);
			case SOUTH -> Shapes.join(box(0, 0, 14, 16, 16, 16),box(4, 4, 12, 12, 12, 14),BooleanOp.OR);
			case WEST -> Shapes.join(box(0, 0, 0, 2, 16, 16),box(2, 4, 4, 4, 12, 12),BooleanOp.OR);
			case UP -> Shapes.join(box(0, 14, 0, 16, 16, 16),box(4, 12, 4, 12, 14, 12),BooleanOp.OR);
		};
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (level.isClientSide) return;
		if (!stack.hasTag()) return;
		CompoundTag tag = stack.getTag();
		if (!tag.contains("BlockParameterSet")) return;
		tag = tag.getCompound("BlockParameterSet");
		if (level.getBlockEntity(pos) instanceof AnalogSourceBlockEntity be){
			if (tag.contains("connection"))
				state = state.setValue(CONNECTION, tag.getInt("connection"));
			be.setParameterSet(tag);
			be.setChanged();
			level.sendBlockUpdated(pos, state, state, 3);
			level.scheduleTick(pos, state.getBlock(), 1);
		}
	}

	@Override
	public InteractionResult use(BlockState blockstate, Level world, BlockPos pos, Player entity, InteractionHand hand, BlockHitResult hit) {
		InteractionResult interactionResult = super.use(blockstate, world, pos, entity, hand, hit);
		if(interactionResult == InteractionResult.FAIL){return interactionResult;}
		ItemStack itemStack = entity.getItemInHand(hand);
		if(!itemStack.isEmpty()){
			if(itemStack.is(RedstonecgModItems.ROTATION_BRACKET.get())){return InteractionResult.FAIL;}
			if(itemStack.is(RedstonecgModItems.NORMAL_ANALOG_SOURCE.get())){
				if(world.getBlockEntity(pos) instanceof AnalogSourceBlockEntity be){
					CompoundTag tag = be.getParameterSet();
					tag.putInt("connection", blockstate.getValue(CONNECTION));
					itemStack.getOrCreateTag().put("BlockParameterSet", tag);
					return InteractionResult.SUCCESS;
				}
			}
			/*if(itemStack.is(RedstonecgModItems.NORMAL_ANALOG_SOURCE.get())){
				if(AdventureProcedure.valueConfig(world, entity)){
					int power = getPower(world, pos);
					power += entity.isCrouching() ? -1 : 1;
					if(power > 15)
						power = 0;
					if(power < 0)
						power = 15;
					setPower(world, blockstate, pos, power);
					return InteractionResult.SUCCESS;
				}
			}*/
		}
		if(AdventureProcedure.valueConfig(world, entity)) {
			Vec3 hitPos = hit.getLocation().subtract(RedstonecgModVersionRides.getBlockPosCenter(pos));
			//RedstonecgMod.LOGGER.debug(hitPos);
			if (switch (blockstate.getValue(FACING)) {
				case DOWN, UP -> (-0.25 <= hitPos.x && hitPos.x <= 0.25) && (-0.25 <= hitPos.z && hitPos.z <= 0.25);
				case EAST, WEST -> (-0.25 <= hitPos.y && hitPos.y <= 0.25) && (-0.25 <= hitPos.z && hitPos.z <= 0.25);
				case NORTH, SOUTH -> (-0.25 <= hitPos.x && hitPos.x <= 0.25) && (-0.25 <= hitPos.y && hitPos.y <= 0.25);
			}) {
				if (world.getBlockEntity(pos) instanceof AnalogSourceBlockEntity be){
					if(be.adjustPower(entity.isCrouching() ? -1 : 1)){
						world.playSound(entity, pos, SoundEvents.STONE_BUTTON_CLICK_ON, SoundSource.BLOCKS, 1.0f, 1.0f);
						be.setChanged();
						world.updateNeighborsAt(pos, blockstate.getBlock());
					}
				}
				return InteractionResult.SUCCESS;
			}
		}
		if(entity.isCrouching()){
			if(AdventureProcedure.pinConfig(world, entity)){
				int connection = blockstate.getValue(CONNECTION) + 1;
				if(connection > 14)
					connection = 0;
				world.setBlock(pos, blockstate.setValue(CONNECTION, connection), 2);
				return InteractionResult.SUCCESS;
			}
		}
		if(AdventureProcedure.gateGUI(world, entity))
			if (entity instanceof ServerPlayer player) {
				ModLoaderRider.openMenu(player, new MenuProvider() {
					@Override
					public Component getDisplayName() {
						return Component.literal("Analog Source");
					}

					@Override
					public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
						return new AnalogSourceGUIMenu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(pos));
					}
				}, pos);
			}
		return InteractionResult.SUCCESS;
	}

	@Override
	public MenuProvider getMenuProvider(BlockState state, Level worldIn, BlockPos pos) {
		BlockEntity tileEntity = worldIn.getBlockEntity(pos);
		return tileEntity instanceof MenuProvider menuProvider ? menuProvider : null;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new AnalogSourceBlockEntity(pos, state);
	}

	@Override
	public boolean triggerEvent(BlockState state, Level world, BlockPos pos, int eventID, int eventParam) {
		super.triggerEvent(state, world, pos, eventID, eventParam);
		BlockEntity blockEntity = world.getBlockEntity(pos);
		return blockEntity == null ? false : blockEntity.triggerEvent(eventID, eventParam);
	}

	@Override
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof AnalogSourceBlockEntity be) {
				//Containers.dropContents(world, pos, be);
				world.updateNeighbourForOutputSignal(pos, this);
			}
			super.onRemove(state, world, pos, newState, isMoving);
		}
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState blockState, Level world, BlockPos pos) {
		if(world.getBlockEntity(pos) instanceof AnalogSourceBlockEntity be)
			return be.POWER;
		return 0;
	}

	public void setPower(LevelAccessor level, BlockState state, BlockPos pos, Player player, int power){
		Level world = (Level) level;
		power = Math.max(0, Math.min(power, 15));
		if (world.getBlockEntity(pos) instanceof AnalogSourceBlockEntity be){
			if(be.POWER != power){
				level.playSound(player, pos, SoundEvents.STONE_BUTTON_CLICK_ON, SoundSource.BLOCKS, 1.0f, 1.0f);
				be.setPower(power);
				be.setChanged();
				world.updateNeighborsAt(pos, state.getBlock());
			}
		}
	}
	public int getPower(LevelAccessor level, BlockPos pos){
		Level world = (Level) level;
		if (world.getBlockEntity(pos) instanceof AnalogSourceBlockEntity be){
			return be.POWER;
		}
		return 0;
	}
	@Override
	public int getRedstonePower(LevelAccessor world, BlockPos pos, ConnectionFace requesterFace) {
		BlockState blockState = world.getBlockState(pos);
		if(!CanConnectWallGateProcedure.To1_4Gate(blockState, requesterFace))
			return 0;
		if(world.getBlockEntity(pos) instanceof AnalogSourceBlockEntity be){
			return be.POWER;
		}
		return 0;
	}
	@Override
	public String getMeasurement(LevelAccessor world, BlockState blockState, BlockPos pos){
		if(world.getBlockEntity(pos) instanceof AnalogSourceBlockEntity blockEntity){
			return String.format("%1d",blockEntity.POWER);
		}
		return "";
	}
}
