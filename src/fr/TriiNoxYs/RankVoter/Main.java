package fr.TriiNoxYs.RankVoter;

import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import fr.TriiNoxYs.RankVoter.utils.Updater;

public class Main extends JavaPlugin implements Listener{
    
    public static Main plugin;
    public Updater updater;
    
    private FileConfiguration config;
    public static HashMap<Player, String> askedRank = new HashMap<Player, String>();
    public static HashMap<Player, Integer> votes = new HashMap<Player, Integer>();
    public static HashMap<String, ArrayList<String>> alreadyVoted = new HashMap<String, ArrayList<String>>();
    
    public void onEnable(){

        plugin = this;
        
        updater = new Updater(this);
        Updater.checkUpdate(true);
        
        if (!getDataFolder().exists())
            getDataFolder().mkdir();
        
        saveDefaultConfig();
        config = getConfig();
            
        Bukkit.getPluginManager().registerEvents(this, this);
            
        if(config.getString("votes-requiered").equals(null) || config.getString("votes-requiered") == ""){
            Bukkit.broadcastMessage("§8[&4RANKVOTER§8] §cError in config.yml, please contact Staff.");
            Bukkit.broadcastMessage("§8[&4RANKVOTER§8] §cDisabling RankVoter.");
            Bukkit.getPluginManager().disablePlugin(this);
        }
        
    }
    
