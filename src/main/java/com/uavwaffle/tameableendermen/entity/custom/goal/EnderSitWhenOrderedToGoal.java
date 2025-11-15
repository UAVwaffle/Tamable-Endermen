package com.uavwaffle.tameableendermen.entity.custom.goal;

import com.uavwaffle.tameableendermen.entity.custom.TameableEndermanEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class EnderSitWhenOrderedToGoal extends Goal {
    private final TameableEndermanEntity mob;

    public EnderSitWhenOrderedToGoal(TameableEndermanEntity pMob) {
        this.mob = pMob;
        this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean canContinueToUse() {
        return this.mob.getFollowState().name().equals("SIT");
    }

    /**
     * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
     * method as well.
     */
    public boolean canUse() {
        if (!this.mob.isTamed()) {
            return false;
        } else if (!mob.getFollowState().name().equals("SIT")) {
            return false;
        } else if (this.mob.isInWaterOrBubble()) {
            return false;
        } else if (!this.mob.isOnGround()) {
            return false;
        } else {
            LivingEntity livingentity = this.mob.getOwner();
            if (livingentity == null) {
                return true;
            } else {
                return (!(this.mob.distanceToSqr(livingentity) < 144.0D) || livingentity.getLastHurtByMob() == null) && this.mob.isOrderedToSit();
            }
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void start() {
        this.mob.getNavigation().stop();
        this.mob.setInSittingPose(true);
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void stop() {
        this.mob.setInSittingPose(false);
    }
}