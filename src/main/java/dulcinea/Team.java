package dulcinea;

import java.util.List;
import java.util.stream.Collectors;

public class Team {
    private String name;
    private List<String> opponentNames;
    private List<Team> opponents;
    private int pointsOffEqual;
    private int getGamesToPlay;

    public Team(String name, List<String> opponentNames, int pointsOffEqual) {
        this.name = name;
        this.opponentNames = opponentNames;
        this.pointsOffEqual = pointsOffEqual;
        this.getGamesToPlay = opponentNames.size();
    }

    public String getName() {
        return name;
    }

    public int getGamesToPlay() {
        return getGamesToPlay;
    }

    public List<String> getOpponentNames() {
        return opponentNames;
    }

//    public void removeOpponentByName(String opponent) {
//        this.opponentNames.remove(opponent);
//    }

    public void removeOpponent(Team opponent) {
        if (opponent != null ) {
            this.opponentNames.remove(opponent.getName());
            if ( opponents != null) {
                this.opponents.remove(opponent);
            }
        }
        getGamesToPlay -= 1;
    }

    public int getPointsOffEqual() {
        return pointsOffEqual;
    }

    public void reducePointsOffEqual(int reduction) {
        this.pointsOffEqual -= reduction;
    }

    public List<Team> getOpponents() {
        return opponents;
    }

    public void setOpponents(List<Team> opponents) {
        this.opponents = opponents;
        this.opponentNames = opponents.stream().filter(x -> x != null).map(Team::getName).collect(Collectors.toList());
    }
}