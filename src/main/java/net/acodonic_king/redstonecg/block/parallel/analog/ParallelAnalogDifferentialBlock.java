
package net.acodonic_king.redstonecg.block.parallel.analog;

import net.acodonic_king.redstonecg.block.defaults.DefaultParallelAnalogAGate;

public class ParallelAnalogDifferentialBlock extends DefaultParallelAnalogAGate {
	public ParallelAnalogDifferentialBlock() {
		super();
	}
	@Override
	public int redstonePowerOperation(int LinePower, int BackPower){
		return Math.abs(LinePower - BackPower);
	}
}
