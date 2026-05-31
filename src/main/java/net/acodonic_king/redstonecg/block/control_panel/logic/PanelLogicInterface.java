package net.acodonic_king.redstonecg.block.control_panel.logic;

import net.minecraft.world.item.ItemStack;

public interface PanelLogicInterface {
    DefaultPanelLogic getPanelLogic(ItemStack itemStack, int slot);
}
