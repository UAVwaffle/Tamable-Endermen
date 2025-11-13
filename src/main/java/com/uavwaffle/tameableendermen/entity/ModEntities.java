package com.uavwaffle.tameableendermen.entity;

import com.uavwaffle.tameableendermen.TameableEndermen;
import com.uavwaffle.tameableendermen.entity.custom.TameableEndermanEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, TameableEndermen.MODID);

    public static final RegistryObject<EntityType<TameableEndermanEntity>> TAMEABLE_ENDERMAN =
            ENTITY_TYPES.register("tameable_enderman",
                    () -> EntityType.Builder.of(TameableEndermanEntity::new, MobCategory.MONSTER)
                            .sized(0.6F, 5.9F).build("tameable_enderman"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
