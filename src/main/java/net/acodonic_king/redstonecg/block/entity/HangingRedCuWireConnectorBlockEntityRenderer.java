package net.acodonic_king.redstonecg.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.acodonic_king.redstonecg.block.defaults.PinMarkConnectionInterface;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class HangingRedCuWireConnectorBlockEntityRenderer implements BlockEntityRenderer<HangingRedCuWireConnectorBlockEntity> {
    BlockEntityRendererProvider.Context context;
    public static final ModelResourceLocation[] PINMARK_MODELS = new ModelResourceLocation[]{
            new ModelResourceLocation(new ResourceLocation("redstonecg", "hanging_redcu_wire_connector"), "connection=8,waterlogged=false"),
            new ModelResourceLocation(new ResourceLocation("redstonecg", "hanging_redcu_wire_connector"), "connection=9,waterlogged=false"),
            new ModelResourceLocation(new ResourceLocation("redstonecg", "hanging_redcu_wire_connector"), "connection=10,waterlogged=false"),
            new ModelResourceLocation(new ResourceLocation("redstonecg", "hanging_redcu_wire_connector"), "connection=11,waterlogged=false"),
            new ModelResourceLocation(new ResourceLocation("redstonecg", "hanging_redcu_wire_connector"), "connection=12,waterlogged=false"),
    };
    public static final ModelResourceLocation CONNECTOR = new ModelResourceLocation(new ResourceLocation("redstonecg", "hanging_redcu_wire_connector"), "connection=0,waterlogged=false");
    public static final ModelResourceLocation WIRE = new ModelResourceLocation(new ResourceLocation("redstonecg", "hanging_redcu_wire_connector"), "connection=1,waterlogged=false");
    public HangingRedCuWireConnectorBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super();
        this.context = context;
    }

    @Override
    public boolean shouldRenderOffScreen(HangingRedCuWireConnectorBlockEntity be) {
        return true;
    }

    @Override
    public void render(HangingRedCuWireConnectorBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ModelManager modelManager = Minecraft.getInstance().getModelManager();
        BlockState blockState = blockEntity.getBlockState();
        ModelBlockRenderer modelRenderer = Minecraft.getInstance().getBlockRenderer().getModelRenderer();
        BakedModel model = modelManager.getModel(WIRE);
        VertexConsumer vc = bufferSource.getBuffer(RenderType.cutoutMipped());

        BlockPos thisPos = blockEntity.getBlockPos();
        for(HangingRedCuWireConnectorBlockEntity.HangingRedCuWireConnectorPosition target: blockEntity.TARGETS){
            if(!target.RENDER)
                continue;
            poseStack.pushPose();
            poseStack.translate(
                    target.RELATIVE[0]+0.5,
                    target.RELATIVE[1]+0.5,
                    target.RELATIVE[2]+0.5
            );
            poseStack.mulPose(target.ROTATION.quaternion);
            poseStack.scale(1f, 1f, target.DISTANCE);
            renderModel(blockEntity, modelRenderer, vc, blockState, model, poseStack, packedLight, packedOverlay);
            poseStack.popPose();
        }

        renderPose(blockEntity, poseStack);
        model = modelManager.getModel(CONNECTOR);
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
            model = modelManager.getModel(PINMARK_MODELS[i]);
            renderModel(blockEntity, modelRenderer, vc, blockState, model, poseStack, packedLight, packedOverlay);
        }
        poseStack.popPose();
    }
    private void renderPose(HangingRedCuWireConnectorBlockEntity blockEntity, PoseStack poseStack){
        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        HangingRedCuWireConnectorBlockEntity.setFacingPoseStack(poseStack, blockEntity.facingMode, blockEntity.facing);
        poseStack.translate(-0.5, -0.5, -0.5);
    }
    private void renderModel(
            HangingRedCuWireConnectorBlockEntity blockEntity,
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
    //Override
    public AABB getRenderBoundingBox(HangingRedCuWireConnectorBlockEntity be) {
        return be.RENDER_BOUNDING_BOX;
    }
}
