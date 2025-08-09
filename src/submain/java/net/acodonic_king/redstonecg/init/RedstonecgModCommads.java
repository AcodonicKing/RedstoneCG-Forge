package net.acodonic_king.redstonecg.init;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.acodonic_king.redstonecg.command.AdventureCommand;
import net.acodonic_king.redstonecg.command.RedCuTransitionModel;
import net.acodonic_king.redstonecg.command.RedCuWireAutoConnectCommand;
import net.acodonic_king.redstonecg.command.RepairCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class RedstonecgModCommads {
    /*public static void register(IEventBus bus){
        bus.addListener(RedstonecgModCommads::onRegisterCommands);
    }*/
    public static void add(RegisterCommandsEvent event, LiteralArgumentBuilder<CommandSourceStack> command){
        event.getDispatcher().register(command);
    }
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event){
        add(event, AdventureCommand.command());
        //add(event, RedCuTransitionModel.command());
        //add(event, RedCuWireAutoConnectCommand.command());
        add(event, RepairCommand.command());
    }
}
