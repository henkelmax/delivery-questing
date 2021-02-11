package de.maxhenkel.delivery.tasks;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import de.maxhenkel.corelib.helpers.AbstractStack;
import de.maxhenkel.corelib.helpers.WrappedFluidStack;
import de.maxhenkel.corelib.tag.TagUtils;
import net.minecraft.tags.ITag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

public class FluidElement extends TaskElement<net.minecraft.fluid.Fluid> {

    public FluidElement(String tag, ITag.INamedTag<net.minecraft.fluid.Fluid> item, long amount) {
        super(tag, item, amount);
    }

    public FluidElement() {

    }

    @Override
    @Nullable
    protected ITag.INamedTag<net.minecraft.fluid.Fluid> getTag(String tag) {
        return TagUtils.getFluid(tag, true);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public AbstractStack<?> getAbstractStack() {
        return new WrappedFluidStack(new FluidStack(getCurrentDisplayedElement(), (int) amount, nbt));
    }

    public static final JsonDeserializer<FluidElement> DESERIALIZER = (json, typeOfT, context) -> {
        JsonObject obj = json.getAsJsonObject();

        FluidElement fluid = new FluidElement();

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
