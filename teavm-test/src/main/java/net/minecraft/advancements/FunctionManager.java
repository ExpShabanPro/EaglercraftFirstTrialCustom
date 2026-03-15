package net.minecraft.advancements;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.minecraft.command.CommandSource;
import net.minecraft.command.FunctionObject;
import net.minecraft.resources.FunctionReloader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.GameRules;

/**
 * Manages the execution of data pack functions.
 * Handles the tick and load tags, and manages the execution queue for nested function calls.
 */
public class FunctionManager {
   private static final ResourceLocation TICK_TAG_ID = new ResourceLocation("tick");
   private static final ResourceLocation LOAD_TAG_ID = new ResourceLocation("load");
   private final MinecraftServer server;
   private boolean isExecuting;
   private final ArrayDeque<FunctionManager.QueuedCommand> commandQueue = new ArrayDeque<>();
   private final List<FunctionManager.QueuedCommand> commandChain = Lists.newArrayList();
   private final List<FunctionObject> tickFunctions = Lists.newArrayList();
   private boolean loadFunctionsRun;
   private FunctionReloader reloader;

   public FunctionManager(MinecraftServer server, FunctionReloader reloader) {
      this.server = server;
      this.reloader = reloader;
      this.clearAndResetTickFunctions(reloader);
   }

   public int getMaxCommandChainLength() {
      return this.server.getGameRules().getInt(GameRules.MAX_COMMAND_CHAIN_LENGTH);
   }

   public CommandDispatcher<CommandSource> getCommandDispatcher() {
      return this.server.getCommandManager().getDispatcher();
   }

   public void tick() {
      // Execute functions tagged with #minecraft:tick every tick
      this.executeAndProfile(this.tickFunctions, TICK_TAG_ID);
      
      // Execute functions tagged with #minecraft:load once after reload
      if (this.loadFunctionsRun) {
         this.loadFunctionsRun = false;
         Collection<FunctionObject> collection = this.reloader.func_240942_b_().getTagByID(LOAD_TAG_ID).getAllElements();
         this.executeAndProfile(collection, LOAD_TAG_ID);
      }
   }

   private void executeAndProfile(Collection<FunctionObject> functionObjects, ResourceLocation identifier) {
      this.server.getProfiler().startSection(identifier::toString);
      for(FunctionObject functionobject : functionObjects) {
         this.execute(functionobject, this.getCommandSource());
      }
      this.server.getProfiler().endSection();
   }

   /**
    * Executes a function object. If a function is already running, 
    * it queues the commands to maintain the correct execution order.
    */
   public int execute(FunctionObject functionObject, CommandSource source) {
      int maxChain = this.getMaxCommandChainLength();
      if (this.isExecuting) {
         if (this.commandQueue.size() + this.commandChain.size() < maxChain) {
            this.commandChain.add(new FunctionManager.QueuedCommand(this, source, new FunctionObject.FunctionEntry(functionObject)));
         }
         return 0;
      } else {
         try {
            this.isExecuting = true;
            int totalExecuted = 0;
            FunctionObject.IEntry[] entries = functionObject.getEntries();

            // Push entries in reverse order so they are processed in correct sequence from the Deque
            for(int k = entries.length - 1; k >= 0; --k) {
               this.commandQueue.push(new FunctionManager.QueuedCommand(this, source, entries[k]));
            }

            while(!this.commandQueue.isEmpty()) {
               try {
                  FunctionManager.QueuedCommand queuedCommand = this.commandQueue.removeFirst();
                  this.server.getProfiler().startSection(queuedCommand::toString);
                  queuedCommand.execute(this.commandQueue, maxChain);
                  
                  if (!this.commandChain.isEmpty()) {
                     Lists.reverse(this.commandChain).forEach(this.commandQueue::addFirst);
                     this.commandChain.clear();
                  }
               } finally {
                  this.server.getProfiler().endSection();
               }

               ++totalExecuted;
               if (totalExecuted >= maxChain) {
                  return totalExecuted;
               }
            }

            return totalExecuted;
         } finally {
            this.commandQueue.clear();
            this.commandChain.clear();
            this.isExecuting = false;
         }
      }
   }

   public void setFunctionReloader(FunctionReloader reloader) {
      this.reloader = reloader;
      this.clearAndResetTickFunctions(reloader);
   }

   private void clearAndResetTickFunctions(FunctionReloader reloader) {
      this.tickFunctions.clear();
      this.tickFunctions.addAll(reloader.func_240942_b_().getTagByID(TICK_TAG_ID).getAllElements());
      this.loadFunctionsRun = true;
   }

   public CommandSource getCommandSource() {
      return this.server.getCommandSource().withPermissionLevel(2).withFeedbackDisabled();
   }

   public Optional<FunctionObject> get(ResourceLocation functionIdentifier) {
      return this.reloader.func_240940_a_(functionIdentifier);
   }

   public ITag<FunctionObject> getFunctionTag(ResourceLocation functionTagIdentifier) {
      return this.reloader.func_240943_b_(functionTagIdentifier);
   }

   public Iterable<ResourceLocation> getFunctionIdentifiers() {
      return this.reloader.func_240931_a_().keySet();
   }

   public Iterable<ResourceLocation> getFunctionTagIdentifiers() {
      return this.reloader.func_240942_b_().getRegisteredTags();
   }

   /**
    * Internal wrapper for a command or sub-function waiting to be executed.
    */
   public static class QueuedCommand {
      private final FunctionManager functionManager;
      private final CommandSource sender;
      private final FunctionObject.IEntry entry;

      public QueuedCommand(FunctionManager functionManager, CommandSource commandSource, FunctionObject.IEntry objectEntry) {
         this.functionManager = functionManager;
         this.sender = commandSource;
         this.entry = objectEntry;
      }

      public void execute(ArrayDeque<FunctionManager.QueuedCommand> commandQueue, int maxCommandChainLength) {
         try {
            this.entry.execute(this.functionManager, this.sender, commandQueue, maxCommandChainLength);
         } catch (Throwable ignored) {
            // Silently fail command execution within functions to prevent server crashes
         }
      }

      @Override
      public String toString() {
         return this.entry.toString();
      }
   }
}
