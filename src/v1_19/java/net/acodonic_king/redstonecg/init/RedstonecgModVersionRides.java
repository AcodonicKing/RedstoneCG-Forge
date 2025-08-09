package net.acodonic_king.redstonecg.init;

import com.mojang.blaze3d.vertex.PoseStack;
import net.acodonic_king.redstonecg.block.defaults.DefaultIndicatorInteractableGate;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.IItemHandler;
import net.minecraft.client.renderer.MultiBufferSource;

//1.19
import net.minecraft.network.chat.ChatType;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.items.CapabilityItemHandler;

public class RedstonecgModVersionRides {
    public static Capability<IItemHandler> item_handler = CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
    public static ResourceKey<ChatType> chat_type = ChatType.CHAT;
    public static BlockBehaviour.Properties defaultGateProperties = BlockBehaviour.Properties
            .of(Material.CLOTH_DECORATION)
            .sound(SoundType.STONE)
            .strength(1f, 10f)
            .noOcclusion()
            .isRedstoneConductor((bs, br, bp) -> false);
    public static BlockBehaviour.Properties defaultIndicatorProperties = BlockBehaviour.Properties
            .of(Material.CLOTH_DECORATION)
            .sound(SoundType.STONE)
            .strength(1f, 10f)
            .noOcclusion()
            .hasPostProcess((bs, br, bp) -> true)
            .emissiveRendering(DefaultIndicatorInteractableGate::emissiveRendering)
            .lightLevel(DefaultIndicatorInteractableGate::emittedLight)
            .isRedstoneConductor((bs, br, bp) -> false);
    public static boolean isBlockStateSoftSolid(BlockState bs){
        return !(bs.isAir() || bs.getMaterial().isLiquid() || bs.getMaterial().isReplaceable());
    }
    public static boolean isBlockStateHardSolid(BlockState bs){
        return bs.getMaterial().isSolid();
    }
    public static void renderStaticItem(ItemRenderer itemRenderer, Item item, int packedLight, int packedOverlay, PoseStack poseStack, MultiBufferSource bufferSource, Level level){
        itemRenderer.renderStatic(
                new ItemStack(item),
                ItemTransforms.TransformType.FIXED,
                packedLight,
                packedOverlay,
                poseStack,
                bufferSource,
                0
        );
    }
    public static Button createButton(int x, int y, int w, int h, String text, Button.OnPress onPress) {
        return new Button(x, y, w, h, Component.translatable(text), onPress);
    }
    public static BlockItem createBlockItem(RegistryObject<Block> block){
        return new BlockItem(block.get(), new Item.Properties().tab(RedstonecgModTabs.TAB_REDSTONE_CG_CREATIVE_TAB));
    }
    public static Item.Properties newItemSuper(int stacksize){
        return new Item.Properties().tab(RedstonecgModTabs.TAB_REDSTONE_CG_CREATIVE_TAB).stacksTo(stacksize).rarity(Rarity.COMMON);
    }
    public static Level getPlayerLevel(Player player){
        return player.level;
    }
    public static Level getPlayerLevel(Entity entity){
        return entity.level;
    }
    public static void sendCommandSuccess(CommandSourceStack source, String string){
        source.sendSuccess(Component.literal(string), false);
    }
    public static void sendCommandFailure(CommandSourceStack source, String string){
        source.sendFailure(Component.literal(string));
    }
    public static Direction directionFromDelta(int dx, int dy, int dz){
        return Direction.fromNormal(dx, dy, dz);
    }
}