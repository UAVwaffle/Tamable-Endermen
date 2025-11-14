package com.uavwaffle.tameableendermen.entity.custom;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.level.Level;

public class TameableEndermanEntity extends EnderMan {
    public TameableEndermanEntity(EntityType<? extends EnderMan> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
}
