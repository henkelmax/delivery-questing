package de.maxhenkel.delivery.tasks;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import de.maxhenkel.corelib.tag.TagUtils;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;

public class Fluid {

    private ITag<net.minecraft.fluid.Fluid> fluid;
    private long amount;

    public Fluid(ITag<net.minecraft.fluid.Fluid> fluid, long amount) {
        this.fluid = fluid;
        this.amount = amount;
    }

    private Fluid() {

    }

    public ITag<net.minecraft.fluid.Fluid> getFluid() {
        return fluid;
    }

    public long getAmount() {
        return amount;
    }

    public static final JsonDeserializer<Fluid> DESERIALIZER = (json, typeOfT, context) -> {
        JsonObject obj = json.getAsJsonObject();

        Fluid fluid = new Fluid();

        if (obj.has("tag")) {
            fluid.fluid = FluidTags.getCollection().get(new ResourceLocation(obj.get("tag").getAsString()));
        } else if (obj.has("fluid")) {
            fluid.fluid = TagUtils.getFluid(obj.get("fluid").getAsString(), true);
        }

        if (obj.has("amount")) {
            fluid.amount = obj.get("amount").getAsLong();
        } else {
            fluid.amount = 1000;
        }

        return fluid;
    };

}
