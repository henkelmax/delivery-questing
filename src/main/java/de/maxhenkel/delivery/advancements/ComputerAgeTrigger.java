package de.maxhenkel.delivery.advancements;

import com.google.gson.JsonObject;
import de.maxhenkel.delivery.Main;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class ComputerAgeTrigger extends SimpleCriterionTrigger<ComputerAgeTrigger.Instance> {

    private static final ResourceLocation ID = new ResourceLocation(Main.MODID, "reach_computer_age");

    public ComputerAgeTrigger() {
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public ComputerAgeTrigger.Instance createInstance(JsonObject json, EntityPredicate.Composite entityPredicate, DeserializationContext conditionsParser) {
        return new ComputerAgeTrigger.Instance(entityPredicate);
    }

    public void trigger(ServerPlayer player, int level) {
        trigger(player, (instance) -> instance.test(level));
    }

    public static class Instance extends AbstractCriterionTriggerInstance {

        public Instance(EntityPredicate.Composite player) {
            super(ComputerAgeTrigger.ID, player);
        }

        public boolean test(int level) {
            return level >= Main.SERVER_CONFIG.minComputerLevel.get();
        }
    }
}