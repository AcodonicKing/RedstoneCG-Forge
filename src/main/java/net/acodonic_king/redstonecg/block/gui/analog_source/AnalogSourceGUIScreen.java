package net.acodonic_king.redstonecg.block.gui.analog_source;

import net.acodonic_king.redstonecg.block.entity.AnalogSourceBlockEntity;
import net.acodonic_king.redstonecg.block.normal.analog.AnalogSourceBlock;
import net.acodonic_king.redstonecg.default_gui_classes.AbstractContainerScreenRide;
import net.acodonic_king.redstonecg.default_gui_classes.ScreenTools;
import net.acodonic_king.redstonecg.default_gui_classes.TypingBox;
import net.acodonic_king.redstonecg.init.RedstonecgModVersionRides;
import net.acodonic_king.redstonecg.procedures.AdventureProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.components.Button;

import java.util.HashMap;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.world.level.block.state.BlockState;
import org.lwjgl.glfw.GLFW;

public class AnalogSourceGUIScreen extends AbstractContainerScreenRide<AnalogSourceGUIMenu> {
	private final static HashMap<String, Object> guistate = AnalogSourceGUIMenu.guistate;
	private final Level world;
	private final BlockPos pos;
	private final Player entity;
	Button button_add;
	Button button_sub;
	Button button_change;
	TypingBox range_box_start;
	TypingBox range_box_end;

	public AnalogSourceGUIScreen(AnalogSourceGUIMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.pos = container.pos;
		this.entity = container.entity;
		this.imageWidth = 179;
		this.imageHeight = 128;
	}
	private static final ResourceLocation background_texture = ScreenTools.getImage("redstonecg","textures/screens/analog_source_gui/background.png");
	//private static final ResourceLocation block_textures = ScreenTools.getImage("redstonecg","textures/block/analog_source.png");
	private static final ResourceLocation crank = ScreenTools.getImage("redstonecg","textures/screens/analog_source_gui/crank.png");
	private static final ResourceLocation pinmark_f = ScreenTools.getImage("redstonecg","textures/block/pins/f.png");
	private static final ResourceLocation pinmark_r = ScreenTools.getImage("redstonecg","textures/block/pins/r.png");
	private static final ResourceLocation pinmark_b = ScreenTools.getImage("redstonecg","textures/block/pins/b.png");
	private static final ResourceLocation pinmark_l = ScreenTools.getImage("redstonecg","textures/block/pins/l.png");

	private static float[][] crank_rectoid = new float[][]{
			{ 0f,  0f, 0f, 1f},
			{57f,  0f, 0f, 0f},
			{57f, 21f, 1f, 0f},
			{ 0f, 21f, 1f, 1f}
	};

	private float rotateX(float x, float y, double ang_cos, double ang_sin){
		return (float) (x * ang_cos - y * ang_sin);
	}
	private float rotateY(float x, float y, double ang_cos, double ang_sin){
		return (float) (x * ang_sin + y * ang_cos);
	}
	private void setCrankRectoid(int x, int y, float angle){
		final float corn = 32f;
		double ang_cos = Math.cos(-angle);
		double ang_sin = Math.sin(-angle);
		crank_rectoid[0][0] = rotateX(-corn, -corn, ang_cos, ang_sin);
		crank_rectoid[0][1] = rotateY(-corn, -corn, ang_cos, ang_sin);

		crank_rectoid[3][0] = rotateX(corn, -corn, ang_cos, ang_sin);
		crank_rectoid[3][1] = rotateY(corn, -corn, ang_cos, ang_sin);

		crank_rectoid[2][0] = rotateX(corn, corn, ang_cos, ang_sin);
		crank_rectoid[2][1] = rotateY(corn, corn, ang_cos, ang_sin);

		crank_rectoid[1][0] = rotateX(-corn, corn, ang_cos, ang_sin);
		crank_rectoid[1][1] = rotateY(-corn, corn, ang_cos, ang_sin);

		float add_x = this.leftPos + x;
		float add_y = this.topPos + y;
		for(int i = 0; i < crank_rectoid.length; i++){
			crank_rectoid[i][0] += add_x;
			crank_rectoid[i][1] += add_y;
		}
	}

