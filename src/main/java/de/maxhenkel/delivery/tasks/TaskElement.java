package de.maxhenkel.delivery.tasks;

import de.maxhenkel.corelib.helpers.AbstractStack;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.ITag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.List;

public abstract class TaskElement<T> implements INBTSerializable<CompoundNBT> {

    protected String tag;
    @Nullable
    protected ITag.INamedTag<T> item;
    protected long amount;
    @Nullable
    protected CompoundNBT nbt;

    public TaskElement(String tag, ITag.INamedTag<T> item, long amount) {
        this.tag = tag;
        this.item = item;
        this.amount = amount;
    }

    public TaskElement() {

    }

    public String getTag() {
        return tag;
    }

    @Nullable
    public ITag.INamedTag<T> getItem() {
        return item;
    }

    public long getAmount() {
        return amount;
    }

    @Nullable
    protected abstract ITag.INamedTag<T> getTag(String tag);

    protected abstract T getDefault();

    @OnlyIn(Dist.CLIENT)
    public abstract AbstractStack<?> getAbstractStack();

    @OnlyIn(Dist.CLIENT)
    public T getCurrentDisplayedElement() {
        long time = Minecraft.getInstance().level.getGameTime();
        if (item == null) {
            return getDefault();
        }
        List<T> allElements = item.getValues();
        return allElements.get((int) (time / 20L % allElements.size()));
    }

    @Nullable
    public CompoundNBT getNbt() {
        return nbt;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT compound = new CompoundNBT();
        if (tag != null) {
            compound.putString("Tag", tag);
        }
        compound.putLong("Amount", amount);
        if (nbt != null) {
            compound.put("NBT", nbt);
        }
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundNBT compound) {
        if (compound.contains("Tag", Constants.NBT.TAG_STRING)) {
            tag = compound.getString("Tag");
            item = getTag(tag);
        }
        amount = compound.getLong("Amount");
        if (compound.contains("NBT", Constants.NBT.TAG_COMPOUND)) {
            nbt = compound.getCompound("NBT");
        }
    }
}
