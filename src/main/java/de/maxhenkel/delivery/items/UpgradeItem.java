package de.maxhenkel.delivery.items;

import de.maxhenkel.delivery.ITiered;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.ModItemGroups;
import de.maxhenkel.delivery.Tier;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class UpgradeItem extends Item implements ITiered {

    private final Tier tier;

    public UpgradeItem(Tier tier) {
        super(new Properties().group(ModItemGroups.TAB_DELIVERY));
        this.tier = tier;
        setRegistryName(new ResourceLocation(Main.MODID, "upgrade_tier_" + tier.getTier()));
    }

    @Override
    public Tier getTier() {
        return tier;
    }

}
