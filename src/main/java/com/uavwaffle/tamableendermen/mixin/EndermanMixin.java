package com.uavwaffle.tamableendermen.mixin;

import com.uavwaffle.tamableendermen.entity.custom.FollowState;
import com.uavwaffle.tamableendermen.entity.custom.TamableEnderManInterface;
import com.uavwaffle.tamableendermen.entity.custom.goal.EnderFollowOwnerGoal;
import com.uavwaffle.tamableendermen.entity.custom.goal.EnderSitWhenOrderedToGoal;
import com.uavwaffle.tamableendermen.entity.custom.goal.WaterAvoidingRandomStrollWithinRadiusGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

@Mixin(EnderMan.class)
public abstract class EndermanMixin extends Monster implements NeutralMob, TamableEnderManInterface {

    private static final Ingredient FOOD_ITEMS = Ingredient.of(Items.CHORUS_FRUIT, Items.APPLE);
    private static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(EnderMan.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Optional<UUID>> DATA_ID_OWNER_UUID = SynchedEntityData.defineId(EnderMan.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final int FLAG_TAME = 2;


    private Vec3 lastInteractPos = new Vec3(1, 1, 1);
    private FollowState followState = FollowState.SIT;

    public EndermanMixin(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(at = @At("TAIL"), method = "<init>")
    public void EnderMan(EntityType pEntityType, Level pLevel, CallbackInfo ci) {
        this.setTamed(false);
    }

    @Inject(at = @At("HEAD"), method = "registerGoals")
    protected void registerGoals(CallbackInfo ci) {

        this.goalSelector.addGoal(2, new EnderSitWhenOrderedToGoal(this, (EnderMan) (Object) this));
        this.goalSelector.addGoal(6, new EnderFollowOwnerGoal(this, (EnderMan) (Object) this, 1.0D, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollWithinRadiusGoal(this, (EnderMan) (Object) this, 1.0D, 0.0F));

    }

    public boolean getFlag(int pFlagId) {
        return (this.entityData.get(DATA_FLAGS_ID) & pFlagId) != 0;
    }


    public void setFlag(int pFlagId, boolean pValue) {
        byte b0 = this.entityData.get(DATA_FLAGS_ID);
        if (pValue) {
            this.entityData.set(DATA_FLAGS_ID, (byte) (b0 | pFlagId));
        } else {
            this.entityData.set(DATA_FLAGS_ID, (byte) (b0 & ~pFlagId));
        }

    }

    public FollowState getFollowState() {
        return followState;
    }

    public void setFollowState(FollowState followState) {
        this.followState = followState;
    }

    public void setFollowState(String followState) {
        switch (followState) {
            case "SIT":
                this.followState = FollowState.SIT;
                break;
            case "FOLLOW":
                this.followState = FollowState.FOLLOW;
                break;
            case "WANDER":
                this.followState = FollowState.WANDER;
                break;
            default:
                this.followState = FollowState.WANDER;
        }
    }

    @Inject(at = @At("HEAD"), method = "teleport()Z")
    protected void teleport(CallbackInfoReturnable<Boolean> cir) {
        this.followState = FollowState.WANDER;
    }

    @Inject(at = @At("HEAD"), method = "isLookingAtMe", cancellable = true)
    void isLookingAtMe(Player pPlayer, CallbackInfoReturnable<Boolean> cir) {
        if (isTamed()) {
            cir.setReturnValue(false);
        }

    }

    @Inject(at = @At("HEAD"), method = "customServerAiStep", cancellable = true)
    protected void customServerAiStep(CallbackInfo ci) {
        if (isTamed()) {
            super.customServerAiStep();
            ci.cancel();
        }
    }

    @Inject(at = {@At("HEAD"), @At("RETURN")}, method = "hurt", cancellable = true)
    public void hurt(DamageSource pSource, float pAmount, CallbackInfoReturnable<Boolean> cir) {
        if (isTamed()) {
            stopBeingAngry();
        }

        if (isTamed() && pSource.getMsgId().equals("drown") && isInRain() && !isInWaterOrBubble()) {
            cir.setReturnValue(false);
        }
    }

    @Unique
    private boolean isInRain() {
        BlockPos blockpos = blockPosition();
        return this.level.isRainingAt(blockpos) || level.isRainingAt(new BlockPos((double) blockpos.getX(), getBoundingBox().maxY, (double) blockpos.getZ()));
    }


    public boolean isTamed() {
        return this.getFlag(2);
    }

    public boolean isOwnedBy(LivingEntity pEntity) {
        return pEntity == this.getOwner();
    }

    public Team getTeam() {
        if (this.isTamed()) {
            LivingEntity livingentity = this.getOwner();
            if (livingentity != null) {
                return livingentity.getTeam();
            }
        }

        return super.getTeam();
    }

    public boolean isAlliedTo(Entity pEntity) {
        if (this.isTamed()) {
            LivingEntity livingentity = this.getOwner();
            if (pEntity == livingentity) {
                return true;
            }

            if (livingentity != null) {
                return livingentity.isAlliedTo(pEntity);
            }
        }

        return super.isAlliedTo(pEntity);
    }

    @Nullable
    public LivingEntity getOwner() {
        try {
            UUID uuid = this.getOwnerUUID();
            return uuid == null ? null : this.level.getPlayerByUUID(uuid);
        } catch (IllegalArgumentException illegalargumentexception) {
            return null;
        }
    }

    @Nullable
    public UUID getOwnerUUID() {
        return this.entityData.get(DATA_ID_OWNER_UUID).orElse((UUID) null);
    }

    public void setOwnerUUID(@Nullable UUID pUuid) {
        this.entityData.set(DATA_ID_OWNER_UUID, Optional.ofNullable(pUuid));
    }

    public void setTamed(boolean pTamed) {
        this.setFlag(2, pTamed);
    }

    public boolean tame(Player pPlayer) {
        this.setOwnerUUID(pPlayer.getUUID());
        this.setTamed(true);


        this.level.broadcastEntityEvent(this, (byte) 7);
        return true;
    }


    public Vec3 getLastInteractPos() {
        return lastInteractPos;
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);

        if (itemstack.isEmpty()) {
            if (this.isTamed()) {
                InteractionResult interactionresult = super.mobInteract(pPlayer, pHand);
                if ((!interactionresult.consumesAction() || this.isBaby()) && this.isOwnedBy(pPlayer)) {
                    this.jumping = false;
                    this.navigation.stop();
                    this.setTarget((LivingEntity) null);
                    this.followState = followState.switchFollowState();

                    pPlayer.displayClientMessage(MutableComponent.create(new LiteralContents(followState.getFollowStateText())), true);

                    this.lastInteractPos = new Vec3(getX(), getY(), getZ());
                    return InteractionResult.SUCCESS;
                }
                return interactionresult;
            }
        }

        if (!itemstack.isEmpty()) {
            if (this.isFood(itemstack)) {
                return this.fedFood(pPlayer, itemstack);
            }

            if (itemstack.is(Items.FEATHER) && isTamed()) {
                this.setSilent(!isSilent());
                pPlayer.displayClientMessage(MutableComponent.create(new LiteralContents("Silenced: " + isSilent())), true);
            }


            if (itemstack.is(Items.POPPED_CHORUS_FRUIT) && !this.isAngry() && !isTamed()) {
                if (!pPlayer.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }

                if (this.random.nextInt(3) == 0) {
                    this.tame(pPlayer);
                    this.navigation.stop();
                    this.setTarget((LivingEntity) null);
                    this.tameableendermen$setOrderedToSit(true);
                    this.level.broadcastEntityEvent(this, (byte) 7);
                } else {
                    this.level.broadcastEntityEvent(this, (byte) 6);
                }

                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.sidedSuccess(this.level.isClientSide);
    }

    public boolean isFood(ItemStack pStack) {
        return FOOD_ITEMS.test(pStack);
    }

    public InteractionResult fedFood(Player pPlayer, ItemStack pStack) {
        boolean flag = this.handleEating(pPlayer, pStack);
        if (!pPlayer.getAbilities().instabuild && flag) {
            pStack.shrink(1);
        }

        if (this.level.isClientSide) {
            return InteractionResult.CONSUME;
        } else {
            return flag ? InteractionResult.SUCCESS : InteractionResult.PASS;
        }
    }

    public boolean handleEating(Player pPlayer, ItemStack pStack) {
        float f = 0.0F;
        if (pStack.is(Items.CHORUS_FRUIT)) {
            f = 10.0F;
        } else if (pStack.is(Items.APPLE)) {
            f = 5.0F;
        }

        if (this.getHealth() < this.getMaxHealth() && f > 0.0F) {
            this.heal(f);
        }

        this.eating();
        this.gameEvent(GameEvent.EAT);

        return true;
    }

    public void eating() {
        if (this.isSilent()) {
            return;
        }


        SoundEvent soundevent = this.getEatingSound();
        if (soundevent == null) {
            return;
        }
        this.level.playSound((Player) null, this.getX(), this.getY(), this.getZ(), soundevent, this.getSoundSource(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);

    }

    public int getMaxHeadXRot() {
        return this.isInSittingPose() ? 20 : super.getMaxHeadXRot();
    }

    public boolean isInSittingPose() {
        return (this.entityData.get(DATA_FLAGS_ID) & 1) != 0;
    }

    public void setInSittingPose(boolean pSitting) {
        byte b0 = this.entityData.get(DATA_FLAGS_ID);
        if (pSitting) {
            this.entityData.set(DATA_FLAGS_ID, (byte) (b0 | 1));
        } else {
            this.entityData.set(DATA_FLAGS_ID, (byte) (b0 & -2));
        }

    }

    public boolean tameableendermen$isOrderedToSit() {
        return followState.name().equals("SIT");
    }

    public void tameableendermen$setOrderedToSit(boolean pOrderedToSit) {
        if (pOrderedToSit) {
            followState = FollowState.SIT;
            return;
        }
        followState = FollowState.FOLLOW;
    }


    @Nullable
    public SoundEvent getEatingSound() {
        return SoundEvents.ENDERMAN_AMBIENT;
    }

    @Override
    public void checkDespawn() {
        if (isTamed()) {
            return;
        }
        super.checkDespawn();
    }

    @Inject(at = @At("HEAD"), method = "defineSynchedData")
    public void defineSynchedData(CallbackInfo ci) {
        this.entityData.define(DATA_FLAGS_ID, (byte) 0);
        this.entityData.define(DATA_ID_OWNER_UUID, Optional.empty());
    }


    @Inject(at = @At("HEAD"), method = "addAdditionalSaveData")
    public void addAdditionalSaveData(CompoundTag pCompound, CallbackInfo ci) {
        pCompound.putBoolean("Tame", this.isTamed());
        if (this.getOwnerUUID() != null) {
            pCompound.putUUID("Owner", this.getOwnerUUID());
        }

        pCompound.putString("FollowState", this.followState.name());
        pCompound.put("LastInteractPos", newDoubleList(lastInteractPos.x(), lastInteractPos.y(), lastInteractPos.z()));


    }

    @Inject(at = @At("HEAD"), method = "readAdditionalSaveData")
    public void readAdditionalSaveData(CompoundTag pCompound, CallbackInfo ci) {
        this.setTamed(pCompound.getBoolean("Tame"));
        UUID uuid;
        if (pCompound.hasUUID("Owner")) {
            uuid = pCompound.getUUID("Owner");
        } else {
            String s = pCompound.getString("Owner");
            uuid = OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), s);
        }

        if (uuid != null) {
            this.setOwnerUUID(uuid);
        }

        setFollowState(pCompound.getString("FollowState"));


        ListTag listTag = pCompound.getList("LastInteractPos", 6);
        lastInteractPos = new Vec3(listTag.getDouble(0), listTag.getDouble(1), listTag.getDouble(2));
    }


    public void spawnTamingParticles(boolean pTamed) {
        ParticleOptions particleoptions = pTamed ? ParticleTypes.HEART : ParticleTypes.SMOKE;

        for (int i = 0; i < 7; ++i) {
            double d0 = this.random.nextGaussian() * 0.02D;
            double d1 = this.random.nextGaussian() * 0.02D;
            double d2 = this.random.nextGaussian() * 0.02D;
            this.level.addParticle(particleoptions, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
        }

    }

    public void handleEntityEvent(byte pId) {
        if (pId == 7) {
            this.spawnTamingParticles(true);
        } else if (pId == 6) {
            this.spawnTamingParticles(false);
        } else {
            super.handleEntityEvent(pId);
        }

    }
}
