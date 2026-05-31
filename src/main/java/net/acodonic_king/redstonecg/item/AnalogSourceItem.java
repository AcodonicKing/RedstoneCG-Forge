package net.acodonic_king.redstonecg.item;

import net.acodonic_king.redstonecg.block.control_panel.logic.AnalogSourcePanelLogic;
import net.acodonic_king.redstonecg.block.control_panel.logic.DefaultPanelLogic;
import net.acodonic_king.redstonecg.block.control_panel.logic.PanelLogicInterface;
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

public class AnalogSourceItem extends BlockItem{
    public AnalogSourceItem(Block block, Properties properties) {
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
            list.add(Component.translatable("tooltip.redstonecg.analog_source.placing"));
            if(tag.contains("range")){
                int[] range = tag.getIntArray("range");
                list.add(Component.translatable(
                        "tooltip.redstonecg.analog_source.range",
                        range[0], range[1]
                ));
            }
            if(tag.contains("power")){
                int power = tag.getInt("power");
                list.add(Component.translatable(
                        "tooltip.redstonecg.analog_source.output", power
                ));
            }
            list.add(Component.translatable("tooltip.redstonecg.analog_source.reset"));
            return;
        }
        list.add(Component.translatable("tooltip.redstonecg.analog_source.set"));
    }
}
