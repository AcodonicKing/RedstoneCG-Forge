package net.acodonic_king.redstonecg.block.normal.indicator;

import net.acodonic_king.redstonecg.block.defaults.DefaultIndicatorRedstoneInteractableGate;
import net.acodonic_king.redstonecg.block.entity.DefaultColoredFlatLampBlockEntity;
import net.acodonic_king.redstonecg.network.MessengerBlockEntityPigeon;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ColorfulFlatLampIndicatorBlock extends DefaultIndicatorRedstoneInteractableGate {

    public ColorfulFlatLampIndicatorBlock(){
        super(ColorfulFlatLampIndicatorBlock::emittedLight);
    }

    public static int emittedLight(BlockState state){
        if(state.getBlock() instanceof ColorfulFlatLampIndicatorBlock)
            return state.getValue(POWER) == 15 ? 0 : 15;
        return 0;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new DefaultColoredFlatLampBlockEntity(blockPos, blockState);
    }

    @Override
    public void setPower(LevelAccessor world, BlockState state, BlockPos pos, int power){
        if (world.isClientSide()) return;
        if(world.getBlockEntity(pos) instanceof DefaultColoredFlatLampBlockEntity be){
            if(power == 15)
                be.setColor(DyeColor.WHITE.getTextColor());
            else
                be.setColor(DyeColor.byId(power).getTextColor());
            be.setChanged();
            state = state.setValue(POWER, power);
            world.setBlock(pos, state, 3);
            //MessengerBlockEntityPigeon.send(new MessengerBlockEntityPigeon(pos, be.getUpdateTag()));
        }
    }
}
