package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.ResourceLocation;

/**
 * Base class for all advancement criterion instances.
 * This class handles the common 'player' condition present in almost all triggers.
 */
public abstract class CriterionInstance implements ICriterionInstance {
   private final ResourceLocation criterion;
   private final EntityPredicate.AndPredicate playerCondition;

   public CriterionInstance(ResourceLocation criterion, EntityPredicate.AndPredicate playerCondition) {
      this.criterion = criterion;
      this.playerCondition = playerCondition;
   }

   @Override
   public ResourceLocation getId() {
      return this.criterion;
   }

   protected EntityPredicate.AndPredicate getPlayerCondition() {
      return this.playerCondition;
   }

   /**
    * Serializes the condition to JSON for saving/loading advancements.
    */
   public JsonObject serialize(ConditionArraySerializer conditions) {
      JsonObject jsonobject = new JsonObject();
      jsonobject.add("player", this.playerCondition.serializeConditions(conditions));
      return jsonobject;
   }

   @Override
   public String toString() {
      return "CriterionInstance{criterion=" + this.criterion + '}';
   }
}
