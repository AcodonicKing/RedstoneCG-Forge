package net.acodonic_king.redstonecg.block.gui.analog_source;

import net.acodonic_king.redstonecg.default_gui_classes.EmptyContainerMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.FriendlyByteBuf;

import net.acodonic_king.redstonecg.init.RedstonecgModMenus;

public class AnalogSourceGUIMenu extends EmptyContainerMenu {
	public AnalogSourceGUIMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
		super(RedstonecgModMenus.ANALOG_SOURCE_GUI.get(), 0, id, inv, extraData);
	}
}
