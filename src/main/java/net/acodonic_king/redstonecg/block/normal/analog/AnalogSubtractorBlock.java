
package net.acodonic_king.redstonecg.block.normal.analog;

import net.acodonic_king.redstonecg.block.defaults.DefaultAnalogInteractable2ABGate;

public class AnalogSubtractorBlock extends DefaultAnalogInteractable2ABGate {
	public AnalogSubtractorBlock() {
		super();
	}
	@Override
	public int redstonePowerOperation(int SideAPower, int SideBPower){
		return Math.max(SideAPower - SideBPower, 0);
	}
}
