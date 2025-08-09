package net.acodonic_king.redstonecg.default_gui_classes;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class AbstractContainerScreenRide<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
    public AbstractContainerScreenRide(T p_97741_, Inventory p_97742_, Component p_97743_) {
        super(p_97741_, p_97742_, p_97743_);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        render(new ScreenStack(poseStack), mouseX, mouseY, partialTicks);
    }

    public void render(ScreenStack ms, int mouseX, int mouseY, float partialTicks) {}

    public void render(ScreenStack ms, int mouseX, int mouseY, float partialTicks, boolean isRenderBackground, boolean renderToolTips) {
        if(isRenderBackground){super.renderBackground(ms.stack);}
        super.render(ms.stack, mouseX, mouseY, partialTicks);
        if(renderToolTips){this.renderTooltip(ms.stack, mouseX, mouseY);}
    }

    @Override
    protected void renderBg(PoseStack poseStack, float v, int i, int i1) {
        renderBg(new ScreenStack(poseStack), v, i, i1);
    }

    public void renderBg(ScreenStack ms, float v, int i, int i1){}

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        renderLabels(new ScreenStack(poseStack), mouseX, mouseY);
    }

    public void renderLabels(ScreenStack ms, int mouseX, int mouseY){}

    public void renderItemStack(ScreenStack ms, ItemStack itemStack, int x, int y){
        this.itemRenderer.renderAndDecorateItem(itemStack, x, y);
    }

    public static class ScreenStack{
        public PoseStack stack;
        public ScreenStack(PoseStack poseStack){
            this.stack = poseStack;
        }
    }
}
