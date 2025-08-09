
package net.acodonic_king.redstonecg.block.parallel.analog;

import net.acodonic_king.redstonecg.block.defaults.DefaultParallelAnalogAGate;

public class ParallelAnalogPassBlock extends DefaultParallelAnalogAGate {
	public ParallelAnalogPassBlock() {
		super();
	}
	@Override
	public int redstonePowerOperation(int LinePower, int BackPower){
		return ((LinePower * BackPower) / 15);
	}
}
