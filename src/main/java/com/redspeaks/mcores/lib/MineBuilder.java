package com.redspeaks.mcores.lib;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MineBuilder implements Listener {

    private final PlayerSelection playerSelection;
    private Cuboid cuboid;
    private final List<BlockComposition> blockComposition;
    private final Random random;
    public MineBuilder(Player builder) {
        this.playerSelection = PlayerSelection.get(builder);

        builder.getInventory().addItem(build("&a&lMine Editor", Material.WOOD_AXE));
        builder.updateInventory();
        this.blockComposition = new ArrayList<>();
        this.random = new Random();
    }

    public void saveSelection() {
        if(!playerSelection.isComplete()) return;
        this.cuboid = new Cuboid(playerSelection.getLocations()[0], playerSelection.getLocations()[1]);
    }

    public void addComposition(Material type, int percentage)  {
        if(!type.isBlock()) return;
        if(!blockComposition.isEmpty()) {
            int totalPercentage = 0;
            for (BlockComposition composition : blockComposition) {
                totalPercentage += composition.getPercentage();
            }
            if((totalPercentage + percentage) > 100) {
                percentage -= ((totalPercentage + percentage) - 100);
            }
            boolean added = false;
            for (BlockComposition composition : blockComposition) {
                if (type == composition.getType()) {
                    added = true;
                    composition.setPercentage(composition.getPercentage() + percentage);
                }
            }
            if(!added) {
                blockComposition.add(new BlockComposition(type, percentage));
            }
            return;
        }
        blockComposition.add(new BlockComposition(type, percentage));
    }

    public void reset() {
        resetComposition();
        this.cuboid = null;
    }

    public void setComposition(Material type, int percentage)  {
        if(!type.isBlock()) return;
        if(!blockComposition.isEmpty()) {
            int totalPercentage = 0;
            for (BlockComposition composition : blockComposition) {
                if(composition.getType() == type) continue;
                totalPercentage += composition.getPercentage();
            }
            if((totalPercentage + percentage) > 100) {
                percentage -= ((totalPercentage + percentage) - 100);
            }
            boolean added = false;
            for (BlockComposition composition : blockComposition) {
                if (type == composition.getType()) {
                    added = true;
                    composition.setPercentage(percentage);
                }
            }
            if(!added) {
                blockComposition.add(new BlockComposition(type, percentage));
            }
            return;
        }
        blockComposition.add(new BlockComposition(type, percentage));
    }

    public boolean prepare() {
        if(getSelection() == null) return false;
        int totalPercentage = 0;
        if(blockComposition.isEmpty()) return false;
        for(BlockComposition composition : blockComposition) {
            totalPercentage += composition.getPercentage();
        }
        if(totalPercentage < 100) {
            blockComposition.add(new BlockComposition(Material.STONE, 100 - totalPercentage));
        }
        if(getSelection() == null) {
            return false;
        }
        scatterBlock();
        playerSelection.getPlayer().getInventory().removeItem(build("&a&lMine Editor", Material.WOOD_AXE));
        playerSelection.getPlayer().updateInventory();
        return true;
    }

    private Random getRandom() {
        return random;
    }

    public void scatterBlock() {
        final List<Composition> compositionList = new ArrayList<>(blockComposition.size());
        blockComposition.forEach(c -> compositionList.add(new Composition(c.getType(), percentageValue(getSelection().getBlocksCount(), c.getPercentage()))));
        getSelection().scatterBlocks(compositionList);
    }

    public List<BlockComposition> getComposition() {
        return blockComposition;
    }

    public void resetComposition() {
        blockComposition.clear();
    }

    private Cuboid getSelection() {
        return cuboid;
    }

    private ItemStack build(String name, Material type) {
        ItemStack stack = new ItemStack(type);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        stack.setItemMeta(meta);
        return stack;
    }

    private int percentageValue(int baseValue, int percentage) {
        double value = ((double)baseValue) * ((double) percentage / 100);
        return (int)value;
    }
}
