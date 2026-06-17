package net.acodonic_king.redstonecg.item;

import net.acodonic_king.redstonecg.ModLoaderRider;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
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
            list.add(Component.translatable("tooltip.redstonecg.control_panel.placing"));
            if(tag.contains("orientation")){
                int o = tag.getByte("orientation") & 0xFF;
                if (o < 6) o = 1;
                else if(o < 30) o = 2;
                else o = 3;
                list.add(Component.translatable("tooltip.redstonecg.control_panel.shape", o));
            }
            float[] angles = new float[16];
            if(tag.contains("slot_angles")){
                ListTag lst = tag.getList("slot_angles", CompoundTag.TAG_FLOAT);
                for(int i = 0; i < Math.min(lst.size(), angles.length); i++)
                    angles[i] = lst.getFloat(i);
            }
            NonNullList<ItemStack> inv = NonNullList.withSize(16, ItemStack.EMPTY);
            ContainerHelper.loadAllItems(tag, inv);
            for(int i = 0; i < 16; i++){
                ItemStack stack = inv.get(i);
                if(stack.isEmpty())
                    continue;
                list.add(Component.translatable("tooltip.redstonecg.control_panel.panel",
                        stack.getItem().getDescription().getString(),
                        Math.toDegrees(angles[i])
               ));
            }
            list.add(Component.translatable("tooltip.redstonecg.control_panel.reset"));
            return;
        }
        list.add(Component.translatable("tooltip.redstonecg.control_panel.set"));
    }
}
