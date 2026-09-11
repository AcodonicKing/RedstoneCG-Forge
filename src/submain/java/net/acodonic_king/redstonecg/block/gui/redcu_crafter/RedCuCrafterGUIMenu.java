package net.acodonic_king.redstonecg.block.gui.redcu_crafter;

import net.acodonic_king.redstonecg.block.gui.redcu_crafter.RedCuCrafterGUIScreen.item_option;
import net.acodonic_king.redstonecg.default_gui_classes.ContainerMenu;
import net.acodonic_king.redstonecg.init.RedstonecgModMenus;
import net.acodonic_king.redstonecg.init.RedstonecgModVersionRides;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Mod.EventBusSubscriber
public class RedCuCrafterGUIMenu extends ContainerMenu {
	public RedCuCrafterGUIMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
		super(RedstonecgModMenus.REDCU_CRAFTER_GUI.get(), 4, id, inv, extraData);
		checkForBind(extraData);
		int[][] slot_pos = {
				{31, 12},
				{31, 30},
				{31, 48},
				{175, 30}
		};
		makeInputSlots(0, 3, slot_pos);
		makeOutputSlots(3, 1, slot_pos);
		makePlayerSlots(inv, 23, 84);
	}
	@Override
	public void handleMenuSlotAction(Player entity, int slotid, int ctype, int meta, BlockPos pos){
		RedCuCrafterGUISlotMessage msg = new RedCuCrafterGUISlotMessage(slotid, pos, ctype, meta);
		RedCuCrafterGUISlotMessage.send(msg);
		msg.handleSlotAction(entity);
	}

	public static void clearOutputSlot(Player _player){
		if (_player.containerMenu instanceof Supplier _current && _current.get() instanceof Map _slots) {
			((Slot) _slots.get(3)).set(ItemStack.EMPTY);
			_player.containerMenu.broadcastChanges();
		}
	}

	public static ItemStack getResultItem(){
		String strcategory = (String) guistate.getOrDefault("variable:block_category_string", "wires");
		strcategory += (String) guistate.getOrDefault("variable:junction_type_string", "/normal");
		Object objcategory = guistate.get("category:"+strcategory);
		if (objcategory == null){
			return ItemStack.EMPTY;
		}
		int block_selected = (int) guistate.getOrDefault("variable:block_selected", 0);
		List<item_option> category = (List<item_option>) objcategory;
		if(block_selected >= category.size()){
			guistate.put("variable:block_selected",0);
			return ItemStack.EMPTY;
		}
		return category.get(block_selected).itemstack.copy();
	}

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		Player entity = event.player;
		if (event.phase == TickEvent.Phase.END && entity.containerMenu instanceof RedCuCrafterGUIMenu) {
			Level world = RedstonecgModVersionRides.getPlayerLevel(entity);
			/*double x = entity.getX();
			double y = entity.getY();
			double z = entity.getZ();*/
			Object ingredientsobj = guistate.get("category:ingredients");
			if(ingredientsobj == null){clearOutputSlot(entity);}else{
				String strcategory = (String) guistate.getOrDefault("variable:block_category_string", "wires");
				strcategory += (String) guistate.getOrDefault("variable:junction_type_string", "/normal");
				Object objcategory = guistate.get("category:"+strcategory);
				if (objcategory == null){clearOutputSlot(entity);}else{
					int block_selected = (int) guistate.getOrDefault("variable:block_selected", 0);
					List<item_option> category = (List<item_option>) objcategory;
					if(block_selected >= category.size()){
						guistate.put("variable:block_selected",0);
						return;
					}
					if (entity.containerMenu instanceof Supplier _current && _current.get() instanceof Map _slots) {
						if(category.get(block_selected).recipe.matches(entity.containerMenu, world)){
							ItemStack _setstack = category.get(block_selected).itemstack.copy();
							((Slot) _slots.get(3)).set(_setstack);
							entity.containerMenu.broadcastChanges();
						}else{clearOutputSlot(entity);}
					}
				}
			}
		}
	}
}
