package net.acodonic_king.redstonecg;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class ModLoaderRider {
    public static <T extends Recipe<?>> List<T> getAllRecipes(List<T> rc) {
        return rc;
    }
    public static void openMenu(ServerPlayer player, MenuProvider provider, BlockPos pos){
        NetworkHooks.openScreen(player, provider, pos);
    }
    public static void itemStackPutBoolean(ItemStack itemStack, String key, boolean state){
        itemStack.getOrCreateTag().putBoolean(key, state);
    }
    public static void itemStackPutString(ItemStack itemStack, String key, String state){
        itemStack.getOrCreateTag().putString(key, state);
    }
    public static boolean itemStackGetBoolean(ItemStack itemStack, String key){
        return itemStack.getOrCreateTag().getBoolean(key);
    }
    public static String itemStackGetString(ItemStack itemStack, String key){
        return itemStack.getOrCreateTag().getString(key);
    }
    public static ResourceLocation getBlockRegistryName(Block block){
        return ForgeRegistries.BLOCKS.getKey(block);
    }
    public static ResourceLocation getItemRegistryName(Item item){
        return ForgeRegistries.ITEMS.getKey(item);
    }
    public static Item getItemFromRegistry(ResourceLocation resourceLocation){
        return ForgeRegistries.ITEMS.getValue(resourceLocation);
    }
}
