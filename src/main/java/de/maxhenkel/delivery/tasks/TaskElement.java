package de.maxhenkel.delivery.tasks;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.ITag;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;

public abstract class TaskElement<T> implements INBTSerializable<CompoundNBT> {

    protected String tag;
    protected ITag.INamedTag<T> item;
    protected long amount;

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

    public ITag.INamedTag<T> getItem() {
        return item;
    }

    public long getAmount() {
        return amount;
    }

    @Nullable
    protected abstract ITag.INamedTag<T> getTag(String tag);

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT compound = new CompoundNBT();
        if (tag != null) {
            compound.putString("Tag", tag);
        }
        compound.putLong("Amount", amount);
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundNBT compound) {
        if (compound.contains("Tag")) {
            tag = compound.getString("Tag");
            item = getTag(tag);
        }
        amount = compound.getLong("Amount");
    }
}
