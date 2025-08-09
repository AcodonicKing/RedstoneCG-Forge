
package net.acodonic_king.redstonecg.block.parallel.hybrid;

import net.acodonic_king.redstonecg.block.defaults.DefaultParallelDigitalInteractableABGate;

public class ParallelNComparatorBlock extends DefaultParallelDigitalInteractableABGate {
	public ParallelNComparatorBlock() {
		super();
	}
	@Override
	public boolean redstoneOutputOperation(int Primary, int Secondary){
		return Primary <= Secondary;
	}
}
