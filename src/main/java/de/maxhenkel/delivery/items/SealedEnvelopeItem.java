package de.maxhenkel.delivery.items;

import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.tasks.Task;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Arrays;

public class SealedEnvelopeItem extends SealedItem {

    public SealedEnvelopeItem() {
        setRegistryName(new ResourceLocation(Main.MODID, "sealed_envelope"));
    }

    @Override
    IFormattableTextComponent getTooltip(ItemStack stack) {
        return new TranslationTextComponent("tooltip.delivery.sealed_envelope").withStyle(TextFormatting.GRAY);
    }

    @Override
    SoundEvent getOpenSound(ItemStack stack) {
        return SoundEvents.BOOK_PAGE_TURN;
    }

    public static ItemStack createTask(Task task, ItemStack... additionalItems) {
        ItemStack stack = new ItemStack(ModItems.SEALED_ENVELOPE);
        NonNullList<ItemStack> contents = NonNullList.create();
        ItemStack taskStack = new ItemStack(ModItems.CONTRACT);
        ModItems.CONTRACT.setTask(taskStack, task.getId());
        contents.add(taskStack);
        contents.addAll(Arrays.asList(additionalItems));
        ModItems.SEALED_ENVELOPE.setContents(stack, contents);
        ModItems.SEALED_ENVELOPE.setSender(stack, new StringTextComponent(task.getContractorName()));
        return stack;
    }

}
