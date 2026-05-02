package net.acodonic_king.redstonecg.block.entity;

import net.acodonic_king.redstonecg.init.RedstonecgModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class DefaultAnalogIndicatorBlockEntity extends OrientationHolderBlockEntity {
    public boolean BASE_READ = false;
    public DefaultAnalogIndicatorBlockEntity(BlockPos pos, BlockState state){
        super(RedstonecgModBlockEntities.DEFAULT_ANALOG_INDICATOR.get(), pos, state);
        //modelUpdate();
    }
    public DefaultAnalogIndicatorBlockEntity(BlockEntityType blockEntityType, BlockPos pos, BlockState state){
        super(blockEntityType, pos, state);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean("base_read", BASE_READ);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("base_read"))
            BASE_READ = tag.getBoolean("base_read");
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
