package net.acodonic_king.redstonecg.integration;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.acodonic_king.redstonecg.RedstonecgMod;
import net.acodonic_king.redstonecg.init.RedstonecgModBlocks;
import net.acodonic_king.redstonecg.recipe.RedCuCrafterRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.apache.commons.lang3.tuple.Pair;

public class RedCuCraftingCategory implements IRecipeCategory<RedCuCrafterRecipe> {
    public static final ResourceLocation ID = new ResourceLocation(RedstonecgMod.MODID, "redcu_crafting");
    private static final ResourceLocation BACKGROUND = new ResourceLocation(RedstonecgMod.MODID, "textures/screens/jei/redcu_crafting.png");
    private static final ResourceLocation SELECTOR = new ResourceLocation(RedstonecgMod.MODID, "textures/screens/jei/selector.png");

    private final IDrawable icon;
    private final IDrawable background;
    private final IDrawable selector;

    public RedCuCraftingCategory(IGuiHelper helper){
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(RedstonecgModBlocks.REDCU_CRAFTER.get()));
        this.background = helper.drawableBuilder(BACKGROUND, 0, 0, 158, 104).setTextureSize(158, 104).build();
        this.selector = helper.drawableBuilder(SELECTOR, 0, 0, 18, 18).setTextureSize(18, 18).build();
    }

    @Override
    public RecipeType<RedCuCrafterRecipe> getRecipeType() {
        return JEIPlugin.REDCU_CRAFTING;
    }

    @Override
    public Component getTitle() {
        return Component.literal("RedCu Crafter");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RedCuCrafterRecipe redCuCrafterRecipe, IFocusGroup iFocusGroup) {
        NonNullList<Ingredient> ingredients = redCuCrafterRecipe.getIngredients();
        int x = 31;
        int y = 26;
        for(Ingredient ingredient: ingredients){
            builder.addSlot(RecipeIngredientRole.INPUT, x, y).addIngredients(ingredient);
            y += 18;
        }
        NonNullList<ItemStack> results = redCuCrafterRecipe.getResults();
        int i = 0;
        int l = results.size();
        for(y = 8; y < 95; y += 18){
            for(x = 55; x < 126; x += 18){
                if(i >= l){break;}
                builder.addSlot(RecipeIngredientRole.OUTPUT, x, y).addItemStack(results.get(i));
                i++;
            }
        }
        String[] parts = redCuCrafterRecipe.getDesignation().split("/", 2);
        Pair<Integer, Integer> category_pos = switch (parts[0]){
            case "wires" -> Pair.of(8,8);
            case "digital" -> Pair.of(8, 26);
            case "hybrid" -> Pair.of(8, 44);
            case "analog" -> Pair.of(8, 62);
            case "indicators" -> Pair.of(8, 80);
            default -> Pair.of(26,8);
        };
        Pair<Integer, Integer> junction_pos = switch (parts[1]){
            case "normal" -> Pair.of(134,8);
            case "parallel" -> Pair.of(134,26);
            default -> Pair.of(134,44);
        };
        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, category_pos.getLeft(), category_pos.getRight()).setBackground(this.selector, -1, -1);
        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, junction_pos.getLeft(), junction_pos.getRight()).setBackground(this.selector, -1, -1);
    }
}
