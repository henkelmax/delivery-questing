package de.maxhenkel.delivery.tasks;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class ActiveTask implements INBTSerializable<CompoundNBT> {

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
    public CompoundNBT serializeNBT() {
        CompoundNBT compound = new CompoundNBT();
        compound.put("Task", task.serializeNBT());
        compound.put("TaskProgress", taskProgress.serializeNBT());
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundNBT compound) {
        task = new Task();
        task.deserializeNBT(compound.getCompound("Task"));

        taskProgress = new TaskProgress();
        taskProgress.deserializeNBT(compound.getCompound("TaskProgress"));
    }
}
