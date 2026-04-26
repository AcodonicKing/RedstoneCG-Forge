package net.acodonic_king.redstonecg.block.normal.analog;

import net.acodonic_king.redstonecg.block.defaults.DefaultAnalogInteractable2TGate;

public class AnalogBiasBlock extends DefaultAnalogInteractable2TGate {
	public AnalogBiasBlock() {
		super();
	}
	@Override
	public int redstonePowerOperation(int[] SidePower){
		int power = 0;
		for(int p: SidePower)
			power += p;
		return Math.min(15, power);
	}
}
