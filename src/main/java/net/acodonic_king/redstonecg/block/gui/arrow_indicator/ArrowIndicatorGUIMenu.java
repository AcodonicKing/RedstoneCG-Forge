package net.acodonic_king.redstonecg.block.gui.arrow_indicator;

import net.acodonic_king.redstonecg.block.entity.DelayerBlockEntity;
import net.acodonic_king.redstonecg.default_gui_classes.EmptyContainerMenu;
import net.acodonic_king.redstonecg.init.RedstonecgModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ArrowIndicatorGUIMenu extends EmptyContainerMenu {
    //public final ContainerData data;
    public ArrowIndicatorGUIMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        super(RedstonecgModMenus.ARROW_INDICATOR_GUI.get(), 0, id, inv, extraData);
        /*BlockEntity be = this.world.getBlockEntity(this.pos);
        if (be instanceof DelayerBlockEntity delayer) {
            this.data = new ContainerData() {
                @Override
                public int get(int index) {
                    return delayer.getSignal(index);
                }
                @Override
                public void set(int index, int value) {
                    return;
                }
                @Override
                public int getCount() {
                    return 1;
                }
            };
        } else {
            this.data = new SimpleContainerData(0); // fallback
        }
        this.addDataSlots(this.data);*/
    }
}
