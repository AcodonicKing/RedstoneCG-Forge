package net.acodonic_king.redstonecg.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.acodonic_king.redstonecg.ModLoaderRider;
import net.acodonic_king.redstonecg.RedstonecgMod;
import net.acodonic_king.redstonecg.init.RedstonecgModBlockEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;

public class DefaultColoredLampBlockEntity extends SuperBlockEntity {
    public byte[] COLOR = new byte[]{(byte) 242, (byte) 189, (byte) 116};
    //public byte BRIGHTNESS = 0;
    public Item ITEM = null;
    public DefaultColoredLampBlockEntity(BlockPos position, BlockState state) {
        super(RedstonecgModBlockEntities.DEFAULT_COLORED_LAMP.get(), position, state);
    }
    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putByteArray("color", COLOR);
        //tag.putInt("color", encodeColor());
        if(ITEM != null)
            tag.putString("item", ModLoaderRider.getItemRegistryName(ITEM).toString());
            //tag.put("item", new ItemStack(ITEM).save(new CompoundTag()));
    }
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        COLOR = tag.getByteArray("color");
        if(COLOR.length != 3)
            COLOR = new byte[]{(byte) 242, (byte) 189, (byte) 116};
        //setColor(tag.getInt("color"));
        if(tag.contains("item"))
            ITEM = ModLoaderRider.getItemFromRegistry(new ResourceLocation(tag.getString("item")));
            //ITEM = ItemStack.of(tag.getCompound("item")).getItem();
    }
    @Override
    public void handleUpdateTag(CompoundTag tag) {
        load(tag);
    }
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
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

    public static class DefaultColoredLampBlockEntityRenderer implements BlockEntityRenderer<DefaultColoredLampBlockEntity> {
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
}
