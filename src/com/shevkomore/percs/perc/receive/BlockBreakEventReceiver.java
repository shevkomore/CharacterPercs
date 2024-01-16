package com.shevkomore.percs.perc.receive;

import org.bukkit.event.block.BlockBreakEvent;

public interface BlockBreakEventReceiver {
	public void onBlockBreakEvent(BlockBreakEvent e);
}
