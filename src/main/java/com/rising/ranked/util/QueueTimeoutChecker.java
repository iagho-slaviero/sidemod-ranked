package com.rising.ranked.util;

import com.rising.ranked.manager.QueueManager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class QueueTimeoutChecker {
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public static void start() {
        // Executa a cada 30 segundos, por exemplo
        scheduler.scheduleAtFixedRate(() -> {
            QueueManager.checkQueueTimeouts();
        }, 0, 30, TimeUnit.SECONDS);
    }
}
