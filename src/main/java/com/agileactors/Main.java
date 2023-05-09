package com.agileactors;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Mimics a service that integrates with a third party and loads data <capacity> times.
public class Main {

    // Create a list with Callable commands
    private static final int capacity = 5;
    private static final List<Callable<String>> getDataCommands = new ArrayList<>(capacity);

    static {
        int i = 1;
        while (i < capacity) {
            int finalI = i++;
            getDataCommands.add(() -> {
                getRemoteData();
                return "command " + finalI;
            });
        }
    }

    public static void main(String[] args) {
        loadDataUsingNormalLoop();
        loadDataUsingOsThreadsLoop();
        loadDataUsingVirtualThreadsLoop();
    }

    private static void loadDataUsingVirtualThreadsLoop() {
        var start = System.currentTimeMillis();
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            executor.invokeAll(getDataCommands);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("virtualThreadsLoop took " + (System.currentTimeMillis() - start) + " millis");
    }

    private static void loadDataUsingOsThreadsLoop() {
        var start = System.currentTimeMillis();
        try (ExecutorService executor = Executors.newFixedThreadPool(16)) {
            executor.invokeAll(getDataCommands);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("osThreadsLoop took " + (System.currentTimeMillis() - start) + " millis");
    }

    private static void loadDataUsingNormalLoop() {
        var start = System.currentTimeMillis();
        for (Callable<String> command : getDataCommands) {
            try {
                command.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("normalLoop took " + (System.currentTimeMillis() - start) + " millis");
    }

    // Just invoke a remote api to get dummy data
    private static void getRemoteData() throws IOException {
        URI.create("https://run.mocky.io/v3/8b916b06-1f82-446c-9391-263f37058ffe").toURL().getContent();
    }
}