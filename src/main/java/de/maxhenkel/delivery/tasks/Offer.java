package de.maxhenkel.delivery.tasks;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class Offer implements INBTSerializable<CompoundNBT> {

    private ItemStack item;
    private int price;
    private int levelRequirement;

    public Offer(ItemStack item, int price, int levelRequirement) {
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

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT compound = new CompoundNBT();
        compound.put("Stack", item.write(new CompoundNBT()));
        compound.putInt("Price", price);
        compound.putInt("LevelRequirement", levelRequirement);
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundNBT compound) {
        item = ItemStack.read(compound.getCompound("Stack"));
        price = compound.getInt("Price");
        levelRequirement = compound.getInt("LevelRequirement");
    }
}
