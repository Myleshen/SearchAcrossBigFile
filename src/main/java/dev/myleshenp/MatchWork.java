package dev.myleshenp;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class MatchWork implements Callable<Map<String, List<MatchDetails>>> {

   private final String text;
   private final List<String> searchStrings;
   private final long lineOffSet;


    @Override
    public Map<String, List<MatchDetails>> call() throws Exception {
        var result = new HashMap<String, List<MatchDetails>>();
        result.put("temp", List.of(new MatchDetails(lineOffSet,text.length())));
        return result;
    }
}
