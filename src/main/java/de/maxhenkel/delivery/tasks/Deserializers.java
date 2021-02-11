package de.maxhenkel.delivery.tasks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class Deserializers {

    public static final JsonDeserializer<ItemStack> ITEM_STACK_DESERIALIZER = (json, typeOfT, context) -> {
        JsonObject obj = json.getAsJsonObject();
        net.minecraft.item.Item value = ForgeRegistries.ITEMS.getValue(new ResourceLocation(obj.get("item").getAsString()));
        int amount = obj.get("amount").getAsInt();
        return new ItemStack(value, amount);
    };

    public static final JsonDeserializer<net.minecraft.item.Item> ITEM_DESERIALIZER = (json, typeOfT, context) -> {
        return ForgeRegistries.ITEMS.getValue(new ResourceLocation(json.getAsString()));
    };

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(ItemElement.class, ItemElement.DESERIALIZER);
        gsonBuilder.registerTypeAdapter(FluidElement.class, FluidElement.DESERIALIZER);
        gsonBuilder.registerTypeAdapter(net.minecraft.item.Item.class, ITEM_DESERIALIZER);
        gsonBuilder.registerTypeAdapter(ItemStack.class, ITEM_STACK_DESERIALIZER);

        return gsonBuilder.create();
    }

}
