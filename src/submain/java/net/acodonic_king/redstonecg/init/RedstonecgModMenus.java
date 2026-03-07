package net.acodonic_king.redstonecg.init;

import net.acodonic_king.redstonecg.RedstonecgMod;
import net.acodonic_king.redstonecg.block.gui.analog_source.AnalogSourceGUIMenu;
import net.acodonic_king.redstonecg.block.gui.arrow_indicator.ArrowIndicatorGUIMenu;
import net.acodonic_king.redstonecg.block.gui.delayer.DelayerGUIMenu;
import net.acodonic_king.redstonecg.block.gui.redcu_crafter.RedCuCrafterGUIMenu;
import net.acodonic_king.redstonecg.block.gui.redcu_wire_transition.RedCuWireTransitionGUIMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RedstonecgModMenus {
	public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.MENU_TYPES, RedstonecgMod.MODID);
	public static final RegistryObject<MenuType<AnalogSourceGUIMenu>> ANALOG_SOURCE_GUI = REGISTRY.register("analog_source_gui", () -> IForgeMenuType.create(AnalogSourceGUIMenu::new));
	public static final RegistryObject<MenuType<RedCuCrafterGUIMenu>> RED_CU_CRAFTER_GUI = REGISTRY.register("red_cu_crafter_gui", () -> IForgeMenuType.create(RedCuCrafterGUIMenu::new));
	public static final RegistryObject<MenuType<RedCuWireTransitionGUIMenu>> REDCU_WIRE_TRANSITION_GUI = REGISTRY.register("redcu_wire_transition_gui", () -> IForgeMenuType.create(RedCuWireTransitionGUIMenu::new));
	public static final RegistryObject<MenuType<DelayerGUIMenu>> DELAYER_GUI = REGISTRY.register("delayer_gui", () -> IForgeMenuType.create(DelayerGUIMenu::new));
	public static final RegistryObject<MenuType<ArrowIndicatorGUIMenu>> ARROW_INDICATOR_GUI = REGISTRY.register("arrow_indicator_gui", () -> IForgeMenuType.create(ArrowIndicatorGUIMenu::new));
}
