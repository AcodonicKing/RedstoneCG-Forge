package net.acodonic_king.redstonecg.block.normal.indicator;

import net.acodonic_king.redstonecg.block.defaults.DefaultIndicatorRedstoneInteractableGate;
import net.acodonic_king.redstonecg.block.entity.DefaultAnalogIndicatorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BarIndicatorBlock extends DefaultIndicatorRedstoneInteractableGate {
    public BarIndicatorBlock(){super();}
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (world.getBlockEntity(pos) instanceof DefaultAnalogIndicatorBlockEntity be) {
            return switch (be.getFacingIndex()) {
                case 0 -> box(0, 0, 0, 16, 6, 16);
                case 1 -> box(0, 0, 0, 16, 16, 6);
                case 2 -> box(12, 0, 0, 16, 16, 16);
                case 3 -> box(0, 0, 12, 16, 16, 16);
                case 4 -> box(0, 0, 0, 6, 16, 16);
                default -> box(0, 12, 0, 16, 16, 16);
            };
        }
        return box(0, 0, 0, 16, 6, 16);
    }
}
