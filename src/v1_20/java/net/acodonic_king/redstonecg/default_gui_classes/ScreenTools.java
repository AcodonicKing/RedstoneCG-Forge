package net.acodonic_king.redstonecg.default_gui_classes;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.acodonic_king.redstonecg.RedstonecgMod;
import net.acodonic_king.redstonecg.procedures.RCGMatrix;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.gui.Font;
import net.minecraft.sounds.SoundEvents;

public class ScreenTools {
    public static ResourceLocation _image;
    public static ResourceLocation getImage(String name){
        return new ResourceLocation(name);
    }
    public static ResourceLocation getImage(String namespace, String identification){
        return new ResourceLocation(namespace, identification);
    }
    public static void renderRectangle(AbstractContainerScreenRide.ScreenStack ms, int color, int x, int y, int l, int w, int h) {
        ms.stack.pose().pushPose();
        ms.stack.pose().translate(0, 0, l);
        ms.stack.fill(x,y,x+w,y+h,color);
        ms.stack.pose().popPose();
    }
    public static void blitTexture(Screen the, AbstractContainerScreenRide.ScreenStack ms, int x, int y, int w, int h, ResourceLocation image){
        ms.stack.blit(image, x, y, 0f, 0f, w, h, w, h);
    }
    public static void blitTexture(Screen the, AbstractContainerScreenRide.ScreenStack ms, int x, int y, int layer, int w, int h, ResourceLocation image){
        ms.stack.blit(image, x, y, layer, 0f, 0f, w, h, w, h);
    }
    public static void drawString(Font font, AbstractContainerScreenRide.ScreenStack ms, String text, int x, int y, int c){
        ms.stack.drawString(font, text, x, y, c);
    }
    public static void setTexture(ResourceLocation image){
        _image = image;
        RenderSystem.setShaderTexture(0, image);
    }
    public static void blitSetTexture(Screen the, AbstractContainerScreenRide.ScreenStack ms, int x, int y, int w, int h){
        ms.stack.blit(_image, x, y, 0, 0, w, h, w, h);
    }
    public static void blitSetTextureRegion(AbstractContainerScreenRide.ScreenStack ms, int OnScreenLeft, int OnScreenTop, int RenderWidthPx, int RenderHeightPx, int OnImageLeftPx, int OnImageTopPx, int ImageWidth, int ImageHeight){
        float OnImageLeft = (float) OnImageLeftPx / ImageWidth;
        float OnImageRight = (float) (OnImageLeftPx + RenderWidthPx) / ImageWidth;
        float OnImageTop = (float) OnImageTopPx / ImageHeight;
        float OnImageBottom = (float) (OnImageTopPx + RenderHeightPx) / ImageHeight;
        blitSetTextureRegion(
                ms,
                OnScreenLeft,OnScreenLeft + RenderWidthPx,
                OnScreenTop,OnScreenTop+ RenderHeightPx,
                0,
                OnImageLeft, OnImageRight,
                OnImageTop, OnImageBottom
        );
    }
    public static void blitSetTextureRegion(AbstractContainerScreenRide.ScreenStack ms, int OnScreenLeft, int OnScreenRight, int OnScreenTop, int OnScreenBottom, int ScreenLayer, float OnImageLeft, float OnImageRight, float OnImageTop, float OnImageBottom) {
        RCGMatrix.M4F matrix4f = new RCGMatrix.M4F(ms.stack.pose().last().pose());
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(matrix4f.getMatrix(), (float) OnScreenLeft,  (float) OnScreenBottom, (float) ScreenLayer).uv(OnImageLeft,  OnImageBottom).endVertex();
        bufferbuilder.vertex(matrix4f.getMatrix(), (float) OnScreenRight, (float) OnScreenBottom, (float) ScreenLayer).uv(OnImageRight, OnImageBottom).endVertex();
        bufferbuilder.vertex(matrix4f.getMatrix(), (float) OnScreenRight, (float) OnScreenTop,    (float) ScreenLayer).uv(OnImageRight, OnImageTop   ).endVertex();
        bufferbuilder.vertex(matrix4f.getMatrix(), (float) OnScreenLeft,  (float) OnScreenTop,    (float) ScreenLayer).uv(OnImageLeft,  OnImageTop   ).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());
    }
    public static void blitSetTextureRectaroid(AbstractContainerScreenRide.ScreenStack ms, float[][] rectaroid_pos_uv, int ScreenLayer){
        RCGMatrix.M4F matrix4f = new RCGMatrix.M4F(ms.stack.pose().last().pose());
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        for(float[] pos_uv : rectaroid_pos_uv)
            bufferbuilder.vertex(matrix4f.getMatrix(), pos_uv[0],  pos_uv[1], (float) ScreenLayer).uv(pos_uv[2],  pos_uv[3]).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());
    }
    public static void playClickSound() {
        SoundManager soundManager = Minecraft.getInstance().getSoundManager();
        soundManager.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    public static class ImageButton{
        public int OnScreenLeft, OnScreenTop, RenderWidthPx, RenderHeightPx, ImageWidth, ImageHeight;
        ResourceLocation atlas;
        public ImageButton(int OnScreenLeft, int OnScreenTop, int RenderWidthPx, int RenderHeightPx, String atlas, int ImageWidth, int ImageHeight){
            this.OnScreenLeft = OnScreenLeft;
            this.OnScreenTop = OnScreenTop;
            this.RenderWidthPx = RenderWidthPx;
            this.RenderHeightPx = RenderHeightPx;
            this.ImageWidth = ImageWidth;
            this.ImageHeight = ImageHeight;
            this.atlas = ScreenTools.getImage(RedstonecgMod.MODID,atlas);
        }
        public void render(AbstractContainerScreenRide.ScreenStack ms, int mouseX, int mouseY){
            ScreenTools.setTexture(this.atlas);
            if (OnScreenLeft <= mouseX && mouseX <= (OnScreenLeft+RenderWidthPx) && OnScreenTop <= mouseY && mouseY <= (OnScreenTop+RenderHeightPx)){
                ScreenTools.blitSetTextureRegion(ms, OnScreenLeft, OnScreenTop, RenderWidthPx, RenderHeightPx, 0, RenderHeightPx, ImageWidth, ImageHeight);
            } else {
                ScreenTools.blitSetTextureRegion(ms, OnScreenLeft, OnScreenTop, RenderWidthPx, RenderHeightPx, 0, 0, ImageWidth, ImageHeight);
            }
        }
        public void checkClick(int mouseX, int mouseY){
            if (OnScreenLeft <= mouseX && mouseX <= (OnScreenLeft+RenderWidthPx) && OnScreenTop <= mouseY && mouseY <= (OnScreenTop+RenderHeightPx)){
                ScreenTools.playClickSound();
                onClick();
            }
        }
        public void onClick(){
        }
    }
}
