
package net.acodonic_king.redstonecg.block.normal.digital;

import net.acodonic_king.redstonecg.block.defaults.DefaultDigitalInteractable2Gate;

public class AndBlock extends DefaultDigitalInteractable2Gate {
	public AndBlock() {
		super();
	}
	@Override
	public boolean redstoneOutputOperation(int SideAPower, int SideBPower){
		return (SideAPower > 0) && (SideBPower > 0);
	}
}
