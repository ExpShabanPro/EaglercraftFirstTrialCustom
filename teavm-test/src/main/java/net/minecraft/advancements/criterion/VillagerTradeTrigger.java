package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.loot.LootContext;
import net.minecraft.util.ResourceLocation;

/**
 * Triggered when a player completes a trade with a villager or wandering trader.
 * Modified for TeaVM compatibility in browser-based environments.
 */
public class VillagerTradeTrigger extends AbstractCriterionTrigger<VillagerTradeTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("villager_trade");

   @Override
   public ResourceLocation getId() {
      return ID;
   }

   @Override
   public VillagerTradeTrigger.Instance deserializeTrigger(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser) {
      EntityPredicate.AndPredicate villagerPredicate = EntityPredicate.AndPredicate.deserializeJSONObject(json, "villager", conditionsParser);
      ItemPredicate itemPredicate = ItemPredicate.deserialize(json.get("item"));
      return new VillagerTradeTrigger.Instance(entityPredicate, villagerPredicate, itemPredicate);
   }

   /**
    * Tests the trigger against all registered listeners for a specific trade event.
    */
   public void test(ServerPlayerEntity player, AbstractVillagerEntity villager, ItemStack stack) {
      LootContext lootcontext = EntityPredicate.getLootContext(player, villager);
      this.triggerListeners(player, (instance) -> {
         return instance.test(lootcontext, stack);
      });
   }

   public static class Instance extends CriterionInstance {
      private final EntityPredicate.AndPredicate villager;
      private final ItemPredicate item;

      public Instance(EntityPredicate.AndPredicate player, EntityPredicate.AndPredicate villager, ItemPredicate stack) {
         super(VillagerTradeTrigger.ID, player);
         this.villager = villager;
         this.item = stack;
      }

      /**
       * Creates a trigger instance that matches any villager trade.
       */
      public static VillagerTradeTrigger.Instance any() {
         return new VillagerTradeTrigger.Instance(EntityPredicate.AndPredicate.ANY_AND, EntityPredicate.AndPredicate.ANY_AND, ItemPredicate.ANY);
      }

      /**
       * Evaluates if the trade matches the criteria.
       * * @param context The loot context of the villager involved.
       * @param stack   The item stack received from the trade.
       * @return true if both the villager and item predicates match.
       */
      public boolean test(LootContext context, ItemStack stack) {
         if (!this.villager.testContext(context)) {
            return false;
         } else {
            return this.item.test(stack);
         }
      }

      @Override
      public JsonObject serialize(ConditionArraySerializer conditions) {
         JsonObject jsonobject = super.serialize(conditions);
         jsonobject.add("item", this.item.serialize());
         jsonobject.add("villager", this.villager.serializeConditions(conditions));
         return jsonobject;
      }
   }
}
