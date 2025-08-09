package net.acodonic_king.redstonecg.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static net.acodonic_king.redstonecg.RedstonecgMod.MODID;

public class RedstonecgModTabs {
    public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final RegistryObject<CreativeModeTab> TAB_REDSTONE_CG_CREATIVE_TAB = REGISTRY.register("tab",
            () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.tab_redstone_cg_creative_tab")).icon(() -> new ItemStack(RedstonecgModBlocks.REDCU_CRAFTER.get())).displayItems((parameters, tabData) -> {
                accept(tabData, RedstonecgModItems.RED_CU_METER);
                accept(tabData, RedstonecgModItems.RED_CU_CRAFTER);
                accept(tabData, RedstonecgModItems.RED_CU_METER);
                accept(tabData, RedstonecgModItems.RED_CU_MIXTURE);
                accept(tabData, RedstonecgModItems.RED_CU_INGOT);
                accept(tabData, RedstonecgModItems.RED_CU_CRAFTER);
                accept(tabData, RedstonecgModItems.SMOOTH_STONE_PLATE);
                accept(tabData, RedstonecgModItems.ROTATION_BRACKET);
                //==== Floor ====
                //Digital
                accept(tabData, RedstonecgModItems.NORMAL_AND);
                accept(tabData, RedstonecgModItems.NORMAL_OR);
                accept(tabData, RedstonecgModItems.NORMAL_XOR);
                accept(tabData, RedstonecgModItems.NORMAL_NAND);
                accept(tabData, RedstonecgModItems.NORMAL_NOR);
                accept(tabData, RedstonecgModItems.NORMAL_NXOR);
                accept(tabData, RedstonecgModItems.NORMAL_BUF);
                accept(tabData, RedstonecgModItems.NORMAL_NOT);
                accept(tabData, RedstonecgModItems.NORMAL_SR_LATCH);
                accept(tabData, RedstonecgModItems.NORMAL_D_LATCH);
                accept(tabData, RedstonecgModItems.NORMAL_T_TRIGGER);
                accept(tabData, RedstonecgModItems.NORMAL_JK_TRIGGER);
                //Analog
                accept(tabData, RedstonecgModItems.NORMAL_ANALOG_SOURCE);
                accept(tabData, RedstonecgModItems.NORMAL_ANALOG_BIAS);
                accept(tabData, RedstonecgModItems.NORMAL_ANALOG_GAIN);
                accept(tabData, RedstonecgModItems.NORMAL_ANALOG_PASS);
                accept(tabData, RedstonecgModItems.NORMAL_ANALOG_MEMORY);
                accept(tabData, RedstonecgModItems.NORMAL_ANALOG_SUBTRACTOR);
                accept(tabData, RedstonecgModItems.NORMAL_ANALOG_DIFFERENTIAL);
                accept(tabData, RedstonecgModItems.NORMAL_ANALOG_MAX);
                accept(tabData, RedstonecgModItems.NORMAL_ANALOG_MIN);
                //Hybrid
                accept(tabData, RedstonecgModItems.NORMAL_COMPARATOR);
                accept(tabData, RedstonecgModItems.NORMAL_N_COMPARATOR);
                accept(tabData, RedstonecgModItems.NORMAL_FORWARD_PATH_SELECTOR);
                accept(tabData, RedstonecgModItems.NORMAL_REVERSED_PATH_SELECTOR);
                accept(tabData, RedstonecgModItems.NORMAL_ONE_WAY_THROUGH_GATE);
                accept(tabData, RedstonecgModItems.NORMAL_ONE_WAY_THROUGH_NOT_GATE);
                //Indicator
                accept(tabData, RedstonecgModItems.UNIVERSAL_INDICATOR);
                accept(tabData, RedstonecgModItems.SEVEN_SEGMENT_INDICATOR);
                accept(tabData, RedstonecgModItems.CLOCK_FILLING_INDICATOR);
                accept(tabData, RedstonecgModItems.ORB_INDICATOR);
                //Parallel Digital
                accept(tabData, RedstonecgModItems.PARALLEL_AND);
                accept(tabData, RedstonecgModItems.PARALLEL_OR);
                accept(tabData, RedstonecgModItems.PARALLEL_XOR);
                accept(tabData, RedstonecgModItems.PARALLEL_NAND);
                accept(tabData, RedstonecgModItems.PARALLEL_NOR);
                accept(tabData, RedstonecgModItems.PARALLEL_NXOR);
                accept(tabData, RedstonecgModItems.PARALLEL_SR_LATCH);
                accept(tabData, RedstonecgModItems.PARALLEL_D_LATCH);
                //Parallel Analog
                accept(tabData, RedstonecgModItems.PARALLEL_ANALOG_BIAS);
                accept(tabData, RedstonecgModItems.PARALLEL_ANALOG_GAIN);
                accept(tabData, RedstonecgModItems.PARALLEL_ANALOG_PASS);
                accept(tabData, RedstonecgModItems.PARALLEL_ANALOG_DIFFERENTIAL);
                accept(tabData, RedstonecgModItems.PARALLEL_ANALOG_MEMORY);
                accept(tabData, RedstonecgModItems.PARALLEL_ANALOG_SUBTRACTOR);
                accept(tabData, RedstonecgModItems.PARALLEL_ANALOG_MAX);
                accept(tabData, RedstonecgModItems.PARALLEL_ANALOG_MIN);
                //Parallel Hybrid
                accept(tabData, RedstonecgModItems.PARALLEL_COMPARATOR);
                accept(tabData, RedstonecgModItems.PARALLEL_N_COMPARATOR);
                accept(tabData, RedstonecgModItems.PARALLEL_ONE_WAY_PATH_SELECTOR);
                accept(tabData, RedstonecgModItems.PARALLEL_ONE_WAY_THROUGH_GATE);
                accept(tabData, RedstonecgModItems.PARALLEL_ONE_WAY_THROUGH_NOT_GATE);
                //Wire
                accept(tabData, RedstonecgModItems.REDCU_WIRE);
                accept(tabData, RedstonecgModItems.REDSTONE_TO_REDCU_CONVERTER);
                accept(tabData, RedstonecgModItems.REDCU_WIRE_INTERSECTION);
                accept(tabData, RedstonecgModItems.REDCU_WIRE_TRANSITION);
                accept(tabData, RedstonecgModItems.PARALLEL_LINE_OUTPUT);
            }).build());
    private static void accept(CreativeModeTab.Output tabData, RegistryObject<Item> obj){
        tabData.accept(obj.get().asItem());
    }
}