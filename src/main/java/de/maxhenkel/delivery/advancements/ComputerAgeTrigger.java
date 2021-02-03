package de.maxhenkel.delivery.advancements;

import com.google.gson.JsonObject;
import de.maxhenkel.delivery.Main;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.util.ResourceLocation;

public class ComputerAgeTrigger extends AbstractCriterionTrigger<ComputerAgeTrigger.Instance> {

    private static final ResourceLocation ID = new ResourceLocation(Main.MODID, "reach_computer_age");

    public ComputerAgeTrigger() {
    }

    public ResourceLocation getId() {
        return ID;
    }

    public ComputerAgeTrigger.Instance deserializeTrigger(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser) {
        return new ComputerAgeTrigger.Instance(entityPredicate);
    }

    public void trigger(ServerPlayerEntity player, int level) {
        triggerListeners(player, (instance) -> instance.test(level));
    }

    public static class Instance extends CriterionInstance {

        public Instance(EntityPredicate.AndPredicate player) {
            super(ComputerAgeTrigger.ID, player);
        }

        public boolean test(int level) {
            return level >= Main.SERVER_CONFIG.minComputerLevel.get();
        }
    }
}