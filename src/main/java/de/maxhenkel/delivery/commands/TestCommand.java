package de.maxhenkel.delivery.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.maxhenkel.corelib.net.NetUtils;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.net.MessageTaskCompletedToast;
import de.maxhenkel.delivery.tasks.ActiveTask;
import de.maxhenkel.delivery.tasks.Group;
import de.maxhenkel.delivery.tasks.TaskProgress;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TestCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> literalBuilder = Commands.literal("delivery").requires(commandSource -> commandSource.hasPermission(2));

        literalBuilder.then(Commands.literal("test").then(Commands.literal("task_complete").executes(context -> {
            ServerPlayer player = context.getSource().getPlayerOrException();
            NetUtils.sendTo(Main.SIMPLE_CHANNEL, player, new MessageTaskCompletedToast(Main.TASK_MANAGER.getTasks().get(0)));
            return 1;
        })));

        literalBuilder.then(Commands.literal("set_xp").then(Commands.argument("xp", IntegerArgumentType.integer(0)).executes(context -> {
            ServerPlayer player = context.getSource().getPlayerOrException();
            int xp = IntegerArgumentType.getInteger(context, "xp");
            Group playerGroup = Main.getProgression(player).getPlayerGroup(player.getUUID());
            playerGroup.setExperience(xp);
            return 1;
        })));

        literalBuilder.then(Commands.literal("add_xp").then(Commands.argument("xp", IntegerArgumentType.integer(0)).executes(context -> {
            ServerPlayer player = context.getSource().getPlayerOrException();
            int xp = IntegerArgumentType.getInteger(context, "xp");
            Group playerGroup = Main.getProgression(player).getPlayerGroup(player.getUUID());
            playerGroup.addExperience(xp);
            return 1;
        })));

        literalBuilder.then(Commands.literal("set_balance").then(Commands.argument("balance", IntegerArgumentType.integer(0)).executes(context -> {
            ServerPlayer player = context.getSource().getPlayerOrException();
            int balance = IntegerArgumentType.getInteger(context, "balance");
            Group playerGroup = Main.getProgression(player).getPlayerGroup(player.getUUID());
            playerGroup.setBalance(balance);
            return 1;
        })));

        literalBuilder.then(Commands.literal("add_balance").then(Commands.argument("balance", IntegerArgumentType.integer(0)).executes(context -> {
            ServerPlayer player = context.getSource().getPlayerOrException();
            int balance = IntegerArgumentType.getInteger(context, "balance");
            Group playerGroup = Main.getProgression(player).getPlayerGroup(player.getUUID());
            playerGroup.addBalance(balance);
            return 1;
        })));

        literalBuilder.then(Commands.literal("active_tasks").executes(context -> {
            ServerPlayer player = context.getSource().getPlayerOrException();
            Group playerGroup = Main.getProgression(player).getPlayerGroup(player.getUUID());
            List<ActiveTask> tasks = playerGroup.getActiveTasks().getTasks();
            if (tasks.isEmpty()) {
                context.getSource().sendSuccess(new TranslatableComponent("command.delivery.no_active_tasks"), false);
            } else {
                for (ActiveTask task : tasks) {
                    player.sendMessage(new TranslatableComponent("message.delivery.task_id",
                            new TextComponent(task.getTask().getName()).withStyle(ChatFormatting.GREEN),
                            new TextComponent(task.getTask().getId().toString()).withStyle(style -> style
                                    .applyFormat(ChatFormatting.GRAY)
                                    .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, task.getTask().getId().toString()))
                                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableComponent("tooltip.delivery.copy_to_clipboard")))).withStyle(ChatFormatting.GRAY)
                    ), Util.NIL_UUID);
                }
            }

            return 1;
        }));

        literalBuilder.then(Commands.literal("complete_task").then(Commands.argument("taskid", UuidArgument.uuid()).executes(context -> {
            ServerPlayer player = context.getSource().getPlayerOrException();
            Group playerGroup = Main.getProgression(player).getPlayerGroup(player.getUUID());
            UUID taskID = UuidArgument.getUuid(context, "taskid");
            Optional<TaskProgress> task = playerGroup.getTasks().stream().filter(t -> t.getTaskID().equals(taskID)).findFirst();

            if (task.isPresent()) {
                playerGroup.onTaskCompleted(task.get());
                playerGroup.getTasks().remove(task.get());
                context.getSource().sendSuccess(new TranslatableComponent("command.delivery.complete_task.success"), false);
            } else {
                context.getSource().sendFailure(new TranslatableComponent("command.delivery.complete_task.not_found"));
            }
            return 1;
        })));

        dispatcher.register(literalBuilder);
    }

}
