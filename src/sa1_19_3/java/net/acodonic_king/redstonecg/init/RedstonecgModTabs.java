package net.acodonic_king.redstonecg.init;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static net.acodonic_king.redstonecg.RedstonecgMod.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RedstonecgModTabs {
    public static CreativeModeTab TAB_REDSTONE_CG_CREATIVE_TAB;

    public static void load(CreativeModeTabEvent.BuildContents event) {
        if(event.getTab() == TAB_REDSTONE_CG_CREATIVE_TAB){
            event.accept(RedstonecgModItems.RED_CU_METER);
            event.accept(RedstonecgModItems.RED_CU_MIXTURE);
            event.accept(RedstonecgModItems.RED_CU_INGOT);
            event.accept(RedstonecgModItems.RED_CU_CRAFTER);
            event.accept(RedstonecgModItems.SMOOTH_STONE_PLATE);
            event.accept(RedstonecgModItems.ROTATION_BRACKET);
            //==== Floor ====
            //Digital
            event.accept(RedstonecgModItems.NORMAL_AND);
            event.accept(RedstonecgModItems.NORMAL_OR);
            event.accept(RedstonecgModItems.NORMAL_XOR);
            event.accept(RedstonecgModItems.NORMAL_NAND);
            event.accept(RedstonecgModItems.NORMAL_NOR);
            event.accept(RedstonecgModItems.NORMAL_NXOR);
            event.accept(RedstonecgModItems.NORMAL_BUF);
            event.accept(RedstonecgModItems.NORMAL_NOT);
            event.accept(RedstonecgModItems.NORMAL_SR_LATCH);
            event.accept(RedstonecgModItems.NORMAL_D_LATCH);
            event.accept(RedstonecgModItems.NORMAL_T_TRIGGER);
            event.accept(RedstonecgModItems.NORMAL_JK_TRIGGER);
            //Analog
            event.accept(RedstonecgModItems.NORMAL_ANALOG_SOURCE);
            event.accept(RedstonecgModItems.NORMAL_ANALOG_BIAS);
            event.accept(RedstonecgModItems.NORMAL_ANALOG_GAIN);
            event.accept(RedstonecgModItems.NORMAL_ANALOG_PASS);
            event.accept(RedstonecgModItems.NORMAL_ANALOG_MEMORY);
            event.accept(RedstonecgModItems.NORMAL_ANALOG_SUBTRACTOR);
            event.accept(RedstonecgModItems.NORMAL_ANALOG_DIFFERENTIAL);
            event.accept(RedstonecgModItems.NORMAL_ANALOG_MAX);
            event.accept(RedstonecgModItems.NORMAL_ANALOG_MIN);
            //Hybrid
            event.accept(RedstonecgModItems.NORMAL_COMPARATOR);
            event.accept(RedstonecgModItems.NORMAL_N_COMPARATOR);
            event.accept(RedstonecgModItems.NORMAL_FORWARD_PATH_SELECTOR);
            event.accept(RedstonecgModItems.NORMAL_REVERSED_PATH_SELECTOR);
            event.accept(RedstonecgModItems.NORMAL_ONE_WAY_THROUGH_GATE);
            event.accept(RedstonecgModItems.NORMAL_ONE_WAY_THROUGH_NOT_GATE);
            //Indicator
            event.accept(RedstonecgModItems.UNIVERSAL_INDICATOR);
            event.accept(RedstonecgModItems.SEVEN_SEGMENT_INDICATOR);
            event.accept(RedstonecgModItems.CLOCK_FILLING_INDICATOR);
            event.accept(RedstonecgModItems.ORB_INDICATOR);
            //Parallel Digital
            event.accept(RedstonecgModItems.PARALLEL_AND);
            event.accept(RedstonecgModItems.PARALLEL_OR);
            event.accept(RedstonecgModItems.PARALLEL_XOR);
            event.accept(RedstonecgModItems.PARALLEL_NAND);
            event.accept(RedstonecgModItems.PARALLEL_NOR);
            event.accept(RedstonecgModItems.PARALLEL_NXOR);
            event.accept(RedstonecgModItems.PARALLEL_SR_LATCH);
            event.accept(RedstonecgModItems.PARALLEL_D_LATCH);
            //Parallel Analog
            event.accept(RedstonecgModItems.PARALLEL_ANALOG_BIAS);
            event.accept(RedstonecgModItems.PARALLEL_ANALOG_GAIN);
            event.accept(RedstonecgModItems.PARALLEL_ANALOG_PASS);
            event.accept(RedstonecgModItems.PARALLEL_ANALOG_DIFFERENTIAL);
            event.accept(RedstonecgModItems.PARALLEL_ANALOG_MEMORY);
            event.accept(RedstonecgModItems.PARALLEL_ANALOG_SUBTRACTOR);
            event.accept(RedstonecgModItems.PARALLEL_ANALOG_MAX);
            event.accept(RedstonecgModItems.PARALLEL_ANALOG_MIN);
            //Parallel Hybrid
            event.accept(RedstonecgModItems.PARALLEL_COMPARATOR);
            event.accept(RedstonecgModItems.PARALLEL_N_COMPARATOR);
            event.accept(RedstonecgModItems.PARALLEL_ONE_WAY_PATH_SELECTOR);
            event.accept(RedstonecgModItems.PARALLEL_ONE_WAY_THROUGH_GATE);
            event.accept(RedstonecgModItems.PARALLEL_ONE_WAY_THROUGH_NOT_GATE);
            //Wire
            event.accept(RedstonecgModItems.REDCU_WIRE);
            event.accept(RedstonecgModItems.REDSTONE_TO_REDCU_CONVERTER);
            event.accept(RedstonecgModItems.REDCU_WIRE_INTERSECTION);
            event.accept(RedstonecgModItems.REDCU_WIRE_TRANSITION);
            event.accept(RedstonecgModItems.PARALLEL_LINE_OUTPUT);
        }
    }

    @SubscribeEvent
    public static void registerCreativeModTabs(CreativeModeTabEvent.Register event){
        TAB_REDSTONE_CG_CREATIVE_TAB = event.registerCreativeModeTab(new ResourceLocation(MODID, "tab_redstone_cg_creative_tab"),
                builder -> builder
                        .icon(() -> new ItemStack(RedstonecgModBlocks.REDCU_CRAFTER.get()))
                        .title(Component.translatable("itemGroup.tab_redstone_cg_creative_tab"))
        );
    }
}