package de.maxhenkel.delivery.tasks;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.List;

public class ActiveTasks implements INBTSerializable<CompoundNBT> {

    private List<ActiveTask> tasks;

    public ActiveTasks(List<ActiveTask> tasks) {
        this.tasks = tasks;
    }

    public ActiveTasks() {

    }

    public List<ActiveTask> getTasks() {
        return tasks;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT compound = new CompoundNBT();
        ListNBT list = new ListNBT();
        for (ActiveTask task : tasks) {
            list.add(task.serializeNBT());
        }
        compound.put("Tasks", list);
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundNBT compound) {
        tasks = new ArrayList<>();

        ListNBT list = compound.getList("Tasks", 10);
        for (int i = 0; i < list.size(); i++) {
            ActiveTask t = new ActiveTask();
            t.deserializeNBT(list.getCompound(i));
            tasks.add(t);
        }
    }
}
