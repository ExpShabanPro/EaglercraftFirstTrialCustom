package net.minecraft.advancements;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.minecraft.network.PacketBuffer;

/**
 * Tracks the progress of a single criterion within an advancement.
 * Stores the date/time when the criterion was obtained.
 */
public class CriterionProgress {
   private static final SimpleDateFormat DATE_TIME_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
   private Date obtained;

   public boolean isObtained() {
      return this.obtained != null;
   }

   /**
    * Marks this criterion as completed at the current system time.
    */
   public void obtain() {
      this.obtained = new Date();
   }

   /**
    * Resets the progress, making the criterion unobtained.
    */
   public void reset() {
      this.obtained = null;
   }

   public Date getObtained() {
      return this.obtained;
   }

   @Override
   public String toString() {
      return "CriterionProgress{obtained=" + (this.obtained == null ? "false" : this.obtained) + '}';
   }

   /**
    * Writes the completion status and timestamp to a network buffer.
    */
   public void write(PacketBuffer buf) {
      buf.writeBoolean(this.obtained != null);
      if (this.obtained != null) {
         buf.writeTime(this.obtained);
      }
   }

   /**
    * Serializes the progress to JSON. If obtained, returns the formatted date string; 
    * otherwise, returns JsonNull.
    */
   public JsonElement serialize() {
      return (JsonElement)(this.obtained != null ? new JsonPrimitive(DATE_TIME_FORMATTER.format(this.obtained)) : JsonNull.INSTANCE);
   }

   /**
    * Reads criterion progress from a network buffer.
    */
   public static CriterionProgress read(PacketBuffer buf) {
      CriterionProgress criterionprogress = new CriterionProgress();
      if (buf.readBoolean()) {
         criterionprogress.obtained = buf.readTime();
      }
      return criterionprogress;
   }

   /**
    * Creates a CriterionProgress instance from a JSON date string.
    */
   public static CriterionProgress fromJson(String dateTime) {
      CriterionProgress criterionprogress = new CriterionProgress();

      try {
         criterionprogress.obtained = DATE_TIME_FORMATTER.parse(dateTime);
         return criterionprogress;
      } catch (ParseException parseexception) {
         throw new JsonSyntaxException("Invalid datetime: " + dateTime, parseexception);
      }
   }
}
