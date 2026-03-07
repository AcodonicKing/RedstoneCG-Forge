package net.acodonic_king.redstonecg.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.acodonic_king.redstonecg.init.RedstonecgModBlockEntities;

public class RedCuWireBlockEntity extends SuperBlockEntity {
	public int POWER = 0;
	public RedCuWireBlockEntity(BlockEntityType blockEntityType, BlockPos position, BlockState state){
		super(blockEntityType, position, state);
	}
	public RedCuWireBlockEntity(BlockPos position, BlockState state) {
		super(RedstonecgModBlockEntities.RED_CU_WIRE.get(), position, state);
	}
	@Override
	public void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putByte("power", (byte) (POWER & 0xFF));
	}
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		POWER = tag.getByte("power") & 0xFF;
	}
	/*@Override
	public CompoundTag getUpdateTag() {
		CompoundTag tag = super.getUpdateTag();
		saveAdditional(tag);
		return tag;
	}
	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}
	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		CompoundTag tag = pkt.getTag();
		if (tag != null) load(tag); // or manually read fields
	}*/
}
