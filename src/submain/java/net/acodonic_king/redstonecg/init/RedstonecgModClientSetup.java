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
        BlockEntityRenderers.register(RedstonecgModBlockEntities.DEFAULT_ANALOG_INDICATOR.get(), DefaultAnalogIndicatorBlockEntity.DefaultAnalogIndicatorBlockEntityRenderer::new);
        BlockEntityRenderers.register(RedstonecgModBlockEntities.REDCU_WIRE_TRANSITION.get(), RedCuWireTransitionBlockEntity.RedCuWireTransitionBlockEntityRenderer::new);
        BlockEntityRenderers.register(RedstonecgModBlockEntities.DELAYER.get(), DefaultAnalogIndicatorBlockEntity.DefaultAnalogIndicatorBlockEntityRenderer::new);
        BlockEntityRenderers.register(RedstonecgModBlockEntities.DEFAULT_COLORED_LAMP.get(), DefaultColoredLampBlockEntity.DefaultColoredLampBlockEntityRenderer::new);
        BlockEntityRenderers.register(RedstonecgModBlockEntities.ARROW_INDICATOR.get(), ArrowIndicatorBlockEntity.ArrowIndicatorBlockEntityRenderer::new);
        BlockEntityRenderers.register(RedstonecgModBlockEntities.DEFAULT_COLORED_FLAT_LAMP.get(), DefaultColoredFlatLampBlockEntity.DefaultColoredFlatLampBlockEntityRenderer::new);
        BlockEntityRenderers.register(RedstonecgModBlockEntities.HANGING_REDCU_WIRE_CONNECTOR.get(), HangingRedCuWireConnectorBlockEntity.HangingRedCuWireConnectorBlockEntityRenderer::new);
        BlockEntityRenderers.register(RedstonecgModBlockEntities.ANALOG_SOURCE.get(), AnalogSourceBlockEntity.AnalogSourceBlockEntityRenderer::new);
    }
}
