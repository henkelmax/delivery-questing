package de.maxhenkel.delivery.items;

import de.maxhenkel.delivery.Main;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class SealedParcelItem extends SealedItem {

    public SealedParcelItem() {
        setRegistryName(new ResourceLocation(Main.MODID, "sealed_parcel"));
    }

    @Override
    IFormattableTextComponent getTooltip(ItemStack stack) {
        return new TranslationTextComponent("tooltip.delivery.sealed_parcel").mergeStyle(TextFormatting.GRAY);
    }

    @Override
    SoundEvent getOpenSound(ItemStack stack) {
        return SoundEvents.ITEM_BOOK_PAGE_TURN;
    }

}
