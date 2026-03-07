package net.acodonic_king.redstonecg.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class ArrowIndicatorItem extends BlockItem {
    public ArrowIndicatorItem(Block block, Properties properties) {
        super(block, properties);
    }
    @Override
    public boolean isFoil(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains("BlockParameterSet");
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            CompoundTag tag = stack.getTag();
            if (tag != null && tag.contains("BlockParameterSet")) {
                tag.remove("BlockParameterSet");
                if (tag.isEmpty())
                    stack.setTag(null);
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
    @Override
    public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(itemstack, level, list, flag);
        CompoundTag tag = itemstack.getTag();
        if (tag != null && tag.contains("BlockParameterSet")) {
            tag = tag.getCompound("BlockParameterSet");
            list.add(Component.translatable("tooltip.redstonecg.arrow_indicator.placing"));
            if(tag.contains("model")){
                int model = tag.getInt("model");
                if(model >= 3)
                    list.add(Component.translatable("tooltip.redstonecg.arrow_indicator.glass_cover"));
                model %= 3;
                if(model == 0)
                    list.add(Component.translatable("tooltip.redstonecg.arrow_indicator.tq_base"));
                else if (model == 1)
                    list.add(Component.translatable("tooltip.redstonecg.arrow_indicator.qq_base"));
                else if (model == 2)
                    list.add(Component.translatable("tooltip.redstonecg.arrow_indicator.oq_base"));
            }
            if(tag.contains("range")){
                int[] range = tag.getIntArray("range");
                list.add(Component.translatable(
                        "tooltip.redstonecg.arrow_indicator.range",
                        (((float)range[0])/16.0f),
                        (((float)range[1])/16.0f)
                ));
            }
            list.add(Component.translatable("tooltip.redstonecg.arrow_indicator.reset"));
            return;
        }
        list.add(Component.translatable("tooltip.redstonecg.arrow_indicator.set"));
    }
}
