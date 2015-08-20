package fr.TriiNoxYs.RankVoter.commands;

import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import fr.TriiNoxYs.RankVoter.Main;


public class VoteCmds implements CommandExecutor{
    
    private FileConfiguration config = Main.plugin.getConfig();
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if(sender instanceof Player){
            Player p = (Player) sender;
            
            if(label.equalsIgnoreCase("vote")){
                if(args.length == 1){
                    if(args[0].equalsIgnoreCase("oui") || args[0].equalsIgnoreCase("oui")){
                        p.sendMessage("§cUsage: /vote " + args[0] + " <joueur>");
                        return true;
                    }
                    ArrayList<String> ranks = (ArrayList<String>) config.getStringList("ranks");
                    
                    if(ranks.contains(args[0])){
                        if(config.getString("mayor").equalsIgnoreCase(" ")){
                            Bukkit.broadcastMessage(config
                                    .getString("VOTE_BROADCAST")
                                    .replaceAll("%player%", p.getName())
                                    .replaceAll("%rank%", args[0])
                                    .replace('&', '§'));
                            Main.askedRank.put(p, args[0]);
                            Main.votes.put(p, 0); 
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
                                if(Main.alreadyVoted.get(target.getName()) == null || !Main.alreadyVoted.get(target.getName()).contains(p.getName())){
                                    if(Main.alreadyVoted.get(target.getName()) == null)
                                        Main.alreadyVoted.put(target.getName(), new ArrayList<String>());
                                    if(Main.votes.get(p) == null) 
                                        Main.votes.put(p, 0);
                                    
                                    Main.votes.put(target, Main.votes.get(p) + 1);
                                    Main.alreadyVoted.get(target.getName()).add(p.getName());
                                    Bukkit.broadcastMessage(config
                                            .getString("YES_BROADCAST")
                                            .replaceAll("%player%", target.getName())
                                            .replaceAll("%target%", p.getName())
                                            .replaceAll("%rank%", Main.askedRank.get(target))
                                            .replace('&', '§'));
                                    if(Main.votes.get(target) >= config.getInt("Main.votes-requiered")){
                                        Bukkit.broadcastMessage(config
                                                .getString("PLAYER_RANKUP")
                                                .replaceAll("%player%", p.getName())
                                                .replaceAll("%target%", target.getName())
                                                .replaceAll("%rank%", Main.askedRank.get(target))
                                                .replace('&', '§'));
                                        
                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "manuadd " + target.getName() + " " + Main.askedRank.get(target));
                                        
                                        Main.askedRank.put(target, null);
                                        Main.votes.put(target, null);
                                        Main.alreadyVoted.get(target).add(p.getName());
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
                                if(Main.alreadyVoted.get(target.getName()) == null || !Main.alreadyVoted.get(target.getName()).contains(p.getName())){
                                    if(Main.alreadyVoted.get(target.getName()) == null) 
                                        Main.alreadyVoted.put(target.getName(), new ArrayList<String>());
                                    if(Main.votes.get(p) == null) 
                                        Main.votes.put(p, 0);
                                    
                                    Main.votes.put(target, Main.votes.get(p) - 1);
                                    Bukkit.broadcastMessage(config
                                            .getString("NO_BROADCAST")
                                            .replaceAll("%player%", p.getName())
                                            .replaceAll("%target%", target.getName())
                                            .replaceAll("%rank%", Main.askedRank.get(target))
                                            .replace('&', '§'));
                                    
                                    Main.alreadyVoted.get(target).add(p.getName());
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
    
}
