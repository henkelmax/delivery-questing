package de.maxhenkel.delivery.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;

import java.util.Arrays;
import java.util.Collection;

public class StringArgument implements ArgumentType<String> {
    private static final Collection<String> EXAMPLES = Arrays.asList("example");

    public static String string(CommandContext<CommandSource> context, String name) {
        return context.getArgument(name, String.class);
    }

    public static StringArgument create() {
        return new StringArgument();
    }

    @Override
    public String parse(StringReader reader) {
        return reader.readUnquotedString();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
