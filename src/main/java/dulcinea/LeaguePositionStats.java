package dulcinea;

import org.apache.commons.lang3.StringUtils;

public class LeaguePositionStats {
    private String teamName;
    private Integer highestImpossible;
    private Integer highestPossible;
    private Integer highestWithoutLargeSwing;
    private Integer currentPosition;
    private Integer lowestWithoutLargeSwing;
    private Integer lowestPossible;
    private Integer lowestImpossible;

    public LeaguePositionStats(String teamName, Integer currentPosition) {
        this.teamName = teamName;
        this.currentPosition = currentPosition;
    }

    public LeaguePositionStats(String teamName, Integer highestImpossible, Integer highestPossible, Integer highestWithoutLargeSwing, Integer currentPosition, Integer lowestWithoutLargeSwing, Integer lowestPossible, Integer lowestImpossible) {
        this.teamName = teamName;
        this.highestImpossible = highestImpossible;
        this.highestPossible = highestPossible;
        this.highestWithoutLargeSwing = highestWithoutLargeSwing;
        this.currentPosition = currentPosition;
        this.lowestWithoutLargeSwing = lowestWithoutLargeSwing;
        this.lowestPossible = lowestPossible;
        this.lowestImpossible = lowestImpossible;
    }

    public String getTeamName() {
        return teamName;
    }

    public Integer getHighestImpossible() {
        return highestImpossible;
    }

    public void setHighestImpossible(Integer highestImpossible) {
        this.highestImpossible = highestImpossible;
    }

    public Integer getHighestPossible() {
        return highestPossible;
    }

    public void setHighestPossible(Integer highestPossible) {
        this.highestPossible = highestPossible;
    }

    public Integer getHighestWithoutLargeSwing() {
        return highestWithoutLargeSwing;
    }

    public void setHighestWithoutLargeSwing(Integer highestWithoutLargeSwing) {
        this.highestWithoutLargeSwing = highestWithoutLargeSwing;
    }

    public Integer getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(Integer currentPosition) {
        this.currentPosition = currentPosition;
    }

    public Integer getLowestWithoutLargeSwing() {
        return lowestWithoutLargeSwing;
    }

    public void setLowestWithoutLargeSwing(Integer lowestWithoutLargeSwing) {
        this.lowestWithoutLargeSwing = lowestWithoutLargeSwing;
    }

    public Integer getLowestPossible() {
        return lowestPossible;
    }

    public void setLowestPossible(Integer lowestPossible) {
        this.lowestPossible = lowestPossible;
    }

    public Integer getLowestImpossible() {
        return lowestImpossible;
    }

    public void setLowestImpossible(Integer lowestImpossible) {
        this.lowestImpossible = lowestImpossible;
    }

    @Override
    public String toString() {
        String result = "";
        result += StringUtils.rightPad(teamName, 8);
        result += intStatToString(highestImpossible);
        result += intStatToString(highestPossible);
        result += intStatToString(highestWithoutLargeSwing);
        result += intStatToString(currentPosition);
        result += intStatToString(lowestWithoutLargeSwing);
        result += intStatToString(lowestPossible);
        result += intStatToString(lowestImpossible);
        return result;
    }

    private static String intStatToString(Integer stat){
        return StringUtils.leftPad(stat != null ? stat.toString() : "", 3);
    }
}
