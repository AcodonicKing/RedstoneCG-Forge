package net.acodonic_king.redstonecg.block.normal.analog;

import net.acodonic_king.redstonecg.block.defaults.DefaultAnalogInteractible2Gate;

public class AnalogBiasBlock extends DefaultAnalogInteractible2Gate {
	public AnalogBiasBlock() {
		super();
	}
	@Override
	public int redstonePowerOperation(int SideAPower, int SideBPower){
		return Math.min(15, SideAPower + SideBPower);
	}
}
