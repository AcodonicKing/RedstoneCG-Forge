package net.acodonic_king.redstonecg.block.control_panel;

import net.acodonic_king.redstonecg.ModLoaderRider;
import net.acodonic_king.redstonecg.block.control_panel.logic.*;
import net.acodonic_king.redstonecg.init.RedstonecgModItems;
import net.acodonic_king.redstonecg.init.RedstonecgModVersionRides;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class PanelLogicRegistry {
    public static final Map<Item, BiFunction<ItemStack, Integer, DefaultPanelLogic>> LOGIC = new HashMap<>();

    public static final BiFunction<ItemStack, Integer, DefaultPanelLogic> DEFAULT_LOGIC = DefaultPanelLogic::new;
    public static final BiFunction<ItemStack, Integer, DefaultPanelLogic> ANALOG_SOURCE = register(RedstonecgModItems.NORMAL_ANALOG_SOURCE.get(), AnalogSourcePanelLogic::new);
    public static final BiFunction<ItemStack, Integer, DefaultPanelLogic> RED_SWITCH = register(RedstonecgModItems.RED_SWITCH.get(), RedSwitchPanelLogic::new);
    public static final BiFunction<ItemStack, Integer, DefaultPanelLogic> RED_BUTTON = register(RedstonecgModItems.RED_BUTTON.get(), RedButtonPanelLogic::new);
    public static final BiFunction<ItemStack, Integer, DefaultPanelLogic> ANALOG_INDICATOR_FLAT = register(
            (itemStack, slot) -> new AnalogIndicatorPanelLogic(itemStack, slot, 0.0f),
            RedstonecgModItems.ORB_INDICATOR.get(),
            RedstonecgModItems.SEVEN_SEGMENT_INDICATOR.get(),
            RedstonecgModItems.CLOCK_FILLING_INDICATOR.get(),
            RedstonecgModItems.HEXADECIMAL_INDICATOR.get()
            );
    public static final BiFunction<ItemStack, Integer, DefaultPanelLogic> ANALOG_INDICATOR_LAMP = register(
            (itemStack, slot) -> new AnalogIndicatorPanelLogic(itemStack, slot, 0.094f),
            RedstonecgModItems.UNIVERSAL_INDICATOR.get(),
            RedstonecgModItems.FLAT_LAMP_INDICATOR.get()
    );
    public static final BiFunction<ItemStack, Integer, DefaultPanelLogic> ANALOG_INDICATOR_BAR = register(
            (itemStack, slot) -> new AnalogIndicatorPanelLogic(itemStack, slot, 0.25f),
            RedstonecgModItems.BAR_INDICATOR.get()
    );
    public static final BiFunction<ItemStack, Integer, DefaultPanelLogic> COLORED_LAMP = register(
            ColoredLampPanelLogic::new,
            RedstonecgModItems.COLORED_FLAT_LAMP_INDICATOR.get(),
            RedstonecgModItems.COLORED_LAMP_BLOCK.get()
    );
    public static final BiFunction<ItemStack, Integer, DefaultPanelLogic> COLORFUL_LAMP = register(
            ColorfulLampPanelLogic::new,
            RedstonecgModItems.COLORFUL_FLAT_LAMP_INDICATOR.get(),
            RedstonecgModItems.COLORFUL_LAMP_BLOCK.get()
    );
    public static final BiFunction<ItemStack, Integer, DefaultPanelLogic> ARROW_INDICATOR = register(RedstonecgModItems.ARROW_INDICATOR.get(), ArrowIndicatorPanelLogic::new);
    public static final BiFunction<ItemStack, Integer, DefaultPanelLogic> LEVER_PANEL = register(
            LeverPanelLogic::new,
            ModLoaderRider.getItemFromRegistry(new ResourceLocation("minecraft","lever"))
    );
    public static final BiFunction<ItemStack, Integer, DefaultPanelLogic> PAPER_TEXT_PANEL = register(
            TextPanelLogic::new,
            ModLoaderRider.getItemFromRegistry(new ResourceLocation("minecraft","paper"))
    );
    public static final BiFunction<ItemStack, Integer, DefaultPanelLogic> NAME_TAG_TEXT_PANEL = register(
            (itemStack, slot) -> new TextPanelLogic(itemStack, slot){
                @Override
                public ResourceLocation getPaper(){
                    return new ResourceLocation("redstonecg","textures/panels/text_panel/name_tag.png");
                }
                @Override
                public int getTextColor(){
                    return 0x7A7162;
                }
            },
            ModLoaderRider.getItemFromRegistry(new ResourceLocation("minecraft","name_tag"))
    );
    /*public static final BiFunction<ItemStack, Integer, DefaultPanelLogic> REDSTONE_LAMP_PANEL = register(
            RedstoneLampLogic::new,
            ModLoaderRider.getItemFromRegistry(new ResourceLocation("minecraft","redstone_lamp"))
    );*/

    static {
        TagKey<Item> tag = RedstonecgModVersionRides.createItemTag("minecraft", "buttons");
        for(Item item: RedstonecgModVersionRides.getItemTag(tag))
            register(item, ButtonPanelLogic::new);
    }



    public static BiFunction<ItemStack, Integer, DefaultPanelLogic> register(Item item, BiFunction<ItemStack, Integer, DefaultPanelLogic> logic){
        LOGIC.put(item, logic);
        return logic;
    }

    public static BiFunction<ItemStack, Integer, DefaultPanelLogic> register(BiFunction<ItemStack, Integer, DefaultPanelLogic> logic, Item... items){
        for (Item item: items)
            LOGIC.put(item, logic);
        return logic;
    }

    public static DefaultPanelLogic get(ItemStack stack, int slot){
        return LOGIC.getOrDefault(stack.getItem(), DEFAULT_LOGIC).apply(stack, slot);
    }
}
