
package net.acodonic_king.redstonecg.block.parallel.digital;

import net.acodonic_king.redstonecg.block.defaults.DefaultParallelDigitalAGate;

public class ParallelOrBlock extends DefaultParallelDigitalAGate {
	public ParallelOrBlock() {
		super();
	}
	@Override
	public boolean redstoneOutputOperation(int LinePower, int BackPower){
		return (LinePower > 0) || (BackPower > 0);
	}
}
