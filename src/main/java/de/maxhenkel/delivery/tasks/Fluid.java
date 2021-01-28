package de.maxhenkel.delivery.tasks;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import de.maxhenkel.corelib.tag.TagUtils;
import net.minecraft.tags.ITag;

import javax.annotation.Nullable;

public class Fluid extends TaskElement<net.minecraft.fluid.Fluid> {

    public Fluid(String tag, ITag.INamedTag<net.minecraft.fluid.Fluid> item, long amount) {
        super(tag, item, amount);
    }

    public Fluid() {

    }

    @Override
    @Nullable
    protected ITag.INamedTag<net.minecraft.fluid.Fluid> getTag(String tag) {
        return TagUtils.getFluid(tag, true);
    }

    public static final JsonDeserializer<Fluid> DESERIALIZER = (json, typeOfT, context) -> {
        JsonObject obj = json.getAsJsonObject();

        Fluid fluid = new Fluid();

        if (obj.has("tag")) {
            fluid.tag = "#" + obj.get("tag");
        } else if (obj.has("fluid")) {
            fluid.tag = obj.get("fluid").getAsString();
        }

        if (fluid.tag != null) {
            fluid.item = fluid.getTag(fluid.tag);
        }

        if (obj.has("amount")) {
            fluid.amount = obj.get("amount").getAsLong();
        } else {
            fluid.amount = 1000L;
        }

        return fluid;
    };

}
