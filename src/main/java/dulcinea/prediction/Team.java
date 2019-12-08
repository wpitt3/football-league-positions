package dulcinea.prediction;

import java.util.ArrayList;
import java.util.List;

public class Team {
    private String name;
    private List<Team> opponents;
    private int pointsOffEqual;

    public Team(String name, int pointsOffEqual) {
        this.name = name;
        this.pointsOffEqual = pointsOffEqual;
    }

    public String getName() {
        return name;
    }

    public int getGamesToPlay() {
        return opponents.size();
    }

    public void removeOpponent(Team opponent) {
        if ( opponents != null) {
            this.opponents.remove(opponent);
        }
    }

    public int getPointsOffEqual() {
        return pointsOffEqual;
    }

    public void reducePointsOffEqual(int reduction) {
        this.pointsOffEqual -= reduction;
    }

    public List<Team> getOpponents() {
        return new ArrayList<>(opponents);
    }

    public void setOpponents(List<Team> opponents) {
        this.opponents = opponents;
    }
}