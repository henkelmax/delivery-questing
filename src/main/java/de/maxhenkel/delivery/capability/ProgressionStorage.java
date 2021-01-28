package de.maxhenkel.delivery.capability;

import de.maxhenkel.delivery.tasks.Progression;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

public class ProgressionStorage implements Capability.IStorage<Progression> {

    @Override
    public INBT writeNBT(Capability<Progression> capability, Progression instance, Direction side) {
        return instance.serializeNBT();
    }

    @Override
    public void readNBT(Capability<Progression> capability, Progression instance, Direction side, INBT nbt) {
        instance.deserializeNBT((CompoundNBT) nbt);
    }
}
