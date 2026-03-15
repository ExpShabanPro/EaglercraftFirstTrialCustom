package net.minecraft.advancements;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

/**
 * Represents a single requirement for an advancement.
 * It links a specific ICriterionTrigger to its configured ICriterionInstance (conditions).
 */
public class Criterion {
   private final ICriterionInstance criterionInstance;

   public Criterion(ICriterionInstance criterionInstance) {
      this.criterionInstance = criterionInstance;
   }

   public Criterion() {
      this.criterionInstance = null;
   }

   /**
    * Serializes the criterion to a network packet. 
    * Note: In many versions, trigger conditions are not fully synced to the client.
    */
   public void serializeToNetwork(PacketBuffer buffer) {
      // Logic for network serialization (often empty or simple in specific versions)
   }

   /**
    * Deserializes a Criterion from a JsonObject.
    */
   public static Criterion deserializeCriterion(JsonObject json, ConditionArrayParser conditionParser) {
      ResourceLocation triggerId = new ResourceLocation(JSONUtils.getString(json, "trigger"));
      ICriterionTrigger<?> trigger = CriteriaTriggers.get(triggerId);
      
      if (trigger == null) {
         throw new JsonSyntaxException("Invalid criterion trigger: " + triggerId);
      } else {
         // Extract conditions block or use an empty object if not present
         JsonObject conditionsJson = JSONUtils.getJsonObject(json, "conditions", new JsonObject());
         ICriterionInstance instance = trigger.deserialize(conditionsJson, conditionParser);
         return new Criterion(instance);
      }
   }

   /**
    * Reads a Criterion from a network buffer.
    */
   public static Criterion criterionFromNetwork(PacketBuffer buffer) {
      return new Criterion();
   }

   /**
    * Deserializes a map of criteria from a JSON object.
    */
   public static Map<String, Criterion> deserializeAll(JsonObject json, ConditionArrayParser conditionParser) {
      Map<String, Criterion> map = Maps.newHashMap();

      for (Entry<String, JsonElement> entry : json.entrySet()) {
         map.put(entry.getKey(), deserializeCriterion(JSONUtils.getJsonObject(entry.getValue(), "criterion"), conditionParser));
      }

      return map;
   }

   /**
    * Reads a map of criteria from a network packet buffer.
    */
   public static Map<String, Criterion> criteriaFromNetwork(PacketBuffer bus) {
      Map<String, Criterion> map = Maps.newHashMap();
      int size = bus.readVarInt();

      for (int i = 0; i < size; ++i) {
         map.put(bus.readString(32767), criterionFromNetwork(bus));
      }

      return map;
   }

   /**
    * Writes a map of criteria to a network packet buffer.
    */
   public static void serializeToNetwork(Map<String, Criterion> criteria, PacketBuffer buf) {
      buf.writeVarInt(criteria.size());

      for (Entry<String, Criterion> entry : criteria.entrySet()) {
         buf.writeString(entry.getKey());
         entry.getValue().serializeToNetwork(buf);
      }
   }

   @Nullable
   public ICriterionInstance getCriterionInstance() {
      return this.criterionInstance;
   }

   /**
    * Serializes this criterion back into a JsonElement for saving to a data pack.
    */
   public JsonElement serialize() {
      JsonObject json = new JsonObject();
      if (this.criterionInstance == null) {
         throw new IllegalStateException("Cannot serialize an empty criterion");
      }
      
      json.addProperty("trigger", this.criterionInstance.getId().toString());
      JsonObject conditions = this.criterionInstance.serialize(ConditionArraySerializer.field_235679_a_);
      
      if (conditions.size() != 0) {
         json.add("conditions", conditions);
      }

      return json;
   }
}
