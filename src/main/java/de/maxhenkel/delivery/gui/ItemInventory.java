package de.maxhenkel.delivery.gui;

import de.maxhenkel.corelib.item.ItemUtils;
import de.maxhenkel.corelib.sound.SoundUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public class ItemInventory implements IInventory {

    protected NonNullList<ItemStack> items;
    protected ItemStack stack;

    public ItemInventory(PlayerEntity player, ItemStack stack, int size) {
        this.stack = stack;

        CompoundNBT compound = stack.getOrCreateTag();
        items = NonNullList.withSize(size, ItemStack.EMPTY);
        ItemUtils.readInventory(compound, "Items", items);

        startOpen(player);
    }

    @Override
    public void startOpen(PlayerEntity player) {
        SoundEvent openSound = getOpenSound();
        if (openSound != null) {
            player.level.playSound(null, player.getX(), player.getY(), player.getZ(), openSound, SoundCategory.PLAYERS, 0.5F, SoundUtils.getVariatedPitch(player.level));
        }
    }

    @Override
    public void stopOpen(PlayerEntity player) {
        SoundEvent closeSound = getCloseSound();
        if (closeSound != null) {
            player.level.playSound(null, player.getX(), player.getY(), player.getZ(), closeSound, SoundCategory.PLAYERS, 0.5F, SoundUtils.getVariatedPitch(player.level));
        }
    }

    public SoundEvent getOpenSound() {
        return null;
    }

    public SoundEvent getCloseSound() {
        return null;
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        return items.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int index) {
        return items.get(index);
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        ItemStack itemstack = ItemStackHelper.removeItem(items, index, count);
        if (!itemstack.isEmpty()) {
            setChanged();
        }
        return itemstack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        ItemStack stack = ItemStackHelper.takeItem(items, index);
        setChanged();
        return stack;
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        items.set(index, stack);
        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
        setChanged();
    }

    @Override
    public void setChanged() {
        ItemUtils.saveInventory(stack.getOrCreateTag(), "Items", items);
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        for (Hand hand : Hand.values()) {
            if (player.getItemInHand(hand).equals(stack)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void clearContent() {
        items.clear();
        setChanged();
    }

}
