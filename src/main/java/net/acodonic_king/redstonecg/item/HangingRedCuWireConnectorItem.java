package net.acodonic_king.redstonecg.item;

import net.acodonic_king.redstonecg.network.RedstonecgModVariables;
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

public class HangingRedCuWireConnectorItem extends BlockItem {
    public HangingRedCuWireConnectorItem(Block block, Properties properties) {
        super(block, properties);
    }
    @Override
    public boolean isFoil(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains("TargetConnector");
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            CompoundTag tag = stack.getTag();
            if (tag != null && tag.contains("TargetConnector")) {
                tag.remove("TargetConnector");
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
        if (tag != null && tag.contains("TargetConnector")) {
            tag = tag.getCompound("TargetConnector");
            list.add(Component.translatable("tooltip.redstonecg.hanging_redcu_wire_connector.placing"));
            int[] position = tag.getIntArray("position");
            list.add(Component.translatable("tooltip.redstonecg.hanging_redcu_wire_connector.connect", position[0], position[1], position[2]));
            list.add(Component.translatable("tooltip.redstonecg.hanging_redcu_wire_connector.distance", RedstonecgModVariables.MapVariables.get(level).hangingRedCuWireMaxDistance));
            return;
        }
        list.add(Component.translatable("tooltip.redstonecg.hanging_redcu_wire_connector.set"));
    }
}
