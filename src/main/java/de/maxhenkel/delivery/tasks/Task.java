package de.maxhenkel.delivery.tasks;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Task implements INBTSerializable<CompoundNBT> {

    private UUID id;
    private String name;
    private String description;
    private String contractorName;
    private String skin;
    private String profession;
    private int minLevel;
    private int maxLevel;
    private int experience;
    private List<Item> items;
    private List<Fluid> fluids;
    private List<ItemStack> rewards;

    public Task(UUID id, String name, String description, String contractorName, String skin, String profession, int minLevel, int maxLevel, int experience, List<Item> items, List<Fluid> fluids, List<ItemStack> rewards) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.contractorName = contractorName;
        this.skin = skin;
        this.profession = profession;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.experience = experience;
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

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT compound = new CompoundNBT();
        compound.putUniqueId("ID", id);
        compound.putString("Name", name);
        compound.putString("Description", description);
        compound.putString("ContractorName", contractorName);
        compound.putString("Skin", skin);
        compound.putString("Profession", profession);
        compound.putInt("MinLevel", minLevel);
        compound.putInt("MaxLevel", maxLevel);
        compound.putInt("Experience", experience);

        ListNBT itemList = new ListNBT();
        for (Item item : items) {
            itemList.add(item.serializeNBT());
        }
        compound.put("Items", itemList);

        ListNBT fluidList = new ListNBT();
        for (Fluid fluid : fluids) {
            fluidList.add(fluid.serializeNBT());
        }
        compound.put("Fluids", fluidList);

        ListNBT rewardList = new ListNBT();
        for (ItemStack stack : rewards) {
            rewardList.add(stack.write(new CompoundNBT()));
        }
        compound.put("Rewards", rewardList);

        return compound;
    }

    @Override
    public void deserializeNBT(CompoundNBT compound) {
        id = compound.getUniqueId("ID");
        name = compound.getString("Name");
        description = compound.getString("Description");
        contractorName = compound.getString("ContractorName");
        skin = compound.getString("Skin");
        profession = compound.getString("Profession");
        minLevel = compound.getInt("MinLevel");
        maxLevel = compound.getInt("MaxLevel");
        experience = compound.getInt("Experience");

        items = new ArrayList<>();
        ListNBT itemList = compound.getList("Items", 10);
        for (int i = 0; i < itemList.size(); i++) {
            CompoundNBT e = itemList.getCompound(i);
            Item item = new Item();
            item.deserializeNBT(e);
            items.add(item);
        }

        fluids = new ArrayList<>();
        ListNBT fluidList = compound.getList("Fluids", 10);
        for (int i = 0; i < fluidList.size(); i++) {
            CompoundNBT e = fluidList.getCompound(i);
            Fluid fluid = new Fluid();
            fluid.deserializeNBT(e);
            fluids.add(fluid);
        }

        rewards = new ArrayList<>();
        ListNBT rewardList = compound.getList("Rewards", 10);
        for (int i = 0; i < rewardList.size(); i++) {
            rewards.add(ItemStack.read(rewardList.getCompound(i)));
        }
    }
}
