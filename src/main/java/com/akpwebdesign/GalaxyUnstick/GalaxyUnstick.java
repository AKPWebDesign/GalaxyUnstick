package com.akpwebdesign.GalaxyUnstick;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class GalaxyUnstick extends JavaPlugin implements Listener
{
	protected boolean VNPHook = false;
	
	protected List<UUID> admins = new ArrayList<UUID>();
	
	public void onEnable()
	{		
		//register our commands
		getCommand("unstick").setExecutor(new UnstickCommand(this));
		getCommand("gu").setExecutor(new GUCommand(this));
		
		//register our event listeners
		getServer().getPluginManager().registerEvents(this, this);
		
		boolean firstrun = false;
		
		File f = new File("./" + this.getDataFolder() + "/config.yml");
		
		
		if(!f.exists())
		{
			firstrun = true;
		}

		//save the default config file on first run.
		this.saveDefaultConfig();
		
		if(firstrun)
		{
			this.getLogger().info("First run detected! Setting up default configuration file.");
			//get a list of all the server worlds
			List<World> worlds = Bukkit.getServer().getWorlds();
			
			//set up a list for our world names
			List<String> worldnames = new ArrayList<String>();
			
			//loop through all the worlds in the list and add them to the config.
			for(Iterator<World> i = worlds.iterator(); i.hasNext(); ) {
				
				//move to the next world.
				World world = i.next();
				
				//add the name of the world to our list
				worldnames.add(world.getName());
				
			}
			
			//set worldlist to be the list of worldnames we got.
			this.getConfig().set("worldlist", worldnames);
			
			//remove the firstrun config item
			this.getConfig().set("firstrun", null);
			
			//save the new config
			this.saveConfig();
		}
		
		//if VNP is on the server, set our hook to true, so we know later on.
		if (this.getServer().getPluginManager().isPluginEnabled("VanishNoPacket")) {
			this.getLogger().info("Detected VanishNoPacket. Enabling VNP hook.");
			this.VNPHook = true;
		}
		
		PluginDescriptionFile pdfFile = this.getDescription();
		getLogger().info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
	}
 
	public void onDisable()
	{
		PluginDescriptionFile pdfFile = this.getDescription();
		getLogger().info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is disabled!");
	}
	
	@EventHandler
	public void onPlayerLogin(PlayerJoinEvent event)
	{
		Player plr = event.getPlayer();
		
		if(plr.hasPermission("galaxyunstick.admin")) {
			this.admins.add(plr.getUniqueId());
		}
	}
	
	@EventHandler
	public void onPlayerLogoff(PlayerQuitEvent event)
	{
		this.admins.remove(event.getPlayer().getUniqueId());
	}
	
	public List<UUID> getAdmins() 
	{
		return this.admins;
	}
}
