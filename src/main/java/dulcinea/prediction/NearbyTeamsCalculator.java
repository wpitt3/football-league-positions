package dulcinea.prediction;

import dulcinea.match.LeaguePostion;

import java.util.*;
import java.util.stream.Collectors;

public class NearbyTeamsCalculator {

    public static List<Team> calcTeamsThatCanBeOvertaken(LeaguePostion mainTeam, List<LeaguePostion> teamsWithinRange, Map<String, ArrayList<String>> teamToOpponents, Integer matchesLookAhead) {
        OpponentResolver opponentResolver = new CatchableOpponentResolver(mainTeam, matchesLookAhead);
        int targetPoints = opponentResolver.calcTargetPoint();

        List<Team> teams = OpponentTeamGenerator.generateTeams(targetPoints, teamsWithinRange, teamToOpponents, matchesLookAhead);
        teams = OpponentTeamGenerator.resolveTeamsWhichAreBehindMainTeam(teams);

        teams = loopThroughTeamsUntilResolved(opponentResolver, teams);

        return teams.stream().filter(team -> team.getPointsOffEqual() >= 0).collect(Collectors.toList());
    }

    public static List<Team> calcCatchingTeams(LeaguePostion mainTeam, List<LeaguePostion> teamsWithinRange, Map<String, ArrayList<String>> teamToOpponents, Integer matchesLookAhead) {
        OpponentResolver opponentResolver = new CatchingOpponentResolver(mainTeam, matchesLookAhead);
        int targetPoints = opponentResolver.calcTargetPoint();

        List<Team> teams = OpponentTeamGenerator.generateTeams(targetPoints, teamsWithinRange, teamToOpponents, matchesLookAhead);
        teams = OpponentTeamGenerator.resolveTeamsWhichAreAheadOfMainTeam(teams);

        teams = loopThroughTeamsUntilResolved(opponentResolver, teams);

        return teams.stream().filter(team -> team.getPointsOffEqual() <= 0).collect(Collectors.toList());
    }

    public static List<Team> loopThroughTeamsUntilResolved(OpponentResolver opponentResolver, List<Team> teams){
        int previousGamesRemaining = 0;
        while (teams.stream().anyMatch(team -> team.getGamesToPlay() > 0)) {
            teams = opponentResolver.sortTeamsOpponents(teams);
            int gamesRemaining = teams.stream().map(Team::getGamesToPlay).mapToInt(Integer::intValue).sum();
            if (previousGamesRemaining == gamesRemaining) {
                opponentResolver.resolveTeamsWithMultipleGame(teams);
            }
            previousGamesRemaining = gamesRemaining;
            for( Team team : teams) {
                opponentResolver.resolveTeamsWhoseOutcomeWillNotChange(team);
                opponentResolver.resolveTeamsOneGameFromResolved(team);
            }
        }
        return teams;
    }
}
