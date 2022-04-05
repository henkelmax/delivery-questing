package de.maxhenkel.delivery.tasks;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import de.maxhenkel.corelib.helpers.AbstractStack;
import de.maxhenkel.corelib.helpers.WrappedFluidStack;
import de.maxhenkel.corelib.tag.Tag;
import de.maxhenkel.corelib.tag.TagUtils;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

public class FluidElement extends TaskElement<net.minecraft.world.level.material.Fluid> {

    public FluidElement(String tag, Tag<Fluid> item, long amount) {
        super(tag, item, amount);
    }

    public FluidElement() {

    }

    @Override
    @Nullable
    protected Tag<net.minecraft.world.level.material.Fluid> getTag(String tag) {
        return TagUtils.getFluid(tag, true);
    }

    @Override
    protected Fluid getDefault() {
        return Fluids.EMPTY;
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
