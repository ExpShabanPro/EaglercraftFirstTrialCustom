package net.minecraft.advancements;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Defines the visual border/frame used for an advancement in the UI, 
 * as well as the color and prefix used in chat announcements.
 */
public enum FrameType {
   TASK("task", 0, TextFormatting.GREEN),
   CHALLENGE("challenge", 26, TextFormatting.DARK_PURPLE),
   GOAL("goal", 52, TextFormatting.GREEN);

   private final String name;
   /** The texture offset/index for the icon frame in the GUI. */
   private final int icon;
   private final TextFormatting format;
   private final ITextComponent translatedToast;

   private FrameType(String nameIn, int iconIn, TextFormatting formatIn) {
      this.name = nameIn;
      this.icon = iconIn;
      this.format = formatIn;
      this.translatedToast = new TranslationTextComponent("advancements.toast." + nameIn);
   }

   public String getName() {
      return this.name;
   }

   /**
    * Returns the texture offset for this frame type.
    */
   @OnlyIn(Dist.CLIENT)
   public int getIcon() {
      return this.icon;
   }

   /**
    * Retrieves a FrameType by its string ID (e.g., "challenge").
    */
   public static FrameType byName(String nameIn) {
      for(FrameType frametype : values()) {
         if (frametype.name.equals(nameIn)) {
            return frametype;
         }
      }

      throw new IllegalArgumentException("Unknown frame type '" + nameIn + "'");
   }

   /**
    * Gets the text formatting used when this advancement is mentioned in chat.
    */
   public TextFormatting getFormat() {
      return this.format;
   }

   /**
    * Returns the localized text for the toast notification header.
    */
   @OnlyIn(Dist.CLIENT)
   public ITextComponent getTranslatedToast() {
      return this.translatedToast;
   }
}
