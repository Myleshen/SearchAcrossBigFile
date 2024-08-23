package dev.myleshenp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {
    static long MAX_BATCH_SIZE = 1000L;

    public static void main(String[] args) throws InterruptedException, IOException {
        String filePathString = "src/main/resources/big.txt";
        List<Future<Map<String, List<MatchDetails>>>> workers = new ArrayList<>();

        long startTime = System.currentTimeMillis();

        try(ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            long currentLine = 0L;
            try (BufferedReader reader = new BufferedReader(new FileReader(filePathString))) {
                String line;
                StringBuilder currentBatchBuilder = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    currentBatchBuilder.append(line).append("\n");
                    if (++currentLine % MAX_BATCH_SIZE == 0) {
                        var currentBatch = currentBatchBuilder.toString();
                        Future<Map<String, List<MatchDetails>>> batchWorker = executorService.submit(new MatchWork(currentBatch, List.of(""), currentLine));
                        workers.add(batchWorker);
                        currentBatchBuilder.setLength(0);
                    }
                }

                if (!currentBatchBuilder.isEmpty()) {
                    var currentBatch = currentBatchBuilder.toString();
                    Future<Map<String, List<MatchDetails>>> batchWorker = executorService.submit(new MatchWork(currentBatch, List.of(""), currentLine));
                    workers.add(batchWorker);
                }
            }
        }
        long endTime = System.currentTimeMillis();
        workers.stream().map(worker -> {
            try {
                return worker.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }).toList().forEach(System.out::println);

        System.out.println("\nTime taken: " + (endTime - startTime) + "ms");

    }
}
