package dulcinea;

import org.apache.commons.lang3.StringUtils;

public class LeaguePositionStats {
    private String teamName;
    private Integer highestPossible;
    private Integer currentPosition;
    private Integer lowestPossible;

    public LeaguePositionStats(String teamName, Integer currentPosition) {
        this.teamName = teamName;
        this.currentPosition = currentPosition;
    }

    public LeaguePositionStats(String teamName, Integer highestPossible, Integer currentPosition, Integer lowestPossible) {
        this.teamName = teamName;
        this.highestPossible = highestPossible;
        this.currentPosition = currentPosition;
        this.lowestPossible = lowestPossible;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public Integer getHighestPossible() {
        return highestPossible;
    }

    public void setHighestPossible(Integer highestPossible) {
        this.highestPossible = highestPossible;
    }

    public Integer getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(Integer currentPosition) {
        this.currentPosition = currentPosition;
    }

    public Integer getLowestPossible() {
        return lowestPossible;
    }

    public void setLowestPossible(Integer lowestPossible) {
        this.lowestPossible = lowestPossible;
    }

    @Override
    public String toString() {
        String result = "";
        result += StringUtils.rightPad(teamName, 8);
        result += intStatToString(highestPossible);
        result += intStatToString(currentPosition);
        result += intStatToString(lowestPossible);
        return result;
    }

    private static String intStatToString(Integer stat){
        return StringUtils.leftPad(stat != null ? stat.toString() : "", 3);
    }
}
