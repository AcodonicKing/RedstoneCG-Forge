package net.acodonic_king.redstonecg.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.acodonic_king.redstonecg.RedstonecgMod;
import net.acodonic_king.redstonecg.init.RedstonecgModVersionRides;
import net.acodonic_king.redstonecg.network.RedstonecgModVariables;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.level.Level;

public class RedstoneCGGameplayCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> command(){
        return Commands.literal("redstonecg_gameplay")
                .requires(cs -> cs.hasPermission(2))
                .then(Commands.literal("maxHangingRedCuWireLength")
                        .then(Commands.argument("length", IntegerArgumentType.integer(1))
                                .executes(ctx -> {
                                    CommandSourceStack source = ctx.getSource();
                                    Level level = source.getLevel();
                                    int length = IntegerArgumentType.getInteger(ctx,"length");
                                    RedstonecgModVariables.MapVariables mapVariables = RedstonecgModVariables.MapVariables.get(level);
                                    mapVariables.hangingRedCuWireMaxDistance = length;
                                    mapVariables.syncData(level);
                                    RedstonecgModVersionRides.sendCommandSuccess(source,"The maximum Hanging RedCu Wire length has been set to "+length);
                                    return 1;
                                })
                        )
                        .executes(ctx -> {
                            CommandSourceStack source = ctx.getSource();
                            Level level = source.getLevel();
                            int length = RedstonecgModVariables.MapVariables.get(level).hangingRedCuWireMaxDistance;
                            RedstonecgModVersionRides.sendCommandSuccess(source,"The maximum Hanging RedCu Wire length is "+length);
                            return 1;
                        })
                )
                .then(Commands.literal("gateChainLimit")
                        .then(Commands.argument("length", IntegerArgumentType.integer(0))
                                .executes(ctx -> {
                                    CommandSourceStack source = ctx.getSource();
                                    Level level = source.getLevel();
                                    int length = IntegerArgumentType.getInteger(ctx,"length");
                                    RedstonecgModVariables.MapVariables mapVariables = RedstonecgModVariables.MapVariables.get(level);
                                    mapVariables.gateChainLimit = length;
                                    mapVariables.syncData(level);
                                    RedstonecgModVersionRides.sendCommandSuccess(source,"The instant gate chain update limit has been set to "+length);
                                    return 1;
                                })
                        )
                        .executes(ctx -> {
                            CommandSourceStack source = ctx.getSource();
                            Level level = source.getLevel();
                            int length = RedstonecgModVariables.MapVariables.get(level).gateChainLimit;
                            RedstonecgModVersionRides.sendCommandSuccess(source,"The instant gate chain update limit is "+length);
                            return 1;
                        })
                )
                .then(Commands.literal("wireChainLimit")
                        .then(Commands.argument("length", IntegerArgumentType.integer(0))
                                .executes(ctx -> {
                                    CommandSourceStack source = ctx.getSource();
                                    Level level = source.getLevel();
                                    int length = IntegerArgumentType.getInteger(ctx,"length");
                                    RedstonecgModVariables.MapVariables mapVariables = RedstonecgModVariables.MapVariables.get(level);
                                    mapVariables.wireChainLimit = length;
                                    mapVariables.syncData(level);
                                    RedstonecgModVersionRides.sendCommandSuccess(source,"The instant wire chain update limit has been set to "+length);
                                    return 1;
                                })
                        )
                        .executes(ctx -> {
                            CommandSourceStack source = ctx.getSource();
                            Level level = source.getLevel();
                            int length = RedstonecgModVariables.MapVariables.get(level).wireChainLimit;
                            RedstonecgModVersionRides.sendCommandSuccess(source,"The instant wire chain update limit is "+length);
                            return 1;
                        })
                )
                ;
    }
}
