package com.agileactors;

import java.util.ArrayList;
import java.util.concurrent.*;

public class Playground {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        // assume that I have 2 operations that I want to parallelize

        Callable<String> command1 = () -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Running command 1 at " + Thread.currentThread().getName());
            return "command 1";
        };
        Callable<String> command2 = () -> {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Running command 2 at " + Thread.currentThread().getName());
            return "command 2";
        };

        var futures = new ArrayList<Future<String>>();
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            futures.add(executor.submit(command1));
            futures.add(executor.submit(command2));
        }

        System.out.println(futures.get(0).get());
        System.out.println(futures.get(1).get());


        System.out.println("He he I've finished.");
    }
}
