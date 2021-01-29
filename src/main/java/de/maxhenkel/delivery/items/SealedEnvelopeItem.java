package de.maxhenkel.delivery.items;

import de.maxhenkel.corelib.item.ItemUtils;
import de.maxhenkel.corelib.sound.SoundUtils;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.ModItemGroups;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class SealedEnvelopeItem extends Item {

    public SealedEnvelopeItem() {
        super(new Properties().maxStackSize(1).group(ModItemGroups.TAB_DELIVERY));
        setRegistryName(new ResourceLocation(Main.MODID, "sealed_envelope"));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(new TranslationTextComponent("tooltip.delivery.sealed_envelope").mergeStyle(TextFormatting.GRAY));
        NonNullList<ItemStack> items = getContents(stack);
        if (!items.isEmpty()) {
            tooltip.add(new TranslationTextComponent("tooltip.delivery.item_count", items.stream().filter(stack1 -> !stack1.isEmpty()).map(ItemStack::getCount).reduce(Integer::sum).orElse(0)).mergeStyle(TextFormatting.GRAY));
        }
    }

    public NonNullList<ItemStack> getContents(ItemStack stack) {
        if (stack.hasTag()) {
            return ItemUtils.readItemList(stack.getTag(), "Items", false);
        } else {
            return NonNullList.create();
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand handIn) {
        ItemStack stack = player.getHeldItem(handIn);

        NonNullList<ItemStack> contents = getContents(stack);

        //TODO remove
        if (contents.isEmpty()) {
            NonNullList<ItemStack> items = NonNullList.create();
            items.add(new ItemStack(Items.APPLE, 32));
            items.add(new ItemStack(Items.STONE, 64));
            setContents(stack, items);
            return ActionResult.resultSuccess(stack);
        }

        for (ItemStack s : contents) {
            if (!player.inventory.addItemStackToInventory(s)) {
                player.dropItem(s, false);
            }
        }

        world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.PLAYERS, 0.5F, SoundUtils.getVariatedPitch(player.world));

        stack = ItemUtils.decrItemStack(stack, player);
        return ActionResult.resultSuccess(stack);
    }

    public ItemStack setContents(ItemStack stack, NonNullList<ItemStack> contents) {
        CompoundNBT tag = stack.getOrCreateTag();
        ItemUtils.saveItemList(tag, "Items", contents, false);
        return stack;
    }

    public static ItemStack createTask(UUID taskID, ItemStack... additionalItems) {
        ItemStack stack = new ItemStack(ModItems.SEALED_ENVELOPE);
        NonNullList<ItemStack> contents = NonNullList.create();
        ItemStack task = new ItemStack(ModItems.CONTRACT);
        ModItems.CONTRACT.setTask(task, taskID);
        contents.add(task);
        contents.addAll(Arrays.asList(additionalItems));
        ModItems.SEALED_ENVELOPE.setContents(stack, contents);
        return stack;
    }

}
