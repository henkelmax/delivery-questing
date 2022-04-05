package de.maxhenkel.delivery.gui;

import de.maxhenkel.corelib.item.ItemUtils;
import de.maxhenkel.corelib.sound.SoundUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ItemInventory implements Container {

    protected NonNullList<ItemStack> items;
    protected ItemStack stack;

    public ItemInventory(Player player, ItemStack stack, int size) {
        this.stack = stack;

        CompoundTag compound = stack.getOrCreateTag();
        items = NonNullList.withSize(size, ItemStack.EMPTY);
        ItemUtils.readInventory(compound, "Items", items);

        startOpen(player);
    }

    @Override
    public void startOpen(Player player) {
        SoundEvent openSound = getOpenSound();
        if (openSound != null) {
            player.level.playSound(null, player.getX(), player.getY(), player.getZ(), openSound, SoundSource.PLAYERS, 0.5F, SoundUtils.getVariatedPitch(player.level));
        }
    }

    @Override
    public void stopOpen(Player player) {
        SoundEvent closeSound = getCloseSound();
        if (closeSound != null) {
            player.level.playSound(null, player.getX(), player.getY(), player.getZ(), closeSound, SoundSource.PLAYERS, 0.5F, SoundUtils.getVariatedPitch(player.level));
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
        ItemStack itemstack = ContainerHelper.removeItem(items, index, count);
        if (!itemstack.isEmpty()) {
            setChanged();
        }
        return itemstack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        ItemStack stack = ContainerHelper.takeItem(items, index);
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
    public boolean stillValid(Player player) {
        for (InteractionHand hand : InteractionHand.values()) {
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
