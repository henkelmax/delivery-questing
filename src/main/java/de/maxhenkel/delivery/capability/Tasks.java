package de.maxhenkel.delivery.capability;

import net.minecraft.nbt.CompoundNBT;

public class Tasks {

    private int posIndex;

    public Tasks() {
        posIndex = 0;
    }

    public int getPosIndex() {
        return posIndex;
    }

    public void setPosIndex(int posIndex) {
        this.posIndex = posIndex;
    }

    public CompoundNBT toNBT() {
        CompoundNBT compound = new CompoundNBT();
        compound.putInt("posIndex", posIndex);
        return compound;
    }

    public void fromNBT(CompoundNBT compound) {
        posIndex = compound.getInt("posIndex");
    }
}
