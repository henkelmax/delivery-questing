package de.maxhenkel.delivery.capability;

import net.minecraft.command.CommandException;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Progression implements INBTSerializable<CompoundNBT> {

    private List<Group> groups;

    public Progression() {
        groups = new ArrayList<>();
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void addGroup(UUID player, String name, String password) throws CommandException {
        if (groups.stream().anyMatch(group -> group.getMembers().stream().anyMatch(uuid -> uuid.equals(player)))) {
            throw new CommandException(new TranslationTextComponent("command.delivery.already_in_another_group"));
        }

        if (groups.stream().anyMatch(group -> group.getName().equals(name))) {
            throw new CommandException(new TranslationTextComponent("command.delivery.group_already_exists"));
        }
        Group group = new Group(name, password);
        group.addMember(player);
        groups.add(group);
    }

    public void joinGroup(UUID player, String name, String password) throws CommandException {
        Optional<Group> optionalGroup = groups.stream().filter(g -> g.getName().equals(name)).findAny();

        if (!optionalGroup.isPresent()) {
            throw new CommandException(new TranslationTextComponent("command.delivery.group_not_found", name));
        }

        Group group = optionalGroup.get();

        if (!group.getPassword().equals(password)) {
            throw new CommandException(new TranslationTextComponent("command.delivery.wrong_password"));
        }

        group.addMember(player);
    }

    public Group leaveGroup(UUID player) throws CommandException {
        Optional<Group> optionalGroup = groups.stream().filter(group -> group.getMembers().stream().anyMatch(uuid -> uuid.equals(player))).findAny();

        if (!optionalGroup.isPresent()) {
            throw new CommandException(new TranslationTextComponent("command.delivery.player_not_in_group"));
        }

        Group group = optionalGroup.get();
        group.removeMember(player);
        return group;
    }

    public void removeGroup(String name, String password) throws CommandException {
        Optional<Group> optionalGroup = groups.stream().filter(g -> g.getName().equals(name)).findAny();

        if (!optionalGroup.isPresent()) {
            throw new CommandException(new TranslationTextComponent("command.delivery.group_not_found", name));
        }

        Group group = optionalGroup.get();

        if (!group.getPassword().equals(password)) {
            throw new CommandException(new TranslationTextComponent("command.delivery.wrong_password"));
        }

        if (!group.getMembers().isEmpty()) {
            throw new CommandException(new TranslationTextComponent("command.delivery.group_has_members"));
        }

        groups.removeIf(g -> g.getName().equals(name));
    }

    public void removeGroup(String name) throws CommandException {
        Optional<Group> optionalGroup = groups.stream().filter(g -> g.getName().equals(name)).findAny();

        if (!optionalGroup.isPresent()) {
            throw new CommandException(new TranslationTextComponent("command.delivery.group_not_found", name));
        }

        groups.removeIf(g -> g.getName().equals(name));
    }

    public Group getGroup(String name) throws CommandException {
        Optional<Group> optionalGroup = groups.stream().filter(group -> group.getName().equals(name)).findAny();

        if (!optionalGroup.isPresent()) {
            throw new CommandException(new TranslationTextComponent("command.delivery.group_not_found", name));
        }

        return optionalGroup.get();
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT compound = new CompoundNBT();

        ListNBT groupList = new ListNBT();
        for (Group group : groups) {
            groupList.add(group.serializeNBT());
        }
        compound.put("Groups", groupList);

        return compound;
    }

    @Override
    public void deserializeNBT(CompoundNBT compound) {
        ListNBT groupList = compound.getList("Groups", 10);
        groups = new ArrayList<>();
        for (int i = 0; i < groupList.size(); i++) {
            Group group = new Group();
            group.deserializeNBT(groupList.getCompound(i));
            groups.add(group);
        }
    }
}
