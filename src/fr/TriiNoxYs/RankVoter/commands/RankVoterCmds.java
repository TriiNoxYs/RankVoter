package fr.TriiNoxYs.RankVoter.commands;

import java.util.ArrayList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import fr.TriiNoxYs.RankVoter.Main;


public class RankVoterCmds implements CommandExecutor{
    
    private FileConfiguration config = Main.plugin.getConfig();
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if(sender instanceof Player){
            Player p = (Player) sender;
            
            if(label.equalsIgnoreCase("rankvoter")){
                if(args.length == 0){
                    p.sendMessage("§cUsage: /rankvoter <add | remove | infos> [rank]");
                }
                else if(args.length >= 1){
                    if(args[0].equalsIgnoreCase("infos")){
                        p.sendMessage("");
                        p.sendMessage("§8-------------------------");
                        p.sendMessage("§a Développeur: §eTriiNoxYs");
                        p.sendMessage("§a Plugin: §eRankVoter");
                        p.sendMessage("§a Version: §e"+ Main.plugin.getDescription().getVersion());
                        p.sendMessage("§8-------------------------");
                    }
                    else if(args[0].equalsIgnoreCase("add")){
                        if(p.hasPermission("rankvoter.add")){
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
                                    Main.plugin.getConfig().set("ranks", ranks);
                                    Main.plugin.saveConfig();
                                    return true;
                                }
                            } 
                        }
                        else p.sendMessage("§cVous ne pouvez pas faire ça.");
                    } 
                    else if(args[0].equalsIgnoreCase("remove")){
                        if(p.hasPermission("rankvoter.remove")){
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
                                    Main.plugin.getConfig().set("ranks", ranks);
                                    Main.plugin.saveConfig();
                                    return true;
                                 }else{
                                     p.sendMessage(config.getString("RANK_NOT_FOUND")
                                             .replaceAll("%player%", p.getName())
                                             .replaceAll("%rank%", args[1])
                                             .replace('&', '§'));
                                     return true;
                                 }
                            }
                        }else p.sendMessage("§cVous ne pouvez pas faire ça.");
                        
                    }
                    return true;
                }
            }
        }else sender.sendMessage("You must be a player to perform this command !");
        return false;
    }
    
}

