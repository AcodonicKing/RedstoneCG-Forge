package net.acodonic_king.redstonecg.block.control_panel.logic;

import net.acodonic_king.redstonecg.block.control_panel.ComposedTextInterface;
import net.acodonic_king.redstonecg.block.entity.ControlPanelBlockEntity;
import net.acodonic_king.redstonecg.procedures.TextFormatProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;

public class AnalogSourcePanelLogic extends DefaultPanelLogic implements ComposedTextInterface {
    public int POWER = 0;
    public int[] POWER_RANGE = new int[]{0, 15};
    public float ANGLE = 0f;
    public TextFormatProcedure.ComposedText RENDER_TEXT = new TextFormatProcedure.ComposedText();

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
        setAngle();
        String name = TextFormatProcedure.getCustomItemName(itemStack);
        if(name.isEmpty())
            RENDER_TEXT.clear();
        else
            RENDER_TEXT.load(name);
    }

    @Override
    public InteractionResult use(ControlPanelBlockEntity be, LevelAccessor world, BlockPos pos, Player player){
        int direction = player.isCrouching() ? -1 : 1;
        int power = POWER;
        if(POWER_RANGE[0] > POWER_RANGE[1])
            power -= direction;
        else
            power += direction;
        int rs = Math.min(POWER_RANGE[0],POWER_RANGE[1]);
        int re = Math.max(POWER_RANGE[0],POWER_RANGE[1]);
        if (power > re)
            power = rs;
        if (power < rs)
            power = re;
        POWER = power;
        setAngle();
        world.playSound(null, pos, SoundEvents.STONE_BUTTON_CLICK_ON, SoundSource.BLOCKS, 1.0f, 1.0f);
        saveStack();
        return InteractionResult.SUCCESS;
    }

    public void setAngle(){
        int rs = Math.min(POWER_RANGE[0],POWER_RANGE[1]);
        int re = Math.max(POWER_RANGE[0],POWER_RANGE[1]);
        ANGLE = (float) (POWER - rs) / (re - rs + 1);
        ANGLE = 0.5f - ANGLE;
        if(POWER_RANGE[0] > POWER_RANGE[1])
            ANGLE = -ANGLE;
        ANGLE *= (float) (2 * Math.PI);
    }

    @Override
    public int provideRedstone(ControlPanelBlockEntity be, LevelAccessor world, BlockPos pos){
        return POWER;
    }

    @Override
    public TextFormatProcedure.ComposedText getComposedText() {
        return RENDER_TEXT;
    }
}
