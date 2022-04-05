package de.maxhenkel.delivery.tasks;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.List;

public class ActiveTasks implements INBTSerializable<CompoundTag> {

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
    public CompoundTag serializeNBT() {
        CompoundTag compound = new CompoundTag();
        ListTag list = new ListTag();
        for (ActiveTask task : tasks) {
            list.add(task.serializeNBT());
        }
        compound.put("Tasks", list);
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag compound) {
        tasks = new ArrayList<>();

        ListTag list = compound.getList("Tasks", 10);
        for (int i = 0; i < list.size(); i++) {
            ActiveTask t = new ActiveTask();
            t.deserializeNBT(list.getCompound(i));
            tasks.add(t);
        }
    }
}
