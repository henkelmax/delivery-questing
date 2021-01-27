package de.maxhenkel.delivery.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.maxhenkel.corelib.CommonUtils;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.capability.Group;
import de.maxhenkel.delivery.capability.Tasks;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.storage.FolderName;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

public class GroupCommand {

    public static final FolderName GROUP_BACKUPS = new FolderName("group_backups");
    public static final SimpleDateFormat BACKUP_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralArgumentBuilder<CommandSource> literalBuilder = Commands.literal("group");

        literalBuilder.then(Commands.literal("create").then(Commands.argument("name", StringArgument.create()).then(Commands.argument("password", StringArgument.create()).executes(context -> {
            ServerPlayerEntity player = context.getSource().asPlayer();
            Tasks tasks = Main.getTasks(context.getSource().getServer());
            String name = StringArgument.string(context, "name");
            tasks.addGroup(player.getUniqueID(), name, StringArgument.string(context, "password"));
            context.getSource().sendFeedback(new TranslationTextComponent("command.delivery.group_created", name), false);
            return 1;
        }))));

        literalBuilder.then(Commands.literal("join").then(Commands.argument("name", StringArgument.create()).then(Commands.argument("password", StringArgument.create()).executes(context -> {
            ServerPlayerEntity player = context.getSource().asPlayer();
            Tasks tasks = Main.getTasks(context.getSource().getServer());
            String name = StringArgument.string(context, "name");
            tasks.joinGroup(player.getUniqueID(), name, StringArgument.string(context, "password"));
            context.getSource().sendFeedback(new TranslationTextComponent("command.delivery.group_joined", name), false);
            return 1;
        }))));

        literalBuilder.then(Commands.literal("leave").executes(context -> {
            ServerPlayerEntity player = context.getSource().asPlayer();
            Tasks tasks = Main.getTasks(context.getSource().getServer());
            Group group = tasks.leaveGroup(player.getUniqueID());
            context.getSource().sendFeedback(new TranslationTextComponent("command.delivery.group_left", group.getName()), false);
            return 1;
        }));

        literalBuilder.then(Commands.literal("remove").then(Commands.argument("name", StringArgument.create()).then(Commands.argument("password", StringArgument.create()).executes(context -> {
            Tasks tasks = Main.getTasks(context.getSource().getServer());
            String name = StringArgument.string(context, "name");
            tasks.removeGroup(name, StringArgument.string(context, "password"));
            context.getSource().sendFeedback(new TranslationTextComponent("command.delivery.group_removed", name), false);
            return 1;
        }))));

        literalBuilder.then(Commands.literal("remove").then(Commands.argument("name", StringArgument.create()).requires((commandSource) -> commandSource.hasPermissionLevel(2)).executes(context -> {
            Tasks tasks = Main.getTasks(context.getSource().getServer());
            String name = StringArgument.string(context, "name");
            tasks.removeGroup(name);
            context.getSource().sendFeedback(new TranslationTextComponent("command.delivery.group_removed", name), false);
            return 1;
        })));

        literalBuilder.then(Commands.literal("listgroups").requires((commandSource) -> commandSource.hasPermissionLevel(2)).executes(context -> {
            Tasks tasks = Main.getTasks(context.getSource().getServer());

            if (tasks.getGroups().isEmpty()) {
                context.getSource().sendFeedback(new TranslationTextComponent("command.delivery.no_groups"), false);
            } else {
                for (Group group : tasks.getGroups()) {
                    context.getSource().sendFeedback(new StringTextComponent(group.getName()), false);
                }
            }

            return 1;
        }));

        literalBuilder.then(Commands.literal("listmembers").then(Commands.argument("name", StringArgument.create()).executes(context -> {
            Tasks tasks = Main.getTasks(context.getSource().getServer());
            String name = StringArgument.string(context, "name");

            Group group = tasks.getGroup(name);
            if (!context.getSource().hasPermissionLevel(2)) {
                ServerPlayerEntity player = context.getSource().asPlayer();
                if (!group.isMember(player.getUniqueID())) {
                    throw new CommandException(new TranslationTextComponent("command.delivery.no_member_of_group"));
                }
            }

            if (group.getMembers().isEmpty()) {
                context.getSource().sendFeedback(new TranslationTextComponent("command.delivery.no_members"), false);
            } else {
                for (UUID member : group.getMembers()) {
                    ServerPlayerEntity playerByUUID = context.getSource().getServer().getPlayerList().getPlayerByUUID(member);
                    if (playerByUUID != null) {
                        context.getSource().sendFeedback(new StringTextComponent("").append(playerByUUID.getDisplayName()).append(new StringTextComponent(" (" + member.toString() + ")")), false);
                    } else {
                        context.getSource().sendFeedback(new StringTextComponent(member.toString()), false);
                    }
                }
            }

            return 1;
        })));

        literalBuilder.then(Commands.literal("backup").then(Commands.literal("create").requires((commandSource) -> commandSource.hasPermissionLevel(2)).executes(context -> {
            Tasks tasks = Main.getTasks(context.getSource().getServer());

            File backups = CommonUtils.getWorldFolder(context.getSource().getServer().getWorld(World.OVERWORLD), GROUP_BACKUPS);
            backups.mkdirs();
            String filename = BACKUP_FORMAT.format(Calendar.getInstance().getTime());
            try {
                CompressedStreamTools.writeCompressed(tasks.serializeNBT(), new File(backups, filename + ".dat"));
            } catch (IOException e) {
                e.printStackTrace();
                throw new CommandException(new TranslationTextComponent("command.delivery.backup_failed", e.getMessage()));
            }
            context.getSource().sendFeedback(new TranslationTextComponent("command.delivery.backup_success", filename), false);
            return 1;
        })));

        literalBuilder.then(Commands.literal("backup").then(Commands.literal("load").then(Commands.argument("name", StringArgument.create()).requires((commandSource) -> commandSource.hasPermissionLevel(2)).executes(context -> {
            Tasks tasks = Main.getTasks(context.getSource().getServer());

            File backups = CommonUtils.getWorldFolder(context.getSource().getServer().getWorld(World.OVERWORLD), GROUP_BACKUPS);

            String name = StringArgument.string(context, "name");
            try {
                CompoundNBT compoundNBT = CompressedStreamTools.readCompressed(new File(backups, name + ".dat"));
                tasks.deserializeNBT(compoundNBT);
            } catch (IOException e) {
                e.printStackTrace();
                throw new CommandException(new TranslationTextComponent("command.delivery.backup_load_failed", e.getMessage()));
            }
            context.getSource().sendFeedback(new TranslationTextComponent("command.delivery.backup_load_success"), false);
            return 1;
        }))));

        dispatcher.register(literalBuilder);
    }

}
