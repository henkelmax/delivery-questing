package de.maxhenkel.delivery.items;

import de.maxhenkel.delivery.ITiered;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.ModItemGroups;
import de.maxhenkel.delivery.Tier;
import de.maxhenkel.delivery.blocks.IUpgradable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;

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
    public InteractionResult useOn(UseOnContext context) {
        BlockState blockState = context.getLevel().getBlockState(context.getClickedPos());
        if (blockState.getBlock() instanceof IUpgradable) {
            IUpgradable upgradable = (IUpgradable) blockState.getBlock();
            InteractionResult actionResultType = upgradable.addUpgrade(context.getPlayer(), context.getItemInHand(), context.getLevel(), context.getClickedPos());
            if (actionResultType.consumesAction()) {
                return actionResultType;
            }
        }
        return super.useOn(context);
    }
}
