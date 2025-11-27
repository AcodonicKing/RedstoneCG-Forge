
package net.acodonic_king.redstonecg.block.normal.analog;

import net.acodonic_king.redstonecg.ModLoaderRider;
import net.acodonic_king.redstonecg.block.defaults.DefaultEmitting1_4Gate;
import net.acodonic_king.redstonecg.init.RedstonecgModItems;
import net.acodonic_king.redstonecg.procedures.AdventureProcedure;
import net.acodonic_king.redstonecg.procedures.CanConnectWallGateProcedure;
import net.acodonic_king.redstonecg.procedures.ConnectionFace;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;

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

public class AnalogSourceBlock extends DefaultEmitting1_4Gate implements EntityBlock {
	public AnalogSourceBlock() {
		super();
	}

	@Override
	public InteractionResult use(BlockState blockstate, Level world, BlockPos pos, Player entity, InteractionHand hand, BlockHitResult hit) {
		InteractionResult interactionResult = super.use(blockstate, world, pos, entity, hand, hit);
		if(interactionResult == InteractionResult.FAIL){return interactionResult;}
		ItemStack itemStack = entity.getItemInHand(hand);
		if(!itemStack.isEmpty()){
			if(itemStack.is(RedstonecgModItems.ROTATION_BRACKET.get())){return InteractionResult.FAIL;}
			if(itemStack.is(RedstonecgModItems.NORMAL_ANALOG_SOURCE.get())){
				if(AdventureProcedure.valueConfig(world, entity)){
					int power = getPower(world, pos) + 1;
					if(power > 15)
						power = 0;
					setPower(world, blockstate, pos, power);
					return InteractionResult.SUCCESS;
				}
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
				Containers.dropContents(world, pos, be);
				world.updateNeighbourForOutputSignal(pos, this);
			}
			super.onRemove(state, world, pos, newState, isMoving);
		}
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return false;
	}

	/*@Override
	public int getAnalogOutputSignal(BlockState blockState, Level world, BlockPos pos) {
		BlockEntity tileentity = world.getBlockEntity(pos);
		if (tileentity instanceof AnalogSourceBlockEntity be)
			return AbstractContainerMenu.getRedstoneSignalFromContainer(be);
		else
			return 0;
	}*/

	public void setPower(LevelAccessor level, BlockState state, BlockPos pos, int power){
		Level world = (Level) level;
		power = Math.max(0, Math.min(power, 15));
		if (world.getBlockEntity(pos) instanceof AnalogSourceBlockEntity be){
			if(be.POWER != power){
				be.POWER = power;
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
