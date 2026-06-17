package net.acodonic_king.redstonecg.block.normal.interaction;

import net.acodonic_king.redstonecg.block.entity.CodedControlPanelBlockEntity;
import net.acodonic_king.redstonecg.block.entity.ControlPanelBlockEntity;
import net.acodonic_king.redstonecg.init.RedstonecgModVersionRides;
import net.acodonic_king.redstonecg.procedures.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class CodedControlPanelBlock extends ControlPanelBlock{
    public static final int[][] BOXES = new int[][]{
            {1, 10, 1,  9, 10, 1,  1, 10, 9,  9, 10, 9,  6, 3, 6},
            {1, 13, 2,  9, 13, 2,  1, 10, 9,  9, 10, 9,  6, 4, 6},
            {4, 9, 10,  10, 9, 4,  4, 1, 10,  10, 1, 4,  5, 6, 5},
            {1, 10, 4,  9, 10, 4,  1, 4, 10,  9, 4, 10,  6, 5, 5}
    };

    @Override
    public VoxelShapeBuilder getInteractionBox(int orientation, int slot, float add){
        int group_index;
        if (orientation < 6)
            group_index = 0;
        else if (orientation < 30)
            group_index = 1;
        else if (orientation > 33 && orientation < 38)
            group_index = 2;
        else
            group_index = 3;
        int si = slot * 3;
        float add2 = add * 2;
        return new VoxelShapeBuilder().first(
                new VoxelShapeBuilder.BoxOperation()
                        .start(BOXES[group_index][si]-add, BOXES[group_index][si + 1]-add, BOXES[group_index][si + 2]-add)
                        .size(BOXES[group_index][12]+add2, BOXES[group_index][13]+add2, BOXES[group_index][14]+add2)
        );
    }

    @Override
    public VoxelShapeBuilder getAllInteractionBox(int orientation, float add){
        int group_index;
        if (orientation < 6)
            group_index = 0;
        else if (orientation < 30)
            group_index = 1;
        else if (orientation > 33 && orientation < 38)
            group_index = 2;
        else
            group_index = 3;
        VoxelShapeBuilder builder = new VoxelShapeBuilder();
        for(int si = 0; si < 12; si += 3){
            builder.OR(
                    new VoxelShapeBuilder.BoxOperation()
                            .start(BOXES[group_index][si], BOXES[group_index][si + 1], BOXES[group_index][si + 2])
                            .size(BOXES[group_index][12], BOXES[group_index][13], BOXES[group_index][14])
            );
        }
        return builder;
    }

    @Override
    public InteractionResult useSlot(BlockState blockstate, Level world, BlockPos pos, Player entity, InteractionHand hand, BlockHitResult hit){
        if(AdventureProcedure.valueConfig(world, entity)) {
            Vec3 hitPos = hit.getLocation().subtract(RedstonecgModVersionRides.getBlockPosCenter(pos));
            hitPos = hitPos.scale(1.0).add(0.5, 0.5, 0.5);
            InteractionResult result = InteractionResult.PASS;
            if(world.getBlockEntity(pos) instanceof ControlPanelBlockEntity be) {
                for (int i = 0; i < 4; i++) {
                    VoxelShape hitBox = getFinalInteractionBox(blockstate.getValue(ORIENTATION), i, 0.1f);
                    if (hitBox.bounds().contains(hitPos)) {
                        InteractionResult resultB = be.usePanelSlot(i, world, pos, entity);
                        if(result == InteractionResult.PASS)
                            result = resultB;
                        else if (resultB != InteractionResult.PASS)
                            result = InteractionResult.SUCCESS;
                    }
                }
                if (result != InteractionResult.PASS) {
                    be.syncInventory(pos);
                    be.setChanged();
                    world.updateNeighborsAt(pos, blockstate.getBlock());
                    return result;
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CodedControlPanelBlockEntity(pos, state, 4);
    }

    @Override
    public int onRedstone(BlockState blockstate, LevelAccessor world, BlockPos pos){
        int power = 0;
        if(world.isClientSide())
            return 0;
        if(world.getBlockEntity(pos) instanceof CodedControlPanelBlockEntity be){
            List<ConnectionFace> connectionFaces = be.getConnectionFaces(blockstate.getValue(ORIENTATION));
            for(ConnectionFace connectionFace: connectionFaces)
                power = Math.max(power, GetRedstoneSignalProcedure.execute(world, pos, connectionFace));
            if(be.receiveRedstone(world, pos, power)) {
                be.setChanged();
                be.syncInventory(pos);
            }
        }
        return power;
    }
}
