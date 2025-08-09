package net.acodonic_king.redstonecg.init;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab;

public class RedstonecgModTabs {
	public static CreativeModeTab TAB_REDSTONE_CG_CREATIVE_TAB;

	public static void load() {
		TAB_REDSTONE_CG_CREATIVE_TAB = new CreativeModeTab("tab_redstone_cg_creative_tab") {
			@Override
			public ItemStack makeIcon() {
				return new ItemStack(RedstonecgModBlocks.REDCU_CRAFTER.get());
			}

			@Override
			public boolean hasSearchBar() {
				return false;
			}
		};
	}
}
