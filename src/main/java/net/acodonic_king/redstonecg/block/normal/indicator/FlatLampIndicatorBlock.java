package net.acodonic_king.redstonecg.block.normal.indicator;

import net.acodonic_king.redstonecg.block.defaults.DefaultIndicatorBooleanInteractableGate;
import net.minecraft.world.level.block.state.BlockState;

public class FlatLampIndicatorBlock extends DefaultIndicatorBooleanInteractableGate {
    public static int emittedLight(BlockState state){
        if(state.getBlock() instanceof DefaultIndicatorBooleanInteractableGate block)
            if (block.powerIsTrue(state)){return 15;}
        return 0;
    }
    public FlatLampIndicatorBlock(){
        super(FlatLampIndicatorBlock::emittedLight);
    }
}
