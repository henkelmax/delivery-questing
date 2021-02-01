package de.maxhenkel.delivery.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.maxhenkel.corelib.net.NetUtils;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.net.MessageTaskCompletedToast;
import de.maxhenkel.delivery.tasks.Group;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;

public class TestCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralArgumentBuilder<CommandSource> literalBuilder = Commands.literal("delivery");

        literalBuilder.then(Commands.literal("test").then(Commands.literal("task_complete").executes(context -> {
            ServerPlayerEntity player = context.getSource().asPlayer();
            NetUtils.sendTo(Main.SIMPLE_CHANNEL, player, new MessageTaskCompletedToast(Main.TASK_MANAGER.getTasks().get(0)));
            return 1;
        })));

        literalBuilder.then(Commands.literal("test").then(Commands.literal("set_xp").then(Commands.argument("xp", IntegerArgumentType.integer(0)).executes(context -> {
            ServerPlayerEntity player = context.getSource().asPlayer();
            int xp = IntegerArgumentType.getInteger(context, "xp");
            Group playerGroup = Main.getProgression(player).getPlayerGroup(player.getUniqueID());
            playerGroup.setExperience(xp);
            return 1;
        }))));

        literalBuilder.then(Commands.literal("test").then(Commands.literal("add_xp").then(Commands.argument("xp", IntegerArgumentType.integer(0)).executes(context -> {
            ServerPlayerEntity player = context.getSource().asPlayer();
            int xp = IntegerArgumentType.getInteger(context, "xp");
            Group playerGroup = Main.getProgression(player).getPlayerGroup(player.getUniqueID());
            playerGroup.addExperience(xp);
            return 1;
        }))));

        literalBuilder.then(Commands.literal("test").then(Commands.literal("set_balance").then(Commands.argument("balance", IntegerArgumentType.integer(0)).executes(context -> {
            ServerPlayerEntity player = context.getSource().asPlayer();
            int balance = IntegerArgumentType.getInteger(context, "balance");
            Group playerGroup = Main.getProgression(player).getPlayerGroup(player.getUniqueID());
            playerGroup.setBalance(balance);
            return 1;
        }))));

        literalBuilder.then(Commands.literal("test").then(Commands.literal("add_balance").then(Commands.argument("balance", IntegerArgumentType.integer(0)).executes(context -> {
            ServerPlayerEntity player = context.getSource().asPlayer();
            int balance = IntegerArgumentType.getInteger(context, "balance");
            Group playerGroup = Main.getProgression(player).getPlayerGroup(player.getUniqueID());
            playerGroup.addBalance(balance);
            return 1;
        }))));

        literalBuilder.then(Commands.literal("test").then(Commands.literal("generate_task").executes(context -> {
            ServerPlayerEntity player = context.getSource().asPlayer();
            Group playerGroup = Main.getProgression(player).getPlayerGroup(player.getUniqueID());
            playerGroup.generateEMailTask();
            return 1;
        })));

        dispatcher.register(literalBuilder);
    }

}
