package de.maxhenkel.delivery.tasks;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.UUID;

public class Offer implements INBTSerializable<CompoundNBT> {

    private UUID id;
    private net.minecraft.item.Item item;
    private int amount;
    private int price;
    private int levelRequirement;

    public Offer(UUID id, net.minecraft.item.Item item, int amount, int price, int levelRequirement) {
        this.id = id;
        this.item = item;
        this.amount = amount;
        this.price = price;
        this.levelRequirement = levelRequirement;
    }

    public Offer() {

    }

    public ItemStack getStack() {
        return new ItemStack(item, amount);
    }

    public Item getItem() {
        return item;
    }

    public int getAmount() {
        return amount;
    }

    public int getPrice() {
        return price;
    }

    public int getLevelRequirement() {
        return levelRequirement;
    }

    public UUID getId() {
        return id;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT compound = new CompoundNBT();
        compound.putUniqueId("ID", id);
        ResourceLocation i = ForgeRegistries.ITEMS.getKey(item);
        compound.putString("Item", i.toString());
        compound.putInt("Amount", amount);
        compound.putInt("Price", price);
        compound.putInt("LevelRequirement", levelRequirement);
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundNBT compound) {
        id = compound.getUniqueId("ID");
        item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(compound.getString("Item")));
        amount = compound.getInt("Amount");
        price = compound.getInt("Price");
        levelRequirement = compound.getInt("LevelRequirement");
    }
}
