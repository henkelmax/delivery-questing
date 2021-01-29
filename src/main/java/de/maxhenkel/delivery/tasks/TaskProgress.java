package de.maxhenkel.delivery.tasks;

import de.maxhenkel.delivery.Main;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TaskProgress implements INBTSerializable<CompoundNBT> {

    private UUID taskID;
    private List<ItemStack> taskItems;
    private List<FluidStack> taskFluids;

    public TaskProgress(UUID taskID) {
        this.taskID = taskID;
        taskItems = new ArrayList<>();
        taskFluids = new ArrayList<>();
    }

    public TaskProgress() {

    }

    public UUID getTaskID() {
        return taskID;
    }

    public List<ItemStack> getTaskItems() {
        return taskItems;
    }

    public List<FluidStack> getTaskFluids() {
        return taskFluids;
    }

    @Nullable
    public Task findTask() {
        return Main.TASK_MANAGER.getTask(taskID);
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT compound = new CompoundNBT();

        compound.putUniqueId("ID", taskID);

        ListNBT taskList = new ListNBT();
        for (ItemStack item : taskItems) {
            taskList.add(item.write(new CompoundNBT()));
        }
        compound.put("TaskItems", taskList);

        ListNBT taskFluidList = new ListNBT();
        for (FluidStack fluid : taskFluids) {
            taskFluidList.add(fluid.writeToNBT(new CompoundNBT()));
        }
        compound.put("TaskFluids", taskFluidList);

        return compound;
    }

    @Override
    public void deserializeNBT(CompoundNBT compound) {
        taskID = compound.getUniqueId("ID");

        ListNBT taskList = compound.getList("TaskItems", 10);
        this.taskItems = new ArrayList<>();
        for (int i = 0; i < taskList.size(); i++) {
            ItemStack stack = ItemStack.read(taskList.getCompound(i));
            this.taskItems.add(stack);
        }

        ListNBT taskFluidList = compound.getList("TaskFluids", 10);
        this.taskFluids = new ArrayList<>();
        for (int i = 0; i < taskFluidList.size(); i++) {
            FluidStack stack = FluidStack.loadFluidStackFromNBT(taskFluidList.getCompound(i));
            this.taskFluids.add(stack);
        }
    }
}
