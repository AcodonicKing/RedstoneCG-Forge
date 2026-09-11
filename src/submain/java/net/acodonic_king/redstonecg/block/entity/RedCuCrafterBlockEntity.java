package net.acodonic_king.redstonecg.block.entity;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import net.acodonic_king.redstonecg.block.gui.redcu_crafter.RedCuCrafterGUIMenu;
import net.acodonic_king.redstonecg.init.RedstonecgModBlockEntities;

import io.netty.buffer.Unpooled;

public class RedCuCrafterBlockEntity extends DefaultContainerBlockEntity {
	public RedCuCrafterBlockEntity(BlockPos position, BlockState state) {
		super(RedstonecgModBlockEntities.REDCU_CRAFTER.get(), position, state, 4);
	}

	@Override
	public Component getDefaultName() {
		return Component.literal("red_cu_crafter");
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inventory) {
		return new RedCuCrafterGUIMenu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(this.worldPosition));
	}

	@Override
	public Component getDisplayName() {
		return Component.literal("Red Cu Crafter");
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack) {
        return index != 3;
    }

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
		if (index == 0)
			return false;
		if (index == 1)
			return false;
		if (index == 2)
			return false;
		if (index == 3)
			return false;
		return true;
	}
}
