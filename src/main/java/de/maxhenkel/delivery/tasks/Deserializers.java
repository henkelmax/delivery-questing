package de.maxhenkel.delivery.tasks;

import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.maxhenkel.delivery.Main;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Random;

public class Deserializers {

    public static final JsonDeserializer<ItemStack> ITEM_STACK_DESERIALIZER = (json, typeOfT, context) -> {
        JsonObject obj = json.getAsJsonObject();
        net.minecraft.world.item.Item value = ForgeRegistries.ITEMS.getValue(new ResourceLocation(obj.get("item").getAsString()));
        int amount = obj.has("amount") ? obj.get("amount").getAsInt() : 1;
        ItemStack stack = new ItemStack(value, amount);
        if (obj.has("nbt")) {
            try {
                stack.setTag(TagParser.parseTag(obj.get("nbt").getAsString()));
            } catch (CommandSyntaxException e) {
                Main.LOGGER.warn("Failed to load NBT from stack");
            }
        }
        return stack;
    };

    public static final JsonDeserializer<net.minecraft.world.item.Item> ITEM_DESERIALIZER = (json, typeOfT, context) -> {
        return ForgeRegistries.ITEMS.getValue(new ResourceLocation(json.getAsString()));
    };

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(ItemElement.class, ItemElement.DESERIALIZER);
        gsonBuilder.registerTypeAdapter(FluidElement.class, FluidElement.DESERIALIZER);
        gsonBuilder.registerTypeAdapter(net.minecraft.world.item.Item.class, ITEM_DESERIALIZER);
        gsonBuilder.registerTypeAdapter(ItemStack.class, ITEM_STACK_DESERIALIZER);
        gsonBuilder.addDeserializationExclusionStrategy(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                return false;
            }

            @Override
            public boolean shouldSkipClass(Class<?> aClass) {
                return aClass.equals(Random.class);
            }
        });

        return gsonBuilder.create();
    }

}
