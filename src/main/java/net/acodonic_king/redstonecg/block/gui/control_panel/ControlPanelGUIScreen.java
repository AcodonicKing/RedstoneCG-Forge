package net.acodonic_king.redstonecg.block.gui.control_panel;

import com.mojang.blaze3d.systems.RenderSystem;
import net.acodonic_king.redstonecg.block.normal.interaction.ControlPanelBlock;
import net.acodonic_king.redstonecg.block.entity.ControlPanelBlockEntity;
import net.acodonic_king.redstonecg.block.gui.pinmark_configurator.PinmarkConfiguratorWidget;
import net.acodonic_king.redstonecg.default_gui_classes.AbstractContainerScreenRide;
import net.acodonic_king.redstonecg.default_gui_classes.ScreenTools;
import net.acodonic_king.redstonecg.procedures.AdventureProcedure;
import net.acodonic_king.redstonecg.procedures.RotatableQuad;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ControlPanelGUIScreen extends AbstractContainerScreenRide<ControlPanelGUIMenu> {
	public final static HashMap<String, Object> guistate = ControlPanelGUIMenu.guistate;
	public final Level world;
	public final BlockPos pos;
	public final Player entity;
	public static List<ScreenTools.ImageButton> renderableButtons = new ArrayList<>();
	public ControlPanelGUIScreen.Slot[] slots;

	public ControlPanelGUIScreen(ControlPanelGUIMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.pos = container.pos;
		this.entity = container.entity;
		this.imageWidth = 200;
		this.imageHeight = 174;
	}
	public static final ResourceLocation background_texture = ScreenTools.getImage("redstonecg","textures/screens/control_panel_gui/background.png");
	public static final ResourceLocation selector = ScreenTools.getImage("redstonecg","textures/screens/control_panel_gui/selector.png");
	public final PinmarkConfiguratorWidget pinConfigA = new PinmarkConfiguratorWidget();
	public final PinmarkConfiguratorWidget pinConfigB = new PinmarkConfiguratorWidget();

	@Override
	public void render(ScreenStack ms, int mouseX, int mouseY, float partialTicks) {
		render(ms, mouseX, mouseY, partialTicks,true,true);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
	    if(button == 0 && pos != null){
			for(ScreenTools.ImageButton ibutton: this.renderableButtons){
				ibutton.checkClick((int) mouseX, (int) mouseY);
			}
			if(AdventureProcedure.pinConfig(world,entity)) {
				int A = pinConfigA.onMouse(true);
				int B = pinConfigB.onMouse(true);
				if (A != -1 || B != -1) {
					ScreenTools.playClickSound();
					if (world.getBlockEntity(pos) instanceof ControlPanelBlockEntity be) {
						be.loadPinConfigLogic(pinConfigA.LOGIC, pinConfigB.LOGIC);
						ControlPanelGUIButtonMessage msg = new ControlPanelGUIButtonMessage(0, pos);
						msg.tag.putByte("connection", be.CONNECTION);
						ControlPanelGUIButtonMessage.sendAndHandle(entity, msg);
					}
				}
				for (int i = 0; i < slots.length; i++) {
					int x = ControlPanelGUIMenu.slot_pos[i][0];
					int y = ControlPanelGUIMenu.slot_pos[i][1];
					float angle = slots[i].mouseClick(x + this.leftPos, y + this.topPos, mouseX, mouseY);
					if (angle >= 0) {
						ScreenTools.playClickSound();
						ControlPanelGUIButtonMessage msg = new ControlPanelGUIButtonMessage(i + 10, pos);
						msg.tag.putFloat("slot_angle", angle);
						ControlPanelGUIButtonMessage.sendAndHandle(entity, msg);
					}
				}
			}
	    }
	    return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button){
		pinConfigA.onMouse(false);
		pinConfigB.onMouse(false);
		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public void renderBg(ScreenStack ms, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		ScreenTools.blitTexture(this, ms, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, background_texture);

		if(pos != null) {
			boolean pinConfig = AdventureProcedure.pinConfig(world, entity);
			if (pinConfig)
				if (world.getBlockEntity(pos) instanceof ControlPanelBlockEntity be) {
					be.setPinConfigLogicA(pinConfigA.LOGIC);
					be.setPinConfigLogicB(pinConfigB.LOGIC);
					pinConfigA.render(this, ms, this.leftPos + 19, this.topPos + 55, mouseX, mouseY);
					pinConfigB.render(this, ms, this.leftPos + 149, this.topPos + 55, mouseX, mouseY);
					for (int i = 0; i < slots.length; i++) {
						int x = ControlPanelGUIMenu.slot_pos[i][0];
						int y = ControlPanelGUIMenu.slot_pos[i][1];
						slots[i].render(this, ms, x + this.leftPos, y + this.topPos, be.SLOT_ANGLES[i] + (float) Math.PI);
					}
				}

			for (ScreenTools.ImageButton button : renderableButtons) {
				button.render(ms, mouseX, mouseY);
			}

			if(pinConfig) {
				BlockState blockState = world.getBlockState(pos);
				int model = ((ControlPanelBlock) blockState.getBlock()).getModel(blockState);
				ScreenTools.ImageButton button = renderableButtons.get(model);
				ScreenTools.blitTexture(
						this, ms,
						button.OnScreenLeft, button.OnScreenTop,
						24, 24,
						selector
				);
			}

		}
		RenderSystem.disableBlend();
	}

	@Override
	public boolean keyPressed(int key, int b, int c) {
		if (key == 256) {
			this.minecraft.player.closeContainer();
			return true;
		}
		return super.keyPressed(key, b, c);
	}

	@Override
	public void containerTick() {
		super.containerTick();
	}

	/*@Override
	public void renderLabels(ScreenStack ms, int mouseX, int mouseY) {
	}*/

	public static class buttonImage{
		public int x, y;
		ScreenTools.ImageButton button;
		public buttonImage(ControlPanelGUIScreen screen, int id, int x, int y, String name){
			this.x = screen.leftPos + x;
			this.y = screen.topPos + y;
			this.button = new ScreenTools.ImageButton(this.x, this.y, 24, 24, "textures/screens/control_panel_gui/atlas/"+name+".png", 24, 48){
				@Override
				public void onClick(){
					ControlPanelGUIButtonMessage.sendAndHandle(screen.entity, id, screen.pos);
					screen.pinConfigA.LOGIC.reset();
					screen.pinConfigB.LOGIC.reset();
				}
			};
			guistate.put("button:"+name, this);
			renderableButtons.add(this.button);
		}
	}

	@Override
	public void init() {
		super.init();
		renderableButtons = new ArrayList<>();
		if(world.getBlockEntity(pos) instanceof ControlPanelBlockEntity be) {
			slots = new ControlPanelGUIScreen.Slot[be.SLOT_ANGLES.length];
			for(int i = 0; i < be.SLOT_ANGLES.length; i++){
				slots[i] = new ControlPanelGUIScreen.Slot();
			}
		}
		if(AdventureProcedure.pinConfig(world, entity)) {
			new buttonImage(this, 1, 62, 63, "o0");
			new buttonImage(this, 2, 88, 63, "o1");
			new buttonImage(this, 3, 114, 63, "o2");
		}
	}

	public static class Slot {
		public static final ResourceLocation DIAL = ScreenTools.getImage("redstonecg","textures/screens/control_panel_gui/dial.png");
		public static final ResourceLocation KNOB = ScreenTools.getImage("redstonecg","textures/screens/control_panel_gui/knob.png");
		public static final ResourceLocation SLOT = ScreenTools.getImage("redstonecg","textures/screens/control_panel_gui/slot.png");
		public final RotatableQuad QUAD = new RotatableQuad().cx(24).cy(24).w(48).h(48);
		public void render(Screen screen, ScreenStack ms, int x, int y, float angle){
			x -= 16;
			y -= 16;
			ScreenTools.blitTexture(screen, ms, x, y, 0, 48, 48, DIAL);
			QUAD.px(x + 24).py(y + 24).radians(angle).transform();
			ScreenTools.setTexture(KNOB);
			ScreenTools.blitSetTextureRectaroid(ms, QUAD.RECTOID, 1);
			ScreenTools.blitTexture(screen, ms, x, y, 2, 48, 48, SLOT);
		}
		public float mouseClick(int px, int py, double mx, double my){
			px += 8;
			py += 8;
			mx -= px;
			my -= py;
			//RedstonecgMod.LOGGER.debug(mx+" "+my);
			if((mx > -9 && mx < 9) && (my > -9 && my < 9))
				return -1;
			double ra = mx * mx + my * my;
			double angle = Math.atan2(my, mx);
			angle /= 2 * Math.PI;
			angle += 1.25;
			if(ra < 22 * 22){
				int size = 360;
				boolean inner = ra < 18 * 18;
				if(inner)
					size = 24;
				angle += 0.5 / ((double) size);
				angle %= 1;
				int sec = (int) (angle * size);
				if(inner)
					sec *= 15;
				angle = Math.toRadians(sec);
				//QUAD.ANGLE = (float) angle;
				return (float) angle;
			}
			return -1;
		}
	}
}
