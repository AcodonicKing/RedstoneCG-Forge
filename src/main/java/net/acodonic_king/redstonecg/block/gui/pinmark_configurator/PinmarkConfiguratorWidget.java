package net.acodonic_king.redstonecg.block.gui.pinmark_configurator;

import net.acodonic_king.redstonecg.default_gui_classes.AbstractContainerScreenRide;
import net.acodonic_king.redstonecg.default_gui_classes.ScreenTools;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;

public class PinmarkConfiguratorWidget {
    public static final ResourceLocation BACKGROUND = ScreenTools.getImage("redstonecg","textures/screens/pinmark_configurator/background.png");
    public static final ResourceLocation UI_ELEMENTS = ScreenTools.getImage("redstonecg","textures/screens/pinmark_configurator/ui.png");

    public PinmarkConfiguratorLogic LOGIC;

    /* 000DWSEN
     */
    public int PRESSED = 0;
    public int SELECTED = 0;

    public PinmarkConfiguratorWidget(){
        LOGIC = new PinmarkConfiguratorLogic();
    }

    public void render(Screen the, AbstractContainerScreenRide.ScreenStack ms, int x, int y, double mx, double my){
        ScreenTools.blitTexture(the, ms, x, y, 32, 32, BACKGROUND);
        SELECTED = mouseTouch(x, y, mx, my);
        ScreenTools.setTexture(UI_ELEMENTS);
        for(int i = 0; i < 5; i++){
            int j = i << 1;
            int t = LOGIC.getType(j);
            if(t == PinmarkConfiguratorLogic.DISABLED_TYPE)
                continue;
            int r = i == 4 ? 2 : 0;
            if(PRESSED == i) // pressed
                fetchAndRenderUI(ms, x, y, r, 3, i);
            if(SELECTED == i) // selected
                fetchAndRenderUI(ms, x, y, r + 1, 3, i);
            t --;
            if(LOGIC.getState(i + 10))
                fetchAndRenderUI(ms, x, y, r + 1, t, i);
            else
                fetchAndRenderUI(ms, x, y, r, t, i);
        }
    }

    public int onMouse(boolean pressed){
        if(pressed && SELECTED != -1) {
            PRESSED = SELECTED;
            LOGIC.setState(SELECTED + 10,!LOGIC.getState(SELECTED + 10));
        } else
            PRESSED = -1;
        return SELECTED;
    }

    public void fetchAndRenderUI(AbstractContainerScreenRide.ScreenStack ms, int x, int y, int r, int c, int m){
        int fx = c << 5;
        int fy = r << 5;
        int wx = 32;
        int wy = 32;
        switch (m) {
            case 0: {
                x += 7;
                y += 3;
                wx = 18;
                wy = 14;
                break;
            }
            case 1: {
                x += 15;
                y += 7;
                fx += 18;
                wx = 14;
                wy = 18;
                break;
            }
            case 2: {
                x += 7;
                y += 15;
                fx += 14;
                fy += 18;
                wx = 18;
                wy = 14;
                break;
            }
            case 3: {
                x += 3;
                y += 7;
                fy += 14;
                wx = 14;
                wy = 18;
                break;
            }
        }
        ScreenTools.blitSetTextureRegion(ms, x, y, wx, wy, fx, fy, 128, 128);
    }

    public int mouseTouch(int x, int y, double mx, double my){
        mx -= x;
        my -= y;
        if(mx <= 0 || mx > 32 || my <= 0 || my > 32)
            return -1;
        if(mx > 8.5 && mx <= 23.5 && my > 4.5 && my <= 8.5)
            return 0;
        if(mx > 23.5 && mx <= 27.5 && my > 8.5 && my <= 23.5)
            return 1;
        if(mx > 8.5 && mx <= 23.5 && my > 23.5 && my <= 27.5)
            return 2;
        if(mx > 4.5 && mx <= 8.5 && my > 8.5 && my <= 23.5)
            return 3;
        if(mx > 8.5 && mx <= 23.5 && my > 8.5 && my <= 23.5){
            if(mx > my){
                if((mx + my) <= 31)
                    return 0;
                return 1;
            }
            if((mx + my) <= 31)
                return 3;
            return 2;
        }
        return 4;
    }
}
