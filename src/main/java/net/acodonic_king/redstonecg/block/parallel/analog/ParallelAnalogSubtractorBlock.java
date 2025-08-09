
package net.acodonic_king.redstonecg.block.parallel.analog;

import net.acodonic_king.redstonecg.block.defaults.DefaultParallelAnalogInteractableABGate;

public class ParallelAnalogSubtractorBlock extends DefaultParallelAnalogInteractableABGate {
	public ParallelAnalogSubtractorBlock() {
		super();
	}
	@Override
	public int redstonePowerOperation(int PrimaryPower, int SecondaryPower){
		return Math.max(PrimaryPower - SecondaryPower, 0);
	}
}
