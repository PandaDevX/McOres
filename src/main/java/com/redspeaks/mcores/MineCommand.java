package com.redspeaks.mcores;

import com.redspeaks.mcores.lib.BlockComposition;
import com.redspeaks.mcores.lib.MineBuilder;
import com.redspeaks.mcores.lib.PlayerSelection;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MineCommand implements CommandExecutor {

    public final static Map<UUID, MineBuilder> builderMap = new HashMap<>();


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission("mcores.admin")) {
            sender.sendMessage(ChatColor.RED + "You must be have no permission to do that.");
            return true;
        }
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to do that.");
            return true;
        }
        if(args.length == 0) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e&l(!) &e/mines builder &7- &fActivate builder mode."));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e&l(!) &e/mines setregion &7- &fTo set the selected region as mine."));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e&l(!) &e/mines composition (block) (percentage) &7- &fActivate builder mode."));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e&l(!) &e/mines reset &7- &fReset the composition."));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e&l(!) &e/mines build &7- &fFinalize."));
            return true;
        }
        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("builder")) {
                if(isInBuilderMode(sender)) {
                    sender.sendMessage(ChatColor.RED + "You are already in builder mode!");
                    return true;
                }
                MineBuilder builder = new MineBuilder((Player) sender);
                builderMap.put(((Player) sender).getUniqueId(), builder);
                sender.sendMessage(ChatColor.GREEN + "You are now in a builder mode!");
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e&l(!) &e/mines setregion &7- &fTo set the selected region as mine."));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e&l(!) &e/mines composition (block) (percentage) &7- &fActivate builder mode."));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e&l(!) &e/mines reset &7- &fReset the composition."));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e&l(!) &e/mines build &7- &fFinalize."));
                return true;
            }
            if(args[0].equalsIgnoreCase("setregion")) {
                if(!isInBuilderMode(sender)) {
                    sender.sendMessage(ChatColor.RED + "You must be in a builder mode to do that!");
                    return true;
                }
                PlayerSelection selection = PlayerSelection.get((Player)sender);
                if(!selection.isComplete()) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e&l(!) Right click for 2nd location and left click for 1st location."));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e&l(!) Use your builder tool to complete the region."));
                    return true;
                }
                builderMap.get(((Player) sender).getUniqueId()).saveSelection();
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e&l(!) Successfully define a region."));
                return true;
            }
            if(args[0].equalsIgnoreCase("reset")) {
                if(!isInBuilderMode(sender)) {
                    sender.sendMessage(ChatColor.RED + "You must be in a builder mode to do that!");
                    return true;
                }
                builderMap.get(((Player) sender).getUniqueId()).reset();
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e(!) &eRegion and compositions are now cleared from the data!"));
                return true;
            }
            if(args[0].equalsIgnoreCase("build")) {
                if(!isInBuilderMode(sender)) {
                    sender.sendMessage(ChatColor.RED + "You must be in a builder mode to do that!");
                    return true;
                }
                if(builderMap.get(((Player) sender).getUniqueId()).prepare()) {
                    sender.sendMessage(ChatColor.GREEN + "Successfully build a new mine!");
                    builderMap.remove(((Player) sender).getUniqueId());
                } else {
                    sender.sendMessage(ChatColor.RED + "Please complete selection of region and compositions.");
                }
                return true;
            }
            if(args[0].equalsIgnoreCase("composition")) {
                if(!isInBuilderMode(sender)) {
                    sender.sendMessage(ChatColor.RED + "You must be in a builder mode to do that!");
                    return true;
                }
                if(builderMap.get(((Player) sender).getUniqueId()).getComposition().isEmpty()) {
                    sender.sendMessage(ChatColor.RED + "Empty composition.");
                    sender.sendMessage(ChatColor.RED + "Use: /mines composition (type) (percentage)");
                } else {
                    sender.sendMessage(ChatColor.GREEN + "Current Composition:");
                    for(BlockComposition composition : builderMap.get(((Player) sender).getUniqueId()).getComposition()) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e&l(!) &eComposition of " + composition.getType()  + " &7 &f" + composition.getPercentage() + "%"));
                    }
                }
                return true;
            }
            return true;
        }
        if(args.length < 3 && args[0].equalsIgnoreCase("composition")) {
            sender.sendMessage(ChatColor.RED + "Use the correct arguments: /mines composition (type) (percentage)");
            return true;
        }
        if(!args[0].equalsIgnoreCase("composition")) {
            sender.sendMessage(ChatColor.RED + "Unknown command");
            return true;
        }
        if(!isInBuilderMode(sender)) {
            sender.sendMessage(ChatColor.RED + "You must be in a builder mode to do that!");
            return true;
        }
        Material type = Material.getMaterial(args[1]);
        int percentage;
        if(type == null) {
            try {
                type = Material.valueOf(args[1].toUpperCase());
            }catch (IllegalArgumentException | NullPointerException e) {
                sender.sendMessage(ChatColor.RED + "Unknown block type!");
                return true;
            }
        }
        if(!type.isBlock()) {
            sender.sendMessage(ChatColor.RED + "You can only select block types.");
            return true;
        }
        try {
            Integer.parseInt(args[2]);
        }catch(NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Please enter correct number format!");
        }
        percentage = Integer.parseInt(args[2]);
        builderMap.get(((Player)sender).getUniqueId()).setComposition(type, percentage);
        sender.sendMessage(ChatColor.GREEN + "" + percentage + "% of " + type.name().toLowerCase() + " has been added to the compositions.");
        sender.sendMessage(ChatColor.GREEN + "Current Composition:");
        for(BlockComposition composition : builderMap.get(((Player) sender).getUniqueId()).getComposition()) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e&l(!) &eComposition of " + composition.getType()  + " &7 &f" + composition.getPercentage() + "%"));
        }
        return false;
    }

    public boolean isInBuilderMode(CommandSender sender) {
        return builderMap.containsKey(((Player)sender).getUniqueId());
    }
}
