package de.maxhenkel.delivery.items;

import de.maxhenkel.delivery.ModItemGroups;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;

public class ModBucketItem extends BucketItem {

    public ModBucketItem(Fluid containedFluidIn, ResourceLocation registryName) {
        super(() -> containedFluidIn, new Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(ModItemGroups.TAB_DELIVERY));
        setRegistryName(registryName);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt) {
        return new FluidBucketWrapper(stack);
    }
}
