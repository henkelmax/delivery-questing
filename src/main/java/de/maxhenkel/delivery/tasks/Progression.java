package de.maxhenkel.delivery.tasks;

import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Progression implements INBTSerializable<CompoundTag> {

    private List<Group> groups;

    public Progression() {
        groups = new ArrayList<>();
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void addGroup(UUID player, String name, String password) throws CommandRuntimeException {
        if (groups.stream().anyMatch(group -> group.getMembers().stream().anyMatch(uuid -> uuid.equals(player)))) {
            throw new CommandRuntimeException(new TranslatableComponent("command.delivery.already_in_another_group"));
        }

        if (groups.stream().anyMatch(group -> group.getName().equals(name))) {
            throw new CommandRuntimeException(new TranslatableComponent("command.delivery.group_already_exists"));
        }
        Group group = new Group(name, password);
        group.addMember(player);
        groups.add(group);
    }

    public void joinGroup(UUID player, String name, String password) throws CommandRuntimeException {
        if (groups.stream().anyMatch(group -> group.getMembers().stream().anyMatch(uuid -> uuid.equals(player)))) {
            throw new CommandRuntimeException(new TranslatableComponent("command.delivery.already_in_another_group"));
        }

        Optional<Group> optionalGroup = groups.stream().filter(g -> g.getName().equals(name)).findAny();

        if (!optionalGroup.isPresent()) {
            throw new CommandRuntimeException(new TranslatableComponent("command.delivery.group_not_found", name));
        }

        Group group = optionalGroup.get();

        if (!group.getPassword().equals(password)) {
            throw new CommandRuntimeException(new TranslatableComponent("command.delivery.wrong_password"));
        }

        group.addMember(player);
    }

    public Group getPlayerGroup(UUID player) throws CommandRuntimeException {
        Optional<Group> optionalGroup = groups.stream().filter(group -> group.getMembers().stream().anyMatch(uuid -> uuid.equals(player))).findAny();

        if (!optionalGroup.isPresent()) {
            throw new CommandRuntimeException(new TranslatableComponent("command.delivery.player_not_in_group"));
        }

        return optionalGroup.get();
    }

    @Nullable
    public Group getGroup(UUID groupID) {
        return groups.stream().filter(group -> group.getId().equals(groupID)).findAny().orElse(null);
    }

    public Group leaveGroup(UUID player) throws CommandRuntimeException {
        Group group = getPlayerGroup(player);
        group.removeMember(player);
        return group;
    }

    public void removeGroup(String name, String password) throws CommandRuntimeException {
        Optional<Group> optionalGroup = groups.stream().filter(g -> g.getName().equals(name)).findAny();

        if (!optionalGroup.isPresent()) {
            throw new CommandRuntimeException(new TranslatableComponent("command.delivery.group_not_found", name));
        }

        Group group = optionalGroup.get();

        if (!group.getPassword().equals(password)) {
            throw new CommandRuntimeException(new TranslatableComponent("command.delivery.wrong_password"));
        }

        if (!group.getMembers().isEmpty()) {
            throw new CommandRuntimeException(new TranslatableComponent("command.delivery.group_has_members"));
        }

        groups.removeIf(g -> g.getName().equals(name));
    }

    public void removeGroup(String name) throws CommandRuntimeException {
        Optional<Group> optionalGroup = groups.stream().filter(g -> g.getName().equals(name)).findAny();

        if (!optionalGroup.isPresent()) {
            throw new CommandRuntimeException(new TranslatableComponent("command.delivery.group_not_found", name));
        }

        groups.removeIf(g -> g.getName().equals(name));
    }

    public Group getGroup(String name) throws CommandRuntimeException {
        Optional<Group> optionalGroup = groups.stream().filter(group -> group.getName().equals(name)).findAny();

        if (!optionalGroup.isPresent()) {
            throw new CommandRuntimeException(new TranslatableComponent("command.delivery.group_not_found", name));
        }

        return optionalGroup.get();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compound = new CompoundTag();

        ListTag groupList = new ListTag();
        for (Group group : groups) {
            groupList.add(group.serializeNBT());
        }
        compound.put("Groups", groupList);

        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag compound) {
        ListTag groupList = compound.getList("Groups", 10);
        groups = new ArrayList<>();
        for (int i = 0; i < groupList.size(); i++) {
            Group group = new Group();
            group.deserializeNBT(groupList.getCompound(i));
            groups.add(group);
        }
    }
}
