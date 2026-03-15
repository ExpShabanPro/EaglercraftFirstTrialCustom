package net.minecraft.advancements;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.command.FunctionObject;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;

public class AdvancementRewards {
   public static final AdvancementRewards EMPTY = new AdvancementRewards(0, new ResourceLocation[0], new ResourceLocation[0], FunctionObject.CacheableFunction.EMPTY);
   private final int experience;
   private final ResourceLocation[] loot;
   private final ResourceLocation[] recipes;
   private final FunctionObject.CacheableFunction function;

   public AdvancementRewards(int experience, ResourceLocation[] loot, ResourceLocation[] recipes, FunctionObject.CacheableFunction function) {
      this.experience = experience;
      this.loot = loot;
      this.recipes = recipes;
      this.function = function;
   }

   public void apply(ServerPlayerEntity player) {
      player.giveExperiencePoints(this.experience);
      LootContext lootcontext = (new LootContext.Builder(player.getServerWorld()))
            .withParameter(LootParameters.THIS_ENTITY, player)
            .withParameter(LootParameters.ORIGIN, player.getPositionVec())
            .withRandom(player.getRNG())
            .build(LootParameterSets.ADVANCEMENT);
      
      boolean flag = false;

      for (ResourceLocation resourcelocation : this.loot) {
         for (ItemStack itemstack : player.server.getLootTableManager().getLootTableFromLocation(resourcelocation).generate(lootcontext)) {
            if (player.addItemStackToInventory(itemstack)) {
               player.world.playSound((PlayerEntity) null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
               flag = true;
            } else {
               ItemEntity itementity = player.dropItem(itemstack, false);
               if (itementity != null) {
                  itementity.setNoPickupDelay();
                  itementity.setOwnerId(player.getUniqueID());
               }
            }
         }
      }

      if (flag) {
         player.container.detectAndSendChanges();
      }

      if (this.recipes.length > 0) {
         player.unlockRecipes(this.recipes);
      }

      MinecraftServer minecraftserver = player.server;
      this.function.getFunction(minecraftserver.getFunctionManager()).ifPresent((resolvedFunction) -> {
         minecraftserver.getFunctionManager().execute(resolvedFunction, player.getCommandSource().withFeedbackDisabled().withPermissionLevel(2));
      });
   }

   @Override
   public String toString() {
      return "AdvancementRewards{experience=" + this.experience + ", loot=" + Arrays.toString(this.loot) + ", recipes=" + Arrays.toString(this.recipes) + ", function=" + this.function + '}';
   }

   public JsonElement serialize() {
      if (this == EMPTY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject jsonobject = new JsonObject();
         if (this.experience != 0) {
            jsonobject.addProperty("experience", this.experience);
         }

         if (this.loot.length > 0) {
            JsonArray jsonarray = new JsonArray();
            for (ResourceLocation resourcelocation : this.loot) {
               jsonarray.add(resourcelocation.toString());
            }
            jsonobject.add("loot", jsonarray);
         }

         if (this.recipes.length > 0) {
            JsonArray jsonarray1 = new JsonArray();
            for (ResourceLocation resourcelocation1 : this.recipes) {
               jsonarray1.add(resourcelocation1.toString());
            }
            jsonobject.add("recipes", jsonarray1);
         }

         if (this.function.getId() != null) {
            jsonobject.addProperty("function", this.function.getId().toString());
         }

         return jsonobject;
      }
   }

   public static AdvancementRewards deserializeRewards(JsonObject json) throws JsonParseException {
      int exp = JSONUtils.getInt(json, "experience", 0);
      
      JsonArray lootArray = JSONUtils.getJsonArray(json, "loot", new JsonArray());
      ResourceLocation[] lootLocations = new ResourceLocation[lootArray.size()];
      for (int i = 0; i < lootLocations.length; ++i) {
         lootLocations[i] = new ResourceLocation(JSONUtils.getString(lootArray.get(i), "loot[" + i + "]"));
      }

      JsonArray recipeArray = JSONUtils.getJsonArray(json, "recipes", new JsonArray());
      ResourceLocation[] recipeLocations = new ResourceLocation[recipeArray.size()];
      for (int i = 0; i < recipeLocations.length; ++i) {
         recipeLocations[i] = new ResourceLocation(JSONUtils.getString(recipeArray.get(i), "recipes[" + i + "]"));
      }

      FunctionObject.CacheableFunction cacheableFunction;
      if (json.has("function")) {
         cacheableFunction = new FunctionObject.CacheableFunction(new ResourceLocation(JSONUtils.getString(json, "function")));
      } else {
         cacheableFunction = FunctionObject.CacheableFunction.EMPTY;
      }

      return new AdvancementRewards(exp, lootLocations, recipeLocations, cacheableFunction);
   }

   public static class Builder {
      private int experience;
      private final List<ResourceLocation> loot = Lists.newArrayList();
      private final List<ResourceLocation> recipes = Lists.newArrayList();
      @Nullable
      private ResourceLocation function;

      public static AdvancementRewards.Builder experience(int experienceIn) {
         return (new AdvancementRewards.Builder()).addExperience(experienceIn);
      }

      public AdvancementRewards.Builder addExperience(int experienceIn) {
         this.experience += experienceIn;
         return this;
      }

      public static AdvancementRewards.Builder recipe(ResourceLocation recipeIn) {
         return (new AdvancementRewards.Builder()).addRecipe(recipeIn);
      }

      public AdvancementRewards.Builder addRecipe(ResourceLocation recipeIn) {
         this.recipes.add(recipeIn);
         return this;
      }

      public AdvancementRewards build() {
         return new AdvancementRewards(
               this.experience, 
               this.loot.toArray(new ResourceLocation[0]), 
               this.recipes.toArray(new ResourceLocation[0]), 
               this.function == null ? FunctionObject.CacheableFunction.EMPTY : new FunctionObject.CacheableFunction(this.function)
         );
      }
   }
}
