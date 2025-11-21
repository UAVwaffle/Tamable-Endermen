package com.uavwaffle.tameableendermen;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod(TameableEndermen.MODID)
public class TameableEndermen {

    public static final String MODID = "tameableendermen";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public TameableEndermen() {
        MinecraftForge.EVENT_BUS.register(this);
    }


}
