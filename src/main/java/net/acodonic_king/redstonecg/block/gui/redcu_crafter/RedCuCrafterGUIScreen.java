package net.acodonic_king.redstonecg.block.gui.redcu_crafter;

import net.acodonic_king.redstonecg.ModLoaderRider;
import net.acodonic_king.redstonecg.default_gui_classes.AbstractContainerScreenRide;
import net.acodonic_king.redstonecg.default_gui_classes.ScreenTools;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;

import java.util.HashMap;
import com.mojang.blaze3d.systems.RenderSystem;
import net.acodonic_king.redstonecg.recipe.RedCuCrafterRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import java.util.ArrayList;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.core.NonNullList;

public class RedCuCrafterGUIScreen extends AbstractContainerScreenRide<RedCuCrafterGUIMenu> {
	private final static HashMap<String, Object> guistate = RedCuCrafterGUIMenu.guistate;
	private final Level world;
	private final BlockPos pos;
	private final Player entity;
	private static List<ScreenTools.ImageButton> renderableButtons = new ArrayList<>();

	public RedCuCrafterGUIScreen(RedCuCrafterGUIMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.pos = container.pos;
		this.entity = container.entity;
		this.imageWidth = 222;
		this.imageHeight = 166;
	}
	private static final ResourceLocation texture = ScreenTools.getImage("redstonecg","textures/screens/red_cu_crafter_gui.png");
	private static final ResourceLocation selected = ScreenTools.getImage("redstonecg","textures/screens/redcucraftertabs/selected.png");
	private int mouse_hovering_over_block = 0;

