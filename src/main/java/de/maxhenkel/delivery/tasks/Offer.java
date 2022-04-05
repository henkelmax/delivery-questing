package de.maxhenkel.delivery.tasks;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.UUID;

public class Offer implements INBTSerializable<CompoundTag> {

    private UUID id;
    private Item item;
    private int amount;
    private int price;
    private int levelRequirement;
    private boolean forEveryMember;

    public Offer(UUID id, Item item, int amount, int price, int levelRequirement, boolean forEveryMember) {
        this.id = id;
        this.item = item;
        this.amount = amount;
        this.price = price;
        this.levelRequirement = levelRequirement;
        this.forEveryMember = forEveryMember;
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

    public boolean isForEveryMember() {
        return forEveryMember;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compound = new CompoundTag();
        compound.putUUID("ID", id);
        ResourceLocation i = ForgeRegistries.ITEMS.getKey(item);
        compound.putString("Item", i.toString());
        compound.putInt("Amount", amount);
        compound.putInt("Price", price);
        compound.putInt("LevelRequirement", levelRequirement);
        compound.putBoolean("ForEveryMember", forEveryMember);
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag compound) {
        id = compound.getUUID("ID");
        item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(compound.getString("Item")));
        amount = compound.getInt("Amount");
        price = compound.getInt("Price");
        levelRequirement = compound.getInt("LevelRequirement");
        forEveryMember = compound.getBoolean("ForEveryMember");
    }
}
