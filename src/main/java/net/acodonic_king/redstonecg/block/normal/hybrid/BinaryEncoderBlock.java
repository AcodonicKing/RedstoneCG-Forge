package net.acodonic_king.redstonecg.block.normal.hybrid;

import net.acodonic_king.redstonecg.block.defaults.DefaultAnalogInteractable3ABCGate;
import net.acodonic_king.redstonecg.block.entity.DefaultAnalogGateBlockEntity;
import net.acodonic_king.redstonecg.procedures.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class BinaryEncoderBlock extends DefaultAnalogInteractable3ABCGate {
    public BinaryEncoderBlock(){
        super();
    }

    @Override
    public int redstonePowerOperation(int SideAPower, int SideBPower, int SideCPower){
        if(SideCPower > 0)
            return SideAPower | SideBPower;
        return SideAPower;
    }

    @Override
    public int onRedstoneUpdate(LevelAccessor world, BlockState blockState, BlockPos pos, int recursion){
        Direction[] Sides = GetGateInputSidesProcedure.Get3ABCGateForth(blockState);
        int[] power = {0,0,0};
        int i = 0;
        for(Direction side: Sides){
            ConnectionFace thisFace = BlockFrameTransformUtils.getConnectionFace(blockState, side);
            power[i] = GetRedstoneSignalProcedure.execute(world, pos, thisFace);
            i++;
        }
        int output = this.redstonePowerOperation(power[0], power[1], power[2]);
        setPower(world, blockState, pos, output, recursion);
        LittleTools.setBooleanProperty(world, pos, power[2] > 0, "visible_state", 2);
        return output;
    }

    @Override
    public void setPower(LevelAccessor level, BlockState state, BlockPos pos, int power, int recursion){
        power = Math.max(0, Math.min(power, 15));
        Level world = (Level) level;
        if (world.getBlockEntity(pos) instanceof DefaultAnalogGateBlockEntity be){
            if(be.POWER != power){
                be.POWER = power;
                be.setChanged();
                Direction direction = BlockFrameTransformUtils.getWorldDirectionFromLocalForward(state);
                this.sendRedstoneUpdateInDirection(level,state.getBlock(),pos,direction, recursion);
            }
        }
    }
}
