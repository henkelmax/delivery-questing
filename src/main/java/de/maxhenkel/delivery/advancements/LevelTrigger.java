package de.maxhenkel.delivery.advancements;

import com.google.gson.JsonObject;
import de.maxhenkel.delivery.Main;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;

import javax.annotation.Nullable;

public class LevelTrigger extends SimpleCriterionTrigger<LevelTrigger.Instance> {

    private static final ResourceLocation ID = new ResourceLocation(Main.MODID, "reach_level");

    public LevelTrigger() {
    }

    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public LevelTrigger.Instance createInstance(JsonObject json, EntityPredicate.Composite entityPredicate, DeserializationContext conditionsParser) {
        return new LevelTrigger.Instance(entityPredicate, deserializeLevel(json));
    }

    @Nullable
    private static Integer deserializeLevel(JsonObject jsonObject) {
        if (jsonObject.has("level")) {
            return GsonHelper.convertToInt(jsonObject, "level");
        } else {
            return null;
        }
    }

    public void trigger(ServerPlayer player, int level) {
        ModTriggers.COMPUTER_AGE_TRIGGER.trigger(player, level);
        trigger(player, (instance) -> instance.test(level));
    }

    public static class Instance extends AbstractCriterionTriggerInstance {
        private final Integer level;

        public Instance(EntityPredicate.Composite player, @Nullable Integer level) {
            super(LevelTrigger.ID, player);
            this.level = level;
        }

        @Override
        public JsonObject serializeToJson(SerializationContext conditions) {
            JsonObject jsonobject = super.serializeToJson(conditions);
            if (this.level != null) {
                jsonobject.addProperty("level", level);
            }
            return jsonobject;
        }

        public boolean test(int level) {
            if (this.level == null) {
                return false;
            }
            return level >= this.level;
        }
    }
}