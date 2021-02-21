package de.maxhenkel.delivery.tasks;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.maxhenkel.corelib.helpers.AbstractStack;
import de.maxhenkel.corelib.helpers.WrappedItemStack;
import de.maxhenkel.corelib.tag.TagUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.tags.ITag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemElement extends TaskElement<net.minecraft.item.Item> {

    public ItemElement(String tag, ITag.INamedTag<net.minecraft.item.Item> item, long amount) {
        super(tag, item, amount);
    }

    public ItemElement() {

    }

    @Override
    protected ITag.INamedTag<net.minecraft.item.Item> getTag(String tag) {
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
                item.nbt = JsonToNBT.getTagFromJson(obj.get("nbt").getAsString());
            } catch (CommandSyntaxException e) {
                throw new JsonParseException(e);
            }
        }

        return item;
    };

}
