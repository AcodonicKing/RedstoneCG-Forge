package net.acodonic_king.redstonecg.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.acodonic_king.redstonecg.ModLoaderRider;
import net.acodonic_king.redstonecg.RedstonecgMod;
import net.acodonic_king.redstonecg.block.defaults.PinMarkConnectionInterface;
import net.acodonic_king.redstonecg.init.RedstonecgModBlockEntities;
import net.acodonic_king.redstonecg.procedures.RCGQuaternion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public class ArrowIndicatorBlockEntity extends DefaultAnalogIndicatorBlockEntity{
    public ResourceLocation BLOCK;
    public ModelResourceLocation BASE_MODEL;
    public static final ModelResourceLocation[] PINMARK_MODELS = new ModelResourceLocation[]{
            new ModelResourceLocation(new ResourceLocation("redstonecg", "arrow_indicator"), "connection=8,waterlogged=false"),
            new ModelResourceLocation(new ResourceLocation("redstonecg", "arrow_indicator"), "connection=9,waterlogged=false"),
            new ModelResourceLocation(new ResourceLocation("redstonecg", "arrow_indicator"), "connection=10,waterlogged=false"),
            new ModelResourceLocation(new ResourceLocation("redstonecg", "arrow_indicator"), "connection=11,waterlogged=false"),
            new ModelResourceLocation(new ResourceLocation("redstonecg", "arrow_indicator"), "connection=12,waterlogged=false"),
    };
    public ModelResourceLocation ARROW_MODEL;
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
        BASE_MODEL = new ModelResourceLocation(BLOCK, "connection="+model+",waterlogged=false");
        model %= 3;
        if(model == 2) {
            ARROW_MODEL = new ModelResourceLocation(BLOCK, "connection=7,waterlogged=false");
            ARROW_MODEL_POSITION[0] = 0.75f;
            ARROW_MODEL_POSITION[2] = 0.75f;
        } else {
            ARROW_MODEL = new ModelResourceLocation(BLOCK, "connection=6,waterlogged=false");
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
    public static class ArrowIndicatorBlockEntityRenderer implements BlockEntityRenderer<ArrowIndicatorBlockEntity> {
        BlockEntityRendererProvider.Context context;
        public ArrowIndicatorBlockEntityRenderer(BlockEntityRendererProvider.Context context){
            super();
            this.context = context;
        }
        @Override
        public void render(ArrowIndicatorBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
            ModelManager modelManager = Minecraft.getInstance().getModelManager();
            BlockState blockState = blockEntity.getBlockState();
            ModelBlockRenderer modelRenderer = Minecraft.getInstance().getBlockRenderer().getModelRenderer();
            BakedModel model;
            VertexConsumer vc = bufferSource.getBuffer(RenderType.cutoutMipped());

            renderPose(blockEntity, poseStack);
            model = modelManager.getModel(blockEntity.BASE_MODEL);
            renderModel(blockEntity, modelRenderer, vc, blockState, model, poseStack, packedLight, packedOverlay);
            int connection = 0;
            if (blockState.getBlock() instanceof PinMarkConnectionInterface pmci) {
                connection = pmci.getConnection(blockState);
                connection = pmci.connectionFilter(connection);
            }
            if (blockEntity.BASE_READ)
                connection |= 16;
            for (int i = 0; i < 5; i++) {
                boolean v = (connection & 1) == 0;
                connection >>= 1;
                if (v)
                    continue;
                model = modelManager.getModel(ArrowIndicatorBlockEntity.PINMARK_MODELS[i]);
                renderModel(blockEntity, modelRenderer, vc, blockState, model, poseStack, packedLight, packedOverlay);
            }
            poseStack.translate(
                    blockEntity.ARROW_MODEL_POSITION[0],
                    blockEntity.ARROW_MODEL_POSITION[1],
                    blockEntity.ARROW_MODEL_POSITION[2]
            );
            poseStack.mulPose(RCGQuaternion.Vector3F.rotateYP(blockEntity.ARROW_MODEL_POSITION[3]).quaternion);
            model = modelManager.getModel(blockEntity.ARROW_MODEL);
            renderModel(blockEntity, modelRenderer, vc, blockState, model, poseStack, packedLight, packedOverlay);
            poseStack.popPose();
        }
        private void renderPose(ArrowIndicatorBlockEntity blockEntity, PoseStack poseStack){
            poseStack.pushPose();
            poseStack.translate(0.5, 0.5, 0.5);
            switch (blockEntity.facingMode){
                case 5 -> poseStack.mulPose(blockEntity.facing.quaternion);
                case 0 -> {}
                default -> {
                    poseStack.mulPose(blockEntity.facing.quaternion);
                    poseStack.mulPose(RCGQuaternion.Vector3F.rotateXP((float) (Math.PI * 0.5)).quaternion);
                }
            }
            poseStack.mulPose(blockEntity.rotation.quaternion);
            poseStack.translate(-0.5, -0.5, -0.5);
        }
        private void renderModel(
                ArrowIndicatorBlockEntity blockEntity,
                ModelBlockRenderer modelRenderer,
                VertexConsumer vc,
                BlockState blockState,
                BakedModel model,
                PoseStack poseStack,
                int packedLight, int packedOverlay
        ){
            modelRenderer.renderModel(
                    poseStack.last(),
                    vc,
                    blockState,
                    model,
                    1.0f, 1.0f, 1.0f,
                    packedLight,
                    packedOverlay
            );
        }
    }
}
