package net.acodonic_king.redstonecg.block.control_panel.logic;

import net.acodonic_king.redstonecg.ModLoaderRider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

public class ButtonPanelLogic extends RedButtonPanelLogic{
    public ResourceLocation MODEL;

    public ButtonPanelLogic(ItemStack itemStack, int slot) {
        super(itemStack, slot);
        if(itemStack.getItem() instanceof BlockItem bi)
            MODEL = ModLoaderRider.getBlockRegistryName(bi.getBlock());
    }

    public int tickIn(){
        return 30;
    }
}
