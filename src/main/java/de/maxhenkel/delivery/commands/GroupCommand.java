package de.maxhenkel.delivery.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.maxhenkel.corelib.CommonUtils;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.blocks.ModBlocks;
import de.maxhenkel.delivery.gui.ContractContainer;
import de.maxhenkel.delivery.gui.containerprovider.TaskContainerProvider;
import de.maxhenkel.delivery.items.ModItems;
import de.maxhenkel.delivery.tasks.Group;
import de.maxhenkel.delivery.tasks.Progression;
import de.maxhenkel.delivery.tasks.Task;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

public class GroupCommand {

    public static final LevelResource PROGRESSION_BACKUPS = new LevelResource("progression_backups");
    public static final SimpleDateFormat BACKUP_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> literalBuilder = Commands.literal("group");

        literalBuilder.then(Commands.literal("create").then(Commands.argument("name", StringArgument.create()).then(Commands.argument("password", StringArgument.create()).executes(context -> {
            ServerPlayer player = context.getSource().getPlayerOrException();
            Progression tasks = Main.getProgression(context.getSource().getServer());
            String name = StringArgument.string(context, "name");
            tasks.addGroup(player.getUUID(), name, StringArgument.string(context, "password"));
            giveItem(player, new ItemStack(ModBlocks.MAILBOX));
            giveItem(player, new ItemStack(ModBlocks.MAILBOX_POST));
            giveItem(player, new ItemStack(ModBlocks.BULLETIN_BOARD));
            context.getSource().sendSuccess(new TranslatableComponent("command.delivery.group_created", name), false);
            return 1;
        }))));

        literalBuilder.then(Commands.literal("join").then(Commands.argument("name", StringArgument.create()).then(Commands.argument("password", StringArgument.create()).executes(context -> {
            ServerPlayer player = context.getSource().getPlayerOrException();
            Progression tasks = Main.getProgression(context.getSource().getServer());
            String name = StringArgument.string(context, "name");
            tasks.joinGroup(player.getUUID(), name, StringArgument.string(context, "password"));
            context.getSource().sendSuccess(new TranslatableComponent("command.delivery.group_joined", name), false);
            return 1;
        }))));

        literalBuilder.then(Commands.literal("leave").executes(context -> {
            ServerPlayer player = context.getSource().getPlayerOrException();
            Progression tasks = Main.getProgression(context.getSource().getServer());
            Group group = tasks.leaveGroup(player.getUUID());
            context.getSource().sendSuccess(new TranslatableComponent("command.delivery.group_left", group.getName()), false);
            return 1;
        }));

        literalBuilder.then(Commands.literal("remove").then(Commands.argument("name", StringArgument.create()).then(Commands.argument("password", StringArgument.create()).executes(context -> {
            Progression tasks = Main.getProgression(context.getSource().getServer());
            String name = StringArgument.string(context, "name");
            tasks.removeGroup(name, StringArgument.string(context, "password"));
            context.getSource().sendSuccess(new TranslatableComponent("command.delivery.group_removed", name), false);
            return 1;
        }))));

        literalBuilder.then(Commands.literal("remove").then(Commands.argument("name", StringArgument.create()).requires((commandSource) -> commandSource.hasPermission(2)).executes(context -> {
            Progression tasks = Main.getProgression(context.getSource().getServer());
            String name = StringArgument.string(context, "name");
            tasks.removeGroup(name);
            context.getSource().sendSuccess(new TranslatableComponent("command.delivery.group_removed", name), false);
            return 1;
        })));

        literalBuilder.then(Commands.literal("listgroups").requires((commandSource) -> commandSource.hasPermission(2)).executes(context -> {
            Progression tasks = Main.getProgression(context.getSource().getServer());

            if (tasks.getGroups().isEmpty()) {
                context.getSource().sendSuccess(new TranslatableComponent("command.delivery.no_groups"), false);
            } else {
                for (Group group : tasks.getGroups()) {
                    context.getSource().sendSuccess(new TextComponent(group.getName()), false);
                }
            }

            return 1;
        }));

        literalBuilder.then(Commands.literal("listmembers").then(Commands.argument("name", StringArgument.create()).executes(context -> {
            Progression tasks = Main.getProgression(context.getSource().getServer());
            String name = StringArgument.string(context, "name");

            Group group = tasks.getGroup(name);
            if (!context.getSource().hasPermission(2)) {
                ServerPlayer player = context.getSource().getPlayerOrException();
                if (!group.isMember(player.getUUID())) {
                    throw new CommandRuntimeException(new TranslatableComponent("command.delivery.no_member_of_group"));
                }
            }

            if (group.getMembers().isEmpty()) {
                context.getSource().sendSuccess(new TranslatableComponent("command.delivery.no_members"), false);
            } else {
                for (UUID member : group.getMembers()) {
                    ServerPlayer playerByUUID = context.getSource().getServer().getPlayerList().getPlayer(member);
                    if (playerByUUID != null) {
                        context.getSource().sendSuccess(new TextComponent("").append(playerByUUID.getDisplayName()).append(new TextComponent(" (" + member.toString() + ")")), false);
                    } else {
                        context.getSource().sendSuccess(new TextComponent(member.toString()), false);
                    }
                }
            }

            return 1;
        })));

