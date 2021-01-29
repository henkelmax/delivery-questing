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
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.List;

public class SingleSlotInventoryItem extends Item implements ITaskContainer {

    public SingleSlotInventoryItem(Properties properties) {
        super(properties);
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
    public NonNullList<FluidStack> getFluids(ItemStack stack) {
        return NonNullList.create();
    }

}
