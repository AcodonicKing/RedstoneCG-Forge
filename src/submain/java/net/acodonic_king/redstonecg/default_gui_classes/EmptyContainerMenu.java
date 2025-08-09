package net.acodonic_king.redstonecg.default_gui_classes;

import net.acodonic_king.redstonecg.init.RedstonecgModVersionRides;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class EmptyContainerMenu extends AbstractContainerMenu implements Supplier<Map<Integer, Slot>> {
    public final static HashMap<String, Object> guistate = new HashMap<>();
    public final Level world;
    public final Player entity;
    public int container_size;
    public BlockPos pos;
    public ContainerLevelAccess access = ContainerLevelAccess.NULL;
    public IItemHandler internal;
    public final Map<Integer, Slot> customSlots = new HashMap<>();
    public boolean bound = false;
    public Supplier<Boolean> boundItemMatcher = null;
    public Entity boundEntity = null;
    public BlockEntity boundBlockEntity = null;

    public EmptyContainerMenu(MenuType<?> gui_menu, int container_size, int id, Inventory inv, FriendlyByteBuf extraData) {
        super(gui_menu, id);
        this.entity = inv.player;
        this.world = RedstonecgModVersionRides.getPlayerLevel(this.entity);
        this.container_size = container_size;
        this.internal = new ItemStackHandler(this.container_size);
        this.pos = null;
        if (extraData != null) {
            this.pos = extraData.readBlockPos();
            this.access = ContainerLevelAccess.create(this.world, this.pos);
        }
    }
    @Override
    public boolean stillValid(@NotNull Player player) {
        if (this.bound) {
            if (this.boundItemMatcher != null)
                return this.boundItemMatcher.get();
            else if (this.boundBlockEntity != null)
                return AbstractContainerMenu.stillValid(this.access, player, this.boundBlockEntity.getBlockState().getBlock());
            else if (this.boundEntity != null)
                return this.boundEntity.isAlive();
        }
        return true;
    }
    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int index) {
        return ItemStack.EMPTY;
    }
    public Map<Integer, Slot> get() {
        return customSlots;
    }
}
