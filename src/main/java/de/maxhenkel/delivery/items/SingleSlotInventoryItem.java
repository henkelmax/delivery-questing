package de.maxhenkel.delivery.items;

import de.maxhenkel.corelib.item.ItemUtils;
import de.maxhenkel.delivery.tasks.ITaskContainer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class SingleSlotInventoryItem extends Item implements ITaskContainer {

    protected final int stackLimit;

    public SingleSlotInventoryItem(Properties properties, int stackLimit) {
        super(properties);
        this.stackLimit = stackLimit;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);

        if (stack.hasTag()) {
            CompoundNBT compound = stack.getTag();
            NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
            ItemUtils.readInventory(compound, "Items", items);
            tooltip.add(new TranslationTextComponent("tooltip.delivery.item_count", items.stream().filter(stack1 -> !stack1.isEmpty()).map(ItemStack::getCount).reduce(Integer::sum).orElse(0)).mergeStyle(TextFormatting.GRAY));
        }
    }

    public ItemStack getContent(ItemStack stack) {
        if (stack.hasTag()) {
            CompoundNBT compound = stack.getTag();
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
            return NonNullList.create();
        }
        return NonNullList.from(ItemStack.EMPTY, content);
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
        int amountToAdd = Math.min(amount, stackLimit - content.getCount());
        content = new ItemStack(stackToAdd.getItem(), (content.isEmpty() ? 0 : content.getCount()) + amountToAdd);
        stackToAdd.shrink(amountToAdd);

        CompoundNBT compound = stack.getOrCreateTag();
        NonNullList<ItemStack> items = NonNullList.from(ItemStack.EMPTY, content);
        ItemUtils.saveInventory(compound, "Items", items);

        return stackToAdd;
    }

    @Override
    public boolean isFull(ItemStack stack) {
        return getContent(stack).getCount() >= stackLimit;
    }
}
