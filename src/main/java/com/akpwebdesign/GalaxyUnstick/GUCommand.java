package com.akpwebdesign.GalaxyUnstick;

import java.util.Iterator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class GUCommand implements CommandExecutor
{

	private GalaxyUnstick plugin;

	public GUCommand(GalaxyUnstick plugin)
	{
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(cmd.getName().equalsIgnoreCase("gu"))
		{
			
			//if there are no arguments (or less than no arguments, just in case. lolwut?)...
			if (args.length <= 0)
			{	
				return false;
			}
			
			String command = "";
			
			if("mode".startsWith(args[0].toLowerCase()))
			{
				command = "mode";
			}
			
			if("addworld".startsWith(args[0].toLowerCase()))
			{
				command = "addworld";
			}
			
			if("removeworld".startsWith(args[0].toLowerCase()))
			{
				command = "removeworld";
			}
			
			if("listworlds".startsWith(args[0].toLowerCase()))
			{
				command = "listworlds";
			}
			
			if("reload".startsWith(args[0].toLowerCase()))
			{
				command = "reload";
			}
			
			switch (command)
			{
				case "mode": 
					return this.modeCommand(args, sender);
				case "addworld": 
					return this.addWorldCommand(args, sender);
				case "removeworld": 
					return this.removeWorldCommand(args, sender);
				case "reload": 
					return this.reloadCommand(sender);
				case "listworlds":
					return this.listWorlds(sender);
				default:
					return false;
			}
			
		}
		return false;
	}

	private boolean reloadCommand(CommandSender sender) {
		
		if(!sender.hasPermission("galaxyunstick.command.gu.reload"))
		{
			sender.sendMessage(ChatColor.RED + "You don't have permission to reload the GalaxyUnstick configuration!");
			return true;
		}
		
		plugin.reloadConfig();
		sender.sendMessage(ChatColor.GREEN + "GalaxyUnstick Configuration Reloaded.");
		return true;
	}
	
	private boolean listWorlds(CommandSender sender) {
		
		if(!sender.hasPermission("galaxyunstick.command.gu.listworlds"))
		{
			sender.sendMessage(ChatColor.RED + "You don't have permission to list the GalaxyUnstick configuration worlds!");
			return true;
		}
		
		//get the list of names from the config
		@SuppressWarnings("unchecked")
		List<String> worldnames = (List<String>) plugin.getConfig().getList("worldlist");
		
		sender.sendMessage(ChatColor.GREEN + "Worlds currently in the GalaxyUnstick configuration:");
		
		//iterate through them
		for(Iterator<String> i = worldnames.iterator(); i.hasNext(); ) {
			
			//move to the next world name.
			String worldname = i.next();
			
			//output the name of the world
			sender.sendMessage(ChatColor.GOLD + worldname);
		}
		
		return true;
	}

	private boolean removeWorldCommand(String[] args, CommandSender sender) {
		
		if(!sender.hasPermission("galaxyunstick.command.gu.removeworld"))
		{
			sender.sendMessage(ChatColor.RED + "You don't have permission to remove worlds from the GalaxyUnstick configuration!");
			return true;
		}
		
		if(args.length != 2)
		{
			sender.sendMessage(ChatColor.RED + "Use \"/gu removeworld <WORLD>\" to remove a world from the list.");
			return true;
		}
		
		//get the list of worlds from the config
		@SuppressWarnings("unchecked")
		List<String> worldnames = (List<String>) plugin.getConfig().getList("worldlist");
		
		//set up a variable to check whether or not we removed a world at the end.
		boolean noworlds = true;
		
		String name = args[1];
		
		//iterate through them
		for(Iterator<String> i = worldnames.iterator(); i.hasNext(); ) {
			
			//move to the next world name.
			String worldname = i.next();
			
			//if worldname == the world we want to remove, remove it.
			if(worldname.equalsIgnoreCase(name))
			{
				i.remove();
				sender.sendMessage(ChatColor.GOLD + worldname + ChatColor.GREEN + " successfully removed from the GalaxyUnstick configuration!");
				noworlds = false;
				plugin.saveConfig();
			}
		}
		
		if(worldnames.isEmpty())
		{
			sender.sendMessage(ChatColor.RED + "There are no worlds left in the configuration file!");
			sender.sendMessage(ChatColor.RED + "Switching to " + ChatColor.GOLD + "ALLWORLDS" + ChatColor.RED + " mode.");
			plugin.getConfig().set("mode", "ALLWORLDS");
			plugin.saveConfig();
		}
		
		if(noworlds)
		{
			sender.sendMessage(ChatColor.RED + "There was no world by the name of " + ChatColor.GOLD + args[1] + ChatColor.RED + " to remove from the GalaxyUnstick Configuration!");
		}
		
		
		return true;
	}

	private boolean addWorldCommand(String[] args, CommandSender sender) {
		
		if(!sender.hasPermission("galaxyunstick.command.gu.addworld"))
		{
			sender.sendMessage(ChatColor.RED + "You don't have permission to add worlds to the GalaxyUnstick configuration!");
			return true;
		}
		
		if(args.length != 2)
		{
			sender.sendMessage(ChatColor.RED + "Use \"/gu addworld <WORLD>\" to add a world to the list.");
			return true;
		}
		
		@SuppressWarnings("unchecked")
		List<String> worldlist = (List<String>) plugin.getConfig().getList("worldlist");
		
		String name = args[1];
		
		boolean alreadyThere = false;
		
		//iterate through them
		for(Iterator<String> i = worldlist.iterator(); i.hasNext(); ) {
			
			//move to the next world name.
			String worldname = i.next();
			
			//if worldname == the world we want to add, make sure we know.
			if(worldname.equalsIgnoreCase(name))
			{
				alreadyThere = true;
			}
		}
		
		//if the world is already there, don't add it. just tell the sender.
		if(alreadyThere)
		{
			
			sender.sendMessage(ChatColor.RED + "That world has already been added to the configuration!");
			return true;
			
		//otherwise, go ahead and add it.
		} else {
			
			worldlist.add(name);
			
		}
		
		plugin.getConfig().set("worldlist", worldlist);
		
		plugin.saveConfig();
		
		sender.sendMessage(ChatColor.GREEN + "Successfully added " + ChatColor.GOLD + args[1] + " to the GalaxyUnstick configuration!");
		return true;
	}

	private boolean modeCommand(String[] args, CommandSender sender) {
		
		if(!sender.hasPermission("galaxyunstick.command.gu.mode"))
		{
			sender.sendMessage(ChatColor.RED + "You don't have permission to change the GalaxyUnstick mode!");
			return true;
		}
		
		if(args.length != 2)
		{
			sender.sendMessage(ChatColor.GREEN + "The current MODE is " + ChatColor.GOLD + plugin.getConfig().getString("mode"));
			sender.sendMessage(ChatColor.RED + "Use \"/gu mode <MODE>\" to set the unstick mode.");
			return true;
		}
		
		String mode = args[1];
		
		if("ALLWORLDS".startsWith(mode.toUpperCase()))
		{
			mode = "ALLWORLDS";
		}
		
		if("CONFIGLIST".startsWith(mode.toUpperCase()))
		{
			mode = "CONFIGLIST";
		}
		
		switch (mode)
		{
		case "ALLWORLDS":
			plugin.getConfig().set("mode", "ALLWORLDS");
			plugin.saveConfig();
			sender.sendMessage(ChatColor.GREEN + "Mode successfully changed to " + ChatColor.GOLD + "ALLWORLDS");
			return true;
		case "CONFIGLIST":
			//get the list of worlds from the config
			@SuppressWarnings("unchecked")
			List<String> worldnames = (List<String>) plugin.getConfig().getList("worldlist");
			
			if(worldnames.isEmpty())
			{
				sender.sendMessage(ChatColor.RED + "There are no worlds in the configuration!");
				sender.sendMessage(ChatColor.RED + "You cannot switch to " + ChatColor.GOLD + "CONFIGLIST" + ChatColor.RED + " mode until you add a world to the list!");
				sender.sendMessage(ChatColor.RED + "Use \"/gu addworld <WORLD>\" to add a world to the list.");
				return true;
			}
			
			plugin.getConfig().set("mode", "CONFIGLIST");
			plugin.saveConfig();
			sender.sendMessage(ChatColor.GREEN + "Mode successfully changed to " + ChatColor.GOLD + "CONFIGLIST");
			return true;
		default:
			sender.sendMessage(ChatColor.GREEN + "Possible modes are:");
			sender.sendMessage(ChatColor.GOLD + "ALLWORLDS");
			sender.sendMessage(ChatColor.GOLD + "CONFIGLIST");
			return true;
		}
		
	}

}
