package de.maxhenkel.delivery.fluid;

import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.blocks.ModBlocks;
import de.maxhenkel.delivery.items.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.fluids.FluidAttributes;

public class LiquidEnergyFlowing extends ModFluidFlowing {

    protected LiquidEnergyFlowing() {
        super(
                FluidAttributes.builder(
                        new ResourceLocation(Main.MODID, "block/liquid_energy_still"),
                        new ResourceLocation(Main.MODID, "block/liquid_energy_flowing")).sound(SoundEvents.BUCKET_FILL),
                () -> ModBlocks.LIQUID_ENERGY,
                () -> ModFluids.LIQUID_ENERGY,
                () -> ModFluids.LIQUID_ENERGY_FLOWING,
                () -> ModItems.LIQUID_ENERGY_BUCKET
        );
        setRegistryName(new ResourceLocation(Main.MODID, "liquid_energy_flowing"));
    }
}

