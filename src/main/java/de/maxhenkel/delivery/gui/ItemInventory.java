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

        openInventory(player);
    }

    @Override
    public void openInventory(PlayerEntity player) {
        SoundEvent openSound = getOpenSound();
        if (openSound != null) {
            player.world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), openSound, SoundCategory.BLOCKS, 0.5F, SoundUtils.getVariatedPitch(player.world));
        }
    }

    @Override
    public void closeInventory(PlayerEntity player) {
        SoundEvent closeSound = getCloseSound();
        if (closeSound != null) {
            player.world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), closeSound, SoundCategory.BLOCKS, 0.5F, SoundUtils.getVariatedPitch(player.world));
        }
    }

    public SoundEvent getOpenSound() {
        return null;
    }

    public SoundEvent getCloseSound() {
        return null;
    }

    @Override
    public int getSizeInventory() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        return items.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return items.get(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack itemstack = ItemStackHelper.getAndSplit(items, index, count);
        if (!itemstack.isEmpty()) {
            markDirty();
        }
        return itemstack;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack stack = ItemStackHelper.getAndRemove(items, index);
        markDirty();
        return stack;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        items.set(index, stack);
        if (stack.getCount() > getInventoryStackLimit()) {
            stack.setCount(getInventoryStackLimit());
        }
        markDirty();
    }

    @Override
    public void markDirty() {
        ItemUtils.saveInventory(stack.getOrCreateTag(), "Items", items);
    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity player) {
        for (Hand hand : Hand.values()) {
            if (player.getHeldItem(hand).equals(stack)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void clear() {
        items.clear();
        markDirty();
    }

}
