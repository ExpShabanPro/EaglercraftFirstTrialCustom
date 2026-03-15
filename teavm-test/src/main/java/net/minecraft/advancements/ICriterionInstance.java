package net.minecraft.advancements;

import com.google.gson.JsonObject;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.ResourceLocation;

/**
 * Represents a specific instance of a criterion trigger with defined parameters.
 * Each implementation defines the logic and data required for a specific advancement check.
 */
public interface ICriterionInstance {
   /**
    * Returns the unique identifier for the trigger type this instance belongs to.
    */
   ResourceLocation getId();

   /**
    * Serializes the parameters of this criterion instance into a JsonObject.
    * * @param conditions The serializer used to handle potential loot table conditions/predicates
    * @return A JsonObject containing the serialized state of this instance
    */
   JsonObject serialize(ConditionArraySerializer conditions);
}
