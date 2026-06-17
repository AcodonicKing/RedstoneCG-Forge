package net.acodonic_king.redstonecg.init;

import net.acodonic_king.redstonecg.RedstonecgMod;
import net.acodonic_king.redstonecg.block.entity.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = RedstonecgMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RedstonecgModClientSetup {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        BlockEntityRenderers.register(RedstonecgModBlockEntities.DEFAULT_ANALOG_INDICATOR.get(), DefaultAnalogIndicatorBlockEntityRenderer::new);
        BlockEntityRenderers.register(RedstonecgModBlockEntities.REDCU_WIRE_TRANSITION.get(), RedCuWireTransitionBlockEntityRenderer::new);
        BlockEntityRenderers.register(RedstonecgModBlockEntities.DELAYER.get(), DefaultAnalogIndicatorBlockEntityRenderer::new);
        BlockEntityRenderers.register(RedstonecgModBlockEntities.DEFAULT_COLORED_LAMP.get(), DefaultColoredLampBlockEntityRenderer::new);
        BlockEntityRenderers.register(RedstonecgModBlockEntities.ARROW_INDICATOR.get(), ArrowIndicatorBlockEntityRenderer::new);
        BlockEntityRenderers.register(RedstonecgModBlockEntities.DEFAULT_COLORED_FLAT_LAMP.get(), DefaultColoredFlatLampBlockEntityRenderer::new);
        BlockEntityRenderers.register(RedstonecgModBlockEntities.HANGING_REDCU_WIRE_CONNECTOR.get(), HangingRedCuWireConnectorBlockEntityRenderer::new);
        BlockEntityRenderers.register(RedstonecgModBlockEntities.ANALOG_SOURCE.get(), AnalogSourceBlockEntityRenderer::new);
        BlockEntityRenderers.register(RedstonecgModBlockEntities.CONTROL_PANEL.get(), ControlPanelBlockEntityRenderer::new);
        BlockEntityRenderers.register(RedstonecgModBlockEntities.CODED_CONTROL_PANEL.get(), ControlPanelBlockEntityRenderer::new);
        BlockEntityRenderers.register(RedstonecgModBlockEntities.RED_TOGGLE.get(), RedToggleBlockEntityRenderer::new);
    }
}
