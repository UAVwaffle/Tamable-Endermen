package com.uavwaffle.tamableendermen.event;

import com.uavwaffle.tamableendermen.TamableEndermen;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

    @Mod.EventBusSubscriber(modid = TamableEndermen.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public class ModEventBusEvents {

        @SubscribeEvent
        public static void registerAttributes(EntityAttributeCreationEvent event) {
//            event.put(ModEntities.TAMEABLE_ENDERMAN.get(), TameableEndermanEntity.createAttributes().build());

        }
}

