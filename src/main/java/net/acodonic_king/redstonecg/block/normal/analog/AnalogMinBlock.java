
package net.acodonic_king.redstonecg.block.normal.analog;

import net.acodonic_king.redstonecg.block.defaults.DefaultAnalogInteractible2Gate;

public class AnalogMinBlock extends DefaultAnalogInteractible2Gate {
	public AnalogMinBlock() {
		super();
	}
	@Override
	public int redstonePowerOperation(int SideAPower, int SideBPower){
		return Math.min(SideAPower, SideBPower);
	}
}
