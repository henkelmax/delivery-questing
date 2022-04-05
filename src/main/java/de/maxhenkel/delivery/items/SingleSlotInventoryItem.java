package de.maxhenkel.delivery.items;

import de.maxhenkel.corelib.item.ItemUtils;
import de.maxhenkel.delivery.tasks.ITaskContainer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class SingleSlotInventoryItem extends Item implements ITaskContainer {

    protected final int stackLimit;

    public SingleSlotInventoryItem(Properties properties, int stackLimit) {
        super(properties);
        this.stackLimit = stackLimit;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        if (stack.hasTag()) {
            CompoundTag compound = stack.getTag();
            NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
            ItemUtils.readInventory(compound, "Items", items);
            tooltip.add(new TranslatableComponent("tooltip.delivery.item_count", items.stream().filter(stack1 -> !stack1.isEmpty()).map(ItemStack::getCount).reduce(Integer::sum).orElse(0)).withStyle(ChatFormatting.GRAY));
        }
    }

    public ItemStack getContent(ItemStack stack) {
        if (stack.hasTag()) {
            CompoundTag compound = stack.getTag();
            NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
            ItemUtils.readInventory(compound, "Items", items);
            return items.get(0);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public NonNullList<ItemStack> getItems(ItemStack stack) {
        ItemStack content = getContent(stack);
        if (content.isEmpty()) {
            return NonNullList.withSize(1, ItemStack.EMPTY);
        }
        return NonNullList.of(ItemStack.EMPTY, content);
    }

    @Override
    public boolean canAcceptItems(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack add(ItemStack stack, ItemStack stackToAdd, int amount) {
        if (stackToAdd.isEmpty()) {
            return stackToAdd;
        }
        ItemStack content = getContent(stack);
        if (!content.isEmpty() && !ItemUtils.isStackable(content, stackToAdd)) {
            return stackToAdd;
        }
        int amountToAdd = Math.min(Math.min(amount, stackToAdd.getCount()), stackLimit - content.getCount());
        content = new ItemStack(stackToAdd.getItem(), (content.isEmpty() ? 0 : content.getCount()) + amountToAdd);
        stackToAdd.shrink(amountToAdd);

        CompoundTag compound = stack.getOrCreateTag();
        NonNullList<ItemStack> items = NonNullList.of(ItemStack.EMPTY, content);
        ItemUtils.saveInventory(compound, "Items", items);

        return stackToAdd;
    }

    @Override
    public boolean isFull(ItemStack stack) {
        return getContent(stack).getCount() >= stackLimit;
    }
}
