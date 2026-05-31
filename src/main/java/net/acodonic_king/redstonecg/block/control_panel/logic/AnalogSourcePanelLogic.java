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

public class AnalogSourcePanelLogic extends DefaultPanelLogic{
    public int POWER = 0;
    public int[] POWER_RANGE = new int[]{0, 15};

    public AnalogSourcePanelLogic(ItemStack itemStack, int slot){
        super(itemStack, slot);
        loadStack(itemStack);
    }

    @Override
    public ItemStack saveStack(){
        CompoundTag tag = new CompoundTag();
        tag.putInt("power", POWER);
        tag.putIntArray("range", POWER_RANGE);
        ITEM_STACK.getOrCreateTag().put("BlockParameterSet", tag);
        return ITEM_STACK;
    }

    @Override
    public void loadStack(ItemStack itemStack){
        ITEM_STACK = itemStack;
        CompoundTag tag = itemStack.getTag();
        if(tag != null){
            if(tag.contains("BlockParameterSet")){
                tag = tag.getCompound("BlockParameterSet");
                if(tag.contains("power"))
                    POWER = tag.getInt("power");
                if(tag.contains("range"))
                    POWER_RANGE = tag.getIntArray("range");
            }
        }
    }

    @Override
    public InteractionResult use(ControlPanelBlockEntity be, LevelAccessor world, BlockPos pos, Player player){
        POWER += player.isCrouching() ? -1 : 1;
        if(POWER > POWER_RANGE[1])
            POWER = POWER_RANGE[0];
        if(POWER < POWER_RANGE[0])
            POWER = POWER_RANGE[1];
        world.playSound(null, pos, SoundEvents.STONE_BUTTON_CLICK_ON, SoundSource.BLOCKS, 1.0f, 1.0f);
        saveStack();
        return InteractionResult.SUCCESS;
    }

    @Override
    public int provideRedstone(ControlPanelBlockEntity be, LevelAccessor world, BlockPos pos){
        return POWER;
    }
}
