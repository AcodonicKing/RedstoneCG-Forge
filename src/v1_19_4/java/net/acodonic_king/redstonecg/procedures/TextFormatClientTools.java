package net.acodonic_king.redstonecg.procedures;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class TextFormatClientTools {
    public static int getWidth(List<Component> textLines, Font font){
        int w = 0;
        for(Component line: textLines)
            w = Math.max(w, font.width(line));
        return Math.max(w, 1);
    }
    public static int getWidth(TextFormatProcedure.ComposedText text, Font font){
        int w = 0;
        for(Component line: text.getLines())
            w = Math.max(w, font.width(line));
        return Math.max(w, 1);
    }
    public static int getWidth(TextFormatProcedure.ComposedText text, Font font, int s, int e){
        int w = 0;
        for(int i = s; i < e; i++)
            w = Math.max(w, font.width(text.getLine(i)));
        return Math.max(w, 1);
    }
    public static int getWidth(Component line, Font font){
        return Math.max(font.width(line), 1);
    }
    public static int getHeight(List<Component> textLines){
        return Math.max(textLines.size() * getHeight(), 1);
    }
    public static int getHeight(TextFormatProcedure.ComposedText text){
        return Math.max(text.lines() * getHeight(), 1);
    }
    public static int getHeight(){
        return 10;
    }
    public static void renderText(FormattedCharSequence text, float x, float y, int color, Font font, PoseStack stack, MultiBufferSource bufferSource, int packedLight){
        font.drawInBatch(
                text, x, y, color,
                false, stack.last().pose(),
                bufferSource, Font.DisplayMode.NORMAL,
                0, packedLight
        );
    }
    public static void topText(TextFormatProcedure.ComposedText text, int color, float w, float h, float zone, Font font, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight){
        Component line = text.getFirst();
        int width = TextFormatClientTools.getWidth(line, font);
        int height = TextFormatClientTools.getHeight();
        float scale = Math.min(w/width, h/height);
        poseStack.translate(0, 0.5f-zone, 0);
        poseStack.scale(scale, scale, 1f);
        TextFormatClientTools.renderText(
                line.getVisualOrderText(),
                -width/2f, 1, color,
                font, poseStack, bufferSource, packedLight
        );
    }

    public static void bottomText(TextFormatProcedure.ComposedText text, int color, float w, float h, float zone, Font font, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight){
        Component line = text.getLast();
        int width = TextFormatClientTools.getWidth(line, font);
        int height = TextFormatClientTools.getHeight();
        float scale = Math.min(w/width, h/height);
        poseStack.translate(0, 0.5f+zone, 0f);
        poseStack.scale(scale, scale, 1f);
        TextFormatClientTools.renderText(
                line.getVisualOrderText(),
                -width/2f, 1-height, color,
                font, poseStack, bufferSource, packedLight
        );
    }

    public static void midText(TextFormatProcedure.ComposedText text, int color, float w, float h, Font font, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int s, int e){
        int width = TextFormatClientTools.getWidth(text, font, s, e);
        int height = TextFormatClientTools.getHeight(text);
        height -= TextFormatClientTools.getHeight() * (text.lines() - e + s);
        float scale = Math.min(w/width, h/height);
        poseStack.scale(scale, scale, 1f);
        int y = 1-(height/2);
        for(int i = s; i < e; i++){
            Component line = text.getLine(i);
            float x = TextFormatClientTools.getWidth(line, font) / 2f;
            TextFormatClientTools.renderText(
                    line.getVisualOrderText(),
                    -x, y, color,
                    font, poseStack,
                    bufferSource, packedLight
            );
            y += TextFormatClientTools.getHeight();
        }
    }

    public static void midText(TextFormatProcedure.ComposedText text, int color, float w, float h, Font font, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight){
        midText(text, color, w, h, font, poseStack, bufferSource, packedLight, 1, text.lines() - 1);
    }

    public static void fullText(TextFormatProcedure.ComposedText text, int color, float w, float h, Font font, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight){
        int width = TextFormatClientTools.getWidth(text, font);
        int height = TextFormatClientTools.getHeight(text);
        float scale = Math.min(w/width, h/height);
        poseStack.scale(scale, scale, 1f);
        int y = 1-(height/2);
        for(Component line: text.getLines()){
            float x = TextFormatClientTools.getWidth(line, font) / 2f;
            TextFormatClientTools.renderText(
                    line.getVisualOrderText(),
                    -x, y, color,
                    font, poseStack,
                    bufferSource, packedLight
            );
            y += TextFormatClientTools.getHeight();
        }
    }
}
