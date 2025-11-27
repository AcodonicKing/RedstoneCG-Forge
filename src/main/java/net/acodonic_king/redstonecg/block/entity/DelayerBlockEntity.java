package net.acodonic_king.redstonecg.block.entity;

import net.acodonic_king.redstonecg.init.RedstonecgModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class DelayerBlockEntity extends DefaultAnalogIndicatorBlockEntity {
    public int SIGNALS = 0;
    public DelayerBlockEntity(BlockPos pos, BlockState state){
        super(RedstonecgModBlockEntities.DELAYER.get(), pos, state);
    }
    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("signals", SIGNALS);
    }
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.SIGNALS = tag.getInt("signals");
    }
    public void setSignal(int index, int value){
        index <<= 2;
        value <<= index;
        SIGNALS &= ~(0xF << index);
        SIGNALS |= value;
    }
    public int getSignal(int index){
        index <<= 2;
        int value = SIGNALS >> index;
        return value & 0xF;
    }
    public int signalMemory(int value, int index){
        SIGNALS <<= 4;
        SIGNALS |= value & 0xF;
        return getSignal(index);
    }
    public boolean singleSignal(int index){
        int s = SIGNALS;
        int c = s & 0xF;
        for(int i = 0; i < index; i++){
            s >>= 4;
            if(c != (s & 0xF))
                return false;
        }
        return true;
    }
}