        literalBuilder.then(Commands.literal("backup").then(Commands.literal("create").requires((commandSource) -> commandSource.hasPermission(2)).executes(context -> {
            Progression tasks = Main.getProgression(context.getSource().getServer());

            File backups = CommonUtils.getWorldFolder(context.getSource().getServer().getLevel(Level.OVERWORLD), PROGRESSION_BACKUPS);
            backups.mkdirs();
            String filename = BACKUP_FORMAT.format(Calendar.getInstance().getTime());
            try {
                NbtIo.writeCompressed(tasks.serializeNBT(), new File(backups, filename + ".dat"));
            } catch (IOException e) {
                e.printStackTrace();
                throw new CommandRuntimeException(new TranslatableComponent("command.delivery.backup_failed", e.getMessage()));
            }
            context.getSource().sendSuccess(new TranslatableComponent("command.delivery.backup_success", filename), false);
            return 1;
        })));

        literalBuilder.then(Commands.literal("backup").then(Commands.literal("load").then(Commands.argument("name", StringArgument.create()).requires((commandSource) -> commandSource.hasPermission(2)).executes(context -> {
            Progression tasks = Main.getProgression(context.getSource().getServer());

            File backups = CommonUtils.getWorldFolder(context.getSource().getServer().getLevel(Level.OVERWORLD), PROGRESSION_BACKUPS);

            String name = StringArgument.string(context, "name");
            try {
                CompoundTag compoundNBT = NbtIo.readCompressed(new File(backups, name + ".dat"));
                tasks.deserializeNBT(compoundNBT);
            } catch (IOException e) {
                e.printStackTrace();
                throw new CommandRuntimeException(new TranslatableComponent("command.delivery.backup_load_failed", e.getMessage()));
            }
            context.getSource().sendSuccess(new TranslatableComponent("command.delivery.backup_load_success"), false);
            return 1;
        }))));

        literalBuilder.then(Commands.literal("showtask").then(Commands.argument("taskid", UuidArgument.uuid()).executes(context -> {
            ServerPlayer player = context.getSource().getPlayerOrException();
            UUID taskID = UuidArgument.getUuid(context, "taskid");
            Task task = Main.TASK_MANAGER.getTask(taskID, 0);

            if (task == null) {
                throw new CommandRuntimeException(new TranslatableComponent("command.delivery.task_not_found"));
            }

            TaskContainerProvider.openGui(player, task, new TranslatableComponent(ModItems.CONTRACT.getDescriptionId()), ContractContainer::new);

            return 1;
        })));

        literalBuilder.then(Commands.literal("changepassword").then(Commands.argument("groupname", StringArgument.create()).then(Commands.argument("oldpassword", StringArgument.create()).then(Commands.argument("newpassword", StringArgument.create()).executes(context -> {
            Progression tasks = Main.getProgression(context.getSource().getServer());
            String name = StringArgument.string(context, "groupname");
            String oldPassword = StringArgument.string(context, "oldpassword");
            String newPassword = StringArgument.string(context, "newpassword");
            Group group = tasks.getGroup(name);
            if (!group.getPassword().equals(oldPassword)) {
                throw new CommandRuntimeException(new TranslatableComponent("command.delivery.wrong_password"));
            }
            group.setPassword(newPassword);
            context.getSource().sendSuccess(new TranslatableComponent("command.delivery.group_password_changed", name), false);
            return 1;
        })))));

        literalBuilder.then(Commands.literal("setpassword").then(Commands.argument("groupname", StringArgument.create()).then(Commands.argument("password", StringArgument.create()).requires((commandSource) -> commandSource.hasPermission(2)).executes(context -> {
            Progression tasks = Main.getProgression(context.getSource().getServer());
            String name = StringArgument.string(context, "groupname");
            String password = StringArgument.string(context, "password");
            Group group = tasks.getGroup(name);
            group.setPassword(password);
            context.getSource().sendSuccess(new TranslatableComponent("command.delivery.group_password_changed", name), false);
            return 1;
        }))));

        dispatcher.register(literalBuilder);
    }

    public static void giveItem(ServerPlayer player, ItemStack stack) {
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }

}
