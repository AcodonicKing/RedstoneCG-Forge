package net.acodonic_king.redstonecg.block.defaults;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import java.util.function.ToIntFunction;

public class DefaultIndicatorBooleanInteractableGate extends DefaultIndicatorInteractableGate{
    public static final BooleanProperty STATE = BooleanProperty.create("state");

    public DefaultIndicatorBooleanInteractableGate(){
        super();
    }
    public DefaultIndicatorBooleanInteractableGate(ToIntFunction<BlockState> emitter){
        super(emitter);
    }

    @Override
    public boolean powerIsTrue(BlockState blockState){
        return blockState.getValue(STATE);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(STATE);
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos){
        return state.getValue(STATE) ? 15 : 0;
    }

    @Override
    public void setPower(LevelAccessor world, BlockState state, BlockPos pos, int power){
        world.setBlock(pos, state.setValue(STATE, power > 0), 2);
    }
}
