package net.acodonic_king.redstonecg.block.defaults;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import java.util.function.ToIntFunction;

public class DefaultIndicatorRedstoneInteractableGate extends DefaultIndicatorInteractableGate{
    public static final IntegerProperty POWER = IntegerProperty.create("power",0,15);

    public DefaultIndicatorRedstoneInteractableGate(){
        super();
    }
    public DefaultIndicatorRedstoneInteractableGate(ToIntFunction<BlockState> emitter){
        super(emitter);
    }

    @Override
    public boolean powerIsTrue(BlockState blockState){
        return (blockState.getValue(POWER) > 0);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POWER);
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos){
        return state.getValue(POWER);
    }

    @Override
    public void setPower(LevelAccessor world, BlockState state, BlockPos pos, int power){
        world.setBlock(pos, state.setValue(POWER, power), 2);
    }
}
