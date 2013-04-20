package com.akpwebdesign.GalaxyUnstick;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnstickCommand implements CommandExecutor
{

	private GalaxyUnstick plugin;

	public UnstickCommand(GalaxyUnstick plugin)
	{
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(cmd.getName().equalsIgnoreCase("unstick"))
		{
			//if there are no arguments (or less than no arguments, just in case. lolwut?)...
			if (args.length <= 0)
			{	
				//...check to see if we're [Server]. if so, we need to know which player
				//to unstick. otherwise, go ahead and unstick the player.
				if (!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.RED + "You must tell me which player to unstick!");
					return true;
				} else {
					
					//if the player has permission to unstick themselves...
					if(sender.hasPermission("galaxyunstick.command.unstick")){
			        	
			        	Player target = (Player) sender;
			        	
			        	//...then now we can do the work of unsticking them.
			        	return unstickPlayer(target, sender);
			        
					}
				}
			}
			
			//if the player does not have permission to unstick others...
			if(!sender.hasPermission("galaxyunstick.command.unstick.others") && (sender instanceof Player)){
	        	
				//...then we should tell them that they can't right away.
				sender.sendMessage(ChatColor.RED + "You don't have permission to unstick other players!");
				return true;
	        
			}
			
			//if we have too many arguments, then we say so. TODO: make this unstick everyone in a list instead.
			if (args.length > 1)
			{
				sender.sendMessage(ChatColor.RED + "Too many arguments!");
				return true;
			}
			
			//if we get here, we must have exactly one argument.
			//now we'll check to see if the target player is logged in.
			Player target = (Bukkit.getServer().getPlayer(args[0]));
			if (target == null) {
				sender.sendMessage(ChatColor.GREEN + args[0] + ChatColor.RED + " is not online!");
				return true;
			}
	        
			//if the player has permission or we are [Server]...
			if(sender.hasPermission("galaxyunstick.command.unstick.others") || !(sender instanceof Player)){
	        	
				//...then now we can do the work of unsticking the player.
				return unstickPlayer(target, sender);
	        
			}
	        
		}
		return true;
	}
	
	public boolean unstickPlayer(Player target, CommandSender sender)
	{
		//get the player's location
		Location loc = target.getLocation().clone();
		
		//get the ceil of the player's Y location.
		double Y = Math.ceil(loc.getY());
		
		//set the new location
		Location newloc = new Location(loc.getWorld(), loc.getX(), Y, loc.getZ(), loc.getYaw(), loc.getPitch());
		
		//get the mode to use from the config
		String mode = plugin.getConfig().getString("mode");
		
		switch (mode) 
		{
		case "ALLWORLDS":
			return this.allWorlds(loc, newloc, target, sender);
		case "CONFIGLIST":
			return this.configList(loc, newloc, target, sender);
		default:
			sender.sendMessage(ChatColor.RED + "This plugin is incorrectly configured!");
			sender.sendMessage(ChatColor.RED + "Setting " + ChatColor.GOLD + "MODE " + ChatColor.RED + "to ALLWORLDS by default.");
			plugin.getConfig().set("mode", "ALLWORLDS");
			return this.allWorlds(loc, newloc, target, sender);
		}

	}

	private boolean configList(Location loc, Location newloc, Player target,
			CommandSender sender) {
		
		//grab a world list from the config
		@SuppressWarnings("unchecked")
		List<String> worldnames = (List<String>) plugin.getConfig().getList("worldlist");
		
		//make a list of actual world objects for us
		List<World> worlds = new ArrayList<World>();
		
		for(Iterator<String> i = worldnames.iterator(); i.hasNext(); ) {
			
			//move to the next world name.
			String worldname = i.next();
			
			//add the world to our teleport list
			worlds.add(Bukkit.getServer().getWorld(worldname));
			
		}
		
		//do the work
		this.theWork(target, worlds, newloc, sender);
		
		return true;
	}

	private boolean allWorlds(Location loc, Location newloc, Player target,
			CommandSender sender) {
		
		//get a list of all worlds on the server.
		List<World> worlds = Bukkit.getServer().getWorlds();
		
		this.theWork(target, worlds, newloc, sender);
		
		return true;
	}
	
	private void theWork(Player target, List<World> worlds, Location newloc,
			CommandSender sender) {
		//send the player through the worlds.
		this.iterateThroughWorlds(target, worlds);
		
		//return the player to where they were.
		target.teleport(newloc);
		
		//let the players know.
		this.notifyUnstick(target, sender);
		
	}
	
	private boolean notifyUnstick(Player target, CommandSender sender) {
		if(sender.getName() == target.getName()){
			sender.sendMessage(ChatColor.AQUA + "You have unstuck yourself!");
			return true;
		}
		
		sender.sendMessage(ChatColor.GREEN + target.getName() + ChatColor.AQUA + " has been unstuck!");
		
		boolean canSee = true;
		
		if(plugin.VNPHook == true && sender.getName() != "CONSOLE"){
			
			if(VNPHook.canSee(target, sender))
			{
				canSee = true;
			}

		}
		
		if(sender.hasPermission("galaxyunstick.notify")){
			if(sender.getName() == "CONSOLE" || canSee == false){
				target.sendMessage(ChatColor.AQUA + "You have been unstuck by: " + ChatColor.LIGHT_PURPLE + "[Server]");
			}else{
				target.sendMessage(ChatColor.AQUA + "You have been unstuck by: " + ChatColor.GREEN + sender.getName());
			}
		}
		
		return true;
	}

	//just to send the player through the list of worlds
	private void iterateThroughWorlds(Player target, List<World> worlds) {
		//loop through all the worlds in the list and teleport the player to the spawn point of each one.
		for(Iterator<World> i = worlds.iterator(); i.hasNext(); ) {
			
			//move to the next world.
			World world = i.next();
			  
			//teleport the player to it's spawn.
			target.teleport(world.getSpawnLocation());
			
		}
	}

}
