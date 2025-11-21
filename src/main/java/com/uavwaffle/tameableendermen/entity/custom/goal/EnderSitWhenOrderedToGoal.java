package com.uavwaffle.tameableendermen.entity.custom.goal;

import com.uavwaffle.tameableendermen.entity.custom.TameableEnderManInterface;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.EnderMan;

import java.util.EnumSet;

public class EnderSitWhenOrderedToGoal extends Goal implements TameableEnderManInterface {
    private final EnderMan mob;
    private final TameableEnderManInterface tameableEnderMan;

    public EnderSitWhenOrderedToGoal(TameableEnderManInterface tameableEnderMan, EnderMan pMob) {
        this.mob = pMob;
        this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
        this.tameableEnderMan = tameableEnderMan;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean canContinueToUse() {
        return this.tameableEnderMan.getFollowState().name().equals("SIT");
    }

    /**
     * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
     * method as well.
     */
    public boolean canUse() {
        if (!this.tameableEnderMan.isTamed()) {
            return false;
        } else if (!tameableEnderMan.getFollowState().name().equals("SIT")) {
            return false;
        } else if (this.mob.isInWaterOrBubble()) {
            return false;
        } else if (!this.mob.isOnGround()) {
            return false;
        } else {
            LivingEntity livingentity = this.tameableEnderMan.getOwner();
            if (livingentity == null) {
                return true;
            } else {
                return (!(this.mob.distanceToSqr(livingentity) < 144.0D) || livingentity.getLastHurtByMob() == null) && this.tameableEnderMan.tameableendermen$isOrderedToSit();
            }
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void start() {
        this.mob.getNavigation().stop();
//        this.tameableEnderMan.setInSittingPose(true);
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void stop() {
        this.tameableEnderMan.setInSittingPose(false);
    }
}