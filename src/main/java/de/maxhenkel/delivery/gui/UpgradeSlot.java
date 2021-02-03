package de.maxhenkel.delivery.gui;

import com.mojang.datafixers.util.Pair;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.items.UpgradeItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class UpgradeSlot extends Slot {

    public static final ResourceLocation UPGRADE_SLOT = new ResourceLocation(Main.MODID, "item/upgrade_slot");

    public UpgradeSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return stack.getItem() instanceof UpgradeItem;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Pair<ResourceLocation, ResourceLocation> getBackground() {
        return Pair.of(PlayerContainer.LOCATION_BLOCKS_TEXTURE, UPGRADE_SLOT);
    }
}
