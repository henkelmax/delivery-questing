package de.maxhenkel.delivery;

import de.maxhenkel.delivery.blocks.ModBlocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ModItemGroups {

    public static final ItemGroup TAB_DELIVERY = new ItemGroup("delivery") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModBlocks.CARDBOARD_BOX_TIER_1);
        }
    };

}
