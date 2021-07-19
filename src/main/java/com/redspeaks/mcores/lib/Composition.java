package com.redspeaks.mcores.lib;

import org.bukkit.Material;

public class Composition {

    private final Material type;
    private int count;
    private final int hash;
    public Composition(Material type, int count) {
        this.type = type;
        this.count = count;
        this.hash = type.hashCode() + Integer.hashCode(count);
    }

    public Material getType() {
        return type;
    }

    public void use() {
        this.count -= 1;
    }

    public int getCount() {
        return count;
    }

    public boolean isUsed() {
        return getCount() == 0;
    }

    public int add(Composition composition) {
        return getCount() + composition.getCount();
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(!(o instanceof Composition)) {
            return false;
        }
        return getType() == ((Composition)o).getType();
    }

    @Override
    public int hashCode() {
        return hash;
    }
}
