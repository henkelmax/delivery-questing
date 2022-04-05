package de.maxhenkel.delivery.items;

import com.google.gson.JsonParseException;
import de.maxhenkel.corelib.item.ItemUtils;
import de.maxhenkel.corelib.sound.SoundUtils;
import de.maxhenkel.delivery.ModItemGroups;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public abstract class SealedItem extends Item {

    public SealedItem() {
        super(new Properties().stacksTo(1).tab(ModItemGroups.TAB_DELIVERY));

    }

    @Nullable
    abstract MutableComponent getTooltip(ItemStack stack);


    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        MutableComponent sender = getSender(stack);
        if (sender != null) {
            tooltip.add(new TranslatableComponent("tooltip.delivery.by", sender.withStyle(ChatFormatting.DARK_BLUE)).withStyle(ChatFormatting.GRAY));
        }

        MutableComponent t = getTooltip(stack);
        if (t != null) {
            tooltip.add(t);
        }
        NonNullList<ItemStack> items = getContents(stack);
        if (!items.isEmpty()) {
            tooltip.add(new TranslatableComponent("tooltip.delivery.item_count", items.stream().filter(stack1 -> !stack1.isEmpty()).map(ItemStack::getCount).reduce(Integer::sum).orElse(0)).withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand handIn) {
        ItemStack stack = player.getItemInHand(handIn);

        NonNullList<ItemStack> contents = getContents(stack);

        SoundEvent openSound = getOpenSound(stack);

        if (openSound != null) {
            world.playSound(null, player.getX(), player.getY(), player.getZ(), openSound, SoundSource.PLAYERS, 0.5F, SoundUtils.getVariatedPitch(player.level));
        }

        stack = ItemUtils.decrItemStack(stack, player);

        for (ItemStack s : contents) {
            if (stack.isEmpty()) {
                stack = s;
                player.setItemInHand(handIn, s);
            } else {
                if (!player.getInventory().add(s)) {
                    player.drop(s, false);
                }
            }
        }

        return InteractionResultHolder.success(stack);
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
        CompoundTag tag = stack.getOrCreateTag();
        ItemUtils.saveItemList(tag, "Items", contents, false);
        return stack;
    }

    public ItemStack setSender(ItemStack stack, MutableComponent sender) {
        CompoundTag tag = stack.getOrCreateTagElement("Sender");
        String json = Component.Serializer.toJson(sender);
        tag.putString("Name", json);
        return stack;
    }

    @Nullable
    public MutableComponent getSender(ItemStack stack) {
        CompoundTag tag = stack.getTagElement("Sender");
        if (tag == null) {
            return null;
        }
        String s = tag.getString("Name");
        try {
            return Component.Serializer.fromJson(s);
        } catch (JsonParseException e) {
            return null;
        }
    }
}
