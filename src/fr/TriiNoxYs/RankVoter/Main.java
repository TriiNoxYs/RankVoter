package fr.TriiNoxYs.RankVoter;

import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener{
    
    private FileConfiguration config;
    private HashMap<Player, String> askedRank = new HashMap<Player, String>();
    private HashMap<Player, Integer> votes = new HashMap<Player, Integer>();
    
    public void onEnable(){
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
    
    @SuppressWarnings("unchecked")
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if(sender instanceof Player){
            Player p = (Player) sender;
            
            if(label.equalsIgnoreCase("rankvoter")){
                if(args.length == 0){
                    sender.sendMessage("§cUsage: /rankvoter <add | remove | infos> [rank]");
                    return true;
                }
                if(args[0].equalsIgnoreCase("add")){
                    if(args.length == 1){
                        sender.sendMessage("§cUsage: /rankvoter add <rank>");
                        return true;
                    }else{
                        ArrayList<String> ranks = (ArrayList<String>) config.getList("ranks");
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
                        sender.sendMessage("§cUsage: /rankvoter remove <rank>");
                        return true;
                    }else{
                        ArrayList<String> ranks = (ArrayList<String>) config.getList("ranks");
                        if(ranks.contains(args[1])){
                            p.sendMessage(config.getString("RANK_REMOVED")
                                    .replaceAll("%player%", p.getName())
                                    .replaceAll("%rank%", args[1])
                                    .replace('&', '§'));
                            ranks.remove(args[1]);
                            getConfig().set("ranks", ranks);
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
                    sender.sendMessage("");
                    sender.sendMessage("§8-------------------------");
                    sender.sendMessage("§a Développeur: §eTriiNoxYs");
                    sender.sendMessage("§a Plugin: §eRankVoter");
                    sender.sendMessage("§a Version: §e"+ getDescription().getVersion());
                    sender.sendMessage("§8-------------------------");
                }
                return true;
            }else if(label.equalsIgnoreCase("vote")){
                if(args.length == 1){
                    if(args[0].equalsIgnoreCase("oui") || args[0].equalsIgnoreCase("oui")){
                        p.sendMessage("§cUsage: /vote " + args[0] + " <joueur>");
                        return true;
                    }
                    ArrayList<String> ranks = (ArrayList<String>) config.getList("ranks");
                    
                    if(ranks.contains(args[0])){
                        Bukkit.broadcastMessage(config
                                .getString("VOTE_BROADCAST")
                                .replaceAll("%player%", p.getName())
                                .replaceAll("%rank%", args[0])
                                .replace('&', '§'));
                        askedRank.put(p, args[0]);
                        votes.put(p, 0);
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
                            votes.put(p, votes.get(p) + 1);
                            Bukkit.broadcastMessage(config
                                    .getString("YES_BROADCAST")
                                    .replaceAll("%player%", p.getName())
                                    .replaceAll("%target%", target.getName())
                                    .replaceAll("%rank%", askedRank.get(target))
                                    .replace('&', '§'));
                            if(votes.get(p) >= config.getInt("votes-requiered")){
                                Bukkit.dispatchCommand(
                                        Bukkit.getConsoleSender(),"manuadd " + p.getName() + " " + askedRank.get(p));
                                Bukkit.broadcastMessage(config
                                        .getString("PLAYER_RANKUP")
                                        .replaceAll("%player%", p.getName())
                                        .replaceAll("%rank%", askedRank.get(target))
                                        .replace('&', '§'));
                                askedRank.put(p, null);
                                votes.put(p, null);
                            }
                        }else p.sendMessage(config.getString("TARGET_OFFLINE")
                                .replaceAll("%player%", p.getName())
                                .replaceAll("%target%", args[1])
                                .replace('&', '§'));
                    }else if(args[0].equalsIgnoreCase("non")){
                        if(target != null){
                            votes.put(p, votes.get(p) - 1);
                            Bukkit.broadcastMessage(config
                                    .getString("NO_BROADCAST")
                                    .replaceAll("%player%", p.getName())
                                    .replaceAll("%target%", target.getName())
                                    .replaceAll("%rank%", askedRank.get(target))
                                    .replace('&', '§'));
                        }else p.sendMessage(config.getString("TARGET_OFFLINE")
                                .replaceAll("%player%", p.getName())
                                .replaceAll("%target%", args[1])
                                .replace('&', '§'));
                    }else p.sendMessage("§Usage: /vote <oui | non> <joueur>");
                }
            }
        }else sender.sendMessage("You must be a player to perform this command !");
        return false;
    }
    
}
