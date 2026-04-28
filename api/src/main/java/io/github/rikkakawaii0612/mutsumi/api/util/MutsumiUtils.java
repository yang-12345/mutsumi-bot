package io.github.rikkakawaii0612.mutsumi.api.util;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class MutsumiUtils {
    public static <T> List<T> getAsync(Collection<Supplier<T>> suppliers) {
        if (suppliers.isEmpty()) {
            return List.of();
        }
        try (ExecutorService executor = Executors.newFixedThreadPool(30)) {
            List<CompletableFuture<T>> futures = suppliers.stream()
                    .map(supplier -> CompletableFuture.supplyAsync(supplier, executor))
                    .toList();

            return futures.stream().map(CompletableFuture::join).toList();
        }
    }
}
