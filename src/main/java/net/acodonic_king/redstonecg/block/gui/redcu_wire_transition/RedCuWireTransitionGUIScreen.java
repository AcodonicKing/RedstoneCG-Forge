package net.acodonic_king.redstonecg.block.gui.redcu_wire_transition;

import com.mojang.blaze3d.systems.RenderSystem;
import net.acodonic_king.redstonecg.RedstonecgMod;
import net.acodonic_king.redstonecg.block.entity.RedCuWireTransitionBlockEntity;
import net.acodonic_king.redstonecg.default_gui_classes.AbstractContainerScreenRide;
import net.acodonic_king.redstonecg.default_gui_classes.ScreenTools;
import net.acodonic_king.redstonecg.procedures.AdventureProcedure;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class RedCuWireTransitionGUIScreen extends AbstractContainerScreenRide<RedCuWireTransitionGUIMenu> {
    private final static HashMap<String, Object> guistate = RedCuWireTransitionGUIMenu.guistate;
    private final Level world;
    private final BlockPos pos;
    private final Player entity;

    public RedCuWireTransitionGUIScreen(RedCuWireTransitionGUIMenu container, Inventory inventory, Component text) {
        super(container, inventory, text);
        this.world = container.world;
        this.pos = container.pos;
        this.entity = container.entity;
        this.imageWidth = 258;
        this.imageHeight = 182;
    }

    private static final ResourceLocation background_texture = ScreenTools.getImage(RedstonecgMod.MODID,"textures/screens/redcu_wire_transition_gui/background.png");
    private static final ResourceLocation wires_texture = ScreenTools.getImage(RedstonecgMod.MODID,"textures/screens/redcu_wire_transition_gui/wires.png");
    private static final ResourceLocation button_connected_texture = ScreenTools.getImage(RedstonecgMod.MODID,"textures/screens/redcu_wire_transition_gui/button_connected.png");
    private static final ResourceLocation button_selected_texture = ScreenTools.getImage(RedstonecgMod.MODID,"textures/screens/redcu_wire_transition_gui/button_selected.png");
    private static final ResourceLocation button_smooth_stone_texture = ScreenTools.getImage(RedstonecgMod.MODID, "textures/item/smooth_stone_plate.png");

    public static Map<String, Pair<Integer, Integer>> button_locations = new HashMap<>();
    public static Map<String, Pair<Integer, Integer>> button_smooth_stone_locations = new HashMap<>();
    public static Map<String, List<Integer>> wire_fields = new HashMap<>();
    public static List<Integer> draw_wires = new ArrayList<>();

    private int refreshing = 0;
    static {
        addButtonWire("D0", 137, 52, new int[] {141, 68, 8, 7});
        addButtonWire("D1", 160, 75, new int[] {153, 79, 7, 8});
        addButtonWire("D2", 137, 98, new int[] {141, 91, 8, 7});
        addButtonWire("D3", 114, 75, new int[] {130, 79, 7, 8});

        addButtonWire("N0", 50, 2, new int[] {66, 6, 63, 9});
        addButtonWire("N1", 201, 34, new int[] {135, 6, 78, 9, 205, 14, 8, 20});
        addButtonWire("N2", 137, 34, new int[] {135, 21, 14, 13});
        addButtonWire("N3", 73, 34, new int[] {77, 21, 52, 13});

        addButtonWire("E1", 201, 52, new int[] {205, 68, 8, 7});
        addButtonWire("E0", 224, 75, new int[] {217, 79, 7, 8});
        addButtonWire("E3", 201, 98, new int[] {205, 91, 8, 7});
        addButtonWire("E2", 178, 75, new int[] {194, 79, 7, 8});

        addButtonWire("S0", 50, 148, new int[] {66, 151, 63, 9});
        addButtonWire("S1", 201, 116, new int[] {135, 151, 78, 9, 205, 132, 8, 20});
        addButtonWire("S2", 137, 116, new int[] {135, 132, 14, 13});
        addButtonWire("S3", 73, 116, new int[] {77, 132, 52, 13});

        addButtonWire("W1", 73, 52, new int[] {77, 68, 8, 7});
        addButtonWire("W2", 96, 75, new int[] {89, 79, 7, 8});
        addButtonWire("W3", 73, 98, new int[] {77, 91, 8, 7});
        addButtonWire("W0", 50, 75, new int[] {66, 79, 7, 8});

        addButtonWire("U0", 32, 2, new int[] {4, 6, 9, 87, 12, 6, 20, 8});
        addButtonWire("U1", 242, 75, new int[] {4, 98, 9, 80, 12, 170, 234, 8, 246, 91, 8, 87});
        addButtonWire("U2", 32, 148, new int[] {19, 98, 13, 62});
        addButtonWire("U3", 32, 75, new int[] {19, 79, 13, 14});

        addButton("WallD", 114, 98);
        addButton("WallN", 224,34);
        addButton("WallE", 178,98);
        addButton("WallS", 224,148);
        addButton("WallW", 50,98);
        addButton("WallU", 32, 52);
    }

    @Override
    public void render(ScreenStack ms, int mouseX, int mouseY, float partialTicks) {
        render(ms, mouseX, mouseY, partialTicks,false,true);
    }

    @Override
    public void renderBg(ScreenStack ms, float partialTicks, int gx, int gy) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        int leftX = Math.min(10, this.leftPos);
        drawWires(ms,leftX,this.topPos);
        ScreenTools.blitTexture(this, ms, leftX, this.topPos, this.imageWidth, this.imageHeight, background_texture);
        if(this.world.getBlockEntity(this.pos) instanceof RedCuWireTransitionBlockEntity be) {
            if(refreshing >= 100){
                computeWires();
                refreshing = 0;
            }
            refreshing ++;
            ScreenTools.setTexture(button_connected_texture);
            button_locations.forEach((String node, Pair<Integer, Integer> pos) -> {
                byte val = be.getSideCharacter(node.charAt(0));
                byte set_to = (byte) (node.charAt(1) - '0');
                if(val == set_to){
                    int x = pos.getLeft() + leftX;
                    int y = pos.getRight() + this.topPos;
                    ScreenTools.blitSetTexture(this, ms, x, y, 16, 16);
                }
            });
            ScreenTools.setTexture(button_smooth_stone_texture);
            button_smooth_stone_locations.forEach((String node, Pair<Integer, Integer> pos) -> {
                byte val = RedCuWireTransitionBlockEntity.getShapeIndexCharacter(node.charAt(4));
                if(((be.WALLS >> val) & 1) > 0){
                    int x = pos.getLeft() + leftX;
                    int y = pos.getRight() + this.topPos;
                    ScreenTools.blitSetTexture(this, ms, x, y, 16, 16);
                }
            });
            ScreenTools.setTexture(button_selected_texture);
            button_locations.forEach((String node, Pair<Integer, Integer> pos) -> {
                int x = pos.getLeft() + leftX;
                int y = pos.getRight() + this.topPos;
                int xw = x + 16;
                int yh = y + 16;
                if (x <= gx && gx <= xw && y <= gy && gy <= yh) {
                    ScreenTools.blitSetTexture(this, ms, x, y, 16, 16);
                }
            });
            button_smooth_stone_locations.forEach((String node, Pair<Integer, Integer> pos) -> {
                int x = pos.getLeft() + leftX;
                int y = pos.getRight() + this.topPos;
                int xw = x + 16;
                int yh = y + 16;
                if (x <= gx && gx <= xw && y <= gy && gy <= yh) {
                    ScreenTools.blitSetTexture(this, ms, x, y, 16, 16);
                }
            });
        }

        RenderSystem.disableBlend();
    }

    public void playClickSound() {
        SoundManager soundManager = Minecraft.getInstance().getSoundManager();
        soundManager.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int leftX = Math.min(10, this.leftPos);
        if(button == 0) {
            if(AdventureProcedure.pinConfig(world, entity)) {
                button_locations.forEach((String node, Pair<Integer, Integer> pos) -> {
                    int x = pos.getLeft() + leftX;
                    int y = pos.getRight() + this.topPos;
                    int xw = x + 16;
                    int yh = y + 16;
                    if (x <= mouseX && mouseX <= xw && y <= mouseY && mouseY <= yh) {
                        playClickSound();
                        RedCuWireTransitionGUIButtonMessage.sendAndHandle(this.entity, node, this.pos);
                    }
                });
                button_smooth_stone_locations.forEach((String node, Pair<Integer, Integer> pos) -> {
                    int x = pos.getLeft() + leftX;
                    int y = pos.getRight() + this.topPos;
                    int xw = x + 16;
                    int yh = y + 16;
                    if (x <= mouseX && mouseX <= xw && y <= mouseY && mouseY <= yh) {
                        playClickSound();
                        RedCuWireTransitionGUIButtonMessage.sendAndHandle(this.entity, node, this.pos);
                    }
                });
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
        // ScreenTools.drawString(this.font, poseStack, "Hello", 116, 29, 0x3C3C3C);
    }

    private static void addButton(String node, int x, int y){
        button_smooth_stone_locations.put(node, Pair.of(x, y));
    }

    private static void addButtonWire(String node, int x, int y, int[] wire_geo){
        button_locations.put(node, Pair.of(x, y));
        wire_fields.put(node, Arrays.stream(wire_geo).boxed().collect(Collectors.toList()));
    }


    public void computeWires(){
        if(this.world.getBlockEntity(this.pos) instanceof RedCuWireTransitionBlockEntity be) {
            draw_wires.clear();
            for(String cable: be.getCables()){
                draw_wires.addAll(wire_fields.get(cable));
            }
        }
    }

    private void drawWires(ScreenStack ms, int ox, int oy){
        ScreenTools.setTexture(wires_texture);
        for(int i = 0; i < draw_wires.size(); i += 4){
            int x = draw_wires.get(i);
            int y = draw_wires.get(i+1);
            int w = draw_wires.get(i+2);
            int h = draw_wires.get(i+3);
            ScreenTools.blitSetTextureRegion(ms, ox + x, oy + y, w, h, x, y, 258, 182);
        }
    }

    @Override
    public void init() {
        super.init();
        computeWires();
    }
}
