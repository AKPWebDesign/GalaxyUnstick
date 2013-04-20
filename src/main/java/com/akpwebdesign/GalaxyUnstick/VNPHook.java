package com.akpwebdesign.GalaxyUnstick;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.kitteh.vanish.staticaccess.VanishNoPacket;
import org.kitteh.vanish.staticaccess.VanishNotLoadedException;

public class VNPHook {

	public static boolean canSee(Player target, CommandSender sender) {
		
		boolean canSee = false;
		
		try {
			canSee = VanishNoPacket.canSee(target, (Player) sender);
		} catch (VanishNotLoadedException e) {
			canSee = false;
		}
		
		return canSee;
	}
	

}
