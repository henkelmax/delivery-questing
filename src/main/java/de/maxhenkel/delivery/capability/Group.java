package de.maxhenkel.delivery.capability;

import net.minecraft.command.CommandException;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Group implements INBTSerializable<CompoundNBT> {

    private String name;
    private String password;
    private List<UUID> members;
    private List<Task> tasks;
    private List<UUID> completedTasks;
    private long experience;

    public Group(String name, String password) {
        this.name = name;
        this.password = password;
        this.members = new ArrayList<>();
        this.tasks = new ArrayList<>();
        this.completedTasks = new ArrayList<>();
        this.experience = 0L;
    }

    public Group() {

    }

    public List<Task> getTasks() {
        return tasks;
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

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT compound = new CompoundNBT();
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
        for (Task task : tasks) {
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

        return compound;
    }

    @Override
    public void deserializeNBT(CompoundNBT compound) {
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
            Task task = new Task();
            task.deserializeNBT(taskList.getCompound(i));
            this.tasks.add(task);
        }

        ListNBT completedTasksList = compound.getList("CompletedTasks", 10);
        this.completedTasks = new ArrayList<>();
        for (int i = 0; i < completedTasksList.size(); i++) {
            this.completedTasks.add(completedTasksList.getCompound(i).getUniqueId("ID"));
        }

        this.experience = compound.getLong("Experience");
    }
}
