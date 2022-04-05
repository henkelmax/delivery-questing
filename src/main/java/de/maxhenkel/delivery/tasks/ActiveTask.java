package de.maxhenkel.delivery.tasks;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public class ActiveTask implements INBTSerializable<CompoundTag> {

    private Task task;
    private TaskProgress taskProgress;

    public ActiveTask(Task task, TaskProgress taskProgress) {
        this.task = task;
        this.taskProgress = taskProgress;
    }

    public ActiveTask() {

    }

    public Task getTask() {
        return task;
    }

    public TaskProgress getTaskProgress() {
        return taskProgress;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compound = new CompoundTag();
        compound.put("Task", task.serializeNBT());
        compound.put("TaskProgress", taskProgress.serializeNBT());
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag compound) {
        task = new Task();
        task.deserializeNBT(compound.getCompound("Task"));

        taskProgress = new TaskProgress();
        taskProgress.deserializeNBT(compound.getCompound("TaskProgress"));
    }
}
