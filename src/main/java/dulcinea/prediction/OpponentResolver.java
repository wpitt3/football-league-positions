package dulcinea.prediction;

import java.util.List;

public interface OpponentResolver {

    /**
     * calculate the target number of points for each team
     */
    int calcTargetPoint();

    /**
     * sort teams, and the opponents of those teams
     */
    List<Team> sortTeamsOpponents(List<Team> teams);

    /**
     * resolve teams with one game remaining
     */
    Team resolveTeamsOneGameFromResolved(Team team);

    /**
     * resolves the matches for teams which are already past the main team,
     * or are to far ahead or behind to affect the main team
     */
    Team resolveTeamsWhoseOutcomeWillNotChange(Team team);

    /**
     * resolve teams with more than one game remaining
     */
    List<Team> resolveTeamsWithMultipleGame(List<Team> teams);

    static void aBeatB(Team teamA, Team teamB) {
//        System.out.println(teamA.getName() + " beat " + teamB.getName());
        teamA.reducePointsOffEqual(3);
        teamA.removeOpponent(teamB);
        teamB.removeOpponent(teamA);
    }

    static void aDrewWithB(Team teamA, Team teamB) {
        teamA.reducePointsOffEqual(1);
        teamA.removeOpponent(teamB);
        teamB.reducePointsOffEqual(1);
        teamB.removeOpponent(teamA);
    }

    static List<Team> sortTeamsByPoints(List<Team> teams) {
        teams.sort((o1, o2) -> {
            int team1Points = o1 != null ? o1.getPointsOffEqual() : 0;
            int team2Points = o2 != null ? o2.getPointsOffEqual() : 0;
            return team2Points - team1Points;
        });
        return teams;
    }
}
