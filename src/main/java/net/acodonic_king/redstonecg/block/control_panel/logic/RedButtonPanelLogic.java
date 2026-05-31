package net.acodonic_king.redstonecg.block.control_panel.logic;

import net.acodonic_king.redstonecg.block.entity.ControlPanelBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;

public class RedButtonPanelLogic extends RedSwitchPanelLogic{
    public RedButtonPanelLogic(ItemStack itemStack, int slot) {
        super(itemStack, slot);
    }

    @Override
    public InteractionResult use(ControlPanelBlockEntity be, LevelAccessor world, BlockPos pos, Player player){
        if(OUTPUT){
            OUTPUT = false;
            playSound(world, pos);
            saveStack();
            return InteractionResult.SUCCESS;
        }
        OUTPUT = true;
        playSound(world, pos);
        scheduleSelfTick(be, world, tickIn());
        saveStack();
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean tick(ControlPanelBlockEntity be, LevelAccessor world, BlockPos pos) {
        if(!OUTPUT)
            return false;
        OUTPUT = false;
        playSound(world, pos);
        saveStack();
        return true;
    }

    public int tickIn(){
        return 10;
    }
}
