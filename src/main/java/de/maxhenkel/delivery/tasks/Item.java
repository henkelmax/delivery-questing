package de.maxhenkel.delivery.tasks;

import com.google.gson.*;
import de.maxhenkel.corelib.tag.TagUtils;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;

public class Item {

    private ITag<net.minecraft.item.Item> item;
    private int amount;

    public Item(ITag<net.minecraft.item.Item> item, int amount) {
        this.item = item;
        this.amount = amount;
    }

    private Item() {

    }

    public ITag<net.minecraft.item.Item> getItem() {
        return item;
    }

    public int getAmount() {
        return amount;
    }

    public static final JsonDeserializer<Item> DESERIALIZER = (json, typeOfT, context) -> {
        JsonObject obj = json.getAsJsonObject();
        Item item = new Item();
        if (obj.has("tag")) {
            item.item = ItemTags.getCollection().get(new ResourceLocation(obj.get("tag").getAsString()));
        } else if (obj.has("item")) {
            item.item = TagUtils.getItem(obj.get("item").getAsString(), true);
        }
        if (obj.has("amount")) {
            item.amount = obj.get("amount").getAsInt();
        } else {
            item.amount = 1;
        }

        return item;
    };

}
