package de.maxhenkel.delivery.advancements;

import com.google.gson.JsonObject;
import de.maxhenkel.delivery.Main;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class AcceptComputerContractTrigger extends SimpleCriterionTrigger<AcceptComputerContractTrigger.Instance> {

    private static final ResourceLocation ID = new ResourceLocation(Main.MODID, "accept_contract_computer");

    public AcceptComputerContractTrigger() {
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public AcceptComputerContractTrigger.Instance createInstance(JsonObject json, EntityPredicate.Composite entityPredicate, DeserializationContext conditionsParser) {
        return new AcceptComputerContractTrigger.Instance(entityPredicate);
    }

    public void trigger(ServerPlayer player) {
        trigger(player, (instance) -> true);
    }

    public static class Instance extends AbstractCriterionTriggerInstance {
        public Instance(EntityPredicate.Composite player) {
            super(AcceptComputerContractTrigger.ID, player);
        }
    }
}