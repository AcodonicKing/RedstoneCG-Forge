package net.acodonic_king.redstonecg.block.gui.delayer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.acodonic_king.redstonecg.RedstonecgMod;
import net.acodonic_king.redstonecg.block.entity.DelayerBlockEntity;
import net.acodonic_king.redstonecg.block.normal.hybrid.DelayerBlock;
import net.acodonic_king.redstonecg.block.defaults.DelayerBlockBase;
import net.acodonic_king.redstonecg.block.parallel.hybrid.ParallelDelayerBlock;
import net.acodonic_king.redstonecg.default_gui_classes.AbstractContainerScreenRide;
import net.acodonic_king.redstonecg.default_gui_classes.ScreenTools;
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

import java.util.HashMap;

public class DelayerGUIScreen extends AbstractContainerScreenRide<DelayerGUIMenu> {
    private final static HashMap<String, Object> guistate = DelayerGUIMenu.guistate;
    private final Level world;
    private final BlockPos pos;
    private final Player entity;
    Button button_add;
    Button button_sub;
    Button button_change;

    public DelayerGUIScreen(DelayerGUIMenu container, Inventory inventory, Component text) {
        super(container, inventory, text);
        this.world = container.world;
        this.pos = container.pos;
        this.entity = container.entity;
        this.imageWidth = 152;
        this.imageHeight = 128;
    }
    private static final ResourceLocation background_texture = ScreenTools.getImage("redstonecg","textures/screens/delayer_gui/background.png");
    private static final ResourceLocation[] normal_pinmarks = {
            ScreenTools.getImage("redstonecg","textures/block/pins/b_l_alt.png"),
            ScreenTools.getImage("redstonecg","textures/block/pins/b_r_alt.png"),
            ScreenTools.getImage("redstonecg","textures/block/pins/l_r_alt.png"),
            ScreenTools.getImage("redstonecg","textures/block/pins/l_b_alt.png"),
            ScreenTools.getImage("redstonecg","textures/block/pins/r_b_alt.png"),
            ScreenTools.getImage("redstonecg","textures/block/pins/r_l_alt.png"),
    };
    private static final ResourceLocation[] parallel_pinmarks = {
            ScreenTools.getImage("redstonecg","textures/block/pins/b_ss_alt.png"),
            ScreenTools.getImage("redstonecg","textures/block/pins/ss_b_alt.png"),
    };
    private static final ResourceLocation torch = ScreenTools.getImage("redstonecg", "textures/screens/delayer_gui/torch.png");
    private static final ResourceLocation strength = ScreenTools.getImage("redstonecg", "textures/screens/delayer_gui/strength.png");
    private static final ResourceLocation state = ScreenTools.getImage("redstonecg", "textures/screens/delayer_gui/state.png");
    private static final ResourceLocation lock = ScreenTools.getImage("redstonecg", "textures/screens/delayer_gui/lock.png");

    @Override
    public void render(ScreenStack ms, int mouseX, int mouseY, float partialTicks) {
        render(ms, mouseX, mouseY, partialTicks,true,true);
    }

