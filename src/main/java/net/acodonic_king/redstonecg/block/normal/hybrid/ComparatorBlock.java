
package net.acodonic_king.redstonecg.block.normal.hybrid;

import net.acodonic_king.redstonecg.block.defaults.DefaultDigitalInteractable2ABGate;

public class ComparatorBlock extends DefaultDigitalInteractable2ABGate {
	public ComparatorBlock() {
		super();
	}
	@Override
	public boolean redstoneOutputOperation(int SideAPower, int SideBPower){
		return SideAPower > SideBPower;
	}
}
