package com.akpwebdesign.GalaxyUnstick;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class GUCommand implements CommandExecutor, TabCompleter
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
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) 
	{
		
		//set up new List for our autocompletes
		List<String> autoComplete = new ArrayList<String>();
		
		//if there are no arguments, we can assume that the commands need to be output.
		if (args[0].equals(""))
		{
			//add the commands to the list
			autoComplete.add("mode");
			autoComplete.add("addworld");
			autoComplete.add("removeworld");
			autoComplete.add("listworlds");
			autoComplete.add("reload");
			
			//return the list
			return autoComplete;
		}
		
		if(args[0].equals("reload")|| args[0].equals("listworlds"))
		{
			return autoComplete;
		}
		
		if(args[0].equals("addworld"))
		{
			//grab all the worlds on the server
			List<World> worlds = (List<World>) Bukkit.getWorlds();
			
			//iterate through all the worlds on the server
			for(Iterator<World> i = worlds.iterator(); i.hasNext(); ) {
				
				//add the world's name to our list
				autoComplete.add(i.next().getName());
			}
			
			return autoComplete;
		}
		
		if(args[0].equals("removeworld"))
		{
			@SuppressWarnings("unchecked")
			List<String> worldnames = (List<String>) plugin.getConfig().getList("worldlist");
			
			//iterate through all the worlds on the server
			for(Iterator<String> i = worldnames.iterator(); i.hasNext(); ) {
				
				//add the world's name to our list
				autoComplete.add(i.next());
			}
			
			return autoComplete;
		}
		
		if(args[0].equals("mode"))
		{
			
			//add the modes to the list
			autoComplete.add("ALLWORLDS");
			autoComplete.add("CONFIGLIST");
			
			//return the list
			return autoComplete;
		}
		
		if("mode".startsWith(args[0].toLowerCase()))
		{
			autoComplete.add("mode");
			return autoComplete;
		}
		
		if("addworld".startsWith(args[0].toLowerCase()))
		{
			autoComplete.add("addworld");
			return autoComplete;
		}
		
		if("reload".startsWith(args[0].toLowerCase()))
		{
			autoComplete.add("reload");
			return autoComplete;
		}
		
		if("removeworld".startsWith(args[0].toLowerCase()))
		{
			autoComplete.add("removeworld");
			return autoComplete;
		}
		
		if("listworlds".startsWith(args[0].toLowerCase()))
		{
			autoComplete.add("listworlds");
			return autoComplete;
		}
		
		return autoComplete;
	}

	private boolean reloadCommand(CommandSender sender) {
		
		if(!sender.hasPermission("galaxyunstick.command.gu.reload"))
		{
			sender.sendMessage(ChatColor.RED + "You don't have permission to reload the GalaxyUnstick configuration!");
			return true;
		}
		
		plugin.reloadConfig();
		sender.sendMessage(ChatColor.GREEN + "GalaxyUnstick configuration Reloaded.");
		return true;
	}
	
	private boolean listWorlds(CommandSender sender) {
		
		if(!sender.hasPermission("galaxyunstick.command.gu.listworlds"))
		{
			sender.sendMessage(ChatColor.RED + "You don't have permission to list the worlds in the GalaxyUnstick configuration!");
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
	
	//TODO: make this accept multiple worlds as a list.
	private boolean removeWorldCommand(String[] args, CommandSender sender) {
		
		if(!sender.hasPermission("galaxyunstick.command.gu.removeworld"))
		{
			sender.sendMessage(ChatColor.RED + "You don't have permission to remove worlds from the GalaxyUnstick configuration!");
			return true;
		}
		
		if(args.length != 2)
		{
			sender.sendMessage(ChatColor.RED + "Use \"/gu removeworld <WORLD>\" to remove a world from the GalaxyUnstick configuration.");
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
			sender.sendMessage(ChatColor.RED + "There are no worlds left in the GalaxyUnstick configuration!");
			sender.sendMessage(ChatColor.RED + "Switching to " + ChatColor.GOLD + "ALLWORLDS" + ChatColor.RED + " mode.");
			plugin.getConfig().set("mode", "ALLWORLDS");
			plugin.saveConfig();
		}
		
		if(noworlds)
		{
			sender.sendMessage(ChatColor.RED + "There was no world by the name of " + ChatColor.GOLD + args[1] + ChatColor.RED + " to remove from the GalaxyUnstick configuration!");
		}
		
		
		return true;
	}

	//TODO: make this accept multiple worlds as a list.
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
		
		List<String> names = new ArrayList<String>();
		
		//if the argument isn't 'addworld', then we add it to our list.
		for (String arg : args) {
			if(!arg.equalsIgnoreCase("addworld"))
			{
				names.add(arg);
			}
		}
		
		for(Iterator<String> name = names.iterator(); name.hasNext(); )
		{
			boolean alreadyThere = false;
			
			//check if world name already exists in config
			for(Iterator<String> i = worldlist.iterator(); i.hasNext(); ) {
				
				//move to the next world name.
				String worldname = i.next();
				
				//if worldname == the world we want to add, make sure we know.
				if(worldname.equalsIgnoreCase(name.next()))
				{
					alreadyThere = true;
				}
			}
			
			//if the world is already there, don't add it. just tell the sender.
			if(alreadyThere)
			{
				
				sender.sendMessage(ChatColor.RED + "The world " + ChatColor.GOLD + name.toString() + ChatColor.RED + " is already in the GalaxyUnstick configuration!");
				return true;
				
			}
			
			//otherwise, go on to the next step.

			List<World> worlds = (List<World>) Bukkit.getWorlds();
			
			boolean worldOnServer = false;
			
			//check if world name exists on server.
			for(Iterator<World> i = worlds.iterator(); i.hasNext(); ) {
				
				//move to the next world name.
				String worldname = i.next().getName();
				
				//if worldname == the world we want to add, make sure we know.
				if(worldname.equalsIgnoreCase(name.next()))
				{
					worldOnServer = true;
				}
			}
			
			//if the world is on the server, add it.
			if(worldOnServer)
			{
				worldlist.add(name.toString());				
				sender.sendMessage(ChatColor.GREEN + "Successfully added " + ChatColor.GOLD + name + ChatColor.GREEN + " to the GalaxyUnstick configuration!");
				
						
			//otherwise, notify the sender that the world doesn't exist and return.
			} else {
				sender.sendMessage(ChatColor.RED + "The world " + ChatColor.GOLD + name + ChatColor.RED + " does not exist on the server!");
			}
			
		}
		
		//put the updated world list into the config
		plugin.getConfig().set("worldlist", worldlist);
		
		//save the running config 
		plugin.saveConfig();
				
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
				sender.sendMessage(ChatColor.RED + "You cannot switch to " + ChatColor.GOLD + "CONFIGLIST" + ChatColor.RED + " mode until you add a world to the GalaxyUnstick configuration!");
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
