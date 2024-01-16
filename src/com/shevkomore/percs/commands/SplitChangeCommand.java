package com.shevkomore.percs.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.shevkomore.percs.created.SplitPerc;
import com.shevkomore.percs.perc.Perc;
import com.shevkomore.percs.percdata.PercsData;

public class SplitChangeCommand implements CommandExecutor  {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			Perc p = PercsData.PlayerPercMap.get(sender.getName());
			if(p!=null & p instanceof SplitPerc) {
				SplitPerc sp = (SplitPerc)p;
				if(args.length > 0) {
					if(args[0].equalsIgnoreCase("0")) {
						sp.IsStable = true;
						sp.change();
						if(!sp.IsSecond)sp.change(); // double change in order to remove IsChanging state
						sender.sendMessage("Set to stable (first state) mode");
						sender.sendMessage("Saving...");
						sp.SaveToFile();
						return true;
					} else if(args[0].equalsIgnoreCase("1")) {
						sp.IsStable = true;
						sp.change();
						if(sp.IsSecond)sp.change(); // double change in order to remove IsChanging state
						sender.sendMessage("Set to stable (second state) mode");
						sender.sendMessage("Saving...");
						sp.SaveToFile();
						return true;
					} else return false;
				}
				if(sp.IsStable) {
					sender.sendMessage("Returning to unstable state");
					sp.IsStable = false;
					sp.change();
					return true;
				}else {
					if(sp.IsChanging) {
						((SplitPerc)p).change();
						return true;
					}
				}
			}
		}
		return false;
	}

}
