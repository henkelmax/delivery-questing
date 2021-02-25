package de.maxhenkel.delivery.tasks;

import de.maxhenkel.corelib.item.ItemUtils;
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
    private long experienceStarted;
    private List<ItemStack> taskItems;
    private List<FluidStack> taskFluids;

    public TaskProgress(UUID taskID, long experienceStarted) {
        this.taskID = taskID;
        this.experienceStarted = experienceStarted;
        taskItems = new ArrayList<>();
        taskFluids = new ArrayList<>();
    }

    public TaskProgress() {

    }

    public UUID getTaskID() {
        return taskID;
    }

    public long getExperienceStarted() {
        return experienceStarted;
    }

    public List<ItemStack> getTaskItems() {
        return taskItems;
    }

    public List<FluidStack> getTaskFluids() {
        return taskFluids;
    }

    public void add(ItemStack stack) {
        for (int i = 0; i < taskItems.size(); i++) {
            ItemStack s = taskItems.get(i);
            if (ItemUtils.isStackable(stack, s)) {
                s.setCount(s.getCount() + stack.getCount());
                taskItems.set(i, s);
                return;
            }
        }

        taskItems.add(stack);
    }

    public void add(FluidStack stack) {
        for (int i = 0; i < taskFluids.size(); i++) {
            FluidStack s = taskFluids.get(i);
            if (s.isFluidEqual(stack)) {
                s.setAmount(s.getAmount() + stack.getAmount());
                taskFluids.set(i, s);
                return;
            }
        }
        taskFluids.add(stack);
    }

    @Nullable
    public Task findTask() {
        return Main.TASK_MANAGER.getTask(taskID, (int) Group.getLevel(experienceStarted));
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT compound = new CompoundNBT();

        compound.putUniqueId("ID", taskID);
        compound.putLong("ExperienceStarted", experienceStarted);

        ListNBT taskList = new ListNBT();
        for (ItemStack item : taskItems) {
            taskList.add(ItemUtils.writeOverstackedItem(new CompoundNBT(), item));
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
        experienceStarted = compound.getLong("ExperienceStarted");

        ListNBT taskList = compound.getList("TaskItems", 10);
        this.taskItems = new ArrayList<>();
        for (int i = 0; i < taskList.size(); i++) {
            ItemStack stack = ItemUtils.readOverstackedItem(taskList.getCompound(i));
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
