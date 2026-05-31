package net.acodonic_king.redstonecg.block.gui.control_panel;

import net.acodonic_king.redstonecg.RedstonecgMod;
import net.acodonic_king.redstonecg.block.entity.ControlPanelBlockEntity;
import net.acodonic_king.redstonecg.default_gui_classes.ContainerMenu;
import net.acodonic_king.redstonecg.init.RedstonecgModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class ControlPanelGUIMenu extends ContainerMenu {
	public ControlPanelGUIMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
		super(RedstonecgModMenus.CONTROL_PANEL_GUI.get(), 1, id, inv, extraData);
		checkForBind(extraData);
		int[][] slot_pos = {
				{80, 30},
		};
		makeInputSlots(0, 1, slot_pos);
		makePlayerSlots(inv, 0, 84);
	}
	@Override
	public void handleMenuSlotAction(Player entity, int slotid, int ctype, int meta, BlockPos pos){
		//RedstonecgMod.LOGGER.debug(slotid+" "+ctype+" "+meta);
		//RedstonecgMod.LOGGER.debug("menu "+slots.get(slotid).getItem());
		ControlPanelGUISlotMessage msg = new ControlPanelGUISlotMessage(slotid, pos, ctype, meta, slots.get(slotid).getItem());
		ControlPanelGUISlotMessage.send(msg);
		msg.handleSlotAction(entity);
	}

	@Override
	public void removed(@NotNull Player playerIn) {
		super.removed(playerIn);
		if(pos == null)
			return;
		if(world.getBlockEntity(pos) instanceof ControlPanelBlockEntity be) {
			be.syncInventory(pos);
			be.initPanelLogic();
			be.setChanged();

		}
	}
}
