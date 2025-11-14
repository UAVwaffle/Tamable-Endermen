package com.uavwaffle.tameableendermen.entity.custom;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class TameableEndermanEntity extends EnderMan {
    public TameableEndermanEntity(EntityType<? extends EnderMan> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    private static final Ingredient FOOD_ITEMS = Ingredient.of(Items.WHEAT, Items.SUGAR, Blocks.HAY_BLOCK.asItem(), Items.APPLE, Items.GOLDEN_CARROT, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE);
    private static final EntityDataAccessor<Byte> DATA_ID_FLAGS = SynchedEntityData.defineId(AbstractHorse.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Optional<UUID>> DATA_ID_OWNER_UUID = SynchedEntityData.defineId(AbstractHorse.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final int FLAG_TAME = 2;



    protected boolean getFlag(int pFlagId) {
        return (this.entityData.get(DATA_ID_FLAGS) & pFlagId) != 0;
    }

    protected void setFlag(int pFlagId, boolean pValue) {
        byte b0 = this.entityData.get(DATA_ID_FLAGS);
        if (pValue) {
            this.entityData.set(DATA_ID_FLAGS, (byte)(b0 | pFlagId));
        } else {
            this.entityData.set(DATA_ID_FLAGS, (byte)(b0 & ~pFlagId));
        }

    }

    public boolean isTamed() {
        return this.getFlag(2);
    }

    @Nullable
    public UUID getOwnerUUID() {
        return this.entityData.get(DATA_ID_OWNER_UUID).orElse((UUID)null);
    }

    public void setOwnerUUID(@Nullable UUID pUuid) {
        this.entityData.set(DATA_ID_OWNER_UUID, Optional.ofNullable(pUuid));
    }

    public void setTamed(boolean pTamed) {
        this.setFlag(2, pTamed);
    }

    public boolean tameWithName(Player pPlayer) {
        this.setOwnerUUID(pPlayer.getUUID());
        this.setTamed(true);
//        if (pPlayer instanceof ServerPlayer) {
//            CriteriaTriggers.TAME_ANIMAL.trigger((ServerPlayer)pPlayer, this);
//        }

        this.level.broadcastEntityEvent(this, (byte)7);
        return true;
    }


    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);

        if (!itemstack.isEmpty()) {
            if (this.isFood(itemstack)) {
                return this.fedFood(pPlayer, itemstack);
            }

            if (!this.isTamed()) {
                return InteractionResult.sidedSuccess(this.level.isClientSide);
            }
        }

        return InteractionResult.sidedSuccess(this.level.isClientSide);
    }

    public boolean isFood(ItemStack pStack) {
        return FOOD_ITEMS.test(pStack);
    }

    public InteractionResult fedFood(Player pPlayer, ItemStack pStack) {
        boolean flag = this.handleEating(pPlayer, pStack);
        if (!pPlayer.getAbilities().instabuild) {
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
                this.level.playSound((Player)null, this.getX(), this.getY(), this.getZ(), soundevent, this.getSoundSource(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
            }
        }

    }

    @Nullable
    protected SoundEvent getEatingSound() {
        return SoundEvents.HORSE_EAT;
    }




    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ID_FLAGS, (byte)0);
        this.entityData.define(DATA_ID_OWNER_UUID, Optional.empty());
    }


    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
//        pCompound.putBoolean("EatingHaystack", this.isEating());
//        pCompound.putBoolean("Bred", this.isBred());
//        pCompound.putInt("Temper", this.getTemper());
        pCompound.putBoolean("Tame", this.isTamed());
        if (this.getOwnerUUID() != null) {
            pCompound.putUUID("Owner", this.getOwnerUUID());
        }

//        if (!this.inventory.getItem(0).isEmpty()) {
//            pCompound.put("SaddleItem", this.inventory.getItem(0).save(new CompoundTag()));
//        }

    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
//        this.setEating(pCompound.getBoolean("EatingHaystack"));
//        this.setBred(pCompound.getBoolean("Bred"));
//        this.setTemper(pCompound.getInt("Temper"));
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

//        if (pCompound.contains("SaddleItem", 10)) {
//            ItemStack itemstack = ItemStack.of(pCompound.getCompound("SaddleItem"));
//            if (itemstack.is(Items.SADDLE)) {
//                this.inventory.setItem(0, itemstack);
//            }
//        }
//
//        this.updateContainerEquipment();
    }

    protected void spawnTamingParticles(boolean pTamed) {
        ParticleOptions particleoptions = pTamed ? ParticleTypes.HEART : ParticleTypes.SMOKE;

        for(int i = 0; i < 7; ++i) {
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
