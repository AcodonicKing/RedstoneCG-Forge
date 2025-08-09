package net.acodonic_king.redstonecg.block.defaults;

import net.acodonic_king.redstonecg.procedures.GetParallelSignalProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class DefaultParallelGateWithAlternate extends DefaultParallelGate{
    public DefaultParallelGateWithAlternate(){
        super();
    }
    public int getAlternateInput(LevelAccessor world, BlockPos thisPos, Direction side){
        BlockState thisBlock = world.getBlockState(thisPos);
        BlockPos targetPos = thisPos.relative(side);
        BlockState targetBlock = world.getBlockState(targetPos);
        if(targetBlock.getBlock() instanceof DefaultParallelGateWithAlternate block){
            if(!GetParallelSignalProcedure.onSameParallelLine(thisBlock, targetBlock)){return 0;}
            return block.giveAlternateInput(world, targetPos, targetBlock, side.getOpposite());
        }
        return 0;
    }
    public int giveAlternateInput(LevelAccessor world, BlockPos thisPos, BlockState blockState, Direction side) {
        return 0;
    }
    /*public void updateAlternate(LevelAccessor world, BlockPos thisPos, BlockPos fromPos, Direction side){

    }*/
}
