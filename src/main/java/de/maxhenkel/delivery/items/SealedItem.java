package de.maxhenkel.delivery.items;

import com.google.gson.JsonParseException;
import de.maxhenkel.corelib.item.ItemUtils;
import de.maxhenkel.corelib.sound.SoundUtils;
import de.maxhenkel.delivery.ModItemGroups;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public abstract class SealedItem extends Item {

    public SealedItem() {
        super(new Properties().maxStackSize(1).group(ModItemGroups.TAB_DELIVERY));

    }

    @Nullable
    abstract IFormattableTextComponent getTooltip(ItemStack stack);


    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);

        IFormattableTextComponent sender = getSender(stack);
        if (sender != null) {
            tooltip.add(new TranslationTextComponent("tooltip.delivery.by", sender.mergeStyle(TextFormatting.DARK_BLUE)).mergeStyle(TextFormatting.GRAY));
        }

        IFormattableTextComponent t = getTooltip(stack);
        if (t != null) {
            tooltip.add(t);
        }
        NonNullList<ItemStack> items = getContents(stack);
        if (!items.isEmpty()) {
            tooltip.add(new TranslationTextComponent("tooltip.delivery.item_count", items.stream().filter(stack1 -> !stack1.isEmpty()).map(ItemStack::getCount).reduce(Integer::sum).orElse(0)).mergeStyle(TextFormatting.GRAY));
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand handIn) {
        ItemStack stack = player.getHeldItem(handIn);

        NonNullList<ItemStack> contents = getContents(stack);

        SoundEvent openSound = getOpenSound(stack);

        if (openSound != null) {
            world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), openSound, SoundCategory.PLAYERS, 0.5F, SoundUtils.getVariatedPitch(player.world));
        }

        stack = ItemUtils.decrItemStack(stack, player);

        for (ItemStack s : contents) {
            if (stack.isEmpty()) {
                stack = s;
                player.setHeldItem(handIn, s);
            } else {
                if (!player.inventory.addItemStackToInventory(s)) {
                    player.dropItem(s, false);
                }
            }
        }

        return ActionResult.resultSuccess(stack);
    }

    @Nullable
    abstract SoundEvent getOpenSound(ItemStack stack);

    public NonNullList<ItemStack> getContents(ItemStack stack) {
        if (stack.hasTag()) {
            return ItemUtils.readItemList(stack.getTag(), "Items", false);
        } else {
            return NonNullList.create();
        }
    }

    public ItemStack setContents(ItemStack stack, NonNullList<ItemStack> contents) {
        CompoundNBT tag = stack.getOrCreateTag();
        ItemUtils.saveItemList(tag, "Items", contents, false);
        return stack;
    }

    public ItemStack setSender(ItemStack stack, IFormattableTextComponent sender) {
        CompoundNBT tag = stack.getOrCreateChildTag("Sender");
        String json = ITextComponent.Serializer.toJson(sender);
        tag.putString("Name", json);
        return stack;
    }

    @Nullable
    public IFormattableTextComponent getSender(ItemStack stack) {
        CompoundNBT tag = stack.getChildTag("Sender");
        if (tag == null) {
            return null;
        }
        String s = tag.getString("Name");
        try {
            return ITextComponent.Serializer.getComponentFromJson(s);
        } catch (JsonParseException e) {
            return null;
        }
    }
}
