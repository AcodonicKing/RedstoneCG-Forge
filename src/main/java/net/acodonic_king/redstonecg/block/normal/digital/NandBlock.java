
package net.acodonic_king.redstonecg.block.normal.digital;

import net.acodonic_king.redstonecg.block.defaults.DefaultDigitalInteractable2TGate;

public class NandBlock extends DefaultDigitalInteractable2TGate {
	public NandBlock() {
		super();
	}
	@Override
	public boolean redstoneOutputOperation(int[] SidePower){
		boolean output = false;
		for(int p: SidePower)
			output = output || (p == 0);
		return output;
	}
}
