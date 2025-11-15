package com.uavwaffle.tameableendermen.entity.custom;

import com.uavwaffle.tameableendermen.TameableEndermen;
import com.uavwaffle.tameableendermen.entity.custom.goal.EnderFollowOwnerGoal;
import com.uavwaffle.tameableendermen.entity.custom.goal.EnderSitWhenOrderedToGoal;
import com.uavwaffle.tameableendermen.entity.custom.goal.WaterAvoidingRandomStrollWithinRadiusGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class TameableEndermanEntity extends EnderMan {
    private static final Ingredient FOOD_ITEMS = Ingredient.of(Items.WHEAT, Items.SUGAR, Blocks.HAY_BLOCK.asItem(), Items.APPLE, Items.GOLDEN_CARROT, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE);
    private static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(TameableEndermanEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Optional<UUID>> DATA_ID_OWNER_UUID = SynchedEntityData.defineId(TameableEndermanEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final int FLAG_TAME = 2;

    private boolean orderedToSit;
    private Vec3 lastInteractPos;

    public TameableEndermanEntity(EntityType<? extends EnderMan> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);

        this.setTamed(true);
        this.lastInteractPos = new Vec3(1,1,1);
    }

    @Override
    protected void registerGoals() {

        this.goalSelector.addGoal(2, new EnderSitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(6, new EnderFollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollWithinRadiusGoal(this, 1.0D, 0.0F));
//        super.registerGoals();
// OLD GOALS
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D, 0.0F));
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Endermite.class, true, false));
        this.targetSelector.addGoal(4, new ResetUniversalAngerTargetGoal<>(this, false));

    }

    protected boolean getFlag(int pFlagId) {
        return (this.entityData.get(DATA_FLAGS_ID) & pFlagId) != 0;
    }

    protected void setFlag(int pFlagId, boolean pValue) {
        byte b0 = this.entityData.get(DATA_FLAGS_ID);
        if (pValue) {
            this.entityData.set(DATA_FLAGS_ID, (byte) (b0 | pFlagId));
        } else {
            this.entityData.set(DATA_FLAGS_ID, (byte) (b0 & ~pFlagId));
        }

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
//        if (pPlayer instanceof ServerPlayer) {
//            CriteriaTriggers.TAME_ANIMAL.trigger((ServerPlayer)pPlayer, this);
//        }


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
                    this.setOrderedToSit(!this.isOrderedToSit());
                    this.jumping = false;
                    this.navigation.stop();
                    this.setTarget((LivingEntity) null);
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

//            if (!this.isTamed()) {
//                return InteractionResult.sidedSuccess(this.level.isClientSide);
//            }


            if (itemstack.is(Items.BONE) && !this.isAngry() && !isTamed()) {
                if (!pPlayer.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }

                if (this.random.nextInt(3) == 0) {
                    this.tame(pPlayer);
                    this.navigation.stop();
                    this.setTarget((LivingEntity) null);
                    this.setOrderedToSit(true);
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

    protected boolean handleEating(Player pPlayer, ItemStack pStack) {
        boolean flag = false;
        float f = 0.0F;
        if (pStack.is(Items.WHEAT)) {
            f = 2.0F;
        } else if (pStack.is(Items.SUGAR)) {
            f = 1.0F;
        } else if (pStack.is(Blocks.HAY_BLOCK.asItem())) {
            f = 20.0F;
        } else if (pStack.is(Items.APPLE)) {
            f = 3.0F;
        } else if (pStack.is(Items.GOLDEN_CARROT)) {
            f = 4.0F;
        } else if (pStack.is(Items.GOLDEN_APPLE) || pStack.is(Items.ENCHANTED_GOLDEN_APPLE)) {
            f = 10.0F;
        }

        if (this.getHealth() < this.getMaxHealth() && f > 0.0F) {
            this.heal(f);
            flag = true;
        }

        if (flag) {
            this.eating();
            this.gameEvent(GameEvent.EAT);
        }

        return flag;
    }

    private void eating() {
        if (!this.isSilent()) {
            SoundEvent soundevent = this.getEatingSound();
            if (soundevent != null) {
                this.level.playSound((Player) null, this.getX(), this.getY(), this.getZ(), soundevent, this.getSoundSource(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
            }
        }

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

    public boolean isOrderedToSit() {
        return this.orderedToSit;
    }

    public void setOrderedToSit(boolean pOrderedToSit) {
        this.orderedToSit = pOrderedToSit;
    }


    @Nullable
    protected SoundEvent getEatingSound() {
        return SoundEvents.HORSE_EAT;
    }


    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FLAGS_ID, (byte) 0);
        this.entityData.define(DATA_ID_OWNER_UUID, Optional.empty());
    }


    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
//        compound.putBoolean("EatingHaystack", this.isEating());
//        compound.putBoolean("Bred", this.isBred());
//        compound.putInt("Temper", this.getTemper());
        compound.putBoolean("Tame", this.isTamed());
        if (this.getOwnerUUID() != null) {
            compound.putUUID("Owner", this.getOwnerUUID());
        }

        compound.putBoolean("Sitting", this.orderedToSit);
        compound.put("LastInteractPos", super.newDoubleList(lastInteractPos.x(), lastInteractPos.y(), lastInteractPos.z()));

//        if (!this.inventory.getItem(0).isEmpty()) {
//            compound.put("SaddleItem", this.inventory.getItem(0).save(new CompoundTag()));
//        }

    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
//        this.setEating(compound.getBoolean("EatingHaystack"));
//        this.setBred(compound.getBoolean("Bred"));
//        this.setTemper(compound.getInt("Temper"));
        this.setTamed(compound.getBoolean("Tame"));
        UUID uuid;
        if (compound.hasUUID("Owner")) {
            uuid = compound.getUUID("Owner");
        } else {
            String s = compound.getString("Owner");
            uuid = OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), s);
        }

        if (uuid != null) {
            this.setOwnerUUID(uuid);
        }

        this.orderedToSit = compound.getBoolean("Sitting");
        this.setInSittingPose(this.orderedToSit);

        ListTag listTag = compound.getList("LastInteractPos", 6);
        System.out.println("HI NOOB");
        System.out.println(listTag.getDouble(0));
        lastInteractPos = new Vec3(listTag.getDouble(0),listTag.getDouble(1),listTag.getDouble(2));
    }

    protected void spawnTamingParticles(boolean pTamed) {
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
