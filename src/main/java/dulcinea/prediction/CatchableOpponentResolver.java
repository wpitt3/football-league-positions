package dulcinea.prediction;

import dulcinea.match.LeaguePostion;

import java.util.Collections;
import java.util.List;

public class CatchableOpponentResolver implements OpponentResolver {
    private Integer matchesLookAhead;
    private LeaguePostion mainTeam;

    CatchableOpponentResolver(LeaguePostion mainTeam, Integer matchesLookAhead) {
        this.mainTeam = mainTeam;
        this.matchesLookAhead = matchesLookAhead;
    }

    @Override
    public int calcTargetPoint() {
        return mainTeam.getPoints() + 3 * matchesLookAhead;
    }

    @Override
    public List<Team> sortTeamsOpponents(List<Team> teams) {
        // this is an estimate
        int pointRequired = teams.stream().map(Team::getPointsOffEqual).filter(x -> x > 0).reduce(0, (a, b) -> a + b);
        int pointsRemaining = teams.stream().map(Team::getGamesToPlay).reduce(0, (a, b) -> a + b) / 2 * 3;
        teams.forEach(team -> {
            List<Team> opponents = OpponentResolver.sortTeamsByPoints(team.getOpponents());
            if (pointRequired > pointsRemaining) {
                Collections.reverse(opponents);
            }
            team.setOpponents(opponents);
        });

        teams = OpponentResolver.sortTeamsByPoints(teams);
        if (pointRequired > pointsRemaining) {
            Collections.reverse(teams);
        }
        return teams;
    }

    @Override
    public Team resolveTeamsOneGameFromResolved(Team team) {
        if (team.getGamesToPlay() == 1) {
            Team opponent = team.getOpponents().get(0);
            if (team.getPointsOffEqual() == 0) {
                OpponentResolver.aBeatB(opponent, team);
            } else if (team.getPointsOffEqual() == 1 || team.getPointsOffEqual() == 2) {
                OpponentResolver.aDrewWithB(team, opponent);
            } else if (team.getPointsOffEqual() < 0 || team.getPointsOffEqual() > 2) {
                OpponentResolver.aBeatB(team, opponent);
            }
        }
        return team;
    }

    @Override
    public Team resolveTeamsWhoseOutcomeWillNotChange(Team team) {
        if ((team.getGamesToPlay() * 3) <= team.getPointsOffEqual() || team.getPointsOffEqual() < 0) {
            for (Team opponent : team.getOpponents()) {
                OpponentResolver.aBeatB(team, opponent);
            }
        }
        return team;
    }

    @Override
    public List<Team> resolveTeamsWithMultipleGame(List<Team> teams) {
        teams.stream().filter(team -> team.getGamesToPlay() > 0).findFirst().ifPresent( topTeam ->
                OpponentResolver.aBeatB(topTeam, topTeam.getOpponents().get(0))
        );
        return teams;
    }
}

