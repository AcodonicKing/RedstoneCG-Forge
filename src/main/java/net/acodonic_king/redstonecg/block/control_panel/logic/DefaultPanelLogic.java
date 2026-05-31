package net.acodonic_king.redstonecg.block.control_panel.logic;

import net.acodonic_king.redstonecg.block.entity.ControlPanelBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class DefaultPanelLogic {
    public ItemStack ITEM_STACK;
    public int SLOT;
    public DefaultPanelLogic(ItemStack itemStack, int slot){
        ITEM_STACK = itemStack;
        SLOT = slot;
    }
    public ItemStack saveStack(){
        return ITEM_STACK;
    }
    public void loadStack(ItemStack stack){
        ITEM_STACK = stack;
    }
    public InteractionResult use(ControlPanelBlockEntity be, LevelAccessor world, BlockPos pos, Player player){
        saveStack();
        return InteractionResult.PASS;
    }
    public boolean tick(ControlPanelBlockEntity be, LevelAccessor world, BlockPos pos){
        saveStack();
        return false;
    }
    public boolean receiveRedstone(ControlPanelBlockEntity be, LevelAccessor world, BlockPos pos, int power){
        return false;
    }
    public int provideRedstone(ControlPanelBlockEntity be, LevelAccessor world, BlockPos pos){
        return 0;
    }
    public boolean receiveRedCu(ControlPanelBlockEntity be, LevelAccessor world, BlockPos pos, int power){
        return receiveRedstone(be, world, pos, power >> 4);
    }
    public int provideRedCu(ControlPanelBlockEntity be, LevelAccessor world, BlockPos pos){
        return provideRedstone(be, world, pos) << 4;
    }
    public void scheduleSelfTick(ControlPanelBlockEntity be, LevelAccessor world, int in){
        be.schedule(world, in, SLOT);
    }
}
