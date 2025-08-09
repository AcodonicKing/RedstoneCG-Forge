package net.acodonic_king.redstonecg.default_gui_classes;

import net.acodonic_king.redstonecg.init.RedstonecgModVersionRides;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class ContainerMenu extends EmptyContainerMenu{
    public ContainerMenu(MenuType<?> gui_menu, int container_size, int id, Inventory inv, FriendlyByteBuf extraData) {
        super(gui_menu, container_size, id, inv, extraData);
    }

    public void checkForBind(FriendlyByteBuf extraData){
        if (this.pos != null) {
            if (extraData.readableBytes() == 1) { // bound to item
                byte hand = extraData.readByte();
                ItemStack itemstack = hand == 0 ? this.entity.getMainHandItem() : this.entity.getOffhandItem();
                this.boundItemMatcher = () -> itemstack == (hand == 0 ? this.entity.getMainHandItem() : this.entity.getOffhandItem());
                itemstack.getCapability(RedstonecgModVersionRides.item_handler, null).ifPresent(capability -> {
                    this.internal = capability;
                    this.bound = true;
                });
            } else if (extraData.readableBytes() > 1) { // bound to entity
                extraData.readByte(); // drop padding
                this.boundEntity = world.getEntity(extraData.readVarInt());
                if (this.boundEntity != null)
                    this.boundEntity.getCapability(RedstonecgModVersionRides.item_handler, null).ifPresent(capability -> {
                        this.internal = capability;
                        this.bound = true;
                    });
            } else { // might be bound to block
                this.boundBlockEntity = this.world.getBlockEntity(pos);
                if (this.boundBlockEntity != null)
                    this.boundBlockEntity.getCapability(RedstonecgModVersionRides.item_handler, null).ifPresent(capability -> {
                        this.internal = capability;
                        this.bound = true;
                    });
            }
        }
    }

    public void makeInputSlots(int start, int amount, int[][] pos){
        amount += start;
        for(int i = start; i < amount; i++){
            int finalI = i;
            this.customSlots.put(i, this.addSlot(new SlotItemHandler(internal, finalI, pos[i][0], pos[i][1]) {
                private final int slot = finalI;
                @Override
                public void setChanged() {
                    super.setChanged();
                    slotChanged(slot, 0, 0);
                }
            }));
        }
    }

    public void makeOutputSlots(int start, int amount, int[][] pos){
        amount += start;
        for(int i = start; i < amount; i++){
            int finalI = i;
            this.customSlots.put(i, this.addSlot(new SlotItemHandler(internal, i, pos[i][0], pos[i][1]) {
                private final int slot = finalI;
                @Override
                public void onTake(@NotNull Player entity, @NotNull ItemStack stack) {
                    super.onTake(entity, stack);
                    slotChanged(slot, 1, 0);
                }

                @Override
                public void onQuickCraft(@NotNull ItemStack a, @NotNull ItemStack b) {
                    super.onQuickCraft(a, b);
                    slotChanged(slot, 2, b.getCount() - a.getCount());
                }

                @Override
                public boolean mayPlace(@NotNull ItemStack stack) {
                    return false;
                }
            }));
        }
    }

    public void makePlayerSlots(Inventory inv, int x, int y){
        //23, 84
        for (int si = 0; si < 3; ++si)
            for (int sj = 0; sj < 9; ++sj)
                this.addSlot(new Slot(inv, sj + (si + 1) * 9, x + 8 + sj * 18, y + si * 18));
        for (int si = 0; si < 9; ++si)
            this.addSlot(new Slot(inv, si, x + 8 + si * 18, y + 58));
    }

    private void slotChanged(int slotid, int ctype, int meta) {
        if (this.world != null && this.world.isClientSide()) {
            handleMenuSlotAction(entity, slotid, ctype, meta, pos);
        }
    }

    public void handleMenuSlotAction(Player entity, int slotid, int ctype, int meta, BlockPos pos){}

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot) this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < 4) {
                if (!this.moveItemStackTo(itemstack1, 4, this.slots.size(), true))
                    return ItemStack.EMPTY;
                slot.onQuickCraft(itemstack1, itemstack);
            } else if (!this.moveItemStackTo(itemstack1, 0, 4, false)) {
                if (index < 4 + 27) {
                    if (!this.moveItemStackTo(itemstack1, 4 + 27, this.slots.size(), true))
                        return ItemStack.EMPTY;
                } else {
                    if (!this.moveItemStackTo(itemstack1, 4, 4 + 27, false))
                        return ItemStack.EMPTY;
                }
                return ItemStack.EMPTY;
            }
            if (itemstack1.getCount() == 0)
                slot.set(ItemStack.EMPTY);
            else
                slot.setChanged();
            if (itemstack1.getCount() == itemstack.getCount())
                return ItemStack.EMPTY;
            slot.onTake(playerIn, itemstack1);
        }
        return itemstack;
    }

    @Override
    protected boolean moveItemStackTo(@NotNull ItemStack itemStack, int p_38905_, int p_38906_, boolean p_38907_) {
        boolean flag = false;
        int i = p_38905_;
        if (p_38907_) {
            i = p_38906_ - 1;
        }
        if (itemStack.isStackable()) {
            while (!itemStack.isEmpty()) {
                if (p_38907_) {
                    if (i < p_38905_) {
                        break;
                    }
                } else if (i >= p_38906_) {
                    break;
                }
                Slot slot = this.slots.get(i);
                ItemStack itemstack = slot.getItem();
                if (slot.mayPlace(itemstack) && !itemstack.isEmpty() && ItemStack.isSameItemSameTags(itemStack, itemstack)) {
                    int j = itemstack.getCount() + itemStack.getCount();
                    int maxSize = Math.min(slot.getMaxStackSize(), itemStack.getMaxStackSize());
                    if (j <= maxSize) {
                        itemStack.setCount(0);
                        itemstack.setCount(j);
                        slot.set(itemstack);
                        flag = true;
                    } else if (itemstack.getCount() < maxSize) {
                        itemStack.shrink(maxSize - itemstack.getCount());
                        itemstack.setCount(maxSize);
                        slot.set(itemstack);
                        flag = true;
                    }
                }
                if (p_38907_) {
                    --i;
                } else {
                    ++i;
                }
            }
        }
        if (!itemStack.isEmpty()) {
            if (p_38907_) {
                i = p_38906_ - 1;
            } else {
                i = p_38905_;
            }
            while (true) {
                if (p_38907_) {
                    if (i < p_38905_) {
                        break;
                    }
                } else if (i >= p_38906_) {
                    break;
                }
                Slot slot1 = this.slots.get(i);
                ItemStack itemstack1 = slot1.getItem();
                if (itemstack1.isEmpty() && slot1.mayPlace(itemStack)) {
                    if (itemStack.getCount() > slot1.getMaxStackSize()) {
                        slot1.set(itemStack.split(slot1.getMaxStackSize()));
                    } else {
                        slot1.set(itemStack.split(itemStack.getCount()));
                    }
                    slot1.setChanged();
                    flag = true;
                    break;
                }
                if (p_38907_) {
                    --i;
                } else {
                    ++i;
                }
            }
        }
        return flag;
    }

    @Override
    public void removed(@NotNull Player playerIn) {
        super.removed(playerIn);
        if (!bound && playerIn instanceof ServerPlayer serverPlayer) {
            if (!serverPlayer.isAlive() || serverPlayer.hasDisconnected()) {
                for (int j = 0; j < internal.getSlots(); ++j) {
                    playerIn.drop(internal.extractItem(j, internal.getStackInSlot(j).getCount(), false), false);
                }
            } else {
                for (int i = 0; i < internal.getSlots(); ++i) {
                    playerIn.getInventory().placeItemBackInInventory(internal.extractItem(i, internal.getStackInSlot(i).getCount(), false));
                }
            }
        }
    }
}