	@Override
	public void render(ScreenStack ms, int mouseX, int mouseY, float partialTicks) {
		render(ms, mouseX, mouseY, partialTicks,true,true);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
	    if(button == 0){
	    	int offsetX = this.leftPos + 59;
			int offsetY = this.topPos + 7;
			int limitX = this.leftPos + 164;
			int limitY = this.topPos + 76;
	    	if(offsetX <= mouseX && mouseX <= limitX && offsetY <= mouseY && mouseY <= limitY){
		    	guistate.put("variable:block_selected",mouse_hovering_over_block);
				String strcategory = (String) guistate.getOrDefault("variable:block_category_string", "wires");
				strcategory += (String) guistate.getOrDefault("variable:junction_type_string", "/normal");
				Object objcategory = guistate.get("category:"+strcategory);
				if (objcategory != null){
					List<item_option> category = (List<item_option>) objcategory;
					if (mouse_hovering_over_block >= category.size()){
						if (mouse_hovering_over_block != 0){
							mouse_hovering_over_block = 0;
							guistate.put("variable:block_selected",0);
						} else {
							guistate.put("category:ingredients",null);
							return super.mouseClicked(mouseX, mouseY, button);
						}
					}
					if(mouse_hovering_over_block < category.size()) {
						guistate.put("category:ingredients", new ingredients_option(category.get(mouse_hovering_over_block).recipe));
					}
				}
	    	}
			for(ScreenTools.ImageButton ibutton: this.renderableButtons){
				ibutton.checkClick((int) mouseX, (int) mouseY);
			}
	    }
	    return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public void renderBg(ScreenStack ms, float partialTicks, int gx, int gy) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		ScreenTools.blitTexture(this, ms, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, texture);

		String strcategory = (String) guistate.getOrDefault("variable:block_category_string", "wires");
		int bcsy = ((buttonImage) guistate.get("button:"+strcategory)).y - 1;
		ScreenTools.blitTexture(this,ms, this.leftPos + 7, bcsy, 18, 18, selected);

		String junccategory = (String) guistate.getOrDefault("variable:junction_type_string", "/normal");
		bcsy = ((buttonImageJunction) guistate.get("button:"+junccategory)).y - 1;
		ScreenTools.blitTexture(this,ms, this.leftPos + this.imageWidth - 25, bcsy, 18, 18, selected);
        strcategory += junccategory;

		Object objcategory = guistate.get("category:"+strcategory);
		if(objcategory != null){
			List<item_option> category = (List<item_option>) objcategory;
			int offsetX = this.leftPos + 59;
			int offsetY = this.topPos + 7;
			int limitX = this.leftPos + 164;
			int limitY = this.topPos + 76;
			int block_selected = (int) guistate.getOrDefault("variable:block_selected", 0);
			if(block_selected >= category.size() && block_selected != 0){guistate.put("variable:block_selected",0);}
			int mX = offsetX + (block_selected % 6) * 18 - 1;
			int mY = offsetY + (block_selected / 6) * 18 - 1;
			ScreenTools.blitTexture(this, ms, mX, mY, 18, 18, selected);
			if(offsetX <= gx && gx <= limitX && offsetY <= gy && gy <= limitY){
				mX = (gx - offsetX - 1)/18;
				mY = (gy - offsetY - 1)/18;
				mouse_hovering_over_block = mY * 6 + mX;
				mX = offsetX + mX * 18 - 1;
				mY = offsetY + mY * 18 - 1;
				ScreenTools.blitTexture(this, ms, mX, mY, 18, 18, selected);
			}
			int posX = offsetX;
			int posY = offsetY;
			for(item_option option: category){
				this.renderItemStack(ms, option.itemstack, posX, posY);
				//RedstonecgModVersionRides.renderItem(this, ms.stack, option.itemstack, posX, posY);
				posX += 18;
				if(posX > limitX){
					posX = offsetX;
					posY += 18;
				}
			}
			Object ingredientsobj = guistate.get("category:ingredients");
			if(ingredientsobj != null){
				ingredients_option ingredients = (ingredients_option) ingredientsobj;
				posX = this.leftPos + 31;
				posY = this.topPos + 12;
				for(ItemStack rl: ingredients.itemStacks){
					this.renderItemStack(ms, rl, posX, posY);
					//RedstonecgModVersionRides.renderItem(this, ms.stack, rl, posX, posY);
					ScreenTools.renderRectangle(ms, 0x80808080, posX, posY, 200, 16, 16);
					posY += 18;
				}
			}
		}


		for(ScreenTools.ImageButton button: renderableButtons){
			button.render(ms,gx,gy);
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
		public buttonImage(RedCuCrafterGUIScreen screen, int id, int x, int y, String name){
			this.x = screen.leftPos + x;
			this.y = screen.topPos + y;
			/*ImageButton new_button = new ImageButton(this.x, this.y, 16, 16, 0, 0, 16, ScreenTools.getImage("redstonecg","textures/screens/redcucraftertabs/atlas/"+name+".png"), 16, 32, e -> {
				RedCuCrafterGUIButtonMessage.sendAndHandle(screen.entity, id, screen.pos);
			});*/
			this.button = new ScreenTools.ImageButton(this.x, this.y, 16, 16, "textures/screens/redcucraftertabs/atlas/"+name+".png", 16, 32){
				@Override
				public void onClick(){
					RedCuCrafterGUIButtonMessage.sendAndHandle(screen.entity, id, screen.pos);
				}
			};
			guistate.put("button:"+name, this);
			renderableButtons.add(this.button);
			//screen.addRenderableWidget(new_button);
		}
	}

	public static class buttonImageJunction{
		public int x, y;
		ScreenTools.ImageButton button;
		public buttonImageJunction(RedCuCrafterGUIScreen screen, int id, int x, int y, String name){
			this.x = screen.leftPos + screen.imageWidth - x;
			this.y = screen.topPos + y;
			/*ImageButton new_button = new ImageButton(this.x, this.y, 16, 16, 0, 0, 16, ScreenTools.getImage("redstonecg","textures/screens/redcucraftertabs/atlas/"+name+".png"), 16, 32, e -> {
				RedCuCrafterGUIButtonMessage.sendAndHandle(screen.entity, id, screen.pos);
			}){
				//@Override
				//public void render(PoseStack ms, int gx, int gy, float ticks) {
				//	super.render(ms, gx, gy, ticks);
				//}
			};*/
			this.button = new ScreenTools.ImageButton(this.x, this.y, 16, 16, "textures/screens/redcucraftertabs/atlas"+name+".png", 16, 32){
				@Override
				public void onClick(){
					RedCuCrafterGUIButtonMessage.sendAndHandle(screen.entity, id, screen.pos);
				}
			};
			guistate.put("button:"+name, this);
			renderableButtons.add(this.button);
			//screen.addRenderableWidget(new_button);
		}
	}

	public static class item_option{
		public RedCuCrafterRecipe recipe;
		public ItemStack itemstack;
		public item_option(RedCuCrafterRecipe recipe, ItemStack itemstack){
			this.itemstack = itemstack;
			this.recipe = recipe;
		}
	}

	public static class ingredients_option{
		public RedCuCrafterRecipe recipe;
		public NonNullList<ItemStack> itemStacks = NonNullList.create();
		public ingredients_option(RedCuCrafterRecipe recipe){
			this.recipe = recipe;
			this.itemStacks = NonNullList.create();
			for(Ingredient ing: recipe.getIngredients()){
				if(ing.getItems().length != 0){
					this.itemStacks.add(ing.getItems()[0]);
				}
			}
		}
	}

	public static void add_item_options(List<item_option> option_list, RedCuCrafterRecipe recipe){
		for (ItemStack result : recipe.getResults()) {
			option_list.add(new item_option(recipe,result));
		}
	}

	@Override
	public void init() {
		super.init();
		renderableButtons = new ArrayList<>();
		new buttonImage(this, 0, 8, 12, "wires");
		new buttonImage(this,1, 8, 30, "digital");

		/*new buttonImage(this, 3, 8, 48, "analog");
		new buttonImage(this, 4, 8, 66, "indicators");*/

		new buttonImage(this, 2, 8, 48, "hybrid");
		new buttonImage(this, 3, 8, 66, "analog");
		new buttonImage(this, 4, 8, 84, "indicators");
		new buttonImageJunction(this, 5, 24, 12, "/normal");
		new buttonImageJunction(this,6, 24, 30, "/parallel");

		guistate.put("variable:block_category", 0);
		guistate.put("variable:junction_type", 0);
		guistate.put("variable:block_selected", 0);

		RecipeManager recipeManager = world.getRecipeManager();
		List<RedCuCrafterRecipe> customRecipes = ModLoaderRider.getAllRecipes(recipeManager.getAllRecipesFor(RedCuCrafterRecipe.Type.INSTANCE));
		List<item_option> wires_normal = new ArrayList<>();
		List<item_option> wires_parallel = new ArrayList<>();
		List<item_option> digital_normal = new ArrayList<>();
		List<item_option> digital_parallel = new ArrayList<>();
		List<item_option> hybrid_normal = new ArrayList<>();
		List<item_option> hybrid_parallel = new ArrayList<>();
		List<item_option> analog_normal = new ArrayList<>();
		List<item_option> analog_parallel = new ArrayList<>();
		List<item_option> indicators_normal = new ArrayList<>();
		List<item_option> indicators_parallel = new ArrayList<>();
		for (RedCuCrafterRecipe recipe : customRecipes) {
            switch (recipe.getDesignation()) {
                case "wires/normal" -> add_item_options(wires_normal, recipe);
                case "wires/parallel" -> add_item_options(wires_parallel, recipe);
                case "digital/normal" -> add_item_options(digital_normal, recipe);
                case "digital/parallel" -> add_item_options(digital_parallel, recipe);
				case "hybrid/normal" -> add_item_options(hybrid_normal, recipe);
				case "hybrid/parallel" -> add_item_options(hybrid_parallel, recipe);
                case "analog/normal" -> add_item_options(analog_normal, recipe);
                case "analog/parallel" -> add_item_options(analog_parallel, recipe);
                case "indicators/normal" -> add_item_options(indicators_normal, recipe);
                case "indicators/parallel" -> add_item_options(indicators_parallel, recipe);
            }
		}
		guistate.put("category:wires/normal", wires_normal);
		guistate.put("category:wires/parallel", wires_parallel);
		guistate.put("category:digital/normal", digital_normal);
		guistate.put("category:digital/parallel", digital_parallel);
		guistate.put("category:hybrid/normal", hybrid_normal);
		guistate.put("category:hybrid/parallel", hybrid_parallel);
		guistate.put("category:analog/normal", analog_normal);
		guistate.put("category:analog/parallel", analog_parallel);
		guistate.put("category:indicators/normal", indicators_normal);
		guistate.put("category:indicators/parallel", indicators_parallel);
	}
}
