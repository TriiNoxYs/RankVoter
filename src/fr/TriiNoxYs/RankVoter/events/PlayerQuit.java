package fr.TriiNoxYs.RankVoter.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import fr.TriiNoxYs.RankVoter.Main;


public class PlayerQuit implements Listener{
    
    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        final Player p = e.getPlayer();
        
        if(Main.plugin.getConfig().getString("mayor").equalsIgnoreCase(p.getName()) && Main.plugin.getConfig().getBoolean("derank-mayor-on-quit")){
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable(){
                
                public void run(){
                    boolean reco = false;
                    for(Player pl : Bukkit.getOnlinePlayers()){
                        if(pl.equals(p)){
                            reco = true;
                            return;
                        }
                    }
                    if(reco == false){
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "manuadd " + p.getName() + " " + Main.plugin.getConfig().getString("default-rank"));
                        Main.plugin.getConfig().set("mayor", "");
                        Bukkit.broadcastMessage(Main.plugin.getConfig().getString("MAYOR_QUIT_DERANKED").replaceAll("%mayor%", p.getName()).replaceAll("%time%", String.valueOf(Main.plugin.getConfig().getInt("mayor-derank-delay"))).replace('&', '§'));
                    }
                }
            }, Main.plugin.getConfig().getInt("mayor-derank-delay")*60*20);
            return;
        }
        
        if(Main.plugin.getConfig().getBoolean("derank-player-on-quit")){
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable(){
                
                public void run(){
                    boolean reco = false;
                    for(Player pl : Bukkit.getOnlinePlayers()){
                        if(pl.equals(p)){
                            reco = true;
                            return;
                        }
                    }
                    if(reco == false){
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "manuadd " + p.getName() + " " + Main.plugin.getConfig().getString("default-rank"));
                        Main.plugin.getConfig().set("mayor", "");
                        Bukkit.broadcastMessage(Main.plugin.getConfig().getString("PLAYER_QUIT_DERANKED").replaceAll("%player%", p.getName()).replaceAll("%time%", String.valueOf(Main.plugin.getConfig().getInt("player-derank-delay"))).replace('&', '§'));
                    }
                }
            }, Main.plugin.getConfig().getInt("player-derank-delay")*60*20);
            return;
        }
        
    }
}
