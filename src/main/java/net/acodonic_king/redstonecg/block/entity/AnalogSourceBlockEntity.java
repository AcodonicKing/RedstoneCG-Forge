package net.acodonic_king.redstonecg.block.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;

import net.acodonic_king.redstonecg.init.RedstonecgModBlockEntities;

public class AnalogSourceBlockEntity extends SuperBlockEntity {
	public int POWER = 0;
	public float ANGLE = 0;
	//public boolean BASE_OUT = true;
	public int[] POWER_RANGE = new int[]{0, 15};

	public AnalogSourceBlockEntity(BlockPos position, BlockState state) {
		super(RedstonecgModBlockEntities.ANALOG_SOURCE.get(), position, state);
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putByte("power", (byte)POWER);
		tag.putInt("power_min", POWER_RANGE[0]);
		tag.putInt("power_max", POWER_RANGE[1]);
		//tag.putBoolean("base_read", BASE_OUT);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		if(tag.contains("power")) POWER = tag.getByte("power");
		if(tag.contains("power_min")) POWER_RANGE[0] = tag.getInt("power_min");
		if(tag.contains("power_max")) POWER_RANGE[1] = tag.getInt("power_max");
		//if (tag.contains("base_read")) BASE_OUT = tag.getBoolean("base_read");
		setAngle();
    }

	public void setAngle(){
		ANGLE = (float) (POWER - POWER_RANGE[0]) / (POWER_RANGE[1] - POWER_RANGE[0] + 1);
		ANGLE = 0.5f - ANGLE;
		ANGLE *= (float) (2 * Math.PI);
	}

	public void setPower(int v){
		POWER = v;
		setAngle();
	}
}
