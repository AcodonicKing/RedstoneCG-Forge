package net.acodonic_king.redstonecg.integration;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.recipe.RecipeType;
import net.acodonic_king.redstonecg.ModLoaderRider;
import net.acodonic_king.redstonecg.RedstonecgMod;
import net.acodonic_king.redstonecg.recipe.RedCuCrafterRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;
import java.util.Objects;

@JeiPlugin
public class JEIPlugin implements IModPlugin {
    public static RecipeType<RedCuCrafterRecipe> REDCU_CRAFTING = new RecipeType<>(RedCuCraftingCategory.ID,RedCuCrafterRecipe.class);

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(RedstonecgMod.MODID, "jei_plugin");
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration){
        RecipeManager recipeManager = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();
        List<RedCuCrafterRecipe> customRecipes = ModLoaderRider.getAllRecipes(recipeManager.getAllRecipesFor(RedCuCrafterRecipe.Type.INSTANCE));
        registration.addRecipes(REDCU_CRAFTING, customRecipes);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration){
        registration.addRecipeCategories(new RedCuCraftingCategory(registration.getJeiHelpers().getGuiHelper()));
    }
}
