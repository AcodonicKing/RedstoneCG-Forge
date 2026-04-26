
package net.acodonic_king.redstonecg.block.normal.digital;

import net.acodonic_king.redstonecg.block.defaults.DefaultDigitalInteractable2TGate;

public class AndBlock extends DefaultDigitalInteractable2TGate {
	public AndBlock() {
		super();
	}
	@Override
	public boolean redstoneOutputOperation(int[] SidePower){
		boolean output = true;
		for(int p: SidePower)
			output = output && (p > 0);
		return output;
	}
}
