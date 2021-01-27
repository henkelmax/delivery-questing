package de.maxhenkel.delivery.tasks;

import java.util.List;
import java.util.UUID;

public class Task {

    private final UUID id;
    private final int minLevel;
    private final int maxLevel;
    private final int experience;
    private final List<Item> items;
    private final List<Fluid> fluids;

    public Task(UUID id, int minLevel, int maxLevel, int experience, List<Item> items, List<Fluid> fluids) {
        this.id = id;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.experience = experience;
        this.items = items;
        this.fluids = fluids;
    }

    public UUID getId() {
        return id;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public int getExperience() {
        return experience;
    }

    public List<Fluid> getFluids() {
        return fluids;
    }

    public List<Item> getItems() {
        return items;
    }

}
