package dulcinea;

public class TeamStatus {
    private String name;
    private int won;
    private int drawn;
    private int lost;
    private int goalsFor;
    private int goalsAgainst;

    public TeamStatus(String name) {
        this.name = name;
        this.won = 0;
        this.drawn = 0;
        this.lost = 0;
        this.goalsFor = 0;
        this.goalsAgainst = 0;
    }

    public String getName() {
        return name;
    }

    public int getPlayed() {
        return won+drawn+lost;
    }

    public int getWon() {
        return won;
    }

    public int getDrawn() {
        return drawn;
    }

    public int getLost() {
        return lost;
    }

    public int getGoalsFor() {
        return goalsFor;
    }

    public int getGoalsAgainst() {
        return goalsAgainst;
    }

    public int getGoalDifference() {
        return goalsFor-goalsAgainst;
    }

    public int getPoints() {
        return won*3+drawn;
    }

    public void addMatch(int teamScore, int opponentScore) {
        if (teamScore > opponentScore) {
            won++;
        } else if (teamScore < opponentScore) {
            lost++;
        } else {
            drawn++;
        }
        goalsFor += teamScore;
        goalsAgainst += opponentScore;
    }
}
