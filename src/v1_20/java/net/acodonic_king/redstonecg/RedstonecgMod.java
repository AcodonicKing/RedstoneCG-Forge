package net.acodonic_king.redstonecg;

import net.acodonic_king.redstonecg.init.*;
import net.acodonic_king.redstonecg.procedures.RCGMatrix;
import net.acodonic_king.redstonecg.procedures.VoxelShapeBuilder;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;
import java.util.function.Function;
import java.util.function.BiConsumer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.AbstractMap;

import static net.acodonic_king.redstonecg.procedures.RightAngleRotation.CCW_MAP;
import static net.acodonic_king.redstonecg.procedures.RightAngleRotation.CW1;

@Mod("redstonecg")
public class RedstonecgMod {
    public static final Logger LOGGER = LogManager.getLogger(RedstonecgMod.class);
    public static final String MODID = "redstonecg";

    public RedstonecgMod() {
        MinecraftForge.EVENT_BUS.register(this);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        RedstonecgModBlocks.REGISTRY.register(bus);
        RedstonecgModBlockEntities.REGISTRY.register(bus);
        RedstonecgModMenus.REGISTRY.register(bus);
        RedstonecgModItems.REGISTRY.register(bus);
        RedstonecgModTabs.REGISTRY.register(bus);
        RedstonecgModRecipes.REGISTRY.register(bus);
        RedstonecgModRecipes.TYPES.register(bus);
        RedstonecgModNetworking.register(bus);

        /*VoxelShapeBuilder builder = new VoxelShapeBuilder();
        builder
                .first(new VoxelShapeBuilder.BoxOperation().start(4, 10, 4).end(12, 14, 12))
                .rotateX(CW1, 8, 8, 8)
        ;
        RedstonecgMod.LOGGER.debug(builder.OPERATIONS.get(0)+"\n");*/
    }
}
