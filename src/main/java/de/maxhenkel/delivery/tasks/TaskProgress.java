package de.maxhenkel.delivery.tasks;

import de.maxhenkel.corelib.item.ItemUtils;
import de.maxhenkel.delivery.Main;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TaskProgress implements INBTSerializable<CompoundTag> {

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
        if (stack.isEmpty()) {
            return;
        }
        for (int i = 0; i < taskItems.size(); i++) {
            ItemStack s = taskItems.get(i);
            if (isOverMaxValue(stack.getCount(), s.getCount())) {
                continue;
            }
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
            if (isOverMaxValue(stack.getAmount(), s.getAmount())) {
                continue;
            }
            if (s.isFluidEqual(stack)) {
                s.setAmount(s.getAmount() + stack.getAmount());
                taskFluids.set(i, s);
                return;
            }
        }
        taskFluids.add(stack);
    }

    public boolean isOverMaxValue(int amount1, int amount2) {
        return ((long) amount1 + (long) amount2) > (long) Integer.MAX_VALUE;
    }

    @Nullable
    public Task findTask() {
        return Main.TASK_MANAGER.getTask(taskID, (int) Group.getLevel(experienceStarted));
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compound = new CompoundTag();

        compound.putUUID("ID", taskID);
        compound.putLong("ExperienceStarted", experienceStarted);

        ListTag taskList = new ListTag();
        for (ItemStack item : taskItems) {
            if (!item.isEmpty()) {
                taskList.add(ItemUtils.writeOverstackedItem(new CompoundTag(), item));
            }
        }
        compound.put("TaskItems", taskList);

        ListTag taskFluidList = new ListTag();
        for (FluidStack fluid : taskFluids) {
            taskFluidList.add(fluid.writeToNBT(new CompoundTag()));
        }
        compound.put("TaskFluids", taskFluidList);

        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag compound) {
        taskID = compound.getUUID("ID");
        experienceStarted = compound.getLong("ExperienceStarted");

        ListTag taskList = compound.getList("TaskItems", 10);
        this.taskItems = new ArrayList<>();
        for (int i = 0; i < taskList.size(); i++) {
            ItemStack stack = ItemUtils.readOverstackedItem(taskList.getCompound(i));
            if (!stack.isEmpty()) {
                this.taskItems.add(stack);
            }
        }

        ListTag taskFluidList = compound.getList("TaskFluids", 10);
        this.taskFluids = new ArrayList<>();
        for (int i = 0; i < taskFluidList.size(); i++) {
            FluidStack stack = FluidStack.loadFluidStackFromNBT(taskFluidList.getCompound(i));
            this.taskFluids.add(stack);
        }
    }
}
