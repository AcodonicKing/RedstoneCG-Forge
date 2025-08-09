
package net.acodonic_king.redstonecg.block.parallel.analog;

import net.acodonic_king.redstonecg.block.defaults.DefaultParallelAnalogAGate;

public class ParallelAnalogMaxBlock extends DefaultParallelAnalogAGate {
	public ParallelAnalogMaxBlock() {
		super();
	}
	@Override
	public int redstonePowerOperation(int LinePower, int BackPower){
		return Math.max(LinePower, BackPower);
	}
}
