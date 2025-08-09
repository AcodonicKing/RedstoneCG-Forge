package net.acodonic_king.redstonecg.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.acodonic_king.redstonecg.block.entity.RedCuWireTransitionBlockEntity;
import net.acodonic_king.redstonecg.init.RedstonecgModVersionRides;
import net.acodonic_king.redstonecg.procedures.RedCuWireTransitionRenderEncoding;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class RedCuTransitionModel {
    public static LiteralArgumentBuilder<CommandSourceStack> command(){
        return Commands.literal("redcutransitionmodel")
                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                        .then(Commands.literal("clear")
                                .executes(ctx -> {
                                    BlockPos pos = BlockPosArgument.getLoadedBlockPos(ctx, "pos");
                                    return handleClear(ctx.getSource(), pos);
                                })
                        )
                        .then(Commands.literal("add")
                                .then(Commands.argument("modelId", IntegerArgumentType.integer(0))
                                        .then(Commands.argument("rotation", StringArgumentType.string())
                                                .executes(ctx -> {
                                                    BlockPos pos = BlockPosArgument.getLoadedBlockPos(ctx, "pos");
                                                    int modelId = IntegerArgumentType.getInteger(ctx, "modelId");
                                                    String rotation = StringArgumentType.getString(ctx, "rotation");
                                                    return handleAdd(ctx.getSource(), pos, modelId, rotation);
                                                })
                                        ))
                        )
        );
    }
    private static int handleClear(CommandSourceStack source, BlockPos pos) {
        try {
            Level level = source.getLevel();
            if(level.isClientSide()){return 0;}
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof RedCuWireTransitionBlockEntity transition) {
                transition.RENDER_OBJECTS.clear();
                transition.setChanged();
                BlockState state = level.getBlockState(pos);
                level.sendBlockUpdated(pos,state,state,3);
                RedstonecgModVersionRides.sendCommandSuccess(source, "RENDER_OBJECTS cleared at " + pos);
                //source.sendSuccess(Component.literal("RENDER_OBJECTS cleared at " + pos), false);
                return 1;
            }
            RedstonecgModVersionRides.sendCommandFailure(source, "No valid block entity at " + pos);
            //source.sendFailure(Component.literal("No valid block entity at " + pos));
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            RedstonecgModVersionRides.sendCommandFailure(source, "Error during execution: " + e.getMessage());
            return 0;
        }
    }

    private static int handleAdd(CommandSourceStack source, BlockPos pos, int modelId, String rotation) {
        try {
            Level level = source.getLevel();
            if(level.isClientSide()){return 0;}
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof RedCuWireTransitionBlockEntity transition) {
                int packed = RedCuWireTransitionRenderEncoding.packRenderEntry(modelId, rotation);
                transition.RENDER_OBJECTS.add(packed);
                transition.setChanged();
                BlockState state = level.getBlockState(pos);
                level.sendBlockUpdated(pos,state,state,3);
                RedstonecgModVersionRides.sendCommandSuccess(source,"Added render entry: " + packed);
                return 1;
            }
            RedstonecgModVersionRides.sendCommandFailure(source, "No valid block entity at " + pos);
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            RedstonecgModVersionRides.sendCommandFailure(source, "Error during execution: " + e.getMessage());
            return 0;
        }
    }
}
