package net.acodonic_king.redstonecg.block.gui.control_panel;

import net.acodonic_king.redstonecg.block.entity.ControlPanelBlockEntity;
import net.acodonic_king.redstonecg.default_gui_classes.ContainerMenu;
import net.acodonic_king.redstonecg.init.RedstonecgModMenus;
import net.acodonic_king.redstonecg.init.RedstonecgModVersionRides;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import org.jetbrains.annotations.NotNull;

public class ControlPanelGUIMenu extends ContainerMenu {
	public static int[][] slot_pos = {
			{20, 21}, {68, 21}, {116, 21}, {164, 21},
	};
	public ControlPanelGUIMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
		super(RedstonecgModMenus.CONTROL_PANEL_GUI.get(), id);
		this.entity = inv.player;
		this.world = RedstonecgModVersionRides.getPlayerLevel(this.entity);
		this.pos = null;
		this.container_size = 1;
		if (extraData != null) {
			this.pos = extraData.readBlockPos();
			this.access = ContainerLevelAccess.create(this.world, this.pos);
			if(this.world.getBlockEntity(this.pos) instanceof ControlPanelBlockEntity be)
				this.container_size = be.PANELS.size();
		}
		setInternal();
		//super(RedstonecgModMenus.CONTROL_PANEL_GUI.get(), 1, id, inv, extraData);
		checkForBind(extraData);

		makeInputSlots(0, this.container_size, slot_pos);
		makePlayerSlots(inv, 12, 92);
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
