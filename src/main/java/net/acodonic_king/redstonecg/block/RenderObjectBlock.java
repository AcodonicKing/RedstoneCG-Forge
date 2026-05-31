package net.acodonic_king.redstonecg.block;

import net.acodonic_king.redstonecg.block.defaults.SuperBlock;
import net.acodonic_king.redstonecg.init.RedstonecgModVersionRides;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class RenderObjectBlock extends SuperBlock {
    public static final IntegerProperty MODEL = IntegerProperty.create("model",0,4);
    public RenderObjectBlock() {
        super(RedstonecgModVersionRides.defaultGateProperties);
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(MODEL);
    }
}
