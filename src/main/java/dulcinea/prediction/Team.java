package dulcinea.prediction;

import java.util.ArrayList;
import java.util.List;

public class Team {
    private String name;
    private List<Team> opponents;
    private int pointsOffEqual;

    Team(String name, int pointsOffEqual) {
        this.name = name;
        this.pointsOffEqual = pointsOffEqual;
    }

    String getName() {
        return name;
    }

    int getGamesToPlay() {
        return opponents.size();
    }

    void removeOpponent(Team opponent) {
        if ( opponents != null) {
            this.opponents.remove(opponent);
        }
    }

    int getPointsOffEqual() {
        return pointsOffEqual;
    }

    void reducePointsOffEqual(int reduction) {
        this.pointsOffEqual -= reduction;
    }

    List<Team> getOpponents() {
        return new ArrayList<>(opponents);
    }

    void setOpponents(List<Team> opponents) {
        this.opponents = opponents;
    }
}