	@Override
	public void render(ScreenStack ms, int mouseX, int mouseY, float partialTicks) {
		render(ms, mouseX, mouseY, partialTicks,true,true);
	}

	@Override
	public void renderBg(ScreenStack ms, float partialTicks, int gx, int gy) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		ScreenTools.blitTexture(this, ms, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, background_texture);
		//ScreenTools.blitTexture(this, ms, this.leftPos + 7, this.topPos + 7, 32, 32, block_textures);
		int connection = this.world.getBlockState(this.pos).getValue(AnalogSourceBlock.CONNECTION);
		int sz = 64;
		int lp = this.leftPos + 65 - (sz / 2);
		int tp = this.topPos + ((this.imageHeight - sz) / 2);
		connection++;
		if ((connection & 1) > 0) {
			ScreenTools.blitTexture(this, ms, lp, tp-24, sz, sz, pinmark_f);
		}
		if ((connection & 2) > 0) {
			ScreenTools.blitTexture(this, ms, lp+24, tp, sz, sz, pinmark_r);
		}
		if ((connection & 4) > 0) {
			ScreenTools.blitTexture(this, ms, lp, tp+24, sz, sz, pinmark_b);
		}
		if ((connection & 8) > 0) {
			ScreenTools.blitTexture(this, ms, lp-24, tp, sz, sz, pinmark_l);
		}
		if(this.world.getBlockEntity(this.pos) instanceof AnalogSourceBlockEntity be){
			ScreenTools.setTexture(crank);
			//lp = this.leftPos + 65 - (64 / 2);
			//tp = this.topPos + ((this.imageHeight - 64) / 2);
			//int power = be.POWER;
			//ScreenTools.blitSetTextureRegion(ms,lp,tp,64,64,power * 64,0,1024,64);
			setCrankRectoid(65, 64, be.ANGLE);
			ScreenTools.blitSetTextureRectaroid(ms, crank_rectoid, 1);
		}

		RenderSystem.disableBlend();
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if(range_box_start.mouseClicked(mouseX, mouseY, button) || range_box_end.mouseClicked(mouseX, mouseY, button)){
			return super.mouseClicked(mouseX, mouseY, button);
		} else if (range_box_start.isFocused() || range_box_end.isFocused()){
			AnalogSourceGUIButtonMessage msg = new AnalogSourceGUIButtonMessage(0, pos);
			msg.tag.putString("range_start", range_box_start.getValue());
			msg.tag.putString("range_end", range_box_end.getValue());
			AnalogSourceGUIButtonMessage.sendAndHandle(entity, msg);
			range_box_start.setFocused(false);
			range_box_end.setFocused(false);
			return true;
		}
		if(button == 0) {
			if(AdventureProcedure.valueConfig(world, entity)) {
				int lp = this.leftPos + 65;
				int tp = this.topPos + (this.imageHeight / 2);
				lp = (int) (mouseX - lp);
				tp = (int) (mouseY - tp);
				int ra = lp * lp + tp * tp;
				if (ra <= 47 * 47) {
					ScreenTools.playClickSound();
					double angle = Math.atan2(tp, lp);
					if (angle > 0)
						angle += Math.PI / 16;
					else if (angle < 0)
						angle -= Math.PI / 16;
					int sec = (int) (angle / (Math.PI / 8));
					sec += 20;
					sec &= 15;
					sec += 8;
					AnalogSourceGUIButtonMessage.sendAndHandle(entity, sec, this.pos);
				}
			}
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean keyPressed(int key, int b, int c) {
		if (key == 256) {
			this.minecraft.player.closeContainer();
			return true;
		}
		if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER) {
			if (range_box_start.isFocused() || range_box_end.isFocused()){
				AnalogSourceGUIButtonMessage msg = new AnalogSourceGUIButtonMessage(3, pos);
				msg.tag.putString("range_start", range_box_start.getValue());
				msg.tag.putString("range_end", range_box_end.getValue());
				AnalogSourceGUIButtonMessage.sendAndHandle(entity, msg);
				range_box_start.setFocused(false);
				range_box_end.setFocused(false);
				return true;
			}
		}
		return super.keyPressed(key, b, c);
	}

