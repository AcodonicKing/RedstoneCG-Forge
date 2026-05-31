package net.acodonic_king.redstonecg.block.normal.indicator;

import net.acodonic_king.redstonecg.block.defaults.DefaultColoredLampBlock;
import net.acodonic_king.redstonecg.block.defaults.PowerIntegerPropertyInterface;
import net.acodonic_king.redstonecg.block.defaults.StainLampInterface;
import net.acodonic_king.redstonecg.block.entity.DefaultColoredLampBlockEntity;
import net.acodonic_king.redstonecg.init.RedstonecgModVersionRides;
import net.acodonic_king.redstonecg.item.ColoredLampItem;
import net.acodonic_king.redstonecg.procedures.AdventureProcedure;
import net.acodonic_king.redstonecg.procedures.LittleTools;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeaconBeamBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

public class ColoredLampBlock extends DefaultColoredLampBlock implements PowerIntegerPropertyInterface {
    public static final IntegerProperty POWER = IntegerProperty.create("power", 0, 15);

    public ColoredLampBlock() {
        super(RedstonecgModVersionRides.defaultLampProperties
                .lightLevel(ColoredLampBlock::emittedLight)
        );
    }

    public static int emittedLight(BlockState state){
        if(state.getBlock() instanceof ColoredLampBlock)
            return state.getValue(POWER);
        return 0;
    }

    @Override
    public InteractionResult use(BlockState blockstate, Level world, BlockPos pos, Player entity, InteractionHand hand, BlockHitResult hit) {
        InteractionResult interactionResult = super.use(blockstate, world, pos, entity, hand, hit);
        if (interactionResult == InteractionResult.FAIL)
            return interactionResult;
        if(!AdventureProcedure.valueConfig(world, entity))
            return InteractionResult.FAIL;
        ItemStack itemStack = entity.getItemInHand(hand);
        if(itemStack.isEmpty())
            return InteractionResult.FAIL;
        if(!(world.getBlockEntity(pos) instanceof DefaultColoredLampBlockEntity))
            return InteractionResult.FAIL;
        if (itemStack.getItem() instanceof ColoredLampItem cli) {
            if (world.getBlockEntity(pos) instanceof DefaultColoredLampBlockEntity be) {
                CompoundTag tag = be.getParameterSet();
                if(tag.contains("item") && !world.isClientSide && !entity.getAbilities().instabuild){
                    cli.clearSet(itemStack, entity);
                    Item item = be.getItem();
                    int count = LittleTools.hasItem(entity, item);
                    if(count >= itemStack.getCount()){
                        LittleTools.removeItems(entity, item, itemStack.getCount());
                    } else if (count > 0) {
                        int drop = itemStack.getCount() - count;
                        itemStack.setCount(count);
                        LittleTools.removeItems(entity, item, itemStack.getCount());
                        ItemStack dropStack = itemStack.copy();
                        dropStack.setCount(drop);
                        if(!entity.addItem(dropStack))
                            entity.drop(dropStack, false);
                    } else
                        return InteractionResult.FAIL;
                }
                itemStack.getOrCreateTag().put("BlockParameterSet", tag);
                return InteractionResult.SUCCESS;
            }
        }
        int color = -1;
        Item stainItem = null;
        if(itemStack.getItem() instanceof BlockItem bi){
            if(bi.getBlock() instanceof BeaconBeamBlock bl){
                if(itemStack.is(RedstonecgModVersionRides.createItemTag("forge", "stained_glass_panes"))) {
                    color = bl.getColor().getTextColor();
                    stainItem = itemStack.getItem();
                    if(!world.isClientSide && !entity.getAbilities().instabuild){
                        //Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), itemStack.copyWithCount(1));
                        itemStack.shrink(1);
                    }
                }
            }
            if(color == -1) {
                if (bi.getBlock() instanceof StainLampInterface bl) {
                    color = bl.getLampStainColor(world, pos, entity, hand, hit);
                    stainItem = bl.consumeStainingItem(world, entity, hand);
                }
            }
        }
        if(color == -1) {
            if (itemStack.getItem() instanceof StainLampInterface bl) {
                color = bl.getLampStainColor(world, pos, entity, hand, hit);
                stainItem = bl.consumeStainingItem(world, entity, hand);
            } else
                return InteractionResult.FAIL;
        }
        if(color == -1)
            return InteractionResult.FAIL;
        if(world.getBlockEntity(pos) instanceof DefaultColoredLampBlockEntity be){
            be.setColor(color);
            be.setChanged();
            world.updateNeighborsAt(pos, blockstate.getBlock());
            if(be.ITEM != null)
                Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(be.ITEM));
            if(stainItem != null)
                be.ITEM = stainItem;
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (level.isClientSide) return;
        if (!stack.hasTag()) return;
        CompoundTag tag = stack.getTag();
        if (!tag.contains("BlockParameterSet")) return;
        tag = tag.getCompound("BlockParameterSet");
        if (level.getBlockEntity(pos) instanceof DefaultColoredLampBlockEntity be){
            be.setParameterSet(tag);
            be.setChanged();
            level.sendBlockUpdated(pos, state, state, 3);
            level.scheduleTick(pos, state.getBlock(), 1);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWER);
    }

    @Override
    public void redstoneUpdate(Level world, BlockPos pos){
        int signal = world.getBestNeighborSignal(pos);
        world.setBlock(pos, world.getBlockState(pos).setValue(POWER, signal), 3);
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos){
        if(state.getBlock() instanceof ColoredLampBlock)
            return state.getValue(POWER);
        return 0;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            if(level.getBlockEntity(pos) instanceof DefaultColoredLampBlockEntity be){
                if(be.ITEM != null)
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(be.ITEM));
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public IntegerProperty getPowerIntegerProperty() {
        return POWER;
    }
}
