package net.acodonic_king.redstonecg.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.acodonic_king.redstonecg.block.ControlPanelBlock;
import net.acodonic_king.redstonecg.block.control_panel.PanelRenderRegistry;
import net.acodonic_king.redstonecg.block.control_panel.logic.DefaultPanelLogic;
import net.acodonic_king.redstonecg.init.RedstonecgModVersionRides;
import net.acodonic_king.redstonecg.procedures.RCGMatrix;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;

import static net.acodonic_king.redstonecg.block.entity.ArrowIndicatorBlockEntityRenderer.PINMARK_MODELS;

public class ControlPanelBlockEntityRenderer implements BlockEntityRenderer<ControlPanelBlockEntity> {
    BlockEntityRendererProvider.Context context;
    public ControlPanelBlockEntityRenderer(BlockEntityRendererProvider.Context context){
        super();
        this.context = context;
    }

    public ModelManager MODEL_MANAGER;
    public ModelBlockRenderer MODEL_RENDERER;
    public ItemRenderer ITEM_RENDERER;
    public VertexConsumer VERTEX_CONSUMER;
    public BlockState BLOCK_STATE;
    public PoseStack POSE_STACK;
    public int PACKED_LIGHT;
    public int PACKED_OVERLAY;
    public ControlPanelBlockEntity BLOCK_ENTITY;
    public MultiBufferSource BUFFER_SOURCE;
    public int SLOT;

    public RCGMatrix.M4F TRANSFORM_MATRIX = new RCGMatrix.M4F();

    @Override
    public void render(ControlPanelBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay){
        MODEL_MANAGER = Minecraft.getInstance().getModelManager();
        MODEL_RENDERER = Minecraft.getInstance().getBlockRenderer().getModelRenderer();
        ITEM_RENDERER = Minecraft.getInstance().getItemRenderer();

        BLOCK_ENTITY = blockEntity;
        BUFFER_SOURCE = bufferSource;
        VERTEX_CONSUMER = bufferSource.getBuffer(RenderType.cutoutMipped());
        BLOCK_STATE = blockEntity.getBlockState();
        PACKED_LIGHT = packedLight;
        PACKED_OVERLAY = packedOverlay;
        POSE_STACK = poseStack;

        POSE_STACK.pushPose();
        int orientation = BLOCK_STATE.getValue(ControlPanelBlock.ORIENTATION);
        int connection = BLOCK_ENTITY.CONNECTION & 0xFF;
        if (orientation < 30) {
            RCGMatrix.M4F mat = ControlPanelBlockEntity.sideTransform(TRANSFORM_MATRIX, orientation);
            POSE_STACK.mulPoseMatrix(mat.getMatrix());
            connection |= (connection >> 2) & 0b00010000;
            for (int i = 0; i < 5; i++) {
                boolean v = (connection & 1) == 0;
                connection >>= 1;
                if (v)
                    continue;
                BakedModel model = MODEL_MANAGER.getModel(PINMARK_MODELS[i]);
                render(model, 1.0f, 1.0f, 1.0f);
            }
        } else {
            RCGMatrix.M4F mat = ControlPanelBlockEntity.sideTransform(TRANSFORM_MATRIX, orientation);
            POSE_STACK.mulPoseMatrix(mat.getMatrix());
            for(int[] im: ControlPanelBlockEntity.O2SMA){
                if((im[1] & connection) == 0)
                    continue;
                BakedModel model = MODEL_MANAGER.getModel(PINMARK_MODELS[im[0]]);
                render(model, 1.0f, 1.0f, 1.0f);
            }
            POSE_STACK.popPose();
            POSE_STACK.pushPose();
            RCGMatrix.M4F matb = ControlPanelBlockEntity.sideTransformB(TRANSFORM_MATRIX, orientation);
            POSE_STACK.mulPoseMatrix(matb.getMatrix());
            for(int[] im: ControlPanelBlockEntity.O2SMB){
                if((im[1] & connection) == 0)
                    continue;
                BakedModel model = MODEL_MANAGER.getModel(PINMARK_MODELS[im[0]]);
                render(model, 1.0f, 1.0f, 1.0f);
            }
        }
        POSE_STACK.popPose();

        for(int i = 0; i < blockEntity.range(); i++){
            SLOT = i;
            DefaultPanelLogic logic = blockEntity.get(i);
            PanelRenderRegistry.get(logic.ITEM_STACK.getItem()).render(this, logic);
        }
    }

    public void applySlotTransform(){
        POSE_STACK.mulPoseMatrix(BLOCK_ENTITY.slotTransform(TRANSFORM_MATRIX, SLOT).getMatrix());
    }

    public void render(BakedModel model, float r, float g, float b){
        MODEL_RENDERER.renderModel(
                POSE_STACK.last(),
                VERTEX_CONSUMER,
                BLOCK_STATE,
                model, r, g, b,
                PACKED_LIGHT,
                PACKED_OVERLAY
        );
    }

    public void renderBlockState(BlockState blockState, float r, float g, float b){
        BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(blockState);
        MODEL_RENDERER.renderModel(
                POSE_STACK.last(),
                VERTEX_CONSUMER,
                blockState,
                model, r, g, b,
                PACKED_LIGHT,
                PACKED_OVERLAY
        );
    }

    public void renderItem(Item item){
        RedstonecgModVersionRides.renderStaticItem(
                ITEM_RENDERER, item,
                PACKED_LIGHT, PACKED_OVERLAY,
                POSE_STACK, BUFFER_SOURCE,
                BLOCK_ENTITY.getLevel()
        );
    }

    public BakedModel getModel(ModelResourceLocation mrl){
        return MODEL_MANAGER.getModel(mrl);
    }

    public BakedModel getBlockStateModel(BlockState blockState){
        return Minecraft.getInstance().getBlockRenderer().getBlockModel(blockState);
    }
}
