
package net.acodonic_king.redstonecg.block.normal.analog;

import net.acodonic_king.redstonecg.block.defaults.DefaultAnalogInteractable2TGate;

public class AnalogMinBlock extends DefaultAnalogInteractable2TGate {
	public AnalogMinBlock() {
		super();
	}
	@Override
	public int redstonePowerOperation(int[] SidePower){
		int power = 15;
		for(int p: SidePower)
			power = Math.min(power, p);
		return power;
	}
}
