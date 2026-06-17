package net.acodonic_king.redstonecg.block.entity;

import net.acodonic_king.redstonecg.RedstonecgMod;
import net.acodonic_king.redstonecg.block.control_panel.logic.DefaultPanelLogic;
import net.acodonic_king.redstonecg.init.RedstonecgModBlockEntities;
import net.acodonic_king.redstonecg.procedures.RCGMatrix;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class CodedControlPanelBlockEntity extends ControlPanelBlockEntity{
    public CodedControlPanelBlockEntity(BlockPos position, BlockState state, int container_size) {
        super(RedstonecgModBlockEntities.CODED_CONTROL_PANEL.get(),position, state, container_size);
    }

    @Override
    public RCGMatrix.M4F slotOrientationTransform(RCGMatrix.M4F matrix, int i){
        super.slotOrientationTransform(matrix, i);
        switch (i){
            case 1 -> matrix.translate(0.75f, 0.0f,0.25f);
            case 2 -> matrix.translate(0.25f, 0.0f,0.75f);
            case 3 -> matrix.translate(0.75f, 0.0f,0.75f);
            default -> matrix.translate(0.25f, 0.0f,0.25f);
        }
        //matrix.translate(0.5f, 0.0f,0.5f);
        matrix.scale(0.75f, 0.75f, 0.75f);
        matrix.translate(-0.5f, 0.0f,-0.5f);
        return matrix;
    }

    @Override
    public float slotSizeX(int i){
        return 0.66f;
    }

    @Override
    public float slotSizeZ(int i){
        return 0.66f;
    }

    @Override
    public int provideRedstone(LevelAccessor world, BlockPos pos){
        int power = 0;
        int i = 0;
        for(DefaultPanelLogic pl: PANELS){
            if(pl.provideRedCu(this, world, pos) > 0)
                power |= 1 << i;
            i++;
        }
        return power;
    }

    @Override
    public boolean receiveRedstone(LevelAccessor world, BlockPos pos, int power){
        if(world.isClientSide())
            return false;
        boolean change = false;
        for(DefaultPanelLogic pl: PANELS){
            //RedstonecgMod.LOGGER.debug(power & 1);
            boolean c = pl.receiveRedCu(this, world, pos, ((power & 1) > 0) ? 255 : 0);
            change = change || c;
            power >>= 1;
        }
        /*for(int i = 0; i < this.stacks.size(); i++)
            this.stacks.set(i, PANELS.get(i).ITEM_STACK);*/
        //CompoundTag tag = ContainerHelper.saveAllItems(new CompoundTag(), this.stacks);
        //RedstonecgMod.LOGGER.debug(tag);
        return change;
    }
}
