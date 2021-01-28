package de.maxhenkel.delivery.capability;

import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.tasks.Progression;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ProgressionCapabilityProvider implements ICapabilitySerializable<CompoundNBT> {

    private Progression progression;

    public ProgressionCapabilityProvider(Progression progression) {
        this.progression = progression;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap.equals(Main.PROGRESSION_CAPABILITY)) {
            return LazyOptional.of(() -> (T) progression);
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundNBT serializeNBT() {
        return progression.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundNBT compound) {
        progression.deserializeNBT(compound);
    }
}
