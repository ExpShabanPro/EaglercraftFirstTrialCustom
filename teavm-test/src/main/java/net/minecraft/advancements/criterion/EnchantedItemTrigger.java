package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
// Swapped ServerPlayerEntity for PlayerEntity for web compatibility
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.ResourceLocation;

public class EnchantedItemTrigger extends AbstractCriterionTrigger<EnchantedItemTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("enchanted_item");

   public ResourceLocation getId() {
      return ID;
   }

   public EnchantedItemTrigger.Instance deserializeTrigger(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser) {
      ItemPredicate itempredicate = ItemPredicate.deserialize(json.get("item"));
      MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.fromJson(json.get("levels"));
      return new EnchantedItemTrigger.Instance(entityPredicate, itempredicate, minmaxbounds$intbound);
   }

   /**
    * Updated for Web: Uses PlayerEntity to match our updated AbstractCriterionTrigger.
    */
   public void trigger(PlayerEntity player, ItemStack item, int levelsSpent) {
      this.triggerListeners(player, (p_226528_2_) -> {
         return p_226528_2_.test(item, levelsSpent);
      });
   }

   public static class Instance extends CriterionInstance {
      private final ItemPredicate item;
      private final MinMaxBounds.IntBound levels;

      public Instance(EntityPredicate.AndPredicate player, ItemPredicate item, MinMaxBounds.IntBound level) {
         super(EnchantedItemTrigger.ID, player);
         this.item = item;
         this.levels = level;
      }

      public static EnchantedItemTrigger.Instance any() {
         return new EnchantedItemTrigger.Instance(EntityPredicate.AndPredicate.ANY_AND, ItemPredicate.ANY, MinMaxBounds.IntBound.UNBOUNDED);
      }

      /**
       * Updated for Web: Logic remains the same, but called via PlayerEntity context.
       */
      public boolean test(ItemStack item, int levelsIn) {
         if (!this.item.test(item)) {
            return false;
         } else {
            return this.levels.test(levelsIn);
         }
      }

      public JsonObject serialize(ConditionArraySerializer conditions) {
         JsonObject jsonobject = super.serialize(conditions);
         jsonobject.add("item", this.item.serialize());
         jsonobject.add("levels", this.levels.serialize());
         return jsonobject;
      }
   }
}
