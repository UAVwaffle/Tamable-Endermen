package com.uavwaffle.tameableendermen.entity.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public interface TameableEnderManInterface {
    public static final Ingredient FOOD_ITEMS = null;
    public static final EntityDataAccessor<Optional<UUID>> DATA_ID_OWNER_UUID = null;
    public static final int FLAG_TAME = 2;

    public Vec3 lastInteractPos = null;
    public FollowState followState = null;


    public default boolean getFlag(int pFlagId) {
        return false;
    }

    public default void setFlag(int pFlagId, boolean pValue) {

    }

    public default FollowState getFollowState() {
        return null;
    }

    public default void setFollowState(FollowState followState) {
    }

    public default void setFollowState(String followState) {
    }

    public default boolean isTamed() {
        return false;
    }

    public default boolean isOwnedBy(LivingEntity pEntity) {
        return pEntity == this.getOwner();
    }

    public default Team getTeam() {
        return null;
    }

    public default boolean isAlliedTo(Entity pEntity) {
        return false;
    }

    @Nullable
    public default LivingEntity getOwner() {
        return null;
    }

    @Nullable
    public default UUID getOwnerUUID() {
        return null;
    }

    public default void setOwnerUUID(@Nullable UUID pUuid) {
    }

    public default void setTamed(boolean pTamed) {
        this.setFlag(2, pTamed);
    }

    public default boolean tame(Player pPlayer) {
        return false;
    }


    public default Vec3 getLastInteractPos() {
        return null;
    }

    public default InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        return null;
    }

    public default boolean isFood(ItemStack pStack) {
        return false;
    }

    public default InteractionResult fedFood(Player pPlayer, ItemStack pStack) {
        return null;
    }

    public default boolean handleEating(Player pPlayer, ItemStack pStack) {
        return false;
    }

    default void eating() {
    }


    public default boolean isInSittingPose() {
        return false;
    }

    public default void setInSittingPose(boolean pSitting) {
    }

    public default boolean tameableendermen$isOrderedToSit() {
        return false;
    }

    public default void tameableendermen$setOrderedToSit(boolean pOrderedToSit) {
    }

    @Nullable
    default SoundEvent getEatingSound() {
        return null;
    }


    public default void checkDespawn() {
    }

    default void defineSynchedData(CallbackInfo ci) {
    }


    public default void addAdditionalSaveData(CompoundTag pCompound, CallbackInfo ci) {
    }

    public default void readAdditionalSaveData(CompoundTag pCompound, CallbackInfo ci) {
    }

    default void spawnTamingParticles(boolean pTamed) {
    }

    default void handleEntityEvent(byte pId) {
    }
}

