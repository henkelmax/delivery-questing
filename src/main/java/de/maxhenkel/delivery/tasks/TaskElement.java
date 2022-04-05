package de.maxhenkel.delivery.tasks;

import de.maxhenkel.corelib.helpers.AbstractStack;
import de.maxhenkel.corelib.tag.Tag;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.List;

public abstract class TaskElement<T> implements INBTSerializable<CompoundTag> {

    protected String tag;
    @Nullable
    protected Tag<T> item;
    protected long amount;
    @Nullable
    protected CompoundTag nbt;

    public TaskElement(String tag, Tag<T> item, long amount) {
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
    public Tag<T> getItem() {
        return item;
    }

    public long getAmount() {
        return amount;
    }

    @Nullable
    protected abstract Tag<T> getTag(String tag);

    protected abstract T getDefault();

    @OnlyIn(Dist.CLIENT)
    public abstract AbstractStack<?> getAbstractStack();

    @OnlyIn(Dist.CLIENT)
    public T getCurrentDisplayedElement() {
        long time = Minecraft.getInstance().level.getGameTime();
        if (item == null) {
            return getDefault();
        }
        List<T> allElements = item.getAll();
        return allElements.get((int) (time / 20L % allElements.size()));
    }

    @Nullable
    public CompoundTag getNbt() {
        return nbt;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compound = new CompoundTag();
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
    public void deserializeNBT(CompoundTag compound) {
        if (compound.contains("Tag", net.minecraft.nbt.Tag.TAG_STRING)) {
            tag = compound.getString("Tag");
            item = getTag(tag);
        }
        amount = compound.getLong("Amount");
        if (compound.contains("NBT", net.minecraft.nbt.Tag.TAG_COMPOUND)) {
            nbt = compound.getCompound("NBT");
        }
    }
}