    @Override
    public void renderBg(ScreenStack ms, float partialTicks, int gx, int gy) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        ScreenTools.blitTexture(this, ms, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, background_texture);
        BlockState blockState = this.world.getBlockState(this.pos);
        int sz = 112;
        int lp = this.leftPos + 65 - (sz / 2);
        int tp = this.topPos + ((this.imageHeight - sz) / 2);
        int connection = 0;
        if (blockState.getBlock() instanceof DelayerBlock){
            connection = blockState.getValue(DelayerBlock.CONNECTION);
            ScreenTools.blitTexture(this, ms, lp, tp, sz, sz, normal_pinmarks[connection]);
        }
        if (blockState.getBlock() instanceof ParallelDelayerBlock){
            connection = blockState.getValue(ParallelDelayerBlock.CONNECTION);
            ScreenTools.blitTexture(this, ms, lp, tp, sz, sz, parallel_pinmarks[connection]);
        }
        tp = this.topPos + 33;
        ScreenTools.setTexture(lock);
        if(blockState.getValue(DelayerBlockBase.LOCKED))
            ScreenTools.blitSetTextureRegion(ms,this.leftPos + 87, tp, 6, 62, 6, 0, 12, 62);
        else
            ScreenTools.blitSetTextureRegion(ms,this.leftPos + 87, tp, 6, 62, 0, 0, 12, 62);
        int delay = blockState.getValue(DelayerBlockBase.DELAY) - 1;
        if(this.world.getBlockEntity(this.pos) instanceof DelayerBlockEntity be){
            ScreenTools.setTexture(state);
            if(be.getSignal(delay) > 0)
                ScreenTools.blitSetTextureRegion(ms,this.leftPos + 57, tp, 6, 62, 6, 0, 12, 62);
            else
                ScreenTools.blitSetTextureRegion(ms,this.leftPos + 57, tp, 6, 62, 0, 0, 12, 62);
            tp = this.topPos + 88 - 8 * delay;
            ScreenTools.blitTexture(this, ms, this.leftPos + 56, tp, 8, 8, torch);
            ScreenTools.setTexture(strength);
            tp = this.topPos + 89;
            lp = this.leftPos + 65;
            for(int i = 0; i <= delay; i++){
                int signal = be.getSignal(i);
                ScreenTools.blitSetTextureRegion(ms, lp, tp, 12, 6, 0, signal * 6, 12, 96);
                tp -= 8;
            }
        }
        RenderSystem.disableBlend();
    }

    public void playClickSound() {
        SoundManager soundManager = Minecraft.getInstance().getSoundManager();
        soundManager.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(button == 0) {
            if(AdventureProcedure.valueConfig(world, entity)) {
                int lp = this.leftPos + 56;
                int tp = this.topPos + 32;
                lp = (int) (mouseX - lp);
                tp = (int) (mouseY - tp);
                if(lp > 0 && lp < 22 && tp > 0 && tp < 64){
                    playClickSound();
                    int value = 16 - (tp >> 3);
                    RedstonecgMod.LOGGER.debug(value);
                    DelayerGUIButtonMessage.sendAndHandle(entity, value, this.pos);
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int key, int b, int c) {
        if (key == 256) {
            this.minecraft.player.closeContainer();
            return true;
        }
        return super.keyPressed(key, b, c);
    }

    @Override
    public void containerTick() {
        super.containerTick();
    }

    @Override
    public void renderLabels(ScreenStack ms, int mouseX, int mouseY) {
        //int value = LittleTools.getIntegerProperty(this.world.getBlockState(pos),"power");
        BlockState ThisBlock = this.world.getBlockState(pos);
        if(ThisBlock.getBlock() instanceof DelayerBlockBase b) {
            int lp = this.imageWidth - 20;
            int delay = ThisBlock.getValue(DelayerBlockBase.DELAY);
            ScreenTools.drawString(this.font, ms, delay + " t", lp, 30, 0xF0F0F0);
            int tp = 96 - 8 * delay;
            ScreenTools.drawString(this.font, ms, (((float)delay) / 20.0f) + " s", 25, tp, 0xF0F0F0);
        }
    }

    @Override
    public void init() {
        super.init();
        int lp = this.leftPos + this.imageWidth - 25;
        if(AdventureProcedure.valueConfig(world, entity)) {
            button_sub = RedstonecgModVersionRides.createButton(lp, this.topPos + 45, 20, 20, "gui.redstonecg.delayer_gui.button_sub", e -> {
                DelayerGUIButtonMessage.sendAndHandle(entity, 0, this.pos);
            });
            guistate.put("button:button_sub", button_sub);
            this.addRenderableWidget(button_sub);
            button_add = RedstonecgModVersionRides.createButton(lp, this.topPos + 5, 20, 20, "gui.redstonecg.delayer_gui.button_add", e -> {
                DelayerGUIButtonMessage.sendAndHandle(entity, 1, this.pos);
            });
            guistate.put("button:button_add", button_add);
            this.addRenderableWidget(button_add);
        }

        if(AdventureProcedure.pinConfig(world, entity)){
            lp -= 36;
            int tp = this.topPos + this.imageHeight - 25;
            button_change = RedstonecgModVersionRides.createButton(lp, tp, 56, 20, "gui.redstonecg.delayer_gui.button_change", e -> {
                DelayerGUIButtonMessage.sendAndHandle(entity, 2, this.pos);
            });
            guistate.put("button:button_change", button_change);
            this.addRenderableWidget(button_change);
        }
    }
}
