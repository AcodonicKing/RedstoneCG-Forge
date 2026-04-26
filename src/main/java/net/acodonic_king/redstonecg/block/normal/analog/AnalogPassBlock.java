
package net.acodonic_king.redstonecg.block.normal.analog;

import net.acodonic_king.redstonecg.block.defaults.DefaultAnalogInteractable2TGate;

public class AnalogPassBlock extends DefaultAnalogInteractable2TGate {
	public AnalogPassBlock() {
		super();
	}
	@Override
	public int redstonePowerOperation(int[] SidePower){
		int power = 1;
		for(int p: SidePower)
			power *= p;
		power /= (int) Math.pow(15, SidePower.length - 1);
		return power;
	}
}
