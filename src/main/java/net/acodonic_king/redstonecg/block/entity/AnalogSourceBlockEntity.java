package net.acodonic_king.redstonecg.block.entity;

import net.acodonic_king.redstonecg.procedures.TextFormatProcedure;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;

import net.acodonic_king.redstonecg.init.RedstonecgModBlockEntities;

public class AnalogSourceBlockEntity extends SuperBlockEntity {
	public int POWER = 0;
	public float ANGLE = 0;
	public String CUSTOM_NAME = "";
	public TextFormatProcedure.ComposedText RENDER_TEXT = new TextFormatProcedure.ComposedText();
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
		tag.putByte("power_min", (byte)POWER_RANGE[0]);
		tag.putByte("power_max", (byte)POWER_RANGE[1]);
		if(!CUSTOM_NAME.isEmpty())
			tag.putString("CustomName", CUSTOM_NAME);
		//tag.putBoolean("base_read", BASE_OUT);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		if(tag.contains("power")) POWER = tag.getByte("power");
		if(tag.contains("power_min")) POWER_RANGE[0] = tag.getByte("power_min");
		if(tag.contains("power_max")) POWER_RANGE[1] = tag.getByte("power_max");
		if(tag.contains("CustomName")) setName(tag.getString("CustomName"));
		//if (tag.contains("base_read")) BASE_OUT = tag.getBoolean("base_read");
		setAngle();
    }

	public CompoundTag getParameterSet(){
		CompoundTag tag = new CompoundTag();
		tag.putInt("power", POWER);
		tag.putIntArray("range", POWER_RANGE);
		return tag;
	}

	public void setParameterSet(CompoundTag tag){
		if(tag.contains("power"))
			POWER = tag.getInt("power");
		if(tag.contains("range"))
			POWER_RANGE = tag.getIntArray("range");
		setAngle();
	}

	public void setName(ItemStack stack){
		CUSTOM_NAME = TextFormatProcedure.getCustomItemName(stack);
		setText();
	}

	public void setName(String name){
		CUSTOM_NAME = name;
		setText();
	}

	public void setText(){
		if(CUSTOM_NAME.isEmpty())
			RENDER_TEXT.clear();
		else
			RENDER_TEXT.load(CUSTOM_NAME);
	}

	public void setAngle(){
		int rs = Math.min(POWER_RANGE[0],POWER_RANGE[1]);
		int re = Math.max(POWER_RANGE[0],POWER_RANGE[1]);
		ANGLE = (float) (POWER - rs) / (re - rs + 1);
		ANGLE = 0.5f - ANGLE;
		if(POWER_RANGE[0] > POWER_RANGE[1])
			ANGLE = -ANGLE;
		ANGLE *= (float) (2 * Math.PI);
	}

	public void setPower(int v){
		POWER = v;
		setAngle();
	}

	public boolean adjustPower(int direction){
		int power = POWER;
		if(POWER_RANGE[0] > POWER_RANGE[1])
			power -= direction;
		else
			power += direction;
		int rs = Math.min(POWER_RANGE[0],POWER_RANGE[1]);
		int re = Math.max(POWER_RANGE[0],POWER_RANGE[1]);
		if (power > re)
			power = rs;
		if (power < rs)
			power = re;
		if(POWER != power) {
			setPower(power);
			return true;
		}
		return false;
	}

	public void setPowerRange(int[] range){
		POWER_RANGE = range;
		POWER = Mth.clamp(POWER, POWER_RANGE[0], POWER_RANGE[1]);
		setAngle();
	}
}
