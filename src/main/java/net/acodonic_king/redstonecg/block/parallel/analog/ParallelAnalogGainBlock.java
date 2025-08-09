
package net.acodonic_king.redstonecg.block.parallel.analog;

import net.acodonic_king.redstonecg.block.defaults.DefaultParallelAnalogAGate;

public class ParallelAnalogGainBlock extends DefaultParallelAnalogAGate {
	public ParallelAnalogGainBlock() {
		super();
	}
	@Override
	public int redstonePowerOperation(int LinePower, int BackPower){
		return Math.min(LinePower * BackPower, 15);
	}
}
