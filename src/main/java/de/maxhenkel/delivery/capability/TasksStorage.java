package de.maxhenkel.delivery.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class TasksStorage implements Capability.IStorage<Tasks> {

    @Nullable
    @Override
    public INBT writeNBT(Capability<Tasks> capability, Tasks instance, Direction side) {
        return instance.toNBT();
    }

    @Override
    public void readNBT(Capability<Tasks> capability, Tasks instance, Direction side, INBT nbt) {
        instance.fromNBT((CompoundNBT) nbt);
    }
}
