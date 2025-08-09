package net.acodonic_king.redstonecg.default_gui_classes;

import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;

public interface ItemRenderingInterface {
    ItemRenderer getItemRenderer();
    void renderItemStack(ItemStack itemStack, int x, int y);
}
