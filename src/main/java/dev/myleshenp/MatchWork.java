package dev.myleshenp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public record MatchWork(String text,
                        List<String> searchStrings,
                        long lineOffSet,
                        long charOffSet)
        implements Callable<Map<String, List<MatchDetails>>> {


    @Override
    public Map<String, List<MatchDetails>> call() throws Exception {
        List<Future<Map<String, List<MatchDetails>>>> matchFutures = new ArrayList<>();
        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            for (String searchString : searchStrings) {
                // If the search string has a blank (Which in this case doesn't but in a normal user input might occur)
                // We are having an extra condition to just skip those cases
                // We can also throw an error for that, but in this case it is fine to just skip them as we are providing the input
                if (!searchString.isEmpty()) {
                    Future<Map<String, List<MatchDetails>>> internalWork = executorService.submit(new InternalMatchWork(text, searchString, lineOffSet, charOffSet));
                    matchFutures.add(internalWork);
                }
            }
        }
        
        Aggregator aggregator = new Aggregator();
        for (Future<Map<String, List<MatchDetails>>> future : matchFutures) {
            aggregator.aggregate(future.get());
        };

        return aggregator.getAggregatedResults();
    }
}
