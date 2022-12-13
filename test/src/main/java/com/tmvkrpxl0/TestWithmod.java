package com.tmvkrpxl0;

import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("test")
public class TestWithmod {

    @Mod.EventBusSubscriber
    public static class CommonTest {
        @SubscribeEvent
        public static void working1(Event event) {

        }

        @SubscribeEvent
        static void working2(PlayerInteractEvent event) {

        }

        /*@SubscribeEvent
        private void notWorking1(Event event) {

        }*/

        /*@SubscribeEvent
        public void notWorking2(Event event) {

        }*/

        /*@SubscribeEvent
        private void notWorking3(Event event) {

        }*/

        /*@SubscribeEvent
        public static void notWorking4(Event event, int dummy) {

        }*/

        /*@SubscribeEvent
        public static void notWorking5() {

        }*/
    }
}