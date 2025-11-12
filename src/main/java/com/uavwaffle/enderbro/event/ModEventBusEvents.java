package com.uavwaffle.enderbro.event;

import com.uavwaffle.enderbro.Enderbro;
import com.uavwaffle.enderbro.entity.ModEntities;
import com.uavwaffle.enderbro.entity.custom.EnderBroEntity;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

    @Mod.EventBusSubscriber(modid = Enderbro.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public class ModEventBusEvents {

        @SubscribeEvent
        public static void registerAttributes(EntityAttributeCreationEvent event) {
            event.put(ModEntities.ENDER_BRO.get(), EnderBroEntity.createAttributes().build());

        }
}

