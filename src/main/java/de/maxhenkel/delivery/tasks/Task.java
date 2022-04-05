package de.maxhenkel.delivery.tasks;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Task implements INBTSerializable<CompoundTag> {

    private UUID id;
    private String name;
    private String description;
    private String contractorName;
    private String skin;
    private String profession;
    private int minLevel;
    private List<UUID> dependencies;
    private boolean forced;
    private int experience;
    private int money;
    private List<ItemElement> items;
    private List<FluidElement> fluids;
    private List<ItemStack> rewards;

    public Task(UUID id, String name, String description, String contractorName, String skin, String profession, int minLevel, List<UUID> dependencies, boolean forced, int experience, int money, List<ItemElement> items, List<FluidElement> fluids, List<ItemStack> rewards) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.contractorName = contractorName;
        this.skin = skin;
        this.profession = profession;
        this.minLevel = minLevel;
        this.dependencies = dependencies;
        this.forced = forced;
        this.experience = experience;
        this.money = money;
        this.items = items;
        this.fluids = fluids;
        this.rewards = rewards;
    }

    public Task() {
        this.items = new ArrayList<>();
        this.fluids = new ArrayList<>();
        this.rewards = new ArrayList<>();
    }

    public UUID getId() {
        return id;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public List<UUID> getDependencies() {
        return dependencies;
    }

    public int getExperience() {
        return experience;
    }

    public List<FluidElement> getFluids() {
        return fluids;
    }

    public List<ItemElement> getItems() {
        return items;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getContractorName() {
        return contractorName;
    }

    public String getSkin() {
        return skin;
    }

    public String getProfession() {
        return profession;
    }

    public List<ItemStack> getRewards() {
        return rewards;
    }

    public int getMoney() {
        return money;
    }

    public boolean isForced() {
        return forced;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compound = new CompoundTag();
        compound.putUUID("ID", id);
        compound.putString("Name", name);
        compound.putString("Description", description);
        compound.putString("ContractorName", contractorName);
        compound.putString("Skin", skin);
        compound.putString("Profession", profession);
        compound.putBoolean("Forced", forced);
        compound.putInt("MinLevel", minLevel);

        ListTag dependencyList = new ListTag();
        for (UUID dependency : dependencies) {
            dependencyList.add(NbtUtils.createUUID(dependency));
        }
        compound.put("Dependencies", dependencyList);

        compound.putInt("Experience", experience);
        compound.putInt("Money", money);

        ListTag itemList = new ListTag();
        for (ItemElement item : items) {
            itemList.add(item.serializeNBT());
        }
        compound.put("Items", itemList);

        ListTag fluidList = new ListTag();
        for (FluidElement fluid : fluids) {
            fluidList.add(fluid.serializeNBT());
        }
        compound.put("Fluids", fluidList);

        ListTag rewardList = new ListTag();
        for (ItemStack stack : rewards) {
            rewardList.add(stack.save(new CompoundTag()));
        }
        compound.put("Rewards", rewardList);

        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag compound) {
        id = compound.getUUID("ID");
        name = compound.getString("Name");
        description = compound.getString("Description");
        contractorName = compound.getString("ContractorName");
        skin = compound.getString("Skin");
        profession = compound.getString("Profession");
        forced = compound.getBoolean("Forced");
        minLevel = compound.getInt("MinLevel");

        dependencies = new ArrayList<>();
        ListTag dependencyList = compound.getList("Dependencies", Tag.TAG_INT_ARRAY);
        for (Tag inbt : dependencyList) {
            dependencies.add(NbtUtils.loadUUID(inbt));
        }

        experience = compound.getInt("Experience");
        money = compound.getInt("Money");

        items = new ArrayList<>();
        ListTag itemList = compound.getList("Items", 10);
        for (int i = 0; i < itemList.size(); i++) {
            CompoundTag e = itemList.getCompound(i);
            ItemElement item = new ItemElement();
            item.deserializeNBT(e);
            items.add(item);
        }

        fluids = new ArrayList<>();
        ListTag fluidList = compound.getList("Fluids", 10);
        for (int i = 0; i < fluidList.size(); i++) {
            CompoundTag e = fluidList.getCompound(i);
            FluidElement fluid = new FluidElement();
            fluid.deserializeNBT(e);
            fluids.add(fluid);
        }

        rewards = new ArrayList<>();
        ListTag rewardList = compound.getList("Rewards", 10);
        for (int i = 0; i < rewardList.size(); i++) {
            rewards.add(ItemStack.of(rewardList.getCompound(i)));
        }
    }
}
