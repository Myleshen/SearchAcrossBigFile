package dev.myleshenp;

public record MatchDetails(long lineOffSet, long charOffSet, long charOffSetStart) {
    @Override
    public String toString() {
//        return "[lineOffSet = " + lineOffSet + ", charOffSetStart = " + charOffSet + ", charOffSetStart = " + charOffSetStart + "]";
        return "[lineOffSet = " + lineOffSet + ", charOffSet = " + charOffSet + "]";
    }
}
