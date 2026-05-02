package net.acodonic_king.redstonecg.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;

public class DefaultColoredLampBlockEntityRenderer implements BlockEntityRenderer<DefaultColoredLampBlockEntity> {
    BlockEntityRendererProvider.Context context;
    public DefaultColoredLampBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super();
        this.context = context;
    }
    @Override
    public void render(DefaultColoredLampBlockEntity blockEntity, float v, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState blockState = blockEntity.getBlockState();
        BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(blockState);
        float r = (blockEntity.COLOR[0] & 0xFF) / 255f;
        float g = (blockEntity.COLOR[1] & 0xFF) / 255f;
        float b = (blockEntity.COLOR[2] & 0xFF) / 255f;
        VertexConsumer vc = bufferSource.getBuffer(RenderType.cutout());
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(
                poseStack.last(),
                vc,
                blockState,
                model,
                r, g, b,
                packedLight,
                packedOverlay
        );
    }
}
