package net.acodonic_king.redstonecg.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.acodonic_king.redstonecg.init.RedstonecgModVersionRides;
import net.acodonic_king.redstonecg.network.RedstonecgModVariables;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.level.Level;

public class AdventureCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> command(){
        return Commands.literal("redstonecg_adventure")
                .requires(cs -> cs.hasPermission(2))
                .then(Commands.literal("pinConfig")
                        .then(Commands.argument("enable", BoolArgumentType.bool())
                                .executes(ctx -> {
                                    CommandSourceStack source = ctx.getSource();
                                    Level level = source.getLevel();
                                    boolean enabled = BoolArgumentType.getBool(ctx, "enable");
                                    RedstonecgModVariables.MapVariables.get(level).adventurePinConfig = enabled;
                                    RedstonecgModVariables.MapVariables.get(level).syncData(level);
                                    if(enabled)
                                        RedstonecgModVersionRides.sendCommandSuccess(source,"Players in Adventure Mode can configure pins.");
                                    else
                                        RedstonecgModVersionRides.sendCommandSuccess(source,"Players in Adventure Mode can not configure pins.");
                                    return 1;
                                })
                        )
                )
                .then(Commands.literal("valueConfig")
                        .then(Commands.argument("enable", BoolArgumentType.bool())
                                .executes(ctx -> {
                                    CommandSourceStack source = ctx.getSource();
                                    Level level = source.getLevel();
                                    boolean enabled = BoolArgumentType.getBool(ctx, "enable");
                                    RedstonecgModVariables.MapVariables.get(level).adventureValueConfig = enabled;
                                    RedstonecgModVariables.MapVariables.get(level).syncData(level);
                                    if(enabled)
                                        RedstonecgModVersionRides.sendCommandSuccess(source,"Players in Adventure Mode can configure block values.");
                                    else
                                        RedstonecgModVersionRides.sendCommandSuccess(source,"Players in Adventure Mode can not configure block values.");
                                    return 1;
                                })
                        )
                )
                .then(Commands.literal("gateGUI")
                        .then(Commands.argument("enable", BoolArgumentType.bool())
                                .executes(ctx -> {
                                    CommandSourceStack source = ctx.getSource();
                                    Level level = source.getLevel();
                                    boolean enabled = BoolArgumentType.getBool(ctx, "enable");
                                    RedstonecgModVariables.MapVariables.get(level).adventureGateGUI = enabled;
                                    RedstonecgModVariables.MapVariables.get(level).syncData(level);
                                    if(enabled)
                                        RedstonecgModVersionRides.sendCommandSuccess(source,"Players in Adventure Mode can open GUI of gates.");
                                    else
                                        RedstonecgModVersionRides.sendCommandSuccess(source,"Players in Adventure Mode can not open GUI of gates.");
                                    return 1;
                                })
                        )
                )
                .then(Commands.literal("applySurvival")
                        .then(Commands.argument("enable", BoolArgumentType.bool())
                                .executes(ctx -> {
                                    CommandSourceStack source = ctx.getSource();
                                    Level level = source.getLevel();
                                    boolean enabled = BoolArgumentType.getBool(ctx, "enable");
                                    RedstonecgModVariables.MapVariables.get(level).adventureSurvival = enabled;
                                    RedstonecgModVariables.MapVariables.get(level).syncData(level);
                                    if(enabled)
                                        RedstonecgModVersionRides.sendCommandSuccess(source,"Adventure Mode settings are applied to Survival Mode.");
                                    else
                                        RedstonecgModVersionRides.sendCommandSuccess(source,"Adventure Mode settings are Adventure Mode exclusive.");
                                    return 1;
                                })
                        )
        );
    }
}
