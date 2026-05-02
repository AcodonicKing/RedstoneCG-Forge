package net.acodonic_king.redstonecg.block.entity;

import net.acodonic_king.redstonecg.ModLoaderRider;
import net.acodonic_king.redstonecg.init.RedstonecgModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public class ArrowIndicatorBlockEntity extends DefaultAnalogIndicatorBlockEntity{
    public ResourceLocation BLOCK;
    public String BASE_MODEL;
    public String ARROW_MODEL;
    public float[] ARROW_MODEL_POSITION = new float[]{0.5f, 0.0f, 0.5f, 0.5f};
    public float[] ANGLE_CONVERSION = new float[]{(float) ((Math.PI * 1.5) / 255.0f), (float) (Math.PI * 1.75)};
    private int[] VALUE_RANGE = new int[]{0, 255};
    public byte MODEL = 0;
    public ArrowIndicatorBlockEntity(BlockPos pos, BlockState state) {
        super(RedstonecgModBlockEntities.ARROW_INDICATOR.get(), pos, state);
        BLOCK = ModLoaderRider.getBlockRegistryName(state.getBlock());
        setModel(0);
        setRange(0,256);
        //super(pos, state);
    }
    public void setModelBase(int model){
        if(isModelGlass())
            MODEL = (byte) (model + 3);
        else
            MODEL = (byte) model;
    }
    public void setModelGlass(boolean glass){
        int md = MODEL % 3;
        if(glass)
            MODEL = (byte) (md + 3);
        else
            MODEL = (byte) md;
    }
    public boolean isModelGlass(){
        return MODEL >= 3;
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
    public float[] angleRange(){
        int model = MODEL % 3;
        return switch (model){
            case 0 -> new float[]{(float)(Math.PI * 1.75), (float)(Math.PI * 0.25)};
            case 1 -> new float[]{(float)(Math.PI * 1.00), (float)(Math.PI * -1.0)};
            case 2 -> new float[]{(float)(Math.PI * 1.00), (float)(Math.PI * 0.50)};
            default -> new float[]{0, 0};
        };
    }
    public void setRange(int start, int end){
        VALUE_RANGE[0] = start;
        VALUE_RANGE[1] = end;
        float[] angles = angleRange();
        float a_range = angles[0] - angles[1];
        ANGLE_CONVERSION[0] = a_range / (end - start);
        ANGLE_CONVERSION[1] = angles[0] + start * ANGLE_CONVERSION[0];
    }
    public int[] getRange(){
        /*float[] angles = angleRange();
        float a_range = angles[0] - angles[1];
        int[] out = new int[2];
        out[0] = (int) ((ANGLE_CONVERSION[1] - angles[0]) / ANGLE_CONVERSION[0]);
        out[1] = (int)(a_range / ANGLE_CONVERSION[0]) + out[0];
        return out;*/
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
    public CompoundTag getParameterSet(){
        CompoundTag tag = new CompoundTag();
        tag.putByte("model", MODEL);
        tag.putIntArray("range", getRange());
        return tag;
    }
    public void setParameterSet(CompoundTag tag){
        if(tag.contains("model"))
            setModel(tag.getByte("model"));
        if(tag.contains("range")){
            int[] range = tag.getIntArray("range");
            setRange(range[0], range[1]);
        }
    }
    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putFloat("angle", ARROW_MODEL_POSITION[3]);
        tag.putByte("model", MODEL);
        tag.putIntArray("range", getRange());
    }
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        //if(tag.contains("power"))
        //    setRedCuSignal(tag.getInt("power") & 0xFF);
        setParameterSet(tag);
        if(tag.contains("angle"))
            ARROW_MODEL_POSITION[3] = tag.getFloat("angle");
    }
}
