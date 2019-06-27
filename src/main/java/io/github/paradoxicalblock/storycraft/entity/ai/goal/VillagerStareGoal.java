package io.github.paradoxicalblock.storycraft.entity.ai.goal;

import io.github.paradoxicalblock.storycraft.entity.SocialVillager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.PathfindingUtil;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;
import java.util.List;

public class VillagerStareGoal extends Goal {
   private final SocialVillager owner;
   private LivingEntity target;
   private final double speed;
   private int timeLeft;

   public VillagerStareGoal(SocialVillager villagerEntity_1, double double_1) {
      this.owner = villagerEntity_1;
      this.speed = double_1;
      this.setControls(EnumSet.of(Control.MOVE));
   }

   public boolean canStart() {
      if (this.owner.getBreedingAge() >= 0) {
         return false;
      } else if (this.owner.getRand().nextInt(400) != 0) {
         return false;
      } else {
         List<SocialVillager> list_1 = this.owner.world.getEntities(SocialVillager.class, this.owner.getBoundingBox().expand(6.0D, 3.0D, 6.0D));
         double double_1 = Double.MAX_VALUE;

         for (SocialVillager villagerEntity_1 : list_1) {
            if (villagerEntity_1 != this.owner && !villagerEntity_1.isStaring() && villagerEntity_1.getBreedingAge() < 0) {
               double double_2 = villagerEntity_1.squaredDistanceTo(this.owner);
               if (double_2 <= double_1) {
                  double_1 = double_2;
                  this.target = villagerEntity_1;
               }
            }
         }

         if (this.target == null) {
            Vec3d vec3d_1 = PathfindingUtil.findTarget(this.owner, 16, 3);
            return vec3d_1 != null;
         }

         return true;
      }
   }

   public boolean shouldContinue() {
      return this.timeLeft > 0;
   }

   public void start() {
      if (this.target != null) {
         this.owner.setStaring(true);
      }

      this.timeLeft = 1000;
   }

   public void onRemove() {
      this.owner.setStaring(false);
      this.target = null;
   }

   public void tick() {
      --this.timeLeft;
      if (this.target != null) {
         if (this.owner.squaredDistanceTo(this.target) > 4.0D) {
            this.owner.getNavigation().startMovingTo(this.target, this.speed);
         }
      } else if (this.owner.getNavigation().isIdle()) {
         Vec3d vec3d_1 = PathfindingUtil.findTarget(this.owner, 16, 3);
         if (vec3d_1 == null) {
            return;
         }

         this.owner.getNavigation().startMovingTo(vec3d_1.x, vec3d_1.y, vec3d_1.z, this.speed);
      }

   }
}
