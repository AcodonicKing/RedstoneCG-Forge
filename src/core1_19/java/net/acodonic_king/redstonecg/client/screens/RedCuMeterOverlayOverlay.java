
package net.acodonic_king.redstonecg.client.screens;

import net.acodonic_king.redstonecg.default_gui_classes.AbstractContainerScreenRide;
import net.acodonic_king.redstonecg.default_gui_classes.ScreenTools;
import org.checkerframework.checker.units.qual.h;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.Minecraft;

import net.acodonic_king.redstonecg.procedures.RedCuMeterMeasuringProcedure;

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
			String measure = RedCuMeterMeasuringProcedure.MeasureString(entity);
			int y = 62;
			for(String m: measure.split("\\n")){
				AbstractContainerScreenRide.ScreenStack stack = new AbstractContainerScreenRide.ScreenStack(event.getPoseStack());
				//ScreenTools.renderRectangle(stack, 0xCCCCCC, w / 2 + 91, h / 2 + y, 0, 50, 10);
				ScreenTools.drawString(Minecraft.getInstance().font, stack, m, w / 2 + 91, h / 2 + y, 0xF0F0F0);
				y += 10;
			}
		}
	}
}
