package net.acodonic_king.redstonecg.block.defaults;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

public interface StainLampInterface {
    int getLampStainColor(Level world, BlockPos pos, Player entity, InteractionHand hand, BlockHitResult hit);
    Item consumeStainingItem(Level world, BlockPos pos, Player entity, InteractionHand hand, BlockHitResult hit);
}
