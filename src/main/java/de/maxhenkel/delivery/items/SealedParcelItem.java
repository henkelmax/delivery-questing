package de.maxhenkel.delivery.items;

import de.maxhenkel.delivery.Main;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;

public class SealedParcelItem extends SealedItem {

    public SealedParcelItem() {
        setRegistryName(new ResourceLocation(Main.MODID, "sealed_parcel"));
    }

    @Override
    MutableComponent getTooltip(ItemStack stack) {
        return new TranslatableComponent("tooltip.delivery.sealed_parcel").withStyle(ChatFormatting.GRAY);
    }

    @Override
    SoundEvent getOpenSound(ItemStack stack) {
        return SoundEvents.BOOK_PAGE_TURN;
    }

}
