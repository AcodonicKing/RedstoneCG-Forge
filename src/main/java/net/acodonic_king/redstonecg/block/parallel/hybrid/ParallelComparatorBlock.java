
package net.acodonic_king.redstonecg.block.parallel.hybrid;

import net.acodonic_king.redstonecg.block.defaults.DefaultParallelDigitalInteractableABGate;

public class ParallelComparatorBlock extends DefaultParallelDigitalInteractableABGate {
	public ParallelComparatorBlock() {
		super();
	}
	@Override
	public boolean redstoneOutputOperation(int Primary, int Secondary){
		return Primary > Secondary;
	}
}