    public void onDisable(){
        saveDefaultConfig();
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if(sender instanceof Player){
            Player p = (Player) sender;
            
            if(label.equalsIgnoreCase("rankvoter")){
                if(args.length == 0){
                    p.sendMessage("§cUsage: /rankvoter <add | remove | infos> [rank]");
                    return true;
                }
                if(args[0].equalsIgnoreCase("add")){
                    if(args.length == 1){
                        p.sendMessage("§cUsage: /rankvoter add <rank>");
                        return true;
                    }else{
                        ArrayList<String> ranks = (ArrayList<String>) config.getStringList("ranks");
                        if(ranks.contains(args[1])){
                           p.sendMessage(config.getString("RANK_ALREADY_SAVED")
                                   .replaceAll("%player%", p.getName())
                                   .replaceAll("%rank%", args[1])
                                   .replace('&', '§'));
                           return true;
                        }else{
                            ranks.add(args[1]);
                            p.sendMessage(config.getString("RANK_ADDED")
                                    .replaceAll("%player%", p.getName())
                                    .replaceAll("%rank%", args[1])
                                    .replace('&', '§'));
                            getConfig().set("ranks", ranks);
                            saveConfig();
                            return true;
                        }
                    }
                }
                if(args[0].equalsIgnoreCase("remove")){
                    if(args.length == 1){
                        p.sendMessage("§cUsage: /rankvoter remove <rank>");
                        return true;
                    }else{
                        ArrayList<String> ranks = (ArrayList<String>) config.getStringList("ranks");
                        if(ranks.contains(args[1])){
                            p.sendMessage(config.getString("RANK_REMOVED")
                                    .replaceAll("%player%", p.getName())
                                    .replaceAll("%rank%", args[1])
                                    .replace('&', '§'));
                            ranks.remove(args[1]);
                            getConfig().set("ranks", ranks);
                            saveConfig();
                            return true;
                         }else{
                             p.sendMessage(config.getString("RANK_NOT_FOUND")
                                     .replaceAll("%player%", p.getName())
                                     .replaceAll("%rank%", args[1])
                                     .replace('&', '§'));
                             return true;
                         }
                    }
                }
                if(args[0].equalsIgnoreCase("infos")){
                    p.sendMessage("");
                    p.sendMessage("§8-------------------------");
                    p.sendMessage("§a Développeur: §eTriiNoxYs");
                    p.sendMessage("§a Plugin: §eRankVoter");
                    p.sendMessage("§a Version: §e"+ getDescription().getVersion());
                    p.sendMessage("§8-------------------------");
                }
                return true;
            }else if(label.equalsIgnoreCase("vote")){
                if(args.length == 1){
                    if(args[0].equalsIgnoreCase("oui") || args[0].equalsIgnoreCase("oui")){
                        p.sendMessage("§cUsage: /vote " + args[0] + " <joueur>");
                        return true;
                    }
                    ArrayList<String> ranks = (ArrayList<String>) config.getStringList("ranks");
                    
                    if(ranks.contains(args[0])){
                        if(config.getString("mayor") == ""){
                            Bukkit.broadcastMessage(config
                                    .getString("VOTE_BROADCAST")
                                    .replaceAll("%player%", p.getName())
                                    .replaceAll("%rank%", args[0])
                                    .replace('&', '§'));
                            askedRank.put(p, args[0]);
                            votes.put(p, 0); 
                        }
                        else p.sendMessage(config.getString("MAYOR_ALREADY_EXISTS")
                                .replaceAll("%player%", p.getName())
                                .replaceAll("%mayor%", config.getString("mayor"))
                                .replace('&', '§'));
                    }else{
                        p.sendMessage(config.getString("RANK_NOT_FOUND")
                                .replaceAll("%player%", p.getName())
                                .replaceAll("%rank%", args[0])
                                .replace('&', '§'));
                        return true;
                    }
                }else if(args.length >= 2){
                    Player target = Bukkit.getPlayer(args[1]);
                    
                    if(args[0].equalsIgnoreCase("oui")){
                        if(target != null){
                            if(target != p){
                                if(alreadyVoted.get(target.getName()) == null || !alreadyVoted.get(target.getName()).contains(p.getName())){
                                    if(alreadyVoted.get(target.getName()) == null) 
                                        alreadyVoted.put(target.getName(), new ArrayList<String>());
                                    votes.put(target, votes.get(p) + 1);
                                    alreadyVoted.get(target.getName()).add(p.getName());
                                    Bukkit.broadcastMessage(config
                                            .getString("YES_BROADCAST")
                                            .replaceAll("%player%", p.getName())
                                            .replaceAll("%target%", target.getName())
                                            .replaceAll("%rank%", askedRank.get(target))
                                            .replace('&', '§'));
                                    
                                    if(votes.get(target) >= config.getInt("votes-requiered")){
                                        Bukkit.broadcastMessage(config
                                                .getString("PLAYER_RANKUP")
                                                .replaceAll("%player%", p.getName())
                                                .replaceAll("%target%", p.getName())
                                                .replaceAll("%rank%", askedRank.get(target))
                                                .replace('&', '§'));
                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "manuadd " + target.getName() + " " + askedRank.get(target));
                                        
                                        if(askedRank.get(target).equalsIgnoreCase("Maire")){
                                            getConfig().set("mayor", target.getName());
                                            saveConfig();
                                        }
                                        
                                        askedRank.put(target, null);
                                        votes.put(target, null);
                                        alreadyVoted.put(target.getName(), null);
                                    }
                                }
                                else p.sendMessage(config.getString("ALREADY_VOTED")
                                        .replaceAll("%player%", p.getName())
                                        .replaceAll("%target%", target.getName())
                                        .replace('&', '§'));
                            }
                            else p.sendMessage(config.getString("CANT_VOTE_YOURSELF")
                                    .replaceAll("%player%", p.getName())
                                    .replaceAll("%target%", target.getName())
                                    .replace('&', '§'));
                            
                        }else p.sendMessage(config.getString("TARGET_OFFLINE")
                                .replaceAll("%player%", p.getName())
                                .replaceAll("%target%", args[1])
                                .replace('&', '§'));
                    }else if(args[0].equalsIgnoreCase("non")){
                        if(target != null){
                            if(target != p){
                                if(alreadyVoted.get(target.getName()) == null || !alreadyVoted.get(target.getName()).contains(p.getName())){
                                    if(alreadyVoted.get(target.getName()) == null) 
                                        alreadyVoted.put(target.getName(), new ArrayList<String>());
                                    votes.put(target, votes.get(p) - 1);
                                    Bukkit.broadcastMessage(config
                                            .getString("NO_BROADCAST")
                                            .replaceAll("%player%", p.getName())
                                            .replaceAll("%target%", target.getName())
                                            .replaceAll("%rank%", askedRank.get(target))
                                            .replace('&', '§'));
                                }
                                else p.sendMessage(config.getString("ALREADY_VOTED")
                                        .replaceAll("%player%", p.getName())
                                        .replaceAll("%target%", target.getName())
                                        .replace('&', '§'));
                            }
                            else p.sendMessage(config.getString("CANT_VOTE_YOURSELF")
                                    .replaceAll("%player%", p.getName())
                                    .replaceAll("%target%", target.getName())
                                    .replace('&', '§'));
                            
                        }else p.sendMessage(config.getString("TARGET_OFFLINE")
                                .replaceAll("%player%", p.getName())
                                .replaceAll("%target%", args[1])
                                .replace('&', '§'));
                    }
                    else p.sendMessage("§cUsage: /vote <oui | non> <joueur>");
                }
                else {
                    p.sendMessage("§cUsage: /vote <oui | non> <joueur> OR /vote <grade>");
                }
            }
        }else sender.sendMessage("You must be a player to perform this command !");
        return false;
    }
    
    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        final Player p = e.getPlayer();
        
        if(config.getString("mayor").equalsIgnoreCase(p.getName())){
            getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
                
                public void run(){
                    boolean reco = false;
                    for(Player pl : Bukkit.getOnlinePlayers()){
                        if(pl.equals(p)){
                            reco = true;
                            return;
                        }
                    }
                    if(reco == false){
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "manuadd " + p.getName() + " " + config.getString("default-rank"));
                        getConfig().set("mayor", "");
                        Bukkit.broadcastMessage(config.getString("MAYOR_DERANKED").replaceAll("%mayor%", p.getName()).replaceAll("%time%", String.valueOf(config.getInt("mayor-derank-delay"))).replace('&', '§'));
                    }
                }
            }, config.getInt("mayor-derank-delay")*60*20);
        }
    }
    
}
