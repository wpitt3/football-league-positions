package dulcinea;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LeaguePositionStats {
    private String teamName;
    private List<Integer> highestPossible;
    private List<Integer> lowestPossible;
    private Integer currentPosition;

    public LeaguePositionStats(String teamName, Integer currentPosition) {
        this.teamName = teamName;
        this.currentPosition = currentPosition;
        highestPossible = new ArrayList<>();
        lowestPossible = new ArrayList<>();
    }


    public String getTeamName() {
        return teamName;
    }

    public void addHighestPossible(Integer highestPossible) {
        this.highestPossible.add(highestPossible);
    }

    public Integer getCurrentPosition() {
        return currentPosition;
    }

    public void addLowestPossible(Integer lowestPossible) {
        this.lowestPossible.add(lowestPossible);
    }

    public LeaguePositionStats withLowestPossible(Integer lowestPossible) {
        this.lowestPossible.add(lowestPossible);
        return this;
    }

    public LeaguePositionStats withHighestPossible(Integer highestPossible) {
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
        String result = "";
        result += teamName + ", ";
        result += currentPosition + ", ";
        result += highestPossible.stream().map( n -> n.toString() ).collect(Collectors.joining(", ")) + ", ";
        result += lowestPossible.stream().map( n -> n.toString() ).collect(Collectors.joining(", "));
        return result;
    }
}
