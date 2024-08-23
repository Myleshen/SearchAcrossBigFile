package dev.myleshenp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record InternalMatchWork (String text, String searchString, long lineOffSet, long charOffSetStart) implements Callable<Map<String, List<MatchDetails>>> {

    @Override
    public Map<String, List<MatchDetails>> call() {
        var result = new HashMap<String, List<MatchDetails>>();
        var locations = new ArrayList<MatchDetails>();
        var lines = text.split("\n");
        var charOffSet = charOffSetStart;

        Pattern pattern = Pattern.compile(searchString);
        // Tried with Streams and Enhanced For, but as i need the line count as well
        // it makes sense to use the regular for loop as it has the index always available
        for (int i = 0; i < lines.length; i++) {
            var line = lines[i];
            Matcher matcher = pattern.matcher(line);
            while (matcher.find()) {
                // adding +1 to both line and character since Java is 0 based and the files are 1 based
                locations.add(new MatchDetails(lineOffSet + i + 1, matcher.start() + 1, charOffSet + matcher.start() + 1));
            }
            charOffSet += line.length() + 1;
        }

        if (!locations.isEmpty()) {
            result.put(searchString, locations);
        }

        return result;
    }
}
