package de.maxhenkel.delivery.tasks;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public interface ITaskContainer {

    default NonNullList<ItemStack> getItems(ItemStack stack) {
        return NonNullList.create();
    }

    default NonNullList<FluidStack> getFluids(ItemStack stack) {
        return NonNullList.create();
    }

    /**
     * @param stack the task container stack
     * @return if the {@link #add(ItemStack, ItemStack, int)} can be used
     */
    default boolean canAcceptItems(ItemStack stack) {
        return false;
    }

    /**
     * @param stack the task container stack
     * @return if the {@link #add(ItemStack, IFluidHandler, int)} can be used
     */
    default boolean canAcceptFluids(ItemStack stack) {
        return false;
    }

    default ItemStack add(ItemStack stack, ItemStack stackToAdd, int amount) {
        return stackToAdd;
    }

    default int add(ItemStack stack, IFluidHandler handler, int amount) {
        return 0;
    }

    /**
     * Used by the packager to determine if it can be extracted
     *
     * @param stack the task container stack
     * @return if it is fill
     */
    default boolean isFull(ItemStack stack) {
        return false;
    }

}
