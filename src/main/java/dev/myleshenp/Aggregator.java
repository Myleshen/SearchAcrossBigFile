package dev.myleshenp;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Aggregator {
    private final Map<String, List<MatchDetails>> aggregatedResults = new HashMap<String, List<MatchDetails>>();

    public void aggregate(Map<String, List<MatchDetails>> workerResult) {
        workerResult.forEach((key, value) -> {
            var oldValues = aggregatedResults.getOrDefault(key, new ArrayList<>());
            oldValues.addAll(value);
            aggregatedResults.put(key, oldValues);
        });
    }

    public void printResults() {
        aggregatedResults.forEach((key, value) -> System.out.println(key + " --> " + value));
    }
}
