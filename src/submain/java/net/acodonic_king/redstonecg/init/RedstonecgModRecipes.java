package net.acodonic_king.redstonecg.init;

import net.acodonic_king.redstonecg.RedstonecgMod;
import net.acodonic_king.redstonecg.recipe.RedCuCrafterRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RedstonecgModRecipes {
    public static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(RedstonecgModVersionRides.RECIPE_TYPE, RedstonecgMod.MODID);
    public static final RegistryObject<RecipeType<RedCuCrafterRecipe>> REDCU_CRAFTING = TYPES.register("redcu_crafting", RedCuCrafterRecipe.Type::new);

    public static final DeferredRegister<RecipeSerializer<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, RedstonecgMod.MODID);
    public static final RegistryObject<RecipeSerializer<RedCuCrafterRecipe>> REDCU_CRAFTER_SERIALIZER = REGISTRY.register("redcu_crafting", () -> RedCuCrafterRecipe.Serializer.INSTANCE);
}