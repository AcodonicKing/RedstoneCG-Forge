package net.acodonic_king.redstonecg.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.acodonic_king.redstonecg.ModLoaderRider;
import net.acodonic_king.redstonecg.init.RedstonecgModBlockEntities;
import net.acodonic_king.redstonecg.procedures.RCGQuaternion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;

import static net.acodonic_king.redstonecg.block.entity.DefaultAnalogIndicatorBlockEntity.DefaultAnalogIndicatorBlockEntityRenderer.BASE_READ_MODEL;

public class DefaultColoredFlatLampBlockEntity extends DefaultAnalogIndicatorBlockEntity{
    public byte[] COLOR = new byte[]{(byte) 242, (byte) 189, (byte) 116};
    public Item ITEM = null;
    public DefaultColoredFlatLampBlockEntity(BlockPos pos, BlockState state) {
        super(RedstonecgModBlockEntities.DEFAULT_COLORED_FLAT_LAMP.get(), pos, state);
    }
    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putByteArray("color", COLOR);
        if(ITEM != null)
            tag.putString("item", ModLoaderRider.getItemRegistryName(ITEM).toString());
    }
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        COLOR = tag.getByteArray("color");
        if(COLOR.length == 0)
            COLOR = new byte[]{(byte) 242, (byte) 189, (byte) 116};
        if(tag.contains("item"))
            ITEM = ModLoaderRider.getItemFromRegistry(new ResourceLocation(tag.getString("item")));
    }
    public void setColor(int c){
        for(int i = 2; i >= 0; i--){
            COLOR[i] = (byte) (c & 0xFF);
            c >>= 8;
        }
    }
    public void setItem(Item item){
        ITEM = item;
    }

    public static class DefaultColoredFlatLampBlockEntityRenderer implements BlockEntityRenderer<DefaultColoredFlatLampBlockEntity>{
        BlockEntityRendererProvider.Context context;
        public DefaultColoredFlatLampBlockEntityRenderer(BlockEntityRendererProvider.Context context){
            super();
            this.context = context;
        }
        @Override
        public void render(DefaultColoredFlatLampBlockEntity blockEntity, float v, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
            BlockState blockState = blockEntity.getBlockState();
            BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(blockState);

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

            float r = (blockEntity.COLOR[0] & 0xFF) / 255f;
            float g = (blockEntity.COLOR[1] & 0xFF) / 255f;
            float b = (blockEntity.COLOR[2] & 0xFF) / 255f;

            VertexConsumer vc = bufferSource.getBuffer(RenderType.cutoutMipped());
            ModelBlockRenderer modelRenderer = Minecraft.getInstance().getBlockRenderer().getModelRenderer();
            modelRenderer.renderModel(
                    poseStack.last(),
                    vc,
                    blockState,
                    model,
                    r, g, b,
                    packedLight,
                    packedOverlay
            );
            if(blockEntity.BASE_READ){
                model = Minecraft.getInstance().getModelManager().getModel(BASE_READ_MODEL);
                modelRenderer.renderModel(
                        poseStack.last(),
                        vc,
                        blockState,
                        model,
                        r, g, b,
                        packedLight,
                        packedOverlay
                );
            }
            poseStack.popPose();
        }
    }

}
