
package net.acodonic_king.redstonecg.item;

import net.acodonic_king.redstonecg.ModLoaderRider;
import net.acodonic_king.redstonecg.block.defaults.MeasurementProvider;
import net.acodonic_king.redstonecg.init.RedstonecgModVersionRides;
import net.acodonic_king.redstonecg.procedures.LittleTools;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.Entity;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class RedCuMeterItem extends Item {
	public RedCuMeterItem() {
		super(RedstonecgModVersionRides.newItemSuper(1));
	}

	@Override
	public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(itemstack, world, entity, slot, selected);
		if (selected) {
			if (entity == null)
				return;
			if (entity instanceof Player _plrCldCheck1 && _plrCldCheck1.getCooldowns().isOnCooldown(itemstack.getItem())) {
				return;
			}
			Level level = RedstonecgModVersionRides.getPlayerLevel(entity);
			if(level.isClientSide())
				return;
			int RayDistance = 1;
			Vec3 eyep = entity.getEyePosition(1f);
			BlockPos LookPos = level.clip(new ClipContext(eyep, eyep.add(entity.getViewVector(1f).scale(RayDistance)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).getBlockPos();
			while (world.getBlockState(LookPos).isAir()) {
			//while (world.getBlockState(LookPos).is(RedstonecgModTags.Blocks.AIRS)) {
				RayDistance++;
				if (RayDistance > 10) {
					ModLoaderRider.itemStackPutBoolean(itemstack, "measured", false);
					return;
				}
				LookPos = level.clip(new ClipContext(eyep, eyep.add(entity.getViewVector(1f).scale(RayDistance)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).getBlockPos();
			}
			BlockState ThisBlock = world.getBlockState(LookPos);
			Block block = ThisBlock.getBlock();
			String measure;
			if(block instanceof MeasurementProvider dw){
				measure = dw.getMeasurement(world, ThisBlock, LookPos);
			} else if (block == Blocks.REDSTONE_TORCH || block == Blocks.REDSTONE_WALL_TORCH){
				measure = String.format("%1d", LittleTools.getBooleanProperty(ThisBlock, "lit") ? 15 : 0);
			} else {
				measure = String.format("%1d", world.getBestNeighborSignal(LookPos));
			}
			if(measure.isEmpty()){
				ModLoaderRider.itemStackPutBoolean(itemstack, "measured", false);
				return;
			}
			//RedstonecgMod.LOGGER.debug("{}",measure);
			ModLoaderRider.itemStackPutBoolean(itemstack, "measured", true);
			ModLoaderRider.itemStackPutString(itemstack, "measuring", measure);
			/*if (ThisBlock.is(RedstonecgModTags.Blocks.REDCUWIRES)) {
				measure = LittleTools.getBlockEntityNBTValue(world, LookPos, "power") / 16;
				if (measure == 0 && itemstack.getOrCreateTag().getDouble("measuring") > 0) {
					return;
				}
			} else if (ThisBlock.is(RedstonecgModTags.Blocks.BOOLEAN_OUTPUT)) {
				if(LittleTools.getBooleanProperty(ThisBlock,"output")){
					measure = 15;
				} else {
                    measure = world.getSignal(LookPos, LittleTools.getDirection(ThisBlock).getOpposite());
                }
			} else if (ThisBlock.is(RedstonecgModTags.Blocks.ANALOG_OUTPUT)) {
				measure = LittleTools.getIntegerProperty(ThisBlock, "power");
			} else {
				measure = world.getBestNeighborSignal(LookPos);
			}
			itemstack.getOrCreateTag().putDouble("measuring", measure);
			itemstack.getOrCreateTag().putBoolean("measured", true);*/
		}
	}
}
