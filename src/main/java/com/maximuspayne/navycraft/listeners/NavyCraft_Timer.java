package com.maximuspayne.navycraft.listeners;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.maximuspayne.navycraft.ConfigManager;
import com.maximuspayne.navycraft.craft.Craft;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class NavyCraft_Timer extends BukkitRunnable {
	
	public static WorldGuardPlugin wgp;
	public static ConfigManager cfgm;
	Plugin plugin;
	Timer timer;
	Craft craft;
	Player player;
	//public String state = "";
	public static HashMap<Player, NavyCraft_Timer> playerTimers = new HashMap<Player, NavyCraft_Timer>();
	public static HashMap<Craft, NavyCraft_Timer> takeoverTimers = new HashMap<Craft, NavyCraft_Timer>();
	public static HashMap<Craft, NavyCraft_Timer> abandonTimers = new HashMap<Craft, NavyCraft_Timer>();
	public static PermissionsEx pex;

	public NavyCraft_Timer(Plugin plug, int seconds, Craft vehicle, Player p, String state, boolean forward) {
		//toolkit = Toolkit.getDefaultToolkit();
		plugin = plug;
		this.craft = vehicle;
		this.player = p;
		timer = new Timer();
		if(state.equals("engineCheck"))
			timer.scheduleAtFixedRate(new EngineTask(), 1000, 1000);
		else if(state.equals("abandonCheck"))
			timer.scheduleAtFixedRate(new ReleaseTask(), seconds * 1000, 60000);
		else if(state.equals("takeoverCheck"))
			timer.scheduleAtFixedRate(new TakeoverTask(), seconds * 1000, 60000);
		else if(state.equals("takeoverCaptainCheck"))
			timer.scheduleAtFixedRate(new TakeoverCaptainTask(), seconds * 1000, 60000);
	}
	
	public void Destroy() {
		timer.cancel();
		craft = null;
	}
	
	class EngineTask extends TimerTask {
		public void run() {
			if(craft == null)
				timer.cancel();
			else
				craft.engineTick();
			return;
		}
	}

	class ReleaseTask extends TimerTask {
		public void run() {			
				if(craft != null && craft.isNameOnBoard.containsKey(player.getName()) ) {
					if( !craft.isNameOnBoard.get(player.getName()) )
						releaseCraftSync();
					
				}
				timer.cancel();
				return;
		}
	}
	
   public void releaseCraftSync()
    {
    	plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
		    public void run()
		    {
		    }
    	}
    	);
	 }
   
	class TakeoverTask extends TimerTask {
		public void run() {	
				takeoverCraftSync();
					

				timer.cancel();
				return;
		}
	}
	
   public void takeoverCraftSync()
    {
    	plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
		    public void run()
		    {
		    	if( craft.abandoned && player != null && player.isOnline() && craft.isOnCraft(player, false) )
		    	{
		    		craft.releaseCraft();
		    		player.sendMessage(ChatColor.GREEN + "Vehicle released! Take command.");
		    	}else
		    	{
		    		player.sendMessage(ChatColor.RED + "Takeover failed.");
		    	}
		    	
		    	
		    }
    	}
    	);
	 }
   
	class TakeoverCaptainTask extends TimerTask {
		public void run() {				

				takeoverCaptainCraftSync();
					

				timer.cancel();
				return;
		}
	}
	
   public void takeoverCaptainCraftSync()
    {
    	plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
		    public void run()
		    {
		    	if( craft.captainAbandoned && player != null && player.isOnline() && craft.isOnCraft(player, false) )
		    	{
		    		craft.releaseCraft();
		    		player.sendMessage(ChatColor.GREEN + "Vehicle released! Take command.");
		    	}else
		    	{
		    		player.sendMessage(ChatColor.RED + "Takeover failed.");
		    	}
		    	
		    	
		    }
    	}
    	);
    }
@Override
public void run() {
}
}
