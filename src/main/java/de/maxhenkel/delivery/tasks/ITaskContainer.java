package de.maxhenkel.delivery.tasks;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;

public interface ITaskContainer {

    NonNullList<ItemStack> getItems(ItemStack stack);

    NonNullList<FluidStack> getFluids(ItemStack stack);

}
