package dev.myleshenp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class Main {
    static long MAX_BATCH_SIZE = 1000L;

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        /* Assumptions Made in the search pattern
        * 1. Since these are all common Names they should follow Title case (Exact Match in our case)
        * 2. For Debugging purpose / as the console might not be able to show the full result we are writing the result to output.txt in resources folder
        * 3. The program also calculates the character off set from the start of the file which is commented out in the {@link MatchDetails}
        * */
        String filePathString = "src/main/resources/big.txt";
        List<String> searchStrings = Arrays.asList("James,John,Robert,Michael,William,David,Richard,Charles,Joseph,Thomas,Christopher,Daniel,Paul,Mark,Donald,George,Kenneth,Steven,Edward,Brian,Ronald,Anthony,Kevin,Jason,Matthew,Gary,Timothy,Jose,Larry,Jeffrey,Frank,Scott,Eric,Stephen,Andrew,Raymond,Gregory,Joshua,Jerry,Dennis,Walter,Patrick,Peter,Harold,Douglas,Henry,Carl,Arthur,Ryan,Roger".split(","));
        List<Future<Map<String, List<MatchDetails>>>> workers = new ArrayList<>();

        long startTime = System.currentTimeMillis();

        try(ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            long currentLine = 0L;
            long currentBatchCharOffset = 0L;
            try (BufferedReader reader = new BufferedReader(new FileReader(filePathString))) {
                String line;
                StringBuilder currentBatchBuilder = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    currentBatchBuilder.append(line).append("\n");
                    if (++currentLine % MAX_BATCH_SIZE == 0) {
                        var currentBatch = currentBatchBuilder.toString();
                        Future<Map<String, List<MatchDetails>>> batchWorker = executorService.submit(new MatchWork(currentBatch, searchStrings, currentLine - MAX_BATCH_SIZE, currentBatchCharOffset));
                        workers.add(batchWorker);
                        currentBatchCharOffset += currentBatch.length();
                        currentBatchBuilder.setLength(0);
                    }
                }

                if (!currentBatchBuilder.isEmpty()) {
                    var currentBatch = currentBatchBuilder.toString();
                    currentBatchCharOffset += currentBatch.length();
                    Future<Map<String, List<MatchDetails>>> batchWorker = executorService.submit(new MatchWork(currentBatch, searchStrings, currentLine - (currentLine % MAX_BATCH_SIZE), currentBatchCharOffset));
                    workers.add(batchWorker);
                }
            }
        }
        long endTime = System.currentTimeMillis();

        Aggregator aggregator = new Aggregator();
        for (Future<Map<String, List<MatchDetails>>> work : workers) {
            aggregator.aggregate(work.get());
        }

        aggregator.printResults();
        Files.writeString(Paths.get("src/main/resources/output.txt"),
                aggregator.getAggregatedResults().entrySet()
                        .stream()
                        .sorted(Map.Entry.comparingByKey())
                        .map(entry -> entry.getKey()+ " --> " + entry.getValue())
                        .collect(Collectors.joining("\n")), StandardCharsets.UTF_8);

        System.out.println("\nTime taken: " + (endTime - startTime) + "ms");

    }
}
