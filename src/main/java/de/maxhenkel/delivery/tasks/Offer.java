package de.maxhenkel.delivery.tasks;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.UUID;

public class Offer implements INBTSerializable<CompoundNBT> {

    private UUID id;
    private ItemStack item;
    private int price;
    private int levelRequirement;

    public Offer(UUID id, ItemStack item, int price, int levelRequirement) {
        this.id = id;
        this.item = item;
        this.price = price;
        this.levelRequirement = levelRequirement;
    }

    public Offer() {

    }

    public ItemStack getItem() {
        return item;
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
        compound.put("Stack", item.write(new CompoundNBT()));
        compound.putInt("Price", price);
        compound.putInt("LevelRequirement", levelRequirement);
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundNBT compound) {
        id = compound.getUniqueId("ID");
        item = ItemStack.read(compound.getCompound("Stack"));
        price = compound.getInt("Price");
        levelRequirement = compound.getInt("LevelRequirement");
    }
}
