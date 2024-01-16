package com.shevkomore.percs;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.shevkomore.percs.perc.Perc;
import com.shevkomore.percs.percdata.*;

public class PercCommand implements CommandExecutor {
	Plugin plugin;
	public PercCommand(Plugin p) {
		plugin = p;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(args.length == 0) {
			((Player) sender).sendTitle("CharacterPercs", "by ShevkoMore_", 20, 41, 50);
			//TODO
			return true;
		}
		if(args[0].equalsIgnoreCase("set")) {
			String name;
			if(args.length < 3) {
				sender.sendMessage("Using sender as target");
				if(sender instanceof Player)
					name = ((Player) sender).getName();
				else {
				sender.sendMessage("Player must be target here; got " + sender.toString() + "instead.");
				return false;
				}
			} else name = args[2];
			Perc perc = PercsData.generatePerk(args[1], plugin, name);
			if(perc == null) {
				return false;
			}
			sender.sendMessage(name + " is now " + perc.getDisplayName());
			return true;
		} 
		else if (args[0].equalsIgnoreCase("list")) {
			sender.sendMessage("List of players with percs:");
			for(String p : PercsData.getPlayerPercList())
				sender.sendMessage(p);
			return true;
		} 
		else if (args[0].equalsIgnoreCase("load")) {
			sender.sendMessage("Loading data from file...");
			PercsData.LoadPlayerMapFromFile(plugin);
			sender.sendMessage("Loading finished");
			return true;
		} 
		else if (args[0].equalsIgnoreCase("remove")) {
			String name;
			if(args.length < 2) {
				sender.sendMessage("Using sender as target");
				if(sender instanceof Player)
					name = ((Player) sender).getName();
				else {
				sender.sendMessage("Player must be target here; got " + sender.toString() + "instead.");
				return false;
				}
			} else name = args[2];
			PercsData.deletePerc(plugin, name);
			sender.sendMessage(name+" perc data is deleted");
			return true;
		}
		return false;
	}
}
