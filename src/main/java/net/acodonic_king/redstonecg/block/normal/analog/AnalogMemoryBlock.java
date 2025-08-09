package net.acodonic_king.redstonecg.block.normal.analog;

import net.acodonic_king.redstonecg.block.defaults.DefaultAnalogInteractible2ABGate;
import net.acodonic_king.redstonecg.procedures.BlockFrameTransformUtils;
import net.acodonic_king.redstonecg.procedures.ConnectionFace;
import net.acodonic_king.redstonecg.procedures.GetGateInputSidesProcedure;
import net.acodonic_king.redstonecg.procedures.GetRedstoneSignalProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class AnalogMemoryBlock extends DefaultAnalogInteractible2ABGate {
    public AnalogMemoryBlock() {
        super();
    }
    @Override
    public int onRedstoneUpdate(LevelAccessor world, BlockState blockState, BlockPos pos){
        Direction[] Sides = GetGateInputSidesProcedure.Get2ABGateForth(blockState);

        int[] power = {0,0};
        int i = 0;
        for(Direction side: Sides){
            ConnectionFace thisFace = BlockFrameTransformUtils.getConnectionFace(blockState, side);
            power[i] = GetRedstoneSignalProcedure.execute(world, pos, thisFace);
            i++;
        }

        if(power[1] > 0){
            setPower(world, blockState, pos, power[0]);
        }

        return 0;
    }
}
