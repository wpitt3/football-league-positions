package dulcinea.prediction;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LeaguePositionStats {
    private String teamName;
    private List<Integer> highestPossible;
    private List<Integer> lowestPossible;
    private Integer currentPosition;

    LeaguePositionStats(String teamName, Integer currentPosition) {
        this.teamName = teamName;
        this.currentPosition = currentPosition;
        highestPossible = new ArrayList<>();
        lowestPossible = new ArrayList<>();
    }

    public String getTeamName() {
        return teamName;
    }

    Integer getCurrentPosition() {
        return currentPosition;
    }

    LeaguePositionStats withLowestPossible(Integer lowestPossible) {
        this.lowestPossible.add(lowestPossible);
        return this;
    }

    LeaguePositionStats withHighestPossible(Integer highestPossible) {
        this.highestPossible.add(highestPossible);
        return this;
    }

    public List<Integer> getHighestPossible() {
        return highestPossible;
    }

    public List<Integer> getLowestPossible() {
        return lowestPossible;
    }

    @Override
    public String toString() {
        String result = "[[";
        result += highestPossible.stream().map( n -> n.toString() ).collect(Collectors.joining(", "));
        result += "], [";
        result += lowestPossible.stream().map( n -> n.toString() ).collect(Collectors.joining(", "));
        result += "]],";
        return result;
    }
}