	@Override
	public void containerTick() {
		super.containerTick();
		range_box_start.tick();
		range_box_end.tick();
	}

	@Override
	public void renderLabels(ScreenStack ms, int mouseX, int mouseY) {
		//int value = LittleTools.getIntegerProperty(this.world.getBlockState(pos),"power");
		BlockState ThisBlock = this.world.getBlockState(pos);
		if(ThisBlock.getBlock() instanceof AnalogSourceBlock b) {
			int value = b.getPower(this.world, pos);
			int lp = this.imageWidth - 20;
			ScreenTools.drawString(this.font, ms, new java.text.DecimalFormat("##").format(value), lp, 30, 0xF0F0F0);
		}
		if(world.getBlockEntity(pos) instanceof AnalogSourceBlockEntity be) {
			int lp = 64 - 2;
			int tp = 64 - 4;
			float angle = 0;
			float da = (float) ((2 * Math.PI) / (be.POWER_RANGE[1] - be.POWER_RANGE[0] + 1));
			for(int r = be.POWER_RANGE[0]; r <= be.POWER_RANGE[1]; r++){
				int x = (int) (lp + Math.sin(angle) * 40);
				int y = (int) (tp - Math.cos(angle) * 40);
				if(r > 9)
					x -= 3;
				ScreenTools.drawString(this.font, ms, String.valueOf(r), x, y, 0xF0F0F0);
				angle += da;
			}
		}
	}

	@Override
	public void init() {
		super.init();
		int lp = this.leftPos + 152;
		int[] range = new int[]{0,15};
		if(world.getBlockEntity(pos) instanceof AnalogSourceBlockEntity be)
			range = be.POWER_RANGE;

		range_box_start = new TypingBox(this.font, lp, this.topPos + 46, 20, 18, Component.empty());
		range_box_start.setValue(String.valueOf(range[0]));
		range_box_start.setMaxLength(2);
		guistate.put("box:range_box_start", range_box_start);
		range_box_end = new TypingBox(this.font, lp, this.topPos + 6, 20, 18, Component.empty());
		range_box_end.setValue(String.valueOf(range[1]));
		range_box_end.setMaxLength(2);
		guistate.put("box:range_box_end", range_box_end);

		lp = this.leftPos + 152 - 25;
		if(AdventureProcedure.valueConfig(world, entity)) {
			button_sub = RedstonecgModVersionRides.createButton(lp, this.topPos + 45, 20, 20, "gui.redstonecg.analog_source_gui.button_sub", e -> {
				AnalogSourceGUIButtonMessage.sendAndHandle(entity, 0, this.pos);
			});
			guistate.put("button:button_sub", button_sub);
			this.addRenderableWidget(button_sub);
			button_add = RedstonecgModVersionRides.createButton(lp, this.topPos + 5, 20, 20, "gui.redstonecg.analog_source_gui.button_add", e -> {
				AnalogSourceGUIButtonMessage.sendAndHandle(entity, 1, this.pos);
			});
			guistate.put("button:button_add", button_add);
			this.addRenderableWidget(button_add);
			this.addRenderableWidget(range_box_start);
			this.addRenderableWidget(range_box_end);
		}

		if(AdventureProcedure.pinConfig(world, entity)){
			lp -= 11;
			int tp = this.topPos + this.imageHeight - 25;
			button_change = RedstonecgModVersionRides.createButton(lp, tp, 56, 20, "gui.redstonecg.analog_source_gui.button_change", e -> {
				AnalogSourceGUIButtonMessage.sendAndHandle(entity, 2, this.pos);
			});
			guistate.put("button:button_change", button_change);
			this.addRenderableWidget(button_change);
		}
	}
}
