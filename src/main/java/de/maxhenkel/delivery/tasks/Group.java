package de.maxhenkel.delivery.tasks;

import de.maxhenkel.corelib.helpers.Triple;
import de.maxhenkel.corelib.item.ItemUtils;
import de.maxhenkel.corelib.net.NetUtils;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.net.MessageTaskCompletedToast;
import net.minecraft.command.CommandException;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Group implements INBTSerializable<CompoundNBT> {

    private UUID id;
    private String name;
    private String password;
    private List<UUID> members;
    private List<TaskProgress> tasks;
    private List<UUID> completedTasks;
    private long experience;
    private NonNullList<ItemStack> mailboxInbox;

    public Group(String name, String password) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.password = password;
        this.members = new ArrayList<>();
        this.tasks = new ArrayList<>();
        this.completedTasks = new ArrayList<>();
        this.experience = 0L;
        this.mailboxInbox = NonNullList.withSize(4, ItemStack.EMPTY);
    }

    public Group() {

    }

    public UUID getId() {
        return id;
    }

    public List<TaskProgress> getTasks() {
        return tasks;
    }

    public void addTask(UUID taskID) {
        tasks.add(new TaskProgress(taskID));
    }

    public List<UUID> getCompletedTasks() {
        return completedTasks;
    }

    public void addMember(UUID member) throws CommandException {
        if (members.stream().anyMatch(uuid -> uuid.equals(member))) {
            throw new CommandException(new TranslationTextComponent("command.delivery.already_member"));
        }
        members.add(member);
    }

    public boolean removeMember(UUID member) {
        return members.removeIf(uuid -> uuid.equals(member));
    }

    public boolean isMember(UUID player) {
        return members.stream().anyMatch(uuid -> uuid.equals(player));
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<UUID> getMembers() {
        return members;
    }

    public long getExperience() {
        return experience;
    }

    public void addExperience(int experience) {
        this.experience += experience;
    }

    public void setExperience(long experience) {
        this.experience = experience;
    }

    public NonNullList<ItemStack> getMailboxInbox() {
        return mailboxInbox;
    }

    public boolean canAcceptTask(UUID taskID) {
        if (completedTasks.stream().anyMatch(uuid -> uuid.equals(taskID))) {
            return false;
        }
        if (tasks.stream().anyMatch(taskProgress -> taskProgress.getTaskID().equals(taskID))) {
            return false;
        }
        return true;
    }

    public ActiveTasks getActiveTasks() {
        List<ActiveTask> t = new ArrayList<>();
        for (TaskProgress taskProgress : tasks) {
            Task task = Main.TASK_MANAGER.getTask(taskProgress.getTaskID());
            if (task != null) {
                t.add(new ActiveTask(task, taskProgress));
            }
        }
        return new ActiveTasks(t);
    }

    public void handInTaskItems(NonNullList<ItemStack> items) {
        NonNullList<ItemStack> taskItems = NonNullList.create();
        NonNullList<FluidStack> taskFluids = NonNullList.create();

        for (ItemStack stack : items) {
            if (stack.getItem() instanceof ITaskContainer) {
                ITaskContainer container = (ITaskContainer) stack.getItem();
                taskItems.addAll(container.getItems(stack));
                taskFluids.addAll(container.getFluids(stack));
            } else if (stack.getItem() instanceof BlockItem && ((BlockItem) stack.getItem()).getBlock() instanceof ITaskContainer) {
                ITaskContainer container = (ITaskContainer) ((BlockItem) stack.getItem()).getBlock();
                taskItems.addAll(container.getItems(stack));
                taskFluids.addAll(container.getFluids(stack));
            } else {
                taskItems.add(stack);
            }
        }


        for (ItemStack stack : taskItems) {
            while (putItemStack(stack)) {
            }
        }

        for (FluidStack stack : taskFluids) {
            while (putFluidStack(stack)) {
            }
        }

        tasks.removeIf(taskProgress -> {
            if (isFinished(taskProgress)) {
                onTaskCompleted(taskProgress);
                return true;
            }
            return false;
        });
    }

    public void onTaskCompleted(TaskProgress taskProgress) {
        completedTasks.add(taskProgress.getTaskID());

        Task task = taskProgress.findTask();

        if (task == null) {
            return;
        }

        PlayerList playerList = ServerLifecycleHooks.getCurrentServer().getPlayerList();
        MessageTaskCompletedToast msg = new MessageTaskCompletedToast(task);
        for (UUID member : members) {
            ServerPlayerEntity player = playerList.getPlayerByUUID(member);
            if (player != null) {
                NetUtils.sendTo(Main.SIMPLE_CHANNEL, player, msg);
            }
        }
    }

    private boolean isFinished(TaskProgress taskProgress) {
        Task task = taskProgress.findTask();

        if (task == null) {
            return true; //TODO check
        }

        boolean itemsFinished = task.getItems().stream().allMatch(item -> getCurrentAmount(taskProgress, item) >= item.amount);
        boolean fluidsFinished = task.getFluids().stream().allMatch(fluid -> getCurrentAmount(taskProgress, fluid) >= fluid.amount);

        return itemsFinished && fluidsFinished;
    }

    private boolean putItemStack(ItemStack stack) {
        Triple<Task, TaskProgress, Item> triple = findTask(stack);
        if (triple == null) {
            return false;
        }
        long itemsNeeded = triple.getValue3().amount - getCurrentAmount(triple.getValue2(), triple.getValue3());

        int insertAmount = (int) Math.min(itemsNeeded, stack.getCount());

        ItemStack insertStack = stack.split(insertAmount);

        triple.getValue2().getTaskItems().add(insertStack);

        return !stack.isEmpty();
    }

    private boolean putFluidStack(FluidStack stack) {
        Triple<Task, TaskProgress, Fluid> triple = findTask(stack);
        if (triple == null) {
            return false;
        }
        long itemsNeeded = triple.getValue3().amount - getCurrentAmount(triple.getValue2(), triple.getValue3());

        int insertAmount = (int) Math.min(itemsNeeded, stack.getAmount());

        FluidStack insertStack = stack.copy();
        insertStack.setAmount(insertAmount);
        stack.setAmount(stack.getAmount() - insertAmount);

        triple.getValue2().getTaskFluids().add(insertStack);

        return !stack.isEmpty();
    }

    private long getCurrentAmount(TaskProgress progress, Item element) {
        return progress.getTaskItems().stream().map(stack -> stack.getItem().isIn(element.item) ? (long) stack.getCount() : 0L).reduce(0L, Long::sum);
    }

    private long getCurrentAmount(TaskProgress progress, Fluid element) {
        return progress.getTaskFluids().stream().map(stack -> stack.getFluid().isIn(element.item) ? (long) stack.getAmount() : 0L).reduce(0L, Long::sum);
    }

    @Nullable
    private Triple<Task, TaskProgress, Item> findTask(ItemStack stack) {
        for (TaskProgress taskProgress : tasks) {
            Task task = taskProgress.findTask();
            if (task == null) {
                continue;
            }
            Optional<Item> item = task.getItems().stream().filter(i -> stack.getItem().isIn(i.getItem())).findAny();
            if (item.isPresent()) {
                return new Triple<>(task, taskProgress, item.get());
            }
        }

        return null;
    }

    @Nullable
    private Triple<Task, TaskProgress, Fluid> findTask(FluidStack stack) {
        for (TaskProgress taskProgress : tasks) {
            Task task = taskProgress.findTask();
            if (task == null) {
                continue;
            }
            Optional<Fluid> item = task.getFluids().stream().filter(i -> stack.getFluid().isIn(i.getItem())).findAny();
            if (item.isPresent()) {
                return new Triple<>(task, taskProgress, item.get());
            }
        }

        return null;
    }

    public float getLevel() {
        return (float) (Math.sqrt(0.2F * experience + 0.25F) - 0.5F);
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT compound = new CompoundNBT();
        compound.putUniqueId("ID", id);
        compound.putString("Name", name);
        compound.putString("Password", password);

        ListNBT memberList = new ListNBT();
        for (UUID member : members) {
            CompoundNBT memberTag = new CompoundNBT();
            memberTag.putUniqueId("Member", member);
            memberList.add(memberTag);
        }
        compound.put("Members", memberList);

        ListNBT taskList = new ListNBT();
        for (TaskProgress task : tasks) {
            taskList.add(task.serializeNBT());
        }
        compound.put("Tasks", taskList);

        ListNBT completedTasksList = new ListNBT();
        for (UUID t : completedTasks) {
            CompoundNBT taskTag = new CompoundNBT();
            taskTag.putUniqueId("ID", t);
            completedTasksList.add(taskTag);
        }
        compound.put("CompletedTasks", completedTasksList);

        compound.putLong("Experience", experience);

        ItemUtils.saveInventory(compound, "MailboxInbox", mailboxInbox);

        return compound;
    }

    @Override
    public void deserializeNBT(CompoundNBT compound) {
        this.id = compound.getUniqueId("ID");
        this.name = compound.getString("Name");
        this.password = compound.getString("Name");

        ListNBT memberList = compound.getList("Members", 10);
        this.members = new ArrayList<>();
        for (int i = 0; i < memberList.size(); i++) {
            this.members.add(memberList.getCompound(i).getUniqueId("Member"));
        }

        ListNBT taskList = compound.getList("Tasks", 10);
        this.tasks = new ArrayList<>();
        for (int i = 0; i < taskList.size(); i++) {
            TaskProgress task = new TaskProgress();
            task.deserializeNBT(taskList.getCompound(i));
            this.tasks.add(task);
        }

        ListNBT completedTasksList = compound.getList("CompletedTasks", 10);
        this.completedTasks = new ArrayList<>();
        for (int i = 0; i < completedTasksList.size(); i++) {
            this.completedTasks.add(completedTasksList.getCompound(i).getUniqueId("ID"));
        }

        this.experience = compound.getLong("Experience");

        this.mailboxInbox = NonNullList.withSize(4, ItemStack.EMPTY);
        ItemUtils.readInventory(compound, "MailboxInbox", mailboxInbox);
    }
}
