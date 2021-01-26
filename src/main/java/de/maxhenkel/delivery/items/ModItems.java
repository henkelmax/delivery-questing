package de.maxhenkel.delivery.items;

import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.fluid.ModFluids;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;

public class ModItems {

    public static final EnvelopeItem ENVELOPE = new EnvelopeItem();
    public static final ParcelItem PARCEL = new ParcelItem();
    public static final ModBucketItem LIQUID_ENERGY_BUCKET = new ModBucketItem(ModFluids.LIQUID_ENERGY, new ResourceLocation(Main.MODID, "liquid_energy_bucket"));

    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                LIQUID_ENERGY_BUCKET,
                ENVELOPE,
                PARCEL
        );
    }

}
