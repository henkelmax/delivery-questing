package de.maxhenkel.delivery.advancements;

import com.google.gson.JsonObject;
import de.maxhenkel.delivery.Main;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.util.ResourceLocation;

public class AcceptComputerContractTrigger extends AbstractCriterionTrigger<AcceptComputerContractTrigger.Instance> {

    private static final ResourceLocation ID = new ResourceLocation(Main.MODID, "accept_contract_computer");

    public AcceptComputerContractTrigger() {
    }

    public ResourceLocation getId() {
        return ID;
    }

    public AcceptComputerContractTrigger.Instance deserializeTrigger(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser) {
        return new AcceptComputerContractTrigger.Instance(entityPredicate);
    }

    public void trigger(ServerPlayerEntity player) {
        triggerListeners(player, (instance) -> true);
    }

    public static class Instance extends CriterionInstance {
        public Instance(EntityPredicate.AndPredicate player) {
            super(AcceptComputerContractTrigger.ID, player);
        }
    }
}