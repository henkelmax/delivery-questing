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

    private Tasks baseWorld;

    public TasksCapabilityProvider(Tasks baseWorld) {
        this.baseWorld = baseWorld;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap.equals(Main.TASKS_CAPABILITY)) {
            return LazyOptional.of(() -> (T) baseWorld);
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundNBT serializeNBT() {
        return baseWorld.toNBT();
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        baseWorld.fromNBT(nbt);
    }
}
