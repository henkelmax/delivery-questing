package de.maxhenkel.delivery.items;

import de.maxhenkel.delivery.ITiered;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.ModItemGroups;
import de.maxhenkel.delivery.Tier;
import de.maxhenkel.delivery.blocks.IUpgradable;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.ResourceLocation;

public class UpgradeItem extends Item implements ITiered {

    private final Tier tier;

    public UpgradeItem(Tier tier) {
        super(new Properties().tab(ModItemGroups.TAB_DELIVERY));
        this.tier = tier;
        setRegistryName(new ResourceLocation(Main.MODID, "upgrade_tier_" + tier.getTier()));
    }

    @Override
    public Tier getTier() {
        return tier;
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        BlockState blockState = context.getLevel().getBlockState(context.getClickedPos());
        if (blockState.getBlock() instanceof IUpgradable) {
            IUpgradable upgradable = (IUpgradable) blockState.getBlock();
            ActionResultType actionResultType = upgradable.addUpgrade(context.getPlayer(), context.getItemInHand(), context.getLevel(), context.getClickedPos());
            if (actionResultType.consumesAction()) {
                return actionResultType;
            }
        }
        return super.useOn(context);
    }
}
