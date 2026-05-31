package net.acodonic_king.redstonecg.item;

import net.acodonic_king.redstonecg.block.defaults.DefaultGate;
import net.acodonic_king.redstonecg.block.defaults.RotationBracketInterface;
import net.acodonic_king.redstonecg.block.defaults.WireInterface;
import net.acodonic_king.redstonecg.block.entity.DefaultAnalogIndicatorBlockEntity;
import net.acodonic_king.redstonecg.init.RedstonecgModVersionRides;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;

import javax.annotation.Nullable;
import java.util.List;

public class RotationBracket extends SuperItem {
    public RotationBracket() {
        super(RedstonecgModVersionRides.newItemSuper(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        if (!world.isClientSide) {
            BlockPos pos = context.getClickedPos();
            Player player = context.getPlayer();
            if(player == null){return InteractionResult.FAIL;}
            BlockState blockState = world.getBlockState(pos);
            //ResourceLocation id = ForgeRegistries.BLOCKS.getKey(blockState.getBlock());
            //if(id == null){return InteractionResult.FAIL;}
            //if(!id.getNamespace().equals(RedstonecgMod.MODID)){return InteractionResult.FAIL;}
            Block block = blockState.getBlock();
            if(block instanceof DefaultGate) {
                Property<?> _prop = block.getStateDefinition().getProperty("rotation");
                if (_prop instanceof DirectionProperty _dp) {
                    return rotateBlock(world, blockState, pos, _dp, player);
                }
                _prop = block.getStateDefinition().getProperty("facing");
                if (_prop instanceof DirectionProperty _dp) {
                    return rotateBlock(world, blockState, pos, _dp, player);
                }
            } else if (block instanceof WireInterface) {
                ((WireInterface) block).rotationBracket(world, pos, !player.isCrouching());
                world.scheduleTick(pos, block, 1);
                return InteractionResult.SUCCESS;
            } else if (world.getBlockEntity(pos) instanceof DefaultAnalogIndicatorBlockEntity be){
                be.ROTATION = rotateBlockDirection(be.ROTATION, player);
                be.modelUpdate();
                be.setChanged();
                world.sendBlockUpdated(pos, blockState, blockState, 3);
                world.scheduleTick(pos, block, 1);
                return InteractionResult.SUCCESS;
            } else if (block instanceof RotationBracketInterface bi){
                bi.rotationBracket(world, pos, !player.isCrouching());
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.FAIL;
    }

    private InteractionResult rotateBlock(Level world, BlockState blockState, BlockPos pos, DirectionProperty _dp, Player player){
        Direction direction = blockState.getValue(_dp);
        if(player.isCrouching()){
            direction = direction.getCounterClockWise(Direction.Axis.Y);
        } else {
            direction = direction.getClockWise(Direction.Axis.Y);
        }
        if(_dp.getPossibleValues().contains(direction)){
            world.setBlock(pos, blockState.setValue(_dp, direction), 3);
            world.scheduleTick(pos, blockState.getBlock(), 1);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    private Direction rotateBlockDirection(Direction direction, Player player){
        if(player.isCrouching()){
            direction = direction.getCounterClockWise(Direction.Axis.Y);
        } else {
            direction = direction.getClockWise(Direction.Axis.Y);
        }
        return direction;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.redstonecg.rotation_bracket").withStyle(ChatFormatting.GRAY));
        // You can also localize the text:
        // tooltip.add(Component.translatable("tooltip.yourmodid.my_item").withStyle(ChatFormatting.GRAY));
    }
}
