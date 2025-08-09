
package net.acodonic_king.redstonecg.block;

import net.acodonic_king.redstonecg.block.defaults.DefaultGate;
import net.minecraft.world.level.block.state.BlockState;

public class SmoothStonePlateBlock extends DefaultGate {
	public SmoothStonePlateBlock() {
		super();
	}
	@Override
	public boolean isSignalSource(BlockState state) {
		return false;
	}
}
