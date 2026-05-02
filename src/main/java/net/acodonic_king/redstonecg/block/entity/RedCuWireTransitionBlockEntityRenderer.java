package net.acodonic_king.redstonecg.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.acodonic_king.redstonecg.init.RedstonecgModItems;
import net.acodonic_king.redstonecg.init.RedstonecgModVersionRides;
import net.acodonic_king.redstonecg.procedures.RCGQuaternion;
import net.acodonic_king.redstonecg.procedures.RedCuWireTransitionRenderEncoding;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.world.level.block.state.BlockState;

public class RedCuWireTransitionBlockEntityRenderer implements BlockEntityRenderer<RedCuWireTransitionBlockEntity> {
    BlockEntityRendererProvider.Context context;

    public RedCuWireTransitionBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super();
        this.context = context;
    }

    @Override
    public void render(RedCuWireTransitionBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ModelManager modelManager = Minecraft.getInstance().getModelManager();
        ModelBlockRenderer modelRenderer = Minecraft.getInstance().getBlockRenderer().getModelRenderer();
        VertexConsumer vc = bufferSource.getBuffer(RenderType.solid());
        BlockState blockState = blockEntity.getBlockState();
        //RandomSource random = RandomSource.create();
        //packedLight = LevelRenderer.getLightColor(blockEntity.getLevel(), blockEntity.getBlockPos());

        if(blockEntity.RENDER_OBJECTS.isEmpty()){
            blockEntity.ticks += partialTicks;
            blockEntity.ticks %= (float) (Math.PI * 20);
            poseStack.pushPose();
            poseStack.translate(0.5, 0.5, 0.5);
            poseStack.mulPose(RCGQuaternion.Vector3F.rotateYP(blockEntity.ticks / 20).quaternion);
            //poseStack.translate(-0.5, -0.5, -0.5);
            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
            RedstonecgModVersionRides.renderStaticItem(
                    itemRenderer,
                    RedstonecgModItems.REDCU_WIRE_TRANSITION.get(),
                    packedLight,
                    packedOverlay,
                    poseStack,
                    bufferSource,
                    blockEntity.getLevel()
            );
                /*itemRenderer.renderStatic(
                        new ItemStack(RedstonecgModItems.REDCU_WIRE_TRANSITION.get()),
                        //ItemTransforms.TransformType.FIXED,
                        RedstonecgModVersionRides.itemDisplayContext,
                        packedLight,
                        packedOverlay,
                        poseStack,
                        bufferSource,
                        0
                );*/
            poseStack.popPose();
        }
        for(int packed_model: blockEntity.RENDER_OBJECTS) {
            BakedModel model = RedCuWireTransitionRenderEncoding.getModel(modelManager,packed_model);
            if(model == null){continue;}

            poseStack.pushPose();
            poseStack.translate(0.5, 0.5, 0.5);
            RedCuWireTransitionRenderEncoding.applyRotationFromCode(poseStack, packed_model >> 4);
            RedCuWireTransitionRenderEncoding.applyRotationFromCode(poseStack, packed_model >> 8);
            poseStack.translate(-0.5, -0.5, -0.5);

            modelRenderer.renderModel(
                    poseStack.last(),
                    vc,
                    blockState,
                    model,
                    0.8f, 0.8f, 0.8f,
                    packedLight,
                    packedOverlay
            );

                /*for (BakedQuad quad : model.getQuads(null, null, random)) {
                    Vector3f normal = quad.getDirection().step(); // unit vector
                    normal.transform(poseStack.last().normal());
                    Direction face = Direction.getNearest(normal.x(), normal.y(), normal.z());
                    float brightness = switch (face) {
                        case DOWN -> 0.5f;
                        case UP -> 1.0f;
                        case NORTH, SOUTH -> 0.8f;
                        case EAST, WEST -> 0.6f;
                    };
                    float r = brightness;
                    float g = brightness;
                    float b = brightness;

                    vc.putBulkData(poseStack.last(), quad, r, g, b, packedLight, packedOverlay);
                }*/

            poseStack.popPose();
        }
    }
}
