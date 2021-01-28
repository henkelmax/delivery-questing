package de.maxhenkel.delivery.tasks;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import de.maxhenkel.corelib.tag.TagUtils;
import net.minecraft.tags.ITag;

public class Item extends TaskElement<net.minecraft.item.Item> {

    public Item(String tag, ITag.INamedTag<net.minecraft.item.Item> item, long amount) {
        super(tag, item, amount);
    }

    public Item() {

    }

    @Override
    protected ITag.INamedTag<net.minecraft.item.Item> getTag(String tag) {
        return TagUtils.getItem(tag, true);
    }

    public static final JsonDeserializer<Item> DESERIALIZER = (json, typeOfT, context) -> {
        JsonObject obj = json.getAsJsonObject();
        Item item = new Item();
        if (obj.has("tag")) {
            item.tag = "#" + obj.get("tag").getAsString();
        } else if (obj.has("item")) {
            item.tag = obj.get("item").getAsString();
        }

        if (item.tag != null) {
            item.item = item.getTag(item.tag);
        }

        if (obj.has("amount")) {
            item.amount = obj.get("amount").getAsLong();
        } else {
            item.amount = 1L;
        }

        return item;
    };

}
