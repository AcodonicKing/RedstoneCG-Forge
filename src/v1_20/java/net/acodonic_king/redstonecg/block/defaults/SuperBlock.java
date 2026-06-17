package net.acodonic_king.redstonecg.block.defaults;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;


import java.util.ArrayList;
import java.util.List;

public class SuperBlock extends Block {
    public SuperBlock(Properties properties) {
        super(properties);
    }
    public List<ItemStack> getDrops(List<ItemStack> list, BlockState state, BlockEntity entity){
        return list;
    }
    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        List<ItemStack> list = super.getDrops(state, builder);
        return getDrops(list, state, builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY));
    }
}
