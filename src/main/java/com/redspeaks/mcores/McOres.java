package com.redspeaks.mcores;

import com.redspeaks.mcores.lib.PlayerSelection;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class McOres extends JavaPlugin implements Listener {

    private static McOres instance = null;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        getCommand("mines").setExecutor(new MineCommand());
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static McOres getInstance() {
        return instance;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if(!MineCommand.builderMap.containsKey(e.getPlayer().getUniqueId())) return;
        if(e.getItem() == null) return;
        if(!e.getItem().hasItemMeta()) return;
        if(!e.getItem().getItemMeta().hasDisplayName()) return;
        if(!ChatColor.stripColor(e.getItem().getItemMeta().getDisplayName()).equals("Mine Editor")) return;
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK && e.getAction() != Action.LEFT_CLICK_BLOCK) return;
        if(e.getClickedBlock() == null) return;
        int slot = 0;
        if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            slot = 2;
        }
        if(e.getAction() == Action.LEFT_CLICK_BLOCK) {
            slot = 1;
        }
        if(slot == 0)return;
        PlayerSelection selection = PlayerSelection.get(e.getPlayer());
        e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&e&l(!) &eSuccessfully select point."));
        selection.select(e.getClickedBlock().getLocation(), slot);
        e.setCancelled(true);
    }
}
