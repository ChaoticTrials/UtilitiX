package de.melanx.utilitix.util;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Tiny replacement for Forge's removed {@code WorldWorkerManager}. Registered workers get a slice of
 * time at the end of each server tick and are dropped once they report no more work. Multiple workers
 * run side by side (e.g. one ancient-city search per player).
 */
@EventBusSubscriber(modid = "utilitix")
public class WorkerManager {

    public interface Worker {

        /**
         * @return whether this worker still has work queued.
         */
        boolean hasWork();

        /**
         * Perform one unit of work. @return {@code true} to keep going this tick if time remains.
         */
        boolean doWork();
    }

    private static final List<Worker> WORKERS = new ArrayList<>();

    public static void addWorker(Worker worker) {
        WORKERS.add(worker);
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        if (WORKERS.isEmpty()) {
            return;
        }

        Iterator<Worker> it = WORKERS.iterator();
        while (it.hasNext()) {
            Worker worker = it.next();
            while (worker.hasWork() && event.hasTime()) {
                if (!worker.doWork()) {
                    break;
                }
            }

            if (!worker.hasWork()) {
                it.remove();
            }
        }
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        WORKERS.clear();
    }
}
