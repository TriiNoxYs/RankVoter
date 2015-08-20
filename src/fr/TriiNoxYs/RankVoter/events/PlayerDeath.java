package fr.TriiNoxYs.RankVoter.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import fr.TriiNoxYs.RankVoter.Main;


public class PlayerDeath implements Listener{
    
    @EventHandler
    public void onDeath(PlayerDeathEvent e){
        Player p = e.getEntity();
        
        if(Main.plugin.getConfig().getString("mayor").equalsIgnoreCase(p.getName()) && Main.plugin.getConfig().getBoolean("derank-mayor-on-death")){
            Main.plugin.getConfig().set("mayor", " ");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "manuadd " + p.getName() + " " + Main.plugin.getConfig().getString("default-rank"));
            Bukkit.broadcastMessage(Main.plugin.getConfig().getString("MAYOR_DEATH_DERANKED").replaceAll("%mayor%", p.getName()).replace('&', '§'));
            return;
        }
        if(Main.plugin.getConfig().getBoolean("derank-player-on-death")){
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "manuadd " + p.getName() + " " + Main.plugin.getConfig().getString("default-rank"));
            Bukkit.broadcastMessage(Main.plugin.getConfig().getString("PLAYER_DEATH_DERANKED").replaceAll("%player%", p.getName()).replace('&', '§'));
        }
    }
    
}
