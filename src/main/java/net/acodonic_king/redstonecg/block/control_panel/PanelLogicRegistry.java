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
    public static final BiFunction<ItemStack, Integer, DefaultPanelLogic> ANALOG_INDICATOR = register(
            AnalogIndicatorPanelLogic::new,
            RedstonecgModItems.UNIVERSAL_INDICATOR.get(),
            RedstonecgModItems.ORB_INDICATOR.get(),
            RedstonecgModItems.SEVEN_SEGMENT_INDICATOR.get(),
            RedstonecgModItems.CLOCK_FILLING_INDICATOR.get(),
            RedstonecgModItems.HEXADECIMAL_INDICATOR.get(),
            RedstonecgModItems.FLAT_LAMP_INDICATOR.get(),
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
