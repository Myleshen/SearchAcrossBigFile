package dev.myleshenp;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MatchDetails {
    private long lineOffSet;
    private long charOffSet;


    @Override
    public String toString() {
        return "[lineOffSet = " + lineOffSet + ", charOffSet = " + charOffSet + "]";
    }
}
