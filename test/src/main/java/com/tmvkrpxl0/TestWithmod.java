package com.tmvkrpxl0;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("test")
public class TestWithmod {

    @Mod.EventBusSubscriber(Dist.DEDICATED_SERVER)
    public static class ServerTest {
        @SubscribeEvent
        public static void working1(Event event) {

        }

        @SubscribeEvent
        static void working2(PlayerInteractEvent event) {

        }

        /*@SubscribeEvent
        private static void notWorking1(Event event) { // cause: it is private

        }

        @SubscribeEvent
        public void notWorking2(Event event) { // cause: it is not static

        }

        @SubscribeEvent
        private void notWorking3(Event event) { // cause: it is private and non-static

        }

        @SubscribeEvent
        public static void notWorking4(Event event, int dummy) { // cause: it has one more argument

        }

        @SubscribeEvent
        public static void notWorking4_2(int dummy) { // cause: argument is not event

        }

        @SubscribeEvent
        public static void notWorking5() { // cause: well, event is missing, duh

        }

        @SubscribeEvent
        public static void notWorking6(ScreenEvent event) { // cause: this doesn't listen for client

        }

        @SubscribeEvent
        public static void notWorking7(FMLDedicatedServerSetupEvent event) { // cause: this doesn't listen for mod bus events

        }*/
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBusTest {
        /*@SubscribeEvent
        public static void notWorking8(PlayerInteractEvent event) { // cause: this doesn't listen for forge bus events

        }*/
    }
}