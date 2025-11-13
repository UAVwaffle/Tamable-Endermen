package com.uavwaffle.tameableendermen.event;

import com.uavwaffle.tameableendermen.TameableEndermen;
import com.uavwaffle.tameableendermen.entity.ModEntities;
import com.uavwaffle.tameableendermen.entity.custom.TameableEndermanEntity;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

    @Mod.EventBusSubscriber(modid = TameableEndermen.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public class ModEventBusEvents {

        @SubscribeEvent
        public static void registerAttributes(EntityAttributeCreationEvent event) {
            event.put(ModEntities.TAMEABLE_ENDERMAN.get(), TameableEndermanEntity.createAttributes().build());

        }
}

