
package net.acodonic_king.redstonecg.block.normal.analog;

import net.acodonic_king.redstonecg.block.defaults.DefaultAnalogInteractable2TGate;

public class AnalogMaxBlock extends DefaultAnalogInteractable2TGate {
	public AnalogMaxBlock() {
		super();
	}
	@Override
	public int redstonePowerOperation(int[] SidePower){
		int power = 0;
		for(int p: SidePower)
			power = Math.max(power, p);
		return power;
	}
}
