package net.acodonic_king.redstonecg.block.normal.indicator;

import net.acodonic_king.redstonecg.block.defaults.DefaultIndicatorRedstoneInteractableGate;
import net.acodonic_king.redstonecg.block.defaults.StainLampInterface;
import net.acodonic_king.redstonecg.block.entity.DefaultAnalogIndicatorBlockEntity;
import net.acodonic_king.redstonecg.block.entity.DefaultColoredFlatLampBlockEntity;
import net.acodonic_king.redstonecg.init.RedstonecgModItems;
import net.acodonic_king.redstonecg.init.RedstonecgModVersionRides;
import net.acodonic_king.redstonecg.procedures.AdventureProcedure;
import net.acodonic_king.redstonecg.procedures.OnBlockRightClickedProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeaconBeamBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class ColoredFlatLampIndicatorBlock extends DefaultIndicatorRedstoneInteractableGate {

    public ColoredFlatLampIndicatorBlock(){
        super(ColoredFlatLampIndicatorBlock::emittedLight);
    }

    public static int emittedLight(BlockState state){
        if(state.getBlock() instanceof ColoredFlatLampIndicatorBlock)
            return state.getValue(POWER);
        return 0;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new DefaultColoredFlatLampBlockEntity(blockPos, blockState);
    }

    @Override
    public InteractionResult use(BlockState blockstate, Level world, BlockPos pos, Player entity, InteractionHand hand, BlockHitResult hit) {
        if(!AdventureProcedure.valueConfig(world, entity))
            return InteractionResult.PASS;
        ItemStack itemStack = entity.getItemInHand(hand);
        if(!(world.getBlockEntity(pos) instanceof DefaultColoredFlatLampBlockEntity))
            return InteractionResult.PASS;
        if(!itemStack.isEmpty()) {
            if (itemStack.is(RedstonecgModItems.ROTATION_BRACKET.get())) {
                return InteractionResult.PASS;
            }
            if (itemStack.is(blockstate.getBlock().asItem()) && AdventureProcedure.pinConfig(world, entity)) {
                if (world.getBlockEntity(pos) instanceof DefaultAnalogIndicatorBlockEntity be) {
                    be.BASE_READ = !be.BASE_READ;
                    be.setChanged();
                    world.sendBlockUpdated(pos, blockstate, blockstate, 3);
                    return InteractionResult.SUCCESS;
                }
            }
            int color = -1;
            Item stainItem = null;
            if (itemStack.getItem() instanceof BlockItem bi) {
                if (bi.getBlock() instanceof BeaconBeamBlock bl) {
                    if (itemStack.is(RedstonecgModVersionRides.getItemTag("forge", "stained_glass_panes"))) {
                        color = bl.getColor().getTextColor();
                        stainItem = itemStack.getItem();
                        if (!world.isClientSide && !entity.getAbilities().instabuild) {
                            itemStack.shrink(1);
                        }
                    }
                }
                if (color == -1)
                    if (bi.getBlock() instanceof StainLampInterface bl) {
                        color = bl.getLampStainColor(world, pos, entity, hand, hit);
                        stainItem = bl.consumeStainingItem(world, pos, entity, hand, hit);
                    }
            }
            if (color != -1)
                if (world.getBlockEntity(pos) instanceof DefaultColoredFlatLampBlockEntity be) {
                    be.setColor(color);
                    be.setChanged();
                    world.updateNeighborsAt(pos, blockstate.getBlock());
                    if (be.ITEM != null)
                        Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(be.ITEM));
                    if (stainItem != null)
                        be.ITEM = stainItem;
                    return InteractionResult.SUCCESS;
                }
        }
        if(AdventureProcedure.pinConfig(world, entity)){
            if(world.getBlockEntity(pos) instanceof DefaultAnalogIndicatorBlockEntity be)
                if (blockstate.getValue(CONNECTION) == 14) {
                    be.BASE_READ = !be.BASE_READ;
                    be.setChanged();
                }
            OnBlockRightClickedProcedure.execute(world, pos, blockstate);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            if(level.getBlockEntity(pos) instanceof DefaultColoredFlatLampBlockEntity be){
                if(be.ITEM != null)
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(be.ITEM));
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }
}
