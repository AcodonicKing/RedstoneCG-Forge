package net.acodonic_king.redstonecg.block.gui.arrow_indicator;

import com.mojang.blaze3d.systems.RenderSystem;
import net.acodonic_king.redstonecg.block.entity.ArrowIndicatorBlockEntity;
import net.acodonic_king.redstonecg.block.normal.indicator.ArrowIndicatorBlock;
import net.acodonic_king.redstonecg.default_gui_classes.AbstractContainerScreenRide;
import net.acodonic_king.redstonecg.default_gui_classes.ScreenTools;
import net.acodonic_king.redstonecg.default_gui_classes.TypingBox;
import net.acodonic_king.redstonecg.init.RedstonecgModVersionRides;
import net.acodonic_king.redstonecg.procedures.AdventureProcedure;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ArrowIndicatorGUIScreen extends AbstractContainerScreenRide<ArrowIndicatorGUIMenu> {
    private final static HashMap<String, Object> guistate = ArrowIndicatorGUIMenu.guistate;
    private final Level world;
    private final BlockPos pos;
    private final Player entity;
    Button button_change;
    TypingBox range_box_start;
    TypingBox range_box_end;
    private static List<buttonImage> renderableButtons = new ArrayList<>();


    public ArrowIndicatorGUIScreen(ArrowIndicatorGUIMenu container, Inventory inventory, Component text) {
        super(container, inventory, text);
        this.world = container.world;
        this.pos = container.pos;
        this.entity = container.entity;
        this.imageWidth = 212;
        this.imageHeight = 128;
    }
    private static final ResourceLocation background_texture = ScreenTools.getImage("redstonecg","textures/screens/arrow_indicator_gui/background.png");
    private static final ResourceLocation pinmark_f = ScreenTools.getImage("redstonecg","textures/block/pins/f.png");
    private static final ResourceLocation pinmark_r = ScreenTools.getImage("redstonecg","textures/block/pins/r.png");
    private static final ResourceLocation pinmark_b = ScreenTools.getImage("redstonecg","textures/block/pins/b.png");
    private static final ResourceLocation pinmark_l = ScreenTools.getImage("redstonecg","textures/block/pins/l.png");
    private static final ResourceLocation f_arrow = ScreenTools.getImage("redstonecg","textures/screens/arrow_indicator_gui/f_arrow.png");
    private static final ResourceLocation h_arrow = ScreenTools.getImage("redstonecg","textures/screens/arrow_indicator_gui/h_arrow.png");
    private static final ResourceLocation platesHQ = ScreenTools.getImage("redstonecg", "textures/screens/arrow_indicator_gui/plates.png");
    private static final ResourceLocation selected = ScreenTools.getImage("redstonecg", "textures/screens/arrow_indicator_gui/selected.png");
    private static float[][] arrow_rectoid = new float[][]{
            { 0f,  0f, 0f, 1f},
            {57f,  0f, 0f, 0f},
            {57f, 21f, 1f, 0f},
            { 0f, 21f, 1f, 1f}
    };

    private float rotateX(float x, float y, double ang_cos, double ang_sin){
        return (float) (x * ang_cos - y * ang_sin);
    }
    private float rotateY(float x, float y, double ang_cos, double ang_sin){
        return (float) (x * ang_sin + y * ang_cos);
    }
    private void setArrowRectoid(float[] arrow_model_position, float length, int x, int y, boolean isLong){
        //63.5f or 46.5f
        final float corn = 8.4f;
        final float qPI = (float) (Math.PI * 0.5);
        float angle = qPI - arrow_model_position[3];
        if(isLong)
            angle -= qPI;
        double ang_cos = Math.cos(angle);
        double ang_sin = Math.sin(angle);
        arrow_rectoid[0][0] = rotateX(-corn, -corn, ang_cos, ang_sin);
        arrow_rectoid[0][1] = rotateY(-corn, -corn, ang_cos, ang_sin);

        arrow_rectoid[3][0] = rotateX(length, -corn, ang_cos, ang_sin);
        arrow_rectoid[3][1] = rotateY(length, -corn, ang_cos, ang_sin);

        arrow_rectoid[2][0] = rotateX(length, corn, ang_cos, ang_sin);
        arrow_rectoid[2][1] = rotateY(length, corn, ang_cos, ang_sin);

        arrow_rectoid[1][0] = rotateX(-corn, corn, ang_cos, ang_sin);
        arrow_rectoid[1][1] = rotateY(-corn, corn, ang_cos, ang_sin);

        float add_x = this.leftPos + x + arrow_model_position[0] * 112;
        float add_y = this.topPos + y + arrow_model_position[2] * 112;
        for(int i = 0; i < arrow_rectoid.length; i++){
            arrow_rectoid[i][0] += add_x;
            arrow_rectoid[i][1] += add_y;
        }
    }

    @Override
    public void render(ScreenStack ms, int mouseX, int mouseY, float partialTicks) {
        render(ms, mouseX, mouseY, partialTicks,true,true);
        //range_box_start.render(ms.stack, mouseX, mouseY, partialTicks);
        //range_box_end.render(ms.stack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void renderBg(ScreenStack ms, float partialTicks, int gx, int gy) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        ScreenTools.blitTexture(this, ms, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, background_texture);
        BlockState blockState = this.world.getBlockState(this.pos);
        int connection = blockState.getValue(ArrowIndicatorBlock.CONNECTION);
        int sz = 64;
        int lp = this.leftPos + 65 - (sz / 2);
        int tp = this.topPos + ((this.imageHeight - sz) / 2);
        connection++;
        if ((connection & 1) > 0)
            ScreenTools.blitTexture(this, ms, lp, tp-24, sz, sz, pinmark_f);
        if ((connection & 2) > 0)
            ScreenTools.blitTexture(this, ms, lp+24, tp, sz, sz, pinmark_r);
        if ((connection & 4) > 0)
            ScreenTools.blitTexture(this, ms, lp, tp+24, sz, sz, pinmark_b);
        if ((connection & 8) > 0)
            ScreenTools.blitTexture(this, ms, lp-24, tp, sz, sz, pinmark_l);
        if(world.getBlockEntity(pos) instanceof ArrowIndicatorBlockEntity be){
            int model = be.MODEL;
            if(AdventureProcedure.valueConfig(world, entity)) {
                buttonImage btn = renderableButtons.get(3);
                btn.button.render(ms, gx, gy);
                if (be.isModelGlass())
                    ScreenTools.blitTexture(this, ms, btn.x - 1, btn.y - 1, 18, 18, selected);
                model %= 3;
                for (int i = 0; i < 3; i++) {
                    btn = renderableButtons.get(i);
                    btn.button.render(ms, gx, gy);
                    if (model == i)
                        ScreenTools.blitTexture(this, ms, btn.x - 1, btn.y - 1, 18, 18, selected);
                }
            }
            if(AdventureProcedure.pinConfig(world, entity)) {
                buttonImage btn = renderableButtons.get(4);
                btn.button.render(ms, gx, gy);
                if(be.BASE_READ)
                    ScreenTools.blitTexture(this, ms, btn.x - 1, btn.y - 1, 18, 18, selected);
            }
            model = be.MODEL;
            model %= 3;
            ScreenTools.setTexture(platesHQ);
            ScreenTools.blitSetTextureRegion(ms, 22 + this.leftPos, 22 + this.topPos, 84, 84, model * 84, 0, 252, 84);
            if(model == 2) {
                setArrowRectoid(be.ARROW_MODEL_POSITION, 55.6f, 8, 8, true);
                ScreenTools.setTexture(f_arrow);
            } else {
                setArrowRectoid(be.ARROW_MODEL_POSITION, 37.2f, 8, 8, false);
                ScreenTools.setTexture(h_arrow);
            }
            ScreenTools.blitSetTextureRectaroid(ms, arrow_rectoid, 1);
        }
        RenderSystem.disableBlend();
    }

    public void playClickSound() {
        SoundManager soundManager = Minecraft.getInstance().getSoundManager();
        soundManager.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(range_box_start.mouseClicked(mouseX, mouseY, button) || range_box_end.mouseClicked(mouseX, mouseY, button)){
            return super.mouseClicked(mouseX, mouseY, button);
        } else if (range_box_start.isFocused() || range_box_end.isFocused()){
            ArrowIndicatorGUIButtonMessage.sendAndHandle(entity, 0, pos);
            range_box_start.setFocused(false);
            range_box_end.setFocused(false);
            return true;
        }
        if(button == 0){
            for(buttonImage btn: renderableButtons)
                btn.button.checkClick((int) mouseX, (int) mouseY);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int key, int b, int c) {
        if (key == 256) {
            this.minecraft.player.closeContainer();
            return true;
        }
        if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER) {
            if (range_box_start.isFocused() || range_box_end.isFocused()){
                ArrowIndicatorGUIButtonMessage.sendAndHandle(entity, 0, pos);
                range_box_start.setFocused(false);
                range_box_end.setFocused(false);
                return true;
            }
        }
        return super.keyPressed(key, b, c);
    }

    @Override
    public void containerTick() {
        super.containerTick();
        range_box_start.tick();
        range_box_end.tick();
    }

    @Override
    public void renderLabels(ScreenStack ms, int mouseX, int mouseY) {
        BlockState ThisBlock = this.world.getBlockState(pos);
        if(world.getBlockEntity(pos) instanceof ArrowIndicatorBlockEntity be)
            ScreenTools.drawString(this.font, ms, String.valueOf(((float)be.getRedCuSignal())/16.0f), 126, 30, 0xF0F0F0);
    }

    public static class buttonImage{
        public int x, y;
        ScreenTools.ImageButton button;
        public buttonImage(ArrowIndicatorGUIScreen screen, int id, int x, int y, String name){
            this.x = screen.leftPos + x;
            this.y = screen.topPos + y;
			this.button = new ScreenTools.ImageButton(this.x, this.y, 16, 16, "textures/screens/arrow_indicator_gui/atlas/"+name+".png", 16, 32){
                @Override
                public void onClick(){
                    ArrowIndicatorGUIButtonMessage.sendAndHandle(screen.entity, id, screen.pos);
                }
            };
            guistate.put("button:"+name, this);
            renderableButtons.add(this);
        }
    }

    @Override
    public void init() {
        super.init();
        int lp = this.leftPos + 126;
        int tp = this.topPos + this.imageHeight - 25;
        renderableButtons = new ArrayList<>();
        button_change = RedstonecgModVersionRides.createButton(lp, tp, 60, 20, "gui.redstonecg.arrow_indicator_gui.button_change", e -> {
            ArrowIndicatorGUIButtonMessage.sendAndHandle(entity, 2, this.pos);
        });
        guistate.put("button:button_change", button_change);
        if(AdventureProcedure.pinConfig(world, entity)){
            this.addRenderableWidget(button_change);
        }
        int[] range = new int[]{0,0};
        if(this.world.getBlockEntity(this.pos) instanceof ArrowIndicatorBlockEntity be)
            range = be.getRange();
        range_box_start = new TypingBox(this.font, lp, this.topPos + 45, 60, 18, Component.empty());
        range_box_start.setValue(String.valueOf(((float)range[0])/16.0f));
        range_box_start.setMaxLength(9);
        guistate.put("box:range_box_start", range_box_start);
        range_box_end = new TypingBox(this.font, lp, this.topPos + 5, 60, 18, Component.empty());
        range_box_end.setValue(String.valueOf(((float)range[1])/16.0f));
        range_box_end.setMaxLength(9);
        guistate.put("box:range_box_end", range_box_end);
        if(AdventureProcedure.valueConfig(world, entity)) {
            this.addRenderableWidget(range_box_start);
            this.addRenderableWidget(range_box_end);
            lp = this.imageWidth - 22;
            new buttonImage(this, 3, lp, 6, "wide");
            new buttonImage(this, 4, lp, 26, "full");
            new buttonImage(this, 5, lp, 46, "corner");
            new buttonImage(this, 6, lp, this.imageHeight - 22, "glass");
        }
        if(AdventureProcedure.pinConfig(world, entity)){
            new buttonImage(this, 7, this.imageWidth - 22, this.imageHeight - 42, "back");
        }
    }
}
