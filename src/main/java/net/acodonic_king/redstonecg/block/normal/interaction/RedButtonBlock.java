package net.acodonic_king.redstonecg.block.normal.interaction;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class RedButtonBlock extends RedSwitchBlock {
    public RedButtonBlock(){
        super();
    }

    @Override
    public void toggleState(LevelAccessor level, BlockState blockState, BlockPos pos, Player player){
        if(blockState.getValue(STATE)){
            setState(level, blockState, pos, null, false);
            return;
        }
        setState(level, blockState, pos, null, true);
        level.scheduleTick(pos, this, 10);
    }

    @Override
    public void tick(BlockState blockState, ServerLevel level, BlockPos pos, RandomSource random){
        setState(level, blockState, pos, null, false);
    }
}
