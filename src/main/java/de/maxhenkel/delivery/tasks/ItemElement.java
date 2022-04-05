package de.maxhenkel.delivery.tasks;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.maxhenkel.corelib.helpers.AbstractStack;
import de.maxhenkel.corelib.helpers.WrappedItemStack;
import de.maxhenkel.corelib.tag.Tag;
import de.maxhenkel.corelib.tag.TagUtils;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemElement extends TaskElement<Item> {

    public ItemElement(String tag, Tag<Item> item, long amount) {
        super(tag, item, amount);
    }

    public ItemElement() {

    }

    @Override
    protected Tag<Item> getTag(String tag) {
        return TagUtils.getItem(tag, true);
    }

    @Override
    protected Item getDefault() {
        return Items.AIR;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public AbstractStack<?> getAbstractStack() {
        ItemStack itemStack = new ItemStack(getCurrentDisplayedElement(), (int) amount);
        if (nbt != null) {
            itemStack.setTag(nbt);
        }
        return new WrappedItemStack(itemStack);
    }

    public static final JsonDeserializer<ItemElement> DESERIALIZER = (json, typeOfT, context) -> {
        JsonObject obj = json.getAsJsonObject();
        ItemElement item = new ItemElement();
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

        if (obj.has("nbt")) {
            try {
                item.nbt = TagParser.parseTag(obj.get("nbt").getAsString());
            } catch (CommandSyntaxException e) {
                throw new JsonParseException(e);
            }
        }

        return item;
    };

}
