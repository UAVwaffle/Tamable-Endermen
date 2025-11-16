package com.uavwaffle.tamableendermen;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod(TamableEndermen.MODID)
public class TamableEndermen {

    public static final String MODID = "tamableendermen";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public TamableEndermen() {
        MinecraftForge.EVENT_BUS.register(this);
    }


}
