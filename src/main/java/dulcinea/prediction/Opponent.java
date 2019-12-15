package dulcinea.prediction;

import java.util.ArrayList;
import java.util.List;

public class Opponent {
    private String name;
    private List<Opponent> opponents;
    private int pointsOffEqual;

    Opponent(String name, int pointsOffEqual) {
        this.name = name;
        this.pointsOffEqual = pointsOffEqual;
    }

    String getName() {
        return name;
    }

    int getGamesToPlay() {
        return opponents.size();
    }

    void removeOpponent(Opponent opponent) {
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

    List<Opponent> getOpponents() {
        return new ArrayList<>(opponents);
    }

    void setOpponents(List<Opponent> opponents) {
        this.opponents = opponents;
    }
}