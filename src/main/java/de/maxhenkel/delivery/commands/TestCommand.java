package de.maxhenkel.delivery.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.maxhenkel.corelib.net.NetUtils;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.net.MessageTaskCompletedToast;
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

        dispatcher.register(literalBuilder);
    }

}
