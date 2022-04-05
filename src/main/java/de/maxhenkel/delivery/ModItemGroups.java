package de.maxhenkel.delivery;

import de.maxhenkel.delivery.blocks.ModBlocks;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModItemGroups {

    public static final CreativeModeTab TAB_DELIVERY = new CreativeModeTab("delivery") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModBlocks.CARDBOARD_BOX_TIER_1);
        }
    };

}
