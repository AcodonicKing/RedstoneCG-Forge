package net.acodonic_king.redstonecg.block.control_panel;

import net.acodonic_king.redstonecg.ModLoaderRider;
import net.acodonic_king.redstonecg.block.control_panel.render.*;
import net.acodonic_king.redstonecg.init.RedstonecgModItems;
import net.acodonic_king.redstonecg.init.RedstonecgModVersionRides;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.Map;

public class PanelRenderRegistry {
    public static final Map<Item, DefaultPanelRender> RENDERERS = new HashMap<>();

    public static final ItemPanelRender ITEM_PANEL_RENDER = new ItemPanelRender();
    public static final DefaultPanelRender ANALOG_SOURCE = register(RedstonecgModItems.NORMAL_ANALOG_SOURCE.get(), new AnalogSourcePanelRender());
    public static final DefaultPanelRender RED_SWITCH = register(RedstonecgModItems.RED_SWITCH.get(), new RedSwitchPanelRender());
    public static final DefaultPanelRender RED_BUTTON = register(RedstonecgModItems.RED_BUTTON.get(), new RedButtonPanelRender());
    public static final DefaultPanelRender ANALOG_INDICATOR = register(
            new AnalogIndicatorPanelRender(),
            RedstonecgModItems.UNIVERSAL_INDICATOR.get(),
            RedstonecgModItems.ORB_INDICATOR.get(),
            RedstonecgModItems.SEVEN_SEGMENT_INDICATOR.get(),
            RedstonecgModItems.CLOCK_FILLING_INDICATOR.get(),
            RedstonecgModItems.HEXADECIMAL_INDICATOR.get(),
            RedstonecgModItems.FLAT_LAMP_INDICATOR.get(),
            RedstonecgModItems.BAR_INDICATOR.get()
    );
    public static final DefaultPanelRender COLORED_FLAT_LAMP = register(
            new ColoredFlatLampPanelRender(),
            RedstonecgModItems.COLORED_FLAT_LAMP_INDICATOR.get(),
            RedstonecgModItems.COLORFUL_FLAT_LAMP_INDICATOR.get()
    );
    public static final DefaultPanelRender COLORED_LAMP = register(
            new ColoredLampPanelRender(),
            RedstonecgModItems.COLORED_LAMP_BLOCK.get(),
            RedstonecgModItems.COLORFUL_LAMP_BLOCK.get()
    );
    public static final DefaultPanelRender ARROW_INDICATOR = register(RedstonecgModItems.ARROW_INDICATOR.get(), new ArrowIndicatorPanelRender());
    public static final DefaultPanelRender BUTTON_PANEL = new ButtonPanelRender();
    public static final DefaultPanelRender LEVER_PANEL = register(
            new LeverPanelRender(),
            ModLoaderRider.getItemFromRegistry(new ResourceLocation("minecraft","lever"))
    );
    public static final DefaultPanelRender TEXT_PANEL = register(
            new TextPanelRender(),
            ModLoaderRider.getItemFromRegistry(new ResourceLocation("minecraft","paper")),
            ModLoaderRider.getItemFromRegistry(new ResourceLocation("minecraft","name_tag"))
    );
    /*public static final DefaultPanelRender BLOCK_RENDER = register(
            new BlockRender(),
            ModLoaderRider.getItemFromRegistry(new ResourceLocation("minecraft","redstone_lamp"))
    );*/

    static {
        TagKey<Item> tag = RedstonecgModVersionRides.createItemTag("minecraft", "buttons");
        for(Item item: RedstonecgModVersionRides.getItemTag(tag))
            register(item, BUTTON_PANEL);
    }

    public static DefaultPanelRender register(Item item, DefaultPanelRender renderer){
        RENDERERS.put(item, renderer);
        return renderer;
    }

    public static DefaultPanelRender register(DefaultPanelRender renderer, Item... items){
        for(Item item: items)
            RENDERERS.put(item, renderer);
        return renderer;
    }

    public static DefaultPanelRender get(Item item){
        return RENDERERS.getOrDefault(item, ITEM_PANEL_RENDER);
    }
}
