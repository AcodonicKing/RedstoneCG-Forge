package net.acodonic_king.redstonecg.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class ControlPanelItem extends BlockItem {
    public ControlPanelItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains("BlockParameterSet");
    }

    @Override
    public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(itemstack, level, list, flag);
        CompoundTag tag = itemstack.getTag();
        if (tag != null && tag.contains("BlockParameterSet")) {
            list.add(Component.translatable("tooltip.redstonecg.control_panel.reset"));
            return;
        }
        list.add(Component.translatable("tooltip.redstonecg.control_panel.set"));
    }
}
