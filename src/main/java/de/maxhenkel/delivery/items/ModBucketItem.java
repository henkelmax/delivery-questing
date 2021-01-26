package de.maxhenkel.delivery.items;

import de.maxhenkel.delivery.ModItemGroups;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;

public class ModBucketItem extends BucketItem {

    public ModBucketItem(Fluid containedFluidIn, ResourceLocation registryName) {
        super(() -> containedFluidIn, new Properties().containerItem(Items.BUCKET).maxStackSize(1).group(ModItemGroups.TAB_DELIVERY));
        setRegistryName(registryName);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
        return new FluidBucketWrapper(stack);
    }
}
