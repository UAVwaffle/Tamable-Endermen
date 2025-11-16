package com.uavwaffle.tamableendermen.entity.custom.goal;

import com.uavwaffle.tamableendermen.entity.custom.TamableEnderManInterface;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class WaterAvoidingRandomStrollWithinRadiusGoal extends WaterAvoidingRandomStrollGoal {
    EnderMan tameableEndermanEntity;
    private final TamableEnderManInterface tameableEnderMan;

    public WaterAvoidingRandomStrollWithinRadiusGoal(TamableEnderManInterface tameableEnderMan, EnderMan pMob, double pSpeedModifier, float pProbability) {
        super(pMob, pSpeedModifier, pProbability);
        tameableEndermanEntity = pMob;
        this.tameableEnderMan = tameableEnderMan;
    }

    @Override
    public boolean canUse() {
        if (!tameableEnderMan.isTamed()) {
            return false;
        }
        if (!tameableEnderMan.getFollowState().name().equals("WANDER")) {
            return false;
        }
        if (this.mob.isVehicle()) {
            return false;
        } else {
            if (!this.forceTrigger) {

                if (this.mob.getRandom().nextInt(reducedTickDelay(this.interval)) != 0) {
                    return false;
                }
            }

            Vec3 vec3 = this.getPosition();
            if (vec3 == null) {
                return false;
            } else {
                this.wantedX = vec3.x;
                this.wantedY = vec3.y;
                this.wantedZ = vec3.z;
                this.forceTrigger = false;
                return true;
            }
        }
    }

    @Override
    @Nullable
    protected Vec3 getPosition() {
        if (this.mob.isInWaterOrBubble()) {
            Vec3 vec3 = LandRandomPos.getPosTowards(this.mob, 16, 7, tameableEnderMan.getLastInteractPos());
            return vec3 == null ? super.getPosition() : vec3;
        } else {
            return this.mob.getRandom().nextFloat() >= this.probability ? LandRandomPos.getPosTowards(this.mob, 16, 7, tameableEnderMan.getLastInteractPos()) : super.getPosition();
        }
    }
}
