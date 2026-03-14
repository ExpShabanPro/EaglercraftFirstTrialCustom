package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
// Swapped ServerPlayerEntity for PlayerEntity for web compatibility
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.ResourceLocation;

public class EffectsChangedTrigger extends AbstractCriterionTrigger<EffectsChangedTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("effects_changed");

   public ResourceLocation getId() {
      return ID;
   }

   public EffectsChangedTrigger.Instance deserializeTrigger(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser) {
      MobEffectsPredicate mobeffectspredicate = MobEffectsPredicate.deserialize(json.get("effects"));
      return new EffectsChangedTrigger.Instance(entityPredicate, mobeffectspredicate);
   }

   /**
    * Updated for Web: Uses PlayerEntity to match our updated AbstractCriterionTrigger.
    */
   public void trigger(PlayerEntity player) {
      this.triggerListeners(player, (p_226524_1_) -> {
         return p_226524_1_.test(player);
      });
   }

   public static class Instance extends CriterionInstance {
      private final MobEffectsPredicate effects;

      public Instance(EntityPredicate.AndPredicate player, MobEffectsPredicate effects) {
         super(EffectsChangedTrigger.ID, player);
         this.effects = effects;
      }

      public static EffectsChangedTrigger.Instance forEffect(MobEffectsPredicate effects) {
         return new EffectsChangedTrigger.Instance(EntityPredicate.AndPredicate.ANY_AND, effects);
      }

      /**
       * Updated for Web: Uses PlayerEntity.
       */
      public boolean test(PlayerEntity player) {
         return this.effects.test(player);
      }

      public JsonObject serialize(ConditionArraySerializer conditions) {
         JsonObject jsonobject = super.serialize(conditions);
         jsonobject.add("effects", this.effects.serialize());
         return jsonobject;
      }
   }
}
