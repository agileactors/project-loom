package com.agileactors;

import java.net.URI;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Mimics a service that integrates with a third party api and loads data <numberOfCallableCommands> times.
 * Use JDK 21 EA (SDKMAN: sdk use java 21.ea.21-open)
 */
public class Main {

    // Number of Callable commands
    private static final int numberOfCallableCommands = 10;

    public static void main(String[] args) {
        printAvailableProcessors();
        loadDataUsingNormalLoop();
        loadDataUsingOsThreadsLoop();
        loadIngDataUsingParallelStream();
        loadDataUsingVirtualThreadsLoop();
    }

    private static void printAvailableProcessors() {
        System.out.println("Available processors [" + Runtime.getRuntime().availableProcessors() + "]");
    }

    private static void loadDataUsingVirtualThreadsLoop() {
        executeWithLogsAndMetrics("loadDataUsingVirtualThreadsLoop", (callableCommands) -> {
            try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
                executor.invokeAll(callableCommands.toList());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return null;
        });
    }

    private static void loadDataUsingOsThreadsLoop() {
        executeWithLogsAndMetrics("loadDataUsingOsThreadsLoop", (callableCommands) -> {
            try (ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())) {
                executor.invokeAll(callableCommands.toList());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return null;
        });
    }

    private static void loadIngDataUsingParallelStream() {
        executeWithLogsAndMetrics("loadIngDataUsingParallelStream", (callableCommands) -> {
            callableCommands.parallel()
                    .forEach(action -> {
                        try {
                            action.call();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
            return null;
        });
    }

    public static void loadDataUsingNormalLoop() {
        executeWithLogsAndMetrics("loadDataUsingNormalLoop", (callableCommands) -> {
            callableCommands.forEach(command -> {
                try {
                    command.call();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            return null;
        });
    }

    private static void sleep() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static Stream<Callable<Object>> getCallableCommands() {
        return IntStream.range(0, numberOfCallableCommands).mapToObj(Main::getRemoteData);
    }

    // Just invoke a remote api to get dummy data. This is a blocking statement
    private static Callable<Object> getRemoteData(int i) {
        return () -> {
            var threadName = Optional.of(Thread.currentThread().getName().trim());
            threadName.ifPresent(name -> System.out.println("Thread [" + name + "] runs [" + i + "]"));

//            sleep();
            return URI.create("https://run.mocky.io/v3/8b916b06-1f82-446c-9391-263f37058ffe?random=" + i).toURL().getContent();
        };
    }

    public static void executeWithLogsAndMetrics(String tag, Function<Stream<Callable<Object>>, Void> function) {
        var callableCommands = getCallableCommands();

        System.out.println("\n" + tag + " started");
        var start = System.currentTimeMillis();
        function.apply(callableCommands);
        System.out.println(tag + " took " + (System.currentTimeMillis() - start) + " millis");
    }
}