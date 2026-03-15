package net.minecraft.advancements;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;

public class AdvancementTreeNode {
   private final Advancement advancement;
   private final AdvancementTreeNode parent;
   private final AdvancementTreeNode sibling;
   private final int index;
   private final List<AdvancementTreeNode> children = Lists.newArrayList();
   private AdvancementTreeNode ancestor;
   private AdvancementTreeNode thread;
   private int x;
   private float y;
   private float mod;
   private float change;
   private float shift;

   public AdvancementTreeNode(Advancement advancementIn, @Nullable AdvancementTreeNode parentIn, @Nullable AdvancementTreeNode siblingIn, int indexIn, int xIn) {
      if (advancementIn.getDisplay() == null) {
         throw new IllegalArgumentException("Can't position an invisible advancement!");
      } else {
         this.advancement = advancementIn;
         this.parent = parentIn;
         this.sibling = siblingIn;
         this.index = indexIn;
         this.ancestor = this;
         this.x = xIn;
         this.y = -1.0F;
         AdvancementTreeNode previousChild = null;

         for (Advancement child : advancementIn.getChildren()) {
            previousChild = this.buildSubTree(child, previousChild);
         }
      }
   }

   @Nullable
   private AdvancementTreeNode buildSubTree(Advancement advancementIn, @Nullable AdvancementTreeNode previous) {
      if (advancementIn.getDisplay() != null) {
         previous = new AdvancementTreeNode(advancementIn, this, previous, this.children.size() + 1, this.x + 1);
         this.children.add(previous);
      } else {
         for (Advancement child : advancementIn.getChildren()) {
            previous = this.buildSubTree(child, previous);
         }
      }

      return previous;
   }

   private void firstWalk() {
      if (this.children.isEmpty()) {
         if (this.sibling != null) {
            this.y = this.sibling.y + 1.0F;
         } else {
            this.y = 0.0F;
         }
      } else {
         AdvancementTreeNode previousNode = null;

         for (AdvancementTreeNode childNode : this.children) {
            childNode.firstWalk();
            previousNode = childNode.apportion(previousNode == null ? childNode : previousNode);
         }

         this.executeShifts();
         float midY = (this.children.get(0).y + this.children.get(this.children.size() - 1).y) / 2.0F;
         if (this.sibling != null) {
            this.y = this.sibling.y + 1.0F;
            this.mod = this.y - midY;
         } else {
            this.y = midY;
         }
      }
   }

   private float secondWalk(float offsetY, int columnX, float subtreeTopY) {
      this.y += offsetY;
      this.x = columnX;
      if (this.y < subtreeTopY) {
         subtreeTopY = this.y;
      }

      for (AdvancementTreeNode child : this.children) {
         subtreeTopY = child.secondWalk(offsetY + this.mod, columnX + 1, subtreeTopY);
      }

      return subtreeTopY;
   }

   private void thirdWalk(float yIn) {
      this.y += yIn;

      for (AdvancementTreeNode child : this.children) {
         child.thirdWalk(yIn);
      }
   }

   private void executeShifts() {
      float currentShift = 0.0F;
      float currentChange = 0.0F;

      for (int i = this.children.size() - 1; i >= 0; --i) {
         AdvancementTreeNode child = this.children.get(i);
         child.y += currentShift;
         child.mod += currentShift;
         currentChange += child.change;
         currentShift += child.shift + currentChange;
      }
   }

   @Nullable
   private AdvancementTreeNode getFirstChild() {
      if (this.thread != null) {
         return this.thread;
      } else {
         return !this.children.isEmpty() ? this.children.get(0) : null;
      }
   }

   @Nullable
   private AdvancementTreeNode getLastChild() {
      if (this.thread != null) {
         return this.thread;
      } else {
         return !this.children.isEmpty() ? this.children.get(this.children.size() - 1) : null;
      }
   }

   private AdvancementTreeNode apportion(AdvancementTreeNode nodeIn) {
      if (this.sibling == null) {
         return nodeIn;
      } else {
         AdvancementTreeNode currentInner = this;
         AdvancementTreeNode currentOuter = this;
         AdvancementTreeNode neighborInner = this.sibling;
         AdvancementTreeNode neighborOuter = this.parent.children.get(0);
         
         float modInner = this.mod;
         float modOuter = this.mod;
         float modNeighborInner = neighborInner.mod;
         float modNeighborOuter = neighborOuter.mod;

         while (neighborInner.getLastChild() != null && currentInner.getFirstChild() != null) {
            neighborInner = neighborInner.getLastChild();
            currentInner = currentInner.getFirstChild();
            neighborOuter = neighborOuter.getFirstChild();
            currentOuter = currentOuter.getLastChild();
            
            currentOuter.ancestor = this;
            float diff = (neighborInner.y + modNeighborInner) - (currentInner.y + modInner) + 1.0F;
            if (diff > 0.0F) {
               neighborInner.getAncestor(this, nodeIn).moveSubtree(this, diff);
               modInner += diff;
               modOuter += diff;
            }

            modNeighborInner += neighborInner.mod;
            modInner += currentInner.mod;
            modNeighborOuter += neighborOuter.mod;
            modOuter += currentOuter.mod;
         }

         if (neighborInner.getLastChild() != null && currentOuter.getLastChild() == null) {
            currentOuter.thread = neighborInner.getLastChild();
            currentOuter.mod += modNeighborInner - modOuter;
         } else {
            if (currentInner.getFirstChild() != null && neighborOuter.getFirstChild() == null) {
               neighborOuter.thread = currentInner.getFirstChild();
               neighborOuter.mod += modInner - modNeighborOuter;
            }

            nodeIn = this;
         }

         return nodeIn;
      }
   }

   private void moveSubtree(AdvancementTreeNode nodeIn, float shiftValue) {
      float indexDiff = (float) (nodeIn.index - this.index);
      if (indexDiff != 0.0F) {
         nodeIn.change -= shiftValue / indexDiff;
         this.change += shiftValue / indexDiff;
      }

      nodeIn.shift += shiftValue;
      nodeIn.y += shiftValue;
      nodeIn.mod += shiftValue;
   }

   private AdvancementTreeNode getAncestor(AdvancementTreeNode self, AdvancementTreeNode other) {
      return this.ancestor != null && self.parent.children.contains(this.ancestor) ? this.ancestor : other;
   }

   private void updatePosition() {
      if (this.advancement.getDisplay() != null) {
         this.advancement.getDisplay().setPosition((float) this.x, this.y);
      }

      if (!this.children.isEmpty()) {
         for (AdvancementTreeNode child : this.children) {
            child.updatePosition();
         }
      }
   }

   public static void layout(Advancement root) {
      if (root.getDisplay() == null) {
         throw new IllegalArgumentException("Can't position children of an invisible root!");
      } else {
         AdvancementTreeNode rootNode = new AdvancementTreeNode(root, null, null, 1, 0);
         rootNode.firstWalk();
         float topY = rootNode.secondWalk(0.0F, 0, rootNode.y);
         if (topY < 0.0F) {
            rootNode.thirdWalk(-topY);
         }

         rootNode.updatePosition();
      }
   }
}
