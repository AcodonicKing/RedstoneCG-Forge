package net.acodonic_king.redstonecg.init;

import net.acodonic_king.redstonecg.RedstonecgMod;
import net.acodonic_king.redstonecg.block.entity.DefaultAnalogIndicatorBlockEntity;
import net.acodonic_king.redstonecg.block.entity.RedCuWireTransitionBlockEntity;
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
    }
}
