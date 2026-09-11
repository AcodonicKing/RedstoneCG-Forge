package net.acodonic_king.redstonecg.block.normal.indicator;

import net.acodonic_king.redstonecg.RedstonecgMod;
import net.acodonic_king.redstonecg.block.defaults.DefaultColoredLampBlock;
import net.acodonic_king.redstonecg.block.defaults.PowerIntegerPropertyInterface;
import net.acodonic_king.redstonecg.block.entity.DefaultColoredLampBlockEntity;
import net.acodonic_king.redstonecg.init.RedstonecgModVersionRides;
import net.acodonic_king.redstonecg.network.MessengerBlockEntityPigeon;
import net.acodonic_king.redstonecg.procedures.LittleTools;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

public class ColorfulLampBlock  extends DefaultColoredLampBlock implements PowerIntegerPropertyInterface {
    public static final IntegerProperty POWER = IntegerProperty.create("power", 0, 15);

    public ColorfulLampBlock() {
        super(RedstonecgModVersionRides.indicatorLight(ColorfulLampBlock::emittedLight));
    }

    public static int emittedLight(BlockState state){
        if(state.getBlock() instanceof ColorfulLampBlock)
            return state.getValue(POWER) == 15 ? 0 : 15;
        return 0;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWER);
    }

    @Override
    public void redstoneUpdate(Level world, BlockPos pos){
        if (world.isClientSide) return;
        int signal = world.getBestNeighborSignal(pos);
        if(world.getBlockEntity(pos) instanceof DefaultColoredLampBlockEntity be){
            if(signal == 15)
                be.setColor(DyeColor.WHITE.getTextColor());
            else
                be.setColor(DyeColor.byId(signal).getTextColor());
            be.setChanged();
            BlockState state = world.getBlockState(pos).setValue(POWER, signal);
            world.setBlock(pos, state, 3);
            MessengerBlockEntityPigeon.send(new MessengerBlockEntityPigeon(pos, be.getUpdateTag()), false);
        }
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos){
        if(state.getBlock() instanceof ColorfulLampBlock)
            return state.getValue(POWER);
        return 0;
    }

    @Override
    public IntegerProperty getPowerIntegerProperty() {
        return POWER;
    }
}
