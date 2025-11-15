package com.uavwaffle.tameableendermen.entity.custom.goal;

import com.uavwaffle.tameableendermen.entity.custom.TameableEndermanEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class WaterAvoidingRandomStrollWithinRadiusGoal extends WaterAvoidingRandomStrollGoal {
    TameableEndermanEntity tameableEndermanEntity;

    public WaterAvoidingRandomStrollWithinRadiusGoal(TameableEndermanEntity pMob, double pSpeedModifier, float pProbability) {
        super(pMob, pSpeedModifier, pProbability);
        tameableEndermanEntity = pMob;
    }

    @Override
    public boolean canUse() {
        if (!tameableEndermanEntity.isTamed()) {
            return false;
        }
        return super.canUse();
    }

    @Override
    @Nullable
    protected Vec3 getPosition() {
        if (this.mob.isInWaterOrBubble()) {
            Vec3 vec3 = LandRandomPos.getPosTowards(this.mob, 16, 7, tameableEndermanEntity.getLastInteractPos());
            return vec3 == null ? super.getPosition() : vec3;
        } else {
            return this.mob.getRandom().nextFloat() >= this.probability ? LandRandomPos.getPosTowards(this.mob, 16, 7, tameableEndermanEntity.getLastInteractPos()) : super.getPosition();
        }
    }
}
