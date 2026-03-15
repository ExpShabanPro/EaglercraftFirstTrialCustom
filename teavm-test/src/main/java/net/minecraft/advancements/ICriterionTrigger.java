package net.minecraft.advancements;

import com.google.gson.JsonObject;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.util.ResourceLocation;

/**
 * Defines a trigger that can be used to grant advancements.
 * Triggers handle listeners for specific players and manage the deserialization 
 * of their respective ICriterionInstance configurations.
 *
 * @param <T> The type of ICriterionInstance this trigger handles.
 */
public interface ICriterionTrigger<T extends ICriterionInstance> {
   /**
    * Returns the unique identifier for this trigger (e.g., "minecraft:placed_block").
    */
   ResourceLocation getId();

   /**
    * Adds a listener to this trigger for a specific player's advancements.
    */
   void addListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<T> listener);

   /**
    * Removes a specific listener for a player.
    */
   void removeListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<T> listener);

   /**
    * Removes all listeners for a specific player from this trigger. 
    * Typically called when a player disconnects or advancements are reloaded.
    */
   void removeAllListeners(PlayerAdvancements playerAdvancementsIn);

   /**
    * Creates a criterion instance from JSON data.
    * * @param object The JSON object containing trigger-specific configuration.
    * @param conditions The parser used to handle loot predicates within the trigger.
    * @return A new instance of T populated with the provided data.
    */
   T deserialize(JsonObject object, ConditionArrayParser conditions);

   /**
    * A wrapper class that links a specific criterion configuration to an advancement 
    * and the name of the criterion within that advancement.
    */
   public static class Listener<T extends ICriterionInstance> {
      private final T criterionInstance;
      private final Advancement advancement;
      private final String criterionName;

      public Listener(T criterionInstanceIn, Advancement advancementIn, String criterionNameIn) {
         this.criterionInstance = criterionInstanceIn;
         this.advancement = advancementIn;
         this.criterionName = criterionNameIn;
      }

      public T getCriterionInstance() {
         return this.criterionInstance;
      }

      /**
       * Notifies the player advancement manager that this specific criterion has been met.
       */
      public void grantCriterion(PlayerAdvancements playerAdvancementsIn) {
         playerAdvancementsIn.grantCriterion(this.advancement, this.criterionName);
      }

      @Override
      public boolean equals(Object p_equals_1_) {
         if (this == p_equals_1_) {
            return true;
         } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
            ICriterionTrigger.Listener<?> listener = (ICriterionTrigger.Listener)p_equals_1_;
            if (!this.criterionInstance.equals(listener.criterionInstance)) {
               return false;
            } else {
               return !this.advancement.equals(listener.advancement) ? false : this.criterionName.equals(listener.criterionName);
            }
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         int i = this.criterionInstance.hashCode();
         i = 31 * i + this.advancement.hashCode();
         i = 31 * i + this.criterionName.hashCode();
         return i;
      }
   }
}
