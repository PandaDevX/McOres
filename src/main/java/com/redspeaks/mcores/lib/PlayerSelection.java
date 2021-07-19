package com.redspeaks.mcores.lib;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerSelection {

    private static final Map<UUID, PlayerSelection> selectionMap = new HashMap<>();

    public static PlayerSelection get(Player player) {
        if(selectionMap.containsKey(player.getUniqueId())) {
            return selectionMap.get(player.getUniqueId());
        }
        PlayerSelection selection = new PlayerSelection(player);
        selectionMap.put(player.getUniqueId(), selection);
        return selection;
    }


    private final Player player;
    private Location point1, point2;
    PlayerSelection(Player player) {
        this.point1 = null;
        this.point2 = null;
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public void select(Location location, int point) {
        if(point > 2) {
            point = 2;
        }
        if(point < 1) {
            point = 1;
        }
        if(point == 1) {
            this.point1 = location;
        } else {
            this.point2 = location;
        }
    }

    public Location[] getLocations() {
        return new Location[] {this.point1, this.point2};
    }

    public void clearSelection() {
        selectionMap.remove(getPlayer().getUniqueId());
    }

    public boolean isComplete() {
        if(point1 != null) {
            return true;
        }
        return point2 != null;
    }

    public static void onDisable() {
        selectionMap.clear();
    }
}
