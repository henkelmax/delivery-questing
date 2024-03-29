package de.maxhenkel.delivery.items;

import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.ModItemGroups;
import de.maxhenkel.delivery.Tier;
import de.maxhenkel.delivery.fluid.ModFluids;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.RegistryEvent;

public class ModItems {

    public static final EnvelopeItem ENVELOPE = new EnvelopeItem();
    public static final SealedEnvelopeItem SEALED_ENVELOPE = new SealedEnvelopeItem();
    public static final Item CARDBOARD = new Item(new Item.Properties().tab(ModItemGroups.TAB_DELIVERY)).setRegistryName(Main.MODID, "cardboard");
    public static final ParcelItem PARCEL = new ParcelItem();
    public static final SealedParcelItem SEALED_PARCEL = new SealedParcelItem();
    public static final ContractItem CONTRACT = new ContractItem();
    public static final Item UPGRADE_BASE = new Item(new Item.Properties().tab(ModItemGroups.TAB_DELIVERY)).setRegistryName(Main.MODID, "upgrade_base");
    public static final UpgradeItem UPGRADE_TIER_1 = new UpgradeItem(Tier.TIER_1);
    public static final UpgradeItem UPGRADE_TIER_2 = new UpgradeItem(Tier.TIER_2);
    public static final UpgradeItem UPGRADE_TIER_3 = new UpgradeItem(Tier.TIER_3);
    public static final UpgradeItem UPGRADE_TIER_4 = new UpgradeItem(Tier.TIER_4);
    public static final UpgradeItem UPGRADE_TIER_5 = new UpgradeItem(Tier.TIER_5);
    public static final UpgradeItem UPGRADE_TIER_6 = new UpgradeItem(Tier.TIER_6);

    public static final ModBucketItem LIQUID_ENERGY_BUCKET = new ModBucketItem(ModFluids.LIQUID_ENERGY, new ResourceLocation(Main.MODID, "liquid_energy_bucket"));

    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                ENVELOPE,
                SEALED_ENVELOPE,
                CARDBOARD,
                PARCEL,
                SEALED_PARCEL,
                CONTRACT,
                LIQUID_ENERGY_BUCKET,
                UPGRADE_BASE,
                UPGRADE_TIER_1,
                UPGRADE_TIER_2,
                UPGRADE_TIER_3,
                UPGRADE_TIER_4,
                UPGRADE_TIER_5,
                UPGRADE_TIER_6
        );
    }

}
