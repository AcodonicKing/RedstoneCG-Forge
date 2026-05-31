package net.acodonic_king.redstonecg.item;

import net.acodonic_king.redstonecg.ModLoaderRider;
import net.acodonic_king.redstonecg.init.RedstonecgModVersionRides;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeaconBeamBlock;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class ColoredLampItem extends BlockItem {
    public ColoredLampItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains("BlockParameterSet");
    }

    public void clearSet(ItemStack stack, Player player){
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("BlockParameterSet")) {
            CompoundTag bps = tag.getCompound("BlockParameterSet");
            if (bps.contains("item")) {
                Item item = ModLoaderRider.getItemFromRegistry(new ResourceLocation(bps.getString("item")));
                ItemStack dropStack = new ItemStack(item);
                dropStack.setCount(stack.getCount());
                //RedstonecgMod.LOGGER.debug(dropStack);
                if(!player.addItem(dropStack))
                    player.drop(dropStack, false);
            }
            tag.remove("BlockParameterSet");
            if (tag.isEmpty())
                stack.setTag(null);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            clearSet(stack,player);
            ItemStack offStack = player.getOffhandItem();
            if(offStack.getItem() instanceof BlockItem bi){
                if(bi.getBlock() instanceof BeaconBeamBlock bl){
                    if(offStack.is(RedstonecgModVersionRides.createItemTag("forge", "stained_glass_panes"))) {
                        Item stainItem = offStack.getItem();
                        CompoundTag bps = new CompoundTag();
                        bps.putString("item", ModLoaderRider.getItemRegistryName(stainItem).toString());
                        if(!player.getAbilities().instabuild) {
                            if (offStack.getCount() >= stack.getCount()) {
                                stack.getOrCreateTag().put("BlockParameterSet", bps);
                                offStack.shrink(stack.getCount());
                            } else if (offStack.getCount() > 0) {
                                ItemStack dropStack = stack.copy();
                                dropStack.setCount(stack.getCount() - offStack.getCount());
                                if(!player.addItem(dropStack))
                                    player.drop(dropStack,false);
                                stack.setCount(offStack.getCount());
                                stack.getOrCreateTag().put("BlockParameterSet", bps);
                                offStack.shrink(stack.getCount());
                            }
                        } else {
                            stack.getOrCreateTag().put("BlockParameterSet", bps);
                        }
                    }
                }
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
            list.add(Component.translatable("tooltip.redstonecg.colored_lamp.placing"));
            if(tag.contains("item")){
                Item item = ModLoaderRider.getItemFromRegistry(new ResourceLocation(tag.getString("item")));
                list.add(Component.translatable(
                        "tooltip.redstonecg.colored_lamp.stained_item",
                        item.getName(new ItemStack(item))
                ));
            }
            if(tag.contains("color")){
                int color = tag.getInt("color");
                list.add(Component.translatable(
                        "tooltip.redstonecg.colored_lamp.stained_color", color & 0xFFFFFF
                ));
            }
            list.add(Component.translatable("tooltip.redstonecg.colored_lamp.reset"));
            return;
        }
        list.add(Component.translatable("tooltip.redstonecg.colored_lamp.set"));
    }
}
