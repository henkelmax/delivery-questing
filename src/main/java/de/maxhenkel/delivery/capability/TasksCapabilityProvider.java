package de.maxhenkel.delivery.capability;

import de.maxhenkel.delivery.Main;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TasksCapabilityProvider implements ICapabilitySerializable<CompoundNBT> {

    private Tasks tasks;

    public TasksCapabilityProvider(Tasks baseWorld) {
        this.tasks = baseWorld;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap.equals(Main.TASKS_CAPABILITY)) {
            return LazyOptional.of(() -> (T) tasks);
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundNBT serializeNBT() {
        return tasks.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundNBT compound) {
        tasks.deserializeNBT(compound);
    }
}
