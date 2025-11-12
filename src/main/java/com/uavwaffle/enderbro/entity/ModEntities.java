package com.uavwaffle.enderbro.entity;

import com.uavwaffle.enderbro.Enderbro;
import com.uavwaffle.enderbro.entity.custom.EnderBroEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Enderbro.MODID);

    public static final RegistryObject<EntityType<EnderBroEntity>> ENDER_BRO =
            ENTITY_TYPES.register("ender_bro",
                    () -> EntityType.Builder.of(EnderBroEntity::new, MobCategory.MONSTER)
                            .sized(0.6F, 5.9F).build("ender_bro"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
