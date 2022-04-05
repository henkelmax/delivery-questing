package de.maxhenkel.delivery.items;

import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.tasks.Task;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;

public class SealedEnvelopeItem extends SealedItem {

    public SealedEnvelopeItem() {
        setRegistryName(new ResourceLocation(Main.MODID, "sealed_envelope"));
    }

    @Override
    MutableComponent getTooltip(ItemStack stack) {
        return new TranslatableComponent("tooltip.delivery.sealed_envelope").withStyle(ChatFormatting.GRAY);
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
        ModItems.SEALED_ENVELOPE.setSender(stack, new TextComponent(task.getContractorName()));
        return stack;
    }

}
