
package net.acodonic_king.redstonecg.block.normal.digital;

import net.acodonic_king.redstonecg.block.defaults.DefaultDigitalInteractable2TGate;

public class OrBlock extends DefaultDigitalInteractable2TGate {
	public OrBlock() {
		super();
	}
	@Override
	public boolean redstoneOutputOperation(int[] SidePower){
		boolean output = false;
		for(int p: SidePower)
			output = output || (p > 0);
		return output;
	}
}
