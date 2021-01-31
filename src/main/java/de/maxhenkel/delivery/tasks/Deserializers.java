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

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Item.class, Item.DESERIALIZER);
        gsonBuilder.registerTypeAdapter(Fluid.class, Fluid.DESERIALIZER);
        gsonBuilder.registerTypeAdapter(ItemStack.class, ITEM_STACK_DESERIALIZER);

        return gsonBuilder.create();
    }

}
