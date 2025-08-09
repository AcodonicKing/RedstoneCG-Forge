
package net.acodonic_king.redstonecg.block.normal.analog;

import net.acodonic_king.redstonecg.block.defaults.DefaultAnalogInteractible2Gate;

public class AnalogMaxBlock extends DefaultAnalogInteractible2Gate {
	public AnalogMaxBlock() {
		super();
	}
	@Override
	public int redstonePowerOperation(int SideAPower, int SideBPower){
		return Math.max(SideAPower, SideBPower);
	}
}
