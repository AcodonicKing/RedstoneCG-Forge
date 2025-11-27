package net.acodonic_king.redstonecg.init;

import net.acodonic_king.redstonecg.RedstonecgMod;
import net.acodonic_king.redstonecg.block.gui.delayer.DelayerGUIButtonMessage;
import net.acodonic_king.redstonecg.block.gui.redcu_crafter.RedCuCrafterGUIButtonMessage;
import net.acodonic_king.redstonecg.block.gui.redcu_crafter.RedCuCrafterGUISlotMessage;
import net.acodonic_king.redstonecg.block.gui.redcu_wire_transition.RedCuWireTransitionGUIButtonMessage;
import net.acodonic_king.redstonecg.network.RedstonecgModVariables;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import net.acodonic_king.redstonecg.block.gui.analog_source.AnalogSourceGUIButtonMessage;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.acodonic_king.redstonecg.RedstonecgMod.MODID;

public class RedstonecgModNetworking {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel PACKET_HANDLER = NetworkRegistry.newSimpleChannel(new ResourceLocation(MODID, MODID), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
    private static int messageID = 0;
    public static <T> void addNetworkMessage(Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, Supplier<NetworkEvent.Context>> messageConsumer) {
        PACKET_HANDLER.registerMessage(messageID, messageType, encoder, decoder, messageConsumer);
        messageID++;
    }
    //@SubscribeEvent
    private static void onCommonSetup(FMLCommonSetupEvent event){
        addNetworkMessage(AnalogSourceGUIButtonMessage.class, AnalogSourceGUIButtonMessage::buffer, AnalogSourceGUIButtonMessage::new, AnalogSourceGUIButtonMessage::handleData);
        addNetworkMessage(RedCuCrafterGUIButtonMessage.class, RedCuCrafterGUIButtonMessage::buffer, RedCuCrafterGUIButtonMessage::new, RedCuCrafterGUIButtonMessage::handleData);
        addNetworkMessage(DelayerGUIButtonMessage.class, DelayerGUIButtonMessage::buffer, DelayerGUIButtonMessage::new, DelayerGUIButtonMessage::handleData);
        addNetworkMessage(RedCuCrafterGUISlotMessage.class, RedCuCrafterGUISlotMessage::buffer, RedCuCrafterGUISlotMessage::new, RedCuCrafterGUISlotMessage::handleData);
        addNetworkMessage(RedCuWireTransitionGUIButtonMessage.class, RedCuWireTransitionGUIButtonMessage::buffer, RedCuWireTransitionGUIButtonMessage::new, RedCuWireTransitionGUIButtonMessage::handleData);
        addNetworkMessage(RedstonecgModVariables.SavedDataSyncMessage.class, RedstonecgModVariables.SavedDataSyncMessage::buffer, RedstonecgModVariables.SavedDataSyncMessage::new, RedstonecgModVariables.SavedDataSyncMessage::handleData);
    }

    public static void register(IEventBus bus){
        bus.addListener(RedstonecgModNetworking::onCommonSetup);
    }
}
