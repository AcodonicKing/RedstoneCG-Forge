
package net.acodonic_king.redstonecg.block.gui.redcu_crafter;

import net.acodonic_king.redstonecg.block.gui.redcu_crafter.RedCuCrafterGUIScreen.ingredients_option;
import net.acodonic_king.redstonecg.block.gui.redcu_wire_transition.RedCuWireTransitionGUIButtonMessage;
import net.acodonic_king.redstonecg.init.RedstonecgModNetworking;
import net.acodonic_king.redstonecg.init.RedstonecgModVersionRides;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class RedCuCrafterGUISlotMessage {
	private final int slotID, changeType, meta;
	private BlockPos pos;

	public RedCuCrafterGUISlotMessage(int slotID, BlockPos pos, int changeType, int meta) {
		this.slotID = slotID;
		this.pos = pos;
		this.changeType = changeType;
		this.meta = meta;
	}

	public static void send(RedCuCrafterGUISlotMessage msg){
		RedstonecgModNetworking.PACKET_HANDLER.sendToServer(msg);
	}

	public RedCuCrafterGUISlotMessage(FriendlyByteBuf buffer) {
		this.slotID = buffer.readInt();
		int x = buffer.readInt();
		int y = buffer.readInt();
		int z = buffer.readInt();
		this.pos = new BlockPos(x, y, z);
		this.changeType = buffer.readInt();
		this.meta = buffer.readInt();
	}

	public static void buffer(RedCuCrafterGUISlotMessage message, FriendlyByteBuf buffer) {
		buffer.writeInt(message.slotID);
		buffer.writeInt(message.pos.getX());
		buffer.writeInt(message.pos.getY());
		buffer.writeInt(message.pos.getZ());
		buffer.writeInt(message.changeType);
		buffer.writeInt(message.meta);
	}

	public static void handleData(RedCuCrafterGUISlotMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			Player entity = context.getSender();
			message.handleSlotAction(entity);
		});
		context.setPacketHandled(true);
	}

	public void handleSlotAction(Player entity) {
		int slot = this.slotID;
		Level world = RedstonecgModVersionRides.getPlayerLevel(entity);
		HashMap guistate = RedCuCrafterGUIMenu.guistate;
		// security measure to prevent arbitrary chunk generation
		if (!world.hasChunkAt(pos))
			return;
		if (slot == 3 && changeType == 2){
			Object ingredientsobj = guistate.get("category:ingredients");
			if(ingredientsobj != null) {
				ingredients_option ingredientsopt = (ingredients_option) ingredientsobj;
				NonNullList<Ingredient> ingredients = ingredientsopt.recipe.getIngredients();
				if (entity.containerMenu instanceof Supplier _current && _current.get() instanceof Map _slots) {
					int amt = 64;
					for(int i = 0; i < ingredients.size(); i++){
						if (ingredients.get(i).getItems().length != 0) {
							Slot theslot = (Slot) _slots.get(i);
							ItemStack stack = theslot.getItem();
							amt = Math.min(amt,stack.getCount());
						}
					}
					ItemStack result = RedCuCrafterGUIMenu.getResultItem();
					amt = Math.min(result.getCount() * amt, result.getMaxStackSize());
					amt /= result.getCount();
					result.setCount(result.getCount() * amt);
					((Slot) _slots.get(3)).set(result);
					amt -= 1;
					for(int i = 0; i < ingredients.size(); i++){
						if(ingredients.get(i).getItems().length != 0){
							((Slot) _slots.get(i)).remove(amt);
						}
					}
				}
			}
		}
		if (slot == 3 && changeType == 1) {
			Object ingredientsobj = guistate.get("category:ingredients");
			if(ingredientsobj != null){
				ingredients_option ingredientsopt = (ingredients_option) ingredientsobj;
				NonNullList<Ingredient> ingredients = ingredientsopt.recipe.getIngredients();
	
			if (entity.containerMenu instanceof Supplier _current && _current.get() instanceof Map _slots) {
					for(int i = 0; i < ingredients.size(); i++){
	            		if(ingredients.get(i).getItems().length != 0){
							((Slot) _slots.get(i)).remove(1);
	            		}
					}
					entity.containerMenu.broadcastChanges();
				}
			}
		}
	}
}
