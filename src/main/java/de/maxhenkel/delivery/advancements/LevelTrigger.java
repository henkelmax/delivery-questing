package de.maxhenkel.delivery.advancements;

import com.google.gson.JsonObject;
import de.maxhenkel.delivery.Main;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class LevelTrigger extends AbstractCriterionTrigger<LevelTrigger.Instance> {

    private static final ResourceLocation ID = new ResourceLocation(Main.MODID, "reach_level");

    public LevelTrigger() {
    }

    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public LevelTrigger.Instance createInstance(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser) {
        return new LevelTrigger.Instance(entityPredicate, deserializeLevel(json));
    }

    @Nullable
    private static Integer deserializeLevel(JsonObject jsonObject) {
        if (jsonObject.has("level")) {
            return JSONUtils.convertToInt(jsonObject, "level");
        } else {
            return null;
        }
    }

    public void trigger(ServerPlayerEntity player, int level) {
        ModTriggers.COMPUTER_AGE_TRIGGER.trigger(player, level);
        trigger(player, (instance) -> instance.test(level));
    }

    public static class Instance extends CriterionInstance {
        private final Integer level;

        public Instance(EntityPredicate.AndPredicate player, @Nullable Integer level) {
            super(LevelTrigger.ID, player);
            this.level = level;
        }

        @Override
        public JsonObject serializeToJson(ConditionArraySerializer conditions) {
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