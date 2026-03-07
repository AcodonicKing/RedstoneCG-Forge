package net.acodonic_king.redstonecg.procedures;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;

import java.util.concurrent.atomic.AtomicReference;

public class LittleTools {
    public static Direction getDirection(BlockState _bs){
        Property<?> _prop = _bs.getBlock().getStateDefinition().getProperty("facing");
        if (_prop instanceof DirectionProperty _dp)
            return _bs.getValue(_dp);
        _prop = _bs.getBlock().getStateDefinition().getProperty("axis");
        return _prop instanceof EnumProperty _ep && _ep.getPossibleValues().toArray()[0] instanceof Direction.Axis ? Direction.fromAxisAndDirection((Direction.Axis) _bs.getValue(_ep), Direction.AxisDirection.POSITIVE) : Direction.NORTH;
    }
    public static Direction getDirectionB(BlockState _bs){
        Property<?> _prop = _bs.getBlock().getStateDefinition().getProperty("rotation");
        return _prop instanceof DirectionProperty _dp ? _bs.getValue(_dp): Direction.NORTH;
    }
    public static void setDirection(LevelAccessor world, BlockPos _pos, Direction _dir){
        BlockState _bs = world.getBlockState(_pos);
        Property<?> _property = _bs.getBlock().getStateDefinition().getProperty("facing");
        if (_property instanceof DirectionProperty _dp && _dp.getPossibleValues().contains(_dir)) {
            world.setBlock(_pos, _bs.setValue(_dp, _dir), 3);
        } else {
            _property = _bs.getBlock().getStateDefinition().getProperty("axis");
            if (_property instanceof EnumProperty _ap && _ap.getPossibleValues().contains(_dir.getAxis()))
                world.setBlock(_pos, _bs.setValue(_ap, _dir.getAxis()), 3);
        }
    }
    public static void setDirectionB(LevelAccessor world, BlockPos _pos, Direction _dir){
        BlockState _bs = world.getBlockState(_pos);
        Property<?> _property = _bs.getBlock().getStateDefinition().getProperty("rotation");
        if (_property instanceof DirectionProperty _dp && _dp.getPossibleValues().contains(_dir)) {
            world.setBlock(_pos, _bs.setValue(_dp, _dir), 3);
        }
    }
    public static void setIntegerProperty(LevelAccessor world, BlockPos _pos, int _value, String _name){
        setIntegerProperty(world, _pos, _value, _name, 3);
    }
    public static void setIntegerProperty(LevelAccessor world, BlockPos _pos, int _value, String _name, int flags){
        BlockState _bs = world.getBlockState(_pos);
        if (_bs.getBlock().getStateDefinition().getProperty(_name) instanceof IntegerProperty _integerProp && _integerProp.getPossibleValues().contains(_value))
            world.setBlock(_pos, _bs.setValue(_integerProp, _value), flags);
    }
    public static int getIntegerProperty(BlockState _bs, String _name){
        return _bs.getBlock().getStateDefinition().getProperty(_name) instanceof IntegerProperty _getip5 ? _bs.getValue(_getip5) : -1;
    }
    public static void setBooleanProperty(LevelAccessor world, BlockPos _pos, boolean _state, String _name){
        setBooleanProperty(world, _pos, _state, _name, 3);
    }
    public static void setBooleanProperty(LevelAccessor world, BlockPos _pos, boolean _state, String _name, int flags){
        BlockState _bs = world.getBlockState(_pos);
        if (_bs.getBlock().getStateDefinition().getProperty(_name) instanceof BooleanProperty _booleanProp)
            world.setBlock(_pos, _bs.setValue(_booleanProp, _state), flags);
    }
    public static boolean getBooleanProperty(BlockState _bs, String _name){
        return _bs.getBlock().getStateDefinition().getProperty(_name) instanceof BooleanProperty _getbp6 && _bs.getValue(_getbp6);
    }
    public static double getBlockEntityNBTValue(LevelAccessor world, BlockPos _pos, String _name){
        BlockEntity blockEntity = world.getBlockEntity(_pos);
        if (blockEntity != null)
            return blockEntity.getPersistentData().getDouble(_name);
        return -1;
    }
    public static void setBlockEntityNBTValue(LevelAccessor world, BlockPos _pos, double _value, String _name){
        if (!world.isClientSide()) {
            BlockEntity _blockEntity = world.getBlockEntity(_pos);
            BlockState _bs = world.getBlockState(_pos);
            if (_blockEntity != null)
                _blockEntity.getPersistentData().putDouble(_name, _value);
            if (world instanceof Level _level)
                _level.sendBlockUpdated(_pos, _bs, _bs, 3);
        }
    }
    public static int hasItem(Player player, Item item){
        int amount = 0;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack invStack = player.getInventory().getItem(i);
            if (invStack.is(item))
                amount += invStack.getCount();
        }
        return amount;
    }
    public static void removeItems(Player player, Item item, int amount) {
        int remaining = amount;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack invStack = player.getInventory().getItem(i);
            if (invStack.is(item)) {
                int remove = Math.min(invStack.getCount(), remaining);
                invStack.shrink(remove);
                remaining -= remove;
                if (remaining <= 0) {
                    break;
                }
            }
        }
    }
    /*public static ItemStack getItemStackFromBlock(LevelAccessor world, BlockPos pos, int slotid) {
        AtomicReference<ItemStack> _retval = new AtomicReference<>(ItemStack.EMPTY);
        BlockEntity _ent = world.getBlockEntity(pos);
        if (_ent != null)
            _ent.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(capability -> _retval.set(capability.getStackInSlot(slotid).copy()));
        return _retval.get();
    }*/
    /*public static ItemStack getItemStackFromBlock(LevelAccessor world, BlockPos pos, int slotid) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity == null) {
            System.out.println("No block entity at: " + pos);
            return ItemStack.EMPTY;
        }

        return blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, null)
                .map(handler -> {
                    if (slotid < 0 || slotid >= handler.getSlots()) {
                        System.out.println("Invalid slot: " + slotid);
                        return ItemStack.EMPTY;
                    }
                    return handler.getStackInSlot(slotid).copy();
                })
                .orElseGet(() -> {
                    System.out.println("Capability missing at: " + pos);
                    return ItemStack.EMPTY;
                });
    }
    public static void setItemStackInBlockInventory(LevelAccessor world, BlockPos pos, ItemStack itemstack, int slotid){
        BlockEntity _ent = world.getBlockEntity(pos);
        if (_ent != null) {
            _ent.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(capability -> {
                if (capability instanceof IItemHandlerModifiable)
                    ((IItemHandlerModifiable) capability).setStackInSlot(slotid, itemstack.copy());
            });
        }
    }*/
}
