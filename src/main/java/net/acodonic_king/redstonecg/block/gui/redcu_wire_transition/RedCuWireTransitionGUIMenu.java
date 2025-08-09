package net.acodonic_king.redstonecg.block.gui.redcu_wire_transition;


import net.acodonic_king.redstonecg.default_gui_classes.EmptyContainerMenu;
import net.acodonic_king.redstonecg.init.RedstonecgModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class RedCuWireTransitionGUIMenu extends EmptyContainerMenu {
    public RedCuWireTransitionGUIMenu(int id, Inventory inv, FriendlyByteBuf extraData){
        super(RedstonecgModMenus.REDCU_WIRE_TRANSITION_GUI.get(), 0, id, inv, extraData);
    }
}
