package net.acodonic_king.redstonecg.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import net.acodonic_king.redstonecg.RedstonecgMod;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class RedCuCrafterRecipe implements Recipe<CraftingContainer> {
    private final ResourceLocation id;
    private final NonNullList<ItemStack> results;
    private final NonNullList<Ingredient> ingredients;
    private final String designation;
    private int counter;

    public RedCuCrafterRecipe(ResourceLocation id, NonNullList<ItemStack> results, NonNullList<Ingredient> ingredients, String designation) {
        this.id = id;
        this.results = results;
        this.ingredients = ingredients;
        this.designation = designation;
        this.counter = -1;
    }

    @Override
    public boolean matches(CraftingContainer container, Level pLevel) {
        if (container.getContainerSize() < 3) {
            return false;
        } else {
            boolean output = true;
            //NonNullList<Slot> cont = container.slots;

            for (int i = 0; i < this.ingredients.size(); ++i) {
                Ingredient ingredient = this.ingredients.get(i);
                if (ingredient.getItems().length != 0) {
                    ItemStack required = ingredient.getItems()[0];
                    ItemStack inContainer = container.getItem(i);
                    output = output && (required.getItem() == inContainer.getItem());
                }
            }

            return output;
        }
    }

    public boolean matches(AbstractContainerMenu container, Level level) {
        if (container.slots.size() < 3) {
            return false;
        } else {
            boolean output = true;
            NonNullList<Slot> cont = container.slots;

            for (int i = 0; i < this.ingredients.size(); ++i) {
                Ingredient ingredient = this.ingredients.get(i);
                if (ingredient.getItems().length != 0) {
                    ItemStack required = ingredient.getItems()[0];
                    ItemStack inContainer = cont.get(i).getItem();
                    output = output && (required.getItem() == inContainer.getItem());
                }
            }

            return output;
        }
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    public void SetCounter(int c) {
        this.counter = c;
    }

    public ItemStack assemble(CraftingContainer container) {
        NonNullList<ItemStack> outputs = NonNullList.create();
        for (ItemStack stack : this.results) {
            outputs.add(stack.copy());
        }
    
        ++this.counter;
        if (this.counter >= this.results.size()) {
            this.counter = -1;
        }
    
        return outputs.get(this.counter);
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess reg) {
        return assemble(container);
    }

    public ItemStack getResultItem() {
        ++this.counter;
        if (this.counter >= this.results.size()) {
            this.counter = 0;
        }

        return (ItemStack)this.results.get(this.counter);
    }

    @Override
    public ItemStack getResultItem(RegistryAccess reg) {
        return getResultItem();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= this.ingredients.size();
    }

    public NonNullList<ItemStack> getResults() {
        return results;
    }

    public NonNullList<ItemStack> getOutputs() {
        return results;
    }

    public String getDesignation(){
        return designation;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<RedCuCrafterRecipe> {
        private Type() { }
        public static final Type INSTANCE = new Type();
        public static final String ID = "redcu_crafting";
    }


    public static class Serializer implements RecipeSerializer<RedCuCrafterRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(RedstonecgMod.MODID, "redcu_crafting");

        @Override
        public RedCuCrafterRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            // Parse designation
            String designation = GsonHelper.getAsString(json, "designation", "");

            // Parse ingredients
            JsonArray ingredientsJson = GsonHelper.getAsJsonArray(json, "ingredients");
            NonNullList<Ingredient> ingredients = NonNullList.create();
            for (JsonElement element : ingredientsJson) {
                ingredients.add(Ingredient.fromJson(element));
            }

            // Parse outputs
            JsonArray outputsJson = GsonHelper.getAsJsonArray(json, "outputs");
            NonNullList<ItemStack> outputs = NonNullList.create();
            for (JsonElement element : outputsJson) {
                JsonObject outputObj = element.getAsJsonObject();
                ItemStack stack = new ItemStack(
                    GsonHelper.getAsItem(outputObj, "id"),
                    GsonHelper.getAsInt(outputObj, "Count", 1)
                );
                outputs.add(stack);
            }

            return new RedCuCrafterRecipe(recipeId, outputs, ingredients, designation);
        }

        @Override
        public RedCuCrafterRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            String designation = buffer.readUtf();

            int ingredientCount = buffer.readVarInt();
            NonNullList<Ingredient> ingredients = NonNullList.withSize(ingredientCount, Ingredient.EMPTY);
            for (int i = 0; i < ingredientCount; i++) {
                ingredients.set(i, Ingredient.fromNetwork(buffer));
            }

            int outputCount = buffer.readVarInt();
            NonNullList<ItemStack> outputs = NonNullList.withSize(outputCount, ItemStack.EMPTY);
            for (int i = 0; i < outputCount; i++) {
                outputs.set(i, buffer.readItem());
            }

            return new RedCuCrafterRecipe(recipeId, outputs, ingredients, designation);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, RedCuCrafterRecipe recipe) {
            buffer.writeUtf(recipe.getDesignation());

            buffer.writeVarInt(recipe.getIngredients().size());
            for (Ingredient ingredient : recipe.getIngredients()) {
                ingredient.toNetwork(buffer);
            }

            buffer.writeVarInt(recipe.getOutputs().size());
            for (ItemStack output : recipe.getOutputs()) {
                buffer.writeItem(output);
            }
        }
    }
}