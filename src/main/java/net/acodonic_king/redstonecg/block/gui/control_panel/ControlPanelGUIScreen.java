package net.acodonic_king.redstonecg.block.gui.control_panel;

import com.mojang.blaze3d.systems.RenderSystem;
import net.acodonic_king.redstonecg.block.entity.ControlPanelBlockEntity;
import net.acodonic_king.redstonecg.block.gui.pinmark_configurator.PinmarkConfiguratorWidget;
import net.acodonic_king.redstonecg.default_gui_classes.AbstractContainerScreenRide;
import net.acodonic_king.redstonecg.default_gui_classes.ScreenTools;
import net.acodonic_king.redstonecg.network.MessengerBlockEntityPigeon;
import net.acodonic_king.redstonecg.procedures.AdventureProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ControlPanelGUIScreen extends AbstractContainerScreenRide<ControlPanelGUIMenu> {
	private final static HashMap<String, Object> guistate = ControlPanelGUIMenu.guistate;
	private final Level world;
	private final BlockPos pos;
	private final Player entity;
	private static List<ScreenTools.ImageButton> renderableButtons = new ArrayList<>();

	public ControlPanelGUIScreen(ControlPanelGUIMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.pos = container.pos;
		this.entity = container.entity;
		this.imageWidth = 176;
		this.imageHeight = 166;
	}
	private static final ResourceLocation background_texture = ScreenTools.getImage("redstonecg","textures/screens/control_panel_gui/background.png");
	public final PinmarkConfiguratorWidget pinConfigA = new PinmarkConfiguratorWidget();
	public final PinmarkConfiguratorWidget pinConfigB = new PinmarkConfiguratorWidget();

	@Override
	public void render(ScreenStack ms, int mouseX, int mouseY, float partialTicks) {
		render(ms, mouseX, mouseY, partialTicks,true,true);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
	    if(button == 0){
			for(ScreenTools.ImageButton ibutton: this.renderableButtons){
				ibutton.checkClick((int) mouseX, (int) mouseY);
			}
			int A = pinConfigA.onMouse(true);
			int B = pinConfigB.onMouse(true);
			if(A != -1 || B != -1) {
				ScreenTools.playClickSound();
				if(world.getBlockEntity(pos) instanceof ControlPanelBlockEntity be) {
					be.loadPinConfigLogic(pinConfigA.LOGIC, pinConfigB.LOGIC);
					ControlPanelGUIButtonMessage msg = new ControlPanelGUIButtonMessage(0, pos);
					msg.tag.putByte("connection", be.CONNECTION);
					ControlPanelGUIButtonMessage.sendAndHandle(entity, msg);
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

		if(AdventureProcedure.pinConfig(world,entity) && pos != null)
			if(world.getBlockEntity(pos) instanceof ControlPanelBlockEntity be) {
				be.setPinConfigLogicA(pinConfigA.LOGIC);
				be.setPinConfigLogicB(pinConfigB.LOGIC);
				pinConfigA.render(this, ms, this.leftPos + 6, this.topPos + 22, mouseX, mouseY);
				pinConfigB.render(this, ms, this.leftPos + 137, this.topPos + 22, mouseX, mouseY);
			}

		for(ScreenTools.ImageButton button: renderableButtons){
			button.render(ms, mouseX, mouseY);
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
			/*ImageButton new_button = new ImageButton(this.x, this.y, 16, 16, 0, 0, 16, ScreenTools.getImage("redstonecg","textures/screens/redcucraftertabs/atlas/"+name+".png"), 16, 32, e -> {
				RedCuCrafterGUIButtonMessage.sendAndHandle(screen.entity, id, screen.pos);
			});*/
			this.button = new ScreenTools.ImageButton(this.x, this.y, 16, 16, "textures/screens/redcucraftertabs/atlas/"+name+".png", 16, 32){
				@Override
				public void onClick(){
					ControlPanelGUIButtonMessage.sendAndHandle(screen.entity, id, screen.pos);
				}
			};
			guistate.put("button:"+name, this);
			renderableButtons.add(this.button);
			//screen.addRenderableWidget(new_button);
		}
	}

	@Override
	public void init() {
		super.init();
		renderableButtons = new ArrayList<>();
	}
}
