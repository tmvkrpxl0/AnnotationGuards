package com.tmvkrpxl0;

import net.minecraftforge.fml.common.Mod;

@SuppressWarnings("unused")
public class TestsWithoutMod {
    @Mod.EventBusSubscriber()
    public static class NotWorking {

    }

    @Mod.EventBusSubscriber(modid = "test")
    public static class Working {

    }
}
