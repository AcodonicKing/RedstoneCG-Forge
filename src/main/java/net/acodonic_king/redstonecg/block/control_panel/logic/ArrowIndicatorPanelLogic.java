package net.acodonic_king.redstonecg.block.control_panel.logic;

import net.acodonic_king.redstonecg.ModLoaderRider;
import net.acodonic_king.redstonecg.block.control_panel.ComposedTextInterface;
import net.acodonic_king.redstonecg.block.entity.ControlPanelBlockEntity;
import net.acodonic_king.redstonecg.procedures.TextFormatProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;

public class ArrowIndicatorPanelLogic extends DefaultPanelLogic implements ComposedTextInterface {
    public byte MODEL = 0;
    public ResourceLocation BLOCK;
    public String BASE_MODEL = "connection=0,waterlogged=false";
    public String ARROW_MODEL = "connection=6,waterlogged=false";
    public float[] ARROW_MODEL_POSITION = new float[]{0.5f, 0.0f, 0.5f, 0.5f};
    public float[] ANGLE_CONVERSION = new float[]{(float) ((Math.PI * 1.5) / 255.0f), (float) (Math.PI * 1.75)};
    public int[] VALUE_RANGE = new int[]{0, 255};
    public int POWER = 0;
    public TextFormatProcedure.ComposedText RENDER_TEXT = new TextFormatProcedure.ComposedText();

    public ArrowIndicatorPanelLogic(ItemStack itemStack, int slot) {
        super(itemStack, slot);
        if(itemStack.getItem() instanceof BlockItem bi)
            BLOCK = ModLoaderRider.getBlockRegistryName(bi.getBlock());
    }

    @Override
    public ItemStack saveStack(){
        CompoundTag compound = ITEM_STACK.getOrCreateTag();
        CompoundTag tag = new CompoundTag();
        if (compound.contains("BlockParameterSet"))
            tag = compound.getCompound("BlockParameterSet");
        //tag.putByte("model",MODEL);
        //tag.putIntArray("range",VALUE_RANGE);
        tag.putInt("power",POWER);
        compound.put("BlockParameterSet", tag);
        return ITEM_STACK;
    }

    @Override
    public void loadStack(ItemStack itemStack){
        ITEM_STACK = itemStack;
        CompoundTag tag = itemStack.getTag();
        if(tag != null){
            if(tag.contains("BlockParameterSet")){
                tag = tag.getCompound("BlockParameterSet");
                if(tag.contains("model"))
                    setModel(tag.getByte("model"));
                if(tag.contains("range"))
                    setRange(tag.getIntArray("range"));
                if(tag.contains("power"))
                    POWER = tag.getInt("power");
            }
        }
        setRedCuSignal(POWER);
        String name = TextFormatProcedure.getCustomItemName(itemStack);
        if(name.isEmpty())
            RENDER_TEXT.clear();
        else
            RENDER_TEXT.load(name);
    }

    public void setModel(int model){
        model %= 6;
        MODEL = (byte) model;
        BASE_MODEL =  "connection="+model+",waterlogged=false";
        model %= 3;
        if(model == 2) {
            ARROW_MODEL = "connection=7,waterlogged=false";
            ARROW_MODEL_POSITION[0] = 0.75f;
            ARROW_MODEL_POSITION[2] = 0.75f;
        } else {
            ARROW_MODEL = "connection=6,waterlogged=false";
            ARROW_MODEL_POSITION[0] = 0.5f;
            ARROW_MODEL_POSITION[2] = 0.5f;
        }
    }

    public static float[] angleRange(int model){
        return switch (model % 3){
            case 0 -> new float[]{(float)(Math.PI * 1.75), (float)(Math.PI * 0.25)};
            case 1 -> new float[]{(float)(Math.PI * 1.00), (float)(Math.PI * -1.0)};
            case 2 -> new float[]{(float)(Math.PI * 1.00), (float)(Math.PI * 0.50)};
            default -> new float[]{0, 0};
        };
    }

    public void setRange(int[] range){
        setRange(range[0],range[1]);
    }
    public void setRange(int start, int end){
        VALUE_RANGE[0] = start;
        VALUE_RANGE[1] = end;
        float[] angles = angleRange(MODEL);
        float a_range = angles[0] - angles[1];
        ANGLE_CONVERSION[0] = a_range / (end - start);
        ANGLE_CONVERSION[1] = angles[0] + start * ANGLE_CONVERSION[0];
    }

    public int[] getRange(){
        return VALUE_RANGE;
    }

    public void setRedCuSignal(int value){
        float angle = value * ANGLE_CONVERSION[0];
        angle = ANGLE_CONVERSION[1] - angle;
        ARROW_MODEL_POSITION[3] = angle;
    }

    public int getRedCuSignal(){
        float angle = ARROW_MODEL_POSITION[3];
        angle = ANGLE_CONVERSION[1] - angle;
        angle /= ANGLE_CONVERSION[0];
        return (int) angle;
    }

    @Override
    public boolean receiveRedCu(ControlPanelBlockEntity be, LevelAccessor world, BlockPos pos, int power) {
        if(POWER == power)
            return false;
        POWER = power;
        setRedCuSignal(power);
        saveStack();
        return true;
    }

    @Override
    public TextFormatProcedure.ComposedText getComposedText() {
        return RENDER_TEXT;
    }
}
