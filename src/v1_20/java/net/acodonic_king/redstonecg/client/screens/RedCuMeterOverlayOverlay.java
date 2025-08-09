
package net.acodonic_king.redstonecg.client.screens;

import net.acodonic_king.redstonecg.default_gui_classes.AbstractContainerScreenRide;
import net.acodonic_king.redstonecg.default_gui_classes.ScreenTools;
import net.acodonic_king.redstonecg.procedures.RedCuMeterMeasuringProcedure;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber({Dist.CLIENT})
public class RedCuMeterOverlayOverlay {
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public static void eventHandler(RenderGuiEvent.Pre event) {
		int w = event.getWindow().getGuiScaledWidth();
		int h = event.getWindow().getGuiScaledHeight();
		Player entity = Minecraft.getInstance().player;
		/*
		Level world = null;
		double x = 0;
		double y = 0;
		double z = 0;
		if (entity != null) {
			world = entity.level;
			x = entity.getX();
			y = entity.getY();
			z = entity.getZ();
		}*/
		if (RedCuMeterMeasuringProcedure.IsMeasuringBool(entity)) {
			ScreenTools.drawString(Minecraft.getInstance().font, new AbstractContainerScreenRide.ScreenStack(event.getGuiGraphics()), RedCuMeterMeasuringProcedure.MeasureString(entity), w / 2 + 91, h / 2 + 62, 0x00F0F0F0);
		}
	}
}
